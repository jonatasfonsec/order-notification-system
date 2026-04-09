# 📦 Order Notification System — Microserviços com Spring Cloud

Sistema de pedidos com arquitetura de microserviços completa.

## 🏗️ Arquitetura

```
                        ┌─────────────────┐
                        │   API Gateway   │  :8080
                        │  Spring Cloud   │  ← único ponto de entrada
                        └────────┬────────┘
                                 │ descobre serviços via Eureka
                    ┌────────────┴────────────┐
                    │                         │
           ┌────────▼────────┐      ┌─────────▼────────┐
           │  order-service  │      │notification-svc  │
           │   :8081         │      │   :8082          │
           │  (Producer)     │      │  (Consumer)      │
           └────────┬────────┘      └─────────▲────────┘
                    │                         │
                    │    ┌────────────────┐   │
                    └───▶│   RabbitMQ     ├───┘
                         │  orders.queue  │
                         └───────┬────────┘
                    ┌────────────┘
                    ▼
             ┌─────────────┐    ┌──────────────────┐
             │  PostgreSQL │    │  Eureka Server   │
             │  ordersdb   │    │  Service Registry│
             └─────────────┘    │     :8761        │
                                └──────────────────┘
```

## 🛠️ Tecnologias

| Camada | Tecnologia |
|---|---|
| API Gateway | Spring Cloud Gateway |
| Service Discovery | Netflix Eureka |
| Mensageria | RabbitMQ + Spring AMQP |
| REST API | Spring Web |
| Persistência | Spring Data JPA + PostgreSQL |
| Segurança | Spring Security + JWT |
| Documentação | Swagger / OpenAPI |
| Testes | JUnit 5 + Mockito |
| Infra local | Docker Compose |

## 🚀 Como rodar

### 1. Subir infraestrutura
```bash
docker-compose up -d
```

### 2. Iniciar serviços (nesta ordem)
```bash
cd eureka-server      && mvn spring-boot:run  # Terminal 1
cd order-service      && mvn spring-boot:run  # Terminal 2
cd notification-service && mvn spring-boot:run # Terminal 3
cd api-gateway        && mvn spring-boot:run  # Terminal 4
```

### 3. Acessar
| URL | Descrição |
|---|---|
| http://localhost:8080/swagger-ui.html | Swagger (via Gateway) |
| http://localhost:8761 | Dashboard Eureka |
| http://localhost:15672 | Dashboard RabbitMQ (guest/guest) |

## 📡 Uso da API

### 1. Login
```http
POST http://localhost:8080/api/auth/login
{"username": "jonatas", "password": "senha123"}
```

### 2. Criar pedido
```http
POST http://localhost:8080/api/orders
Authorization: Bearer <token>
{"customerName":"Jônatas","customerEmail":"j@email.com","productName":"Notebook","quantity":1,"totalPrice":4500.00}
```

## 📌 Fluxo completo

1. Cliente → **Gateway** (8080)
2. Gateway consulta **Eureka** → descobre order-service
3. order-service valida **JWT** → salva no **PostgreSQL**
4. **RabbitMQ** recebe o evento
5. notification-service **consome** e envia notificação

## 💡 Próximos passos
- [ ] Spring Mail para e-mails reais
- [ ] Dead Letter Queue (DLQ)
- [ ] Flyway para versionamento do banco
- [ ] Dockerizar os serviços
- [ ] Testes de integração com Testcontainers
- [ ] Circuit Breaker com Resilience4j
