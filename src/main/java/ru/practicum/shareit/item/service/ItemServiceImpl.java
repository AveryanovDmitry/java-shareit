package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDtoShort;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.StatusBooking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exeptions.BookingException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CreateCommentDto;
import ru.practicum.shareit.item.dto.CreateUpdateItemDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.exeptions.NotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;


import java.time.LocalDateTime;
import java.util.*;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static org.springframework.data.domain.Sort.Direction.DESC;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final ItemMapper itemMapper;
    private final BookingMapper bookingMapper;

    private final CommentMapper commentMapper;

    private final CommentRepository commentRepository;

    @Transactional
    public ItemDto createItem(CreateUpdateItemDto itemDto, Long ownerId) {
        Item item = itemMapper.toItem(itemDto);
        item.setOwner(userRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException("Владелец вещи под таким id не найден")).getId());
        return itemMapper.toItemDto(itemRepository.save(item));
    }

    public ItemDto getItemFromStorage(Long itemId, Long userId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь под таким id не найдена"));
        ItemDto itemDto = itemMapper.toItemDto(item);

        if (Objects.equals(item.getOwner(), userId)) {
            Map<Long, List<BookingDtoShort>> bookings = bookingRepository
                    .findByItemIdAndStatus(itemId, Sort.by(Sort.Direction.ASC, "start"))
                    .stream()
                    .map(bookingMapper::fromDtoToShort)
                    .collect(groupingBy(booking -> booking.getItem().getId(), toList()));

            findLastAndNextBookings(itemDto, bookings.getOrDefault(itemDto.getId(), Collections.emptyList()),
                    LocalDateTime.now());
        }

        itemDto.setComments(commentRepository.findAllByItemId(itemId,
                        Sort.by(Sort.Direction.ASC, "created")).stream()
                .map(commentMapper::fromCommentToDto)
                .collect(toList()));
        return itemDto;
    }

    public List<ItemDto> getAllItemFromStorageByUserId(Long userId) {
        List<Item> items = itemRepository.findAllByOwnerOrderById(userId);
        List<ItemDto> itemsDto = items.stream().map(itemMapper::toItemDto).collect(toList());

        Map<Long, List<BookingDtoShort>> bookings = bookingRepository
                .findAllByItemOwnerWhereStatusApproved(userId, Sort.by(Sort.Direction.ASC, "start"))
                .stream()
                .map(bookingMapper::fromDtoToShort)
                .collect(groupingBy(booking -> booking.getItem().getId(), toList()));

        Map<Long, List<Comment>> comments = commentRepository.findByItemIn(items, Sort.by(DESC, "created"))
                .stream()
                .collect(groupingBy(comment -> comment.getItem().getId(), toList()));

        itemsDto.forEach(itemDto -> {
            findLastAndNextBookings(itemDto, bookings.getOrDefault(itemDto.getId(), Collections.emptyList()),
                    LocalDateTime.now());

            itemDto.setComments(comments.getOrDefault(itemDto.getId(), Collections.emptyList())
                    .stream()
                    .map(commentMapper::fromCommentToDto)
                    .collect(toList()));
        });

        return itemsDto;
    }

    private void findLastAndNextBookings(ItemDto itemDto, List<BookingDtoShort> bookings, LocalDateTime now) {
        bookings.stream()
                .filter(booking -> booking.getStart().isAfter(now))
                .findFirst().ifPresent(itemDto::setNextBooking);

        itemDto.setLastBooking(bookings.stream()
                .filter(booking -> !booking.getStart().isAfter(now))
                .reduce((a, b) -> b).orElse(null));
    }

    public void deleteItemFromStorage(Long id) {
        itemRepository.deleteById(id);
    }

    @Transactional
    public ItemDto updateItem(CreateUpdateItemDto itemDto, Long itemId, Long userId) {

        Item updateItem = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь не найдена по id при её обновлении"));
        checkOwnerForUpdate(updateItem, userId);

        String nameItem = itemDto.getName();
        if (nameItem != null && !nameItem.isBlank()) {
            updateItem.setName(nameItem);
        }

        String descriptionItem = itemDto.getDescription();
        if (descriptionItem != null && !descriptionItem.isBlank()) {
            updateItem.setDescription(descriptionItem);
        }

        if (itemDto.getAvailable() != null) {
            updateItem.setAvailable(itemDto.getAvailable());
        }

        updateItem.setOwner(userId);

        return itemMapper.toItemDto(itemRepository.save(updateItem));
    }

    private void checkOwnerForUpdate(Item oldItem, Long userId) {
        Long idOwnerOldItem = oldItem.getOwner();
        if (!Objects.equals(idOwnerOldItem, userId)) {
            throw new NotFoundException("Пользователь не владелец этой вещи");
        }
    }

    public List<ItemDto> searchItemsByText(String text) {
        if (text.isBlank()) {
            return Collections.emptyList();
        }
        return itemRepository.findAllByNameOrDescriptionContainingIgnoreCase(text)
                .stream()
                .map(itemMapper::toItemDto)
                .collect(toList());
    }

    @Transactional
    @Override
    public CommentDto addComment(long userId, long itemId, CreateCommentDto commentDto) {
        Comment comment = commentMapper.fromCreateDtoToComment(commentDto);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Юзер не найден при добавлении коммента"));
        Item item = itemRepository
                .findById(itemId).orElseThrow(() -> new NotFoundException(
                        String.format("Вещь с id %s не найдена", itemId)));
        List<Booking> bookings = bookingRepository.findAllByItemIdAndBookerIdAndStatus(itemId, userId,
                StatusBooking.APPROVED, Sort.by(DESC, "start"));

        bookings.stream().filter(booking -> booking.getEnd().isBefore(LocalDateTime.now())).findAny().orElseThrow(() ->
                new BookingException(String.format("Пользователь с id %d не может оставлять комментарии вещи " +
                        "с id %d.", userId, itemId)));
        comment.setAuthor(user);
        comment.setItem(item);
        comment.setCreated(LocalDateTime.now());
        Comment commentSaved = commentRepository.save(comment);
        return commentMapper.fromCommentToDto(commentSaved);
    }
}
