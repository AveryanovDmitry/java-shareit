package ru.practicum.shareit.request.service;

import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.RequestWasCreatedDto;
import ru.practicum.shareit.request.dto.RequestWithItemsDto;

import java.util.List;

public interface RequestService {
    RequestWasCreatedDto createRequest(ItemRequestDto request, Long requesterId);

    List<RequestWithItemsDto> getRequestsByRequesterId(PageRequest pageRequest, Long requesterId);

    List<RequestWithItemsDto> getAllRequestsWithoutRequesterId(PageRequest pageRequest, Long requesterId);

    RequestWithItemsDto getRequestById(Long userId, Long requestId);
}
