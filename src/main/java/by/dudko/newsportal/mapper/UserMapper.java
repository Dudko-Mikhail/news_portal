package by.dudko.newsportal.mapper;

import by.dudko.newsportal.dto.user.UserCreateEditDto;
import by.dudko.newsportal.dto.user.UserReadDto;
import by.dudko.newsportal.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {
    default User toUser(UserCreateEditDto createEditDto) {
        return toUser(createEditDto, new User());
    }

    @Mapping(target = "password", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "news", ignore = true)
    @Mapping(target = "comments", ignore = true)
    @Mapping(target = "creationDate", ignore = true)
    @Mapping(target = "lastEditDate", ignore = true)
    @Mapping(target = "role", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    User toUser(UserCreateEditDto createEditDto, @MappingTarget User user);

    UserReadDto toReadDto(User user);
}
