package com.hilohq.hilo.connectors_mongo.sink;

import com.hilohq.hilo.connectors_mongo.client.MongoClient;
import com.hilohq.hilo.connectors_mongo.client.MongoClients;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.apache.flink.api.common.io.RichOutputFormat;
import org.apache.flink.configuration.Configuration;

public final class MongoOutputFormat<T> extends RichOutputFormat<T> {
  public static class Builder<T> {
    private String url;
    private String database;
    private MongoCommandGenerator<T> commandGenerator;

    private Builder(final MongoCommandGenerator<T> commandGenerator) {
      this.commandGenerator = commandGenerator;
    }

    public Builder<T> url(final String url) {
      this.url = url;
      return this;
    }

    public Builder<T> database(final String database) {
      this.database = database;
      return this;
    }

    public Builder<T> commandGenerator(final MongoCommandGenerator<T> commandGenerator) {
      this.commandGenerator = commandGenerator;
      return this;
    }

    public MongoOutputFormat<T> build() {
      return new MongoOutputFormat<>(url, database, commandGenerator);
    }
  }

  public static <T> Builder<T> newBuilder(final MongoCommandGenerator<T> commandGenerator) {
    return new Builder<>(commandGenerator);
  }

  private final String url;
  private final String database;
  private final MongoCommandGenerator<T> commandGenerator;
  private MongoClient client;

  MongoOutputFormat(
      final String url, final String database, final MongoCommandGenerator<T> commandGenerator) {
    this.url = url;
    this.database = database;
    this.commandGenerator = commandGenerator;
    this.client = null;
  }

  @Override
  public void configure(final Configuration parameters) {}

  @Override
  public void open(final int taskNumber, final int numTasks) throws IOException {
    client =
        MongoClients.create(
            MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString(url))
                .retryWrites(true)
                .build());
  }

  @Override
  public void writeRecord(final T input) throws IOException {
    final var command = commandGenerator.generate(database, input);
    try {
      command.execute(client).get(5, TimeUnit.SECONDS);
    } catch (InterruptedException | ExecutionException | TimeoutException e) {
      throw new IOException(e);
    }
  }

  @Override
  public void close() throws IOException {
    client.close();
  }
}
