# Redis TTL and Automatic Cache Expiration

## Objective

Understand how Redis TTL (Time To Live) works and verify that
expired cache entries are automatically removed and reloaded
by the Spring Boot Cache-Aside implementation.

## Configuration

The DoctorCacheService uses a configurable cache TTL.

For this experiment, the TTL was temporarily reduced from
300 seconds to 20 seconds.

```java
private static final long CACHE_TTL_SECONDS = 20;