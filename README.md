# 🚀 Flash Sale System

An **Event-Driven Microservices** project built with **Spring Boot**, **Apache Kafka**, **Redis**, and **PostgreSQL** to simulate high-traffic e-commerce flash sale scenarios.

The primary goal of this project is to handle thousands of concurrent purchase requests while maintaining **high performance**, **fault tolerance**, and **data consistency** across distributed services using the **Saga Pattern (Choreography)**.

---

# 📌 Overview

During flash sale events, traditional database-centric architectures often become bottlenecks because thousands of users attempt to purchase the same product simultaneously.

This project demonstrates how to solve this problem by combining:

* Redis for ultra-fast stock management
* Kafka for asynchronous communication
* PostgreSQL for persistent storage
* Saga Pattern for distributed transaction management

---

# 🏗️ Architecture

```
                +----------------+
                |     Client     |
                +--------+-------+
                         |
                         |
                  HTTP Request
                         |
                +--------v-------+
                | Order Service  |
                +--------+-------+
                         |
         Save Order (PENDING)
                         |
              Publish Event (Kafka)
                         |
                +--------v-------+
                |  Kafka Broker  |
                +--------+-------+
                         |
              Consume Order Event
                         |
                +--------v-------+
                | Wallet Service |
                +--------+-------+
                         |
          Balance Check / Payment
               |              |
         SUCCESS          FAILED
               |              |
               |      PaymentFailedEvent
               |              |
               +--------------+
                              |
                     Order Service
                              |
             Update Order Status
             Restore Redis Stock
```

---

# ⚙️ Tech Stack

| Technology      | Purpose                    |
| --------------- | -------------------------- |
| Java 17         | Programming Language       |
| Spring Boot 3   | Microservice Framework     |
| Spring Data JPA | Database Access            |
| Apache Kafka    | Event Streaming            |
| Redis           | In-Memory Stock Management |
| PostgreSQL      | Persistent Storage         |
| Docker          | Containerization           |
| Docker Compose  | Infrastructure Management  |
| Maven           | Dependency Management      |

---

# ✨ Key Features

## 🚀 High Performance Stock Management

Instead of updating the database for every purchase request:

* Product stock is stored in Redis.
* Stock deduction happens using Redis atomic operations.
* Database load is dramatically reduced.
* Millisecond-level response times.

---

## 📨 Event-Driven Communication

Services never call each other directly.

The Order Service simply publishes an event to Kafka.

Benefits:

* Loose coupling
* Scalability
* Non-blocking communication
* Easier service evolution

---

## 🔄 Saga Pattern (Choreography)

Distributed transactions cannot rely on traditional ACID transactions.

This project implements the **Choreography Saga Pattern**.

Scenario:

1. Order Service creates an order.
2. Stock is deducted from Redis.
3. Order status becomes **PENDING**.
4. Order event is published.
5. Wallet Service processes payment.
6. If payment succeeds:

   * Order completes.
7. If payment fails:

   * Wallet Service publishes `PaymentFailedEvent`
   * Order Service receives rollback event
   * Order status becomes **FAILED**
   * Stock is restored to Redis

This guarantees **eventual consistency** across services.

---

# 📂 Project Structure

```
flash-sale-system
│
├── infrastructure
│   └── docker-compose.yml
│
├── order-service
│   ├── controller
│   ├── service
│   ├── kafka
│   ├── redis
│   ├── entity
│   └── repository
│
├── wallet-service
│   ├── controller
│   ├── service
│   ├── kafka
│   ├── entity
│   └── repository
│
└── README.md
```

---

# 🚀 Getting Started

## Prerequisites

* Java 17
* Maven
* Docker
* Docker Compose

---

## 1. Clone Repository

```bash
git clone https://github.com/yourusername/flash-sale-system.git

cd flash-sale-system
```

---

## 2. Start Infrastructure

```bash
docker compose up -d
```

This starts:

* PostgreSQL
* Redis
* Kafka
* Zookeeper

---

## 3. Run Microservices

Start the services in the following order:

```
Order Service
Port: 8081

↓

Wallet Service
Port: 8082
```

---

# 🧪 API Testing

## Initialize Product Stock

```http
POST /api/orders/init-stock?productId=macbook_pro&quantity=10
```

Example:

```
POST http://localhost:8081/api/orders/init-stock?productId=macbook_pro&quantity=10
```

---

## Insert Wallet Balance

```sql
INSERT INTO wallet (user_id, balance)
VALUES ('usr_9981',1000);

INSERT INTO wallet (user_id, balance)
VALUES ('usr_poor',10);
```

---

## Successful Purchase

```http
POST /api/orders/create?productId=macbook_pro&userId=usr_9981&amount=500
```

Expected Result

* Order created
* Redis stock decreases
* Wallet balance decreases
* Payment succeeds

---

## Failed Purchase (Saga Rollback)

```http
POST /api/orders/create?productId=macbook_pro&userId=usr_poor&amount=50000
```

Expected Result

* Wallet balance insufficient
* PaymentFailedEvent published
* Order marked as FAILED
* Redis stock restored

---

## Manual Rollback Test

```http
POST /api/orders/test-rollback?orderNumber=ORDER_NUMBER
```

Useful when Kafka is temporarily unavailable.

---

# 🎯 Concepts Demonstrated

* Event-Driven Architecture
* Microservices
* Apache Kafka
* Saga Pattern
* Distributed Transactions
* Eventual Consistency
* Redis Atomic Operations
* Asynchronous Messaging
* Dockerized Infrastructure
* REST APIs

---

# 📈 Future Improvements

* API Gateway
* Spring Security + JWT
* Distributed Tracing (Zipkin)
* Prometheus & Grafana Monitoring
* Kubernetes Deployment
* Circuit Breaker (Resilience4j)
* Outbox Pattern
* Idempotency Support
* CI/CD Pipeline (GitHub Actions)

---

# 👨‍💻 Author

**Mevlüt Can Erdem**

Computer Engineering Student

Backend Developer (Java & Spring Boot)

Interested in Distributed Systems, Event-Driven Architecture, and Scalable Backend Development.

---

# ⭐ If you found this project useful, consider giving it a star!
