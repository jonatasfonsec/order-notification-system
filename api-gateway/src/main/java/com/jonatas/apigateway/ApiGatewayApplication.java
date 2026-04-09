package com.jonatas.apigateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// O Gateway é o único ponto de entrada do sistema.
// O cliente externo nunca fala diretamente com order-service ou notification-service.
// Toda requisição passa pelo Gateway, que descobre os serviços via Eureka
// e faz o roteamento automaticamente.
@SpringBootApplication
public class ApiGatewayApplication {
    public static void main(String[] args) {
        SpringApplication.run(ApiGatewayApplication.class, args);
    }
}
