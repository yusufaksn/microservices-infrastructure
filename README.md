# Microservices Infrastructure

A sample microservices infrastructure built with **Spring Boot** and **Spring Cloud**, demonstrating a production-oriented architecture for scalable backend applications.

## Features

* Reactive API Gateway (Spring Cloud Gateway)
* JWT Authentication and Authorization with Keycloak
* Token Relay between microservices
* Asynchronous messaging with RabbitMQ
* Relational database integration
* Kubernetes deployments
* Horizontal Pod Autoscaler (HPA)
* ConfigMaps and Secrets for externalized configuration
* Docker containerization

The project currently consists of:

* API Gateway
* Ticket Service
* Notification Service
* Keycloak
* RabbitMQ
* Relational databases

The primary goal of this project is to demonstrate how modern microservices can be deployed, secured, and scaled using Kubernetes while following common cloud-native practices.


## 1. Kubernetes Cluster Setup

For local development and testing, **Minikube** is recommended. It provides a lightweight Kubernetes cluster running on a local machine and allows testing Kubernetes resources such as Deployments, Services, ConfigMaps, Secrets, and HPA without requiring a cloud environment.

### Requirements

Install the following tools:

* Docker Desktop
* Minikube
* kubectl

### Start Minikube Cluster

```bash
minikube start --driver=docker
```

Verify the cluster:

```bash
kubectl get nodes
```

Expected output:

```text
NAME       STATUS   ROLES           AGE
minikube   Ready    control-plane   1m
```

### Stop / Delete Cluster

Stop:

```bash
minikube stop
```

Delete:

```bash
minikube delete
```


2. Start Required Services

Before deploying to Kubernetes, start the required infrastructure services using Docker Compose.

From the project root directory:

docker compose up -d

This will start the required services defined in the docker-compose.yml file.


3. Deploy Kubernetes Resources

Each service contains a k8s directory with the required Kubernetes manifests.

Before applying them, review the configuration files and update any environment-specific values (such as IP addresses or hostnames) if needed.

Then apply the manifests in order:

kubectl apply -f k8s/

Repeat this step for each service.