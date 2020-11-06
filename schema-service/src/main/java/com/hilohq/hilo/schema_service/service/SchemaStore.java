package com.hilohq.hilo.schema_service.service;

import com.hilohq.hilo.protobufs.schema.Model;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;

public interface SchemaStore {
  CompletableFuture<Collection<Model.SchemaMetadataModel>> getSchemaMetadata(String sourceId);

  CompletableFuture<Model.SchemaModel> getSchema(String schemaId);

  CompletableFuture<Collection<Model.SchemaFieldLog>> getSchemaLogs(String schemaId);

  CompletableFuture<Collection<Model.SchemaParseError>> getSchemaErrors(String schemaId);
}
