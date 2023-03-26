package by.dudko.newsportal.integration.service;

import by.dudko.newsportal.dto.PageResponse;
import by.dudko.newsportal.dto.comment.CommentCreateEditDto;
import by.dudko.newsportal.dto.comment.CommentReadDto;
import by.dudko.newsportal.exception.EntityNotFoundException;
import by.dudko.newsportal.integration.IntegrationTest;
import by.dudko.newsportal.integration.TestConfigurationWithFakeAuditorAware;
import by.dudko.newsportal.model.Comment;
import by.dudko.newsportal.model.News;
import by.dudko.newsportal.model.User;
import by.dudko.newsportal.repository.CommentRepository;
import by.dudko.newsportal.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@IntegrationTest(classes = TestConfigurationWithFakeAuditorAware.class)
@RequiredArgsConstructor
class CommentServiceIntegrationTest {
    private static final long COMMENT_ID = 1L;
    private static final long NEWS_ID = 2L;
    private static final long USER_ID = 1L;
    private static final long NON_EXISTENT_COMMENT_ID = -1L;
    private static final long NON_EXISTENT_NEWS_ID = -1L;
    private static final String COMMENT_NOT_FOUND_MESSAGE = EntityNotFoundException.byId(Comment.class,
            NON_EXISTENT_COMMENT_ID).getMessage();
    private static final String NEWS_NOT_FOUND_MESSAGE = EntityNotFoundException.byId(News.class, NON_EXISTENT_NEWS_ID)
            .getMessage();

    private final CommentService commentService;
    private final CommentRepository commentRepository;

    @Test
    void findAllByUserId() {
        PageResponse.Metadata expectedMetadata = PageResponse.Metadata.builder()
                .page(0)
                .size(20)
                .numberOfElements(20)
                .totalElements(82)
                .totalPages(5)
                .build();
        long userId = 1L;

        PageResponse<CommentReadDto> response = commentService.findAllByUserId(userId, PageRequest.ofSize(20));
        List<CommentReadDto> content = response.getContent();

        assertThat(response.getMetadata()).isEqualTo(expectedMetadata);
        assertAll(
                () -> assertThat(content).hasSize(20),
                () -> assertThat(content).allMatch(comment -> comment.getOwnerId() == userId)
        );
    }

    @Test
    void findAllByUserIdWithNonExistentUser() {
        long nonExistentUserId = -1L;
        String expectedMessage = EntityNotFoundException.byId(User.class, nonExistentUserId).getMessage();

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> commentService.findAllByUserId(nonExistentUserId, Pageable.unpaged()));
        assertThat(exception.getMessage()).isEqualTo(expectedMessage);
    }

    @Test
    void findAllByNewsId() {
        PageResponse.Metadata expectedMetadata = PageResponse.Metadata.builder()
                .page(0)
                .size(20)
                .numberOfElements(10)
                .totalElements(10)
                .totalPages(1)
                .build();

        PageResponse<CommentReadDto> response = commentService.findAllByNewsId(NEWS_ID, PageRequest.ofSize(20));
        List<CommentReadDto> content = response.getContent();

        assertThat(response.getMetadata()).isEqualTo(expectedMetadata);
        assertThat(content).hasSize(10);
    }

    @Test
    void findAllByNewsIdWithNonExistentNewsId() {
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> commentService.findAllByNewsId(NON_EXISTENT_NEWS_ID, Pageable.unpaged()));
        assertThat(exception.getMessage()).isEqualTo(NEWS_NOT_FOUND_MESSAGE);
    }

    @Test
    void findById() {
        CommentReadDto searchResult = commentService.findById(COMMENT_ID);

        assertAll(
                () -> assertThat(searchResult.getId()).isEqualTo(COMMENT_ID),
                () -> assertThat(searchResult.getText()).isEqualTo("comment text1"),
                () -> assertThat(searchResult.getOwnerId()).isEqualTo(1L)
        );
    }

    @Test
    void findByIdWithNonExistentCommentId() {
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> commentService.findById(NON_EXISTENT_COMMENT_ID));
        assertThat(exception.getMessage()).isEqualTo(COMMENT_NOT_FOUND_MESSAGE);
    }

    @Test
    void isCommentOwnerShouldReturnTrue() {
        assertTrue(commentService.isCommentOwner(USER_ID, COMMENT_ID));
    }

    @Test
    void isCommentOwnerWithNonExistentCommentId() {
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> commentService.isCommentOwner(USER_ID, NON_EXISTENT_COMMENT_ID));
        assertThat(exception.getMessage()).isEqualTo(COMMENT_NOT_FOUND_MESSAGE);
    }

    @Test
    void isCommentOwnerShouldReturnFalse() {
        long notTheCommentOwnerId = 500;
        assertFalse(commentService.isCommentOwner(notTheCommentOwnerId, COMMENT_ID));
    }

    @Test
    void saveByNewsId() {
        CommentCreateEditDto newComment = CommentCreateEditDto.of("Amazing ideas");

        CommentReadDto savedComment = commentService.saveByNewsId(NEWS_ID, newComment);

        assertThat(commentRepository.findById(savedComment.getId())).isPresent();
        assertAll(
                () -> assertThat(savedComment.getText()).isEqualTo(newComment.getText()),
                () -> assertThat(savedComment.getOwnerId()).isEqualTo(USER_ID)
        );
    }

    @Test
    void saveByNewsIdWithNonExistentNewsId() {
        CommentCreateEditDto newComment = CommentCreateEditDto.of("Amazing ideas");

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> commentService.saveByNewsId(NON_EXISTENT_NEWS_ID, newComment));
        assertThat(exception.getMessage()).isEqualTo(NEWS_NOT_FOUND_MESSAGE);
    }

    @Test
    void updateById() {
        CommentCreateEditDto newCommentInfo = CommentCreateEditDto.of("Amazing text");

        CommentReadDto updatedComment = commentService.updateById(COMMENT_ID, newCommentInfo);

        assertAll(
                () -> assertThat(updatedComment.getId()).isEqualTo(COMMENT_ID),
                () -> assertThat(updatedComment.getText()).isEqualTo(newCommentInfo.getText()),
                () -> assertThat(updatedComment.getOwnerId()).isEqualTo(1L)
        );
    }

    @Test
    void updateByIdWithNonExistentCommentId() {
        CommentCreateEditDto newCommentInfo = CommentCreateEditDto.of("Amazing text");

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> commentService.updateById(NON_EXISTENT_COMMENT_ID, newCommentInfo));
        assertThat(exception.getMessage()).isEqualTo(COMMENT_NOT_FOUND_MESSAGE);
    }

    @Test
    void deleteById() {
        assertDoesNotThrow(() -> commentService.deleteById(COMMENT_ID));
        assertThat(commentRepository.findById(COMMENT_ID)).isEmpty();
    }

    @Test
    void deleteByIdWithNonExistentCommentId() {
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> commentService.deleteById(NON_EXISTENT_COMMENT_ID));
        assertThat(exception.getMessage()).isEqualTo(COMMENT_NOT_FOUND_MESSAGE);
    }
}
