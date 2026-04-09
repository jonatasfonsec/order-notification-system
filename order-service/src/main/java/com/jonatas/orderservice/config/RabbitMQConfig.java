package com.jonatas.orderservice.config;


import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@Configuration
public class RabbitMQConfig {

    @Value("${app.rabbitmq.exchange}")
    private String exchange;

    @Value("${app.rabbitmq.queue}")
    private String queue;

    @Value("${app.rabbitmq.routing-key}")
    private String routingKey;

    // Fila onde as mensagens ficam armazenadas
    @Bean
    public Queue ordersQueue() {
        // durable = true: a fila sobrevive a restart do RabbitMQ
        return new Queue(queue, true);
    }

    // Exchange do tipo Direct: roteamento exato pela routing key
    @Bean
    public DirectExchange ordersExchange() {
        return new DirectExchange(exchange);
    }

    // Binding: liga a fila ao exchange via routing key
    @Bean
    public Binding ordersBinding(Queue ordersQueue, DirectExchange ordersExchange) {
        return BindingBuilder
                .bind(ordersQueue)
                .to(ordersExchange)
                .with(routingKey);
    }

    // Converte objetos Java para JSON automaticamente
    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
    	   ObjectMapper mapper = new ObjectMapper();
    	    mapper.registerModule(new JavaTimeModule());
    	    mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    	    return new Jackson2JsonMessageConverter(mapper);
    }
    
    // RabbitTemplate com o converter JSON configurado
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter());
        return template;
    }
}
