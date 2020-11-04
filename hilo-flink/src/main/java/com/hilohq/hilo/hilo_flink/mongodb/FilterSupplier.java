package com.hilohq.hilo.hilo_flink.mongodb;

import org.bson.conversions.Bson;

import java.io.Serializable;

public interface FilterSupplier<T> extends Serializable {
    Bson supply(T data);
}
