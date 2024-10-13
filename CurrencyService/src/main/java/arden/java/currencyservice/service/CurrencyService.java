package arden.java.currencyservice.service;

import arden.java.currencyservice.api.dto.request.CurrencyConvertRequest;
import arden.java.currencyservice.api.dto.response.CurrencyConvertResponse;
import arden.java.currencyservice.api.dto.response.CurrencyRateResponse;

public interface CurrencyService {
    CurrencyRateResponse getCurrencyRate(String code);

    CurrencyConvertResponse convertCurrency(CurrencyConvertRequest currencyConvertRequest);
}
