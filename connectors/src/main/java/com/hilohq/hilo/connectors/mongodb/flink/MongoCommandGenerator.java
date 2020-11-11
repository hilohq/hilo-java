package com.hilohq.hilo.connectors.mongodb.flink;

import java.io.Serializable;

public interface MongoCommandGenerator<T> extends Serializable {
  MongoCommand generate(String database, T value);
}
