package by.dudko.newsportal.service;

import by.dudko.newsportal.dto.PageResponse;
import by.dudko.newsportal.dto.comment.CommentCreateEditDto;
import by.dudko.newsportal.dto.comment.CommentReadDto;
import org.springframework.data.domain.Pageable;

public interface CommentService {
    PageResponse<CommentReadDto> findAllByUserId(long userId, Pageable pageable);

    PageResponse<CommentReadDto> findAllByNewsId(long newsId, Pageable pageable);

    CommentReadDto findById(long id);

    CommentReadDto saveByNewsId(long newsId, CommentCreateEditDto createEditDto);

    CommentReadDto updateById(long id, CommentCreateEditDto createEditDto);

    void deleteById(long id);
}
