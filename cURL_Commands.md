### 1. Ingestion Service (`ingestion-service`) — Port 7030

#### Health Check
Verify the service is running and healthy:
```bash
curl -i http://localhost:7030/health
```
To display it in a beautiful way:
```bash
curl -s http://localhost:7030/wards | jq .
```

### 2. Ward Service (`ward-service`) — Port 7031

#### Health Check
Verify the service is running and healthy:
```bash
curl -i http://localhost:7031/health
```
Get ward by ID
```bash
curl -i http://localhost:7031/wards/W-01
```

Get all Departments 
```bash
curl -i http://localhost:7031/departments
```

Report Equipment Failure Post request with json payload
```bash
curl -i -X POST http://localhost:7031/equipment-failure \
  -H "Content-Type: application/json" \
  -d '{"wardId": "W-01", "equipment": "Ventilator-03", "issue": "Power supply failure"}'
```

### 3. Alert Level Service (`alert-level-service`) — Port 7032

#### Health Check
Verify the service is running and healthy:
```bash
curl -i http://localhost:7032/health
```

Get current Emergency alert level 
```bash
curl -i http://localhost:7032/alert-level
```

set emergency alert level (Post request with Path variable)
```bash
curl -i -X POST http://localhost:7032/alert-level/5
```


### 4. Staffing Service (`staffing-service`) — Port 7033

#### Health Check
Verify the service is running and healthy:
```bash
curl -i http://localhost:7033/health
```

Get on-call schedule for a ward
```bash
curl -i http://localhost:7033/schedule/W-01
```

Publish Staffing Update Event