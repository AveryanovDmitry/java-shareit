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

    default RequestWasCreatedDto fromModelToWasCreatedDto(ItemRequest request) {
        if (request == null) {
            return null;
        }
        RequestWasCreatedDto requestWasCreatedDto = new RequestWasCreatedDto();

        requestWasCreatedDto.setRequester(request.getId());
        requestWasCreatedDto.setId(request.getId());
        requestWasCreatedDto.setDescription(request.getDescription());
        requestWasCreatedDto.setCreated(request.getCreated());

        return requestWasCreatedDto;
    }

    @Mapping(source = "request.id", target = "requestId")
    ItemResponseDto mapToItemDataForRequestDto(Item item);
}
