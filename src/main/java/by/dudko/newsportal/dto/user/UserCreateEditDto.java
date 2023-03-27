package by.dudko.newsportal.dto.user;

import by.dudko.newsportal.model.User;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Value;
import org.hibernate.validator.constraints.Length;

@Value
@Builder
public class UserCreateEditDto { // todo unique username
    @NotEmpty
    @Length(max = 40)
    String username;

    @Length(max = 20)
    String name;

    @Length(max = 20)
    String surname;

    @Length(max = 20)
    String parentName;

    @NotNull
    User.Role role;
}
