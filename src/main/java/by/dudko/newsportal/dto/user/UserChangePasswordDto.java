package by.dudko.newsportal.dto.user;

import jakarta.validation.constraints.NotEmpty;
import org.hibernate.validator.constraints.Length;

public record UserChangePasswordDto(@NotEmpty @Length(max = 80) String oldPassword,
                                    @NotEmpty @Length(max = 80) String newPassword) {
}
