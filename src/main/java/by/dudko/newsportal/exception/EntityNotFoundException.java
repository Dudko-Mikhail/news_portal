package by.dudko.newsportal.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
@Getter
@RequiredArgsConstructor(staticName = "of")
public class EntityNotFoundException extends RuntimeException {
    private final Class<?> entityClass;
    private final String fieldName;
    private final String fieldValue;

    public static EntityNotFoundException byId(Class<?> entityClass, Object id) {
        return EntityNotFoundException.of(entityClass, "id", id.toString());
    }

    @Override
    public String getMessage() {
        return String.format("Entity from class: [%s] not found by field named: [%s] by value: [%s].",
                entityClass.getSimpleName(), fieldName, fieldValue);
    }
}
