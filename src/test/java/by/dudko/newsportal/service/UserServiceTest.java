package by.dudko.newsportal.service;

import by.dudko.newsportal.dto.PageResponse;
import by.dudko.newsportal.dto.user.UserChangePasswordDto;
import by.dudko.newsportal.dto.user.UserCreateEditDto;
import by.dudko.newsportal.dto.user.UserReadDto;
import by.dudko.newsportal.exception.EntityNotFoundException;
import by.dudko.newsportal.mapper.UserMapper;
import by.dudko.newsportal.model.User;
import by.dudko.newsportal.repository.NewsRepository;
import by.dudko.newsportal.repository.UserRepository;
import by.dudko.newsportal.service.impl.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static by.dudko.newsportal.dto.PageResponse.Metadata;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    private static final long USER_ID = 1L;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private NewsRepository newsRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void findAll() {
        User user = new User();
        UserReadDto userReadDto = UserReadDto.builder()
                .username("zebra")
                .name("Ivan")
                .surname("Ivanov")
                .role(User.Role.JOURNALIST)
                .build();
        Pageable pageable = Pageable.ofSize(20);
        Page<User> page = new PageImpl<>(List.of(user), pageable, 1);
        when(userRepository.findAllByDeletedIsFalse(pageable))
                .thenReturn(page);
        when(userMapper.toReadDto(user))
                .thenReturn(userReadDto);
        Metadata expectedMetadata = Metadata.builder()
                .page(0)
                .size(20)
                .numberOfElements(1)
                .totalElements(1)
                .totalPages(1)
                .build();

        PageResponse<UserReadDto> response = userService.findAllActiveUsers(pageable);
        List<UserReadDto> content = response.getContent();

        assertThat(response.getMetadata()).isEqualTo(expectedMetadata);
        assertThat(content).hasSize(1);
        assertThat(content.get(0)).isEqualTo(userReadDto);
        verify(userRepository).findAllByDeletedIsFalse(pageable);
        verify(userMapper).toReadDto(user);
        verifyNoMoreInteractions(userRepository, newsRepository, userMapper, passwordEncoder);
    }

    @Test
    void findById() {
        User user = new User();
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        when(userMapper.toReadDto(user)).thenReturn(UserReadDto.builder()
                .id(USER_ID)
                .build());

        UserReadDto searchResult = userService.findById(USER_ID);

        assertThat(searchResult.getId()).isEqualTo(USER_ID);
        verify(userRepository).findById(USER_ID);
        verify(userMapper).toReadDto(user);
        verifyNoMoreInteractions(userRepository, newsRepository, userMapper, passwordEncoder);
    }

    @Test
    void findByIdWithNonExistentUserId() {
        when(userRepository.findById(USER_ID))
                .thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> userService.findById(USER_ID));
        verify(userRepository).findById(USER_ID);
        verifyNoMoreInteractions(userRepository, newsRepository, userMapper, passwordEncoder);
    }

    @Test
    void save() {
        UserCreateEditDto newUser = UserCreateEditDto.builder()
                .build();
        User user = new User();
        when(userMapper.toUser(newUser))
                .thenReturn(user);
        UserReadDto savedUser = UserReadDto.builder()
                .id(10L)
                .build();
        when(userMapper.toReadDto(user))
                .thenReturn(savedUser);
        when(userRepository.saveAndFlush(user))
                .thenReturn(user);

        UserReadDto result = userService.save(newUser);

        assertThat(result.getId()).isEqualTo(10L);
        verify(userMapper).toUser(newUser);
        verify(userMapper).toReadDto(user);
        verify(userRepository).saveAndFlush(user);
        verify(passwordEncoder).encode(any(String.class));
        verifyNoMoreInteractions(userRepository, newsRepository, userMapper, passwordEncoder);
    }

    @Test
    void saveWithNullUserCreateEditDto() {
        assertThrows(NullPointerException.class, () -> userService.save(null));
    }

    @Test
    void updateById() {
        UserCreateEditDto newUserInfo = UserCreateEditDto.builder().build();
        User user = new User();
        UserReadDto updatedUser = UserReadDto.builder()
                .id(4L)
                .build();
        when(userRepository.findById(USER_ID))
                .thenReturn(Optional.of(user));
        when(userMapper.toUser(newUserInfo, user))
                .thenReturn(user);
        when(userMapper.toReadDto(user))
                .thenReturn(updatedUser);

        UserReadDto result = userService.updateById(USER_ID, newUserInfo);

        assertThat(result).isEqualTo(updatedUser);
        verify(userRepository).findById(USER_ID);
        verify(userMapper).toUser(newUserInfo, user);
        verify(userMapper).toReadDto(user);
        verifyNoMoreInteractions(userRepository, newsRepository, userMapper, passwordEncoder);
    }

    @Test
    void updateByIdWithNonExistentUserId() {
        UserCreateEditDto newUserInfo = UserCreateEditDto.builder().build();
        when(userRepository.findById(USER_ID))
                .thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> userService.updateById(USER_ID, newUserInfo));
        verify(userRepository).findById(USER_ID);
        verifyNoMoreInteractions(userRepository, newsRepository, userMapper, passwordEncoder);
    }

    @Test
    void changePassword() {
        String oldPassword = "1111";
        String newPassword = "2222";
        String encodedPassword = "qwerty";
        UserChangePasswordDto changePasswordDto = new UserChangePasswordDto(oldPassword, newPassword);
        User userWithPassword = User.builder()
                .password(encodedPassword)
                .build();
        when(userRepository.findById(USER_ID))
                .thenReturn(Optional.of(userWithPassword));
        when(passwordEncoder.matches(oldPassword, encodedPassword))
                .thenReturn(true);

        assertTrue(userService.changePassword(USER_ID, changePasswordDto));

        verify(userRepository).findById(USER_ID);
        verify(passwordEncoder).matches(oldPassword, encodedPassword);
        verify(passwordEncoder).encode(newPassword);
        verifyNoMoreInteractions(userRepository, newsRepository, userMapper, passwordEncoder);
    }

    @Test
    void changePasswordWithInvalidUserId() {
        UserChangePasswordDto changePasswordDto = new UserChangePasswordDto("1111", "2222");
        when(userRepository.findById(USER_ID))
                .thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> userService.changePassword(USER_ID, changePasswordDto));

        verify(userRepository).findById(USER_ID);
        verifyNoMoreInteractions(userRepository, newsRepository, userMapper, passwordEncoder);
    }

    @Test
    void changePasswordWithInvalidOldPassword() {
        String oldPassword = "1111";
        String newPassword = "2222";
        String encodedPassword = "qwerty";
        UserChangePasswordDto changePasswordDto = new UserChangePasswordDto(oldPassword, newPassword);
        User userWithPassword = User.builder()
                .password(encodedPassword)
                .build();
        when(userRepository.findById(USER_ID))
                .thenReturn(Optional.of(userWithPassword));
        when(passwordEncoder.matches(oldPassword, encodedPassword))
                .thenReturn(false);

        assertFalse(userService.changePassword(USER_ID, changePasswordDto));

        verify(userRepository).findById(USER_ID);
        verify(passwordEncoder).matches(oldPassword, encodedPassword);
        verifyNoMoreInteractions(userRepository, newsRepository, userMapper, passwordEncoder);
    }

    @Test
    void deleteById() {
        User user = new User();
        when(userRepository.findById(USER_ID))
                .thenReturn(Optional.of(user));
        List<Long> newsIds = List.of(1L, 2L, 3L);
        when(newsRepository.findAllNewsIdByOwnerId(USER_ID)).thenReturn(newsIds);

        assertDoesNotThrow(() -> userService.deleteById(USER_ID));
        verify(userRepository).findById(USER_ID);
        verify(userRepository).delete(user);
        verify(newsRepository).findAllNewsIdByOwnerId(USER_ID);
        verify(newsRepository).deleteAllByIdInBatch(newsIds);
        verify(userRepository).flush();
        verifyNoMoreInteractions(userRepository, newsRepository, userMapper, passwordEncoder);
    }

    @Test
    void deleteByIdWithNonExistentUserId() {
        when(userRepository.findById(USER_ID))
                .thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> userService.deleteById(USER_ID));
        verify(userRepository).findById(USER_ID);
        verifyNoMoreInteractions(userRepository, newsRepository, userMapper, passwordEncoder);
    }
}
