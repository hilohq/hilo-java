package com.hilohq.hilo.hilo_tool.cmd;

@FunctionalInterface
public interface CommandFunction {
  void run(String[] args) throws Exception;
}
