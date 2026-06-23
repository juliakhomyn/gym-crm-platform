package com.gym.crm.workload.config;

import com.gym.crm.workload.logging.RestLoggingFilter;
import com.gym.crm.workload.logging.TransactionLoggingFilter;
import jakarta.servlet.DispatcherType;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

@Configuration
public class AppConfig {

    @Bean
    public FilterRegistrationBean<RestLoggingFilter> restLoggingFilterBean() {
        FilterRegistrationBean<RestLoggingFilter> bean = new FilterRegistrationBean<>();
        bean.setFilter(new RestLoggingFilter());
        bean.addUrlPatterns("/*");
        bean.setDispatcherTypes(DispatcherType.REQUEST);
        bean.setAsyncSupported(true);
        bean.setOrder(Ordered.HIGHEST_PRECEDENCE + 1);

        return bean;
    }

    @Bean
    public FilterRegistrationBean<TransactionLoggingFilter> transactionLoggingFilterBean() {
        FilterRegistrationBean<TransactionLoggingFilter> bean = new FilterRegistrationBean<>();
        bean.setFilter(new TransactionLoggingFilter());
        bean.addUrlPatterns("/*");
        bean.setDispatcherTypes(DispatcherType.REQUEST);
        bean.setAsyncSupported(true);
        bean.setOrder(Ordered.HIGHEST_PRECEDENCE);

        return bean;
    }
}
