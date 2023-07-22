package ru.practicum.shareit.item.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;

@Mapper(componentModel = "Spring")
public interface CommentMapper {
    @Mapping(target = "authorName", source = "author.name")
    CommentDto fromCommentToDto(Comment comment);

    Comment fromDtoToComment(CommentDto dto);
}
