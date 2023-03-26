package by.dudko.newsportal.util;

import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Root;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

@NoArgsConstructor(staticName = "build")
public class SpecificationBuilder<T> {
    List<Specification<T>> specifications = new ArrayList<>();

    public static <T> Specification<T> equalsIgnoreCase(String value, Function<Root<T>, Path<String>> fieldProvider) {
        return (root, query, cb) -> cb.like(cb.lower(fieldProvider.apply(root)),
                MessageFormat.format("%{0}%", value.toLowerCase()));
    }

    public <V> SpecificationBuilder<T> addSpecification(V value, Function<V, Specification<T>> mapper) {
        if (value != null) {
            specifications.add(mapper.apply(value));
        }
        return this;
    }

    public Specification<T> buildAnd() {
        return specifications.stream()
                .reduce(Specification::and)
                .orElse(null);
    }

    public Specification<T> buildOr() {
        return specifications.stream()
                .reduce(Specification::or)
                .orElse(null);
    }
}
