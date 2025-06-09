package ru.yandex_practicum.shoponline.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;
import ru.yandex_practicum.shoponline.model.redis.Product;
import ru.yandex_practicum.shoponline.repository.redis.ProductRedisRepository;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class ProductRedisService {

    private final ProductRedisRepository productRedisRepository;

    public List<ru.yandex_practicum.shoponline.model.entity.Product> findAll() {
        var productList = StreamSupport.stream(productRedisRepository.findAll().spliterator(), false)
                .toList();
        if (!productList.isEmpty() && productList.get(0) == null) {
            productRedisRepository.deleteAll();
            productList = List.of();
        }
        return productList.stream()
                .map(this::mapToEntity)
                .toList();
    }

    @Transactional
    public Mono<List<ru.yandex_practicum.shoponline.model.entity.Product>> saveAll(List<ru.yandex_practicum.shoponline.model.entity.Product> productList) {
        var redisProductList = productList.stream().map(this::mapToRedis).toList();
        return Mono.just(
                StreamSupport.stream(productRedisRepository.saveAll(redisProductList).spliterator(), false)
                        .toList()
                        .stream()
                        .map(this::mapToEntity)
                        .toList()
        );
    }

    private ru.yandex_practicum.shoponline.model.entity.Product mapToEntity(Product product) {
        return new ru.yandex_practicum.shoponline.model.entity.Product(
                product.id(), product.name(), product.description(), product.image(), product.price()
        );
    }

    private Product mapToRedis(ru.yandex_practicum.shoponline.model.entity.Product product) {
        return new Product(
                product.getId(), product.getName(), product.getDescription(), product.getImage(), product.getPrice()
        );
    }

    public Mono<ru.yandex_practicum.shoponline.model.entity.Product> save(ru.yandex_practicum.shoponline.model.entity.Product p) {
        return Mono.just(mapToEntity(productRedisRepository.save(mapToRedis(p))));
    }

    public Mono<ru.yandex_practicum.shoponline.model.entity.Product> findById(Long productId) {
        var cashedProductOpt = productRedisRepository.findById(productId);
        return Mono.just(cashedProductOpt.isPresent() ? mapToEntity(cashedProductOpt.get()) : null);
    }
}
