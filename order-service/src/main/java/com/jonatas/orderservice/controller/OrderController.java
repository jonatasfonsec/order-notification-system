package com.jonatas.orderservice.controller;

import com.jonatas.orderservice.dto.OrderDTO;
import com.jonatas.orderservice.model.Order;
import com.jonatas.orderservice.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Tag(name = "Orders", description = "API de gerenciamento de pedidos")
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    @Operation(summary = "Criar pedido", description = "Cria um novo pedido e publica evento no RabbitMQ")
    public ResponseEntity<OrderDTO.Response> createOrder(@Valid @RequestBody OrderDTO.Request request) {
        OrderDTO.Response response = orderService.createOrder(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Operation(summary = "Listar pedidos", description = "Retorna todos os pedidos")
    public ResponseEntity<List<OrderDTO.Response>> findAll() {
        return ResponseEntity.ok(orderService.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar pedido por ID")
    public ResponseEntity<OrderDTO.Response> findById(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.findById(id));
    }

    @GetMapping("/customer/{email}")
    @Operation(summary = "Buscar pedidos por e-mail do cliente")
    public ResponseEntity<List<OrderDTO.Response>> findByEmail(@PathVariable String email) {
        return ResponseEntity.ok(orderService.findByEmail(email));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Atualizar status do pedido")
    public ResponseEntity<OrderDTO.Response> updateStatus(
            @PathVariable Long id,
            @RequestParam Order.OrderStatus status) {
        return ResponseEntity.ok(orderService.updateStatus(id, status));
    }
}
