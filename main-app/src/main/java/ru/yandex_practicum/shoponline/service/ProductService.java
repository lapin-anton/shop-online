package ru.yandex_practicum.shoponline.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.yandex_practicum.shoponline.model.entity.Product;
import ru.yandex_practicum.shoponline.repository.ProductRepository;

import java.util.Comparator;

import static java.util.Comparator.comparing;
import static java.util.Comparator.comparingDouble;
import static java.util.Comparator.naturalOrder;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    private final ProductRedisService productRedisService;

    public Flux<Product> findAllBySearchAndSort(String search, String sort, int pageSize, int pageNumber) {
        return productRedisService.findAll()
                .switchIfEmpty(productRepository.findAll())
                .flatMap(productRedisService::save)
                .filter(product -> product.getName().toUpperCase().contains(search.toUpperCase()))
                .sort(getComparator(sort))
                .skip((long) (pageNumber - 1) * pageSize)
                .take(pageSize);
    }

    public Mono<Product> findById(Long productId) {
        return productRedisService.findById(productId)
                .switchIfEmpty(productRepository.findById(productId)
                        .flatMap(productRedisService::save));
    }

    public Mono<Product> saveNewProduct(Product product) {
        return productRepository.save(product);
    }

    private Comparator<Product> getComparator(String sort) {
        if (sort.equals("ALPHA")) {
            return comparing(Product::getName);
        } else if (sort.equals("PRICE")) {
            return comparingDouble(Product::getPrice);
        } else {
            return naturalOrder();
        }
    }

}
