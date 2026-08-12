# Cloud-Native Banking Platform

A production-style full-stack banking platform built to demonstrate
**Spring Boot microservices, React/Next.js, Docker, Kubernetes, AWS,
GitOps CI/CD, and observability**.

> The project started as a Spring Boot monolith and evolved into a
> distributed, containerized and Kubernetes-deployed platform.

## Architecture

``` text
                         React / Next.js
                                |
                         HTTP / HTTPS
                                |
                             Traefik
                                |
                           API Gateway
                                |
             +------------------+------------------+
             |                  |                  |
             v                  v                  v
        Account Service   Customer Service   Transaction Service
             |                  |                  |
             +------------------+------------------+
                                |
                           PostgreSQL

                         Auth Service
                              |
                         PostgreSQL

 Supporting Platform:
 Eureka | Config Server | Redis | Zipkin | Prometheus | Grafana

 Deployment Platform:
 Docker | Kubernetes/K3s | AWS EC2 | Traefik | Argo CD

 CI/CD:
 GitHub Actions -> Docker Hub -> GitOps -> Argo CD -> Kubernetes
```

## Product Capabilities

-   Customer registration and management
-   Account management
-   Authentication
-   Deposit and withdrawal
-   Fund transfers
-   Transaction history
-   Customer search
-   Protected APIs
-   Service-to-service communication

## Microservices

### Account Service

Owns account operations and account-related business logic.

### Customer Service

Owns customer creation, retrieval, updates, deletion and search.

### Transaction Service

Handles deposits, withdrawals, transfers and transaction history.

A transfer demonstrates service-to-service communication:

``` text
Client
  -> API Gateway
  -> Transaction Service
  -> Account Service
  -> PostgreSQL
```

### Auth Service

Handles authentication and authentication-related workflows.

### API Gateway

Single public backend entry point. Routes requests such as:

``` text
/api/accounts/**
/api/customers/**
/api/transactions/**
/api/auth/**
```

## Service Discovery

Netflix Eureka is used for service registration and discovery.

Services register themselves and can be addressed using logical names
such as:

``` text
lb://ACCOUNT-SERVICE
lb://CUSTOMER-SERVICE
lb://TRANSACTION-SERVICE
```

This avoids hardcoding service IP addresses.

## Centralized Configuration

Spring Cloud Config Server provides centralized environment-specific
configuration.

Services communicate with it through the Kubernetes service:

``` text
http://config-server:8888
```

## Database Architecture

PostgreSQL follows a database-per-service approach:

``` text
Account Service       -> account_db
Customer Service      -> customer_db
Transaction Service   -> transaction_db
Auth Service          -> auth_db
```

Services do not directly access another service's database.

## Redis

Redis is deployed as a Kubernetes infrastructure service and is
available for caching and fast-access data.

``` text
redis:6379
```

## Security

The platform includes authentication and protected APIs.

Kubernetes Secrets are used for sensitive values such as database and
mail credentials rather than embedding credentials directly in
application manifests.

Example:

``` text
Unauthenticated request
        |
        v
Protected API
        |
        v
403 Forbidden
```

## Docker

Each Spring Boot service is packaged independently as a Docker image.

Typical flow:

``` text
Source Code
   -> Build/Test
   -> Docker Image
   -> Docker Hub
   -> Kubernetes
```

## Kubernetes

The application is deployed to Kubernetes/K3s on AWS EC2.

Workloads include:

``` text
Account Service
Customer Service
Transaction Service
Auth Service
API Gateway
Config Server
Service Registry
PostgreSQL
Redis
Zipkin
Prometheus
Grafana
```

Kubernetes resources include:

-   Deployments
-   Services
-   ConfigMaps
-   Secrets
-   PersistentVolumeClaims
-   Ingresses

Useful commands:

``` bash
kubectl get pods
kubectl get svc
kubectl get ingress
kubectl top pods
kubectl top node
```

## AWS

The current learning/portfolio deployment runs on a single:

``` text
m7i-flex.large
```

EC2 instance.

Traffic flows through:

