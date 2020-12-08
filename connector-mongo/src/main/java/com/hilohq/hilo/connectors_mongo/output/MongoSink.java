package com.hilohq.hilo.connectors_mongo.output;

import com.hilohq.hilo.connectors_mongo.client.MongoClient;
import com.hilohq.hilo.connectors_mongo.client.MongoClients;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import java.util.concurrent.TimeUnit;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;

public final class MongoSink<T> extends RichSinkFunction<T> {

  private final String url;
  private final String database;
  private final MongoCommandGenerator<T> commandGenerator;
  private MongoClient mongo;

  public MongoSink(
      final String url, final String database, final MongoCommandGenerator<T> commandGenerator) {
    this.url = url;
    this.database = database;
    this.commandGenerator = commandGenerator;
  }

  @Override
  public void open(final Configuration parameters) throws Exception {
    final var settings =
        MongoClientSettings.builder()
            .applyConnectionString(new ConnectionString(url))
            .retryWrites(true)
            .build();
    mongo = MongoClients.create(settings);
  }

  @Override
  public void close() throws Exception {
    mongo.close();
  }

  @Override
  public void invoke(final T input, final Context context) throws Exception {
    final var command = commandGenerator.generate(database, input);
    command.execute(mongo).get(1, TimeUnit.SECONDS);
  }
}
