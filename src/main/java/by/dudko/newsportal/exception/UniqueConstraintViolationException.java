package by.dudko.newsportal.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
@Getter
@RequiredArgsConstructor(staticName = "of")
public class UniqueConstraintViolationException extends RuntimeException {
    private final String fieldName;
    private final String fieldValue;

    @Override
    public String getMessage() {
        return String.format("Value [%s] for field with name [%s] already exists.", fieldValue, fieldName);
    }
}
