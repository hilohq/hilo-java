package com.hilohq.hilo.structure_learning_integration_tests.parsing.json;

public final class JsonSchemaGenerateTest {

  //    private List<Schema.SchemaEvent> generateSchema(final List<TextLineBatch> batches) throws
  // Exception {
  //        final StreamExecutionEnvironment executionEnvironment =
  //                StreamExecutionEnvironment.createRemoteEnvironment("127.0.0.1", 8081,
  //
  // "/home/eaugeas/Workspace/github.com/hilohq/hilo-java/structure-learning-integration-tests/build/libs/structure-learning-integration-tests-0.0.1-all.jar");
  //
  //        final DataStream<TextLineBatch> records =
  //                executionEnvironment.addSource(new TextLineBatchSource(batches)).name("source");
  //
  //        final DataStream<Schema.SchemaEvent> events =
  //                records.process(new JsonSchemaGenerate()).name("generate-schema");
  //
  //        final var pipe = new FileSinkPipe<Schema.SchemaEvent>();
  //        events.addSink(pipe.sink()).name("sink");
  //        executionEnvironment.execute("JsonParsing");
  //
  //        return pipe.events(Schema.SchemaEvent.newBuilder().build());
  //    }
  //
  //    private Schema.SchemaEvent eventWithFieldCreate(final List<Schema.SchemaEvent> events,
  //                                                    final String fieldName) {
  //        for (final var event : events) {
  //            if (event.getData().hasFieldCreate() &&
  // event.getData().getFieldCreate().getFieldStats().getJson().getName().equals(fieldName)) {
  //                return event;
  //            }
  //        }
  //
  //        throw new IllegalArgumentException("no event with field create");
  //    }
  //
  //    @Test
  //    public void testGenerateSchemaOneBatchCreateEvents() throws Exception {
  //        final List<Schema.SchemaEvent> events =
  // generateSchema(Collections.singletonList(TextLineBatch.fromLines(
  //                "{\"integer\": 1, \"string\": \"hello\"}",
  //                "{\"integer\": 2, \"string\": \"bye\"}"
  //        )));
  //
  //        assertEquals(3, events.size());
  //        assertEquals(eventWithFieldCreate(events, "integer").getData(),
  // Schema.SchemaEventData.newBuilder()
  //                .setFieldCreate(Schema.SchemaEventData.SchemaFieldCreate.newBuilder()
  //
  // .setFieldStats(Schema.SchemaFieldStats.newBuilder().setJson(Json.JsonFieldStats.newBuilder()
  //                                .setName("integer")
  //
  // .setTypeHistogram(Json.JsonTypeHistogram.newBuilder().setIntegerCount(2))
  //                                .build()).build()).build()).build());
  //        assertEquals(eventWithFieldCreate(events, "string").getData(),
  // Schema.SchemaEventData.newBuilder()
  //                .setFieldCreate(Schema.SchemaEventData.SchemaFieldCreate.newBuilder()
  //
  // .setFieldStats(Schema.SchemaFieldStats.newBuilder().setJson(Json.JsonFieldStats.newBuilder()
  //                                .setName("string")
  //
  // .setTypeHistogram(Json.JsonTypeHistogram.newBuilder().setStringCount(2))
  //                                .build()).build()).build()).build());
  //        assertEquals(events.get(2).getData(), Schema.SchemaEventData.newBuilder()
  //                .setParseStats(Schema.SchemaEventData.SchemaParseStats.newBuilder()
  //                        .setParseAttemptCount(2)
  //                        .setParseSuccessCount(2).build()).build());
  //    }
  //
  //    @Test
  //    public void testGenerateSchemaParseErrorEvent() throws Exception {
  //        final List<Schema.SchemaEvent> events = generateSchema(Collections.singletonList(
  //                TextLineBatch.fromLines("not a json")));
  //
  //        assertEquals(2, events.size());
  //        assertEquals(events.get(0).getData().getParseError().getContent(), "not a json");
  //        assertEquals(events.get(0).getData().getParseError().getReason(), "Invalid JSON");
  //
  // assertTrue(events.get(0).getData().getParseError().getDescription().contains("Unrecognized
  // token"));
  //    }
  //
  //    @Test
  //    public void testGenerateSchemaMultiBatchUpdateEvent() throws Exception {
  //        final List<Schema.SchemaEvent> events = generateSchema(Collections.singletonList(
  //                TextLineBatch.fromLines(
  //                        "{\"integer\": true, \"string\": \"hello\"}",
  //                        "{\"integer\": false, \"string\": \"bye\"}",
  //                        "{\"integer\": 1, \"string\": \"hello\"}",
  //                        "{\"integer\": 2, \"string\": \"bye\"}"
  //                )
  //        ));
  //
  //        assertEquals(3, events.size());
  //        assertEquals(eventWithFieldCreate(events, "integer").getData(),
  // Schema.SchemaEventData.newBuilder()
  //                .setFieldCreate(Schema.SchemaEventData.SchemaFieldCreate.newBuilder()
  //
  // .setFieldStats(Schema.SchemaFieldStats.newBuilder().setJson(Json.JsonFieldStats.newBuilder()
  //                                .setName("integer")
  //                                .setTypeHistogram(Json.JsonTypeHistogram.newBuilder()
  //                                        .setIntegerCount(2)
  //                                        .setBooleanCount(2)))
  //                                .build()).build()).build());
  //        assertEquals(eventWithFieldCreate(events, "string").getData(),
  // Schema.SchemaEventData.newBuilder()
  //                .setFieldCreate(Schema.SchemaEventData.SchemaFieldCreate.newBuilder()
  //
  // .setFieldStats(Schema.SchemaFieldStats.newBuilder().setJson(Json.JsonFieldStats.newBuilder()
  //                                .setName("string")
  //
  // .setTypeHistogram(Json.JsonTypeHistogram.newBuilder().setStringCount(4)))
  //                                .build()).build()).build());
  //        assertEquals(events.get(2).getData(), Schema.SchemaEventData.newBuilder()
  //                .setParseStats(Schema.SchemaEventData.SchemaParseStats.newBuilder()
  //                        .setParseAttemptCount(4)
  //                        .setParseSuccessCount(4).build()).build());
  //    }
}
