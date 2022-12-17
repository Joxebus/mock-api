package io.github.joxebus.mockapi.serializer;

import java.io.IOException;
import java.util.Map;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import io.github.joxebus.mockapi.model.ApiOperation;

public class ApiOperationSerializer extends StdSerializer<ApiOperation> {

    public ApiOperationSerializer(Class<ApiOperation> type) {
        super(type);
    }

    @Override
    public void serialize(ApiOperation value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();
        genApiOperation(value, gen);
        gen.writeEndObject();
    }

    public void genApiOperation(ApiOperation value, JsonGenerator gen) throws IOException {
        gen.writeStringField("method", value.getMethod());
        gen.writeStringField("body", value.getBody());
        gen.writeNumberField("statusCode", value.getStatusCode());
        gen.writeObjectField("headers", value.getHeaders());
    }

}
