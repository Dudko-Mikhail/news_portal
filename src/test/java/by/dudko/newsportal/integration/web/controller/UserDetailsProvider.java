package by.dudko.newsportal.integration.web.controller;

import by.dudko.newsportal.dto.user.UserDetailsImpl;
import by.dudko.newsportal.model.User;
import lombok.experimental.UtilityClass;

import static by.dudko.newsportal.model.User.Role;

@UtilityClass
public class UserDetailsProvider {
    public static final UserDetailsImpl ADMIN;
    public static final UserDetailsImpl JOURNALIST;
    public static final UserDetailsImpl SUBSCRIBER;
    public static final UserDetailsImpl DELETED;

    static {
        ADMIN = UserDetailsImpl.of(
                User.builder()
                        .id(1L)
                        .username("admin")
                        .role(User.Role.ADMIN)
                        .build());
        JOURNALIST = UserDetailsImpl.of(
                User.builder()
                        .id(2L)
                        .username("journalist")
                        .role(Role.JOURNALIST)
                        .build());
        SUBSCRIBER = UserDetailsImpl.of(
                User.builder()
                        .id(3L)
                        .username("subscriber")
                        .role(Role.SUBSCRIBER)
                        .build());
        DELETED = UserDetailsImpl.of(
                User.builder()
                        .id(4L)
                        .username("deleted")
                        .role(Role.ADMIN)
                        .deleted(true)
                        .build());
    }
}
