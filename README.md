# REST-Assured API Automation Framework

Enterprise-grade **API automation framework** built using **REST-Assured, Java, and JUnit 5** designed for scalable, maintainable, and CI-integrated API testing.

This framework provides a standardized architecture for validating RESTful services across authentication, integration, and resiliency domains.

---

# Table of Contents

- Overview
- Architecture
- Technology Stack
- Framework Features
- Project Structure
- Installation & Setup
- Configuration
- Running Tests
- Reporting
- CI/CD Integration
- Coding Guidelines
- Logging & Security
- Contributing

---

# Overview

The REST-Assured API Automation Framework establishes an enterprise standard for automated API validation.

It enables:

- Scalable automation architecture
- Centralized configuration management
- Secure logging practices
- Parallel test execution
- CI/CD pipeline integration
- Rich execution reporting

The framework follows **layered modular architecture** enforcing strong separation of concerns.

---

# Architecture

The framework is built using a **Layered Architecture** to ensure maintainability and extensibility.

```
Test Layer
   ↓
Client Layer
   ↓
Core Execution Layer
   ↓
Utilities
   ↓
Configuration
```

### Key Architectural Principles

- Separation of Concerns
- Single Responsibility Principle
- Client Abstraction Pattern
- Factory Pattern
- Configuration-as-Code
- CI-First Validation Model

---

# Technology Stack

| Domain | Technology |
|------|------|
| Language | Java 17 |
| API Automation | REST-Assured |
| Test Framework | JUnit 5 |
| Build Tool | Maven |
| Reporting | Allure |
| Logging | SLF4J + Logback |
| CI/CD | GitHub Actions |
| JSON Serialization | Jackson |

---

# Framework Features

## Retry Mechanism

The framework includes **RetryUtil** which automatically retries transient API failures to reduce false negatives caused by unstable systems.

---

## Parallel Execution

JUnit 5 supports parallel execution allowing tests to run concurrently.

---

## Environment Configuration

Environment-based configuration enables execution across multiple environments.

Supported environments:

```
dev
qa
```

---

## Allure Reporting

Integrated **Allure Reporting** provides:

- Step level reporting
- Execution metadata
- Failure diagnostics
- Visual reports

---

## Secure Logging

Sensitive data is protected through:

- `LogSanitizerUtil`
- `LoggerUtils`

Security features include:

- Masked credentials
- Structured logs
- Secure log formatting

---

# Project Structure

```
src
 ├── test
 │   ├── java
 │   │   ├── tests
 │   │   │   ├── GoRestUserCrudTests
 │   │   │   ├── DummyJsonAuthTests
 │   │   │   └── HttpBinRetryTests
 │   │   │
 │   │   ├── clients
 │   │   │   ├── GoRestClient
 │   │   │   ├── DummyJsonClient
 │   │   │   └── HttpBinClient
 │   │   │
 │   │   ├── core
 │   │   │   ├── BaseClient
 │   │   │   └── RequestSpecFactory
 │   │   │
 │   │   ├── utils
 │   │   │   ├── RetryUtil
 │   │   │   ├── LoggerUtils
 │   │   │   └── LogSanitizerUtil
 │   │   │
 │   │   └── config
 │   │       └── ConfigManager
 │
 │   └── resources
 │       ├── config
 │       │   ├── dev.properties
 │       │   └── qa.properties
 │       │
 │       └── testdata
 │           └── *.json
```

---

# Installation & Setup

Follow the steps below to set up the REST-Assured API Automation Framework.

---

## 1. Prerequisites

Ensure the following tools are installed.

| Tool | Version |
|-----|-----|
| Java | 17 or higher |
| Maven | 3.8+ |
| Git | Latest |
| IDE | IntelliJ / VS Code / Eclipse |

Verify installation:

```
java -version
mvn -version
git --version
```

---

##  Running via Command Prompt (CMD)

This project can be executed entirely using **Command Prompt (no IDE required)**.

### Step-by-step (CMD only)

