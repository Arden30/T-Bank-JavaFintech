package arden.java.kudago.config;

import lombok.NonNull;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "multithreading")
public record ThreadsConfig(
        @NonNull
        Integer threadsNum,
        @NonNull
        Long periodInSeconds,
        @NonNull
        Long delayInSeconds,
        @NonNull
        Integer maxNumOfThreads
) {
}
