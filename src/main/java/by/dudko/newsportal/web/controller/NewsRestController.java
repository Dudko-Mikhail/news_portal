package by.dudko.newsportal.web.controller;

import by.dudko.newsportal.dto.PageResponse;
import by.dudko.newsportal.dto.news.NewsCreateEditDto;
import by.dudko.newsportal.dto.news.NewsReadDto;
import by.dudko.newsportal.service.NewsService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
public class NewsRestController {
    private final NewsService newsService;

    @GetMapping("/news")
    public PageResponse<NewsReadDto> findAll(Pageable pageable) {
        return newsService.findAll(pageable);
    }

    @GetMapping("/users/{userId}/news")
    public PageResponse<NewsReadDto> findAllByUserId(@PathVariable long userId, Pageable pageable) {
        return newsService.findAllByUserId(userId, pageable);
    }

    @GetMapping("/news/{id}")
    public NewsReadDto findById(@PathVariable long id) {
        return newsService.findById(id);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/news")
    public NewsReadDto create(@AuthenticationPrincipal(expression = "id") long userId,
                              @RequestBody @Validated NewsCreateEditDto createEditDto) {
        return newsService.save(userId, createEditDto);
    }

    @PutMapping("/news/{id}")
    public NewsReadDto updateNews(@PathVariable long id, @RequestBody @Validated NewsCreateEditDto createEditDto) {
        return newsService.updateById(id, createEditDto);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/news/{id}")
    public void delete(@PathVariable long id) {
        newsService.deleteById(id);
    }
}
