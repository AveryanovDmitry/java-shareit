package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.StatusBooking;
import ru.practicum.shareit.item.dto.ItemDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class BookingDtoJsonTest {
    @Autowired
    private JacksonTester<BookingDto> jsonBookingDto;

    @Test
    void testBookingDto() throws Exception {
        LocalDateTime now = LocalDateTime.now();
        BookingDto bookingDto = BookingDto.builder()
                .start(now)
                .end(now.plusHours(1L))
                .item(ItemDto.builder()
                        .name("item test")
                        .description("item test description")
                        .available(Boolean.TRUE)
                        .build())
                .status(StatusBooking.WAITING)
                .build();

        JsonContent<BookingDto> result = jsonBookingDto.write(bookingDto);

        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo(
                now.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo(
                now.plusHours(1L).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        assertThat(result).extractingJsonPathStringValue("$.status");
    }
}