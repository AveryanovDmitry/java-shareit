package ru.practicum.shareit.item.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.item.dto.CreateUpdateItemDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

@Mapper(componentModel = "Spring")
public interface ItemMapper {
    @Mapping(source = "request.id", target = "requestId")
    ItemDto toItemDto(Item item);

    Item toItem(CreateUpdateItemDto itemDto);
}
