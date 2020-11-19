package com.hilohq.hilo.hilo_tool.commands;

import com.hilohq.hilo.hilo_tool.cmd.Commands;
import com.hilohq.hilo.hilo_tool.cmd.ProxyCommand;
import org.apache.commons.cli.Options;

public class PipelineCommand extends ProxyCommand {
  private static Commands createCommands() {
    return Commands.newBuilder().add(new ExecutePipelineCommand()).build();
  }

  public PipelineCommand() {
    super("pipeline", "manage pipelines on HiLO", createCommands(), new Options());
  }
}
