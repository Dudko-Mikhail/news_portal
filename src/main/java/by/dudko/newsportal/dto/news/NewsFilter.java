package by.dudko.newsportal.dto.news;

import by.dudko.newsportal.model.News;
import by.dudko.newsportal.util.SpecificationBuilder;
import lombok.Builder;
import lombok.Value;
import org.springframework.data.jpa.domain.Specification;

@Value
@Builder
public class NewsFilter {
    String title;
    String text;

    public Specification<News> toSpecification() {
        return SpecificationBuilder.<News>build()
                .addSpecification(title,
                        value -> SpecificationBuilder.equalsIgnoreCase(value, newsRoot -> newsRoot.get("title")))
                .addSpecification(text,
                        value -> SpecificationBuilder.equalsIgnoreCase(value, newsRoot -> newsRoot.get("text")))
                .buildAnd();
    }
}
