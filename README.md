# 📦 Order Notification System

> Sistema de gerenciamento de pedidos com arquitetura de microserviços, mensageria assíncrona e autenticação JWT.

![Java](https://img.shields.io/badge/Java-17-orange?style=flat-square&logo=openjdk)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.2-green?style=flat-square&logo=springboot)
![Spring Cloud](https://img.shields.io/badge/Spring_Cloud-2023.0-green?style=flat-square&logo=spring)
![Kafka](https://img.shields.io/badge/Kafka-3.x-black?style=flat-square&logo=apachekafka)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15-blue?style=flat-square&logo=postgresql)
![JWT](https://img.shields.io/badge/JWT-Auth-black?style=flat-square&logo=jsonwebtokens)

---

## 📌 Sobre o projeto

Este projeto foi desenvolvido como portfólio pessoal com o objetivo de demonstrar na prática o uso de tecnologias amplamente adotadas no mercado de desenvolvimento backend Java.

O sistema simula um fluxo real de e-commerce: ao criar um pedido via API REST, um evento é publicado de forma assíncrona no **Kafka**, que é consumido pelo serviço de notificações — o qual poderia enviar um e-mail, SMS ou qualquer outro tipo de notificação ao cliente.

---

## 🏗️ Arquitetura

```
                      ┌─────────────────┐
                      │   API Gateway   │  :8080
                      │  Spring Cloud   │  ← único ponto de entrada
                      └────────┬────────┘
                               │ descobre serviços via Eureka
                  ┌────────────┴────────────┐
                  │                         │
         ┌────────▼────────┐      ┌─────────▼──────────┐
         │  order-service  │      │ notification-service│
         │     :8081       │      │      :8082          │
         │   (Producer)    │      │    (Consumer)       │
         └────────┬────────┘      └─────────▲───────────┘
                  │                         │
                  │    ┌────────────────┐   │
                  └───▶│     Kafka      ├───┘
                       │ orders.created │
                       └───────┬────────┘
                  ┌────────────┘
                  ▼
           ┌─────────────┐    ┌──────────────────┐
           │  PostgreSQL │    │  Eureka Server   │
           │  ordersdb   │    │     :8761        │
           └─────────────┘    └──────────────────┘
```

---

## 🛠️ Tecnologias utilizadas

| Camada | Tecnologia | Descrição |
|---|---|---|
| API Gateway | Spring Cloud Gateway | Ponto único de entrada, roteamento |
| Service Discovery | Netflix Eureka | Registro e descoberta de serviços |
| Mensageria | Kafka + Spring Kafka | Comunicação assíncrona entre serviços |
| REST API | Spring Web | Endpoints RESTful |
| Persistência | Spring Data JPA + PostgreSQL | ORM e banco relacional |
| Segurança | Spring Security + JWT | Autenticação stateless |
| Documentação | Swagger / OpenAPI | Documentação interativa da API |
| Testes | JUnit 5 + Mockito | Testes unitários |
| Build | Maven | Gerenciamento de dependências |

---

## 📂 Estrutura do projeto

```
order-notification-system/
├── api-gateway/              # Roteamento e ponto de entrada
├── eureka-server/            # Service Discovery
├── order-service/            # API de pedidos (Producer)
│   ├── controller/           # Endpoints REST
│   ├── service/              # Regras de negócio
│   ├── repository/           # Acesso ao banco
│   ├── model/                # Entidades JPA
│   ├── dto/                  # Objetos de transferência
│   ├── messaging/            # Producer Kafka
│   └── config/               # Security, JWT, Kafka
├── notification-service/     # Serviço de notificações (Consumer)
│   ├── consumer/             # Consumer Kafka
│   └── config/               # Configuração Kafka
└── docker-compose.yml        # PostgreSQL + Kafka
```

---

## 🚀 Como executar

### Pré-requisitos

- Java 17+
- Maven 3.8+
- Docker e Docker Compose

### 1. Subir a infraestrutura

```bash
docker-compose up -d
```

Isso sobe o PostgreSQL, Kafka, Zookeeper e o Kafka UI automaticamente. O tópico `orders.created` é criado automaticamente na primeira mensagem.

### 2. Iniciar os serviços (nesta ordem)

```bash
# Terminal 1 — Service Discovery
cd eureka-server && mvn spring-boot:run

# Terminal 2 — Order Service
cd order-service && mvn spring-boot:run

# Terminal 3 — Notification Service
cd notification-service && mvn spring-boot:run

# Terminal 4 — API Gateway
cd api-gateway && mvn spring-boot:run
```

### 3. Acessar

| URL | Descrição |
|---|---|
| http://localhost:8080/swagger-ui/index.html | Documentação Swagger |
| http://localhost:8761 | Dashboard Eureka |
| http://localhost:8090 | Dashboard Kafka UI |

---

## 📡 Usando a API

### 1. Autenticar e obter token JWT

```http
POST http://localhost:8080/api/auth/login
Content-Type: application/json

{
  "username": "jonatas",
  "password": "senha123"
}
```

Resposta:
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "type": "Bearer",
  "expiresIn": 86400
}
```

### 2. Criar um pedido

```http
POST http://localhost:8080/api/orders
Authorization: Bearer {token}
Content-Type: application/json

{
  "customerName": "Jônatas",
  "customerEmail": "jonatas@email.com",
  "productName": "Notebook Dell",
  "quantity": 1,
  "totalPrice": 4500.00
}
```

### 3. Outros endpoints disponíveis

```http
GET    /api/orders                        # Listar todos os pedidos
GET    /api/orders/{id}                   # Buscar por ID
GET    /api/orders/customer/{email}       # Buscar por e-mail
PATCH  /api/orders/{id}/status?status=CONFIRMED  # Atualizar status
```

Todos os endpoints de pedidos requerem o token JWT no header `Authorization`.

> **Dica:** No Swagger, clique no botão **Authorize** (cadeado) no canto superior direito e cole o token sem o prefixo `Bearer` — ele é adicionado automaticamente.

---

## 🔄 Fluxo de mensageria

```
1. Cliente faz POST /api/orders via Gateway
2. order-service valida JWT e salva pedido no PostgreSQL
3. OrderProducer publica OrderEvent no tópico orders.created
4. Kafka entrega a mensagem para o consumer do grupo notification-group
5. notification-service consome a mensagem
6. Notificação é processada (log / e-mail / SMS)
```

---

## 🧪 Executar os testes

```bash
cd order-service
mvn test
```

---

## 💡 Próximos passos

- [ ] Envio real de e-mails com Spring Mail
- [ ] Dead Letter Topic (DLT) para mensagens com falha
- [ ] Versionamento do banco com Flyway
- [ ] Containerização com Dockerfile
- [ ] Testes de integração com Testcontainers
- [ ] Circuit Breaker com Resilience4j

---

## 👨‍💻 Autor

**Jônatas Brandani Fonseca de Abreu**  
Desenvolvedor Java Backend  
[![LinkedIn](https://img.shields.io/badge/LinkedIn-blue?style=flat-square&logo=linkedin)](https://linkedin.com/in/jbfda)
[![GitHub](https://img.shields.io/badge/GitHub-black?style=flat-square&logo=github)](https://github.com/jonatasfonsec)
