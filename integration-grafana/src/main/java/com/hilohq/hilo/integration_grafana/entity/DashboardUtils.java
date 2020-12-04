package com.hilohq.hilo.integration_grafana.entity;

import java.util.Arrays;
import java.util.Collections;

public final class DashboardUtils {
    private DashboardUtils() { }

    public static Dashboard.Builder withDefaults() {
    return Dashboard.newBuilder()
        .annotations(
            DashboardAnnotations.newBuilder()
                .list(
                    Collections.singletonList(
                        DashboardAnnotation.newBuilder()
                            .builtIn(1)
                            .datasource("-- Grafana --")
                            .enable(true)
                            .hide(true)
                            .iconColor("rgba(0, 211, 255, 1)")
                            .name("Annotations & Alerts")
                            .type("dashboard")
                            .build()))
                .build())
        .editable(false)
        .style("dark")
        .time(DashboardTime.newBuilder().from("now-30m").to("now").build())
        .timePicker(
            DashboardTimePicker.newBuilder()
                .refreshIntervals(
                    Arrays.asList("5s", "10s", "30s", "1m", "5m", "15m", "30m", "1h", "2h", "1d"))
                .build())
        .timezone("");
    }
}
