package com.jonatas.orderservice;

import com.jonatas.orderservice.dto.OrderDTO;
import com.jonatas.orderservice.messaging.OrderProducer;
import com.jonatas.orderservice.model.Order;
import com.jonatas.orderservice.repository.OrderRepository;
import com.jonatas.orderservice.service.OrderService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes do OrderService")
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderProducer orderProducer;

    @InjectMocks
    private OrderService orderService;

    private OrderDTO.Request validRequest;
    private Order savedOrder;

    @BeforeEach
    void setUp() {
        validRequest = new OrderDTO.Request(
                "Jônatas",
                "jonatas@email.com",
                "Notebook Dell",
                1,
                new BigDecimal("4500.00")
        );

        savedOrder = Order.builder()
                .id(1L)
                .customerName("Jônatas")
                .customerEmail("jonatas@email.com")
                .productName("Notebook Dell")
                .quantity(1)
                .totalPrice(new BigDecimal("4500.00"))
                .status(Order.OrderStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("Deve criar pedido e publicar evento no RabbitMQ")
    void shouldCreateOrderAndPublishEvent() {
        // Arrange
        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);

        // Act
        OrderDTO.Response response = orderService.createOrder(validRequest);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getCustomerEmail()).isEqualTo("jonatas@email.com");
        assertThat(response.getStatus()).isEqualTo(Order.OrderStatus.PENDING);

        // Verifica que o evento foi publicado no RabbitMQ
        verify(orderProducer, times(1)).sendOrderEvent(any(OrderDTO.OrderEvent.class));
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando pedido não for encontrado")
    void shouldThrowExceptionWhenOrderNotFound() {
        // Arrange
        when(orderRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> orderService.findById(99L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("99");

        // Garante que o producer NÃO foi chamado
        verifyNoInteractions(orderProducer);
    }

    @Test
    @DisplayName("Deve retornar pedido quando encontrado por ID")
    void shouldReturnOrderWhenFoundById() {
        // Arrange
        when(orderRepository.findById(1L)).thenReturn(Optional.of(savedOrder));

        // Act
        OrderDTO.Response response = orderService.findById(1L);

        // Assert
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getProductName()).isEqualTo("Notebook Dell");
    }
}
