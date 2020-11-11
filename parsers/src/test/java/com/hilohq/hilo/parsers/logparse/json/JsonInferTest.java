package com.hilohq.hilo.parsers.logparse.json;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public final class JsonInferTest {

  private final JsonInfer infer = new JsonInfer();

  @Test
  public void testInferObject() {
    final var fields =
        infer.infer(
            "{\"difficulty\":\"12549329517745\",\"extraData\":\"0xd783010303844765746887676f312e352e31856c696e7578\",\"gasLimit\":3141592,\"gasUsed\":63000,\"hash\":\"0x95c3a05973fec7bf98f1131a72e607b4eba171d0576571cf83ee7162bbcdb7d9\",\"logsBloom\":\"0x00000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000\",\"miner\":\"0x95581Ea0c5B362933F3523138F54d51eaE817211\",\"mixHash\":\"0xa01f9d00ac510a726f883459834e30cfe085f47b04e22f72207f5a9e9d652ca6\",\"nonce\":\"0x1c080c4ec6f2553b\",\"number\":1000002,\"parentHash\":\"0xcb5cab7266694daa0d28cbf40496c08dd30bf732c41e0455e7ad389c10d79f4f\",\"receiptsRoot\":\"0x97097902b6b4d6b695ef16b923e33b8780d95cf4bd54540ac450deb019d07647\",\"sha3Uncles\":\"0x1dcc4de8dec75d7aab85b567b6ccd41ad312451b948a7413f0a142fd40d49347\",\"size\":883,\"stateRoot\":\"0x643430d1afc3f02ce5249e4ba5979fb8601b1907a5923a4a74d36d66321a27e5\",\"timestamp\":1455404078,\"totalDifficulty\":\"7135227569124568698\",\"transactions\":[\"0x2bd653c639c086ad1c6e468d2cbdba76fc144f747373be6d2db7854d0d0e36e7\",\"0x7e3abff232dd5fc72db441807bcd59d9f17311a960a8f6ff6d0e693c22fa1eb2\",\"0x07a911c87c6e656e2f83c30e0cbdac47789fd874c747337ad1776b7dd5238893\"],\"transactionsRoot\":\"0xdbdf7457111e50e435853974d5412c2151fde6e3c2e3f5aecc253aa4cb21fce2\",\"uncles\":[]}");

    assertEquals(20, fields.size());
    assertEquals(JsonType.STRING, fields.typeInfo().get(new JsonFieldKey("difficulty")));
    assertEquals(JsonType.STRING, fields.get(new JsonFieldKey("extraData")));
    assertEquals(JsonType.NUMBER, fields.get(new JsonFieldKey("gasLimit")));
    assertEquals(JsonType.NUMBER, fields.get(new JsonFieldKey("gasUsed")));
    assertEquals(JsonType.STRING, fields.get(new JsonFieldKey("hash")));
    assertEquals(JsonType.STRING, fields.get(new JsonFieldKey("logsBloom")));
    assertEquals(JsonType.STRING, fields.get(new JsonFieldKey("miner")));
    assertEquals(JsonType.STRING, fields.get(new JsonFieldKey("mixHash")));
    assertEquals(JsonType.STRING, fields.get(new JsonFieldKey("nonce")));
    assertEquals(JsonType.NUMBER, fields.get(new JsonFieldKey("number")));
    assertEquals(JsonType.STRING, fields.get(new JsonFieldKey("parentHash")));
    assertEquals(JsonType.STRING, fields.get(new JsonFieldKey("receiptsRoot")));
    assertEquals(JsonType.STRING, fields.get(new JsonFieldKey("sha3Uncles")));
    assertEquals(JsonType.NUMBER, fields.get(new JsonFieldKey("size")));
    assertEquals(JsonType.STRING, fields.get(new JsonFieldKey("stateRoot")));
    assertEquals(JsonType.NUMBER, fields.get(new JsonFieldKey("timestamp")));
    assertEquals(JsonType.STRING, fields.get(new JsonFieldKey("totalDifficulty")));
    assertEquals(JsonType.ARRAY, fields.get(new JsonFieldKey("transactions")));
    assertEquals(JsonType.STRING, fields.get(new JsonFieldKey("transactionsRoot")));
    assertEquals(JsonType.ARRAY, fields.get(new JsonFieldKey("uncles")));
  }
}
