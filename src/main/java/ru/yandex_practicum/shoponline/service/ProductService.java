package ru.yandex_practicum.shoponline.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.yandex_practicum.shoponline.model.entity.Product;
import ru.yandex_practicum.shoponline.repository.ProductRepository;

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public List<Product> findAllBySearchAndSort(String search, String sort, int pageSize, int pageNumber) {
        Page<Product> foundedPage;
        if (sort.equals("ALPHA")) {
            foundedPage = productRepository
                    .findAllByNameContainingOrderByName(search, PageRequest.of(pageNumber - 1, pageSize));
        } else if (sort.equals("PRICE")) {
            foundedPage = productRepository
                    .findAllByNameContainingOrderByPrice(search, PageRequest.of(pageNumber - 1, pageSize));
        } else {
            foundedPage = productRepository.findAllByNameContaining(search, PageRequest.of(pageNumber - 1, pageSize));
        }
        return foundedPage.toList();
    }

    public Product findById(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(NoSuchElementException::new);
    }

    public void addNewProduct(String name, MultipartFile image, String description, double price) throws IOException {
        var product = new Product(name, description, image.getBytes(), price);
        productRepository.save(product);
    }

}
