# Spring Boot Redis Cache Demo

A hands-on Spring Boot project demonstrating Redis integration, cache-aside architecture, TTL-based expiration, cache invalidation, manual eviction, cache statistics, and performance analysis.

The project focuses on understanding how Redis can improve data access patterns in backend applications and how caching should be implemented, tested, and maintained.

## Project Objectives

- Integrate Redis with Spring Boot.
- Implement the Cache-Aside (Lazy Loading) pattern.
- Store and retrieve Java objects using Redis.
- Configure TTL-based cache expiration.
- Handle cache invalidation after data updates.
- Implement manual cache eviction.
- Track cache hits and misses.
- Analyze repository access versus Redis access performance.
- Write unit and controller tests using JUnit 5, Mockito, and MockMvc.

## Technology Stack

| Technology | Purpose |
|---|---|
| Java 25 | Application development runtime |
| Spring Boot 4.1.2 | Backend application framework |
| Spring Data Redis | Redis integration |
| Redis | In-memory data store |
| Maven Wrapper | Build and dependency management |
| Docker | Running Redis locally |
| JUnit 5 | Unit testing |
| Mockito | Mocking dependencies |
| MockMvc | REST controller testing |
| YAML | Application configuration |

## Architecture

The application follows a layered architecture:

```text
Client
  |
  v
REST Controllers
  |
  +----------------------+
  |                      |
  v                      v
DoctorController     RedisController
  |                      |
  v                      v
DoctorCacheService    RedisService
  |                      |
  +----------+-----------+
             |
             v
          Redis
             |
             v
      DoctorRepository
      (In-memory data)
```

The doctor cache workflow uses Redis as the cache layer and an in-memory `ConcurrentHashMap` repository as the data source.

**Important:** The repository is intentionally in-memory for this learning project. It is not a production relational database.

## Cache-Aside Pattern

The project implements the Cache-Aside pattern for doctor retrieval.

```text
Client requests doctor
        |
        v
Check Redis cache
        |
        v
   Cache hit?
    /     \
  Yes      No
   |        |
   v        v
Return    Query repository
cached        |
doctor        v
          Doctor found?
           /      \
         Yes       No
          |         |
          v         v
      Store in    Return
      Redis       not found
          |
          v
      Return doctor
```

### Cache key convention

```text
cache:doctor:{doctorId}
```

Example:

```text
cache:doctor:101
```

### Cache TTL

The doctor cache uses a default TTL of **300 seconds (5 minutes)**.

After expiration, Redis removes the cached entry. The next request fetches the doctor from the repository and repopulates the cache.

## Implemented Features

### 1. Redis Fundamentals

- Redis string operations.
- Object serialization and deserialization.
- Redis key management.
- TTL configuration.
- Redis CLI experimentation.

### 2. Doctor Cache Management

- Cache-first doctor retrieval.
- Repository fallback on cache miss.
- Redis cache population.
- Cache invalidation after doctor updates.
- Manual cache eviction.

### 3. Cache Statistics

The application tracks:

- Cache hits.
- Cache misses.

Endpoint:

```http
GET /api/doctors/cache/stats
```

Example response:

```json
{
  "cacheHits": 2,
  "cacheMisses": 4
}
```

These counters are maintained in application memory and reset when the application restarts.

### 4. Cache Performance Analysis

The project includes a benchmark endpoint comparing:

- In-memory `ConcurrentHashMap` repository access.
- Redis access through `RedisTemplate`.

Endpoint:

```http
GET /api/performance/cache?doctorId=101&iterations=1000
```

Example response structure:

```json
{
  "doctorId": 101,
  "iterations": 1000,
  "repositoryAverageMs": 0.001,
  "redisAverageMs": 0.8,
  "repositoryAccessType": "In-memory ConcurrentHashMap",
  "redisAccessType": "Redis via RedisTemplate"
}
```

The values above are illustrative, not fixed benchmark results.

**Benchmark limitation:** This is a local learning benchmark, not a production database-versus-Redis comparison. The in-memory repository avoids network and database overhead, while Redis access includes Redis communication and serialization costs. Results vary with machine load, environment, and iteration count.

## Project Structure

```text
springboot-redis-cache-demo/
│
├── docs/
│   ├── 01-redis-fundamentals.md
│   ├── 02-redis-ttl-expiration.md
│   ├── 03-cache-invalidation.md
│   ├── 05-manual-cache-eviction-and-statistics.md
│   ├── 06-cache-performance-analysis.md
│   └── 07-testing-strategy.md
│
├── src/
│   ├── main/
│   │   ├── java/com/backendengineering/redis_cache_demo/
│   │   │   ├── config/
│   │   │   │   └── RedisConfig.java
│   │   │   ├── controller/
│   │   │   │   ├── DoctorController.java
│   │   │   │   ├── RedisController.java
│   │   │   │   └── CachePerformanceController.java
│   │   │   ├── model/
│   │   │   │   └── Doctor.java
│   │   │   ├── repository/
│   │   │   │   └── DoctorRepository.java
│   │   │   ├── service/
│   │   │   │   ├── DoctorCacheService.java
│   │   │   │   ├── RedisService.java
│   │   │   │   └── CachePerformanceService.java
│   │   │   └── SpringbootRedisCacheDemoApplication.java
│   │   │
│   │   └── resources/
│   │       └── application.yml
│   │
│   └── test/
│       └── java/com/backendengineering/redis_cache_demo/
│           ├── controller/
│           │   ├── DoctorControllerTest.java
│           │   ├── RedisControllerTest.java
│           │   └── CachePerformanceControllerTest.java
│           ├── service/
│           │   ├── DoctorCacheServiceTest.java
│           │   └── RedisServiceTest.java
│           └── SpringbootRedisCacheDemoApplicationTests.java
│
├── .gitignore
├── mvnw
├── mvnw.cmd
├── pom.xml
└── README.md
```

