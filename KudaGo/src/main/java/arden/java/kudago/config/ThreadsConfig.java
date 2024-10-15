package arden.java.kudago.config;

import lombok.NonNull;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "multithreading")
public record ThreadsConfig(
        @NonNull
        Integer fixedPoolSize,
        @NonNull
        Integer scheduledPoolSize,
        @NonNull
        Long periodInSeconds,
        @NonNull
        Long delayInSeconds
) {
}
