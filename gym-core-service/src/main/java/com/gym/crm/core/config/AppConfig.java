package com.gym.crm.core.config;

import com.gym.crm.core.logging.RestLoggingFilter;
import com.gym.crm.core.logging.TransactionLoggingFilter;
import jakarta.servlet.DispatcherType;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.core.Ordered;

@Configuration
@EnableAspectJAutoProxy
public class AppConfig {

    @Bean
    public FilterRegistrationBean<RestLoggingFilter> restLoggingFilterBean() {
        FilterRegistrationBean<RestLoggingFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new RestLoggingFilter());
        registrationBean.addUrlPatterns("/*");
        registrationBean.setDispatcherTypes(DispatcherType.REQUEST);
        registrationBean.setAsyncSupported(true);
        registrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE + 1);

        return registrationBean;
    }

    @Bean
    public FilterRegistrationBean<TransactionLoggingFilter> transactionLoggingFilterBean() {
        FilterRegistrationBean<TransactionLoggingFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new TransactionLoggingFilter());
        registrationBean.addUrlPatterns("/*");
        registrationBean.setDispatcherTypes(DispatcherType.REQUEST);
        registrationBean.setAsyncSupported(true);
        registrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE);

        return registrationBean;
    }
}
