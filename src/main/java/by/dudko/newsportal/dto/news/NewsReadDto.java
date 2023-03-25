package by.dudko.newsportal.dto.news;

import by.dudko.newsportal.dto.PageResponse;
import by.dudko.newsportal.dto.comment.CommentReadDto;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NewsReadDto {
    private long id;
    private String title;
    private String text;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private PageResponse<CommentReadDto> comments;
}
