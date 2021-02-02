package com.hilohq.hilo.integration_jdbc.utils;

import com.hilohq.hilo.protobufs.integrations.jdbc.Mysql;

public final class CreateTableQueryUtils {
  private CreateTableQueryUtils() {}

  public static String getMysqlType(final Mysql.MysqlType type) {
    return type.name();
  }

  public static String createMysqlTableQuery(final String table, final Mysql.MysqlMetrics metrics) {
    final var builder = new StringBuilder();
    for (final var metric : metrics.getMetricsMap().values()) {
      builder
          .append("`")
          .append(metric.getName())
          .append("` ")
          .append(getMysqlType(metric.getType()))
          .append(",");
    }

    if (builder.length() > 0 && builder.lastIndexOf(",") > -1) {
      builder.deleteCharAt(builder.lastIndexOf(","));
    }

    final var query = "CREATE TABLE IF NOT EXISTS `" + table + "` (" + builder.toString() + ");";
    System.out.println("QUERY " + query);
    return query;
  }
}
