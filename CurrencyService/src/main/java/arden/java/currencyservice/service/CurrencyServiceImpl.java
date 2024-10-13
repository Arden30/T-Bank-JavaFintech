package arden.java.currencyservice.service;

import arden.java.currencyservice.api.dto.request.CurrencyConvertRequest;
import arden.java.currencyservice.api.dto.response.CurrencyConvertResponse;
import arden.java.currencyservice.api.dto.response.CurrencyRateResponse;
import arden.java.currencyservice.api.dto.response.CurrencyRateResponse.CurrencyRateWithIDResponse;
import arden.java.currencyservice.client.CurrencyWebClient;
import arden.java.currencyservice.exception.CurrencyException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Currency;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CurrencyServiceImpl implements CurrencyService {
    private final CurrencyWebClient currencyRateWebClient;
    private final XmlMapper xmlMapper;
    private static final String RUB = "RUB";
    
    @Override
    public CurrencyRateResponse getCurrencyRate(String code) {
        List<CurrencyRateWithIDResponse> responseList = readAndParseCurrencyRate();
        validateCurrencyRequest(code);
        log.info("Запрос на валюту валиден, начинаю обработку");

        return responseList.stream()
                .filter(currency -> currency.id() != null && currency.charCode().equals(code))
                .map(currency -> CurrencyRateResponse.builder()
                        .rate(Double.parseDouble(currency.value().replace(",", ".")) / currency.nominal())
                        .currency(currency.charCode())
                        .build())
                .findFirst().orElseThrow(() -> new CurrencyException("Валюта не найдена в базе ЦБ: " + code, HttpStatus.NOT_FOUND));
    }

    @Override
    public CurrencyConvertResponse convertCurrency(CurrencyConvertRequest currencyConvertRequest) {
        List<CurrencyRateWithIDResponse> responseList = readAndParseCurrencyRate();

        validateCurrencyRequest(currencyConvertRequest.fromCurrency());
        validateCurrencyRequest(currencyConvertRequest.toCurrency());

        Optional<CurrencyRateWithIDResponse> fromCurrency = responseList.stream().filter(currency -> currency.id() != null && currency.charCode().equals(currencyConvertRequest.fromCurrency())).findFirst();
        Optional<CurrencyRateWithIDResponse> toCurrency = responseList.stream().filter(currency -> currency.id() != null && currency.charCode().equals(currencyConvertRequest.toCurrency())).findFirst();

        CurrencyConvertResponse convertRub = convertRub(currencyConvertRequest, fromCurrency, toCurrency);
        if (convertRub != null) {
            return convertRub;
        }

        if (fromCurrency.isPresent() && toCurrency.isPresent()) {
            Double fromValue = Double.parseDouble(fromCurrency.get().value().replace(",", ".")) / fromCurrency.get().nominal();
            Double toValue = Double.parseDouble(toCurrency.get().value().replace(",", ".")) / toCurrency.get().nominal();
            Double convertedAmount = fromValue / toValue * currencyConvertRequest.amount();

            return buildCurrencyConvertResponse(currencyConvertRequest, convertedAmount);
        } else {
            var notFoundCurrency = fromCurrency.isEmpty() ? currencyConvertRequest.fromCurrency() : currencyConvertRequest.toCurrency();
            throw new CurrencyException("Валюта не найдена в базе ЦБ: " + notFoundCurrency, HttpStatus.NOT_FOUND);
        }
    }

    private void validateCurrencyRequest(String code) {
        try {
            Currency.getInstance(code);
        } catch (IllegalArgumentException e) {
            throw new CurrencyException("Валюты не существует: " + code, HttpStatus.BAD_REQUEST);
        }
    }

    private CurrencyConvertResponse convertRub(CurrencyConvertRequest currencyConvertRequest, Optional<CurrencyRateWithIDResponse> fromCurrency, Optional<CurrencyRateWithIDResponse> toCurrency) {
        boolean fromCurrencyIsRub = currencyConvertRequest.fromCurrency().equals(RUB);
        boolean toCurrencyIsRub = currencyConvertRequest.toCurrency().equals(RUB);

        if (fromCurrencyIsRub && toCurrencyIsRub) {
            return buildCurrencyConvertResponse(currencyConvertRequest, currencyConvertRequest.amount());
        } else if (fromCurrencyIsRub) {
            return convertFromRubCurrency(currencyConvertRequest, toCurrency.get());
        } else if (toCurrencyIsRub) {
            return convertToRubCurrency(currencyConvertRequest, fromCurrency.get());
        }

        return null;
    }

    private CurrencyConvertResponse convertFromRubCurrency(CurrencyConvertRequest currencyConvertRequest, CurrencyRateWithIDResponse toCurrency) {
        return buildCurrencyConvertResponse(currencyConvertRequest, currencyConvertRequest.amount() / (Double.parseDouble(toCurrency.value().replace(",", ".")) / toCurrency.nominal()));
    }

    private CurrencyConvertResponse convertToRubCurrency(CurrencyConvertRequest currencyConvertRequest, CurrencyRateWithIDResponse fromCurrency) {
        return buildCurrencyConvertResponse(currencyConvertRequest, Double.parseDouble(fromCurrency.value().replace(",", ".")) * currencyConvertRequest.amount());
    }

    private CurrencyConvertResponse buildCurrencyConvertResponse(CurrencyConvertRequest currencyConvertRequest, Double convertedAmount) {
        log.info("Запрос на конвертацию обработан, собираю ответ");
        return CurrencyConvertResponse.builder()
                .fromCurrency(currencyConvertRequest.fromCurrency())
                .toCurrency(currencyConvertRequest.toCurrency())
                .convertedAmount(convertedAmount)
                .build();
    }

    private List<CurrencyRateWithIDResponse> readAndParseCurrencyRate() {
        try {
            return xmlMapper.readValue(currencyRateWebClient.currencyRate(), new TypeReference<>() {
            });
        } catch (JsonProcessingException e) {
            throw new CurrencyException("Сервер вернул невалидный XML-файл", HttpStatus.BAD_REQUEST);
        }
    }
}
