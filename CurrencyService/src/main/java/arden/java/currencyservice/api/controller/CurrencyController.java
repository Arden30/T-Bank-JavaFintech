package arden.java.currencyservice.api.controller;

import arden.java.currencyservice.api.dto.request.CurrencyConvertRequest;
import arden.java.currencyservice.api.dto.response.CurrencyConvertResponse;
import arden.java.currencyservice.api.dto.response.CurrencyRateResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/currencies")
@RequiredArgsConstructor
public class CurrencyController {
    @GetMapping("/rates/{code}")
    public ResponseEntity<CurrencyRateResponse> rate(@PathVariable String code) {
        return null;
    }

    @PostMapping("/currencies/convert")
    public ResponseEntity<CurrencyConvertResponse> convert(@RequestBody CurrencyConvertRequest request) {
        return null;
    }
}