*Note: Adjust the documentation filenames in this tree if your actual `docs/` directory uses different names.*

## Prerequisites

- Java 25 or compatible configured Java runtime.
- Docker Desktop.
- Git.

Maven is included through the Maven Wrapper, so a separate global Maven installation is not required.

## Running Redis with Docker

Start a Redis container:

```powershell
docker run --name redis-learning -d -p 6379:6379 redis
```

Check the running container:

```powershell
docker ps
```

Open the Redis CLI:

```powershell
docker exec -it redis-learning redis-cli
```

Test the connection:

```redis
PING
```

Expected output:

```text
PONG
```

If the container already exists but is stopped:

```powershell
docker start redis-learning
```

## Application Configuration

File:

`src/main/resources/application.yml`

```yaml
spring:
  application:
    name: springboot-redis-cache-demo
  data:
    redis:
      host: localhost
      port: 6379
      timeout: 2s

server:
  port: 8080
```

## Run the Application

Clone the repository:

```powershell
git clone <your-github-repository-url>
```

Navigate into the project:

```powershell
cd springboot-redis-cache-demo
```

Start the application:

```powershell
.\mvnw.cmd spring-boot:run
```

Application base URL:

```text
http://localhost:8080
```

## API Endpoints

### Redis Operations

| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/redis/{key}` | Store a string value with TTL |
| GET | `/api/redis/{key}` | Retrieve a value |
| DELETE | `/api/redis/{key}` | Delete a key |

Example:

```http
POST /api/redis/learning?value=redis-demo&ttlSeconds=60
```

```http
GET /api/redis/learning
```

```http
DELETE /api/redis/learning
```

The default TTL is 300 seconds. Non-positive TTL values are rejected.

### Doctor Operations

| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/doctors/{id}` | Retrieve a doctor using cache-aside |
| PUT | `/api/doctors/{id}` | Update doctor and invalidate cache |
| DELETE | `/api/doctors/{id}/cache` | Manually evict doctor cache |
| GET | `/api/doctors/cache/stats` | Retrieve cache hit/miss statistics |

Example:

```http
GET /api/doctors/101
```

Update a doctor:

```http
PUT /api/doctors/101
Content-Type: application/json
```

```json
{
  "id": 101,
  "name": "Dr. Sharma",
  "specialization": "Cardiology",
  "consultationFee": 1200
}
```

Manually evict the cache:

```http
DELETE /api/doctors/101/cache
```

### Performance Analysis

| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/performance/cache` | Compare repository and Redis access |

Example:

```http
GET /api/performance/cache?doctorId=101&iterations=1000
```

Default parameters:

- `doctorId`: 101
- `iterations`: 1000

## Testing

The project includes tests for:

- Doctor cache service.
- Redis service.
- Doctor REST controller.
- Redis REST controller.
- Cache performance controller.
- Spring application context.

Testing uses JUnit 5, Mockito, and MockMvc.

Run the complete test suite:

```powershell
.\mvnw.cmd clean test
```

Run a specific test class:

```powershell
.\mvnw.cmd -Dtest=DoctorCacheServiceTest test
```

## Learning Documentation

The `docs/` directory contains hands-on learning notes and experiment results covering Redis fundamentals, TTL expiration, cache invalidation, manual cache eviction, statistics, performance analysis, and testing strategy.

## Key Engineering Concepts Learned

- Cache-Aside architecture.
- Redis data structures and key management.
- TTL-based expiration.
- Cache invalidation.
- Manual cache eviction.
- Cache hit and miss tracking.
- Java object serialization.
- Spring Data Redis and `RedisTemplate`.
- Unit and controller testing.
- Benchmark methodology and limitations.

## Future Improvements

- Integrate Redis into the Telemedicine HMS microservices project.
- Replace the in-memory doctor repository with persistent database storage.
- Introduce centralized cache configuration.
- Add cache metrics and monitoring.
- Improve cache statistics for concurrent requests.
- Explore Redis-based distributed locking.
- Implement resilience and fallback behavior for Redis failures.

## Project Status

**Completed:** Standalone Spring Boot + Redis cache demonstration, cache experiments, performance analysis, documentation, and automated tests.

**Next milestone:** Integrate Redis caching into the Telemedicine HMS microservices architecture.
