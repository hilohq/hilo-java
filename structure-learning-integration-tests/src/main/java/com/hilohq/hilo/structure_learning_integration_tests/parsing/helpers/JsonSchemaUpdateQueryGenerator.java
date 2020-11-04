package com.hilohq.hilo.structure_learning_integration_tests.parsing.helpers;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;
import com.google.protobuf.util.JsonFormat;
import com.hilohq.hilo.hilo_flink.mongodb.MongoQueryGenerator;
import com.hilohq.hilo.hilo_flink.mongodb.Query;
import com.hilohq.hilo.protobufs.schema.Schema;
import org.bson.Document;

import javax.print.Doc;
import java.nio.charset.StandardCharsets;

public final class JsonSchemaUpdateQueryGenerator implements MongoQueryGenerator<Schema.SchemaUpdate> {
    private Document generateQuery(final Schema.SchemaUpdate element) throws InvalidProtocolBufferException {
        final var update = element.getJson().getSchemaUpdate();
        final Document query = new Document();
        final Document setOp = new Document();

        if (update.hasStats()) {
            setOp.put("stats", Document.parse(serializedMessage(update.getStats())));
        }

        for (final var field : update.getFieldsList()) {
            setOp.put("fields.json." + field.getField().getName(),
                    Document.parse(serializedMessage(field)));
        }

        if (!setOp.isEmpty()) {
            setOp.put("metadata", Document.parse(serializedMessage(element.getMetadata())));
            query.put("$set", setOp);
        }

        return query;
    }

    private String serializedMessage(final Message message) {
        try {
            final var bytes = JsonFormat.printer().print(message).getBytes(StandardCharsets.UTF_8);
            return new String(bytes, StandardCharsets.UTF_8);
        } catch (com.google.protobuf.InvalidProtocolBufferException e) {
            throw new IllegalArgumentException(e);
        }
    }

    private Document queryFromMessage(final Schema.SchemaUpdate payload) throws InvalidProtocolBufferException {
        final var s = serializedMessage(payload);
        return Document.parse(s);
    }

    @Override
    public Query query(final Schema.SchemaUpdate payload) {
        if (!payload.hasJson()) {
            throw new IllegalArgumentException("JsonSchemaUpdateQueryGenerator only supports JSON");
        }

        try {
            if (payload.getJson().hasSchemaUpdate()) {
                final Document document = generateQuery(payload);
                return new Query(Query.Action.update, document);
            } else {
                final Document document = queryFromMessage(payload);
                return new Query(Query.Action.insert, document);
            }

        } catch (InvalidProtocolBufferException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
