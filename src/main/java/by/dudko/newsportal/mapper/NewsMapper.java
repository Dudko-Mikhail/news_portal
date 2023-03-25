package by.dudko.newsportal.mapper;

import by.dudko.newsportal.dto.news.NewsCreateEditDto;
import by.dudko.newsportal.dto.news.NewsReadDto;
import by.dudko.newsportal.model.News;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = CommentMapper.class)
public interface NewsMapper {
    default News toNews(NewsCreateEditDto createEditDto) {
        return toNews(createEditDto, new News());
    }

    @Mapping(target = "comments", ignore = true)
    @Mapping(target = "updatedById", ignore = true)
    @Mapping(target = "ownerId", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "creationDate", ignore = true)
    @Mapping(target = "lastEditDate", ignore = true)
    News toNews(NewsCreateEditDto createEditDto, @MappingTarget News news);

    @Mapping(target = "comments", ignore = true)
    NewsReadDto toReadDto(News news);
}
