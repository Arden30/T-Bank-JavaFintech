package arden.java.kudago.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public class IdNotFoundException extends GeneralException {
    private final String description = "Id not found";
    private final HttpStatusCode httpStatusCode = HttpStatus.NOT_FOUND;

    public IdNotFoundException(final String message) {
        super(message);
    }
}
