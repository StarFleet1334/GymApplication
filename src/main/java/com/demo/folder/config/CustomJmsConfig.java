package com.demo.folder.config;

import com.demo.folder.entity.dto.request.TrainingSessionDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.jms.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.converter.MessageConversionException;
import org.springframework.jms.support.converter.MessageConverter;

@Configuration
public class CustomJmsConfig {

    private final ObjectMapper objectMapper;

    public CustomJmsConfig(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Bean
    public JmsTemplate jmsTemplate(@Qualifier("cachingConnectionFactory") ConnectionFactory connectionFactory) {
        JmsTemplate jmsTemplate = new JmsTemplate(connectionFactory);
        jmsTemplate.setMessageConverter(customMessageConverter());
        return jmsTemplate;
    }

    @Bean
    public MessageConverter customMessageConverter() {
        return new MessageConverter() {
            @Override
            public Message toMessage(Object object, Session session) throws JMSException, MessageConversionException {
                if (object instanceof TrainingSessionDTO trainingSessionDTO) {
                    try {
                        String json = objectMapper.writeValueAsString(trainingSessionDTO);
                        return session.createTextMessage(json);
                    } catch (JsonProcessingException e) {
                        throw new MessageConversionException("Could not convert TrainingSessionDTO to JSON", e);
                    }
                }
                throw new MessageConversionException("Unsupported message type: " + object.getClass().getName());
            }

            @Override
            public Object fromMessage(Message message) throws JMSException, MessageConversionException {
                if (message instanceof TextMessage textMessage) {
                    try {
                        String json = textMessage.getText();
                        return objectMapper.readValue(json, TrainingSessionDTO.class);
                    } catch (JsonProcessingException e) {
                        throw new MessageConversionException("Could not convert JSON to TrainingSessionDTO", e);
                    }
                }
                throw new MessageConversionException("Unsupported message type");
            }
        };
    }
}