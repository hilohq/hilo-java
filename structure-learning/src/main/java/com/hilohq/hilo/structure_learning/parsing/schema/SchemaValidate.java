package com.hilohq.hilo.structure_learning.parsing.schema;

import com.hilohq.hilo.protobufs.schema.Model;
import org.apache.flink.streaming.api.functions.KeyedProcessFunction;

public abstract class SchemaValidate<T extends Batch> extends KeyedProcessFunction<Model.SchemaModel, T, Samples> {}
