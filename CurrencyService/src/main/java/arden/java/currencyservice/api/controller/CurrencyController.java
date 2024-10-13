package arden.java.currencyservice.api.controller;

import arden.java.currencyservice.api.dto.request.CurrencyConvertRequest;
import arden.java.currencyservice.api.dto.response.CurrencyConvertResponse;
import arden.java.currencyservice.api.dto.response.CurrencyRateResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

public interface CurrencyController {
    @Operation(
            summary = "Получение информации по валюте",
            description = "Позволяет узнать курс валюты по отношению к российскому рублю"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Курс найден",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = CurrencyRateResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Валюты не существует",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Валюта не найдена в базе ЦБ",
                    content = @Content),
            @ApiResponse(responseCode = "503", description = "Сервер не доступен",
                    content = @Content)})
    ResponseEntity<CurrencyRateResponse> rate(@PathVariable @Parameter(description = "Идентификатор валюты") String code);

    @Operation(
            summary = "Конвертация валюты",
            description = "Позволяет конвертировать сумму из одной валюты в другую"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Курс найден",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = CurrencyConvertResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Валюты не существует/Стоимость не может быть отрицательной",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Валюта не найдена в базе ЦБ",
                    content = @Content),
            @ApiResponse(responseCode = "503", description = "Сервер не доступен",
                    content = @Content)})
    ResponseEntity<CurrencyConvertResponse> convert(@RequestBody @Valid CurrencyConvertRequest request);
}
