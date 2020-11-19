package com.hilohq.hilo.hilo_tool.commands;

import com.hilohq.hilo.hilo_tool.cmd.Commands;
import com.hilohq.hilo.hilo_tool.cmd.ProxyCommand;
import org.apache.commons.cli.Options;

public final class HiloToolCommand extends ProxyCommand {
  private static Commands createCommands() {
    return Commands.newBuilder().add(new PipelineCommand()).build();
  }

  public HiloToolCommand() {
    super(
        "hilo_tool", "command line utility to interact with HiLO", createCommands(), new Options());
  }
}
