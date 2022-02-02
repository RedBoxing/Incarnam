package fr.redboxing.incarnam.utils.serializers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.vladmihalcea.hibernate.type.util.ObjectMapperSupplier;
import fr.redboxing.incarnam.utils.SystemConfiguration;

public class CustomObjectMapperSupplier implements ObjectMapperSupplier {

    @Override
    public ObjectMapper get() {
        ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();
        SimpleModule simpleModule = new SimpleModule();
        simpleModule.addSerializer(SystemConfiguration.class, new SystemConfigurationConverter.Serializer());
        simpleModule.addDeserializer(SystemConfiguration.class, new SystemConfigurationConverter.Deserializer());
        objectMapper.registerModule(simpleModule);
        return objectMapper;
    }
}
