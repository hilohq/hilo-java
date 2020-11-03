package com.hilohq.hilo.structure_learning.parsing.schema;

import com.hilohq.hilo.protobufs.schema.Schema;
import org.apache.flink.streaming.api.functions.ProcessFunction;

public abstract class SchemaGenerate<T> extends ProcessFunction<T, Schema.SchemaEvent> {}
