package com.hilohq.hilo.hilo_tool.commands;

import static com.hilohq.hilo.hilo_tool.cmd.PrintUtils.printErrorAndExit;

import com.hilohq.hilo.connectors_mongo.sink.MongoOutputFormat;
import com.hilohq.hilo.hilo_tool.cmd.ActionCommand;
import com.hilohq.hilo.integration_prometheus.converter.PrometheusMetricUpdateMongoCommandGenerator;
import com.hilohq.hilo.integration_prometheus.sink.PrometheusSink;
import com.hilohq.hilo.pipelines.*;
import com.hilohq.hilo.structure_learning.features.mongo.FeaturesUpdateMongoCommandGenerator;
import com.hilohq.hilo.structure_learning.schema.mongo.SchemaUpdateMongoCommandGenerator;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

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
      targetPath.toFile().delete();
    }

    Files.copy(inputPath, targetPath);
    final var target = targetPath.toString();
    generateSchema(target, projectId, sourceId, schemaId);
    generateFeatures(target, schemaId);
    generatePrometheusMetrics(target, schemaId);
    pushToPrometheus(target, schemaId);
  }

  private void generateSchema(
      final String filePath, final String projectId, final String sourceId, final String schemaId)
      throws Exception {
    final var output =
        MongoOutputFormat.newBuilder(new SchemaUpdateMongoCommandGenerator())
            .database("test")
            .url("mongodb://root:example@mongodb:27017/admin")
            .build();

    final var executor =
        BatchPipelineExecutor.newBuilder(output).jarModule("integration-tests").build();
    final var pipelineBuilder =
        new JsonSchemaGenerateBatchPipelineBuilder(projectId, sourceId, schemaId);
    executor.execute(filePath, pipelineBuilder);
  }

  private void generateFeatures(final String filePath, final String schemaId) throws Exception {
    final var output =
        MongoOutputFormat.newBuilder(new FeaturesUpdateMongoCommandGenerator())
            .database("test")
            .url("mongodb://root:example@mongodb:27017/admin")
            .build();

    final var executor =
        BatchPipelineExecutor.newBuilder(output).jarModule("integration-tests").build();
    final var pipelineBuilder =
        new JsonFeaturesGenerateBatchPipelineBuilder(
            schemaId, "mongodb://root:example@mongodb:27017/admin");
    executor.execute(filePath, pipelineBuilder);
  }

  private void generatePrometheusMetrics(final String filePath, final String schemaId)
      throws Exception {
    final var output =
        MongoOutputFormat.newBuilder(new PrometheusMetricUpdateMongoCommandGenerator())
            .database("test")
            .url("mongodb://root:example@mongodb:27017/admin")
            .build();
    final var executor =
        BatchPipelineExecutor.newBuilder(output).jarModule("integration-tests").build();
    final var pipelineBuilder =
        new PrometheusGenerateMetricsPipelineBuilder(
            schemaId, "mongodb://root:example@mongodb:27017/admin");
    executor.execute(filePath, pipelineBuilder);
  }

  private void pushToPrometheus(final String filePath, final String schemaId) throws Exception {
    final var sink =
        PrometheusSink.newBuilder()
            .address("172.20.0.1:9091")
            .tag("test")
            .schemaId(schemaId)
            .mongoUrl("mongodb://root:example@mongodb:27017/admin")
            .build();
    final var executor =
        StreamPipelineExecutor.newBuilder(sink).jarModule("integration-tests").build();
    final var pipelineBuilder =
        new JsonPrometheusPushPipelineBuilder(
            schemaId, "mongodb://root:example@mongodb:27017/admin");
    executor.execute(filePath, pipelineBuilder);
  }
}
