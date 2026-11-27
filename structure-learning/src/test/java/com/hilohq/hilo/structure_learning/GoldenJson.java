package com.hilohq.hilo.structure_learning;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Writes golden JSON files in the layout the checked-in samples already use (2-space indent,
 * JavaScript-style numbers, key order of the previous file), so that regenerating a golden file
 * only shows the values that actually changed.
 */
final class GoldenJson {
  private static final ObjectMapper MAPPER = new ObjectMapper();

  private GoldenJson() {}

  /**
   * Formats {@code json} (typically protobuf JsonFormat output). Object keys that also appear in
   * the corresponding object of {@code previous} keep that order; new keys follow.
   */
  static String format(final String json, final String previous) {
    try {
      final var node = MAPPER.readTree(json);
      final var reference = previous == null ? null : MAPPER.readTree(previous);
      final var out = new StringBuilder();
      write(node, reference, 0, out);
      return out.toString();
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  private static void write(
      final JsonNode node, final JsonNode reference, final int depth, final StringBuilder out)
      throws IOException {
    if (node.isObject()) {
      final var fields = orderedFields((ObjectNode) node, reference);
      if (fields.isEmpty()) {
        out.append("{}");
        return;
      }
      out.append("{\n");
      var i = 0;
      for (final var entry : fields.entrySet()) {
        indent(depth + 1, out);
        out.append(MAPPER.writeValueAsString(entry.getKey())).append(": ");
        final var childReference = reference == null ? null : reference.get(entry.getKey());
        write(entry.getValue(), childReference, depth + 1, out);
        out.append(++i < fields.size() ? ",\n" : "\n");
      }
      indent(depth, out);
      out.append('}');
    } else if (node.isArray()) {
      if (node.size() == 0) {
        out.append("[]");
        return;
      }
      out.append("[\n");
      for (int i = 0; i < node.size(); i++) {
        indent(depth + 1, out);
        final var childReference =
            reference != null && reference.isArray() && i < reference.size()
                ? reference.get(i)
                : null;
        write(node.get(i), childReference, depth + 1, out);
        out.append(i + 1 < node.size() ? ",\n" : "\n");
      }
      indent(depth, out);
      out.append(']');
    } else if (node.isNumber() && !node.isIntegralNumber()) {
      out.append(javascriptNumber(node.doubleValue()));
    } else {
      out.append(MAPPER.writeValueAsString(node));
    }
  }

  private static LinkedHashMap<String, JsonNode> orderedFields(
      final ObjectNode node, final JsonNode reference) {
    final var result = new LinkedHashMap<String, JsonNode>();
    if (reference != null && reference.isObject()) {
      for (final Iterator<String> it = reference.fieldNames(); it.hasNext(); ) {
        final var name = it.next();
        if (node.has(name)) {
          result.put(name, node.get(name));
        }
      }
    }
    final List<String> rest = new ArrayList<>();
    node.fieldNames().forEachRemaining(rest::add);
    for (final var name : rest) {
      result.putIfAbsent(name, node.get(name));
    }
    return result;
  }

  /** Formats a double the way JavaScript's Number.prototype.toString does. */
  static String javascriptNumber(final double value) {
    if (value == 0) {
      return "0";
    }
    final var decimal = new BigDecimal(Double.toString(value)).stripTrailingZeros();
    final var digits = decimal.unscaledValue().abs().toString();
    final var k = digits.length();
    // value = 0.digits * 10^n
    final var n = k - decimal.scale();
    final var sign = value < 0 ? "-" : "";

    if (k <= n && n <= 21) {
      return sign + digits + "0".repeat(n - k);
    } else if (0 < n && n <= 21) {
      return sign + digits.substring(0, n) + "." + digits.substring(n);
    } else if (-6 < n && n <= 0) {
      return sign + "0." + "0".repeat(-n) + digits;
    }
    final var exponent = n - 1;
    final var mantissa = k == 1 ? digits : digits.charAt(0) + "." + digits.substring(1);
    return sign + mantissa + "e" + (exponent >= 0 ? "+" : "-") + Math.abs(exponent);
  }

  private static void indent(final int depth, final StringBuilder out) {
    out.append("  ".repeat(depth));
  }
}
