package ru.yandex_practicum.shoponline.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import ru.yandex_practicum.shoponline.model.entity.Product;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @BeforeEach
    void setUp() {
        productRepository.deleteAll();
    }

    @Test
    void findAllByNameContaining_shouldReturnUnsortedFullListWhenSearchIsEmpty() {
        var products = List.of(
                new Product("t-short", "test t-short", "t-short image".getBytes(), 50.00),
                new Product("trousers", "test trousers", "trousers image".getBytes(), 150.00),
                new Product("sneakers", "test sneakers", "sneakers image".getBytes(), 100.00)
        );
        productRepository.saveAll(products);

        var productList = productRepository.findAllByNameContaining("", PageRequest.of(0, 5))
                .stream().toList();
        assertEquals(products.size(), productList.size());
        assertArrayEquals(new List[]{products}, new List[]{productList});
    }

    @Test
    void findAllByNameContaining_shouldReturnElementsThatContainsSearch() {
        var products = List.of(
                new Product("t-short", "test t-short", "t-short image".getBytes(), 50.00),
                new Product("trousers", "test trousers", "trousers image".getBytes(), 150.00),
                new Product("sneakers", "test sneakers", "sneakers image".getBytes(), 100.00)
        );
        productRepository.saveAll(products);

        var productList = productRepository.findAllByNameContaining("ers", PageRequest.of(0, 5))
                .stream().toList();
        assertEquals(2, productList.size());
        assertTrue(productList.get(0).getName().contains("ers"));
        assertTrue(productList.get(1).getName().contains("ers"));
    }

    @Test
    void findAllByNameContainingOrderByName_shouldReturnSortedByNameList() {
        var products = List.of(
                new Product("t-short", "test t-short", "t-short image".getBytes(), 50.00),
                new Product("trousers", "test trousers", "trousers image".getBytes(), 150.00),
                new Product("sneakers", "test sneakers", "sneakers image".getBytes(), 100.00)
        );
        productRepository.saveAll(products);

        var productList = productRepository.findAllByNameContainingOrderByName("", PageRequest.of(0, 5)).stream().toList();

        assertFalse(productList.isEmpty());
        assertEquals(3, productList.size());
        assertEquals("sneakers", productList.getFirst().getName());
        assertEquals("trousers", productList.getLast().getName());
    }

    @Test
    void findAllByNameContainingOrderByPrice_shouldReturnSortedByPriceList() {
        var products = List.of(
                new Product("t-short", "test t-short", "t-short image".getBytes(), 50.00),
                new Product("trousers", "test trousers", "trousers image".getBytes(), 150.00),
                new Product("sneakers", "test sneakers", "sneakers image".getBytes(), 100.00)
        );
        productRepository.saveAll(products);

        var productList = productRepository.findAllByNameContainingOrderByPrice("", PageRequest.of(0, 5)).stream().toList();

        assertFalse(productList.isEmpty());
        assertEquals(3, productList.size());
        assertEquals(50.00, productList.getFirst().getPrice());
        assertEquals(150.00, productList.getLast().getPrice());
    }

}