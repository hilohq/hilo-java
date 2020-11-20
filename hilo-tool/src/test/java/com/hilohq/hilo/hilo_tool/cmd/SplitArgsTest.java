package com.hilohq.hilo.hilo_tool.cmd;

import static org.junit.Assert.*;

import java.util.Set;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.junit.Test;

public final class SplitArgsTest {

  @Test
  public void testSplitJustACommand() throws Exception {
    final var args = new String[] {"command"};
    final var option = Option.builder().argName("option").hasArg(true).longOpt("option").build();
    final var options = new Options();
    options.addOption(option);

    final var split = SplitArgs.split(args, options, Set.of("command"));

    assertArrayEquals(new String[] {}, split.currentArgs());
    assertTrue(split.command().isPresent());
    assertEquals("command", split.command().get());
    assertArrayEquals(new String[] {}, split.nextArgs());
  }

  @Test
  public void testSplitOneOptionAndCommand() throws Exception {
    final var args = new String[] {"-option", "value", "command"};
    final var option = Option.builder().argName("option").hasArg(true).longOpt("option").build();
    final var options = new Options();
    options.addOption(option);

    final var split = SplitArgs.split(args, options, Set.of("command"));

    assertArrayEquals(new String[] {"-option", "value"}, split.currentArgs());
    assertTrue(split.command().isPresent());
    assertEquals("command", split.command().get());
    assertArrayEquals(new String[] {}, split.nextArgs());
  }

  @Test
  public void testSplitEmptyArgs() throws Exception {
    final var args = new String[] {};
    final var option = Option.builder().argName("option").hasArg(true).longOpt("option").build();
    final var options = new Options();
    options.addOption(option);

    final var split = SplitArgs.split(args, options, Set.of("command"));

    assertArrayEquals(new String[] {}, split.currentArgs());
    assertFalse(split.command().isPresent());
    assertArrayEquals(new String[] {}, split.nextArgs());
  }

  @Test
  public void testSplitOneOptionWithMultipleValues() throws Exception {
    final var args = new String[] {"-option", "1", "2", "3", "command"};
    final var option = Option.builder().argName("option").hasArgs().longOpt("option").build();
    final var options = new Options();
    options.addOption(option);

    final var split = SplitArgs.split(args, options, Set.of("command"));

    assertArrayEquals(new String[] {"-option", "1", "2", "3"}, split.currentArgs());
    assertTrue(split.command().isPresent());
    assertEquals("command", split.command().get());
    assertArrayEquals(new String[] {}, split.nextArgs());
  }

  @Test
  public void testSplitMultipleOptionsWithCommandWithOptions() throws Exception {
    final var args = new String[] {"-a", "1", "-b", "2", "-c", "command", "-d", "3"};
    final var options = new Options();
    options.addOption(Option.builder().argName("a").hasArg(true).longOpt("a").build());
    options.addOption(Option.builder().argName("b").hasArg(true).longOpt("b").build());
    options.addOption(Option.builder().argName("c").hasArg(false).longOpt("c").build());

    final var split = SplitArgs.split(args, options, Set.of("command"));

    assertArrayEquals(new String[] {"-a", "1", "-b", "2", "-c"}, split.currentArgs());
    assertTrue(split.command().isPresent());
    assertEquals("command", split.command().get());
    assertArrayEquals(new String[] {"-d", "3"}, split.nextArgs());
  }
}
