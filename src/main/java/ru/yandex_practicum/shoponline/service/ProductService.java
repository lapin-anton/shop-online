package ru.yandex_practicum.shoponline.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.yandex_practicum.shoponline.model.entity.Product;
import ru.yandex_practicum.shoponline.repository.ProductRepository;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    private final ProductRedisService productRedisService;

    public Flux<Product> findAllBySearchAndSort(String search, String sort, int pageSize, int pageNumber) {
        List<Product> cachedProducts = productRedisService.findAll();
        if (cachedProducts.isEmpty()) {
            productRepository.findAll()
                    .flatMap(productRedisService::save).subscribe();
            cachedProducts = productRedisService.findAll();
        }
        if (sort.equals("ALPHA")) {
            cachedProducts = cachedProducts.stream()
                    .filter(product -> product.getName().toUpperCase().contains(search.toUpperCase()))
                    .sorted(Comparator.comparing(Product::getName))
                    .skip((long) (pageNumber - 1) * pageSize)
                    .limit(pageSize)
                    .toList();
        } else if (sort.equals("PRICE")) {
            cachedProducts = cachedProducts.stream()
                    .filter(product -> product.getName().toUpperCase().contains(search.toUpperCase()))
                    .sorted(Comparator.comparing(Product::getPrice))
                    .skip((long) (pageNumber - 1) * pageSize)
                    .limit(pageSize)
                    .toList();
        } else {
            cachedProducts = cachedProducts.stream()
                    .filter(product -> product.getName().toUpperCase().contains(search.toUpperCase()))
                    .skip((long) (pageNumber - 1) * pageSize)
                    .limit(pageSize)
                    .toList();
        }
        return Flux.fromIterable(cachedProducts);
    }

    public Mono<Product> findById(Long productId) {
        return productRepository.findById(productId);
    }

    public Mono<Product> saveNewProduct(Product product) {
        return productRepository.save(product);
    }

}
