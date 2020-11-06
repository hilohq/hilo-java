package com.hilohq.hilo.schema_server;

import com.hilohq.hilo.schema_service.service.MongoSchemaStore;
import com.hilohq.hilo.schema_service.service.SchemaService;
import io.grpc.ServerBuilder;

public final class SchemaServer {
    public static void main(final String[] args) throws Exception {
        final var store = MongoSchemaStore.open("mongodb://root:example@localhost:27017/admin");
        final var service = new SchemaService(store);
        final var server = ServerBuilder.forPort(1234)
                .addService(service).build();
        server.start();
        System.out.println("SERVER LISTENING");
        server.awaitTermination();
    }
}
