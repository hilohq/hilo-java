package com.hilohq.hilo.structure_learning_integration_tests.parsing.helpers;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;
import com.google.protobuf.util.JsonFormat;
import com.hilohq.hilo.protobufs.schema.Json;
import com.hilohq.hilo.protobufs.schema.Schema;
import org.apache.flink.api.common.serialization.SerializationSchema;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.hilohq.hilo.structure_learning.parsing.json.entity.JsonUtils.fullname;

public class JsonSchemaUpdateSerializationSchema implements SerializationSchema<Schema.SchemaUpdate> {
    private Map<String, Json.JsonFieldModel> listToMap(final List<Json.JsonFieldModel> fields) {
        final var result = new HashMap<String, Json.JsonFieldModel>();
        for (final var field : fields) {
            final var fullname = fullname(field.getField().getPath(),
                    field.getField().getName());
            result.put(fullname, field);
        }
        return result;
    }

    private Message schemaUpdateToSchemaModel(final Schema.SchemaUpdate update) {
        return Schema.SchemaModel.newBuilder()
                .setMetadata(update.getMetadata())
                .setJson(Json.JsonSchemaModel.newBuilder()
                        .setStats(update.getJson().getSchemaUpdate().getStats())
                        .putAllFields(listToMap(update.getJson().getSchemaUpdate().getFieldsList())))
        .build();
    }

    private Message messageToSerialize(final Schema.SchemaUpdate element) {
        if (element.getJson().hasSchemaUpdate()) {
            return schemaUpdateToSchemaModel(element);
        } else {
            return element;
        }
    }

    @Override
    public byte[] serialize(final Schema.SchemaUpdate element) {
        if (!element.hasJson()) {
            throw new IllegalArgumentException("JsonSchemaUpdateSerializationSchema only supports JSON");
        }

        final var message = messageToSerialize(element);
        try {
            return JsonFormat.printer().print(message).getBytes(StandardCharsets.UTF_8);
        } catch (InvalidProtocolBufferException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
