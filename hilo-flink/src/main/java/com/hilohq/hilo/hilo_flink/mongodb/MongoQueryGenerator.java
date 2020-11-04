package com.hilohq.hilo.hilo_flink.mongodb;

import org.bson.Document;

import java.io.Serializable;

public interface MongoQueryGenerator<T> extends Serializable {
    Query query(T payload);
}
