package ru.practicum.shareit.exeptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ValidationException;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class ExceptionsHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleNotFoundExceptionHandler(NotFoundException exception) {
        log.info(exception.getMessage());
        return Map.of("error", "Not found",
                "errorMessage", exception.getMessage());
    }

    @ExceptionHandler(EmailExeption.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, String> handleEmailExceptionHandler(EmailExeption exception) {
        log.info(exception.getMessage());
        return Map.of("error", exception.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, String> handleServerErrorExceptionHandler(Throwable exception) {
        log.info(exception.getMessage());
        return Map.of("error", exception.getMessage());
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, BookingException.class, ValidationException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    private Map<String, String> handleBadRequestException(Exception exception) {
        log.debug(exception.getMessage());
        return Map.of("error", exception.getMessage());
    }
}