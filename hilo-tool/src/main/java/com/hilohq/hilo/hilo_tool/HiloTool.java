package com.hilohq.hilo.hilo_tool;

import com.hilohq.hilo.hilo_tool.commands.HiloToolCommand;

public final class HiloTool {
  private HiloTool() {}

  public static void main(final String[] args) throws Exception {
    final var command = new HiloToolCommand();
    command.run(args);
  }
}
