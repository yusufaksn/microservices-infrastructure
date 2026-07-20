# Microservices Infrastructure

A sample microservices infrastructure built with Spring Boot and Spring Cloud, demonstrating a production-oriented architecture for scalable backend applications.

---

# Architecture

<p align="center">
  <img src="images/architecture.png" alt="Microservices Architecture" width="100%">
</p>

---

# Features

- Reactive API Gateway (Spring Cloud Gateway)
- Retry and Circuit Breaker support
- JWT Authentication and Authorization with Keycloak
- Token Relay between microservices
- Event-driven communication with Apache Kafka
- Change Data Capture (CDC) using Debezium
- Distributed tracing with Zipkin
- Prometheus monitoring
- PostgreSQL integration
- Kubernetes deployments
- Horizontal Pod Autoscaler (HPA)
- ConfigMaps and Secrets for externalized configuration
- Docker containerization

---

# Project Structure

The project currently consists of:

- API Gateway
- Ticket Service
- Notification Service
- Kafka
- Debezium
- Keycloak
- Zipkin
- PostgreSQL

---

# Event Flow

The services communicate asynchronously using Kafka and Debezium.

```
Client
   │
   ▼
API Gateway
   │
   ▼
Ticket Service
   │
   │ Save Ticket
   ▼
PostgreSQL (ticketdb)
   │
   │ WAL
   ▼
Debezium
   │
   ▼
Kafka
   │
   ▼
Notification Service
```

The Ticket Service writes only to PostgreSQL.

Debezium monitors PostgreSQL WAL (Write-Ahead Log), detects database changes, publishes them to Kafka, and Notification Service consumes these events.

This approach removes direct Kafka dependencies from the Ticket Service while providing reliable event publishing through Change Data Capture (CDC).

---

# Kubernetes Cluster Setup

For local development and testing, Minikube is recommended.

It provides a lightweight Kubernetes cluster capable of running Deployments, Services, ConfigMaps, Secrets and Horizontal Pod Autoscaler without requiring a cloud provider.

## Requirements

Install:

- Docker Desktop
- Minikube
- kubectl

Start Minikube:

```bash
minikube start --driver=docker
```

Verify the cluster:

```bash
kubectl get nodes
```

Expected output:

```
NAME        STATUS   ROLES           AGE
minikube    Ready    control-plane   1m
```

---

## Enable Metrics Server

Horizontal Pod Autoscaler requires Metrics Server.

Enable it:

```bash
minikube addons enable metrics-server
```

Verify:

```bash
kubectl top nodes
```

---

## Stop/Delete Cluster

Stop:

```bash
minikube stop
```

Delete:

```bash
minikube delete
```

---

# Start Required Infrastructure

Start all required infrastructure services.

```bash
docker compose up -d
```

This starts:

- PostgreSQL
- Kafka
- Debezium
- Keycloak
- Zipkin

---

# PostgreSQL Configuration

Ticket Service uses PostgreSQL.

Example schema:

```sql
CREATE TABLE tickets (
    id VARCHAR(36) PRIMARY KEY,
    description VARCHAR(600),
    notes VARCHAR(1000),
    assignee VARCHAR(50),
    ticket_date TIMESTAMP,
    priority_type INTEGER,
    ticket_status INTEGER,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE TABLE outbox_events (
    id VARCHAR(36) PRIMARY KEY,
    aggregate_type      VARCHAR(255) NOT NULL,
    aggregate_id        VARCHAR(255) NOT NULL,
    event_type          VARCHAR(255) NOT NULL,
    payload             JSONB NOT NULL,

    -- B3 Trace
    trace_id            VARCHAR(32) NOT NULL,
    span_id             VARCHAR(16) NOT NULL,

    sampled             VARCHAR(1) NOT NULL DEFAULT '1',


    db_committed_at     TIMESTAMPTZ NOT NULL DEFAULT now()
);

ALTER TABLE outbox_events REPLICA IDENTITY FULL;

```

To allow Debezium to capture UPDATE and DELETE operations correctly, configure Replica Identity:

---

# Configure Debezium

After PostgreSQL and Kafka are running, register the PostgreSQL connector.

Example:

```http
POST http://localhost:8083/connectors
```

Request body:

```json
{
  "name": "ticket-outbox-connector",
  "config": {
    "connector.class": "io.debezium.connector.postgresql.PostgresConnector",
    "tasks.max": "1",
    "plugin.name": "pgoutput",
    "database.hostname": "postgres_ticket",
    "database.port": "5432",
    "database.user": "user",
    "database.password": "password",
    "database.dbname": "ticketdb",
    "topic.prefix": "postgres_ticket",
    "table.include.list": "public.outbox_events",
    "uuid.representation": "standard"
  }
}
```

---

# Deploy Kubernetes Resources

Each service contains a `k8s` directory.

Before deploying, verify configuration values such as hostnames and IP addresses if your environment differs.

Deploy:

```bash
kubectl apply -f k8s/
```

Repeat for every service.

# Configure API Gateway

The API Gateway uses ConfigMaps for externalized configuration.

When adding a new service, update the Gateway ConfigMap with the appropriate route.

Forward Gateway locally:

```bash
kubectl port-forward service/gateway-service 8504:8504
```

Gateway endpoint:

```
http://localhost:8504
```

---

# Configure Keycloak

After all services are running:

- Create or import a Realm
- Create an API Gateway Client
- Create at least one User
- Assign the required Roles
- Obtain an Access Token

Use the token in requests:

```
Authorization: Bearer <access_token>
```

---

# Prometheus Monitoring

Create the monitoring namespace:

```bash
kubectl create namespace monitoring
```

Install the Prometheus Operator CRDs:

```bash
kubectl apply --server-side -f https://raw.githubusercontent.com/prometheus-operator/prometheus-operator/main/example/prometheus-operator-crd/stripped-down-crds.yaml
```

Add the Helm repository:

```bash
helm repo add prometheus-community https://prometheus-community.github.io/helm-charts

helm repo update
```

Install the monitoring stack:

```bash
helm install prometheus prometheus-community/kube-prometheus-stack \
    --namespace monitoring \
    --create-namespace
```

The Gateway exposes metrics through:

```
/actuator/prometheus
```

which can be scraped by Prometheus using a ServiceMonitor.

---

# Verify Deployment

Verify that every component is running correctly.

```bash
kubectl get pods
```

```bash
kubectl get services
```

```bash
kubectl get hpa
```

```bash
kubectl top nodes
```

If all Pods are in the `Running` state and HPA is available, the application is ready.

---

# Zipkin

Distributed traces can be viewed at:

```
http://localhost:9411
```

---

# Technology Stack

- Spring Boot
- Spring Cloud Gateway
- Spring Security
- Keycloak
- PostgreSQL
- Apache Kafka
- Debezium
- Kubernetes
- Docker
- Zipkin
- Prometheus