package com.sb.eventingestion;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ConcurrentLoadTest {

    @Autowired
    private TestRestTemplate restTemplate;

    private static final String URL = "http://localhost:8080/api/events/ratelimit";
    private static final int NUMBER_OF_REQUESTS = 1000;

    @Test
    public void testConcurrentRequests() throws InterruptedException {
        // Prepare request body for the test
        List<String> requests = prepareRequestBodies(NUMBER_OF_REQUESTS);

        ExecutorService executorService = Executors.newFixedThreadPool(100); // Pool of 100 threads
        CountDownLatch latch = new CountDownLatch(NUMBER_OF_REQUESTS);
        List<String> responses = new ArrayList<>();

        for (String requestBody : requests) {
            executorService.submit(() -> {
                try {
                    HttpHeaders headers = new HttpHeaders();
                    headers.setContentType(MediaType.APPLICATION_JSON);
                    HttpEntity<String> request = new HttpEntity<>(requestBody, headers);
                    String response = restTemplate.postForObject(URL, request, String.class);

                    synchronized (responses) {
                        responses.add(response);
                        // Log response to see what's returned
                        System.out.println("Response: " + response);
                    }
                } catch (Exception e) {
                    // Log any exceptions encountered
                    System.err.println("Error during request: " + e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await(); // Wait for all requests to finish
        executorService.shutdown(); // Shutdown the executor

        System.out.println(responses.size());

        // Validate responses
        assertEquals(NUMBER_OF_REQUESTS, responses.size(), "Expected 1000 responses but got " + responses.size());
    }


    private List<String> prepareRequestBodies(int numberOfRequests) {
        List<String> requestBodies = new ArrayList<>();
        // Alternate between two device IDs
        String deviceId1 = "device123";
        String deviceId2 = "device456";

        for (int i = 0; i < numberOfRequests; i++) {
            String deviceId = (i % 2 == 0) ? deviceId1 : deviceId2; // Alternate device IDs
            String requestBody = String.format("""
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
            """, deviceId);
            requestBodies.add(requestBody);
        }
        return requestBodies;
    }
}


