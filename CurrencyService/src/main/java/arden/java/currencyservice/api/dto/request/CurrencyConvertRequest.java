package arden.java.currencyservice.api.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
@Schema(description = "DTO класс для запроса на конвертацию стоимости денег из одной валюты в другую")
public record CurrencyConvertRequest(
        @Schema(description = "Код исходной валюты", example = "USD")
        @NotBlank(message = "Исходная валюта для конвертации не указана")
        String fromCurrency,

        @Schema(description = "Код целевой валюты", example = "AUD")
        @NotBlank(message = "Целевая валюта для конвертации не указана")
        String toCurrency,

        @Schema(description = "Сумма денег для конвертации", example = "100")
        @Min(value = 0, message = "Количество валюты для конвертации должно быть неотрицательным числом")
        Double amount
) {
}
