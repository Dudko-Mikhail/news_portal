package by.dudko.newsportal.repository;

import by.dudko.newsportal.model.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    Page<Comment> findAllByOwnerId(long userId, Pageable pageable);

    Page<Comment> findAllByNewsId(long newsId, Pageable pageable);
}
