package by.dudko.newsportal.mapper;

import by.dudko.newsportal.dto.user.UserCreateEditDto;
import by.dudko.newsportal.dto.user.UserReadDto;
import by.dudko.newsportal.model.User;
import by.dudko.newsportal.model.User.Role;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {
    default User toUser(UserCreateEditDto createEditDto) {
        return toUser(createEditDto, new User());
    }

    @Mapping(target = "password", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "role", expression = "java(fromString(createEditDto.getRole()))")
    @Mapping(target = "news", ignore = true)
    @Mapping(target = "comments", ignore = true)
    @Mapping(target = "creationDate", ignore = true)
    @Mapping(target = "lastEditDate", ignore = true)
    User toUser(UserCreateEditDto createEditDto, @MappingTarget User user);

    UserReadDto toReadDto(User user);

    default String toString(Role role) {
        return role.getAuthority();
    }

    default Role fromString(String role) {
        return Role.fromAuthority(role);
    }
}
