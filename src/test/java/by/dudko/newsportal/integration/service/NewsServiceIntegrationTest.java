package by.dudko.newsportal.integration.service;

import by.dudko.newsportal.dto.PageResponse;
import by.dudko.newsportal.dto.PageResponse.Metadata;
import by.dudko.newsportal.dto.comment.CommentReadDto;
import by.dudko.newsportal.dto.news.NewsCreateEditDto;
import by.dudko.newsportal.dto.news.NewsFilter;
import by.dudko.newsportal.dto.news.NewsReadDto;
import by.dudko.newsportal.exception.EntityNotFoundException;
import by.dudko.newsportal.integration.IntegrationTest;
import by.dudko.newsportal.integration.TestConfigurationWithFakeAuditorAware;
import by.dudko.newsportal.model.News;
import by.dudko.newsportal.model.User;
import by.dudko.newsportal.repository.NewsRepository;
import by.dudko.newsportal.service.NewsService;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@IntegrationTest(classes = TestConfigurationWithFakeAuditorAware.class)
@RequiredArgsConstructor
class NewsServiceIntegrationTest {
    private static final long NEWS_ID = 1L;
    private static final long NON_EXISTENT_NEWS_ID = -1L;
    private static final String NEWS_NOT_FOUND_MESSAGE = EntityNotFoundException.byId(News.class, NON_EXISTENT_NEWS_ID)
            .getMessage();

    private final NewsService newsService;
    private final NewsRepository newsRepository;

    @Test
    void findAllByFilterWithEmptyFilter() {
        Metadata expectedMetadata = Metadata.builder()
                .page(0)
                .size(5)
                .numberOfElements(5)
                .totalElements(20)
                .totalPages(4)
                .build();
        NewsFilter emptyFilter = NewsFilter.builder().build();

        PageResponse<NewsReadDto> response = newsService.findAllByFilter(emptyFilter, PageRequest.ofSize(5));
        List<NewsReadDto> content = response.getContent();

        assertThat(response.getMetadata()).isEqualTo(expectedMetadata);
        assertAll(
                () -> assertThat(content).hasSize(5),
                () -> assertThat(content).allMatch(news -> news.getComments() == null)
        );
    }

    @Test
    void findAllByFilterWithTitleFiltration() {
        Metadata expectedMetadata = Metadata.builder()
                .page(0)
                .size(5)
                .numberOfElements(1)
                .totalElements(1)
                .totalPages(1)
                .build();
        NewsFilter titleFilter = NewsFilter.builder()
                .title("ws3")
                .build();
        NewsReadDto expectedResult = NewsReadDto.builder()
                .id(3)
                .title("news3")
                .text("text3")
                .build();

        PageResponse<NewsReadDto> response = newsService.findAllByFilter(titleFilter, PageRequest.ofSize(5));
        List<NewsReadDto> content = response.getContent();

        assertThat(response.getMetadata()).isEqualTo(expectedMetadata);
        assertAll(
                () -> assertThat(content).hasSize(1),
                () -> assertThat(content).allMatch(news -> news.getComments() == null),
                () -> assertThat(content.get(0)).isEqualTo(expectedResult)
        );
    }

    @Test
    void findAllByFilterWithTextFiltration() {
        Metadata expectedMetadata = Metadata.builder()
                .page(0)
                .size(5)
                .numberOfElements(2)
                .totalElements(2)
                .totalPages(1)
                .build();
        NewsFilter textFilter = NewsFilter.builder()
                .text("ext2")
                .build();

        PageResponse<NewsReadDto> response = newsService.findAllByFilter(textFilter, PageRequest.ofSize(5));
        List<NewsReadDto> content = response.getContent();

        assertThat(response.getMetadata()).isEqualTo(expectedMetadata);
        assertAll(
                () -> assertThat(content).hasSize(2),
                () -> assertThat(content).allMatch(news -> news.getComments() == null),
                () -> assertThat(content).map(NewsReadDto::getText).containsExactlyInAnyOrder("text2", "text20")
        );
    }

    @Test
    void findAllByFilterWithTextAndTitleFiltration() {
        Metadata expectedMetadata = Metadata.builder()
                .page(0)
                .size(5)
                .numberOfElements(1)
                .totalElements(1)
                .totalPages(1)
                .build();
        NewsFilter newsFilter = NewsFilter.builder()
                .title("2")
                .text("text20")
                .build();
        NewsReadDto expectedResult = NewsReadDto.builder()
                .id(20)
                .title("news20")
                .text("text20")
                .build();

        PageResponse<NewsReadDto> response = newsService.findAllByFilter(newsFilter, PageRequest.ofSize(5));
        List<NewsReadDto> content = response.getContent();

        assertThat(response.getMetadata()).isEqualTo(expectedMetadata);
        assertAll(
                () -> assertThat(content).hasSize(1),
                () -> assertThat(content).allMatch(news -> news.getComments() == null),
                () -> assertThat(content.get(0)).isEqualTo(expectedResult)
        );
    }

