package by.dudko.newsportal.service.impl;

import by.dudko.newsportal.dto.user.UserDetailsImpl;
import by.dudko.newsportal.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetailsImpl loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .map(UserDetailsImpl::of)
                .orElseThrow(() -> new UsernameNotFoundException("Username: %s not found".formatted(username)));
    }
}
