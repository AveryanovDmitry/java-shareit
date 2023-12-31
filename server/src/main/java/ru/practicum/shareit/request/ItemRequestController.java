package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.RequestWasCreatedDto;
import ru.practicum.shareit.request.dto.RequestWithItemsDto;
import ru.practicum.shareit.request.service.RequestService;

import java.util.List;

import static ru.practicum.shareit.ShareItApp.USER_ID_HEADER;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Validated
public class ItemRequestController {

    private final RequestService requestService;

    @PostMapping
    public RequestWasCreatedDto createRequest(@RequestBody ItemRequestDto requestDto,
                                              @RequestHeader(USER_ID_HEADER) Long senderId) {
        return requestService.createRequest(requestDto, senderId);
    }

    @GetMapping
    public List<RequestWithItemsDto> getRequestsBySenderId(@RequestHeader(USER_ID_HEADER) Long requesterId,
                                                           @RequestParam(defaultValue = "0") Integer from,
                                                           @RequestParam(defaultValue = "10") Integer size) {
        return requestService.getRequestsByRequesterId(PageRequest.of(
                from / size, size, Sort.by(Sort.Direction.DESC, "created")), requesterId);
    }

    @GetMapping("all")
    public List<RequestWithItemsDto> getAllRequests(@RequestHeader(USER_ID_HEADER) Long requesterId,
                                                    @RequestParam(defaultValue = "0") Integer from,
                                                    @RequestParam(defaultValue = "10") Integer size) {
        return requestService.getAllRequestsWithoutRequesterId(PageRequest.of(
                from / size, size, Sort.by(Sort.Direction.DESC, "created")), requesterId);
    }

    @GetMapping("{requestId}")
    public RequestWithItemsDto getItemRequest(
            @RequestHeader(USER_ID_HEADER) Long userId,
            @PathVariable Long requestId) {
        return requestService.getRequestById(userId, requestId);
    }
}
