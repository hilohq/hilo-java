package com.hilohq.hilo.hilo_tool.cmd;

import com.hilohq.hilo.hilo_tool.cmd.exception.CmdException;
import com.hilohq.hilo.hilo_tool.cmd.exception.NotEnoughArgsForOptionException;
import com.hilohq.hilo.hilo_tool.cmd.exception.UnknownOptionException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import org.apache.commons.cli.Options;

public final class SplitArgs {
  private final String[] currentArgs;
  private final String command;
  private final String[] nextArgs;

  SplitArgs(final String[] currentArgs, final String command, final String[] nextArgs) {
    this.currentArgs = currentArgs;
    this.command = command;
    this.nextArgs = nextArgs;
  }

  public String[] currentArgs() {
    return currentArgs;
  }

  public String[] nextArgs() {
    return nextArgs;
  }

  public Optional<String> command() {
    return Optional.ofNullable(command);
  }

  private static boolean isOption(final String s) {
    return s.length() > 0 && s.charAt(0) == '-';
  }

  private static SplitArgs createSplit(final String[] args, final int index) {
    final var currentArgs = index == 0 ? new String[0] : Arrays.copyOfRange(args, 0, index);
    final var command = index >= args.length ? null : args[index];
    final var nextArgs =
        index >= args.length ? new String[0] : Arrays.copyOfRange(args, index + 1, args.length);

    return new SplitArgs(currentArgs, command, nextArgs);
  }

  public static SplitArgs split(
      final String[] args, final Options options, final Collection<String> stopCollection)
      throws CmdException {
    int index = 0;
    while (index < args.length) {
      if (!isOption(args[index])) {
        break;
      }

      final var name = args[index].replaceFirst("-", "");
      final var option = options.getOption(name);
      if (option == null) {
        throw new UnknownOptionException(name);
      }

      final var count = option.getArgs();
      ++index;
      switch (count) {
        case -1: // Options expects 0 arguments
          break;
        case -2:
          while (index < args.length
              && !isOption(args[index])
              && !stopCollection.contains(args[index])) {
            ++index;
          }
          break;
        default:
          var remainingArgs = count;
          while (index < args.length
              && !isOption(args[index])
              && !stopCollection.contains(args[index])
              && remainingArgs > 0) {
            ++index;
            --remainingArgs;
          }
          if (remainingArgs > 0) {
            throw new NotEnoughArgsForOptionException(name, count, count - remainingArgs);
          }
          break;
      }
    }

    return createSplit(args, index);
  }
}
