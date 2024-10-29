package arden.java.kudago.config;

import lombok.NonNull;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties(prefix = "multithreading")
public record ThreadsConfig(
        @NonNull
        Integer threadsNum,
        @NonNull
        Duration periodInSeconds,
        @NonNull
        Duration delayInSeconds,
        @NonNull
        Integer maxNumOfThreads
) {
}
