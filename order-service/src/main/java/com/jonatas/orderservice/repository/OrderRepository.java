package com.jonatas.orderservice.repository;

import com.jonatas.orderservice.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    // Spring Data gera o SQL automaticamente pelo nome do método
    List<Order> findByCustomerEmail(String email);

    List<Order> findByStatus(Order.OrderStatus status);

    List<Order> findByCustomerNameContainingIgnoreCase(String name);
}
