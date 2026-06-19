package com.gym.crm.core.config;

import com.gym.crm.core.client.TrainerWorkloadClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.ClientHttpRequestFactories;
import org.springframework.boot.web.client.ClientHttpRequestFactorySettings;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

import java.time.Duration;

@Configuration
public class WorkloadClientConfig {

    @Bean
    public TrainerWorkloadClient workloadClient(RestClient.Builder restClientBuilder,
            @Value("${app.services.workload.url}") String workloadUrl,
            @Value("${app.services.workload.connect-timeout-ms}") int connectTimeoutMs,
            @Value("${app.services.workload.read-timeout-ms}") int readTimeoutMs) {

        ClientHttpRequestFactorySettings settings = ClientHttpRequestFactorySettings.DEFAULTS
                .withConnectTimeout(Duration.ofMillis(connectTimeoutMs))
                .withReadTimeout(Duration.ofMillis(readTimeoutMs));

        RestClient restClient = restClientBuilder
                .baseUrl(workloadUrl)
                .requestFactory(ClientHttpRequestFactories.get(settings))
                .build();

        return createClient(restClient, TrainerWorkloadClient.class);
    }

    private <T> T createClient(RestClient restClient, Class<T> clientClass) {
        RestClientAdapter adapter = RestClientAdapter.create(restClient);
        HttpServiceProxyFactory factory = HttpServiceProxyFactory
                .builderFor(adapter)
                .build();

        return factory.createClient(clientClass);
    }
}
