package com.hilohq.hilo.connectors.mongodb.exceptions;

import java.io.IOException;

public class MongoException extends IOException {
  public MongoException(final String msg) {
    super(msg);
  }
}
