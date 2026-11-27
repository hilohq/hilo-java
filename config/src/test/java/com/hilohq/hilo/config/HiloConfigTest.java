package com.hilohq.hilo.config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import com.hilohq.hilo.config.HiloConfig.Setting;
import java.util.HashSet;
import java.util.Map;
import org.junit.Test;

public final class HiloConfigTest {

  private static final HiloConfig EMPTY = HiloConfig.fromEnvironment(name -> null);

  @Test
  public void testDefaultsMatchTheLocalDockerSetup() {
    assertEquals("mongodb://root:example@localhost:27017/admin", EMPTY.mongoUrl());
    assertEquals("mongodb://root:example@mongodb:27017/admin", EMPTY.pipelineMongoUrl());
    assertEquals("test", EMPTY.mongoDatabase());
    assertEquals("127.0.0.1", EMPTY.flinkHost());
    assertEquals(8081, EMPTY.flinkPort());
    assertEquals(1234, EMPTY.getInt(Setting.SCHEMA_SERVICE_PORT));
    assertEquals(1235, EMPTY.getInt(Setting.FEATURES_SERVICE_PORT));
  }

  @Test
  public void testEnvironmentOverridesDefault() {
    final var config = HiloConfig.fromEnvironment(Map.of("HILO_FLINK_PORT", "9999")::get);
    assertEquals(9999, config.flinkPort());
  }

  @Test
  public void testBlankEnvironmentValueFallsBackToDefault() {
    final var config = HiloConfig.fromEnvironment(Map.of("HILO_MONGO_DATABASE", " ")::get);
    assertEquals("test", config.mongoDatabase());
  }

  @Test
  public void testOverrideWinsOverEnvironment() {
    final var config =
        HiloConfig.fromEnvironment(Map.of("HILO_MONGO_DATABASE", "from-env")::get)
            .with(Setting.MONGO_DATABASE, "from-flag");
    assertEquals("from-flag", config.mongoDatabase());
  }

  @Test
  public void testNullOverrideIsIgnored() {
    assertEquals("test", EMPTY.with(Setting.MONGO_DATABASE, null).mongoDatabase());
  }

  @Test
  public void testSettingWithoutDefault() {
    assertFalse(EMPTY.find(Setting.GRAFANA_TOKEN).isPresent());
    try {
      EMPTY.get(Setting.GRAFANA_TOKEN);
      fail("expected IllegalStateException");
    } catch (IllegalStateException e) {
      assertTrue(e.getMessage().contains("GRAFANA_TOKEN"));
    }
  }

  @Test
  public void testInvalidInteger() {
    final var config = HiloConfig.fromEnvironment(Map.of("HILO_FLINK_PORT", "abc")::get);
    try {
      config.flinkPort();
      fail("expected IllegalStateException");
    } catch (IllegalStateException e) {
      assertTrue(e.getMessage().contains("HILO_FLINK_PORT"));
    }
  }

  @Test
  public void testEnvironmentVariableNamesAreUnique() {
    final var names = new HashSet<String>();
    for (final var setting : Setting.values()) {
      assertTrue(setting.env(), names.add(setting.env()));
    }
  }
}
