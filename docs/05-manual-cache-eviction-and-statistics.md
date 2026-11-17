
# Manual Cache Eviction and Cache Statistics

## Objective

Understand manual Redis cache eviction and monitor cache hit and miss counters through Spring Boot REST APIs.

## Endpoints Tested

- GET `/api/doctors/{id}`
- DELETE `/api/doctors/{id}/cache`
- GET `/api/doctors/cache/stats`

## Experiment

Doctor ID: 102

Doctor: Dr. Mehta

Specialization: Dermatology

Consultation Fee: 600.0

### Steps

1. Requested doctor 102 to populate the cache.
2. Verified the Redis key using EXISTS.
3. Invoked the manual cache eviction endpoint.
4. Verified that the Redis key was deleted.
5. Requested doctor 102 again.
6. Verified that the application fetched the doctor from the repository.
7. Confirmed that Redis was populated again.
8. Compared cache statistics before and after eviction.

## Verification Results

| Operation | Result |
|---|---|
| Initial cache existence | EXISTS = 1 |
| Manual eviction | cacheEvicted = true |
| Cache after eviction | EXISTS = 0 |
| Subsequent GET | Doctor data returned |
| Cache after GET | EXISTS = 1 |
| Initial cache hits | 2 |
| Final cache hits | 2 |
| Initial cache misses | 2 |
| Final cache misses | 4 |

## Cache Statistics

Cache hit ratio:

Hits / (Hits + Misses) × 100

Observed hit ratio:

2 / (2 + 4) × 100 = 33.33%

## Key Learnings

- Manual cache eviction removes a cached entry explicitly.
- Eviction is useful when cached data must be refreshed.
- A subsequent GET after eviction results in a cache miss.
- Cache statistics help monitor cache effectiveness.
- Cache hit ratio measures how often requests are served from cache.
- Manual eviction does not increment hit or miss counters in the current implementation.

## Status

Completed and verified.
