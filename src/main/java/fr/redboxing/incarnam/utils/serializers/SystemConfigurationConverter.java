package fr.redboxing.incarnam.utils.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fr.redboxing.incarnam.utils.SystemConfiguration;

import javax.persistence.AttributeConverter;
import java.io.IOException;
import java.util.Map;

public class SystemConfigurationConverter implements AttributeConverter<SystemConfiguration, String> {
    private static final ObjectMapper mapper = new ObjectMapper().registerModule(new SimpleModule()
            .addSerializer(SystemConfiguration.class, new SystemConfigurationConverter.Serializer())
            .addDeserializer(SystemConfiguration.class, new SystemConfigurationConverter.Deserializer()));

    @Override
    public String convertToDatabaseColumn(SystemConfiguration systemConfiguration) {
        try {
            return mapper.writeValueAsString(systemConfiguration);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public SystemConfiguration convertToEntityAttribute(String s) {
        try {
            return mapper.readValue(s, SystemConfiguration.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static class Serializer extends JsonSerializer<SystemConfiguration> {
        @Override
        public void serialize(SystemConfiguration systemConfiguration, JsonGenerator gen, SerializerProvider serializers) throws IOException {
            gen.writeStartObject();
            for(Map.Entry<SystemConfiguration.SystemConfigurationType, String> entry : systemConfiguration.getProperties().entrySet()) {
                gen.writeFieldName(entry.getKey().name());
                gen.writeString(entry.getValue());
            }
            gen.writeEndObject();
        }
    }

    public static class Deserializer extends JsonDeserializer<SystemConfiguration> {

        @Override
        public SystemConfiguration deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            SystemConfiguration systemConfiguration = new SystemConfiguration();

            JsonNode node = p.getCodec().readTree(p);

            node.fields().forEachRemaining(entry -> {
                SystemConfiguration.SystemConfigurationType type = SystemConfiguration.SystemConfigurationType.valueOf(entry.getKey());
                systemConfiguration.setProperty(type, entry.getValue().asText());
            });

            return systemConfiguration;
        }
    }
}
