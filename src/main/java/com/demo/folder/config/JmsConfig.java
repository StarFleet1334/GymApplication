package com.demo.folder.config;

import com.demo.folder.utils.ActiveMQConstants;
import jakarta.jms.ConnectionFactory;
import org.apache.activemq.artemis.jms.client.ActiveMQJMSConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.connection.CachingConnectionFactory;

@Configuration
@EnableJms
public class JmsConfig {

    @Bean
    @Primary
    public ConnectionFactory connectionFactory() {
        ActiveMQJMSConnectionFactory cf = new ActiveMQJMSConnectionFactory(ActiveMQConstants.BROKER_URL, ActiveMQConstants.USER, ActiveMQConstants.PASSWORD);
        cf.setReconnectAttempts(-1);
        return cf;
    }

    @Bean
    public CachingConnectionFactory cachingConnectionFactory(ConnectionFactory cf) {
        return new CachingConnectionFactory(cf);
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
