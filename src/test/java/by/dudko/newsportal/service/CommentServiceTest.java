package by.dudko.newsportal.service;

import by.dudko.newsportal.dto.PageResponse;
import by.dudko.newsportal.dto.comment.CommentCreateEditDto;
import by.dudko.newsportal.dto.comment.CommentReadDto;
import by.dudko.newsportal.exception.EntityNotFoundException;
import by.dudko.newsportal.mapper.CommentMapper;
import by.dudko.newsportal.model.Comment;
import by.dudko.newsportal.model.News;
import by.dudko.newsportal.repository.CommentRepository;
import by.dudko.newsportal.repository.NewsRepository;
import by.dudko.newsportal.repository.UserRepository;
import by.dudko.newsportal.service.impl.CommentServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {
    private final long COMMENT_ID = 1L;
    private final long NEWS_ID = 2L;
    private final long USER_ID = 3L;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private NewsRepository newsRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CommentMapper commentMapper;


    @InjectMocks
    private CommentServiceImpl commentService;

    @Test
    void findAllByUserId() {
        when(userRepository.existsById(USER_ID))
                .thenReturn(true);
        Comment comment = new Comment();
        CommentReadDto commentReadDto = CommentReadDto.builder()
                .text("interesting comment")
                .build();
        Pageable pageable = Pageable.ofSize(20);
        Page<Comment> page = new PageImpl<>(List.of(comment), pageable, 1);
        when(commentRepository.findAllByOwnerId(USER_ID, pageable))
                .thenReturn(page);
        when(commentMapper.toReadDto(comment))
                .thenReturn(commentReadDto);
        PageResponse.Metadata expectedMetadata = PageResponse.Metadata.builder()
                .page(0)
                .size(20)
                .numberOfElements(1)
                .totalElements(1)
                .totalPages(1)
                .build();

        PageResponse<CommentReadDto> response = commentService.findAllByUserId(USER_ID, pageable);
        List<CommentReadDto> content = response.getContent();

        assertThat(response.getMetadata()).isEqualTo(expectedMetadata);
        assertThat(content).hasSize(1);
        assertThat(content.get(0)).isEqualTo(commentReadDto);
        verifyNoMoreInteractions(commentRepository, newsRepository, userRepository, commentMapper);
    }

    @Test
    void findAllByUserIdWithNonExistentUserId() {
        when(userRepository.existsById(USER_ID))
                .thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> commentService.findAllByUserId(USER_ID, Pageable.unpaged()));
        verifyNoMoreInteractions(commentRepository, newsRepository, userRepository, commentMapper);
    }

    @Test
    void findAllByNewsId() {
        when(newsRepository.existsById(NEWS_ID))
                .thenReturn(true);
        Comment comment = new Comment();
        CommentReadDto commentReadDto = CommentReadDto.builder()
                .text("interesting comment")
                .build();
        Pageable pageable = Pageable.ofSize(20);
        Page<Comment> page = new PageImpl<>(List.of(comment), pageable, 1);
        when(commentRepository.findAllByNewsId(NEWS_ID, pageable))
                .thenReturn(page);
        when(commentMapper.toReadDto(comment))
                .thenReturn(commentReadDto);
        PageResponse.Metadata expectedMetadata = PageResponse.Metadata.builder()
                .page(0)
                .size(20)
                .numberOfElements(1)
                .totalElements(1)
                .totalPages(1)
                .build();

        PageResponse<CommentReadDto> response = commentService.findAllByNewsId(NEWS_ID, pageable);
        List<CommentReadDto> content = response.getContent();

        assertThat(response.getMetadata()).isEqualTo(expectedMetadata);
        assertThat(content).hasSize(1);
        assertThat(content.get(0)).isEqualTo(commentReadDto);
        verifyNoMoreInteractions(commentRepository, newsRepository, userRepository, commentMapper);
    }

    @Test
    void findAllByNewsIdWithNonExistentNewsId() {
        when(newsRepository.existsById(NEWS_ID))
                .thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> commentService.findAllByNewsId(NEWS_ID, Pageable.unpaged()));
        verifyNoMoreInteractions(commentRepository, newsRepository, userRepository, commentMapper);
    }

    @Test
    void findById() {
        Comment comment = new Comment();
        when(commentRepository.findById(COMMENT_ID)).thenReturn(Optional.of(comment));
        when(commentMapper.toReadDto(comment)).thenReturn(CommentReadDto.builder()
                .id(COMMENT_ID)
                .build());

        CommentReadDto searchResult = commentService.findById(COMMENT_ID);

        assertThat(searchResult.getId()).isEqualTo(COMMENT_ID);
        verifyNoMoreInteractions(commentRepository, newsRepository, userRepository, commentMapper);
    }

    @Test
    void findByIdWithNonExistentCommentId() {
        when(commentRepository.findById(COMMENT_ID))
                .thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> commentService.findById(COMMENT_ID));
        verifyNoMoreInteractions(commentRepository, newsRepository, userRepository, commentMapper);
    }

    @Test
    void isCommentOwnerShouldReturnTrue() {
        Comment comment = Comment.builder()
                .ownerId(USER_ID)
                .build();
        when(commentRepository.findById(COMMENT_ID))
                .thenReturn(Optional.of(comment));

        assertTrue(commentService.isCommentOwner(USER_ID, COMMENT_ID));
        verifyNoMoreInteractions(commentRepository, newsRepository, userRepository, commentMapper);
    }

    @Test
    void isCommentOwnerWithNonExistentCommentId() {
        when(commentRepository.findById(COMMENT_ID))
                .thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> commentService.isCommentOwner(USER_ID, COMMENT_ID));
        verifyNoMoreInteractions(commentRepository, newsRepository, userRepository, commentMapper);
    }

    @Test
    void isCommentOwnerShouldReturnFalse() {
        long notTheCommentOwnerId = 500;
        Comment comment = Comment.builder()
                .ownerId(notTheCommentOwnerId)
                .build();
        when(commentRepository.findById(COMMENT_ID))
                .thenReturn(Optional.of(comment));

        assertFalse(commentService.isCommentOwner(USER_ID, COMMENT_ID));
        verifyNoMoreInteractions(commentRepository, newsRepository, userRepository, commentMapper);
    }

    @Test
    void saveByNewsId() {
        News news = new News();
        when(newsRepository.findById(NEWS_ID))
                .thenReturn(Optional.of(news));
        CommentCreateEditDto newComment = CommentCreateEditDto.of("");
        Comment comment = new Comment();
        when(commentMapper.toComment(newComment))
                .thenReturn(comment);
        when(commentRepository.saveAndFlush(comment))
                .thenReturn(comment);
        CommentReadDto savedComment = CommentReadDto.builder()
                .id(10L)
                .text("comment")
                .build();
        when(commentMapper.toReadDto(comment))
                .thenReturn(savedComment);

        CommentReadDto result = commentService.saveByNewsId(NEWS_ID, newComment);

        assertThat(result).isEqualTo(savedComment);
        verifyNoMoreInteractions(commentRepository, newsRepository, userRepository, commentMapper);
    }

    @Test
    void saveByNewsIdWithNonExistentNewsId() {
        when(newsRepository.findById(NEWS_ID))
                .thenReturn(Optional.empty());
        CommentCreateEditDto newComment = CommentCreateEditDto.of("comment");

        assertThrows(EntityNotFoundException.class, () -> commentService.saveByNewsId(NEWS_ID, newComment));
        verifyNoMoreInteractions(commentRepository, newsRepository, userRepository, commentMapper);
    }

    @Test
    void updateById() {
        CommentCreateEditDto newCommentInfo = CommentCreateEditDto.of("");
        Comment comment = new Comment();
        CommentReadDto updatedComment = CommentReadDto.builder()
                .text("Some text")
                .build();
        when(commentRepository.findById(COMMENT_ID))
                .thenReturn(Optional.of(comment));
        when(commentMapper.toComment(newCommentInfo, comment))
                .thenReturn(comment);
        when(commentMapper.toReadDto(comment))
                .thenReturn(updatedComment);

        CommentReadDto result = commentService.updateById(COMMENT_ID, newCommentInfo);

        assertThat(result).isEqualTo(updatedComment);
        verifyNoMoreInteractions(commentRepository, newsRepository, userRepository, commentMapper);
    }

    @Test
    void updateByIdWithNonExistentCommentId() {
        CommentCreateEditDto newCommentInfo = CommentCreateEditDto.of("new text");
        when(commentRepository.findById(COMMENT_ID))
                .thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> commentService.findById(COMMENT_ID));
        verifyNoMoreInteractions(commentRepository, newsRepository, userRepository, commentMapper);
    }

    @Test
    void deleteById() {
        Comment comment = new Comment();
        when(commentRepository.findById(COMMENT_ID))
                .thenReturn(Optional.of(comment));

        assertDoesNotThrow(() -> commentService.deleteById(COMMENT_ID));
        verify(commentRepository).delete(comment);
        verify(commentRepository).flush();
        verifyNoMoreInteractions(commentRepository, newsRepository, userRepository, commentMapper);
    }

    @Test
    void deleteByIdWithNonExistentCommentId() {
        when(commentRepository.findById(COMMENT_ID))
                .thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> commentService.deleteById(COMMENT_ID));
        verifyNoMoreInteractions(commentRepository, newsRepository, userRepository, commentMapper);
    }
}
