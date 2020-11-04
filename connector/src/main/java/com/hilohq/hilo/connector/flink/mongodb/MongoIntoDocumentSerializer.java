package com.hilohq.hilo.connector.flink.mongodb;

import java.io.Serializable;
import org.bson.Document;

public interface MongoIntoDocumentSerializer<T> extends Serializable {
  Document serialize(T value);
}
