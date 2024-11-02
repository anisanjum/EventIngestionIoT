package com.sb.eventingestion.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "device_events")
public class DeviceEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "device_id", nullable = false)
    private String deviceId;

    @Column(name = "event_type", nullable = false)
    private String eventType;

    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;

    @Embedded
    private DeviceDataEntity deviceDataEntity;

    // Getters and setters

//    public Long getId() {
//        return id;
//    }
//
//    public void setId(Long id) {
//        this.id = id;
//    }
//
//    public String getDeviceId() {
//        return deviceId;
//    }
//
//    public void setDeviceId(String deviceId) {
//        this.deviceId = deviceId;
//    }
//
//    public String getEventType() {
//        return eventType;
//    }
//
//    public void setEventType(String eventType) {
//        this.eventType = eventType;
//    }
//
//    public LocalDateTime getTimestamp() {
//        return timestamp;
//    }
//
//    public void setTimestamp(LocalDateTime timestamp) {
//        this.timestamp = timestamp;
//    }
//
//    public DeviceData getData() {
//        return deviceData;
//    }
//
//    public void setData(DeviceData deviceData) {
//        this.deviceData = deviceData;
    }

    // Nested class for data
//    @Embeddable
//    public static class EventData {
//
//        @Column(name = "temperature")
//        private Double temperature;
//
//        @Column(name = "humidity")
//        private Double humidity;
//
//        @Column(name = "battery_level")
//        private Double batteryLevel;
//
//        // Getters and setters
//
//        public Double getTemperature() {
//            return temperature;
//        }
//
//        public void setTemperature(Double temperature) {
//            this.temperature = temperature;
//        }
//
//        public Double getHumidity() {
//            return humidity;
//        }
//
//        public void setHumidity(Double humidity) {
//            this.humidity = humidity;
//        }
//
//        public Double getBatteryLevel() {
//            return batteryLevel;
//        }
//
//        public void setBatteryLevel(Double batteryLevel) {
//            this.batteryLevel = batteryLevel;
//        }
//    }
//}
