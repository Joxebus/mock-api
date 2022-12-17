package io.github.joxebus.mockapi.serializer;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import io.github.joxebus.mockapi.model.Endpoint;
import io.github.joxebus.mockapi.model.EndpointConfiguration;

public class EndpointConfigurationSerializer extends StdSerializer<EndpointConfiguration> {

    private EndpointSerializer endpointSerializer;

    public EndpointConfigurationSerializer(Class<EndpointConfiguration> type) {
        super(type);
        this.endpointSerializer = new EndpointSerializer(Endpoint.class);
    }

    @Override
    public void serialize(EndpointConfiguration value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();
        gen.writeStringField("config", value.getConfig());
        gen.writeArrayFieldStart("endpoints");
        for(Endpoint endpoint: value.getEndpoints()) {
            gen.writeObject(endpoint);
            //endpointSerializer.genEndpoint(endpoint, gen);
        }
        gen.writeEndArray();
        gen.writeEndObject();
    }
}
