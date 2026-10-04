# WardServiceApp

## Overview

Provides lists of wards and departments.

Part of the [HealthSafe](../README.md) project. Independent Maven module, no
parent pom.

MQ: this service subscribes to the ActiveMQ topic `staffing-events-topic` — see [`../common/`](../common) — and publishes to the ActiveMQ queue `equipment-failure-queue` when it detects an equipment failure on one of its wards, consumed by [`../equipment-alert-service`](../equipment-alert-service). Broker URL, topic name, and queue name come from the common `co.wethinkcode.healthsafe.mq.MqConfig` class alongside it in this module.

REST: called by `staffing-service` (`../staffing-service`) and `alert-level-service` (`../alert-level-service`) — see [Integration contracts](../README.md#integration-contracts) in the root README for the endpoint shapes.

## Project structure

```
ward-service/
├── pom.xml
└── src/main/java/co/wethinkcode/healthsafe/
    ├── WardServiceApp.java
    └── mq/
        └── MqConfig.java
```

## Build

```
mvn package
```

## Run

```
java -jar target/ward-service.jar
```

Listens on port `7031`.

## Test

No automated tests yet. Manually verify it's up:

```
curl http://localhost:7031/health   # -> OK
```

To add real tests, add JUnit 5 + the Surefire plugin to `pom.xml`, put tests under
`src/test/java/co/wethinkcode/healthsafe/`, and run `mvn test`.

## API Endpoints
### cURL commands to ensure the api endpoints work as intended 
You can test the running server using the following `curl` commands.
*Note: If you are on Linux/macOS, you can pipe the output to `jq` (e.g., `curl -s http://localhost:7031/wards | jq`) to pretty-print the JSON response.*
**First start up the ingestion-service server**

**Check Server Health**
```bash
curl -X GET http://localhost:7031/health
```

**Get all wards**
```bash
curl -X GET http://localhost:7031/wards
```

**Get a specific ward by id**
replace "TEST-1" with a valid ID from the csv file
```bash
curl -X GET http://localhost:7031/wards/TEST-1
```

**Get a specific ward that does not exist and see the Error 404**
```bash
curl -i -X GET http://localhost:7031/wards/INVALID-ID
```

**Get all unique departments**
```bash
curl -X GET http://localhost:7031/departments
```



