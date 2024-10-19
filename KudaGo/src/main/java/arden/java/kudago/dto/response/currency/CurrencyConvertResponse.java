package arden.java.kudago.dto.response.currency;

public record CurrencyConvertResponse(
        String fromCurrency,
        String toCurrency,
        Double convertedAmount
) {
}
