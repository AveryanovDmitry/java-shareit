package ru.practicum.shareit.item.mapper;

import org.mapstruct.Mapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

@Mapper(componentModel = "Spring")
public interface ItemMapper {
    ItemDto itemToItemDto(Item item);

    Item itemDtoToItem(ItemDto itemDto);
}
