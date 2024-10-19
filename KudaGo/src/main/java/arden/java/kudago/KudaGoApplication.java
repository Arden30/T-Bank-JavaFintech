package arden.java.kudago;

import arden.java.kudago.config.ThreadsConfig;
import arden.java.kudago.config.UrlConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableConfigurationProperties({UrlConfig.class, ThreadsConfig.class})
@EnableAsync
public class KudaGoApplication {

    public static void main(String[] args) {
        SpringApplication.run(KudaGoApplication.class, args);
    }

}
