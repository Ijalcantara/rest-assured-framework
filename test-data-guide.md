# Test Data Guide

Version: 1.0
Framework: REST-Assured API Automation Framework

---

# 1. Overview

This document describes how test data is structured and used within the REST-Assured API Automation Framework.

The framework stores configuration values, request payloads, expected responses, and resiliency settings in a centralized JSON file.

Centralizing test data allows:

* Separation of test logic and test data
* Easier maintenance of request payloads
* Reusable data across multiple test scenarios
* Consistent environment configuration

---

# 2. Test Data Location

All test data is stored in a single JSON file located at:

```
src/test/resources/testdata/testdata.json
```

This file acts as the central source of configuration and data used by the automation framework.

---

# 3. Test Data Structure

The `testdata.json` file is organized by functional domains and configuration groups.

Example structure:

```
testdata.json
│
├── environment
├── gorest
├── dummyjson
├── advantageShopping
├── resiliency
└── cloudflare
```

Each section contains data used by specific APIs or framework components.

---

# 4. Environment Configuration

The `environment` section stores base URLs for APIs used in testing.

Example:

```json
"environment": {
  "dev": {
    "dummyjsonBaseUrl": "https://dummyjson.com",
    "httpbinBaseUrl": "https://httpbin.org",
    "gorestBaseUrl": "https://gorest.co.in/public/v2",
    "advantageBaseUrl": "https://www.advantageonlineshopping.com"
  }
}
```

This allows the framework to dynamically select API endpoints based on the execution environment.

---

# 5. API Test Data

Test payloads and parameters are grouped by API domain.

### Example: GoRest

```json
"gorest": {
  "token": "YOUR_GOREST_BEARER_TOKEN",
  "createUser": {
    "name": "Automation User",
    "email": "placeholder@example.com",
    "gender": "male",
    "status": "active"
  }
}
```

### Example: DummyJSON Login Scenarios

```json
"dummyjson": {
  "login": {
    "validUser": {
      "username": "emilys",
      "password": "emilyspass"
    }
  }
}
```

These datasets are used as request payloads for API tests.

---

# 6. Expected Response Messages

Expected validation messages are also stored in the test data file.

Example:

```json
"expectedMessages": {
  "missingCredentials": "Username and password required",
  "invalidUsername": "Username is not valid"
}
```

This allows tests to validate responses without hardcoding values inside test classes.

---

# 7. Resiliency Configuration

The framework also stores retry and timeout settings in the test data file.

Example:

```json
"resiliency": {
  "retry": {
    "maxAttempts": 5,
    "waitSeconds": 1
  },
  "timeout": {
    "requestTimeoutMs": 5000,
    "delaySeconds": 10
  }
}
```

These values are used by utility components such as retry mechanisms.

---

# 8. Security Considerations

To maintain secure automation practices:

* API tokens should not contain production credentials
* Sensitive values should be masked in logs
* Secrets should be injected through CI/CD environments when possible

Example placeholder:

```
YOUR_GOREST_BEARER_TOKEN
```

---

# 9. Best Practices

Follow these guidelines when maintaining test data:

* Do not hardcode request payloads inside test classes
* Keep JSON structure organized by API domain
* Use clear and descriptive keys
* Avoid storing sensitive information
* Update datasets when API schemas change

---

# 10. Maintenance

The `testdata.json` file should be updated when:

* New APIs are added
* Request payloads change
* Additional test scenarios are required

Maintaining accurate test data ensures reliable and stable automation results.

---

# 11. Related Documents

| Document                       | Description                            |
| ------------------------------ | -------------------------------------- |
| Automation Framework Run Guide | Instructions for running the framework |
| Framework Design Document      | Architecture and framework design      |
