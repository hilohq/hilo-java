package com.hilohq.hilo.integration_jdbc.stages;

import static com.google.common.base.Verify.verifyNotNull;
import static com.hilohq.hilo.integration_jdbc.utils.CreateTableQueryUtils.createMysqlTableQuery;

import com.hilohq.hilo.integrations_service.service.MongoIntegrationsStore;
import com.hilohq.hilo.parsers.field.FieldKey;
import com.hilohq.hilo.parsers.field.Fields;
import com.hilohq.hilo.parsers.string.StringParser;
import com.hilohq.hilo.parsers.value.Value;
import com.hilohq.hilo.protobufs.integrations.jdbc.Jdbc;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.concurrent.TimeUnit;
import org.apache.flink.api.common.functions.RichGroupReduceFunction;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.types.Row;
import org.apache.flink.util.Collector;

public final class JdbcRowGenerate extends RichGroupReduceFunction<Fields<FieldKey>, Row> {
  private final String mongoUrl;
  private final String schemaId;
  private final StringParser parser;
  private final String jdbcDbUrl;
  private final String jdbcUsername;
  private final String jdbcPassword;
  private Jdbc.JdbcMetricsModel metrics;

  public static final class Builder {
    private String mongoUrl;
    private String schemaId;
    private String jdbcDbUrl;
    private String jdbcUsername;
    private String jdbcPassword;

    public Builder schemaId(final String schemaId) {
      verifyNotNull(schemaId);
      this.schemaId = schemaId;
      return this;
    }

    public Builder mongoUrl(final String mongoUrl) {
      verifyNotNull(mongoUrl);
      this.mongoUrl = mongoUrl;
      return this;
    }

    public Builder jdbcDbUrl(final String jdbcDbUrl) {
      verifyNotNull(jdbcDbUrl);
      this.jdbcDbUrl = jdbcDbUrl;
      return this;
    }

    public Builder jdbcUsername(final String jdbcUsername) {
      verifyNotNull(jdbcUsername);
      this.jdbcUsername = jdbcUsername;
      return this;
    }

    public Builder jdbcPassword(final String jdbcPassword) {
      verifyNotNull(jdbcPassword);
      this.jdbcPassword = jdbcPassword;
      return this;
    }

    public JdbcRowGenerate build() {
      verifyNotNull(mongoUrl);
      verifyNotNull(schemaId);
      verifyNotNull(jdbcDbUrl);
      verifyNotNull(jdbcUsername);
      verifyNotNull(jdbcPassword);

      return new JdbcRowGenerate(mongoUrl, schemaId, jdbcDbUrl, jdbcUsername, jdbcPassword);
    }
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  public JdbcRowGenerate(
      final String mongoUrl,
      final String schemaId,
      final String jdbcDbUrl,
      final String jdbcUsername,
      final String jdbcPassword) {
    this.mongoUrl = mongoUrl;
    this.schemaId = schemaId;
    this.jdbcDbUrl = jdbcDbUrl;
    this.jdbcUsername = jdbcUsername;
    this.jdbcPassword = jdbcPassword;
    this.parser = new StringParser();
  }

  private Value parseValue(final Value value) {
    if (value.isString()) {
      return parser.parse(value.stringValue());
    }
    return value;
  }

  @Override
  public void open(final Configuration parameters) throws Exception {
    Connection conn = null;
    try {
      Class.forName("com.mysql.jdbc.Driver");
      final var store = MongoIntegrationsStore.open(mongoUrl);
      metrics = store.getJdbcMetrics(schemaId).get(5, TimeUnit.SECONDS);

      conn = DriverManager.getConnection(jdbcDbUrl, jdbcUsername, jdbcPassword);
      final var sql = createMysqlTableQuery(schemaId, metrics.getMysql());
      final var statement = conn.createStatement();
      statement.execute(sql);

    } finally {
      if (conn != null) {
        conn.close();
      }
    }
  }

  @Override
  public void reduce(Iterable<Fields<FieldKey>> values, Collector<Row> out) throws Exception {
    for (final var value : values) {
      reduceValue(value, out);
    }
  }

  private void reduceValue(Fields<FieldKey> values, Collector<Row> out) {
    final var row = new Row(metrics.getMysql().getMetricsCount());
    final var metricsMap = metrics.getMysql().getMetricsMap();
    boolean setValue = false;
    for (final var entry : values.map().entrySet()) {
      if (!metricsMap.containsKey(entry.getKey().key())) {
        continue;
      }

      final var value = parseValue(entry.getValue());
      if (!value.isNumeric()) {
        continue;
      }

      setValue = true;
      row.setField(metricsMap.get(entry.getKey().key()).getIndex(), "" + value.numericValue());
    }

    if (setValue) {
      out.collect(row);
    }
  }
}
