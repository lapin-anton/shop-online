package ru.yandex_practicum.shoponline.repository;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import ru.yandex_practicum.shoponline.model.entity.Item;

@Repository
public interface ItemRepository extends R2dbcRepository<Item, Long> {
}
