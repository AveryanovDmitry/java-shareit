package ru.practicum.shareit.booking.valid;


import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;


@Documented
@Target({ElementType.TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidatorStartAndEnd.class)
public @interface ValidateStartAndEndBooking {
    String message() default "Время старта аренды не может быть позже конца аренды или быть ей равной";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
