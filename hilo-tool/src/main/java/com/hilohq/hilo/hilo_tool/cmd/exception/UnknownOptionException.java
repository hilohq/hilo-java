package com.hilohq.hilo.hilo_tool.cmd.exception;

public final class UnknownOptionException extends OptionException {
  public UnknownOptionException(final String option) {
    super("unknown option `" + option + "`");
  }
}
