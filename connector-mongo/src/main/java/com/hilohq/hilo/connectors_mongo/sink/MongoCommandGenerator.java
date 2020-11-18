package com.hilohq.hilo.connectors_mongo.sink;

import java.io.Serializable;

public interface MongoCommandGenerator<T> extends Serializable {
  MongoCommand generate(String database, T value);
}
