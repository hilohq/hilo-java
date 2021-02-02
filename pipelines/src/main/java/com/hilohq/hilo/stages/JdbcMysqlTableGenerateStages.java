package com.hilohq.hilo.stages;

import static com.google.common.base.Verify.verifyNotNull;

import com.hilohq.hilo.integration_jdbc.stages.JdbcMysqlTableGenerate;
import com.hilohq.hilo.protobufs.integrations.jdbc.Jdbc;
import com.hilohq.hilo.structure_learning.schema.json.JsonSchemaBatchValidate;
import de.javakaffee.kryoserializers.UnmodifiableCollectionsSerializer;
import de.javakaffee.kryoserializers.protobuf.ProtobufSerializer;
import org.apache.flink.api.common.ExecutionConfig;
import org.apache.flink.api.java.DataSet;

public final class JdbcMysqlTableGenerateStages implements BatchStages<String, Jdbc.JdbcEvent> {
  private final String schemaId;
  private final String mongoUrl;

  public static final class Builder {
    private String schemaId = null;
    private String mongoUrl = "localhost";

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

    public JdbcMysqlTableGenerateStages build() {
      return new JdbcMysqlTableGenerateStages(schemaId, mongoUrl);
    }
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  JdbcMysqlTableGenerateStages(final String schemaId, final String mongoUrl) {
    this.schemaId = schemaId;
    this.mongoUrl = mongoUrl;
  }

  @Override
  public DataSet<Jdbc.JdbcEvent> bind(ExecutionConfig config, DataSet<String> input)
      throws Exception {
    final Class<?> unmodColl = Class.forName("java.util.Collections$UnmodifiableCollection");
    config.addDefaultKryoSerializer(unmodColl, UnmodifiableCollectionsSerializer.class);
    final Class<?> type = Class.forName("com.google.protobuf.MapEntry");
    config.registerTypeWithKryoSerializer(type, ProtobufSerializer.class);

    return input
        .reduceGroup(new JsonSchemaBatchValidate(mongoUrl, schemaId))
        .reduceGroup(new JdbcMysqlTableGenerate(mongoUrl, schemaId));
  }
}
