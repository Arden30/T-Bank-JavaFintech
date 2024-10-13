package arden.java.kudago.exception;

public record ApiErrorResponse(
        String description,
        String code,
        String exceptionName,
        String exceptionMessage) {
}
