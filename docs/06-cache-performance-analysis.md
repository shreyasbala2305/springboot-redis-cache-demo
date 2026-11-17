
# Cache Performance Analysis

## Objective

Compare direct in-memory repository access against Redis access using RedisTemplate and measure average access latency.

## Benchmark Configuration

- Doctor ID: 101
- Iterations per benchmark: 1,000
- Warm-up iterations: 100
- Repository: ConcurrentHashMap
- Cache: Redis
- Measurement: System.nanoTime()

## Benchmark Results

| Run | Repository Average (ms) | Redis Average (ms) |
|---|---:|---:|
| 1 | 0.0019618 | 1.1576173 |
| 2 | 0.0007478 | 0.8451993 |
| 3 | 0.0005409 | 0.7090996 |
| 4 | 0.0005077 | 0.6585319 |
| Average | 0.00093955 | 0.842612025 |

## Analysis

Redis took approximately 897 times longer than the in-memory repository lookup in this local benchmark.

This is expected because:

- ConcurrentHashMap performs local in-memory access.
- Redis requires communication between the application and Redis server.
- Redis access includes serialization and network overhead.

## Key Learnings

- Redis is not necessarily faster than local in-memory data structures.
- Redis provides centralized caching across application instances.
- Redis supports TTL and multiple data structures.
- JVM warm-up can influence benchmark measurements.
- Repeated benchmark runs provide more useful information than a single measurement.
- Production performance testing should use realistic workloads and concurrency.

## Limitations

- The repository is an in-memory ConcurrentHashMap, not a relational database.
- The benchmark uses sequential operations.
- Results are specific to the local development environment.
- Measurements are not production performance guarantees.
- The benchmark measures repository and Redis access separately, not the complete Cache-Aside request lifecycle.

## Status

Completed and verified.
