package com.sb.eventingestion.entity;


import jakarta.persistence.Embeddable;
import lombok.Data;

@Embeddable
@Data
public class DeviceDataEntity {

    private Double temperature;
    private Double humidity;
    private Double batteryLevel;

//    // Getters and Setters
//    public Double getTemperature() {
//        return temperature;
//    }
//
//    public void setTemperature(Double temperature) {
//        this.temperature = temperature;
//    }
//
//    public Double getHumidity() {
//        return humidity;
//    }
//
//    public void setHumidity(Double humidity) {
//        this.humidity = humidity;
//    }
//
//    public Double getBatteryLevel() {
//        return batteryLevel;
//    }
//
//    public void setBatteryLevel(Double batteryLevel) {
//        this.batteryLevel = batteryLevel;
//    }
}
