package com.demo.folder.config;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.connection.CachingConnectionFactory;

@Configuration
@EnableJms
public class JmsConfig {

    @Bean
    public CachingConnectionFactory cachingConnectionFactory() {
        ActiveMQConnectionFactory activeMQConnectionFactory = new ActiveMQConnectionFactory("tcp://localhost:61616");
        return new CachingConnectionFactory(activeMQConnectionFactory);
    }

    @Bean
    public DefaultJmsListenerContainerFactory jmsListenerContainerFactory(CachingConnectionFactory cachingConnectionFactory) {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        factory.setConnectionFactory(cachingConnectionFactory);
        factory.setConcurrency("1-1");
        factory.setSessionTransacted(false);
        return factory;
    }
}
