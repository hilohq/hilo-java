package com.hilohq.hilo.config;

import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

/**
 * Connection settings for HiLO. Each setting is read from an environment variable and falls back to
 * a default that matches the local Docker Compose setup used by the integration tests. Explicit
 * overrides (for example hilo-tool command-line flags) take precedence over both.
 *
 * <p>Settings are resolved on the process that builds a pipeline or opens a client. Values that a
 * Flink job needs are copied into the pipeline definition, so the task managers' environment is not
 * consulted.
 */
public final class HiloConfig {

  /** A configurable setting: its environment variable and default value. */
  public enum Setting {
    MONGO_URL(
        "HILO_MONGO_URL",
        "mongodb://root:example@localhost:27017/admin",
        "MongoDB URL used by the gRPC servers, hilo-tool and the integration-test helpers"),
    MONGO_DATABASE("HILO_MONGO_DATABASE", "test", "MongoDB database that holds every collection"),
    PIPELINE_MONGO_URL(
        "HILO_PIPELINE_MONGO_URL",
        "mongodb://root:example@mongodb:27017/admin",
        "MongoDB URL handed to Flink jobs (resolved from inside the Flink cluster)"),
    FLINK_HOST("HILO_FLINK_HOST", "127.0.0.1", "Flink JobManager REST host"),
    FLINK_PORT("HILO_FLINK_PORT", "8081", "Flink JobManager REST port"),
    SCHEMA_SERVICE_HOST("HILO_SCHEMA_SERVICE_HOST", "localhost", "Schema gRPC service host"),
    SCHEMA_SERVICE_PORT("HILO_SCHEMA_SERVICE_PORT", "1234", "Schema gRPC service port"),
    FEATURES_SERVICE_HOST("HILO_FEATURES_SERVICE_HOST", "localhost", "Features gRPC service host"),
    FEATURES_SERVICE_PORT("HILO_FEATURES_SERVICE_PORT", "1235", "Features gRPC service port"),
    INTEGRATIONS_SERVICE_HOST(
        "HILO_INTEGRATIONS_SERVICE_HOST", "localhost", "Integrations gRPC service host"),
    INTEGRATIONS_SERVICE_PORT(
        "HILO_INTEGRATIONS_SERVICE_PORT", "1235", "Integrations gRPC service port"),
    PIPELINES_SERVICE_HOST(
        "HILO_PIPELINES_SERVICE_HOST", "localhost", "Pipelines gRPC service host"),
    PIPELINES_SERVICE_PORT("HILO_PIPELINES_SERVICE_PORT", "1234", "Pipelines gRPC service port"),
    GRAFANA_URL(
        "HILO_GRAFANA_URL", "http://172.20.0.1:3000", "Grafana URL used by dashboard generation"),
    GRAFANA_TOKEN("GRAFANA_TOKEN", null, "Grafana API bearer token (no default)"),
    PUSHGATEWAY_ADDRESS(
        "HILO_PUSHGATEWAY_ADDRESS", "172.20.0.1:9091", "Prometheus Pushgateway host:port"),
    JDBC_URL("HILO_JDBC_URL", "jdbc:mysql://172.20.0.1:3306/database", "JDBC URL for MySQL push"),
    JDBC_USERNAME("HILO_JDBC_USERNAME", "root", "JDBC user"),
    JDBC_PASSWORD("HILO_JDBC_PASSWORD", "password", "JDBC password"),
    JDBC_DRIVER("HILO_JDBC_DRIVER", "com.mysql.jdbc.Driver", "JDBC driver class"),
    INFLUXDB_URL("HILO_INFLUXDB_URL", "127.0.0.1:8086", "InfluxDB address"),
    INFLUXDB_TOKEN("HILO_INFLUXDB_TOKEN", "token", "InfluxDB API token"),
    INFLUXDB_ORG("HILO_INFLUXDB_ORG", "org", "InfluxDB organization"),
    INFLUXDB_BUCKET("HILO_INFLUXDB_BUCKET", "db0", "InfluxDB bucket");

    private final String env;
    private final String defaultValue;
    private final String description;

    Setting(final String env, final String defaultValue, final String description) {
      this.env = env;
      this.defaultValue = defaultValue;
      this.description = description;
    }

    public String env() {
      return env;
    }

    /** The default value, or {@code null} if the setting has none. */
    public String defaultValue() {
      return defaultValue;
    }

    public String description() {
      return description;
    }
  }

  private final Function<String, String> env;
  private final Map<Setting, String> overrides;

  private HiloConfig(final Function<String, String> env, final Map<Setting, String> overrides) {
    this.env = env;
    this.overrides = overrides;
  }

  /** Settings from the process environment. */
  public static HiloConfig fromEnvironment() {
    return fromEnvironment(System::getenv);
  }

  /** Settings from the given environment lookup (for tests). */
  public static HiloConfig fromEnvironment(final Function<String, String> env) {
    return new HiloConfig(Objects.requireNonNull(env), new EnumMap<>(Setting.class));
  }

  /** Returns a copy in which {@code setting} is {@code value}; a null or blank value is ignored. */
  public HiloConfig with(final Setting setting, final String value) {
    if (value == null || value.isBlank()) {
      return this;
    }
    final var copy = new EnumMap<Setting, String>(Setting.class);
    copy.putAll(overrides);
    copy.put(setting, value);
    return new HiloConfig(env, copy);
  }

  /** The override, else the environment variable, else the default (which may be empty). */
  public Optional<String> find(final Setting setting) {
    final var override = overrides.get(setting);
    if (override != null) {
      return Optional.of(override);
    }
    final var fromEnv = env.apply(setting.env());
    if (fromEnv != null && !fromEnv.isBlank()) {
      return Optional.of(fromEnv);
    }
    return Optional.ofNullable(setting.defaultValue());
  }

  /**
   * The value of {@code setting}.
   *
   * @throws IllegalStateException if the setting has no value and no default
   */
  public String get(final Setting setting) {
    return find(setting)
        .orElseThrow(
            () -> new IllegalStateException(setting.env() + " is not set and has no default"));
  }

  public int getInt(final Setting setting) {
    final var value = get(setting);
    try {
      return Integer.parseInt(value.trim());
    } catch (NumberFormatException e) {
      throw new IllegalStateException(setting.env() + " must be an integer, got `" + value + "`");
    }
  }

  public String mongoUrl() {
    return get(Setting.MONGO_URL);
  }

  public String mongoDatabase() {
    return get(Setting.MONGO_DATABASE);
  }

  public String pipelineMongoUrl() {
    return get(Setting.PIPELINE_MONGO_URL);
  }

  public String flinkHost() {
    return get(Setting.FLINK_HOST);
  }

  public int flinkPort() {
    return getInt(Setting.FLINK_PORT);
  }
}
