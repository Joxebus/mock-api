package io.github.joxebus.mockapi.serializer;

import java.io.IOException;
import java.util.Map;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import io.github.joxebus.mockapi.model.ApiConfiguration;
import io.github.joxebus.mockapi.model.ApiOperation;

public class ApiConfigurationSerializer extends StdSerializer<ApiConfiguration> {

    private final ApiOperationSerializer apiOperationSerializer;

    public ApiConfigurationSerializer(Class<ApiConfiguration> type) {
        super(type);
        this.apiOperationSerializer = new ApiOperationSerializer(ApiOperation.class);
    }

    @Override
    public void serialize(ApiConfiguration value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();
        gen.writeStringField("name", value.getName());
        gen.writeBooleanField("secured", value.isSecured());
        gen.writeStringField("authConfig", value.getAuthConfig());
        gen.writeObjectFieldStart("operations");
        for(Map.Entry<String, ApiOperation> apiOperationEntry : value.getOperations().entrySet()) {
            gen.writeObjectFieldStart(apiOperationEntry.getKey());
            apiOperationSerializer.genApiOperation(apiOperationEntry.getValue(), gen);
            gen.writeEndObject();
        }
        gen.writeEndObject();
        gen.writeEndObject();
    }
}
