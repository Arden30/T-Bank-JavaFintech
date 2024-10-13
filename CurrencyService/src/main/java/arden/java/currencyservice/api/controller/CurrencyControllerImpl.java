package arden.java.currencyservice.api.controller;

import arden.java.currencyservice.api.dto.request.CurrencyConvertRequest;
import arden.java.currencyservice.api.dto.response.CurrencyConvertResponse;
import arden.java.currencyservice.api.dto.response.CurrencyRateResponse;
import arden.java.currencyservice.service.CurrencyService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/currencies")
@RequiredArgsConstructor
@Tag(name = "Контроллер валют", description = "Делает запросы к API ЦБ для получения информации по курсу валюты или осуществляет конвертацию денежной суммы из одной валюты в другую")
public class CurrencyControllerImpl implements CurrencyController {
    private final CurrencyService currencyService;

    @Override
    @GetMapping("/rates/{code}")
    public ResponseEntity<CurrencyRateResponse> rate(@PathVariable @Parameter(description = "Идентификатор валюты") String code) {
        return ResponseEntity.ok(currencyService.getCurrencyRate(code));
    }

    @Override
    @PostMapping("/convert")
    public ResponseEntity<CurrencyConvertResponse> convert(@RequestBody @Valid CurrencyConvertRequest request) {
        return ResponseEntity.ok(currencyService.convertCurrency(request));
    }
}
