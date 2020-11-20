package com.hilohq.hilo.hilo_tool.cmd.exception;

public class UnknownCommandException extends CommandException {
  public UnknownCommandException(final String command) {
    super("unknown command `" + command + "`");
  }
}
