package com.hilohq.hilo.hilo_tool.cmd;

public interface Command extends CommandFunction {
  String name();

  String help();
}
