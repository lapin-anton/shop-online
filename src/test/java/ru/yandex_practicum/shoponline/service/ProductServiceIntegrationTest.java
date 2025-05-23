package ru.yandex_practicum.shoponline.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.yandex_practicum.shoponline.ShopOnlineApplicationTests;
import ru.yandex_practicum.shoponline.model.entity.Product;
import ru.yandex_practicum.shoponline.repository.ProductRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ProductServiceIntegrationTest extends ShopOnlineApplicationTests {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

    @BeforeEach
    void setUp() {
        productRepository.deleteAll().block();
    }

    @Test
    void findAllBySearchAndSort_shouldFindAllProductsSortedByAlpha() {
        List<Product> products = List.of(
                new Product("t-short", "test t-short", "t-short image".getBytes(), 50.0),
                new Product("trousers", "test trousers", "trousers image".getBytes(), 150.0),
                new Product("sneakers", "test sneakers", "sneakers image".getBytes(), 100.0)
        );

        productRepository.saveAll(products).subscribe();

        var sortedByAlpha = productService.findAllBySearchAndSort("", "ALPHA", 5, 1)
                .toIterable();

        assertThat(sortedByAlpha)
                .isNotEmpty()
                .hasSize(products.size())
                .first()
                .extracting(Product::getName)
                .isEqualTo("sneakers");

        assertThat(sortedByAlpha)
                .last()
                .extracting(Product::getName)
                .isEqualTo("trousers");
    }

    @Test
    void findAllBySearchAndSort_shouldFindAllProductsSortedByPrice() {
        List<Product> products = List.of(
                new Product("t-short", "test t-short", "t-short image".getBytes(), 50.0),
                new Product("trousers", "test trousers", "trousers image".getBytes(), 150.0),
                new Product("sneakers", "test sneakers", "sneakers image".getBytes(), 100.0)
        );

        productRepository.saveAll(products).subscribe();

        var sortedByPrice = productService.findAllBySearchAndSort("", "PRICE", 5, 1)
                .toIterable();

        assertThat(sortedByPrice)
                .isNotEmpty()
                .first()
                .extracting(Product::getPrice)
                .isEqualTo(50.0);

        assertThat(sortedByPrice)
                .last()
                .extracting(Product::getPrice)
                .isEqualTo(150.0);
    }

    @Test
    void findAllBySearchAndSort_shouldFindAllProductsNotSorted() {
        List<Product> products = List.of(
                new Product("t-short", "test t-short", "t-short image".getBytes(), 50.0),
                new Product("trousers", "test trousers", "trousers image".getBytes(), 150.0),
                new Product("sneakers", "test sneakers", "sneakers image".getBytes(), 100.0)
        );

        productRepository.saveAll(products).subscribe();

        var unsorted = productService.findAllBySearchAndSort("", "NO", 5, 1)
                .toIterable();

        assertThat(unsorted)
                .isNotEmpty()
                .hasSize(products.size())
                .first()
                .extracting(Product::getName)
                .isEqualTo("t-short");

        assertThat(unsorted)
                .last()
                .extracting(Product::getName)
                .isEqualTo("sneakers");
    }

    @Test
    void findById_shouldReturnProductById() {
        List<Product> products = List.of(
                new Product("t-short", "test t-short", "t-short image".getBytes(), 50.0),
                new Product("trousers", "test trousers", "trousers image".getBytes(), 150.0),
                new Product("sneakers", "test sneakers", "sneakers image".getBytes(), 100.0)
        );

        productRepository.saveAll(products).subscribe();

        productService.findById(2L)
                .doOnNext(product -> {
                    assertThat(product)
                            .isNotNull()
                            .extracting(Product::getId)
                            .isEqualTo(2L);

                    assertThat(product)
                            .extracting(Product::getName)
                            .isEqualTo("trousers");
                }).block();
    }

    @Test
    void saveNewProduct_shouldSaveNewProduct() {
        productService.saveNewProduct(new Product("t-short", "test t-short", "t-short image".getBytes(), 50.0))
                .doOnNext(product -> {
                    assertThat(product)
                            .isNotNull()
                            .extracting(Product::getId)
                            .isNotNull();

                    assertThat(product)
                            .extracting(Product::getName)
                            .isEqualTo("t-short");
                }).block();
    }

}