package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exeptions.NotFoundException;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.RequestWasCreatedDto;
import ru.practicum.shareit.request.dto.RequestWithItemsDto;
import ru.practicum.shareit.request.mapper.RequestItemMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.RequestItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {

    private final RequestItemRepository requestItemRepository;

    private final UserRepository userRepository;

    private final ItemRepository itemRepository;
    private final RequestItemMapper mapperRequest;

    public RequestWasCreatedDto createRequest(ItemRequestDto request, Long requesterId) {
        User user = userRepository.findById(requesterId)
                .orElseThrow(() -> new NotFoundException("Не найдены пользователь с таким id"));

        ItemRequest model = mapperRequest.createModel(request, LocalDateTime.now());
        model.setRequester(user);
        return mapperRequest.fromModelToWasCreatedDto(requestItemRepository.save(model));
    }

    public List<RequestWithItemsDto> getRequestsByRequesterId(PageRequest pageRequest, Long requesterId) {
        if (!userRepository.existsById(requesterId)) {
            throw new NotFoundException("Не найдены запросы от пользователя с таким id");
        }

        return requestItemRepository.findAllByRequesterId(pageRequest, requesterId).stream()
                .map(mapperRequest::fromModelToDto).collect(Collectors.toList());
    }

    public List<RequestWithItemsDto> getAllRequestsWithoutRequesterId(PageRequest pageRequest, Long requesterId) {
        if (!userRepository.existsById(requesterId)) {
            throw new NotFoundException("Не найдены запросы от пользователя с таким id");
        }

        return requestItemRepository.findAllByRequesterIdNot(pageRequest, requesterId).stream()
                .map(mapperRequest::fromModelToDto).collect(Collectors.toList());
    }

    public RequestWithItemsDto getRequestById(Long userId, Long requestId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Не найдены запросы от пользователя с таким id");
        }

        return mapperRequest.fromModelToDto(requestItemRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Запрос с таким id не найден")));
    }

}
