package com.hilohq.hilo.integration_jdbc.stages;

import com.hilohq.hilo.features_service.service.MongoFeaturesStore;
import com.hilohq.hilo.parsers.field.FieldKey;
import com.hilohq.hilo.parsers.field.Fields;
import com.hilohq.hilo.protobufs.integrations.jdbc.Jdbc;
import com.hilohq.hilo.protobufs.integrations.jdbc.Mysql;
import com.hilohq.hilo.protobufs.structure.feature.FeatureOuterClass;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.apache.flink.api.common.functions.RichGroupReduceFunction;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.util.Collector;

public final class JdbcMysqlTableGenerate
    extends RichGroupReduceFunction<Fields<FieldKey>, Jdbc.JdbcEvent> {
  private final String mongoUrl;
  private final String schemaId;
  private Map<String, FeatureOuterClass.Feature> features;

  public JdbcMysqlTableGenerate(final String mongoUrl, final String schemaId) {
    this.mongoUrl = mongoUrl;
    this.schemaId = schemaId;
  }

  @Override
  public void open(final Configuration parameters) throws Exception {
    final var store = MongoFeaturesStore.open(mongoUrl);
    final var featuresModel = store.getFeatures(schemaId).get(5, TimeUnit.SECONDS);
    features = featuresModel.getFeaturesMap();
    store.close();
  }

  @Override
  public void reduce(final Iterable<Fields<FieldKey>> values, final Collector<Jdbc.JdbcEvent> out)
      throws Exception {
    final var metadataBuilder = Jdbc.JdbcMetadata.newBuilder();
    final var metrics = new HashMap<String, Mysql.MysqlMetric>();

    for (final var value : values) {
      metadataBuilder
          .setProjectId(value.metadata().getProjectId())
          .setSchemaId(value.metadata().getSchemaId())
          .setSourceId(value.metadata().getSourceId());

      for (final var field : value.map().entrySet()) {
        if (!features.containsKey(field.getKey().key())) {
          continue;
        }

        final var feature = features.get(field.getKey().key());
        if (!feature.getFeatureClass().hasGauge()
            && !feature.getFeatureClass().hasMonotonic()
            && !feature.getFeatureClass().hasCounter()
            && !feature.getFeatureClass().hasDiscrete()
            && !feature.getFeatureClass().hasTimestamp()) {
          continue;
        }

        if (!metrics.containsKey(field.getKey().key())) {
          metrics.put(
              field.getKey().key(),
              Mysql.MysqlMetric.newBuilder()
                  .setIndex(metrics.size())
                  .setName(field.getKey().key())
                  .setType(Mysql.MysqlType.DOUBLE)
                  .build());
        }
      }
    }

    final var metadata = metadataBuilder.build();
    for (final var metric : metrics.values()) {
      out.collect(
          Jdbc.JdbcEvent.newBuilder()
              .setMetadata(metadata)
              .setMysql(Mysql.MysqlEvent.newBuilder().setMetric(metric).build())
              .build());
    }

    final var list = Collections.nCopies(metrics.size(), "?");
    final var loadQuery = "INSERT INTO `" + schemaId + "` VALUES(" + String.join(",", list) + ");";
    out.collect(
        Jdbc.JdbcEvent.newBuilder()
            .setMetadata(metadata)
            .setMysql(
                Mysql.MysqlEvent.newBuilder()
                    .setLoadQuery(
                        Mysql.MysqlEvent.LoadQuery.newBuilder().setLoadQuery(loadQuery).build())
                    .build())
            .build());
  }
}
