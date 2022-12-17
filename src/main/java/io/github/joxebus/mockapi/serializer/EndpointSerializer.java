package io.github.joxebus.mockapi.serializer;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import io.github.joxebus.mockapi.model.Endpoint;

public class EndpointSerializer extends StdSerializer<Endpoint> {

    public EndpointSerializer(Class<Endpoint> type) {
        super(type);
    }

    @Override
    public void serialize(Endpoint value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();
        genEndpoint(value, gen);
        gen.writeEndObject();
    }

    public void genEndpoint(Endpoint value, JsonGenerator gen) throws IOException {
        gen.writeStringField("href", value.getHref());
        gen.writeStringField("method", value.getMethod());
        gen.writeNumberField("statusCode", value.getStatusCode());
    }
}
