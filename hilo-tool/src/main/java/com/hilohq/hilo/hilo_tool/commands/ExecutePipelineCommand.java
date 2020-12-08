package com.hilohq.hilo.hilo_tool.commands;

import static com.hilohq.hilo.hilo_tool.cmd.PrintUtils.printErrorAndExit;

import com.hilohq.hilo.hilo_tool.cmd.ActionCommand;
import com.hilohq.hilo.pipelines.*;
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
      final var okDelete = targetPath.toFile().delete();
      if (!okDelete) {
        printErrorAndExit(
            name(), "could not delete already existing file `" + targetPath.toString() + "`");
      }
    }

    Files.copy(inputPath, targetPath);
    final var target = targetPath.toString();
    final var batchExecutor = DefaultBatchExecutor.newBuilder().jarModule("hilo-tool").build();
    final var streamExecutor = DefaultStreamExecutor.newBuilder().jarModule("hilo-tool").build();

    batchExecutor.execute(
        JsonSchemaGeneratePipeline.newBuilder()
            .database("test")
            .mongoUrl("mongodb://root:example@mongodb:27017/admin")
            .inputPath(target)
            .schemaId(schemaId)
            .sourceId(sourceId)
            .projectId(projectId)
            .build());
    batchExecutor.execute(
        JsonFeaturesGeneratePipeline.newBuilder()
            .database("test")
            .mongoUrl("mongodb://root:example@mongodb:27017/admin")
            .inputPath(target)
            .schemaId(schemaId)
            .build());
    batchExecutor.execute(
        PrometheusGenerateMetricsPipeline.newBuilder()
            .database("test")
            .mongoUrl("mongodb://root:example@mongodb:27017/admin")
            .inputPath(target)
            .schemaId(schemaId)
            .build());
    batchExecutor.execute(
        GrafanaDashboardGeneratePipeline.newBuilder()
            .database("test")
            .mongoUrl("mongodb://root:example@mongodb:27017/admin")
            .schemaId(schemaId)
            .grafanaUrl("http://172.20.0.1:3000")
            .bearerToken(
                "eyJrIjoiSDA0aGM0YXhNTTBZNkVPYURQTDF1aENVMTNpaUN1b2YiLCJuIjoiYWRtaW4iLCJpZCI6MX0=")
            .build());
    streamExecutor.execute(
        JsonPrometheusPushPipeline.newBuilder()
            .inputPath(target)
            .mongoUrl("mongodb://root:example@mongodb:27017/admin")
            .schemaId(schemaId)
            .prometheusAddress("172.20.0.1:9091")
            .build());
  }
}
