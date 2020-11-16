package com.hilohq.hilo.features.stats.numbers;

import java.time.DateTimeException;
import java.time.Duration;
import java.time.Instant;
import java.util.List;

public final class IntegerUtils {
  private IntegerUtils() {}

  public static boolean isValidMillisTimestamp(final long value) {
    try {
      Instant.ofEpochMilli(value);
      return true;
    } catch (DateTimeException ignored) {
    }

    return false;
  }

  public static boolean isValidSecondsTimestamp(final long value) {
    try {
      Instant.ofEpochSecond(value);
      return true;
    } catch (DateTimeException ignored) {
    }

    return false;
  }

  public static double isTimestampWithProbability(final List<Long> values) {
    if (values.size() == 0) {
      return 0;
    }

    final var now = Instant.now().getEpochSecond();
    final var years20 = Duration.ofDays(365 * 20);
    double probability = 1;

    for (final var value : values) {
      if (isValidSecondsTimestamp(value)) {
        if (years20.compareTo(Duration.ofSeconds(Math.abs(value - now))) < 0) {
          probability = probability * ((double) (values.size() - 1) / (2 * (double) values.size()));
        }

      } else if (isValidMillisTimestamp(value)) {
        if (years20.compareTo(Duration.ofMillis(Math.abs(value - now * 1000))) < 0) {
          probability = probability * ((double) (values.size() - 1) / (2 * (double) values.size()));
        }

      } else {
        probability = probability * ((double) (values.size() - 1) / (2 * (double) values.size()));
      }
    }

    return probability;
  }
}
