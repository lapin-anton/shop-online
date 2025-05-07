package ru.yandex_practicum.shoponline.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import ru.yandex_practicum.shoponline.model.entity.Product;

@Repository
public interface ProductRepository extends R2dbcRepository<Product, Long> {

    Flux<Product> findAllByNameContaining(String search, Pageable pageable);

    Flux<Product> findAllByNameContainingOrderByName(String search, Pageable pageable);

    Flux<Product> findAllByNameContainingOrderByPrice(String search, Pageable pageable);

}
