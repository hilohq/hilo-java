package com.hilohq.hilo.structure_learning.parsing.json;

import com.hilohq.hilo.connector.mongodb.client.MongoClient;
import com.hilohq.hilo.connector.mongodb.client.MongoClients;
import com.hilohq.hilo.connector.mongodb.codec.MongoProtobufCodec;
import com.hilohq.hilo.protobufs.schema.Model;
import com.hilohq.hilo.structure_learning.parsing.schema.Batch;
import com.hilohq.hilo.structure_learning.parsing.schema.Samples;
import com.hilohq.hilo.structure_learning.parsing.schema.SchemaValidate;
import com.hilohq.hilo.structure_learning.parsing.text.TextLineBatch;
import com.hilohq.hilo.structure_learning.parsing.value.*;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.flink.util.Collector;
import org.bson.BsonDocument;
import org.bson.BsonString;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public final class JsonSchemaValidate extends SchemaValidate<TextLineBatch> {

  private final String database;
  private final String url;
  private final String schemaId;
  private final ObjectMapper mapper;
  private Model.SchemaModel schema;

  public JsonSchemaValidate(final String database,
                            final String url,
                            final String schemaId) {
    this.database = database;
    this.url = url;
    this.schemaId = schemaId;
    this.mapper = new ObjectMapper();
  }

  @Override
  public void open(final Configuration parameters) throws Exception {
    final var codec = new MongoProtobufCodec<>(Model.SchemaMetadataModel.newBuilder().build());
    final var codecRegistry = CodecRegistries.fromCodecs(codec);
    final var settings =
        MongoClientSettings.builder()
            .applyConnectionString(new ConnectionString(url))
            .codecRegistry(codecRegistry)
            .build();
    final var mongo = MongoClients.create(settings);
    final var filter = new BsonDocument();
    filter.append("metadata.schemaId", new BsonString(schemaId));
    schema = mongo.findOne(database, "schemas", filter, Model.SchemaModel.class).get(5, TimeUnit.SECONDS);
    mongo.close();
  }

  @Override
  public void processElement(final TextLineBatch value,
                             final Context ctx,
                             final Collector<Samples> out) throws Exception {
    final var samples = new ArrayList<Map<String, Value>>();
    for (final var line : value.lines()) {
      final var node = mapper.readTree(line);
      final var sample = new HashMap<String, Value>();
      for (final var field : schema.getJson().getFieldsMap().values()) {
        if (node.has(field.getField().getName())) {
          final var fieldNode = node.get(field.getField().getName());
          if (!fieldNode.isValueNode()) {
            continue;
          }

          if (fieldNode.isBoolean()) {
            sample.put(field.getField().getName(), new BooleanValue(fieldNode.asBoolean()));

          } else if (fieldNode.isInt()) {
            sample.put(field.getField().getName(), new IntValue(fieldNode.asInt()));

          } else if (fieldNode.isLong()) {
            sample.put(field.getField().getName(), new LongValue(fieldNode.asLong()));

          } else if (fieldNode.isFloat()) {
            sample.put(field.getField().getName(), new FloatValue((float) fieldNode.asDouble()));

          } else if (fieldNode.isDouble()) {
            sample.put(field.getField().getName(), new DoubleValue(fieldNode.asDouble()));

          } else if (fieldNode.isTextual()) {
            sample.put(field.getField().getName(), new StringValue(fieldNode.asText()));
          }
        }
        if (!sample.isEmpty()){
          samples.add(sample);
        }
      }
    }

    out.collect(new Samples(samples));
  }

  @Override
  public void close() {

  }

}
