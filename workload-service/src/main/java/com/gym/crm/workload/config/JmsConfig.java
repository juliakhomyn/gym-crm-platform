package com.gym.crm.workload.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gym.crm.workload.dto.TrainerWorkloadMessage;
import jakarta.jms.ConnectionFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.activemq.RedeliveryPolicy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jms.DefaultJmsListenerContainerFactoryConfigurer;
import org.springframework.boot.autoconfigure.jms.activemq.ActiveMQConnectionFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.MessageType;
import org.springframework.util.ErrorHandler;

import java.util.Map;

@Slf4j
@Configuration
@EnableJms
public class JmsConfig {

    @Bean
    public MessageConverter jacksonJmsMessageConverter(ObjectMapper mapper) {
        var converter = new MappingJackson2MessageConverter();
        converter.setTargetType(MessageType.TEXT);
        converter.setTypeIdPropertyName("_type");
        converter.setObjectMapper(mapper);
        converter.setTypeIdMappings(Map.of("trainerWorkload", TrainerWorkloadMessage.class));

        return converter;
    }

    @Bean
    public ActiveMQConnectionFactoryCustomizer activeMqConnectionFactoryCustomizer(@Value("${spring.jms.redelivery.initial-delay-ms}") long initialDelayMs,
                                                                                   @Value("${spring.jms.redelivery.maximum-redeliveries}") int maximumRedeliveries,
                                                                                   @Value("${spring.jms.redelivery.maximum-delay-ms}") long maximumDelayMs,
                                                                                   @Value("${spring.jms.redelivery.back-off-multiplier}") double backOffMultiplier,
                                                                                   @Value("${spring.jms.redelivery.use-exponential-back-off}") boolean useExponentialBackOff) {
        return connectionFactory -> {
            RedeliveryPolicy redeliveryPolicy = connectionFactory.getRedeliveryPolicy();

            redeliveryPolicy.setInitialRedeliveryDelay(initialDelayMs);
            redeliveryPolicy.setMaximumRedeliveries(maximumRedeliveries);
            redeliveryPolicy.setMaximumRedeliveryDelay(maximumDelayMs);
            redeliveryPolicy.setBackOffMultiplier(backOffMultiplier);
            redeliveryPolicy.setUseExponentialBackOff(useExponentialBackOff);
        };
    }

    @Bean
    public DefaultJmsListenerContainerFactory jmsListenerContainerFactory(ConnectionFactory connectionFactory,
                                                                          MessageConverter messageConverter,
                                                                          ErrorHandler jmsErrorHandler,
                                                                          DefaultJmsListenerContainerFactoryConfigurer configurer) {
        var factory = new DefaultJmsListenerContainerFactory();
        configurer.configure(factory, connectionFactory);
        factory.setMessageConverter(messageConverter);
        factory.setErrorHandler(jmsErrorHandler);
        factory.setSessionTransacted(true);

        return factory;
    }

    @Bean
    public ErrorHandler jmsErrorHandler() {
        return t -> log.error("Unhandled JMS listener error", t);
    }
}
