package com.sb.eventingestion.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;


import java.time.LocalDateTime;

@Data
public class DeviceEventRequest {

    @JsonProperty("device_id")
    @NotBlank(message = "Device ID cannot be blank")
    private String deviceId;

    @JsonProperty("event_type")
    @NotBlank(message = "Event type cannot be blank")
    private String eventType;

    @JsonProperty("timestamp")
    @NotNull(message = "Timestamp is required")
    private LocalDateTime timestamp;

    @JsonProperty("deviceData")
    @NotNull(message = "Data field is required")
    private DeviceData deviceData;

    @lombok.Data
    public static class DeviceData {

        @JsonProperty("temperature")
        @NotNull(message = "Temperature is required")
        private Double temperature;

        @JsonProperty("humidity")
        @NotNull(message = "Humidity is required")
        private Double humidity;

        @JsonProperty("battery_level")
        @NotNull(message = "Battery level is required")
        private Double batteryLevel;
    }
}
