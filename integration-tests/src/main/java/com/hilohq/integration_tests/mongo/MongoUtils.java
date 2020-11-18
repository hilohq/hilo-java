package com.hilohq.integration_tests.mongo;

import static com.hilohq.integration_tests.resources.ResourceUtils.readStringResource;

import com.hilohq.hilo.connectors_mongo.client.MongoClients;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.bson.Document;

public final class MongoUtils {
  private MongoUtils() {}

  public static void insertResource(
      final String resourcePath,
      final String collectionName,
      final Map<String, String> substitutions)
      throws Exception {
    final var client = MongoClients.create("mongodb://root:example@localhost:27017/admin");
    final var schema = readStringResource(resourcePath, substitutions);
    final var document = Document.parse(schema);
    client.insertOne("test", collectionName, document).get(5, TimeUnit.SECONDS);
    client.close();
  }
}
