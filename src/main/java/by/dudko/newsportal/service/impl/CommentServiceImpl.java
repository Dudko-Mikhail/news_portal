package by.dudko.newsportal.service.impl;

import by.dudko.newsportal.dto.PageResponse;
import by.dudko.newsportal.dto.comment.CommentCreateEditDto;
import by.dudko.newsportal.dto.comment.CommentReadDto;
import by.dudko.newsportal.exception.EntityNotFoundException;
import by.dudko.newsportal.mapper.CommentMapper;
import by.dudko.newsportal.model.Comment;
import by.dudko.newsportal.model.News;
import by.dudko.newsportal.repository.CommentRepository;
import by.dudko.newsportal.repository.NewsRepository;
import by.dudko.newsportal.service.CommentService;
import by.dudko.newsportal.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final NewsRepository newsRepository;
    private final UserService userService;
    private final CommentMapper commentMapper;

    @Override
    public PageResponse<CommentReadDto> findAllByUserId(long userId, Pageable pageable) {
        userService.findById(userId);
        return PageResponse.of(commentRepository.findAllByOwnerId(userId, pageable)
                .map(commentMapper::toReadDto));
    }

    @Override
    public PageResponse<CommentReadDto> findAllByNewsId(long newsId, Pageable pageable) {
        if (!newsRepository.existsById(newsId)) {
            throw EntityNotFoundException.byId(News.class, newsId);
        }
        return PageResponse.of(commentRepository.findAllByNewsId(newsId, pageable)
                .map(commentMapper::toReadDto));
    }

    @Override
    public CommentReadDto findById(long id) {
        return commentRepository.findById(id)
                .map(commentMapper::toReadDto)
                .orElseThrow(() -> EntityNotFoundException.byId(Comment.class, id));
    }

    @Transactional
    @Override
    public CommentReadDto save(long newsId, CommentCreateEditDto createEditDto) {
        return newsRepository.findById(newsId)
                .map(news -> {
                    var comment = commentMapper.toComment(createEditDto);
                    comment.setNews(news);
                    return comment;
                })
                .map(commentRepository::saveAndFlush)
                .map(commentMapper::toReadDto)
                .orElseThrow(() -> EntityNotFoundException.byId(News.class, newsId));

    }

    @Transactional
    @Override
    public CommentReadDto updateById(long id, CommentCreateEditDto createEditDto) {
        return commentRepository.findById(id)
                .map(comment -> commentMapper.toComment(createEditDto, comment))
                .map(commentMapper::toReadDto)
                .orElseThrow(() -> EntityNotFoundException.byId(Comment.class, id));
    }

    @Transactional
    @Override
    public void deleteById(long id) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> EntityNotFoundException.byId(Comment.class, id));
        commentRepository.delete(comment);
    }
}
