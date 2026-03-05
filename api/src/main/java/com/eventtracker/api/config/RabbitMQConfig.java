package com.eventtracker.api.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;

import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;

@Configuration
public class RabbitMQConfig {

    private final String exchange;
    private final String routingKey;
    private final String queueName;

    public RabbitMQConfig(@Value("${spring.rabbitmq.template.exchange}") String exchange,
            @Value("${spring.rabbitmq.template.routing-key}") String routingKey,
            @Value("${rabbitmq.queue.name:user_activities}") String queueName) {
        this.exchange = exchange;
        this.routingKey = routingKey;
        this.queueName = queueName;
    }

    @Bean
    public DirectExchange activityExchange() {
        return new DirectExchange(exchange);
    }

    @Bean
    public Queue activityQueue() {
        return new Queue(queueName, true);
    }

    @Bean
    public Binding activityBinding(Queue queue, DirectExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(routingKey);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
