package ru.yandex_practicum.shoponline.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.yandex_practicum.shoponline.model.entity.Item;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
}
