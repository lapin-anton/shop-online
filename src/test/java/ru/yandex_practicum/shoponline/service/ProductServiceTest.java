package ru.yandex_practicum.shoponline.service;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.mock.web.MockMultipartFile;
import ru.yandex_practicum.shoponline.model.entity.Product;
import ru.yandex_practicum.shoponline.repository.ProductRepository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    private static final List<Product> productList = new ArrayList<>();

    @BeforeAll
    static void prepareTestData() {
        var tShort = new Product();
        tShort.setId(1L);
        tShort.setName("t-short");
        tShort.setDescription("test t-short");
        tShort.setPrice(50.0);
        tShort.setImage("t-short image".getBytes());
        productList.add(tShort);
        var trousers = new Product();
        trousers.setId(2L);
        trousers.setName("trousers");
        trousers.setDescription("test trousers");
        trousers.setPrice(150.0);
        trousers.setImage("trousers image".getBytes());
        productList.add(trousers);
        var sneakers = new Product();
        sneakers.setId(2L);
        sneakers.setName("sneakers");
        sneakers.setDescription("test sneakers");
        sneakers.setPrice(100.0);
        sneakers.setImage("sneakers image".getBytes());
        productList.add(sneakers);
    }

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void findAllBySearchAndSort_shouldFindAllProductsSortedByAlpha() {
        var pageNumber = 1;
        var pageSize = 5;
        when(productRepository.findAllByNameContainingOrderByName("", PageRequest.of(pageNumber - 1, pageSize)))
                .thenReturn(new PageImpl<>(productList));

        var products = productService.findAllBySearchAndSort("", "ALPHA", pageSize, pageNumber);

        assertEquals(productList.size(), products.size());
        assertEquals(productList.get(0), products.get(0));
        assertEquals(productList.get(1), products.get(1));
        assertEquals(productList.get(2), products.get(2));

        verify(productRepository, times(1))
                .findAllByNameContainingOrderByName("", PageRequest.of(pageNumber - 1, pageSize));
        verify(productRepository, times(0))
                .findAllByNameContaining("", PageRequest.of(pageNumber - 1, pageSize));
        verify(productRepository, times(0))
                .findAllByNameContainingOrderByPrice("", PageRequest.of(pageNumber - 1, pageSize));
    }

    @Test
    void findAllBySearchAndSort_shouldFindAllProductsSortedByPrice() {
        var pageNumber = 1;
        var pageSize = 5;
        when(productRepository.findAllByNameContainingOrderByPrice("", PageRequest.of(pageNumber - 1, pageSize)))
                .thenReturn(new PageImpl<>(productList));

        var products = productService.findAllBySearchAndSort("", "PRICE", pageSize, pageNumber);

        assertEquals(productList.size(), products.size());
        assertEquals(productList.get(0), products.get(0));
        assertEquals(productList.get(1), products.get(1));
        assertEquals(productList.get(2), products.get(2));

        verify(productRepository, times(0))
                .findAllByNameContainingOrderByName("", PageRequest.of(pageNumber - 1, pageSize));
        verify(productRepository, times(0))
                .findAllByNameContaining("", PageRequest.of(pageNumber - 1, pageSize));
        verify(productRepository, times(1))
                .findAllByNameContainingOrderByPrice("", PageRequest.of(pageNumber - 1, pageSize));
    }

    @Test
    void findAllBySearchAndSort_shouldFindAllProductsNotSorted() {
        var pageNumber = 1;
        var pageSize = 5;
        when(productRepository.findAllByNameContaining("", PageRequest.of(pageNumber - 1, pageSize)))
                .thenReturn(new PageImpl<>(productList));

        var products = productService.findAllBySearchAndSort("", "NO", pageSize, pageNumber);

        assertEquals(productList.size(), products.size());
        assertEquals(productList.get(0), products.get(0));
        assertEquals(productList.get(1), products.get(1));
        assertEquals(productList.get(2), products.get(2));

        verify(productRepository, times(0))
                .findAllByNameContainingOrderByName("", PageRequest.of(pageNumber - 1, pageSize));
        verify(productRepository, times(1))
                .findAllByNameContaining("", PageRequest.of(pageNumber - 1, pageSize));
        verify(productRepository, times(0))
                .findAllByNameContainingOrderByPrice("", PageRequest.of(pageNumber - 1, pageSize));
    }

    @Test
    void findById_shouldReturnProductById() {
        var expected = productList.get(0);
        when(productRepository.findById(1L)).thenReturn(Optional.of(expected));

        var product = productService.findById(1L);

        assertEquals(expected, product);

        verify(productRepository, times(1)).findById(1L);
    }

    @Test
    void findById_shouldReturnNoSuchElementExceptionWhenNotExists() {
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(
                NoSuchElementException.class,
                () -> productService.findById(1L),
                "Expected findById() to throw, but it didn't"
        );

        verify(productRepository, times(1)).findById(1L);
    }

    @Test
    void addNewProduct_shouldSaveNewProduct() throws IOException {
        var newProduct = new Product();
        newProduct.setName(productList.get(0).getName());
        newProduct.setImage(productList.get(0).getImage());
        newProduct.setDescription(productList.get(0).getDescription());
        newProduct.setPrice(productList.get(0).getPrice());

        productService.addNewProduct(
                newProduct.getName(),
                new MockMultipartFile("mock image", "newImage.jpg", "image/jpeg", newProduct.getImage()),
                newProduct.getDescription(),
                newProduct.getPrice()
        );

        verify(productRepository, times(1)).save(newProduct);
    }

}