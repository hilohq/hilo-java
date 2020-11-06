package com.hilohq.hilo.structure_learning_integration_tests.parsing.helpers;

import com.hilohq.hilo.connector.mongodb.flink.MongoIntoDocumentSerializer;
import com.hilohq.hilo.protobufs.schema.Model;
import org.bson.Document;

public final class JsonSchemaErrorsToDocumentSerializer  implements MongoIntoDocumentSerializer<Model.SchemaUpdate> {
    private final ProtobufMessageToDocumentSerializer<Model.SchemaParseErrorsModel> serializer;

    public JsonSchemaErrorsToDocumentSerializer() {
        this.serializer = new ProtobufMessageToDocumentSerializer<>();
    }

  @Override
  public Document serialize(final Model.SchemaUpdate value) {
    final var error =
        Model.SchemaParseError.newBuilder()
            .setJson(value.getJson().getParseErrors().getParseErrors())
            .build();
    final var model =
        Model.SchemaParseErrorsModel.newBuilder()
            .setError(error)
            .setMetadata(value.getMetadata())
            .build();

    return serializer.serialize(model);
    }
}
