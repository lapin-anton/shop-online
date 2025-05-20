package ru.yandex_practicum.shoponline.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.yandex_practicum.shoponline.model.entity.Product;
import ru.yandex_practicum.shoponline.repository.ProductRepository;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public Flux<Product> findAllBySearchAndSort(String search, String sort, int pageSize, int pageNumber) {
        Flux<Product> foundedPage;
        if (sort.equals("ALPHA")) {
            foundedPage = productRepository
                    .findAllByNameContainingOrderByName(search, PageRequest.of(pageNumber - 1, pageSize));
        } else if (sort.equals("PRICE")) {
            foundedPage = productRepository
                    .findAllByNameContainingOrderByPrice(search, PageRequest.of(pageNumber - 1, pageSize));
        } else {
            foundedPage = productRepository.findAllByNameContaining(search, PageRequest.of(pageNumber - 1, pageSize));
        }
        return foundedPage;
    }

    public Mono<Product> findById(Long productId) {
        return productRepository.findById(productId);
    }

    public Mono<Product> saveNewProduct(Product product) {
        return productRepository.save(product);
    }

}
