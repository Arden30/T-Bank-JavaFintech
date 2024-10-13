package arden.java.currencyservice.exception;

import lombok.Builder;

@Builder
public record ApiErrorResponse(
        int code,
        String message
) {
}
