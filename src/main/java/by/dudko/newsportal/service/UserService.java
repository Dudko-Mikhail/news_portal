package by.dudko.newsportal.service;

import by.dudko.newsportal.dto.PageResponse;
import by.dudko.newsportal.dto.user.UserChangePasswordDto;
import by.dudko.newsportal.dto.user.UserCreateEditDto;
import by.dudko.newsportal.dto.user.UserReadDto;
import org.springframework.data.domain.Pageable;

public interface UserService {
    PageResponse<UserReadDto> findAllActiveUsers(Pageable pageable);

    UserReadDto findById(long id);

    UserReadDto save(UserCreateEditDto createEditDto);

    UserReadDto updateById(long id, UserCreateEditDto createEditDto);

    boolean changePassword(long id, UserChangePasswordDto changePasswordDto);

    void deleteById(long id);
}
