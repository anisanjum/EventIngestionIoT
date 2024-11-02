package com.sb.eventingestion.config;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Bucket4j;
import io.github.bucket4j.Refill;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Configuration class for managing rate limiting based on device IDs.
 * <p>
 * This class uses the Bucket4j library to implement in-memory rate limiting,
 * allowing up to 100 requests per minute per device ID. It stores a
 * separate bucket for each device ID to track the request counts and
 * enforce the rate limits.
 */

@Configuration
public class RateLimitConfig {

    // In-memory map to keep track of rate limits for each device_id
    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    /**
     * Resolves a Bucket for the given device ID. If a Bucket does not exist for
     * the specified device ID, a new Bucket is created with a rate limit of
     * 100 requests per minute per device.
     *
     * @param deviceId the unique identifier for the device
     * @return the Bucket associated with the given device ID
     */
    public Bucket resolveBucket(String deviceId) {
        return buckets.computeIfAbsent(deviceId, key -> {
            Bandwidth limit = Bandwidth.classic(100, Refill.greedy(100, Duration.ofMinutes(1)));
            return Bucket4j.builder().addLimit(limit).build();
        });
    }
}


