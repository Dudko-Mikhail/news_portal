package by.dudko.newsportal.repository;

import by.dudko.newsportal.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Page<User> findAllByDeletedIsFalse(Pageable pageable);

    Optional<User> findByUsername(String username);

    @Query(value = "select count(u) = 0 from User u where u.id <> :userId and u.username = :username")
    boolean isUsernameUniqueExceptUserWithId(@Param("username") String username, @Param("userId") long userId);
}
