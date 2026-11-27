package com.hilohq.integration_tests.mongo;

import static com.hilohq.integration_tests.resources.ResourceUtils.readStringResource;

import com.google.protobuf.Message;
import com.google.protobuf.util.JsonFormat;
import com.hilohq.hilo.config.HiloConfig;
import com.hilohq.hilo.connectors_mongo.client.MongoClients;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.bson.Document;

public final class MongoUtils {
  private static final HiloConfig CONFIG = HiloConfig.fromEnvironment();

  private MongoUtils() {}

  public static void insertResource(
      final String resourcePath,
      final String collectionName,
      final Map<String, String> substitutions)
      throws Exception {
    final var client = MongoClients.create(CONFIG.mongoUrl());
    final var resource = readStringResource(resourcePath, substitutions);
    final var document = Document.parse(resource);
    client.insertOne(CONFIG.mongoDatabase(), collectionName, document).get(5, TimeUnit.SECONDS);
    client.close();
  }

  public static void insertResource(
      final String resourcePath,
      final String collectionName,
      final Message type,
      final Map<String, String> substitutions)
      throws Exception {
    final var client = MongoClients.create(CONFIG.mongoUrl());
    final var resource = readStringResource(resourcePath, substitutions);
    final var builder = type.newBuilderForType();
    JsonFormat.parser().merge(resource, builder);
    final var s = JsonFormat.printer().print(builder);
    final var document = Document.parse(s);
    client.insertOne(CONFIG.mongoDatabase(), collectionName, document).get(5, TimeUnit.SECONDS);
    client.close();
  }
}
