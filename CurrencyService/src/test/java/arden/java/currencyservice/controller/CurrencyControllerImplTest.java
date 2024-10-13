package arden.java.currencyservice.controller;

import arden.java.currencyservice.api.controller.CurrencyControllerImpl;
import arden.java.currencyservice.api.dto.request.CurrencyConvertRequest;
import arden.java.currencyservice.api.dto.response.CurrencyConvertResponse;
import arden.java.currencyservice.api.dto.response.CurrencyRateResponse;
import arden.java.currencyservice.exception.CurrencyException;
import arden.java.currencyservice.service.CurrencyService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest({CurrencyControllerImpl.class})
public class CurrencyControllerImplTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private CurrencyService currencyService;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Запрос на курс валюты: Валидная валюта, возвращается её стоимость")
    public void rate_ValidCurrency_ReturnsOk() throws Exception {
        when(currencyService.getCurrencyRate("USD")).thenReturn(CurrencyRateResponse.builder()
                .currency("USD")
                .rate(100D)
                .build());

        mockMvc.perform(get("/api/v1/currencies/rates/USD"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.currency").value("USD"))
                .andExpect(jsonPath("$.rate").value(100D));
    }

    @Test
    @DisplayName("Запрос на курс валюты: Невалидная валюта, ошибка")
    public void rate_InvalidCurrency_ReturnsBadRequest() throws Exception {
        when(currencyService.getCurrencyRate("HHH")).thenThrow(new CurrencyException("Валюты не существует: HHH", HttpStatus.BAD_REQUEST));

        mockMvc.perform(get("/api/v1/currencies/rates/HHH"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Валюты не существует: HHH"));
    }

    @Test
    @DisplayName("Запрос на конвертацию: валюты валидны, возвращается сконвертированная стоимость")
    public void convert_ValidCurrency_ReturnsOk() throws Exception {
        var request = CurrencyConvertRequest.builder()
                .fromCurrency("USD")
                .toCurrency("AUD")
                .amount(100D)
                .build();

        when(currencyService.convertCurrency(request))
                .thenReturn(CurrencyConvertResponse.builder().fromCurrency("USD")
                        .toCurrency("AUD")
                        .convertedAmount(200D)
                        .build());

        mockMvc.perform(post("/api/v1/currencies/convert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.convertedAmount").value(200D));
    }


    @Test
    @DisplayName("Запрос на конвертацию: валюта невалидна, ошибка")
    public void convert_InvalidCurrency_ReturnsBadRequest() throws Exception {
        var request = CurrencyConvertRequest.builder()
                .fromCurrency("HHH")
                .toCurrency("AUD")
                .amount(100D)
                .build();

        when(currencyService.convertCurrency(request))
                .thenThrow(new CurrencyException("Валюта не валидна: HHH", HttpStatus.BAD_REQUEST));

        mockMvc.perform(post("/api/v1/currencies/convert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Валюта не валидна: HHH"));
    }

    @Test
    @DisplayName("Запрос на конвертацию: стоимость отрицательная, ошибка")
    public void convert_NegativeAmount_ReturnsBadRequest() throws Exception {
        var request = CurrencyConvertRequest.builder()
                .fromCurrency("USD")
                .toCurrency("AUD")
                .amount(-100D)
                .build();

        when(currencyService.convertCurrency(request))
                .thenThrow(new CurrencyException("Стоимость не может быть отрицательной", HttpStatus.BAD_REQUEST));

        mockMvc.perform(post("/api/v1/currencies/convert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Количество валюты для конвертации должно быть неотрицательным числом"));
    }
}
