package com.jonatas.orderservice.dto;

import com.jonatas.orderservice.model.Order;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

// DTO para criar um pedido (entrada)
public class OrderDTO {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Request {

        @NotBlank(message = "Nome do cliente é obrigatório")
        private String customerName;

        @Email(message = "E-mail inválido")
        @NotBlank(message = "E-mail é obrigatório")
        private String customerEmail;

        @NotBlank(message = "Nome do produto é obrigatório")
        private String productName;

        @Min(value = 1, message = "Quantidade mínima é 1")
        @NotNull(message = "Quantidade é obrigatória")
        private Integer quantity;

        @DecimalMin(value = "0.01", message = "Preço deve ser maior que zero")
        @NotNull(message = "Preço total é obrigatório")
        private BigDecimal totalPrice;
    }

    // DTO de resposta (saída)
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response {
        private Long id;
        private String customerName;
        private String customerEmail;
        private String productName;
        private Integer quantity;
        private BigDecimal totalPrice;
        private Order.OrderStatus status;
        private LocalDateTime createdAt;

        // Converte a entidade Order para Response DTO
        public static Response fromEntity(Order order) {
            return Response.builder()
                    .id(order.getId())
                    .customerName(order.getCustomerName())
                    .customerEmail(order.getCustomerEmail())
                    .productName(order.getProductName())
                    .quantity(order.getQuantity())
                    .totalPrice(order.getTotalPrice())
                    .status(order.getStatus())
                    .createdAt(order.getCreatedAt())
                    .build();
        }
    }

    // Evento publicado no RabbitMQ
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderEvent {
        private Long orderId;
        private String customerName;
        private String customerEmail;
        private String productName;
        private Integer quantity;
        private BigDecimal totalPrice;
        private String status;
        private LocalDateTime createdAt;

        public static OrderEvent fromEntity(Order order) {
            return OrderEvent.builder()
                    .orderId(order.getId())
                    .customerName(order.getCustomerName())
                    .customerEmail(order.getCustomerEmail())
                    .productName(order.getProductName())
                    .quantity(order.getQuantity())
                    .totalPrice(order.getTotalPrice())
                    .status(order.getStatus().name())
                    .createdAt(order.getCreatedAt())
                    .build();
        }
    }
}
