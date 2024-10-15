package arden.java.kudago.config;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;

@Configuration
@RequiredArgsConstructor
public class ThreadPoolConfiguration {
    private final ThreadsConfig threadsConfig;

    @Bean
    public ExecutorService fixedThreadPool() {
        ThreadFactory threadFactory = new ThreadFactoryBuilder()
                .setNameFormat("Data-processor-%d")
                .build();

        return Executors.newFixedThreadPool(threadsConfig.fixedPoolSize(), threadFactory);
    }

    @Bean
    public ScheduledExecutorService scheduledThreadPool() {
        ThreadFactory threadFactory = new ThreadFactoryBuilder()
                .setNameFormat("Scheduler-%d")
                .build();

        return Executors.newScheduledThreadPool(threadsConfig.scheduledPoolSize(), threadFactory);
    }

    @Bean
    public Duration period() {
        return Duration.ofSeconds(threadsConfig.periodInSeconds());
    }

    @Bean
    public Duration delay() {
        return Duration.ofSeconds(threadsConfig.delayInSeconds());
    }
}
