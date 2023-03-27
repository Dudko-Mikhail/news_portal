package by.dudko.newsportal.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.SQLDelete;
import org.springframework.security.core.GrantedAuthority;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "username")
@Builder
@SQLDelete(sql = "update users set is_deleted = true where id = ?")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "users")
public class User extends AuditedEntity<Long> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;
    private String password;
    private String name;
    private String surname;
    private String parentName;

    @Enumerated(value = EnumType.STRING)
    private Role role;

    @OneToMany
    @JoinColumn(name = "inserted_by_id", referencedColumnName = "id", updatable = false)
    @Builder.Default
    List<News> news = new ArrayList<>();

    @OneToMany
    @JoinColumn(name = "inserted_by_id", referencedColumnName = "id", updatable = false)
    @Builder.Default
    List<Comment> comments = new ArrayList<>();

    @Column(name = "is_deleted")
    private boolean deleted;

    public enum Role implements GrantedAuthority {
        ADMIN,
        JOURNALIST,
        SUBSCRIBER;

        @Override
        public String getAuthority() {
            return name();
        }
    }
}
