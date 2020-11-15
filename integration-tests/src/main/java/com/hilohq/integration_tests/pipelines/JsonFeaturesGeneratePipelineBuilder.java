package com.hilohq.integration_tests.pipelines;

import com.hilohq.hilo.protobufs.structure.feature.FeatureOuterClass;
import com.hilohq.hilo.structure_learning.features.FeaturesStatsGenerate;
import com.hilohq.hilo.structure_learning.features.FeaturesUpdateGenerate;
import com.hilohq.hilo.structure_learning.schema.json.JsonSchemaValidate;
import de.javakaffee.kryoserializers.UnmodifiableCollectionsSerializer;
import de.javakaffee.kryoserializers.protobuf.ProtobufSerializer;
import org.apache.flink.api.java.DataSet;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.operators.DataSource;

public final class JsonFeaturesGeneratePipelineBuilder
    implements PipelineBuilder<String, FeatureOuterClass.FeaturesUpdate> {
  private final String schemaId;
  private final String mongoUrl;

  public JsonFeaturesGeneratePipelineBuilder(final String schemaId, final String mongoUrl) {
    this.schemaId = schemaId;
    this.mongoUrl = mongoUrl;
  }

  @Override
  public DataSet<FeatureOuterClass.FeaturesUpdate> build(
      final ExecutionEnvironment executionEnvironment, final DataSource<String> input)
      throws Exception {
    final Class<?> unmodColl = Class.forName("java.util.Collections$UnmodifiableCollection");
    executionEnvironment
        .getConfig()
        .addDefaultKryoSerializer(unmodColl, UnmodifiableCollectionsSerializer.class);
    final Class<?> type = Class.forName("com.google.protobuf.MapEntry");
    executionEnvironment.getConfig().registerTypeWithKryoSerializer(type, ProtobufSerializer.class);

    return input
        .reduceGroup(new JsonSchemaValidate(mongoUrl, schemaId))
        .reduceGroup(new FeaturesStatsGenerate())
        .reduceGroup(new FeaturesUpdateGenerate());
  }
}
