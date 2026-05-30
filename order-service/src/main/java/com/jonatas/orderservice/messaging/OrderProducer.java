package com.jonatas.orderservice.messaging;

import com.jonatas.orderservice.dto.OrderDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderProducer {

    private final KafkaTemplate<String, OrderDTO.OrderEvent> kafkaTemplate;

    @Value("${app.kafka.topic}")
    private String topic;

    /**
     * Publica um evento de pedido no Kafka.
     * O notification-service vai consumir essa mensagem de forma assíncrona.
     */
    public void sendOrderEvent(OrderDTO.OrderEvent event) {
        log.info("Publicando evento de pedido no Kafka. OrderId: {}, Customer: {}",
                event.getOrderId(), event.getCustomerEmail());

        kafkaTemplate.send(topic, String.valueOf(event.getOrderId()), event);

        log.info("Evento publicado com sucesso no tópico '{}'", topic);
    }
}