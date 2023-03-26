package by.dudko.newsportal.integration.service;

import by.dudko.newsportal.dto.PageResponse;
import by.dudko.newsportal.dto.user.UserChangePasswordDto;
import by.dudko.newsportal.dto.user.UserCreateEditDto;
import by.dudko.newsportal.dto.user.UserReadDto;
import by.dudko.newsportal.exception.EntityNotFoundException;
import by.dudko.newsportal.integration.IntegrationTest;
import by.dudko.newsportal.integration.TestConfigurationWithFakeAuditorAware;
import by.dudko.newsportal.model.User;
import by.dudko.newsportal.repository.UserRepository;
import by.dudko.newsportal.service.UserService;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static by.dudko.newsportal.dto.PageResponse.Metadata;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@IntegrationTest(classes = TestConfigurationWithFakeAuditorAware.class)
@RequiredArgsConstructor
class UserServiceIntegrationTest {
    private static final long USER_ID = 1L;
    private static final long NON_EXISTENT_USER_ID = -1L;
    private static final String USER_NOT_FOUND_MESSAGE = EntityNotFoundException.byId(User.class, NON_EXISTENT_USER_ID)
            .getMessage();

    private final UserService userService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Test
    void findAll() {
        Metadata expectedMetadata = Metadata.builder()
                .page(0)
                .size(20)
                .numberOfElements(3)
                .totalElements(3)
                .totalPages(1)
                .build();

        PageResponse<UserReadDto> response = userService.findAllActiveUsers(PageRequest.ofSize(20));
        List<UserReadDto> content = response.getContent();

        assertThat(response.getMetadata()).isEqualTo(expectedMetadata);
        assertThat(content).hasSize(3);
    }

    @Test
    void findById() {
        UserReadDto searchResult = userService.findById(USER_ID);

        assertAll(
                () -> assertThat(searchResult.getId()).isEqualTo(USER_ID),
                () -> assertThat(searchResult.getUsername()).isEqualTo("admin"),
                () -> assertThat(searchResult.getName()).isEqualTo("Ivan"),
                () -> assertThat(searchResult.getSurname()).isEqualTo("Ivanov"),
                () -> assertThat(searchResult.getParentName()).isEqualTo("Ivanovich"),
                () -> assertThat(searchResult.getRole()).isEqualTo(User.Role.ADMIN.getAuthority())
        );
    }

    @Test
    void findByIdWithNonExistentUserId() {
        assertThrows(EntityNotFoundException.class, () -> userService.findById(NON_EXISTENT_USER_ID));
    }

    @Test
    void save() {
        UserCreateEditDto newUser = UserCreateEditDto.builder()
                .username("zebra")
                .name("Ivan")
                .surname("Ivanov")
                .role(User.Role.JOURNALIST.getAuthority())
                .build();

        UserReadDto savedUser = userService.save(newUser);

        assertThat(userRepository.findById(savedUser.getId())).isPresent();
        assertAll(
                () -> assertThat(savedUser.getUsername()).isEqualTo(newUser.getUsername()),
                () -> assertThat(savedUser.getName()).isEqualTo(newUser.getName()),
                () -> assertThat(savedUser.getSurname()).isEqualTo(newUser.getSurname()),
                () -> assertNull(savedUser.getParentName()),
                () -> assertThat(savedUser.getRole()).isEqualTo(newUser.getRole())
        );
    }

    @Test
    void updateById() {
        UserCreateEditDto newUserInfo = UserCreateEditDto.builder()
                .username("zebra")
                .name("Ivan")
                .surname("Ivanov")
                .role(User.Role.JOURNALIST.getAuthority())
                .build();

        UserReadDto updatedUser = userService.updateById(USER_ID, newUserInfo);

        assertAll(
                () -> assertThat(updatedUser.getId()).isEqualTo(USER_ID),
                () -> assertThat(updatedUser.getUsername()).isEqualTo(newUserInfo.getUsername()),
                () -> assertThat(updatedUser.getName()).isEqualTo(newUserInfo.getName()),
                () -> assertThat(updatedUser.getSurname()).isEqualTo(newUserInfo.getSurname()),
                () -> assertNull(updatedUser.getParentName()),
                () -> assertThat(updatedUser.getRole()).isEqualTo(newUserInfo.getRole())
        );
    }

    @Test
    void updateByIdWithNonExistentUserId() {
        UserCreateEditDto newUserInfo = UserCreateEditDto.builder()
                .build();

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> userService.updateById(NON_EXISTENT_USER_ID, newUserInfo));
        assertThat(exception.getMessage()).isEqualTo(USER_NOT_FOUND_MESSAGE);
    }

    @Test
    void changePasswordWithValidOldPassword() {
        UserChangePasswordDto passwordInfo = new UserChangePasswordDto("1111", "9999");

        boolean isChanged = userService.changePassword(USER_ID, passwordInfo);

        assertTrue(isChanged);
        String encodedNewPassword = userRepository.findById(USER_ID).get().getPassword();
        assertTrue(passwordEncoder.matches(passwordInfo.newPassword(), encodedNewPassword));
    }

    @Test
    void changePasswordWithNonExistentUserId() {
        UserChangePasswordDto changePasswordDto = new UserChangePasswordDto("1111", "9999");

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> userService.changePassword(NON_EXISTENT_USER_ID, changePasswordDto));
        assertThat(exception.getMessage()).isEqualTo(USER_NOT_FOUND_MESSAGE);
    }

    @Test
    void changePasswordWithInvalidOldPassword() {
        String validOldPassword = "1111";
        String encodedPassword = userRepository.findById(USER_ID).get().getPassword();
        UserChangePasswordDto passwordInfo = new UserChangePasswordDto("2222", "9999");

        boolean isChanged = userService.changePassword(USER_ID, passwordInfo);

        assertFalse(isChanged);
        assertTrue(passwordEncoder.matches(validOldPassword, encodedPassword));
    }

    @Test
    void deleteById() {
        assertDoesNotThrow(() -> userService.deleteById(USER_ID));

        Optional<User> deletedUser = userRepository.findById(USER_ID);
        assertThat(deletedUser).isPresent();
        deletedUser.ifPresent(user -> assertTrue(user.isDeleted()));
    }

    @Test
    void deleteByIdWithNonExistentUserId() {
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> userService.deleteById(NON_EXISTENT_USER_ID));
        assertThat(exception.getMessage()).isEqualTo(USER_NOT_FOUND_MESSAGE);
    }
}
