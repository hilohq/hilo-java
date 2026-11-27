package com.hilohq.hilo.hilo_tool.commands;

import static com.hilohq.hilo.hilo_tool.cmd.PrintUtils.printErrorAndExit;

import com.hilohq.hilo.config.HiloConfig;
import com.hilohq.hilo.config.HiloConfig.Setting;
import com.hilohq.hilo.hilo_tool.cmd.ActionCommand;
import com.hilohq.hilo.integrations_service.service.MongoIntegrationsStore;
import com.hilohq.hilo.pipelines.*;
import com.hilohq.hilo.pipelines_service.service.FlinkClient;
import com.hilohq.hilo.protobufs.pipelines.Pipelines;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.flink.api.common.JobStatus;

public class ExecutePipelineCommand extends ActionCommand {

  private static final String OPTION_INPUT = "input";

  /** Settings that can be overridden on the command line, e.g. {@code --flink-port 8082}. */
  static final List<Setting> CLI_SETTINGS =
      List.of(
          Setting.MONGO_URL,
          Setting.MONGO_DATABASE,
          Setting.PIPELINE_MONGO_URL,
          Setting.FLINK_HOST,
          Setting.FLINK_PORT,
          Setting.GRAFANA_URL,
          Setting.GRAFANA_TOKEN,
          Setting.PUSHGATEWAY_ADDRESS,
          Setting.JDBC_URL,
          Setting.JDBC_USERNAME,
          Setting.JDBC_PASSWORD,
          Setting.JDBC_DRIVER);

  /** Configuration from the environment and command-line overrides; set by {@link #run}. */
  private HiloConfig config = HiloConfig.fromEnvironment();

  /** The command-line option that overrides {@code setting}, e.g. {@code grafana-token}. */
  static String optionName(final Setting setting) {
    return setting.name().toLowerCase(Locale.ROOT).replace('_', '-');
  }

  private static Options createOptions() {
    final var options = new Options();
    options.addOption(
        Option.builder()
            .longOpt(OPTION_INPUT)
            .argName(OPTION_INPUT)
            .hasArg(true)
            .required()
            .build());
    for (final var setting : CLI_SETTINGS) {
      final var defaultValue =
          setting.defaultValue() == null ? "no default" : "default " + setting.defaultValue();
      options.addOption(
          Option.builder()
              .longOpt(optionName(setting))
              .argName(optionName(setting))
              .hasArg(true)
              .desc(setting.description() + " (env " + setting.env() + ", " + defaultValue + ")")
              .build());
    }
    return options;
  }

  /** Applies the command-line overrides in {@code cli} on top of {@code base}. */
  static HiloConfig configFrom(final CommandLine cli, final HiloConfig base) {
    var result = base;
    for (final var setting : CLI_SETTINGS) {
      result = result.with(setting, cli.getOptionValue(optionName(setting)));
    }
    return result;
  }

  /**
   * Resolves the Grafana bearer token: the {@code --grafana-token} option wins, otherwise the
   * {@code GRAFANA_TOKEN} environment variable is used.
   *
   * @throws IllegalStateException if neither is set
   */
  static String resolveGrafanaToken(final HiloConfig config) {
    return config
        .find(Setting.GRAFANA_TOKEN)
        .orElseThrow(
            () ->
                new IllegalStateException(
                    "a Grafana API token is required: pass --"
                        + optionName(Setting.GRAFANA_TOKEN)
                        + " <token> or set the "
                        + Setting.GRAFANA_TOKEN.env()
                        + " environment variable"));
  }

  private static boolean verifyInput(final String input) {
    final var absolute = Path.of(input).toAbsolutePath().toString();
    final var file = new File(absolute);
    return file.exists() && file.canRead();
  }

  public ExecutePipelineCommand() {
    super("execute", "execute a HiLO pipeline", createOptions());
  }

  private void verifyJobExecution(
      final FlinkClient client, final String jobId, final String jobName) throws Exception {
    var sleepTime = 1000;

    for (int i = 0; i < 100; i++) {
      final var status = client.getPipelineStatus(jobId).get(5, TimeUnit.SECONDS);
      if (status.isTerminalState()) {
        break;
      }

      System.out.println("Task " + jobId + " " + jobName + " has status " + status);
      Thread.sleep(sleepTime);
      sleepTime = Math.min(sleepTime + 1000, 5000);
    }

    final var status = client.getPipelineStatus(jobId).get(5, TimeUnit.SECONDS);
    if (status.isTerminalState()) {
      System.out.println("Task " + jobId + " " + jobName + " finished with status " + status);
      if (!status.equals(JobStatus.FINISHED)) {
        throw new RuntimeException("Task failed with status " + status);
      }
    } else {
      System.out.println(
          "Task "
              + jobId
              + " "
              + jobName
              + " has not terminated. Current status is "
              + status
              + ". Cancelling job");
      client.cancelPipeline(jobId).get(5, TimeUnit.SECONDS);
      final var newStatus = client.getPipelineStatus(jobId).get(5, TimeUnit.SECONDS);
      if (!newStatus.isTerminalState()) {
        System.out.println(
            "Task " + jobId + " " + jobName + " failed to cancel. Current status is " + status);
      }
    }
  }

  public void execute(
      final FlinkClient client, final Pipelines.BatchPipeline pipelineConfig, final String jobName)
      throws Exception {
    final var pipeline = BatchPipelineUtils.build(pipelineConfig);
    final var jobId = client.executeBatchPipeline(pipeline).get(5, TimeUnit.SECONDS);
    verifyJobExecution(client, jobId, jobName);
  }

