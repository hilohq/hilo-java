package com.hilohq.hilo.schema_service.service;

import com.hilohq.hilo.protobufs.structure.Schema;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

public interface SchemaStore {
  CompletableFuture<Schema.SchemaFieldsModel> getSchema(String schemaId);

  CompletableFuture<Collection<Schema.SchemaMetadataModel>> getSchemaIdsBySource(String sourceId);

  CompletableFuture<Collection<Schema.SchemaLogsModel>> getSchemaLogs(String schemaId);
}
