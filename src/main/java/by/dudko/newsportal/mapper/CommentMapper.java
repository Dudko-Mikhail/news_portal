package by.dudko.newsportal.mapper;

import by.dudko.newsportal.dto.comment.CommentCreateEditDto;
import by.dudko.newsportal.dto.comment.CommentReadDto;
import by.dudko.newsportal.model.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CommentMapper {
    default Comment toComment(CommentCreateEditDto createEditDto) {
        return toComment(createEditDto, new Comment());
    }

    @Mapping(target = "ownerId", ignore = true)
    @Mapping(target = "news", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "creationDate", ignore = true)
    @Mapping(target = "lastEditDate", ignore = true)
    Comment toComment(CommentCreateEditDto createEditDto, @MappingTarget Comment comment);

    CommentReadDto toReadDto(Comment comment);
}
