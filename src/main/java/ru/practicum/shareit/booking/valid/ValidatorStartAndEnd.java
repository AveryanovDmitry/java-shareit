package ru.practicum.shareit.booking.valid;

import ru.practicum.shareit.booking.dto.NewBooking;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;

public class ValidatorStartAndEnd implements ConstraintValidator<ValidateStartAndEndBooking, NewBooking> {

    @Override
    public void initialize(ValidateStartAndEndBooking constraintAnnotation) {
    }

    @Override
    public boolean isValid(NewBooking newBooking,
                           ConstraintValidatorContext constraintValidatorContext) {
        LocalDateTime start = newBooking.getStart();
        LocalDateTime end = newBooking.getEnd();
        if (start == null || end == null) {
            return false;
        }
        return start.isBefore(end);
    }
}
