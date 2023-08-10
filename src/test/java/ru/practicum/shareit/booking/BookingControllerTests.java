package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoShort;
import ru.practicum.shareit.booking.dto.NewBooking;
import ru.practicum.shareit.booking.model.StatusBooking;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemForBookingDto;
import ru.practicum.shareit.user.dto.UserDto;


import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class BookingControllerTests {
    @InjectMocks
    private BookingController bookingController;
    @Mock
    private BookingService bookingServiceMock;
    private MockMvc mvc;
    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
    private NewBooking newBooking;
    private BookingDto bookingDto;
    private static final String OWNER_ID = "X-Sharer-User-Id";

    @BeforeEach
    public void setUp() {
        BookingDtoShort next = new BookingDtoShort(1L, new ItemForBookingDto(1L, "item"),
                1, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2));
        BookingDtoShort last = new BookingDtoShort(1L, new ItemForBookingDto(1L, "item"),
                1, LocalDateTime.now().minusDays(2), LocalDateTime.now().minusDays(1));

        bookingDto = BookingDto.builder()
                .id(1L)
                .booker(UserDto.builder()
                        .name("test")
                        .email("test@yandex.ru")
                        .build())
                .item(ItemDto.builder()
                        .name("item test")
                        .description("item test description")
                        .available(Boolean.TRUE)
                        .nextBooking(next)
                        .lastBooking(last)
                        .build())
                .status(StatusBooking.APPROVED)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(3))
                .build();

        newBooking = new NewBooking(1L, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(3));

        mvc = MockMvcBuilders.standaloneSetup(bookingController).build();
    }

    @Test
    void createRequestEndBeforeStart() throws Exception {
        newBooking.setEnd(LocalDateTime.now().plusDays(1));
        newBooking.setStart(LocalDateTime.now().plusDays(2));
        mvc.perform(post("/bookings")
                        .content(objectMapper.writeValueAsString(newBooking))
                        .header(OWNER_ID, 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void createRequestEndNull() throws Exception {
        newBooking.setEnd(null);
        newBooking.setStart(LocalDateTime.now().plusDays(2));
        mvc.perform(post("/bookings")
                        .content(objectMapper.writeValueAsString(newBooking))
                        .header(OWNER_ID, 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void createBookingRequest() throws Exception {
        when(bookingServiceMock.createBooking(any(NewBooking.class), anyLong())).thenReturn(bookingDto);

        mvc.perform(post("/bookings")
                        .content(objectMapper.writeValueAsString(newBooking))
                        .header(OWNER_ID, 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.start[0]", is(bookingDto.getStart().getYear())))
                .andExpect(jsonPath("$.start[1]", is(bookingDto.getStart().getMonthValue())))
                .andExpect(jsonPath("$.start[2]", is(bookingDto.getStart().getDayOfMonth())))
                .andExpect(jsonPath("$.end[0]", is(bookingDto.getEnd().getYear())))
                .andExpect(jsonPath("$.end[1]", is(bookingDto.getEnd().getMonthValue())))
                .andExpect(jsonPath("$.end[2]", is(bookingDto.getEnd().getDayOfMonth())))
                .andExpect(jsonPath("$.booker.id", is(bookingDto.getBooker().getId())))
                .andExpect(jsonPath("$.item.id", is(bookingDto.getItem().getId())))
                .andExpect(jsonPath("$.status", is(bookingDto.getStatus().toString())))
                .andExpect(jsonPath("$.item.nextBooking.start[0]",
                        is(bookingDto.getItem().getNextBooking().getStart().getYear())))
                .andExpect(jsonPath("$.item.nextBooking.start[1]",
                        is(bookingDto.getItem().getNextBooking().getStart().getMonthValue())))
                .andExpect(jsonPath("$.item.nextBooking.start[2]",
                        is(bookingDto.getItem().getNextBooking().getStart().getDayOfMonth())))
                .andExpect(jsonPath("$.item.nextBooking.end[0]",
                        is(bookingDto.getItem().getNextBooking().getEnd().getYear())))
                .andExpect(jsonPath("$.item.nextBooking.end[1]",
                        is(bookingDto.getItem().getNextBooking().getEnd().getMonthValue())))
                .andExpect(jsonPath("$.item.nextBooking.end[2]",
                        is(bookingDto.getItem().getNextBooking().getEnd().getDayOfMonth())));
    }

    @Test
    void getBookingById() throws Exception {
        when(bookingServiceMock.getBookingById(anyLong(), anyLong())).thenReturn(bookingDto);

        mvc.perform(get("/bookings/{bookingId}", 1)
                        .header(OWNER_ID, 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.start[0]", is(bookingDto.getStart().getYear())))
                .andExpect(jsonPath("$.start[1]", is(bookingDto.getStart().getMonthValue())))
                .andExpect(jsonPath("$.start[2]", is(bookingDto.getStart().getDayOfMonth())))
                .andExpect(jsonPath("$.end[0]", is(bookingDto.getEnd().getYear())))
                .andExpect(jsonPath("$.end[1]", is(bookingDto.getEnd().getMonthValue())))
                .andExpect(jsonPath("$.end[2]", is(bookingDto.getEnd().getDayOfMonth())))
                .andExpect(jsonPath("$.booker.id", is(bookingDto.getBooker().getId())))
                .andExpect(jsonPath("$.item.id", is(bookingDto.getItem().getId())))
                .andExpect(jsonPath("$.status", is(bookingDto.getStatus().toString())))
                .andExpect(jsonPath("$.item.nextBooking.start[0]",
                        is(bookingDto.getItem().getNextBooking().getStart().getYear())))
                .andExpect(jsonPath("$.item.nextBooking.start[1]",
                        is(bookingDto.getItem().getNextBooking().getStart().getMonthValue())))
                .andExpect(jsonPath("$.item.nextBooking.start[2]",
                        is(bookingDto.getItem().getNextBooking().getStart().getDayOfMonth())))
                .andExpect(jsonPath("$.item.nextBooking.end[0]",
                        is(bookingDto.getItem().getNextBooking().getEnd().getYear())))
                .andExpect(jsonPath("$.item.nextBooking.end[1]",
                        is(bookingDto.getItem().getNextBooking().getEnd().getMonthValue())))
                .andExpect(jsonPath("$.item.nextBooking.end[2]",
                        is(bookingDto.getItem().getNextBooking().getEnd().getDayOfMonth())));
    }

    @Test
    void approveOrRejectBooking() throws Exception {
        when(bookingServiceMock.approveOrRejected(anyLong(), anyBoolean(), anyLong())).thenReturn(bookingDto);

        mvc.perform(patch("/bookings/{bookingId}", 1)
                        .param("approved", "true")
                        .header(OWNER_ID, 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.start[0]", is(bookingDto.getStart().getYear())))
                .andExpect(jsonPath("$.start[1]", is(bookingDto.getStart().getMonthValue())))
                .andExpect(jsonPath("$.start[2]", is(bookingDto.getStart().getDayOfMonth())))
                .andExpect(jsonPath("$.end[0]", is(bookingDto.getEnd().getYear())))
                .andExpect(jsonPath("$.end[1]", is(bookingDto.getEnd().getMonthValue())))
                .andExpect(jsonPath("$.end[2]", is(bookingDto.getEnd().getDayOfMonth())))
                .andExpect(jsonPath("$.booker.id", is(bookingDto.getBooker().getId())))
                .andExpect(jsonPath("$.item.id", is(bookingDto.getItem().getId())))
                .andExpect(jsonPath("$.status", is(bookingDto.getStatus().toString())))
                .andExpect(jsonPath("$.item.nextBooking.start[0]",
                        is(bookingDto.getItem().getNextBooking().getStart().getYear())))
                .andExpect(jsonPath("$.item.nextBooking.start[1]",
                        is(bookingDto.getItem().getNextBooking().getStart().getMonthValue())))
                .andExpect(jsonPath("$.item.nextBooking.start[2]",
                        is(bookingDto.getItem().getNextBooking().getStart().getDayOfMonth())))
                .andExpect(jsonPath("$.item.nextBooking.end[0]",
                        is(bookingDto.getItem().getNextBooking().getEnd().getYear())))
                .andExpect(jsonPath("$.item.nextBooking.end[1]",
                        is(bookingDto.getItem().getNextBooking().getEnd().getMonthValue())))
                .andExpect(jsonPath("$.item.nextBooking.end[2]",
                        is(bookingDto.getItem().getNextBooking().getEnd().getDayOfMonth())));
    }

    @Test
    void getBookingAllByUserID() throws Exception {
        when(bookingServiceMock.getAllBookingByUserId(any(PageRequest.class), anyLong(), anyString()))
                .thenReturn(List.of(bookingDto));

        mvc.perform(get("/bookings").header(OWNER_ID, 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].start[0]", is(bookingDto.getStart().getYear())))
                .andExpect(jsonPath("$[0].start[1]", is(bookingDto.getStart().getMonthValue())))
                .andExpect(jsonPath("$[0].start[2]", is(bookingDto.getStart().getDayOfMonth())))
                .andExpect(jsonPath("$[0].end[0]", is(bookingDto.getEnd().getYear())))
                .andExpect(jsonPath("$[0].end[1]", is(bookingDto.getEnd().getMonthValue())))
                .andExpect(jsonPath("$[0].end[2]", is(bookingDto.getEnd().getDayOfMonth())))
                .andExpect(jsonPath("$[0].booker.id", is(bookingDto.getBooker().getId())))
                .andExpect(jsonPath("$[0].item.id", is(bookingDto.getItem().getId())))
                .andExpect(jsonPath("$[0].status", is(bookingDto.getStatus().toString())))
                .andExpect(jsonPath("$[0].item.nextBooking.start[0]",
                        is(bookingDto.getItem().getNextBooking().getStart().getYear())))
                .andExpect(jsonPath("$[0].item.nextBooking.start[1]",
                        is(bookingDto.getItem().getNextBooking().getStart().getMonthValue())))
                .andExpect(jsonPath("$[0].item.nextBooking.start[2]",
                        is(bookingDto.getItem().getNextBooking().getStart().getDayOfMonth())))
                .andExpect(jsonPath("$[0].item.nextBooking.end[0]",
                        is(bookingDto.getItem().getNextBooking().getEnd().getYear())))
                .andExpect(jsonPath("$[0].item.nextBooking.end[1]",
                        is(bookingDto.getItem().getNextBooking().getEnd().getMonthValue())))
                .andExpect(jsonPath("$[0].item.nextBooking.end[2]",
                        is(bookingDto.getItem().getNextBooking().getEnd().getDayOfMonth())));
    }

    @Test
    void shouldFindOwnerBookings() throws Exception {
        when(bookingServiceMock.getBookingsOfOwner(any(PageRequest.class), anyLong(), anyString()))
                .thenReturn(List.of(bookingDto));

        mvc.perform(get("/bookings/owner").header(OWNER_ID, 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].start[0]", is(bookingDto.getStart().getYear())))
                .andExpect(jsonPath("$[0].start[1]", is(bookingDto.getStart().getMonthValue())))
                .andExpect(jsonPath("$[0].start[2]", is(bookingDto.getStart().getDayOfMonth())))
                .andExpect(jsonPath("$[0].end[0]", is(bookingDto.getEnd().getYear())))
                .andExpect(jsonPath("$[0].end[1]", is(bookingDto.getEnd().getMonthValue())))
                .andExpect(jsonPath("$[0].end[2]", is(bookingDto.getEnd().getDayOfMonth())))
                .andExpect(jsonPath("$[0].booker.id", is(bookingDto.getBooker().getId())))
                .andExpect(jsonPath("$[0].item.id", is(bookingDto.getItem().getId())))
                .andExpect(jsonPath("$[0].status", is(bookingDto.getStatus().toString())))
                .andExpect(jsonPath("$[0].item.nextBooking.start[0]",
                        is(bookingDto.getItem().getNextBooking().getStart().getYear())))
                .andExpect(jsonPath("$[0].item.nextBooking.start[1]",
                        is(bookingDto.getItem().getNextBooking().getStart().getMonthValue())))
                .andExpect(jsonPath("$[0].item.nextBooking.start[2]",
                        is(bookingDto.getItem().getNextBooking().getStart().getDayOfMonth())))
                .andExpect(jsonPath("$[0].item.nextBooking.end[0]",
                        is(bookingDto.getItem().getNextBooking().getEnd().getYear())))
                .andExpect(jsonPath("$[0].item.nextBooking.end[1]",
                        is(bookingDto.getItem().getNextBooking().getEnd().getMonthValue())))
                .andExpect(jsonPath("$[0].item.nextBooking.end[2]",
                        is(bookingDto.getItem().getNextBooking().getEnd().getDayOfMonth())));
    }
}