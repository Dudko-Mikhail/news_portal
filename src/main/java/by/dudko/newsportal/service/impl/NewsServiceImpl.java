package by.dudko.newsportal.service.impl;

import by.dudko.newsportal.dto.PageResponse;
import by.dudko.newsportal.dto.news.NewsCreateEditDto;
import by.dudko.newsportal.dto.news.NewsReadDto;
import by.dudko.newsportal.exception.EntityNotFoundException;
import by.dudko.newsportal.mapper.NewsMapper;
import by.dudko.newsportal.model.News;
import by.dudko.newsportal.repository.NewsRepository;
import by.dudko.newsportal.service.NewsService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class NewsServiceImpl implements NewsService {
    private final NewsRepository newsRepository;
    private final NewsMapper newsMapper;

    @Override
    public PageResponse<NewsReadDto> findAll(Pageable pageable) {
        return PageResponse.of(newsRepository.findAll(pageable)
                .map(newsMapper::toReadDto));
    }

    @Override
    public PageResponse<NewsReadDto> findAllByUserId(long userId, Pageable pageable) {
        return PageResponse.of(newsRepository.findAllByOwnerId(userId, pageable)
                .map(newsMapper::toReadDto));
    }

    @Override
    public NewsReadDto findById(long id) {
        return newsRepository.findById(id)
                .map(newsMapper::toReadDto)
                .orElseThrow(() -> EntityNotFoundException.byId(News.class, Long.toString(id)));
    }

    @Transactional
    @Override
    public NewsReadDto save(long userId, NewsCreateEditDto createEditDto) {
        return Optional.of(createEditDto)
                .map(newsMapper::toNews)
                .map(newsRepository::saveAndFlush)
                .map(newsMapper::toReadDto)
                .orElseThrow();
    }

    @Transactional
    @Override
    public NewsReadDto updateById(long id, NewsCreateEditDto createEditDto) {
        return newsRepository.findById(id)
                .map(news -> newsMapper.toNews(createEditDto, news))
                .map(newsMapper::toReadDto)
                .orElseThrow(() -> EntityNotFoundException.byId(News.class, Long.toString(id)));
    }

    @Transactional
    @Override
    public void deleteById(long id) {
        var news = newsRepository.findById(id)
                .orElseThrow(() -> EntityNotFoundException.byId(News.class, Long.toString(id)));
        newsRepository.delete(news);
    }
}