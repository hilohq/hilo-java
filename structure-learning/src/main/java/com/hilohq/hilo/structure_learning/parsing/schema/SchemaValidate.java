package com.hilohq.hilo.structure_learning.parsing.schema;

import org.apache.flink.streaming.api.functions.ProcessFunction;

public abstract class SchemaValidate<T extends Batch> extends ProcessFunction<T, Samples> {}
