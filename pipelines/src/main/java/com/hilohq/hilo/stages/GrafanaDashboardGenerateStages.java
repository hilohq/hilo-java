package com.hilohq.hilo.stages;

import com.hilohq.hilo.integration_grafana.entity.GrafanaDashboard;
import com.hilohq.hilo.integration_grafana.stages.GrafanaDashboardGenerate;
import com.hilohq.hilo.protobufs.structure.feature.FeatureOuterClass;
import de.javakaffee.kryoserializers.UnmodifiableCollectionsSerializer;
import org.apache.flink.api.common.ExecutionConfig;
import org.apache.flink.api.java.DataSet;

public final class GrafanaDashboardGenerateStages
    implements BatchStages<FeatureOuterClass.FeaturesModel, GrafanaDashboard> {
  @Override
  public DataSet<GrafanaDashboard> bind(
      final ExecutionConfig config, final DataSet<FeatureOuterClass.FeaturesModel> input)
      throws Exception {
    final Class<?> unmodColl = Class.forName("java.util.Collections$UnmodifiableCollection");
    config.addDefaultKryoSerializer(unmodColl, UnmodifiableCollectionsSerializer.class);

    return input.reduceGroup(new GrafanaDashboardGenerate());
  }
}
