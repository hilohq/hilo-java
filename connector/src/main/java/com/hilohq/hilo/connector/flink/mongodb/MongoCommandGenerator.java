package com.hilohq.hilo.connector.flink.mongodb;

import java.io.Serializable;

public interface MongoCommandGenerator<T> extends Serializable {
  MongoCommand<T> generate(T value);
}