``` text
Internet
   -> AWS EC2
   -> Traefik
   -> Kubernetes
   -> API Gateway / Applications
```

This is a portfolio/learning deployment rather than a highly available
production banking environment.

## Traefik Ingress

Traefik is the Kubernetes Ingress Controller.

Example public routes:

``` text
/api/**       -> API Gateway
/grafana/     -> Grafana
/argocd/      -> Argo CD
```

## CI/CD

GitHub Actions handles continuous integration and Docker image creation.

Typical flow:

``` text
Developer Push
      |
      v
GitHub
      |
      v
GitHub Actions
      |
      +--> Test
      +--> Build
      +--> Package
      |
      v
Docker Image
      |
      v
Docker Hub
```

Service-specific workflows allow independent builds.

## GitOps with Argo CD

Kubernetes desired state is stored in Git.

``` text
GitHub
   |
   v
Argo CD
   |
   v
Kubernetes
```

Argo CD compares Git state with cluster state and synchronizes
deployments.

Infrastructure is organized into separate paths:

``` text
infrastructure/
├── postgres/
├── redis/
├── zipkin/
├── service-registry/
├── config-server/
├── account-service/
├── customer-service/
├── transaction-service/
├── auth-service/
├── api-gateway/
├── ingress/
├── prometheus/
├── grafana/
└── argoCD/
```

Each major infrastructure component can be represented by its own Argo
CD Application.

## Observability

### Prometheus

Prometheus scrapes Spring Boot Actuator metrics from services through:

``` text
/actuator/prometheus
```

Metrics include:

-   JVM memory
-   JVM CPU
-   HTTP request metrics
-   Application startup metrics
-   Disk usage
-   Connection pool metrics
-   Application health metrics

### Grafana

Grafana uses Prometheus as a data source to visualize:

-   CPU usage
-   Memory usage
-   HTTP traffic
-   JVM metrics
-   Application health
-   Service behavior

### Zipkin

Zipkin provides distributed tracing.

Example:

``` text
API Gateway
     |
     v
Transaction Service
     |
     v
Account Service
```

This makes cross-service request flow and latency visible.

## Kubernetes Self-Healing

Kubernetes maintains the desired state.

For example:

``` text
Desired replicas = 1

Pod deleted
    |
    v
Deployment detects missing replica
    |
    v
ReplicaSet creates replacement
    |
    v
New Pod Running
```

This can be demonstrated with:

``` bash
kubectl delete pod <pod-name>
kubectl get pods -w
```

## API Documentation

OpenAPI/Swagger documentation is available through the API Gateway.

Service specifications are proxied through routes such as:

``` text
/account-service/v3/api-docs
/customer-service/v3/api-docs
/transaction-service/v3/api-docs
/auth-service/v3/api-docs
```

## Example End-to-End Transfer Flow

``` text
React / Next.js
       |
       v
Traefik
       |
       v
API Gateway
       |
       v
Transaction Service
       |
       | Account Service API
       v
Account Service
       |
       v
PostgreSQL
```

The same request can be observed through:

``` text
Prometheus -> Metrics
Grafana    -> Visualization
Zipkin     -> Distributed Trace
```

## Deployment Flow

``` text
Developer
    |
    v
Git Push
    |
    v
GitHub
    |
    v
GitHub Actions
    |
    +--> Tests
    +--> Build
    +--> Docker Image
            |
            v
        Docker Hub
            |
            v
      GitOps Manifest
            |
            v
         Argo CD
            |
            v
       Kubernetes
            |
            v
         Traefik
            |
            v
          Users
```

## Technology Stack

  Area                Technologies
  ------------------- ---------------------------------
  Frontend            React / Next.js, TypeScript
  Backend             Java, Spring Boot, Spring Cloud
  APIs                REST, OpenAPI
  Security            Spring Security, Auth Service
  Databases           PostgreSQL
  Cache               Redis
  Service Discovery   Netflix Eureka
  Configuration       Spring Cloud Config
  Gateway             Spring Cloud Gateway
  Containers          Docker, Docker Hub
  Orchestration       Kubernetes / K3s
  Cloud               AWS EC2
  Ingress             Traefik
  CI                  GitHub Actions
  CD / GitOps         Argo CD
  Metrics             Prometheus
  Dashboards          Grafana
  Tracing             Zipkin

