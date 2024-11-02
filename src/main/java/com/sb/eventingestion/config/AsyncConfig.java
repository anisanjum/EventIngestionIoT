package com.sb.eventingestion.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * Configuration class for enabling and configuring asynchronous task execution.
 * <p>
 * This class defines a thread pool executor that allows for executing methods asynchronously
 * in a separate thread, improving the scalability and responsiveness of the application.
 */

@Configuration
@EnableAsync
public class AsyncConfig {

    /**
     * Defines a ThreadPoolTaskExecutor bean for asynchronous execution.
     * <p>
     * The executor is configured with the following settings:
     * - Core pool size: 100 threads
     * - Maximum pool size: 200 threads
     * - Queue capacity: 1000 tasks
     * - Thread name prefix: "Async-"
     *
     * @return an Executor instance configured for asynchronous tasks
     */
    
    @Bean(name = "asyncExecutor")
    public Executor asyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(100);
        executor.setMaxPoolSize(200);
        executor.setQueueCapacity(1000);
        executor.setThreadNamePrefix("Async-");
        executor.initialize();
        return executor;
    }
}
