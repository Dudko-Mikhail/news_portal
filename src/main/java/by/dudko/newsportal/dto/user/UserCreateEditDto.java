package by.dudko.newsportal.dto.user;

import by.dudko.newsportal.model.User;
import by.dudko.newsportal.validation.annotation.UniqueFieldValue;
import by.dudko.newsportal.validation.groups.CreateAction;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserCreateEditDto {
    @NotEmpty
    @UniqueFieldValue(entityClass = User.class, fieldName = "username", groups = CreateAction.class)
    @Length(max = 40)
    private String username;

    @Length(max = 20)
    private String name;

    @Length(max = 20)
    private String surname;

    @Length(max = 20)
    private String parentName;

    @NotNull(groups = CreateAction.class)
    private User.Role role;
}
