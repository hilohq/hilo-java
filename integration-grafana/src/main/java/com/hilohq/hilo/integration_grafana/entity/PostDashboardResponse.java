package com.hilohq.hilo.integration_grafana.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public final class PostDashboardResponse {
  private final long id;
  private final String slug;
  private final String status;
  private final String uid;
  private final String url;
  private final long version;

  @JsonCreator
  public PostDashboardResponse(
      @JsonProperty("id") final long id,
      @JsonProperty("slug") final String slug,
      @JsonProperty("status") final String status,
      @JsonProperty("uid") final String uid,
      @JsonProperty("url") final String url,
      @JsonProperty("version") final long version) {
    this.id = id;
    this.slug = slug;
    this.status = status;
    this.uid = uid;
    this.url = url;
    this.version = version;
  }

  @JsonProperty("id")
  public long id() {
    return id;
  }

  @JsonProperty("slug")
  public String slug() {
    return slug;
  }

  @JsonProperty("status")
  public String status() {
    return status;
  }

  @JsonProperty("uid")
  public String uid() {
    return uid;
  }

  @JsonProperty("url")
  public String url() {
    return url;
  }

  @JsonProperty("version")
  public long version() {
    return version;
  }
}
