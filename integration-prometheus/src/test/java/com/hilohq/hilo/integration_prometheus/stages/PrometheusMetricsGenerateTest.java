package com.hilohq.hilo.integration_prometheus.stages;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.google.protobuf.Message;
import com.google.protobuf.util.JsonFormat;
import com.hilohq.hilo.parsers.field.FieldKey;
import com.hilohq.hilo.parsers.field.Fields;
import com.hilohq.hilo.parsers.field.FieldsMap;
import com.hilohq.hilo.parsers.logparse.json.JsonFieldKey;
import com.hilohq.hilo.parsers.logparse.json.JsonParser;
import com.hilohq.hilo.parsers.logparse.json.JsonType;
import com.hilohq.hilo.parsers.logparse.json.JsonValidator;
import com.hilohq.hilo.parsers.logparse.log.TypeInfo;
import com.hilohq.hilo.protobufs.integrations.prometheus.Prometheus;
import com.hilohq.hilo.protobufs.structure.feature.FeatureOuterClass;
import com.hilohq.hilo.protobufs.structure.schema.Schema;
import com.hilohq.hilo.structure_learning.schema.json.JsonProtobufUtils;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.flink.api.common.functions.util.ListCollector;
import org.junit.Test;

/**
 * Tests the metric-model generation step. PrometheusMetricsGenerate normally loads the learned
 * features from Mongo in open(); here they are injected directly so the reduce logic can be checked
 * in isolation.
 */
public final class PrometheusMetricsGenerateTest {

  private static final Path SAMPLES_DIR =
      Path.of("..", "integration-tests", "src", "integration-test", "resources", "samples");
  private static final String SCHEMA_ID = "test-schema";

  private static final Schema.SchemaMetadata METADATA =
      Schema.SchemaMetadata.newBuilder()
          .setProjectId("test-project")
          .setSourceId("test-source")
          .setSchemaId(SCHEMA_ID)
          .build();

  static void inject(final Object target, final String field, final Object value) throws Exception {
    final var f = target.getClass().getDeclaredField(field);
    f.setAccessible(true);
    f.set(target, value);
  }

  private static String readSample(final String sample, final String file) throws Exception {
    return Files.readString(SAMPLES_DIR.resolve(sample).resolve(file))
        .replace("$PROJECT_ID", METADATA.getProjectId())
        .replace("$SOURCE_ID", METADATA.getSourceId())
        .replace("$SCHEMA_ID", METADATA.getSchemaId());
  }

  @SuppressWarnings("unchecked")
  private static <T extends Message> T readProto(
      final String sample, final String file, final T prototype) throws Exception {
    final var builder = prototype.newBuilderForType();
    JsonFormat.parser().merge(readSample(sample, file), builder);
    return (T) builder.build();
  }

  private static List<Fields<FieldKey>> validatedSampleFields(final String sample)
      throws Exception {
    final var schema =
        readProto(sample, "schema.json", Schema.SchemaFieldsModel.getDefaultInstance());
    final var types = new HashMap<JsonFieldKey, JsonType>();
    for (final var field : schema.getJson().getFieldsMap().values()) {
      types.put(
          new JsonFieldKey(field.getPath(), field.getName()),
          JsonProtobufUtils.mapFromProtoType(field.getType()));
    }
    final var parser = new JsonParser();
    final var validator = new JsonValidator(new TypeInfo<>(types));

    final var lines =
        readSample(sample, "logs.txt")
            .lines()
            .filter(l -> !l.isBlank())
            .collect(Collectors.toList());
    final var result = new ArrayList<Fields<FieldKey>>();
    for (final var line : lines) {
      final var map = new FieldsMap<FieldKey>();
      for (final var entry : validator.validate(parser.parse(line)).entrySet()) {
        map.put(entry.getKey(), entry.getValue());
      }
      result.add(new Fields<>(schema.getMetadata(), map));
    }
    return result;
  }

  private static List<Prometheus.PrometheusEvent> generate(
      final Map<String, FeatureOuterClass.Feature> features, final List<Fields<FieldKey>> input)
      throws Exception {
    final var stage = new PrometheusMetricsGenerate("unused", SCHEMA_ID);
    inject(stage, "features", features);
    final var out = new ArrayList<Prometheus.PrometheusEvent>();
    stage.reduce(input, new ListCollector<>(out));
    return out;
  }

