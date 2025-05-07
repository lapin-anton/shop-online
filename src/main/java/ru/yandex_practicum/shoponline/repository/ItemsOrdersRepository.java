package ru.yandex_practicum.shoponline.repository;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import ru.yandex_practicum.shoponline.model.entity.ItemsOrders;

public interface ItemsOrdersRepository extends R2dbcRepository<ItemsOrders, Long> {
}