```
git clone https://github.com/Ijalcantara/rest-assured-framework.git
cd rest-assured-api-framework
mvn clean install
mvn clean test -Denv=qa
```

### One-line execution

```
mvn clean test -Denv=qa
```

### Optional: One-click runner (Windows)

Create a file named:

```
run-tests.bat
```

Add the following:

```
@echo off
echo Running API Automation Tests...
mvn clean test -Denv=qa
pause
```

Run:

```
run-tests.bat
```

### Notes

- Make sure Java and Maven are added to your system PATH
- Always run commands inside the project root directory

---

## 2. Clone Repository

```
git clone https://github.com/your-org/rest-assured-api-framework.git
cd rest-assured-api-framework
```

---

## 3. Install Project Dependencies

```
mvn clean install
```

This installs:

- REST-Assured
- JUnit 5
- Jackson
- Allure Reporting
- SLF4J / Logback

---

## 4. Required Maven Dependencies

Ensure the following dependencies exist in `pom.xml`.

```xml
<dependencies>

    <dependency>
        <groupId>io.rest-assured</groupId>
        <artifactId>rest-assured</artifactId>
        <version>5.4.0</version>
        <scope>test</scope>
    </dependency>

    <dependency>
        <groupId>org.junit.jupiter</groupId>
        <artifactId>junit-jupiter</artifactId>
        <version>5.10.2</version>
        <scope>test</scope>
    </dependency>

    <dependency>
        <groupId>com.fasterxml.jackson.core</groupId>
        <artifactId>jackson-databind</artifactId>
        <version>2.17.0</version>
    </dependency>

    <dependency>
        <groupId>io.qameta.allure</groupId>
        <artifactId>allure-junit5</artifactId>
        <version>2.25.0</version>
    </dependency>

    <dependency>
        <groupId>ch.qos.logback</groupId>
        <artifactId>logback-classic</artifactId>
        <version>1.5.6</version>
    </dependency>

</dependencies>
```

---

# Configuration

Environment configuration files are located in:

```
src/test/resources/config
```

Example:

```
dev.properties
qa.properties
```

Example configuration:

```
base.url=https://api.example.com
api.token=YOUR_API_TOKEN
```

---

# Running Tests

Run all tests:

```
mvn clean test
```

Run tests with specific environment:

```
mvn clean test -Denv=qa
```

Run specific test class:

```
mvn test -Dtest=GoRestUserCrudTests
```

---

# Reporting

Generate Allure reports:

```
allure serve target/allure-results
```

Reports include:

- Test results
- Step details
- Failure logs
- Execution timeline

---

# CI/CD Integration

The framework integrates with **GitHub Actions**.

Pipeline stages:

1. Checkout Source
2. Setup Java
3. Maven Build
4. Test Execution
5. Allure Report Generation
6. Artifact Publishing

Pipeline command:

```
mvn clean test -Denv=qa
```

---

# Coding Guidelines

### Test Layer

Tests must:

- Contain validation logic only
- Not call REST-Assured directly
- Use client classes for API interaction

---

### Client Layer

Clients must:

- Extend `BaseClient`
- Define API endpoints
- Not include assertions
- Not contain credentials

---

### Core Layer

Core components include:

- `BaseClient`
- `RequestSpecFactory`

All HTTP calls must pass through **BaseClient**.

---

# Logging Standards

Logging uses **SLF4J with Logback**.

| Level | Purpose |
|------|------|
| INFO | Normal logs |
| WARN | Recoverable issues |
| ERROR | Failures |
| DEBUG | Detailed debugging |

Sensitive data must always be sanitized before logging.

---

# Security Guidelines

- No hardcoded credentials
- Secrets injected via CI/CD
- Sensitive data masked in logs
- Centralized configuration management

---

# Contributing

Before submitting pull requests ensure:

- Naming conventions followed
- Logging standards implemented
- Security guidelines followed
- Code reviewed by peers

All PRs must pass CI validation before merging.

---

# License

Internal Enterprise Use Only.

This framework contains proprietary automation architecture and should not be distributed without authorization.