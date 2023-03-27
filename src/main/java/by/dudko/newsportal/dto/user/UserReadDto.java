package by.dudko.newsportal.dto.user;

import by.dudko.newsportal.model.User;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class UserReadDto {
    long id;
    String username;
    String name;
    String surname;
    String parentName;
    User.Role role;
}
