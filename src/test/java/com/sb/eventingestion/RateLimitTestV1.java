package com.sb.eventingestion;

import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

public class RateLimitTestV1 {

    public static void main(String[] args) {
        String url = "http://localhost:8080/api/events/ratelimit";
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Loop to simulate 110 requests for each of two device IDs
        for (int i = 1; i <= 110; i++) {
            // Alternate between two device IDs for each request
            String deviceId = (i % 2 == 0) ? "device123" : "device456";
            HttpEntity<String> request = getHttpEntity(headers, deviceId);

            try {
                ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, request, String.class);
                if (response.getStatusCodeValue() == 200) {
                    System.out.println("Response for " + deviceId + " " + i + ": 200 - " + response.getBody());
                } else if (response.getStatusCodeValue() == 429) {
                    System.out.println("Response for " + deviceId + " " + i + ": 429 - Rate Limit Exceeded");
                }
            } catch (HttpClientErrorException.TooManyRequests e) {
                System.out.println("Error for " + deviceId + " on request " + i + ": Rate limit exceeded (429)");
            } catch (Exception e) {
                System.out.println("Error for " + deviceId + " on request " + i + ": " + e.getMessage());
            }

            // Delay between requests to distribute requests more evenly
//            try {
//                Thread.sleep(100); // 600ms delay to allow more controlled request flow
//            } catch (InterruptedException e) {
//                Thread.currentThread().interrupt();
//            }
        }
    }

    private static HttpEntity<String> getHttpEntity(HttpHeaders headers, String deviceId) {
        String requestBody = """
                [
                    {
                        "device_id": "%s",
                        "event_type": "temperature_update",
                        "timestamp": "2024-10-29T12:00:00Z",
                        "deviceData": {
                            "temperature": 24.5,
                            "humidity": 55.0,
                            "battery_level": 85.0
                        }
                    }
                ]
                """.formatted(deviceId);

        return new HttpEntity<>(requestBody, headers);
    }
}
