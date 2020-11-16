package com.hilohq.hilo.parsers.string;

import com.hilohq.hilo.parsers.value.*;

public final class StringParser implements ValueParser {
  public Value parse(final String s) {
    return parseChain(
        s,
        this::parseBoolean,
        this::parseInt,
        this::parseLong,
        this::parseFloat,
        this::parseDouble,
        this::parseString);
  }

  public BooleanValue parseBoolean(final String s) {
    if ("true".equalsIgnoreCase(s)) {
      return new BooleanValue(true);
    } else if ("false".equalsIgnoreCase(s)) {
      return new BooleanValue(false);
    } else {
      throw new IllegalArgumentException();
    }
  }

  public IntValue parseInt(final String s) {
    return new IntValue(Integer.parseInt(s));
  }

  public LongValue parseLong(final String s) {
    return new LongValue(Long.parseLong(s));
  }

  public FloatValue parseFloat(final String s) {
    return new FloatValue(Float.parseFloat(s));
  }

  public DoubleValue parseDouble(final String s) {
    return new DoubleValue(Double.parseDouble(s));
  }

  public StringValue parseString(final String s) {
    return new StringValue(s);
  }

  private Value parseChain(final String s, final ValueParser... parsers) {
    for (final var parser : parsers) {
      try {
        return parser.parse(s);
      } catch (IllegalArgumentException ignored) {
      }
    }

    throw new IllegalArgumentException("failed to parse string");
  }
}
