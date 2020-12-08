package com.hilohq.hilo.connectors_mongo.input;

import com.google.protobuf.Message;
import com.hilohq.hilo.connectors_mongo.client.MongoClient;
import com.hilohq.hilo.connectors_mongo.client.MongoClients;
import com.hilohq.hilo.connectors_mongo.codec.MongoProtobufCodec;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.apache.flink.api.common.io.RichInputFormat;
import org.apache.flink.api.common.io.statistics.BaseStatistics;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.core.io.InputSplitAssigner;
import org.bson.BsonDocument;
import org.bson.BsonString;
import org.bson.codecs.configuration.CodecRegistries;

public final class MongoInputFormat<T extends Message> extends RichInputFormat<T, IdInputSplit> {

  private final String mongoUrl;
  private final String db;
  private final String collection;
  private final List<String> ids;
  private final String key;
  private final T message;
  private MongoClient mongoClient;

  public MongoInputFormat(
      final String mongoUrl,
      final String db,
      final String collection,
      final List<String> ids,
      final String key,
      final T message) {
    this.mongoUrl = mongoUrl;
    this.db = db;
    this.collection = collection;
    this.ids = new ArrayList<>(ids);
    this.key = key;
    this.message = message;
  }

  private static final class MongoRecordStats implements BaseStatistics {

    private final long numberOfRecords;

    public MongoRecordStats(final long numberOfRecords) {
      this.numberOfRecords = numberOfRecords;
    }

    @Override
    public long getTotalInputSize() {
      return SIZE_UNKNOWN;
    }

    @Override
    public long getNumberOfRecords() {
      return numberOfRecords;
    }

    @Override
    public float getAverageRecordWidth() {
      return AVG_RECORD_BYTES_UNKNOWN;
    }
  }

  @Override
  public void configure(Configuration parameters) {}

  @Override
  public BaseStatistics getStatistics(BaseStatistics cachedStatistics) throws IOException {
    return new MongoRecordStats(ids.size());
  }

  @Override
  public IdInputSplit[] createInputSplits(int minNumSplits) throws IOException {
    return new IdInputSplit[] {new IdInputSplit(0)};
  }

  @Override
  public InputSplitAssigner getInputSplitAssigner(IdInputSplit[] inputSplits) {
    final var list = new ArrayList<IdInputSplit>();
    Collections.addAll(list, inputSplits);
    return new MongoInputSplitAssigner(list);
  }

  @Override
  public void open(IdInputSplit split) throws IOException {
    final var codecRegistry = CodecRegistries.fromCodecs(new MongoProtobufCodec<>(message));

    final var settings =
        MongoClientSettings.builder()
            .applyConnectionString(new ConnectionString(mongoUrl))
            .codecRegistry(codecRegistry)
            .build();
    mongoClient = MongoClients.create(settings);
  }

  @Override
  public void openInputFormat() {}

  @Override
  public void closeInputFormat() {}

  @Override
  public boolean reachedEnd() throws IOException {
    return ids.isEmpty();
  }

  @Override
  public T nextRecord(T reuse) throws IOException {
    final var filter = new BsonDocument();
    final var id = ids.remove(0);
    filter.append(key, new BsonString(id));
    try {
      final var record =
          mongoClient
              .findOne(db, collection, filter, (Class<T>) message.getClass())
              .get(5, TimeUnit.SECONDS);
      return record;
    } catch (ExecutionException | InterruptedException | TimeoutException e) {
      throw new IOException(e);
    }
  }

  @Override
  public void close() throws IOException {
    mongoClient.close();
  }
}
