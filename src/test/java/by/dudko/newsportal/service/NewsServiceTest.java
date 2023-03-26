package by.dudko.newsportal.service;

import by.dudko.newsportal.dto.PageResponse;
import by.dudko.newsportal.dto.comment.CommentReadDto;
import by.dudko.newsportal.dto.news.NewsCreateEditDto;
import by.dudko.newsportal.dto.news.NewsFilter;
import by.dudko.newsportal.dto.news.NewsReadDto;
import by.dudko.newsportal.exception.EntityNotFoundException;
import by.dudko.newsportal.mapper.NewsMapper;
import by.dudko.newsportal.model.News;
import by.dudko.newsportal.repository.NewsRepository;
import by.dudko.newsportal.repository.UserRepository;
import by.dudko.newsportal.service.impl.NewsServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NewsServiceTest {
    private static final long NEWS_ID = 1L;
    private static final long USER_ID = 1L;

    @Mock
    private NewsRepository newsRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CommentService commentService;

    @Mock
    private NewsMapper newsMapper;

    @InjectMocks
    private NewsServiceImpl newsService;

    @Test
    void findAllByFilter() {
        News news = new News();
        NewsReadDto newsReadDto = NewsReadDto.builder()
                .title("test title")
                .text("test text")
                .build();
        NewsFilter newsFilter = NewsFilter.builder().build();
        Specification<News> newsSearchCriteria = newsFilter.toSpecification();
        Pageable pageable = Pageable.ofSize(20);
        Page<News> page = new PageImpl<>(List.of(news), pageable, 1);
        when(newsRepository.findAll(newsSearchCriteria, pageable))
                .thenReturn(page);
        when(newsMapper.toReadDto(news))
                .thenReturn(newsReadDto);
        PageResponse.Metadata expectedMetadata = PageResponse.Metadata.builder()
                .page(0)
                .size(20)
                .numberOfElements(1)
                .totalElements(1)
                .totalPages(1)
                .build();

        PageResponse<NewsReadDto> response = newsService.findAllByFilter(newsFilter, pageable);
        List<NewsReadDto> content = response.getContent();

        assertThat(response.getMetadata()).isEqualTo(expectedMetadata);
        assertThat(content).hasSize(1);
        assertThat(content.get(0)).isEqualTo(newsReadDto);
        verify(newsRepository).findAll(newsSearchCriteria, pageable);
        verify(newsMapper).toReadDto(news);
        verifyNoMoreInteractions(commentService, newsRepository, userRepository, newsMapper);
    }

    @Test
    void findAllByUserId() {
        when(userRepository.existsById(USER_ID))
                .thenReturn(true);
        News news = new News();
        NewsReadDto newsReadDto = NewsReadDto.builder()
                .title("test title")
                .text("test text")
                .build();
        Pageable pageable = Pageable.ofSize(20);
        Page<News> page = new PageImpl<>(List.of(news), pageable, 1);
        when(newsRepository.findAllByOwnerId(USER_ID, pageable))
                .thenReturn(page);
        when(newsMapper.toReadDto(news))
                .thenReturn(newsReadDto);
        PageResponse.Metadata expectedMetadata = PageResponse.Metadata.builder()
                .page(0)
                .size(20)
                .numberOfElements(1)
                .totalElements(1)
                .totalPages(1)
                .build();

        PageResponse<NewsReadDto> response = newsService.findAllByUserId(USER_ID, pageable);
        List<NewsReadDto> content = response.getContent();

        assertThat(response.getMetadata()).isEqualTo(expectedMetadata);
        assertThat(content).hasSize(1);
        assertThat(content.get(0)).isEqualTo(newsReadDto);
        verify(userRepository).existsById(USER_ID);
        verify(newsRepository).findAllByOwnerId(USER_ID, pageable);
        verify(newsMapper).toReadDto(news);
        verifyNoMoreInteractions(commentService, newsRepository, userRepository, newsMapper);
    }

    @Test
    void findAllByUserIdWithNonExistentUserId() {
        when(userRepository.existsById(USER_ID))
                .thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> newsService.findAllByUserId(USER_ID, Pageable.unpaged()));
        verify(userRepository).existsById(USER_ID);
        verifyNoMoreInteractions(commentService, newsRepository, userRepository, newsMapper);
    }

    @Test
    void findByIdWithComments() {
        News news = new News();
        when(newsRepository.findById(NEWS_ID))
                .thenReturn(Optional.of(news));
        NewsReadDto newsReadDto = NewsReadDto.builder()
                .id(NEWS_ID)
                .title("Greetings")
                .text("Hello")
                .build();
        when(newsMapper.toReadDto(news))
                .thenReturn(newsReadDto);
        Pageable pageable = Pageable.ofSize(20);
        Page<CommentReadDto> commentPage = Page.empty(pageable);
        when(commentService.findAllByNewsId(NEWS_ID, pageable))
                .thenReturn(PageResponse.of(commentPage));

        NewsReadDto searchResult = newsService.findByIdWithComments(NEWS_ID, pageable);

        assertThat(searchResult).isEqualTo(newsReadDto);
        verify(newsRepository).findById(NEWS_ID);
        verify(newsMapper).toReadDto(news);
        verify(commentService).findAllByNewsId(NEWS_ID, pageable);
        verifyNoMoreInteractions(commentService, newsRepository, userRepository, newsMapper);
    }

    @Test
    void findByIdWithCommentsWithNonExistentNewsId() {
        when(newsRepository.findById(NEWS_ID))
                .thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> newsService.findByIdWithComments(NEWS_ID, Pageable.unpaged()));
        verify(newsRepository).findById(NEWS_ID);
        verifyNoMoreInteractions(commentService, newsRepository, userRepository, newsMapper);
    }

    @Test
    void save() {
        NewsCreateEditDto newNews = NewsCreateEditDto.builder()
                .build();
        News news = new News();
        when(newsMapper.toNews(newNews))
                .thenReturn(news);
        NewsReadDto savedNews = NewsReadDto.builder()
                .id(10L)
                .build();
        when(newsMapper.toReadDto(news))
                .thenReturn(savedNews);
        when(newsRepository.saveAndFlush(news))
                .thenReturn(news);

        NewsReadDto result = newsService.save(newNews);

        assertThat(result.getId()).isEqualTo(10L);
        verify(newsMapper).toNews(newNews);
        verify(newsMapper).toReadDto(news);
        verify(newsRepository).saveAndFlush(news);
        verifyNoMoreInteractions(commentService, newsRepository, userRepository, newsMapper);
    }

    @Test
    void saveWithNullNewsCreateEditDto() {
        assertThrows(NullPointerException.class, () -> newsService.save(null));
    }

    @Test
    void updateById() {
        NewsCreateEditDto newNewsInfo = NewsCreateEditDto.builder().build();
        News news = new News();
        NewsReadDto updatedNews = NewsReadDto.builder()
                .id(4L)
                .build();
        when(newsRepository.findById(NEWS_ID))
                .thenReturn(Optional.of(news));
        when(newsMapper.toNews(newNewsInfo, news))
                .thenReturn(news);
        when(newsMapper.toReadDto(news))
                .thenReturn(updatedNews);

        NewsReadDto result = newsService.updateById(NEWS_ID, newNewsInfo);

        assertThat(result).isEqualTo(updatedNews);
        verify(newsRepository).findById(NEWS_ID);
        verify(newsMapper).toNews(newNewsInfo, news);
        verify(newsMapper).toReadDto(news);
        verifyNoMoreInteractions(commentService, newsRepository, userRepository, newsMapper);
    }

    @Test
    void updateByIdWithNonExistentNewsId() {
        NewsCreateEditDto newNewsInfo = NewsCreateEditDto.builder().build();
        when(newsRepository.findById(NEWS_ID))
                .thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> newsService.updateById(NEWS_ID, newNewsInfo));
        verify(newsRepository).findById(NEWS_ID);
        verifyNoMoreInteractions(commentService, newsRepository, userRepository, newsMapper);
    }

    @Test
    void deleteById() {
        News news = new News();
        when(newsRepository.findById(NEWS_ID))
                .thenReturn(Optional.of(news));

        Assertions.assertDoesNotThrow(() -> newsService.deleteById(NEWS_ID));
        verify(newsRepository).findById(NEWS_ID);
        verify(newsRepository).delete(news);
        verify(newsRepository).flush();
        verifyNoMoreInteractions(commentService, newsRepository, userRepository, newsMapper);
    }

    @Test
    void deleteByIdWithNonExistentNewsId() {
        when(newsRepository.findById(NEWS_ID))
                .thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> newsService.deleteById(NEWS_ID));
        verify(newsRepository).findById(NEWS_ID);
        verifyNoMoreInteractions(commentService, newsRepository, userRepository, newsMapper);
    }
}
