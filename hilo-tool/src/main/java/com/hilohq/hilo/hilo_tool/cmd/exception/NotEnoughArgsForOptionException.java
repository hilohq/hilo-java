package com.hilohq.hilo.hilo_tool.cmd.exception;

public final class NotEnoughArgsForOptionException extends OptionException {
  public NotEnoughArgsForOptionException(
      final String option, final int expected, final int received) {
    super("Option `" + option + "` expects " + expected + " arguments but received " + received);
  }
}
