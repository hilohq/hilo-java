package com.hilohq.hilo.hilo_tool.cmd;

import static com.hilohq.hilo.hilo_tool.cmd.PrintUtils.printError;
import static com.hilohq.hilo.hilo_tool.cmd.PrintUtils.printHelpAndExit;

import org.apache.commons.cli.Options;

public class ProxyCommand implements Command {
  private final String name;
  private final String help;
  private final Commands commands;
  private final Options options;

  public ProxyCommand(
      final String name, final String help, final Commands commands, final Options options) {
    this.name = name;
    this.help = help;
    this.commands = commands;
    this.options = options;
  }

  protected void apply(final Options options) {}

  @Override
  public void run(final String[] args) throws Exception {
    final var splits = SplitArgs.split(args, options, commands.commandNames());

    if (splits.command().isPresent()) {
      if (commands.hasCommand(splits.command().get())) {
        apply(options);
        commands.get(splits.command().get()).run(splits.nextArgs());

      } else {
        printError(name, "unknown command " + splits.command().get());
      }
    } else {
      printHelpAndExit(name, help, options, commands);
    }
  }

  @Override
  public String name() {
    return name;
  }

  @Override
  public String help() {
    return help;
  }
}
