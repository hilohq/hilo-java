package com.hilohq.hilo.pipelines;

import org.apache.flink.api.common.io.InputFormat;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.typeutils.TypeExtractor;

public final class DefaultTypeInformationSupplier<T> implements TypeInformationSupplier<T> {
  @Override
  public TypeInformation<T> of(InputFormat<T, ?> type) {
    return TypeExtractor.getInputFormatTypes(type);
  }
}
