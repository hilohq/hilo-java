package com.hilohq.hilo.structure_learning_integration_tests.parsing.helpers;

import com.hilohq.hilo.hilo_flink.mongodb.FilterSupplier;
import com.hilohq.hilo.protobufs.schema.Schema;
import org.bson.BsonDocument;
import org.bson.BsonString;
import org.bson.conversions.Bson;

public final class JsonSchemaUpdateFilterSupplier implements FilterSupplier<Schema.SchemaUpdate> {
    @Override
    public Bson supply(final Schema.SchemaUpdate data) {
        if (!data.hasJson()) {
            throw new IllegalArgumentException("JsonSchemaUpdateFilterSupplier only supports " +
                    "json updates");
        }

        final var bson = new BsonDocument();
        final var update = data.getJson();
        final var metadata = data.getMetadata();

        if (update.hasSchemaUpdate()) {
//            final var metadataDocument = new BsonDocument();
//            metadataDocument.append("schemaId", new BsonString(metadata.getSchemaId()));
//            bson.append("metadata.schema", metadataDocument);
            bson.append("metadata.schemaId", new BsonString(metadata.getSchemaId()));
        }

        return bson;
    }
}
