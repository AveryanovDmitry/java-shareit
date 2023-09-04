package ru.practicum.shareit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ShareItApp {

    public static final String USER_ID_HEADER = "X-Sharer-User-Id";

    public static void main(String[] args) {
        SpringApplication.run(ShareItApp.class, args);
    }

}
