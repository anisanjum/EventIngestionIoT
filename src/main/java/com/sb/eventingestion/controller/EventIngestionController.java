package com.sb.eventingestion.controller;

import com.sb.eventingestion.config.RateLimitConfig;
import com.sb.eventingestion.dto.DeviceEventRequest;
import com.sb.eventingestion.entity.DeviceDataEntity;
import com.sb.eventingestion.entity.DeviceEvent;
import com.sb.eventingestion.exception.BadRequestException;
import com.sb.eventingestion.repository.DeviceEventRepository;
import io.github.bucket4j.Bucket;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Controller for handling device event ingestion and rate limiting.
 * <p>
 * This controller exposes an API endpoint for ingesting events from devices.
 * It utilizes a rate limiting mechanism to ensure that each device ID can only
 * send a specified number of requests per minute.
 */

@RestController
@RequestMapping("/api/events")
@EnableAsync
public class EventIngestionController {

    private final RateLimitConfig rateLimitConfig;
    private final DeviceEventRepository deviceEventRepository;

    /**
     * Constructs an EventIngestionController with the specified rate limit configuration
     * and device event repository.
     *
     * @param rateLimitConfig       the configuration for rate limiting
     * @param deviceEventRepository the repository for device event storage
     */

    public EventIngestionController(RateLimitConfig rateLimitConfig, DeviceEventRepository deviceEventRepository) {
        this.rateLimitConfig = rateLimitConfig;
        this.deviceEventRepository = deviceEventRepository;
    }

    /**
     * Ingests a list of device event requests, processing each event and applying rate limiting.
     * <p>
     * If the rate limit is exceeded for a device ID, a corresponding error message is returned.
     *
     * @param deviceEventRequests a list of device event requests to be processed
     * @return a CompletableFuture containing the response entity, including either
     * a success status with processed events or a 429 status if rate limits were exceeded
     * @throws BadRequestException if any event request is invalid (null or missing device ID)
     */

    @Operation(summary = "Post devices by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Within Rate Limit"),
            @ApiResponse(responseCode = "429", description = "Exceeded Rate Limit")
    })
    @Async("asyncExecutor")
    @PostMapping("/ratelimit")
    public CompletableFuture<ResponseEntity<?>> ingestEvents(@Valid @RequestBody List<DeviceEventRequest> deviceEventRequests) {
        List<Map<String, Object>> responses = new ArrayList<>();
        boolean rateLimitExceeded = false;
        for (DeviceEventRequest deviceEventRequest : deviceEventRequests) {
            if (deviceEventRequest == null || deviceEventRequest.getDeviceId() == null || deviceEventRequest.getDeviceId().isEmpty()) {
                throw new BadRequestException("Invalid event request data");
            }

            Bucket bucket = rateLimitConfig.resolveBucket(deviceEventRequest.getDeviceId());

            if (bucket.tryConsume(1)) {
                // Save the device event to the database
                DeviceEvent deviceEvent = getDeviceEvent(deviceEventRequest);

                deviceEventRepository.save(deviceEvent);

                responses.add(Map.of("device_id", deviceEventRequest.getDeviceId(), "status", "success", "message", "Event processed successfully"));
            } else {
                responses.add(Map.of("device_id", deviceEventRequest.getDeviceId(), "status", "error", "message", "Too many requests. Please try again later."));
                rateLimitExceeded = true;
            }
        }

        // If any rate limit is exceeded, return 429 Too Many Requests
        if (rateLimitExceeded) {
            return CompletableFuture.completedFuture(ResponseEntity.status(429).body(responses));
        }

        return CompletableFuture.completedFuture(ResponseEntity.ok(responses));
    }

    /**
     * Converts a DeviceEventRequest into a DeviceEvent entity for storage.
     *
     * @param deviceEventRequest the request containing event data to be converted
     * @return a DeviceEvent entity populated with data from the request
     */

    private static DeviceEvent getDeviceEvent(DeviceEventRequest deviceEventRequest) {
        DeviceEvent deviceEvent = new DeviceEvent();
        deviceEvent.setDeviceId(deviceEventRequest.getDeviceId());
        deviceEvent.setEventType(deviceEventRequest.getEventType());
        deviceEvent.setTimestamp(LocalDateTime.now());
//        deviceEvent.setTimestamp(deviceEventRequest.getTimestamp());

        DeviceDataEntity deviceDataEntity = new DeviceDataEntity();
        deviceDataEntity.setTemperature(deviceEventRequest.getDeviceData().getTemperature());
        deviceDataEntity.setHumidity(deviceEventRequest.getDeviceData().getHumidity());
        deviceDataEntity.setBatteryLevel(deviceEventRequest.getDeviceData().getBatteryLevel());
        deviceEvent.setDeviceDataEntity(deviceDataEntity);
        return deviceEvent;
    }
}
