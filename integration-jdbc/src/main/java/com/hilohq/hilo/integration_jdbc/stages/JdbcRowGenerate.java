package com.hilohq.hilo.integration_jdbc.stages;

import com.hilohq.hilo.features_service.service.MongoFeaturesStore;
import com.hilohq.hilo.parsers.field.FieldKey;
import com.hilohq.hilo.parsers.field.Fields;
import com.hilohq.hilo.parsers.string.StringParser;
import com.hilohq.hilo.parsers.value.Value;
import com.hilohq.hilo.protobufs.structure.feature.FeatureOuterClass;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import org.apache.flink.api.common.functions.RichGroupReduceFunction;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.types.Row;
import org.apache.flink.util.Collector;

public final class JdbcRowGenerate extends RichGroupReduceFunction<Fields<FieldKey>, Row> {
  private final String mongoUrl;
  private final String schemaId;
  private final StringParser parser;
  private final Map<String, Integer> featureIndices;

  public JdbcRowGenerate(final String mongoUrl, final String schemaId) {
    this.mongoUrl = mongoUrl;
    this.schemaId = schemaId;
    this.parser = new StringParser();
    this.featureIndices = new HashMap<>();
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
    final var features = featuresModel.getFeaturesMap();
    store.close();

    final var featuresList =
        features
            .values()
            .stream()
            .filter(
                feature ->
                    feature.hasFeatureClass()
                        && (feature.getFeatureClass().hasGauge()
                            || feature.getFeatureClass().hasMonotonic()
                            || feature.getFeatureClass().hasCounter()
                            || feature.getFeatureClass().hasDiscrete()
                            || feature.getFeatureClass().hasTimestamp()))
            .sorted(Comparator.comparing(FeatureOuterClass.Feature::getKey))
            .collect(Collectors.toList());

    for (int i = 0; i < featuresList.size(); i++) {
      featureIndices.put(featuresList.get(i).getKey(), i);
    }
  }

  @Override
  public void reduce(Iterable<Fields<FieldKey>> values, Collector<Row> out) throws Exception {
    for (final var value : values) {
      reduceValue(value, out);
    }
  }

  private void reduceValue(Fields<FieldKey> values, Collector<Row> out) {
    final var row = new Row(featureIndices.size());
    boolean setValue = false;
    for (final var entry : values.map().entrySet()) {
      if (!featureIndices.containsKey(entry.getKey().key())) {
        continue;
      }

      final var value = parseValue(entry.getValue());
      if (!value.isNumeric()) {
        continue;
      }

      setValue = true;
      row.setField(featureIndices.get(entry.getKey().key()), "" + value.numericValue());
    }

    if (setValue) {
      out.collect(row);
    }
  }
}
