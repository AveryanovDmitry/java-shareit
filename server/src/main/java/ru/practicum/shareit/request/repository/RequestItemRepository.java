package ru.practicum.shareit.request.repository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.repository.PagingAndSortingRepository;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public interface RequestItemRepository extends PagingAndSortingRepository<ItemRequest, Long> {
    List<ItemRequest> findAllByRequesterId(PageRequest pageRequest, Long id);

    List<ItemRequest> findAllByRequesterIdNot(PageRequest pageRequest, Long requester);
}
