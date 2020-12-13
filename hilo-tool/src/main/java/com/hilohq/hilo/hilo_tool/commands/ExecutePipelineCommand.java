package com.hilohq.hilo.hilo_tool.commands;

import static com.hilohq.hilo.hilo_tool.cmd.PrintUtils.printErrorAndExit;

import com.hilohq.hilo.hilo_tool.cmd.ActionCommand;
import com.hilohq.hilo.pipelines.*;
import com.hilohq.hilo.pipelines_service.service.FlinkClient;
import com.hilohq.hilo.protobufs.pipelines.Pipelines;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.flink.api.common.JobStatus;

public class ExecutePipelineCommand extends ActionCommand {

  private static final String OPTION_INPUT = "input";

  private static Options createOptions() {
    final var options = new Options();
    options.addOption(
        Option.builder()
            .longOpt(OPTION_INPUT)
            .argName(OPTION_INPUT)
            .hasArg(true)
            .required()
            .build());
    return options;
  }

  private static boolean verifyInput(final String input) {
    final var absolute = Path.of(input).toAbsolutePath().toString();
    final var file = new File(absolute);
    return file.exists() && file.canRead();
  }

  public ExecutePipelineCommand() {
    super("execute", "execute a HiLO pipeline", createOptions());
  }

  private void verifyJobExecution(final FlinkClient client, final String jobId) throws Exception {
    var sleepTime = 1000;

    for (int i = 0; i < 100; i++) {
      final var status = client.getPipelineStatus(jobId).get(5, TimeUnit.SECONDS);
      if (status.isTerminalState()) {
        break;
      }

      System.out.println("Task " + jobId + " has status " + status);
      Thread.sleep(sleepTime);
      sleepTime = Math.min(sleepTime + 1000, 5000);
    }

    final var status = client.getPipelineStatus(jobId).get(5, TimeUnit.SECONDS);
    if (status.isTerminalState()) {
      System.out.println("Task " + jobId + " finished with status " + status);
      if (!status.equals(JobStatus.FINISHED)) {
        throw new RuntimeException("Task failed with status " + status);
      }
    } else {
      System.out.println(
          "Task "
              + jobId
              + " has not terminated. Current status is "
              + status
              + ". Cancelling job");
      client.cancelPipeline(jobId).get(5, TimeUnit.SECONDS);
      final var newStatus = client.getPipelineStatus(jobId).get(5, TimeUnit.SECONDS);
      if (!newStatus.isTerminalState()) {
        System.out.println("Task " + jobId + " failed to cancel. Current status is " + status);
      }
    }
  }

  public void execute(final FlinkClient client, final Pipelines.BatchPipeline pipelineConfig)
      throws Exception {
    final var pipeline = BatchPipelineUtils.build(pipelineConfig);
    final var jobId = client.executeBatchPipeline(pipeline).get(5, TimeUnit.SECONDS);
    verifyJobExecution(client, jobId);
  }

  public void execute(final FlinkClient client, final Pipelines.StreamPipeline pipelineConfig)
      throws Exception {
    final var pipeline = StreamPipelineUtils.build(pipelineConfig);
    final var jobId = client.executeStreamPipeline(pipeline).get(5, TimeUnit.SECONDS);
    verifyJobExecution(client, jobId);
  }

  @Override
  public void run(final String[] args) throws Exception {
    final var parser = new DefaultParser();
    final var cli = parser.parse(options(), args);
    final var input = cli.getOptionValue(OPTION_INPUT);
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
    final var flinkClient = new FlinkClient();

    execute(
        flinkClient,
        Pipelines.BatchPipeline.newBuilder()
            .setJsonSchemaGenerate(
                Pipelines.JsonSchemaGeneratePipeline.newBuilder()
                    .setMongoDatabase("test")
                    .setMongoUrl("mongodb://root:example@mongodb:27017/admin")
                    .setSchemaId(schemaId)
                    .setSourceId(sourceId)
                    .setProjectId(projectId)
                    .setInputPath(target))
            .build());
    execute(
        flinkClient,
        Pipelines.BatchPipeline.newBuilder()
            .setJsonFeaturesGenerate(
                Pipelines.JsonFeaturesGeneratePipeline.newBuilder()
                    .setMongoDatabase("test")
                    .setMongoUrl("mongodb://root:example@mongodb:27017/admin")
                    .setSchemaId(schemaId)
                    .setInputPath(target))
            .build());
    execute(
        flinkClient,
        Pipelines.BatchPipeline.newBuilder()
            .setPrometheusMetricsGenerate(
                Pipelines.PrometheusMetricsGeneratePipeline.newBuilder()
                    .setMongoDatabase("test")
                    .setMongoUrl("mongodb://root:example@mongodb:27017/admin")
                    .setSchemaId(schemaId)
                    .setInputPath(target))
            .build());
    execute(
        flinkClient,
        Pipelines.BatchPipeline.newBuilder()
            .setGrafanaDashboardGenerate(
                Pipelines.GrafanaDashboardGeneratePipeline.newBuilder()
                    .setMongoDatabase("test")
                    .setMongoUrl("mongodb://root:example@mongodb:27017/admin")
                    .setSchemaId(schemaId)
                    .setGrafanaUrl("http://172.20.0.1:3000")
                    .setGrafanaBearerToken(
                        "eyJrIjoiU1h5QUMwVHdoa1p4eEJERkx0OVJOWFBaQjNEc0tzenoiLCJuIjoiYWRtaW4iLCJpZCI6MX0="))
            .build());
    execute(
        flinkClient,
        Pipelines.StreamPipeline.newBuilder()
            .setJsonPrometheusPush(
                Pipelines.JsonPrometheusPushPipeline.newBuilder()
                    .setMongoDatabase("test")
                    .setMongoUrl("mongodb://root:example@mongodb:27017/admin")
                    .setSchemaId(schemaId)
                    .setInputPath(target)
                    .setPrometheusUrl("172.20.0.1:9091"))
            .build());
  }
}
