package com.hilohq.hilo.parsers.logparse.json;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public final class JsonParserTest {

  private final JsonParser parser = new JsonParser();

  private static <T> void testSingleValue(
      final JsonParser parser, final String log, final T value) {
    final var fields = parser.parse(log);

    assertEquals(1, fields.size());
    final var entry = fields.entrySet().iterator().next();
    assertEquals("", entry.getKey().key());
    assertEquals(value, entry.getValue().value());
  }

  @Test
  public void testParseSingleString() {
    testSingleValue(parser, "\"string\"", "string");
  }

  @Test
  public void testParseSingleInt() {
    testSingleValue(parser, "1", 1);
  }

  @Test
  public void testParseSingleLong() {
    testSingleValue(parser, "1234567890987654321", 1234567890987654321L);
  }

  @Test
  public void testParseObject() {
    final var fields =
        parser.parse(
            "{\"difficulty\":\"12549329517745\",\"extraData\":\"0xd783010303844765746887676f312e352e31856c696e7578\",\"gasLimit\":3141592,\"gasUsed\":63000,\"hash\":\"0x95c3a05973fec7bf98f1131a72e607b4eba171d0576571cf83ee7162bbcdb7d9\",\"logsBloom\":\"0x00000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000\",\"miner\":\"0x95581Ea0c5B362933F3523138F54d51eaE817211\",\"mixHash\":\"0xa01f9d00ac510a726f883459834e30cfe085f47b04e22f72207f5a9e9d652ca6\",\"nonce\":\"0x1c080c4ec6f2553b\",\"number\":1000002,\"parentHash\":\"0xcb5cab7266694daa0d28cbf40496c08dd30bf732c41e0455e7ad389c10d79f4f\",\"receiptsRoot\":\"0x97097902b6b4d6b695ef16b923e33b8780d95cf4bd54540ac450deb019d07647\",\"sha3Uncles\":\"0x1dcc4de8dec75d7aab85b567b6ccd41ad312451b948a7413f0a142fd40d49347\",\"size\":883,\"stateRoot\":\"0x643430d1afc3f02ce5249e4ba5979fb8601b1907a5923a4a74d36d66321a27e5\",\"timestamp\":1455404078,\"totalDifficulty\":\"7135227569124568698\",\"transactions\":[\"0x2bd653c639c086ad1c6e468d2cbdba76fc144f747373be6d2db7854d0d0e36e7\",\"0x7e3abff232dd5fc72db441807bcd59d9f17311a960a8f6ff6d0e693c22fa1eb2\",\"0x07a911c87c6e656e2f83c30e0cbdac47789fd874c747337ad1776b7dd5238893\"],\"transactionsRoot\":\"0xdbdf7457111e50e435853974d5412c2151fde6e3c2e3f5aecc253aa4cb21fce2\",\"uncles\":[]}");

    assertEquals(20, fields.size());
    assertEquals("12549329517745", fields.get(new JsonFieldKey("difficulty")).stringValue());
    assertEquals(
        "0xd783010303844765746887676f312e352e31856c696e7578",
        fields.get(new JsonFieldKey("extraData")).stringValue());
    assertEquals(3141592, fields.get(new JsonFieldKey("gasLimit")).intValue());
    assertEquals(63000, fields.get(new JsonFieldKey("gasUsed")).intValue());
    assertEquals(
        "0x95c3a05973fec7bf98f1131a72e607b4eba171d0576571cf83ee7162bbcdb7d9",
        fields.get(new JsonFieldKey("hash")).stringValue());
    assertEquals(
        "0x00000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000",
        fields.get(new JsonFieldKey("logsBloom")).stringValue());
    assertEquals(
        "0x95581Ea0c5B362933F3523138F54d51eaE817211",
        fields.get(new JsonFieldKey("miner")).stringValue());
    assertEquals(
        "0xa01f9d00ac510a726f883459834e30cfe085f47b04e22f72207f5a9e9d652ca6",
        fields.get(new JsonFieldKey("mixHash")).stringValue());
    assertEquals("0x1c080c4ec6f2553b", fields.get(new JsonFieldKey("nonce")).stringValue());
    assertEquals(1000002, fields.get(new JsonFieldKey("number")).intValue());
    assertEquals(
        "0xcb5cab7266694daa0d28cbf40496c08dd30bf732c41e0455e7ad389c10d79f4f",
        fields.get(new JsonFieldKey("parentHash")).stringValue());
    assertEquals(
        "0x97097902b6b4d6b695ef16b923e33b8780d95cf4bd54540ac450deb019d07647",
        fields.get(new JsonFieldKey("receiptsRoot")).stringValue());
    assertEquals(
        "0x1dcc4de8dec75d7aab85b567b6ccd41ad312451b948a7413f0a142fd40d49347",
        fields.get(new JsonFieldKey("sha3Uncles")).stringValue());
    assertEquals(883, fields.get(new JsonFieldKey("size")).intValue());
    assertEquals(
        "0x643430d1afc3f02ce5249e4ba5979fb8601b1907a5923a4a74d36d66321a27e5",
        fields.get(new JsonFieldKey("stateRoot")).stringValue());
    assertEquals(1455404078, fields.get(new JsonFieldKey("timestamp")).intValue());
    assertEquals(
        "7135227569124568698", fields.get(new JsonFieldKey("totalDifficulty")).stringValue());
    assertEquals(
        "[\"0x2bd653c639c086ad1c6e468d2cbdba76fc144f747373be6d2db7854d0d0e36e7\","
            + "\"0x7e3abff232dd5fc72db441807bcd59d9f17311a960a8f6ff6d0e693c22fa1eb2\","
            + "\"0x07a911c87c6e656e2f83c30e0cbdac47789fd874c747337ad1776b7dd5238893\"]",
        fields.get(new JsonFieldKey("transactions")).stringValue());
    assertEquals(
        "0xdbdf7457111e50e435853974d5412c2151fde6e3c2e3f5aecc253aa4cb21fce2",
        fields.get(new JsonFieldKey("transactionsRoot")).stringValue());
    assertEquals("[]", fields.get(new JsonFieldKey("uncles")).stringValue());
  }
}
