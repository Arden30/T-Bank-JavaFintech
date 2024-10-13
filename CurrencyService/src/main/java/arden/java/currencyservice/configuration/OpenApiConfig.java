package arden.java.currencyservice.configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;

@OpenAPIDefinition(
        info = @Info(
                title = "Currency Service Api",
                description = "Currency Service", version = "1.0.0",
                contact = @Contact(
                        name = "Arsenev Denis",
                        email = "denis@gmail.com"
                )
        )
)
public class OpenApiConfig {
}
