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

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Validated
public class ItemRequestController {

    private final RequestService requestService;

    private static final String OWNER_ID = "X-Sharer-User-Id";

    @PostMapping
    public RequestWasCreatedDto createRequest(@RequestBody ItemRequestDto requestDto,
                                              @RequestHeader(OWNER_ID) @Min(1) Long senderId) {
        return requestService.createRequest(requestDto, senderId);
    }

    @GetMapping
    public List<RequestWithItemsDto> getRequestsBySenderId(@RequestHeader(OWNER_ID) @Min(1) Long requesterId,
                                                           @RequestParam(defaultValue = "0") @Min(0) Integer from,
                                                           @RequestParam(defaultValue = "10") @Min(1) @Max(20) Integer size) {
        return requestService.getRequestsByRequesterId(PageRequest.of(
                from / size, size, Sort.by(Sort.Direction.DESC, "created")), requesterId);
    }

    @GetMapping("all")
    public List<RequestWithItemsDto> getAllRequests(@RequestHeader(OWNER_ID) @Min(1) Long requesterId,
                                                    @RequestParam(defaultValue = "0") @Min(0) Integer from,
                                                    @RequestParam(defaultValue = "10") @Min(1) @Max(20) Integer size) {
        return requestService.getAllRequestsWithoutRequesterId(PageRequest.of(
                from / size, size, Sort.by(Sort.Direction.DESC, "created")), requesterId);
    }

    @GetMapping("{requestId}")
    public RequestWithItemsDto getItemRequest(
            @RequestHeader(OWNER_ID) @Min(1) Long userId,
            @PathVariable @Min(1) Long requestId) {
        return requestService.getRequestById(userId, requestId);
    }
}
