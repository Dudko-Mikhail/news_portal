package by.dudko.newsportal.service.impl;

import by.dudko.newsportal.dto.PageResponse;
import by.dudko.newsportal.dto.user.UserChangePasswordDto;
import by.dudko.newsportal.dto.user.UserCreateEditDto;
import by.dudko.newsportal.dto.user.UserReadDto;
import by.dudko.newsportal.exception.EntityNotFoundException;
import by.dudko.newsportal.mapper.UserMapper;
import by.dudko.newsportal.model.User;
import by.dudko.newsportal.repository.NewsRepository;
import by.dudko.newsportal.repository.UserRepository;
import by.dudko.newsportal.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final NewsRepository newsRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    @Override
    public PageResponse<UserReadDto> findAllActiveUsers(Pageable pageable) {
        return PageResponse.of(userRepository.findAllByDeletedIsFalse(pageable)
                .map(userMapper::toReadDto));
    }

    @Override
    public UserReadDto findById(long id) {
        return userRepository.findById(id)
                .map(userMapper::toReadDto)
                .orElseThrow(() -> EntityNotFoundException.byId(User.class, id));
    }

    @Transactional
    @Override
    public UserReadDto save(UserCreateEditDto createEditDto) {
        return Optional.of(createEditDto)
                .map(userMapper::toUser)
                .map(user -> { // random password generation
                    var password = passwordEncoder.encode(UUID.randomUUID().toString());
                    user.setPassword(password);
                    return user;
                })
                .map(userRepository::saveAndFlush)
                .map(userMapper::toReadDto)
                .orElseThrow();
    }

    @Transactional
    @Override
    public UserReadDto updateById(long id, UserCreateEditDto createEditDto) {
        return userRepository.findById(id)
                .map(user -> userMapper.toUser(createEditDto, user))
                .map(userMapper::toReadDto)
                .orElseThrow(() -> EntityNotFoundException.byId(User.class, id));
    }

    @Transactional
    @Override
    public boolean changePassword(long id, UserChangePasswordDto changePasswordDto) {
        return userRepository.findById(id)
                .map(user -> {
                    if (!passwordEncoder.matches(changePasswordDto.oldPassword(), user.getPassword())) {
                        return false;
                    }
                    user.setPassword(passwordEncoder.encode(changePasswordDto.newPassword()));
                    return true;
                })
                .orElseThrow(() -> EntityNotFoundException.byId(User.class, id));
    }

    @Transactional
    @Override
    public void deleteById(long id) {
        var user = userRepository.findById(id)
                .orElseThrow(() -> EntityNotFoundException.byId(User.class, id));
        userRepository.delete(user);
        newsRepository.deleteAllByIdInBatch(newsRepository.findAllNewsIdByOwnerId(id));
        userRepository.flush();
    }
}
