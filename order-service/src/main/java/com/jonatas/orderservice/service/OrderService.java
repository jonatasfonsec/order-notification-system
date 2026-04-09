package com.jonatas.orderservice.service;

import com.jonatas.orderservice.dto.OrderDTO;
import com.jonatas.orderservice.messaging.OrderProducer;
import com.jonatas.orderservice.model.Order;
import com.jonatas.orderservice.repository.OrderRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderProducer orderProducer;

    /**
     * Cria um pedido, salva no banco e publica evento no RabbitMQ.
     */
    @Transactional
    public OrderDTO.Response createOrder(OrderDTO.Request request) {
        log.info("Criando pedido para o cliente: {}", request.getCustomerEmail());

        // Monta a entidade
        Order order = Order.builder()
                .customerName(request.getCustomerName())
                .customerEmail(request.getCustomerEmail())
                .productName(request.getProductName())
                .quantity(request.getQuantity())
                .totalPrice(request.getTotalPrice())
                .status(Order.OrderStatus.PENDING)
                .build();

        // Salva no banco
        Order savedOrder = orderRepository.save(order);
        log.info("Pedido criado com ID: {}", savedOrder.getId());

        // Publica evento no RabbitMQ (assíncrono)
        OrderDTO.OrderEvent event = OrderDTO.OrderEvent.fromEntity(savedOrder);
        orderProducer.sendOrderEvent(event);

        return OrderDTO.Response.fromEntity(savedOrder);
    }

    @Transactional(readOnly = true)
    public List<OrderDTO.Response> findAll() {
        return orderRepository.findAll()
                .stream()
                .map(OrderDTO.Response::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public OrderDTO.Response findById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Pedido não encontrado. ID: " + id));
        return OrderDTO.Response.fromEntity(order);
    }

    @Transactional(readOnly = true)
    public List<OrderDTO.Response> findByEmail(String email) {
        return orderRepository.findByCustomerEmail(email)
                .stream()
                .map(OrderDTO.Response::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public OrderDTO.Response updateStatus(Long id, Order.OrderStatus newStatus) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Pedido não encontrado. ID: " + id));

        log.info("Atualizando status do pedido {} de {} para {}", id, order.getStatus(), newStatus);
        order.setStatus(newStatus);

        return OrderDTO.Response.fromEntity(orderRepository.save(order));
    }
}
