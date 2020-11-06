package com.hilohq.hilo.connector.mongodb.flink;

import java.io.Serializable;

public interface MongoCommandGenerator<T> extends Serializable {
  MongoCommand<T> generate(String database, T value);
}
