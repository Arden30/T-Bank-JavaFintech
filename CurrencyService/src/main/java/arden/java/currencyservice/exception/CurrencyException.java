package arden.java.currencyservice.exception;

import lombok.Getter;
import org.springframework.http.HttpStatusCode;

import java.util.HashMap;
import java.util.Map;

@Getter
public class CurrencyException extends RuntimeException {
    private final HttpStatusCode httpStatusCode;
    private final Map<String, String> headers = new HashMap<>();

    public CurrencyException(String message, HttpStatusCode httpStatusCode) {
        super(message);
        this.httpStatusCode = httpStatusCode;
    }

    public CurrencyException withHeader(String key, String value) {
        this.headers.put(key, value);
        return this;
    }
}
