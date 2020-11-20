package com.hilohq.hilo.hilo_tool.commands;

import static com.hilohq.hilo.hilo_tool.cmd.PrintUtils.printErrorAndExit;

import com.hilohq.hilo.hilo_tool.cmd.ActionCommand;
import java.io.File;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

public class ExecutePipelineCommand extends ActionCommand {

  private static final String OPTION_INPUT = "input";

  private static Options createOptions() {
    final var options = new Options();
    options.addOption(
        Option.builder()
            .longOpt(OPTION_INPUT)
            .argName(OPTION_INPUT)
            .hasArg(true)
            .required()
            .build());
    return options;
  }

  private static boolean verifyInput(final String input) {
    final var file = new File(input);
    return !file.exists() || !file.canRead();
  }

  public ExecutePipelineCommand() {
    super("execute", "execute a HiLO pipeline", createOptions());
  }

  @Override
  public void run(final String[] args) throws Exception {
    final var parser = new DefaultParser();
    final var cli = parser.parse(options(), args);
    final var input = cli.getOptionValue(OPTION_INPUT);
    final var ok = verifyInput(input);
    if (!ok) {
      printErrorAndExit(name(), "input `" + input + "` cannot be read");
    }
  }
}
