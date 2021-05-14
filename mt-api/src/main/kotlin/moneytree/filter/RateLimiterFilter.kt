package moneytree.filter

import io.github.resilience4j.ratelimiter.RateLimiter
import io.github.resilience4j.ratelimiter.RateLimiterConfig
import java.time.Duration
import org.http4k.filter.ResilienceFilters

val rateLimiterConfig: RateLimiterConfig = RateLimiterConfig.custom()
    .limitRefreshPeriod(Duration.ofSeconds(1))
    .limitForPeriod(1)
    .timeoutDuration(Duration.ofMillis(10)).build()

val rateLimiterFilter = ResilienceFilters.RateLimit(RateLimiter.of("rateLimiter", rateLimiterConfig))