  private static FeatureOuterClass.Feature feature(final FeatureOuterClass.FeatureClass cls) {
    return FeatureOuterClass.Feature.newBuilder().setFeatureClass(cls).build();
  }

  private static Fields<FieldKey> fields(final String... keys) {
    final var map = new FieldsMap<FieldKey>();
    for (final var key : keys) {
      map.put(new JsonFieldKey(key), new com.hilohq.hilo.parsers.value.IntValue(1));
    }
    return new Fields<>(METADATA, map);
  }

  @Test
  public void testGaugeFeaturesUseTheRawSeriesAndCountersUseIrate() throws Exception {
    final var features = new HashMap<String, FeatureOuterClass.Feature>();
    features.put(
        "temperature",
        feature(
            FeatureOuterClass.FeatureClass.newBuilder()
                .setGauge(FeatureOuterClass.FeatureClass.Gauge.getDefaultInstance())
                .build()));
    features.put(
        "requests",
        feature(
            FeatureOuterClass.FeatureClass.newBuilder()
                .setCounter(FeatureOuterClass.FeatureClass.Counter.getDefaultInstance())
                .build()));
    features.put(
        "bytes",
        feature(
            FeatureOuterClass.FeatureClass.newBuilder()
                .setMonotonic(FeatureOuterClass.FeatureClass.Monotonic.getDefaultInstance())
                .build()));

    final var events = generate(features, List.of(fields("temperature", "requests", "bytes")));

    final var exprs = new HashMap<String, String>();
    for (final var event : events) {
      assertEquals(SCHEMA_ID, event.getMetadata().getSchemaId());
      final var metric = event.getMetric();
      assertTrue(metric.hasGauge());
      assertEquals(metric.getKey(), metric.getGauge().getName());
      exprs.put(metric.getKey(), metric.getDashboard().getGrafana().getExpr());
    }
    assertEquals("temperature", exprs.get("temperature"));
    assertEquals("irate(requests[1m])", exprs.get("requests"));
    assertEquals("irate(bytes[1m])", exprs.get("bytes"));
  }

  @Test
  public void testNonNumericFeatureClassesAndUnknownFieldsAreSkipped() throws Exception {
    final var features = new HashMap<String, FeatureOuterClass.Feature>();
    features.put(
        "hash",
        feature(
            FeatureOuterClass.FeatureClass.newBuilder()
                .setEphemeral(FeatureOuterClass.FeatureClass.Ephemeral.getDefaultInstance())
                .build()));
    features.put(
        "timestamp",
        feature(
            FeatureOuterClass.FeatureClass.newBuilder()
                .setTimestamp(FeatureOuterClass.FeatureClass.Timestamp.getDefaultInstance())
                .build()));

    final var events = generate(features, List.of(fields("hash", "timestamp", "unknown")));

    assertTrue(events.isEmpty());
  }

  @Test
  public void testEachMetricIsEmittedOnceAcrossManyRecords() throws Exception {
    final var features =
        Collections.singletonMap(
            "gas",
            feature(
                FeatureOuterClass.FeatureClass.newBuilder()
                    .setDiscrete(FeatureOuterClass.FeatureClass.Discrete.getDefaultInstance())
                    .build()));

    final var events = generate(features, List.of(fields("gas"), fields("gas"), fields("gas")));

    assertEquals(1, events.size());
    assertFalse(events.get(0).getMetric().getDashboard().getGrafana().getExpr().contains("irate"));
  }

  private void assertMatchesSample(final String sample) throws Exception {
    final var features =
        readProto(sample, "features.json", FeatureOuterClass.FeaturesModel.getDefaultInstance())
            .getFeaturesMap();
    final var expected =
        readProto(
            sample, "prometheus.json", Prometheus.PrometheusMetricsModel.getDefaultInstance());

    final var actual = new HashMap<String, Prometheus.PrometheusMetric>();
    for (final var event : generate(features, validatedSampleFields(sample))) {
      actual.put(event.getMetric().getKey(), event.getMetric());
    }

    assertEquals(expected.getMetricsMap(), actual);
  }

  @Test
  public void testGeneratesTheBlocksSampleMetrics() throws Exception {
    assertMatchesSample("blocks");
  }

  @Test
  public void testGeneratesTheTransactionsSampleMetrics() throws Exception {
    assertMatchesSample("transactions");
  }
}
