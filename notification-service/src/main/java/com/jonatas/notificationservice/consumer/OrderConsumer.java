package com.jonatas.notificationservice.consumer;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderConsumer {

    /**
     * Escuta a fila 'orders.queue' e processa os eventos de pedido.
     * O Spring deserializa o JSON automaticamente para OrderEvent.
     *
     * Em produção, aqui você enviaria um e-mail real (ex: Spring Mail + Thymeleaf)
     * ou integração com serviços como SendGrid, AWS SES, etc.
     */
    @KafkaListener(topics = "${app.kafka.topic}", groupId = "notification-group")
    public void receiveOrderEvent(OrderEvent event) {
        log.info("=== NOVO PEDIDO RECEBIDO ===");
        log.info("ID do Pedido: {}", event.getOrderId());
        log.info("Cliente: {} <{}>", event.getCustomerName(), event.getCustomerEmail());
        log.info("Produto: {} | Qtd: {} | Total: R$ {}", 
                event.getProductName(), event.getQuantity(), event.getTotalPrice());
        log.info("Status: {}", event.getStatus());
        log.info("===========================");

        // Simula envio de notificação
        sendNotification(event);
    }

    private void sendNotification(OrderEvent event) {
        // Aqui você implementaria o envio real de e-mail
        // Exemplo com Spring Mail:
        // SimpleMailMessage message = new SimpleMailMessage();
        // message.setTo(event.getCustomerEmail());
        // message.setSubject("Pedido #" + event.getOrderId() + " recebido!");
        // message.setText("Olá " + event.getCustomerName() + "...");
        // mailSender.send(message);

        log.info("Notificação enviada para: {} - Pedido #{} confirmado!",
                event.getCustomerEmail(), event.getOrderId());
    }

    // Classe interna representando o evento recebido da fila
    @Data
    public static class OrderEvent {
        private Long orderId;
        private String customerName;
        private String customerEmail;
        private String productName;
        private Integer quantity;
        private BigDecimal totalPrice;
        private String status;
        private LocalDateTime createdAt;
    }
}
