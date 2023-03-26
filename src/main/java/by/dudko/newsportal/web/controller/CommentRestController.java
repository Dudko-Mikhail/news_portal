package by.dudko.newsportal.web.controller;

import by.dudko.newsportal.dto.PageResponse;
import by.dudko.newsportal.dto.comment.CommentCreateEditDto;
import by.dudko.newsportal.dto.comment.CommentReadDto;
import by.dudko.newsportal.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
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

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CommentRestController {
    private final CommentService commentService;

    @GetMapping("/comments/{id}")
    public CommentReadDto findById(@PathVariable long id) {
        return commentService.findById(id);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @GetMapping("/users/{userId}/comments")
    public PageResponse<CommentReadDto> findAllByUserId(@PathVariable long userId, Pageable pageable) {
        return commentService.findAllByUserId(userId, pageable);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @GetMapping("/news/{newsId}/comments")
    public PageResponse<CommentReadDto> findAllByNewsId(@PathVariable long newsId, Pageable pageable) {
        return commentService.findAllByNewsId(newsId, pageable);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/news/{newsId}/comments")
    public CommentReadDto create(@PathVariable long newsId,
                                 @RequestBody @Validated CommentCreateEditDto createEditDto) {
        return commentService.saveByNewsId(newsId, createEditDto);
    }

    @PutMapping("/comments/{id}")
    public CommentReadDto update(@PathVariable long id,
                                 @RequestBody @Validated CommentCreateEditDto createEditDto) {
        return commentService.updateById(id, createEditDto);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/comments/{id}")
    public void delete(@PathVariable long id) {
        commentService.deleteById(id);
    }
}
