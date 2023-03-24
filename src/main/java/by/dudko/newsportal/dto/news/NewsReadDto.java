package by.dudko.newsportal.dto.news;

import by.dudko.newsportal.dto.PageResponse;
import by.dudko.newsportal.dto.comment.CommentReadDto;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class NewsReadDto {
    long id;
    String title;
    String text;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    PageResponse<CommentReadDto> comments;
}
