package com.hilohq.hilo.structure_learning_integration_tests.parsing.helpers;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;
import com.google.protobuf.util.JsonFormat;
import org.apache.flink.api.common.serialization.SerializationSchema;

import java.nio.charset.StandardCharsets;

public class JsonSerializationSchema<T extends Message> implements SerializationSchema<T> {
    @Override
    public byte[] serialize(T element) {
        try {
            return JsonFormat.printer().print(element).getBytes(StandardCharsets.UTF_8);
        } catch (InvalidProtocolBufferException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
