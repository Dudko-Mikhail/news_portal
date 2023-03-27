package by.dudko.newsportal.dto.user;

import by.dudko.newsportal.model.User;
import by.dudko.newsportal.validation.annotation.UniqueFieldValue;
import by.dudko.newsportal.validation.groups.CreateAction;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Value;
import org.hibernate.validator.constraints.Length;

@Value
@Builder
public class UserCreateEditDto {
    @NotEmpty
    @UniqueFieldValue(entityClass = User.class, fieldName = "username", groups = CreateAction.class)
    @Length(max = 40)
    String username;

    @Length(max = 20)
    String name;

    @Length(max = 20)
    String surname;

    @Length(max = 20)
    String parentName;

    @NotNull(groups = CreateAction.class)
    User.Role role;
}
