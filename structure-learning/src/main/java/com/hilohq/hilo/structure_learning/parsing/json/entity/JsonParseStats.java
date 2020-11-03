package com.hilohq.hilo.structure_learning.parsing.json.entity;

import java.io.Serializable;

public final class JsonParseStats implements Serializable {
  private long parseAttemptCount;
  private long parseErrorCount;
  private long parseSuccessCount;

  public JsonParseStats() {
    this.parseAttemptCount = 0;
    this.parseErrorCount = 0;
    this.parseSuccessCount = 0;
  }

  public void incrParseAttemptCount() {
    ++parseAttemptCount;
  }

  public void incrParseErrorCount() {
    ++parseErrorCount;
  }

  public void incrParseSuccessCount() {
    ++parseSuccessCount;
  }

  public long parseAttemptCount() {
    return parseAttemptCount;
  }

  public long parseErrorCount() {
    return parseErrorCount;
  }

  public long parseSuccessCount() {
    return parseSuccessCount;
  }
}
