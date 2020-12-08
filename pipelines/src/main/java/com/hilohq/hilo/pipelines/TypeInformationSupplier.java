package com.hilohq.hilo.pipelines;

import org.apache.flink.api.common.io.InputFormat;
import org.apache.flink.api.common.typeinfo.TypeInformation;

@FunctionalInterface
public interface TypeInformationSupplier<T> {
  TypeInformation<T> of(InputFormat<T, ?> type);
}
