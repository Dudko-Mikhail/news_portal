package by.dudko.newsportal.dto.comment;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class CommentReadDto {
    long id;
    String text;
    long ownerId;
}
