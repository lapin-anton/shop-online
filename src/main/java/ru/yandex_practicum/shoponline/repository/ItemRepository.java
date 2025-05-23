package ru.yandex_practicum.shoponline.repository;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.yandex_practicum.shoponline.model.entity.Item;

@Repository
public interface ItemRepository extends R2dbcRepository<Item, Long> {

    Flux<Item> findAllByOrderId(Long orderId);

}
