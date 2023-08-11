package ru.practicum.shareit.request.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemResponseDto;
import ru.practicum.shareit.request.dto.RequestWasCreatedDto;
import ru.practicum.shareit.request.dto.RequestWithItemsDto;
import ru.practicum.shareit.request.model.ItemRequest;

import java.time.LocalDateTime;

@Mapper(componentModel = "Spring")
public interface RequestItemMapper {
    @Mapping(target = "created", source = "now")
    ItemRequest createModel(ItemRequestDto request, LocalDateTime now);

    RequestWithItemsDto fromModelToDto(ItemRequest request);

    @Mapping(source = "requester.id", target = "requester")
    RequestWasCreatedDto fromModelToWasCreatedDto(ItemRequest request);

    @Mapping(source = "request.id", target = "requestId")
    ItemResponseDto mapToItemDataForRequestDto(Item item);
}
