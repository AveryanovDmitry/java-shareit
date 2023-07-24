package ru.practicum.shareit.booking.valid;

import ru.practicum.shareit.booking.dto.BookingDtoCreateNew;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ValidatorStartAndEnd implements ConstraintValidator<ValidateStartAndEndBooking, BookingDtoCreateNew> {

    @Override
    public void initialize(ValidateStartAndEndBooking constraintAnnotation) {
    }

    @Override
    public boolean isValid(BookingDtoCreateNew newBooking,
                           ConstraintValidatorContext constraintValidatorContext) {
        return newBooking.getStart().isBefore(newBooking.getEnd());
    }
}
