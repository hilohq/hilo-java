package com.hilohq.hilo.integration_grafana.client;

import static com.google.common.base.Verify.verifyNotNull;
import static com.hilohq.hilo.integration_grafana.client.GrafanaUtils.defaultLocalhostUrl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.hilohq.hilo.integration_grafana.client.exception.GrafanaResponseException;
import com.hilohq.hilo.integration_grafana.entity.PostDashboardRequest;
import com.hilohq.hilo.integration_grafana.entity.PostDashboardResponse;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;

public final class GrafanaClient {
  private final HttpClient httpClient;
  private final String url;
  private final String bearerToken;
  private final ObjectMapper mapper;

  public static class Builder {
    private String url = defaultLocalhostUrl();
    private String bearerToken;

    public Builder url(final String url) {
      verifyNotNull(url);
      this.url = url;
      return this;
    }

    public Builder bearerToken(final String bearerToken) {
      verifyNotNull(bearerToken);
      this.bearerToken = bearerToken;
      return this;
    }

    public GrafanaClient build() {
      verifyNotNull(url);
      verifyNotNull(bearerToken);

      if (!url.endsWith("/")) {
        url = url + "/";
      }

      return new GrafanaClient(url, bearerToken);
    }
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  private GrafanaClient(final String url, final String bearerToken) {
    this.httpClient = HttpClient.newBuilder().build();
    this.url = url;
    this.bearerToken = bearerToken;
    this.mapper = new ObjectMapper();
    mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
  }

  private byte[] writeAsBytes(final Object o) {
    try {
      return mapper.writeValueAsBytes(o);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  private <T> T readFromBytes(final byte[] bytes, final Class<T> c) {
    try {
      return mapper.readValue(bytes, c);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  public CompletableFuture<PostDashboardResponse> postDashboard(
      final PostDashboardRequest request) {
    final var body = writeAsBytes(request);
    final var uri = URI.create(url + "api/dashboards/db");
    final var httpRequest =
        HttpRequest.newBuilder(uri)
            .POST(HttpRequest.BodyPublishers.ofByteArray(body))
            .header("Content-type", "application/json")
            .header("Accept", "application/json")
            .header("Authorization", "Bearer " + bearerToken)
            .build();
    final var result = new CompletableFuture<PostDashboardResponse>();

    httpClient
        .sendAsync(httpRequest, HttpResponse.BodyHandlers.ofByteArray())
        .thenAccept(
            response -> {
              if (response.statusCode() == 200) {
                result.complete(readFromBytes(response.body(), PostDashboardResponse.class));
              } else {
                result.completeExceptionally(
                    new GrafanaResponseException(
                        response.statusCode(),
                        new String(response.body(), StandardCharsets.UTF_8)));
              }
            })
        .exceptionally(
            t -> {
              result.completeExceptionally(t);
              return null;
            });

    return result;
  }

  public void close() {}
}