    @Test
    void findAllByUserId() {
        Metadata expectedMetadata = Metadata.builder()
                .page(0)
                .size(20)
                .numberOfElements(10)
                .totalElements(10)
                .totalPages(1)
                .build();

        PageResponse<NewsReadDto> response = newsService.findAllByUserId(1L, PageRequest.ofSize(20));
        List<NewsReadDto> content = response.getContent();

        assertThat(response.getMetadata()).isEqualTo(expectedMetadata);
        assertAll(
                () -> assertThat(content).hasSize(10),
                () -> assertThat(content).allMatch(news -> news.getComments() == null)
        );
    }

    @Test
    void findAllByUserIdWithNonExistentUserId() {
        long nonExistentUserId = -1L;
        String expectedMessage = EntityNotFoundException.byId(User.class, nonExistentUserId)
                .getMessage();

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> newsService.findAllByUserId(nonExistentUserId, Pageable.unpaged()));
        assertThat(exception.getMessage()).isEqualTo(expectedMessage);
    }

    @Test
    void findByIdWithComments() {
        Metadata expectedMetadata = Metadata.builder()
                .page(0)
                .size(20)
                .numberOfElements(10)
                .totalElements(10)
                .totalPages(1)
                .build();

        NewsReadDto news = newsService.findByIdWithComments(NEWS_ID, PageRequest.ofSize(20));
        PageResponse<CommentReadDto> commentsResponse = news.getComments();
        List<CommentReadDto> comments = commentsResponse.getContent();

        assertAll(
                () -> assertThat(news.getTitle()).isEqualTo("news1"),
                () -> assertThat(news.getText()).isEqualTo("text1")
        );
        assertThat(commentsResponse.getMetadata()).isEqualTo(expectedMetadata);
        assertThat(comments).hasSize(10);
    }

    @Test
    void findByIdWithCommentsWithNonExistentNewsId() {
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> newsService.findByIdWithComments(NON_EXISTENT_NEWS_ID, Pageable.unpaged()));
        assertThat(exception.getMessage()).isEqualTo(NEWS_NOT_FOUND_MESSAGE);
    }

    @Test
    void isNewsOwnerShouldReturnTrue() {
        long newsOwnerId = 1;
        assertTrue(newsService.isNewsOwner(newsOwnerId, NEWS_ID));
    }

    @Test
    void isNewsOwnerWithNonExistentNewsId() {
        long userId = 50;
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> newsService.isNewsOwner(userId, NON_EXISTENT_NEWS_ID));
        assertThat(exception.getMessage()).isEqualTo(NEWS_NOT_FOUND_MESSAGE);
    }

    @Test
    void isNewsOwnerShouldReturnFalse() {
        long notTheNewsOwnerId = 500;
        assertFalse(newsService.isNewsOwner(notTheNewsOwnerId, NEWS_ID));
    }

    @Test
    void save() {
        NewsCreateEditDto newNews = NewsCreateEditDto.builder()
                .title("Lions")
                .text("Facts about lions")
                .build();

        NewsReadDto savedNews = newsService.save(newNews);

        assertThat(newsRepository.findById(savedNews.getId())).isPresent();
        assertAll(
                () -> assertThat(savedNews.getTitle()).isEqualTo(newNews.getTitle()),
                () -> assertThat(savedNews.getText()).isEqualTo(newNews.getText())
        );
    }

    @Test
    void updateById() {
        NewsCreateEditDto newNewsInfo = NewsCreateEditDto.builder()
                .title("Simple title")
                .text("Some text")
                .build();

        NewsReadDto updatedNews = newsService.updateById(NEWS_ID, newNewsInfo);

        assertAll(
                () -> assertThat(updatedNews.getId()).isEqualTo(NEWS_ID),
                () -> assertThat(updatedNews.getTitle()).isEqualTo(newNewsInfo.getTitle()),
                () -> assertThat(updatedNews.getText()).isEqualTo(newNewsInfo.getText())
        );
    }

    @Test
    void updateByIdWithNonExistentNewsId() {
        NewsCreateEditDto newNewsInfo = NewsCreateEditDto.builder().build();

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> newsService.updateById(NON_EXISTENT_NEWS_ID, newNewsInfo));
        assertThat(exception.getMessage()).isEqualTo(NEWS_NOT_FOUND_MESSAGE);
    }

    @Test
    void deleteById() {
        Assertions.assertDoesNotThrow(() -> newsService.deleteById(NEWS_ID));
        assertThat(newsRepository.findById(NEWS_ID)).isEmpty();
    }

    @Test
    void deleteByIdWithNonExistentNewsId() {
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> newsService.deleteById(NON_EXISTENT_NEWS_ID));
        assertThat(exception.getMessage()).isEqualTo(NEWS_NOT_FOUND_MESSAGE);
    }
}
