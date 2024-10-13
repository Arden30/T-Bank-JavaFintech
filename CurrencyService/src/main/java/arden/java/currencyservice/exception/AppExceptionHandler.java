package arden.java.currencyservice.exception;

import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class AppExceptionHandler {
    @ExceptionHandler(CurrencyException.class)
    public ResponseEntity<ApiErrorResponse> handleCurrencyException(CurrencyException e) {
        ApiErrorResponse apiErrorResponse = ApiErrorResponse.builder()
                .message(e.getMessage())
                .code(e.getHttpStatusCode().value())
                .build();

        HttpHeaders headers = new HttpHeaders();
        for (Map.Entry<String, String> entry : e.getHeaders().entrySet()) {
            headers.add(entry.getKey(), entry.getValue());
        }

        return new ResponseEntity<>(apiErrorResponse, headers, HttpStatus.resolve(e.getHttpStatusCode().value()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        String message = e.getBindingResult()
                .getAllErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.joining("; "));

        ApiErrorResponse apiErrorResponse = ApiErrorResponse.builder()
                .message(message)
                .code(e.getStatusCode().value())
                .build();

        return new ResponseEntity<>(apiErrorResponse, HttpStatus.resolve(e.getStatusCode().value()));
    }
}
