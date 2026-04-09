package com.jonatas.orderservice.messaging;

import com.jonatas.orderservice.dto.OrderDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderProducer {

    private final RabbitTemplate rabbitTemplate;

    @Value("${app.rabbitmq.exchange}")
    private String exchange;

    @Value("${app.rabbitmq.routing-key}")
    private String routingKey;

    /**
     * Publica um evento de pedido no RabbitMQ.
     * O notification-service vai consumir essa mensagem de forma assíncrona.
     */
    public void sendOrderEvent(OrderDTO.OrderEvent event) {
        log.info("Publicando evento de pedido no RabbitMQ. OrderId: {}, Customer: {}",
                event.getOrderId(), event.getCustomerEmail());

        rabbitTemplate.convertAndSend(exchange, routingKey, event);

        log.info("Evento publicado com sucesso para o exchange '{}' com routing key '{}'",
                exchange, routingKey);
    }
}
