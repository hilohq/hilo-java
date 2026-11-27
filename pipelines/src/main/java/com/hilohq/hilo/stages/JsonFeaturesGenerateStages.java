package com.hilohq.hilo.stages;

import static com.google.common.base.Verify.verifyNotNull;

import com.hilohq.hilo.config.HiloConfig;
import com.hilohq.hilo.protobufs.structure.feature.FeatureOuterClass;
import com.hilohq.hilo.structure_learning.features.FeaturesStatsGenerate;
import com.hilohq.hilo.structure_learning.features.FeaturesUpdateGenerate;
import com.hilohq.hilo.structure_learning.features.MongoFeaturesLoader;
import com.hilohq.hilo.structure_learning.schema.json.JsonSchemaBatchValidate;
import de.javakaffee.kryoserializers.UnmodifiableCollectionsSerializer;
import de.javakaffee.kryoserializers.protobuf.ProtobufSerializer;
import org.apache.flink.api.common.ExecutionConfig;
import org.apache.flink.api.java.DataSet;

public final class JsonFeaturesGenerateStages
    implements BatchStages<String, FeatureOuterClass.FeaturesUpdate> {
  private final String schemaId;
  private final String mongoUrl;
  private final String database;

  public static final class Builder {
    private String schemaId = null;
    private String mongoUrl = "localhost";
    private String database = HiloConfig.fromEnvironment().mongoDatabase();

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

    /** Database the previously learned features are read from ({@code HILO_MONGO_DATABASE}). */
    public Builder database(final String database) {
      verifyNotNull(database);
      this.database = database;
      return this;
    }

    public JsonFeaturesGenerateStages build() {
      return new JsonFeaturesGenerateStages(schemaId, mongoUrl, database);
    }
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  JsonFeaturesGenerateStages(final String schemaId, final String mongoUrl, final String database) {
    this.schemaId = schemaId;
    this.mongoUrl = mongoUrl;
    this.database = database;
  }

  @Override
  public DataSet<FeatureOuterClass.FeaturesUpdate> bind(
      final ExecutionConfig executionConfig, final DataSet<String> input) throws Exception {
    final Class<?> unmodColl = Class.forName("java.util.Collections$UnmodifiableCollection");
    executionConfig.addDefaultKryoSerializer(unmodColl, UnmodifiableCollectionsSerializer.class);
    final Class<?> type = Class.forName("com.google.protobuf.MapEntry");
    executionConfig.registerTypeWithKryoSerializer(type, ProtobufSerializer.class);

    return input
        .reduceGroup(new JsonSchemaBatchValidate(mongoUrl, schemaId))
        .reduceGroup(new FeaturesStatsGenerate())
        .reduceGroup(new FeaturesUpdateGenerate(new MongoFeaturesLoader(mongoUrl, database)));
  }
}
