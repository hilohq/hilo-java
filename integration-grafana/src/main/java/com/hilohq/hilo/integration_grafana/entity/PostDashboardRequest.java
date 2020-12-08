package com.hilohq.hilo.integration_grafana.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties
public final class PostDashboardRequest {
  private final long folderId;
  private final boolean overwrite;
  private final GrafanaDashboard dashboard;

  public static final class Builder {
    private long folderId;
    private boolean overwrite;
    private GrafanaDashboard dashboard;

    public Builder folderId(final long folderId) {
      this.folderId = folderId;
      return this;
    }

    public Builder overwrite(final boolean overwrite) {
      this.overwrite = overwrite;
      return this;
    }

    public Builder dashboard(final GrafanaDashboard dashboard) {
      this.dashboard = dashboard;
      return this;
    }

    public PostDashboardRequest build() {
      return new PostDashboardRequest(folderId, overwrite, dashboard);
    }
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  @JsonCreator
  public PostDashboardRequest(
      @JsonProperty("folderId") final long folderId,
      @JsonProperty("overwrite") final boolean overwrite,
      @JsonProperty("dashboard") final GrafanaDashboard dashboard) {
    this.folderId = folderId;
    this.overwrite = overwrite;
    this.dashboard = dashboard;
  }

  @JsonProperty("folderId")
  public long folderId() {
    return folderId;
  }

  @JsonProperty("overwrite")
  public boolean overwrite() {
    return overwrite;
  }

  @JsonProperty("dashboard")
  public GrafanaDashboard dashboard() {
    return dashboard;
  }
}
