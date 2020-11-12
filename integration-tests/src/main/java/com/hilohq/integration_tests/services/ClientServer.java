package com.hilohq.integration_tests.services;

public final class ClientServer<C, S> {
  private final C client;
  private final S server;

  public ClientServer(final C client, final S server) {
    this.client = client;
    this.server = server;
  }

  public C client() {
    return client;
  }

  public S server() {
    return server;
  }
}
