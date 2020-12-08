package com.hilohq.hilo.integration_grafana.client.exception;

public final class GrafanaResponseException extends GrafanaException {
  private final int statusCode;

  public GrafanaResponseException(int statusCode, String msg) {
    super("error response received with status code " + statusCode + " and message " + msg);
    this.statusCode = statusCode;
  }

  public int statusCode() {
    return statusCode;
  }
}
