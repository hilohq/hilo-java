package com.hilohq.hilo.integration_influxdb.stages;

import com.hilohq.hilo.features_service.service.MongoFeaturesStore;
import com.hilohq.hilo.parsers.field.FieldKey;
import com.hilohq.hilo.parsers.field.Fields;
import com.hilohq.hilo.parsers.string.StringParser;
import com.hilohq.hilo.parsers.value.Value;
import com.hilohq.hilo.protobufs.structure.feature.FeatureOuterClass;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.write.Point;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.apache.flink.api.common.functions.RichGroupReduceFunction;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.util.Collector;

public final class InfluxDbValuesGenerate extends RichGroupReduceFunction<Fields<FieldKey>, Point> {
  private final String mongoUrl;
  private final String schemaId;
  private final StringParser parser;
  private Map<String, FeatureOuterClass.Feature> features;

  public InfluxDbValuesGenerate(final String mongoUrl, final String schemaId) {
    this.mongoUrl = mongoUrl;
    this.schemaId = schemaId;
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
    final var store = MongoFeaturesStore.open(mongoUrl);
    final var featuresModel = store.getFeatures(schemaId).get(5, TimeUnit.SECONDS);
    features = featuresModel.getFeaturesMap();
    store.close();
  }

  @Override
  public void reduce(Iterable<Fields<FieldKey>> fields, Collector<Point> out) throws Exception {
    for (final var record : fields) {
      reduceRecord(record, out);
    }
  }

  private void reduceRecord(Fields<FieldKey> record, Collector<Point> out) {
    var time = Instant.now().getEpochSecond() - 300;

    for (final var entry : record.map().entrySet()) {
      time += 1;
      if (!features.containsKey(entry.getKey().key())) {
        continue;
      }

      final var feature = features.get(entry.getKey().key());
      if (!feature.hasFeatureClass()
          || (!feature.getFeatureClass().hasGauge()
              && !feature.getFeatureClass().hasMonotonic()
              && !feature.getFeatureClass().hasCounter()
              && !feature.getFeatureClass().hasDiscrete())) {
        continue;
      }

      final var value = parseValue(entry.getValue());
      if (!value.isNumeric()) {
        continue;
      }

      final var point =
          Point.measurement(entry.getKey().key())
              .addField("value", value.numericValue())
              .time(time, WritePrecision.S);
      out.collect(point);
    }
  }
}
