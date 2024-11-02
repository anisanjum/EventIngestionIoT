package com.sb.eventingestion;

import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

public class RateLimitTest {

    public static void main(String[] args) {
        String url = "http://localhost:8080/api/events/ratelimit";

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> request = getHttpEntity(headers);

        for (int i = 1; i < 110; i++) {
            try {
                // Using exchange method to get response entity with status code
                ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, request, String.class);
                if (response.getStatusCodeValue() == 200) {
                    System.out.println("Response " + i + ": 200 - " + response.getBody());
                }
            } catch (Exception e) {
                System.out.println("Error on request " + i + ": " + e.getMessage());
            }

            // Delay between requests to avoid hitting the rate limit
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private static HttpEntity<String> getHttpEntity(HttpHeaders headers) {
        String requestBody = """
                [
                    {
                        "device_id": "device123",
                        "event_type": "temperature_update",
                        "timestamp": "2024-10-29T12:00:00Z",
                        "deviceData": {
                            "temperature": 24.5,
                            "humidity": 55.0,
                            "battery_level": 85.0
                        }
                    },
                    {
                        "device_id": "device456",
                        "event_type": "humidity_update",
                        "timestamp": "2024-10-29T12:01:00Z",
                        "deviceData": {
                            "temperature": 22.0,
                            "humidity": 60.0,
                            "battery_level": 90.0
                        }
                    }
                ]
                """;

        return new HttpEntity<>(requestBody, headers);
    }
}

