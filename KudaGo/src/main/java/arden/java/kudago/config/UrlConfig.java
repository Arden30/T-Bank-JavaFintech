package arden.java.kudago.config;

import lombok.NonNull;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "base-config")
public record UrlConfig(
        @NonNull
        String kudaGoUrl,
        @NonNull
        String currencyUrl
) {
}
