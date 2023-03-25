package by.dudko.newsportal.service;

import by.dudko.newsportal.dto.PageResponse;
import by.dudko.newsportal.dto.news.NewsCreateEditDto;
import by.dudko.newsportal.dto.news.NewsReadDto;
import org.springframework.data.domain.Pageable;

public interface NewsService {
    PageResponse<NewsReadDto> findAll(Pageable pageable);

    PageResponse<NewsReadDto> findAllByUserId(long userId, Pageable pageable);

    NewsReadDto findByIdWithComments(long id, Pageable pageable);

    NewsReadDto save(NewsCreateEditDto createEditDto);

    NewsReadDto updateById(long id, NewsCreateEditDto createEditDto);

    void deleteById(long id);
}
