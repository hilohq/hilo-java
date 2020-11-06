package com.hilohq.hilo.structure_learning_integration_tests.parsing.helpers;

import com.hilohq.hilo.connector.mongodb.flink.MongoIntoDocumentSerializer;
import com.hilohq.hilo.protobufs.schema.Model;
import org.bson.Document;

public final class JsonSchemaLogsToDocumentSerializer implements MongoIntoDocumentSerializer<Model.SchemaUpdate> {
    private final ProtobufMessageToDocumentSerializer<Model.SchemaFieldLogsModel> serializer;

    public JsonSchemaLogsToDocumentSerializer() {
        this.serializer = new ProtobufMessageToDocumentSerializer<>();
    }

    @Override
    public Document serialize(final Model.SchemaUpdate value) {
        final var log = Model.SchemaFieldLog
                .newBuilder()
                .setJson(value.getJson().getFieldLogs().getFieldLogs()).build();
        final var model = Model.SchemaFieldLogsModel.newBuilder()
                .setLog(log)
                .setMetadata(value.getMetadata()).build();

        return serializer.serialize(model);
    }
}
