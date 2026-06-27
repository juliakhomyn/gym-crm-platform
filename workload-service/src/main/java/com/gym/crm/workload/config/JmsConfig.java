package com.gym.crm.workload.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gym.crm.workload.dto.TrainerWorkloadMessage;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.MessageType;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableJms
public class JmsConfig {

    @Bean
    public MessageConverter jacksonJmsMessageConverter(ObjectMapper mapper) {
        var converter = new MappingJackson2MessageConverter();
        converter.setTargetType(MessageType.TEXT);
        converter.setTypeIdPropertyName("_type");
        converter.setObjectMapper(mapper);

        Map<String, Class<?>> mappings = new HashMap<>();
        mappings.put("trainerWorkload", TrainerWorkloadMessage.class);

        converter.setTypeIdMappings(mappings);

        return converter;
    }
}
