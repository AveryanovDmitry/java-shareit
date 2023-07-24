package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    @Query(value = "SELECT it " +
            "FROM Item as it " +
            "WHERE lower(it.name) LIKE lower(concat('%',:text,'%')) " +
            "   OR lower(it.description) LIKE lower(concat('%',:text,'%')) " +
            "   AND it.available=true")
    List<Item> findAllByNameOrDescriptionContainingIgnoreCase(@Param("text") String text);

    List<Item> findAllByOwnerOrderById(Long userId);
}
