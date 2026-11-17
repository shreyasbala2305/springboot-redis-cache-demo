# Spring Boot Redis Demo — Testing Strategy

## 1. Objective

Verify that the Spring Boot Redis caching application behaves correctly at the service and REST API layers.

The tests cover cache-aside behavior, Redis operations, cache invalidation, cache statistics, REST request validation, and performance endpoint responses.

## 2. Testing Tools

- **JUnit 5** — test execution and assertions
- **Mockito** — mocking dependencies and verifying interactions
- **Spring Boot Test** — application testing support
- **MockMvc** — REST controller testing without starting a real HTTP server
- **Maven Wrapper** — repeatable test execution

## 3. Test Coverage

### Service layer

**DoctorCacheServiceTest**
- Returns a doctor when the Redis cache contains the requested doctor.
- Fetches from the repository when the cache misses.
- Returns null when the doctor does not exist.
- Updates a doctor and invalidates the corresponding cache entry.

**RedisServiceTest**
- Stores a value with a TTL.
- Retrieves a stored value.
- Returns true when a key is deleted.
- Returns false when the key does not exist.

### Controller layer

**DoctorControllerTest**
- Retrieves a doctor.
- Returns 404 when the doctor is not found.
- Updates a doctor.
- Rejects mismatched doctor IDs.
- Evicts a doctor's cache entry.
- Returns cache statistics.

**RedisControllerTest**
- Stores a Redis value.
- Applies the default TTL when one is not supplied.
- Rejects an invalid TTL.
- Retrieves a stored value.
- Returns 404 for a missing value.
- Deletes a stored value.
- Returns 404 when deleting a missing key.

**CachePerformanceControllerTest**
- Returns performance analysis results.
- Uses default parameters when query parameters are omitted.

### Application context

**SpringbootRedisCacheDemoApplicationTests**
- Verifies that the Spring application context loads successfully.

## 4. Full Test Execution

Run the complete test suite from the project root:

```powershell
.\mvnw.cmd clean test
```

### Verified result

- Command: `.\mvnw.cmd clean test`
- Build result: **BUILD SUCCESS**
- Individual test classes were also executed during development.

## 5. Testing Approach

- Mock external dependencies when testing service and controller behavior.
- Verify interactions with repositories and Redis abstractions.
- Test successful responses and error scenarios.
- Validate cache TTL and invalidation behavior.
- Run the complete suite after individual tests to check for regressions.

## 6. Important Learning

- Unit tests help isolate service-layer business logic.
- Controller tests verify HTTP status codes, request handling, and response contracts.
- Mocking avoids requiring a live Redis instance for these isolated tests.
- A clean full-suite run provides confidence that the application changes work together.

## 7. Status

**Completed** — individual test classes and the full Maven clean test build passed.
