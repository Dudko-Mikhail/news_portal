package by.dudko.newsportal.web.controller;

import by.dudko.newsportal.dto.PageResponse;
import by.dudko.newsportal.dto.user.UserChangePasswordDto;
import by.dudko.newsportal.dto.user.UserCreateEditDto;
import by.dudko.newsportal.dto.user.UserReadDto;
import by.dudko.newsportal.model.User.Role;
import by.dudko.newsportal.service.UserService;
import by.dudko.newsportal.validation.groups.CreateAction;
import jakarta.validation.groups.Default;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserRestController {
    private final UserService userService;

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping
    public PageResponse<UserReadDto> findAllActiveUsers(Pageable pageable) {
        return userService.findAllActiveUsers(pageable);
    }

    @PreAuthorize("hasAuthority('ADMIN') || principal.id == #id")
    @GetMapping("/{id}")
    public UserReadDto findById(@PathVariable long id) {
        return userService.findById(id);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public UserReadDto create(@RequestBody @Validated({Default.class,
            CreateAction.class}) UserCreateEditDto createEditDto) {
        return userService.save(createEditDto);
    }

    @PreAuthorize("hasAuthority('ADMIN') || principal.id == #id")
    @PutMapping("/{id}")
    public UserReadDto update(@PathVariable long id, @RequestBody @Validated UserCreateEditDto createEditDto,
                              @AuthenticationPrincipal(expression = "role") Role currentRole) {
        if (currentRole != Role.ADMIN) { // only admin can change user role
            createEditDto.setRole(currentRole);
        }
        return userService.updateById(id, createEditDto);
    }

    @PreAuthorize("hasAuthority('ADMIN') || principal.id == #id")
    @PostMapping("/{id}/password")
    public void changePassword(@PathVariable long id, @RequestBody @Validated UserChangePasswordDto changePasswordDto) {
        if (!userService.changePassword(id, changePasswordDto)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void delete(@PathVariable long id) {
        userService.deleteById(id);
    }
}
