package arden.java.currencyservice.api.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(description = "DTO класс ответа на запрос по курсу валюты")
public record CurrencyRateResponse(
        @Schema(description = "Код валюты", example = "USD")
        String currency,

        @Schema(description = "Курс валюты (стоимость номинальной единицы в рублях)", example = "150.2")
        Double rate
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record CurrencyRateWithIDResponse(
            @JacksonXmlProperty(isAttribute = true, localName = "ID")
            String id,

            @JacksonXmlProperty(localName = "CharCode")
            String charCode,

            @JacksonXmlProperty(localName = "Value")
            String value,

            @JacksonXmlProperty(localName = "Nominal")
            Integer nominal
    ) {
    }
}
