package com.hilohq.hilo.features.stats.feature;

import com.hilohq.hilo.protobufs.structure.FeatureOuterClass;

public final class FeatureProtobufObjectMapper {
  private FeatureProtobufObjectMapper() {}

  public static FeatureOuterClass.FeatureLog createFeatureCreateLog(
      final String key, final FeatureOuterClass.FeatureClass featureClass) {
    return FeatureOuterClass.FeatureLog.newBuilder()
        .setFeatureCreate(
            FeatureOuterClass.FeatureCreate.newBuilder().setKey(key).setFeatureClass(featureClass))
        .build();
  }

  public static FeatureOuterClass.FeaturesUpdate createFeatureCreateUpdate(
      final FeatureOuterClass.FeaturesMetadata metadata,
      final String key,
      final FeatureOuterClass.FeatureClass featureClass) {
    return createFeatureUpdate(metadata, createFeatureCreateLog(key, featureClass));
  }

  public static FeatureOuterClass.FeatureLog createFeatureUpdateLog(
      final String key,
      final FeatureOuterClass.FeatureClass beforeFeatureClass,
      final FeatureOuterClass.FeatureClass afterFeatureClass) {

    return FeatureOuterClass.FeatureLog.newBuilder()
        .setFeatureUpdate(
            FeatureOuterClass.FeatureUpdate.newBuilder()
                .setBefore(
                    FeatureOuterClass.FeatureUpdate.Feature.newBuilder()
                        .setKey(key)
                        .setFeatureClass(beforeFeatureClass))
                .setAfter(
                    FeatureOuterClass.FeatureUpdate.Feature.newBuilder()
                        .setKey(key)
                        .setFeatureClass(afterFeatureClass)))
        .build();
  }

  public static FeatureOuterClass.FeaturesUpdate createFeatureUpdateUpdate(
      final FeatureOuterClass.FeaturesMetadata metadata,
      final String key,
      final FeatureOuterClass.FeatureClass beforeFeatureClass,
      final FeatureOuterClass.FeatureClass afterFeatureClass) {
    return createFeatureUpdate(
        metadata, createFeatureUpdateLog(key, beforeFeatureClass, afterFeatureClass));
  }

  public static FeatureOuterClass.FeaturesUpdate createFeatureUpdate(
      final FeatureOuterClass.FeaturesMetadata metadata, final FeatureOuterClass.FeatureLog log) {
    return FeatureOuterClass.FeaturesUpdate.newBuilder().setMetadata(metadata).setLog(log).build();
  }
}
