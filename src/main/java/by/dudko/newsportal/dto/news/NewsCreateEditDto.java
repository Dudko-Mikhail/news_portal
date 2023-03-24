package by.dudko.newsportal.dto.news;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewsCreateEditDto {
    @NotEmpty
    @Length(max = 150)
    private String title;

    @NotEmpty
    @Length(max = 2000)
    private String text;
}
