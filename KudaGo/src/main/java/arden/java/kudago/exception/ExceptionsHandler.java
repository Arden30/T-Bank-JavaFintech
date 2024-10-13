package arden.java.kudago.exception;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.Arrays;

@RestControllerAdvice
public class ExceptionsHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        HttpStatusCode statusCode = e.getStatusCode();
        String description = Arrays.toString(e.getDetailMessageArguments());
        ApiErrorResponse apiErrorResponse =
                buildDefaultErrorResponse(statusCode, description, e);

        return ResponseEntity.status(statusCode).body(apiErrorResponse);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiErrorResponse> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
        String description = e.getMessage();
        ApiErrorResponse apiErrorResponse =
                buildDefaultErrorResponse(HttpStatusCode.valueOf(400), description, e);

        return ResponseEntity.status(HttpStatusCode.valueOf(400)).body(apiErrorResponse);
    }

    @ExceptionHandler(HttpClientErrorException.class)
    public ResponseEntity<ApiErrorResponse> handleHttpException(HttpClientErrorException exception) {
        ApiErrorResponse apiErrorResponse = buildDefaultErrorResponse(exception.getStatusCode(),
                exception.getMessage(), exception
        );

        return ResponseEntity.status(exception.getStatusCode()).body(apiErrorResponse);
    }

    @ExceptionHandler(GeneralException.class)
    public ResponseEntity<ApiErrorResponse> handleGeneralException(GeneralException exception) {
        ApiErrorResponse apiErrorResponse = buildDefaultErrorResponse(exception.getHttpStatusCode(),
                exception.getDescription(), exception
        );

        return ResponseEntity.status(exception.getHttpStatusCode()).body(apiErrorResponse);
    }

    private ApiErrorResponse buildDefaultErrorResponse(
            HttpStatusCode statusCode,
            String description,
            Exception exception
    ) {
        String exceptionName = exception.getClass().getSimpleName();
        String exceptionMessage = exception.getMessage();

        return new ApiErrorResponse(
                description,
                statusCode.toString(),
                exceptionName,
                exceptionMessage
        );
    }
}
