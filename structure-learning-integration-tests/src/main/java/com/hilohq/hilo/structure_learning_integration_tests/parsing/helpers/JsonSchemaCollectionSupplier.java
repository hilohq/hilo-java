package com.hilohq.hilo.structure_learning_integration_tests.parsing.helpers;

import com.hilohq.hilo.hilo_flink.mongodb.CollectionSupplier;
import com.hilohq.hilo.protobufs.schema.Schema;

public final class JsonSchemaCollectionSupplier implements CollectionSupplier<Schema.SchemaUpdate> {
    @Override
    public String supply(final Schema.SchemaUpdate value) {
        if (!value.hasJson()) {
            throw new IllegalArgumentException("JsonSchemaCollectionSupplier only supports " +
                    "json updates");
        }

        if (value.getJson().hasSchemaUpdate()) {
            return "schemas";

        } else if (value.getJson().hasParseErrors()) {
            return "schema_parse_errors";

        } else if (value.getJson().hasFieldLogs()) {
            return "schema_field_logs";

        } else {
            throw new IllegalArgumentException("Unexpected event type " + value.getJson());
        }
    }
}
