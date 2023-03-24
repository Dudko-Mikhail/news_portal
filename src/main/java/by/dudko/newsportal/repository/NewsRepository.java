package by.dudko.newsportal.repository;

import by.dudko.newsportal.model.News;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NewsRepository extends JpaRepository<News, Long> {
    Page<News> findAllByOwnerId(long userId, Pageable pageable);

    List<Long> findAllNewsIdByOwnerId(long userId);
}
