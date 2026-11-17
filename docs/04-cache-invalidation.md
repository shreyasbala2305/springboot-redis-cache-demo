
# Cache Invalidation on Data Update

## Objective

Verify that updating source data invalidates the corresponding Redis cache entry and ensures subsequent requests return fresh data.

## Implementation

The `DoctorCacheService` follows the Cache-Aside write strategy:

1. Update the doctor in the repository.
2. Delete the corresponding Redis cache entry.
3. On the next GET request, retrieve fresh data from the repository.
4. Store the updated doctor in Redis.

## Experiment

Doctor ID: 101

Initial consultation fee: 800.0

Updated consultation fee: 1500.0

### Verification Results

| Operation | Result |
|---|---|
| Initial GET | Fee = 800.0 |
| Cache populated | EXISTS = 1 |
| PUT update | Fee = 1500.0 |
| Cache invalidation | EXISTS = 0 |
| Subsequent GET | Fee = 1500.0 |
| Redis stored JSON | Updated fee = 1500.0 |

## Application Logs

```text
CACHE HIT: cache:doctor:101
CACHE INVALIDATED: cache:doctor:101
CACHE MISS: cache:doctor:101
CACHE WRITE: cache:doctor:101
```

## Key Learnings

- Updating the repository does not automatically update Redis.
- Cache invalidation prevents stale cached data from being served.
- The next read repopulates Redis with fresh repository data.
- Cache invalidation is an important part of maintaining cache consistency.
- Cache-Aside is a common strategy for read-heavy applications.

## Status

Completed and verified.
