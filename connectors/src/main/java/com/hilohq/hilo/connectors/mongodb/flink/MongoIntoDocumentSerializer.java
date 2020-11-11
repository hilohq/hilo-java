package com.hilohq.hilo.connectors.mongodb.flink;

import java.io.Serializable;
import org.bson.Document;

public interface MongoIntoDocumentSerializer<T> extends Serializable {
  Document serialize(T value);
}
