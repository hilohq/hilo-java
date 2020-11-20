package com.hilohq.hilo.hilo_tool.cmd;

import org.apache.commons.cli.Options;

public final class PrintUtils {

  private PrintUtils() {}

  public static void printError(final String prog, final String msg) {
    System.err.println(prog + ": " + msg);
    System.err.println(prog + ": see " + prog + " -help");
  }

  public static void printErrorAndExit(final String prog, final String msg) {
    printError(prog, msg);
    System.exit(1);
  }

  public static void printHelp(
      final String prog, final String help, final Options options, final Commands commands) {
    System.out.println();
    System.out.println("Usage: " + prog + " [OPTIONS] COMMAND");
    if (help != null) {
      System.out.println();
      System.out.println(help);
    }

    if (options != null && options.getOptions().size() > 0) {
      System.out.println();
      System.out.println("Options");
      for (final var option : options.getOptions()) {
        System.out.print("  -" + option.getLongOpt());
        if (option.getDescription() == null) {
          System.out.println();
        } else {
          System.out.println("\t" + option.getDescription());
        }
      }
    }

    if (commands != null && commands.commands().size() > 0) {
      System.out.println();
      System.out.println("Commands");
      for (final var command : commands.commands()) {
        System.out.println("  " + command.name() + "\t" + command.help());
      }
    }

    System.out.println();
  }

  public static void printHelpAndExit(
      final String prog, final String help, final Options options, final Commands commands) {
    printHelp(prog, help, options, commands);
    System.exit(0);
  }
}
