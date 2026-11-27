package com.hilohq.hilo.structure_learning.features;

import com.hilohq.hilo.features_service.service.MongoFeaturesStore;
import com.hilohq.hilo.protobufs.structure.feature.FeatureOuterClass;
import java.util.Map;

/** Loads the stored features of a schema from the Mongo features store. */
public final class MongoFeaturesLoader implements FeaturesLoader {
  private static final long TIMEOUT_SECONDS = 5;

  private final String mongoUrl;
  private final String database;

  public MongoFeaturesLoader(final String mongoUrl, final String database) {
    this.mongoUrl = mongoUrl;
    this.database = database;
  }

  @Override
  public Map<String, FeatureOuterClass.Feature> load(final String schemaId) throws Exception {
    final var store = MongoFeaturesStore.open(mongoUrl, database);
    try {
      return FeaturesLoader.loadFrom(store, schemaId, TIMEOUT_SECONDS);
    } finally {
      store.close();
    }
  }
}
