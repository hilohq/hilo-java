package com.hilohq.hilo.hilo_tool.cmd;

import org.apache.commons.cli.Options;

public class ActionCommand implements Command {

  private final String name;
  private final String help;
  private final Options options;

  public ActionCommand(final String name, final String help, final Options options) {
    this.name = name;
    this.help = help;
    this.options = options;
  }

  public String name() {
    return name;
  }

  public String help() {
    return help;
  }

  public Options options() {
    return options;
  }

  @Override
  public void run(final String[] args) throws Exception {}
}
