package arden.java.kudago.config;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.*;

@Configuration
@RequiredArgsConstructor
public class ThreadPoolConfiguration {
    private final ThreadsConfig threadsConfig;

    @Bean
    public ExecutorService fixedThreadPool() {
        ThreadFactory threadFactory = new ThreadFactoryBuilder()
                .setNameFormat("Data-processor-%d")
                .build();

        return Executors.newFixedThreadPool(threadsConfig.threadsNum(), threadFactory);
    }

    @Bean
    public ScheduledExecutorService scheduledThreadPool() {
        ThreadFactory threadFactory = new ThreadFactoryBuilder()
                .setNameFormat("Scheduler-%d")
                .build();

        return Executors.newScheduledThreadPool(threadsConfig.threadsNum(), threadFactory);
    }

    @Bean
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setThreadNamePrefix("SuitableEvents-");
        executor.initialize();
        return executor;
    }

    @Bean
    public Semaphore semaphore() {
        return new Semaphore(threadsConfig.maxNumOfThreads());
    }
}
