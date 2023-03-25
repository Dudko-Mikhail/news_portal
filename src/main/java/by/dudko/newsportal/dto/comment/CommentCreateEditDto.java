package by.dudko.newsportal.dto.comment;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Data
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
public class CommentCreateEditDto {
    @NotEmpty
    @Length(max = 300)
    private String text;
}
