package ru.yandex_practicum.shoponline.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.yandex_practicum.shoponline.model.entity.Product;
import ru.yandex_practicum.shoponline.repository.ProductRepository;

import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public List<Product> findAllBySearchAndSort(String search, String sort, int pageSize, int pageNumber) {
        var founded = productRepository.findAllByName(search, PageRequest.of(pageNumber - 1, pageSize));
        if (sort.equals("ALPHA")) {
            founded.sort(Comparator.comparing(Product::getName));
        } else if (sort.equals("PRICE")) {
            founded.sort(Comparator.comparing(Product::getPrice));
        }
        return founded;
    }

    public Product findById(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(NoSuchElementException::new);
    }

    //
}
