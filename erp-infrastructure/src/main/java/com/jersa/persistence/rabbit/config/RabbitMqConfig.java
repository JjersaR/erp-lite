package com.jersa.persistence.rabbit.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import tools.jackson.databind.json.JsonMapper;

@Configuration
public class RabbitMqConfig {
    public static final String EXCHANGE = "erp.events"; // TOPIC
    public static final String QUEUE = "erp.product.created";
    public static final String ROUTING_KEY = "product.created";

    @Bean
    public TopicExchange getExchange() {
        return new TopicExchange(EXCHANGE);
    }

    @Bean
    public Queue getQueue() {
        return new Queue(QUEUE);
    }

    @Bean
    public Binding getBinding(Queue queue, TopicExchange exchange) {
        return BindingBuilder
                .bind(queue)
                .to(exchange)
                .with(ROUTING_KEY);
    }

    @Bean
    public JacksonJsonMessageConverter messageConverter(JsonMapper jsonMapper) {
        return new JacksonJsonMessageConverter(jsonMapper);
    }

    @Bean
    public RabbitTemplate getRabbitTemplate(ConnectionFactory connectionFactory, JacksonJsonMessageConverter converter) {
        var rabbitTemplate = new  RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(converter);

        return rabbitTemplate;
    }
}