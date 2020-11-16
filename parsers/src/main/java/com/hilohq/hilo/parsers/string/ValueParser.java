package com.hilohq.hilo.parsers.string;

import com.hilohq.hilo.parsers.value.Value;
import java.io.Serializable;

@FunctionalInterface
public interface ValueParser extends Serializable {
  Value parse(String s);
}