  public void execute(
      final FlinkClient client, final Pipelines.StreamPipeline pipelineConfig, final String jobName)
      throws Exception {
    final var pipeline = StreamPipelineUtils.build(pipelineConfig);
    final var jobId = client.executeStreamPipeline(pipeline).get(5, TimeUnit.SECONDS);
    verifyJobExecution(client, jobId, jobName);
  }

  @Override
  public void run(final String[] args) throws Exception {
    final var parser = new DefaultParser();
    final var cli = parser.parse(options(), args);
    final var input = cli.getOptionValue(OPTION_INPUT);
    config = configFrom(cli, HiloConfig.fromEnvironment());
    final var ok = verifyInput(input);
    if (!ok) {
      printErrorAndExit(name(), "input `" + input + "` cannot be read");
    }

    final var projectId = UUID.randomUUID().toString();
    final var schemaId = UUID.randomUUID().toString();
    final var sourceId = UUID.randomUUID().toString();

    final var targetPath = Path.of("/tmp/", Path.of(input).getFileName().toString());
    final var inputPath = Path.of(input);
    if (targetPath.toFile().exists()) {
      final var okDelete = targetPath.toFile().delete();
      if (!okDelete) {
        printErrorAndExit(
            name(), "could not delete already existing file `" + targetPath.toString() + "`");
      }
    }

    Files.copy(inputPath, targetPath);
    final var target = targetPath.toString();
    final var flinkClient = new FlinkClient(config);
    final var integrationsStore =
        MongoIntegrationsStore.open(config.mongoUrl(), config.mongoDatabase());

    execute(
        flinkClient,
        Pipelines.BatchPipeline.newBuilder()
            .setJsonSchemaGenerate(
                Pipelines.JsonSchemaGeneratePipeline.newBuilder()
                    .setMongoDatabase(config.mongoDatabase())
                    .setMongoUrl(config.pipelineMongoUrl())
                    .setSchemaId(schemaId)
                    .setSourceId(sourceId)
                    .setProjectId(projectId)
                    .setInputPath(target))
            .build(),
        "JsonSchemaGeneratePipeline");
    execute(
        flinkClient,
        Pipelines.BatchPipeline.newBuilder()
            .setJsonFeaturesGenerate(
                Pipelines.JsonFeaturesGeneratePipeline.newBuilder()
                    .setMongoDatabase(config.mongoDatabase())
                    .setMongoUrl(config.pipelineMongoUrl())
                    .setSchemaId(schemaId)
                    .setInputPath(target))
            .build(),
        "JsonFeaturesGeneratePipeline");
    execute(
        flinkClient,
        Pipelines.BatchPipeline.newBuilder()
            .setJdbcMysqlTableGeneratePipeline(
                Pipelines.JdbcMysqlTableGeneratePipeline.newBuilder()
                    .setMongoDatabase(config.mongoDatabase())
                    .setMongoUrl(config.pipelineMongoUrl())
                    .setSchemaId(schemaId)
                    .setInputPath(target))
            .build(),
        "JdbcMysqlTableGeneratePipeline");

    final var metrics = integrationsStore.getJdbcMetrics(schemaId).get(5, TimeUnit.SECONDS);
    execute(
        flinkClient,
        Pipelines.BatchPipeline.newBuilder()
            .setJsonJdbcPushPipeline(
                Pipelines.JsonJdbcPushPipeline.newBuilder()
                    .setMongoDatabase(config.mongoDatabase())
                    .setMongoUrl(config.pipelineMongoUrl())
                    .setSchemaId(schemaId)
                    .setInputPath(target)
                    .setJdbcUsername(config.get(Setting.JDBC_USERNAME))
                    .setJdbcPassword(config.get(Setting.JDBC_PASSWORD))
                    .setJdbcDriverName(config.get(Setting.JDBC_DRIVER))
                    .setJdbcDbUrl(config.get(Setting.JDBC_URL))
                    .setJdbcQuery(metrics.getLoadQuery())
                    .setJdbcBatchInterval(1))
            .build(),
        "JsonJdbcPushPipeline");
  }

  void loadPrometheus(final FlinkClient flinkClient, final String schemaId, final String target)
      throws Exception {
    // Resolve the token up front so a missing token fails before any job is submitted.
    final var grafanaToken = resolveGrafanaToken(config);
    execute(
        flinkClient,
        Pipelines.BatchPipeline.newBuilder()
            .setPrometheusMetricsGenerate(
                Pipelines.PrometheusMetricsGeneratePipeline.newBuilder()
                    .setMongoDatabase(config.mongoDatabase())
                    .setMongoUrl(config.pipelineMongoUrl())
                    .setSchemaId(schemaId)
                    .setInputPath(target))
            .build(),
        "PrometheusMetricsGeneratePipeline");
    execute(
        flinkClient,
        Pipelines.BatchPipeline.newBuilder()
            .setPrometheusGrafanaDashboardGenerate(
                Pipelines.PrometheusGrafanaDashboardGeneratePipeline.newBuilder()
                    .setMongoDatabase(config.mongoDatabase())
                    .setMongoUrl(config.pipelineMongoUrl())
                    .setSchemaId(schemaId)
                    .setGrafanaUrl(config.get(Setting.GRAFANA_URL))
                    .setGrafanaBearerToken(grafanaToken))
            .build(),
        "PrometheusGrafanaDashboardGeneratePipeline");
    execute(
        flinkClient,
        Pipelines.StreamPipeline.newBuilder()
            .setJsonPrometheusPush(
                Pipelines.JsonPrometheusPushPipeline.newBuilder()
                    .setMongoDatabase(config.mongoDatabase())
                    .setMongoUrl(config.pipelineMongoUrl())
                    .setSchemaId(schemaId)
                    .setInputPath(target)
                    .setPrometheusUrl(config.get(Setting.PUSHGATEWAY_ADDRESS)))
            .build(),
        "JsonPrometheusPushPipeline");
  }
}
