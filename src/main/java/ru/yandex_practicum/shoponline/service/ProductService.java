package ru.yandex_practicum.shoponline.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.yandex_practicum.shoponline.model.entity.Product;
import ru.yandex_practicum.shoponline.repository.ProductRepository;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public List<Product> findAllBySearchAndSort(String search, String sort, int pageSize, int pageNumber) {
        var foundedPage = productRepository.findAllByNameContaining(search, PageRequest.of(pageNumber - 1, pageSize));
        var foundedList = foundedPage.toList();
        if (sort.equals("ALPHA")) {
            foundedList.sort(Comparator.comparing(Product::getName));
        } else if (sort.equals("PRICE")) {
            foundedList.sort(Comparator.comparing(Product::getPrice));
        }
        return foundedList;
    }

    public Product findById(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(NoSuchElementException::new);
    }

    public Product findProductById(Long itemId) {
        return productRepository.findById(itemId).orElseThrow(NoSuchElementException::new);
    }

    @Transactional
    public void addNewProduct(String name, MultipartFile image, String description, double price) throws IOException {
        var product = new Product(name, description, image.getBytes(), price);
        productRepository.save(product);
    }

    //
}
