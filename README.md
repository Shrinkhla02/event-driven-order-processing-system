# Event-Driven Order Processing System

## Overview

This project is a cloud-native, event-driven microservices system built using Spring Boot, Apache Kafka, Docker, and Kubernetes. It simulates a real-world distributed order processing workflow similar to production e-commerce platforms. The system demonstrates modern backend architecture principles such as asynchronous communication, loose coupling, scalability, and fault tolerance.

## Architecture

```text
Client
  |
  v
Order Service
  |
  v
Kafka Topic (order-created)
  |
  v
--------------------------------------
|            |              |         |
Payment   Inventory   Notification   (Consumers)
Service    Service       Service
--------------------------------------
```

The system follows an event-driven architecture where services communicate using Kafka events instead of direct synchronous calls.

## Design Principles

* Event-driven communication using Kafka
* Loose coupling between services
* Independent deployment and scaling
* Fault isolation across services
* Asynchronous processing for improved scalability

## Technology Stack

* Backend: Spring Boot (Microservices)
* Messaging: Apache Kafka
* Containerization: Docker
* Orchestration: Kubernetes (Minikube)
* Language: Java

## Microservices

Order Service: Accepts order requests and publishes OrderCreated events to Kafka. Payment Service: Consumes order events and processes payment logic. Inventory Service: Consumes order events and updates stock availability. Notification Service: Consumes order events and sends order confirmation notifications.

## Kafka Topics

order-created: Triggered when a new order is placed. order-failed: Used for handling failed events (optional extension).

## Running with Docker

Build Docker images for all services and run the system using Docker Compose.

Commands:

```bash
docker build -t order-service .
docker build -t payment-service .
docker build -t inventory-service .
docker build -t notification-service .

docker-compose up
```

This will start Kafka, Zookeeper, and all microservices together.

## Running with Kubernetes (Minikube)

Start Kubernetes cluster:

```bash
minikube start
```

Deploy application:

```bash
kubectl apply -f k8s/
```

Verify deployment:

```bash
kubectl get pods
kubectl get services
```

## Event Flow

1. Client sends an order request to Order Service
2. Order Service publishes event to Kafka
3. Kafka distributes event to consumer services
4. Payment, Inventory, and Notification services process the event independently
5. Each service runs asynchronously without direct dependency

## Fault Tolerance Features

* Retry mechanism for transient failures
* Dead Letter Queue (DLQ) for failed events
* Idempotent consumers to prevent duplicate processing
* Service isolation to avoid cascading failures

## Observability (Optional Enhancement)

* Prometheus for metrics collection
* Grafana for dashboards
* Monitoring includes API latency, Kafka consumer lag, and service health

## Key Learnings

* Event-driven architecture using Kafka
* Microservices design and communication patterns
* Docker containerization of distributed systems
* Kubernetes deployment and orchestration
* Fault-tolerant system design
* Scalable backend architecture principles

## Future Enhancements

* API Gateway using Spring Cloud Gateway
* JWT authentication and authorization
* Distributed tracing using OpenTelemetry or Zipkin
* Database per microservice (PostgreSQL)
* CI/CD pipeline using GitHub Actions

## Author

This project was built for learning and demonstrating production-grade backend system design using microservices, event-driven architecture, and cloud-native tools.

## Quick Start

```bash
git clone <repository-url>
cd project-directory
docker-compose up
```
