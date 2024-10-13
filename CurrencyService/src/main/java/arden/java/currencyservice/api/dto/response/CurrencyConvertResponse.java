package arden.java.currencyservice.api.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(description = "DTO класс ответа конвертации стоимости денег из одной валюты в другую")
public record CurrencyConvertResponse(
        @Schema(description = "Код исходной валюты", example = "USD")
        String fromCurrency,

        @Schema(description = "Код целевой валюты", example = "AUD")
        String toCurrency,

        @Schema(description = "Конвертированная сумма денег", example = "200")
        Double convertedAmount
) {
}
