package com.hilohq.hilo.hilo_tool.commands;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import com.hilohq.hilo.config.HiloConfig;
import java.util.Map;
import org.apache.commons.cli.DefaultParser;
import org.junit.Test;

public final class GrafanaTokenTest {

  private static HiloConfig config(final Map<String, String> env, final String... args)
      throws Exception {
    final var command = new ExecutePipelineCommand();
    final var cli = new DefaultParser().parse(command.options(), args);
    return ExecutePipelineCommand.configFrom(cli, HiloConfig.fromEnvironment(env::get));
  }

  @Test
  public void testOptionTakesPrecedenceOverEnv() throws Exception {
    final var config =
        config(
            Map.of("GRAFANA_TOKEN", "from-env"),
            "--input",
            "logs.txt",
            "--grafana-token",
            "from-option");
    assertEquals("from-option", ExecutePipelineCommand.resolveGrafanaToken(config));
  }

  @Test
  public void testFallsBackToEnv() throws Exception {
    final var config = config(Map.of("GRAFANA_TOKEN", "from-env"), "--input", "logs.txt");
    assertEquals("from-env", ExecutePipelineCommand.resolveGrafanaToken(config));
  }

  @Test
  public void testFailsWhenNeitherIsSet() throws Exception {
    final var config = config(Map.of(), "--input", "logs.txt");
    try {
      ExecutePipelineCommand.resolveGrafanaToken(config);
      fail("expected IllegalStateException");
    } catch (final IllegalStateException e) {
      assertTrue(e.getMessage().contains("--grafana-token"));
      assertTrue(e.getMessage().contains("GRAFANA_TOKEN"));
    }
  }

  @Test
  public void testConfigFlagsOverrideEnvAndDefaults() throws Exception {
    final var config =
        config(
            Map.of("HILO_FLINK_HOST", "flink.internal", "HILO_MONGO_DATABASE", "from-env"),
            "--input",
            "logs.txt",
            "--mongo-database",
            "from-flag",
            "--flink-port",
            "18081");
    assertEquals("from-flag", config.mongoDatabase());
    assertEquals("flink.internal", config.flinkHost());
    assertEquals(18081, config.flinkPort());
    assertEquals("mongodb://root:example@mongodb:27017/admin", config.pipelineMongoUrl());
  }

  @Test
  public void testEveryCliSettingHasAnOption() {
    final var options = new ExecutePipelineCommand().options();
    for (final var setting : ExecutePipelineCommand.CLI_SETTINGS) {
      assertTrue(setting.name(), options.hasLongOption(ExecutePipelineCommand.optionName(setting)));
    }
    assertTrue(options.hasLongOption("grafana-token"));
  }
}
