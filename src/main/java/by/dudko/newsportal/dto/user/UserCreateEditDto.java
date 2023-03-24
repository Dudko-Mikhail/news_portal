package by.dudko.newsportal.dto.user;

import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Value;
import org.hibernate.validator.constraints.Length;

@Value
@Builder
public class UserCreateEditDto { // todo uniquie username, valid role
    @NotEmpty
    @Length(max = 40)
    String username;

    @Length(max = 20)
    String name;

    @Length(max = 20)
    String surname;

    @Length(max = 20)
    String parentName;

    @NotEmpty
    String role;
}
