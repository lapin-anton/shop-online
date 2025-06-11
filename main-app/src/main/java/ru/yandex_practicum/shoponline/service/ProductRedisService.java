package ru.yandex_practicum.shoponline.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.yandex_practicum.shoponline.model.redis.Product;
import ru.yandex_practicum.shoponline.repository.redis.ProductRedisRepository;

import java.util.List;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class ProductRedisService {

    private final ProductRedisRepository productRedisRepository;

//    public List<ru.yandex_practicum.shoponline.model.entity.Product> findAll() {
//        var productList = StreamSupport.stream(productRedisRepository.findAll().spliterator(), false)
//                .toList();
//        if (!productList.isEmpty() && productList.get(0) == null) {
//            productRedisRepository.deleteAll();
//            productList = List.of();
//        }
//        return productList.stream()
//                .map(this::mapToEntity)
//                .toList();
//    }

    public Flux<ru.yandex_practicum.shoponline.model.entity.Product> findAll() {
        return Flux.fromIterable(productRedisRepository.findAll())
                .map(this::mapToEntity);
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
        return cashedProductOpt.isPresent() ? Mono.just(mapToEntity(cashedProductOpt.get())) : Mono.empty();
    }

    public void deleteAll() {
        productRedisRepository.deleteAll();
    }
}
