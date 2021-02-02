package com.hilohq.hilo.stages;

import static com.google.common.base.Verify.verifyNotNull;

import com.hilohq.hilo.integration_jdbc.stages.JdbcRowGenerate;
import com.hilohq.hilo.structure_learning.schema.json.JsonSchemaBatchValidate;
import de.javakaffee.kryoserializers.UnmodifiableCollectionsSerializer;
import de.javakaffee.kryoserializers.protobuf.ProtobufSerializer;
import org.apache.flink.api.common.ExecutionConfig;
import org.apache.flink.api.java.DataSet;
import org.apache.flink.types.Row;

public final class JsonJdbcPushStages implements BatchStages<String, Row> {
  private final String schemaId;
  private final String mongoUrl;
  private final String jdbcDbUrl;
  private final String jdbcUsername;
  private final String jdbcPassword;

  public static final class Builder {
    private String schemaId = null;
    private String mongoUrl = "localhost";
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

    public JsonJdbcPushStages build() {
      verifyNotNull(schemaId);
      verifyNotNull(jdbcDbUrl);
      verifyNotNull(jdbcUsername);
      verifyNotNull(jdbcPassword);

      return new JsonJdbcPushStages(mongoUrl, schemaId, jdbcDbUrl, jdbcUsername, jdbcPassword);
    }
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  JsonJdbcPushStages(
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
  }

  @Override
  public DataSet<Row> bind(final ExecutionConfig executionConfig, final DataSet<String> input)
      throws Exception {
    final Class<?> unmodColl = Class.forName("java.util.Collections$UnmodifiableCollection");
    executionConfig.addDefaultKryoSerializer(unmodColl, UnmodifiableCollectionsSerializer.class);
    final Class<?> type = Class.forName("com.google.protobuf.MapEntry");
    executionConfig.registerTypeWithKryoSerializer(type, ProtobufSerializer.class);

    return input
        .reduceGroup(new JsonSchemaBatchValidate(mongoUrl, schemaId))
        .reduceGroup(
            JdbcRowGenerate.newBuilder()
                .mongoUrl(mongoUrl)
                .schemaId(schemaId)
                .jdbcDbUrl(jdbcDbUrl)
                .jdbcPassword(jdbcPassword)
                .jdbcUsername(jdbcUsername)
                .build());
  }
}