## Portfolio Demonstration

A recommended live demonstration:

### 1. Product

``` text
Register
  -> Login
  -> View Account
  -> Transfer Funds
  -> Transaction History
```

### 2. Microservices

Show Eureka registrations:

``` text
ACCOUNT-SERVICE
CUSTOMER-SERVICE
TRANSACTION-SERVICE
AUTH-SERVICE
API-GATEWAY
CONFIG-SERVER
```

### 3. API Gateway

Demonstrate:

``` text
Frontend
  -> Traefik
  -> API Gateway
  -> Internal Service
```

### 4. Distributed Tracing

Perform a transaction and inspect the trace in Zipkin.

### 5. Metrics

Generate traffic and inspect Prometheus/Grafana.

### 6. Kubernetes

Show:

``` bash
kubectl get pods
kubectl get svc
kubectl get ingress
```

Optionally demonstrate Pod recovery.

### 7. GitOps

Make a deployment change:

``` text
Git Push
  -> Argo CD detects change
  -> Sync
  -> Kubernetes
  -> New Pod
```

## Engineering Decisions

### Why Microservices?

The original monolithic application was decomposed to learn:

-   Service boundaries
-   Independent deployment
-   Service discovery
-   Inter-service communication
-   Failure isolation
-   Distributed observability

### Why API Gateway?

The frontend only needs one public backend entry point. Internal service
addresses remain hidden.

### Why Eureka?

Dynamic service discovery avoids hardcoded service IP addresses and
supports multiple service instances.

### Why Database per Service?

Each service owns its data and other services communicate through APIs
rather than directly accessing another service's database.

### Why Kubernetes?

Kubernetes provides orchestration, desired-state management,
self-healing, rolling deployments, service networking and resource
management.

### Why Argo CD?

Git becomes the source of truth for Kubernetes desired state, providing
auditable and repeatable deployments.

### Why Prometheus + Grafana?

Prometheus collects metrics and Grafana makes system behavior visible
through dashboards.

### Why Zipkin?

Distributed tracing provides visibility into requests crossing multiple
microservices.

## Current Limitations

This is a single-node learning/portfolio deployment.

Current limitations include:

-   Single Kubernetes node
-   No multi-AZ deployment
-   No managed PostgreSQL
-   No managed Redis
-   No production-grade secret manager
-   Kafka is not part of the active deployment
-   No dedicated fraud/notification services
-   Limited horizontal scaling
-   No disaster recovery architecture
-   Production TLS/domain setup is intentionally simplified

## Future Improvements

Potential future work:

-   Kubernetes HPA
-   Multi-node Kubernetes
-   AWS RDS
-   AWS ElastiCache
-   AWS Secrets Manager
-   Kafka event-driven workflows
-   Fraud Detection Service
-   Notification Service
-   Centralized logging
-   Alertmanager
-   HTTPS with a custom domain
-   Circuit breakers
-   Rate limiting
-   Idempotency for financial operations
-   Contract testing
-   More integration testing
-   Canary / blue-green deployments

## Repository Structure

The Kubernetes repository is organized by infrastructure component:

``` text
banking-kubernetes/
└── infrastructure/
    ├── postgres/
    ├── redis/
    ├── zipkin/
    ├── service-registry/
    ├── config-server/
    ├── account-service/
    ├── customer-service/
    ├── transaction-service/
    ├── auth-service/
    ├── api-gateway/
    ├── ingress/
    ├── prometheus/
    ├── grafana/
    └── argoCD/
```

## Project Goal

The primary goal is to demonstrate the full application lifecycle:

``` text
Development
    |
    v
Testing
    |
    v
Build
    |
    v
Containerization
    |
    v
CI/CD
    |
    v
GitOps
    |
    v
Kubernetes Deployment
    |
    v
Observability
    |
    v
Operations
```

The project demonstrates the ability to work across the full stack and
application lifecycle rather than only implementing CRUD APIs.
