package by.dudko.newsportal.repository;

import by.dudko.newsportal.model.News;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NewsRepository extends JpaRepository<News, Long>, JpaSpecificationExecutor<News> {
    Page<News> findAllByOwnerId(long userId, Pageable pageable);

    @Query("select n.id from News n where n.ownerId = :ownerId")
    List<Long> findAllNewsIdByOwnerId(@Param(value = "ownerId") long userId);
}
