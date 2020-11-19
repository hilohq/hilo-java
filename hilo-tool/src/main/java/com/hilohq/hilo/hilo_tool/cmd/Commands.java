package com.hilohq.hilo.hilo_tool.cmd;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public final class Commands {
  private final Map<String, Command> commands;

  public static class Builder {
    private final Map<String, Command> commands;

    public Builder() {
      this.commands = new HashMap<>();
    }

    public Builder add(final Command command) {
      commands.put(command.name(), command);
      return this;
    }

    public Commands build() {
      return new Commands(commands);
    }
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  Commands(final Map<String, Command> commands) {
    this.commands = commands;
  }

  public Collection<String> commandNames() {
    return commands.keySet();
  }

  public Collection<Command> commands() {
    return commands.values();
  }

  public boolean hasCommand(final String name) {
    return commands.containsKey(name);
  }

  public Command get(final String name) {
    return commands.get(name);
  }

  public void add(final Command command) {
    commands.put(command.name(), command);
  }
}
