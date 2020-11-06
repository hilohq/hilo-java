package com.hilohq.hilo.hilo_client;

import com.hilohq.hilo.protobufs.schema.Api;
import com.hilohq.hilo.schema_service.client.SchemaClient;

import java.util.concurrent.TimeUnit;

public final class Hilo {
    public static void main(final String[] args) throws Exception {
        final var client = new SchemaClient();
        final var result = client.getSchema(Api.GetSchemaRequest.newBuilder()
                .setSourceId("f9952110-d745-4ce9-a3f6-f9d2baa16253")
                .build()).get(1, TimeUnit.SECONDS);
        System.out.println("SOMETHING");
        System.out.println("" + result.toString());
        client.close();
    }
}
