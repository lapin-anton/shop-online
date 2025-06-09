package ru.yandex_practicum.shoponline.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageRequest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.yandex_practicum.shoponline.model.entity.Product;
import ru.yandex_practicum.shoponline.repository.ProductRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }
    @Test
    void findAllBySearchAndSort_shouldReturnSortedProductsByName() {
        String search = "test";
        String sort = "ALPHA";
        int pageSize = 10;
        int pageNumber = 1;

        Product product1 = new Product();
        product1.setName("t-short");
        Product product2 = new Product();
        product2.setName("sneakers");

        when(productRepository.findAllByNameContainingOrderByName(search, PageRequest.of(pageNumber - 1, pageSize)))
                .thenReturn(Flux.just(product2, product1));

        Flux<Product> products = productService.findAllBySearchAndSort(search, sort, pageSize, pageNumber);

        List<Product> productList = products.collectList().block();

        assertNotNull(productList);
        assertEquals(2, productList.size());
        assertEquals("sneakers", productList.get(0).getName());
        assertEquals("t-short", productList.get(1).getName());

        verify(productRepository, times(1)).findAllByNameContainingOrderByName(search, PageRequest.of(pageNumber - 1, pageSize));
    }

    @Test
    void findAllBySearchAndSort_shouldReturnSortedProductsByPrice() {
        String search = "test";
        String sort = "PRICE";
        int pageSize = 10;
        int pageNumber = 1;

        Product product1 = new Product();
        product1.setPrice(10.0);
        Product product2 = new Product();
        product2.setPrice(5.0);

        when(productRepository.findAllByNameContainingOrderByPrice(search, PageRequest.of(pageNumber - 1, pageSize)))
                .thenReturn(Flux.just(product2, product1));

        Flux<Product> products = productService.findAllBySearchAndSort(search, sort, pageSize, pageNumber);

        List<Product> productList = products.collectList().block();

        assertNotNull(productList);
        assertEquals(2, productList.size());
        assertEquals(5.0, productList.get(0).getPrice());
        assertEquals(10.0, productList.get(1).getPrice());

        verify(productRepository, times(1)).findAllByNameContainingOrderByPrice(search, PageRequest.of(pageNumber - 1, pageSize));
    }

    @Test
    void findAllBySearchAndSort_shouldReturnUnsortedProducts() {
        String search = "test";
        String sort = "NONE";
        int pageSize = 10;
        int pageNumber = 1;

        Product product1 = new Product();
        Product product2 = new Product();

        when(productRepository.findAllByNameContaining(search, PageRequest.of(pageNumber - 1, pageSize)))
                .thenReturn(Flux.just(product1, product2));

        Flux<Product> products = productService.findAllBySearchAndSort(search, sort, pageSize, pageNumber);

        List<Product> productList = products.collectList().block();

        assertNotNull(productList);
        assertEquals(2, productList.size());

        verify(productRepository, times(1)).findAllByNameContaining(search, PageRequest.of(pageNumber - 1, pageSize));
    }

    @Test
    void findById_shouldReturnProduct() {
        Long productId = 1L;
        Product product = new Product();

        when(productRepository.findById(productId)).thenReturn(Mono.just(product));
        Mono<Product> foundProduct = productService.findById(productId);

        foundProduct.subscribe(result -> {
            assertNotNull(result);
            assertEquals(product, result);
        });

        verify(productRepository, times(1)).findById(productId);
    }

    @Test
    void findById_shouldReturnEmptyWhenProductNotFound() {
        Long productId = 1L;

        when(productRepository.findById(productId)).thenReturn(Mono.empty());

        Mono<Product> foundProduct = productService.findById(productId);

        foundProduct.subscribe(Assertions::assertNull);

        verify(productRepository, times(1)).findById(productId);
    }

    @Test
    void saveNewProduct_shouldSaveProduct() {
        Product product = new Product();

        when(productRepository.save(any(Product.class))).thenReturn(Mono.just(product));

        Mono<Product> savedProduct = productService.saveNewProduct(product);

        savedProduct.subscribe(result -> {
            assertNotNull(result);
            assertEquals(product, result);
        });

        verify(productRepository, times(1)).save(product);
    }
}