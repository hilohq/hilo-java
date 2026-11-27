package com.hilohq.hilo.integration_jdbc.utils;

import static org.junit.Assert.assertEquals;

import com.hilohq.hilo.protobufs.integrations.jdbc.Mysql;
import org.junit.Test;

public final class CreateTableQueryUtilsTest {

  @Test
  public void testCreatesOneQuotedColumnPerMetric() {
    final var metrics =
        Mysql.MysqlMetrics.newBuilder()
            .putMetrics(
                "gas",
                Mysql.MysqlMetric.newBuilder()
                    .setName("gas")
                    .setType(Mysql.MysqlType.DOUBLE)
                    .build())
            .build();

    assertEquals(
        "CREATE TABLE IF NOT EXISTS `schema-1` (`gas` DOUBLE);",
        CreateTableQueryUtils.createMysqlTableQuery("schema-1", metrics));
  }

  @Test
  public void testSeparatesColumnsWithCommas() {
    final var metrics =
        Mysql.MysqlMetrics.newBuilder()
            .putMetrics(
                "a",
                Mysql.MysqlMetric.newBuilder().setName("a").setType(Mysql.MysqlType.DOUBLE).build())
            .putMetrics(
                "b",
                Mysql.MysqlMetric.newBuilder().setName("b").setType(Mysql.MysqlType.DOUBLE).build())
            .build();

    final var query = CreateTableQueryUtils.createMysqlTableQuery("t", metrics);

    // protobuf map iteration order is not part of the contract, so accept either order
    final var ab = "CREATE TABLE IF NOT EXISTS `t` (`a` DOUBLE,`b` DOUBLE);";
    final var ba = "CREATE TABLE IF NOT EXISTS `t` (`b` DOUBLE,`a` DOUBLE);";
    assertEquals(true, query.equals(ab) || query.equals(ba));
  }

  @Test
  public void testEmptyMetricsProduceAnEmptyColumnList() {
    assertEquals(
        "CREATE TABLE IF NOT EXISTS `t` ();",
        CreateTableQueryUtils.createMysqlTableQuery("t", Mysql.MysqlMetrics.getDefaultInstance()));
  }
}
