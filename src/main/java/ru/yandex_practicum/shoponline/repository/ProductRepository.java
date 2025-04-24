package ru.yandex_practicum.shoponline.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.yandex_practicum.shoponline.model.entity.Product;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    Page<Product> findAllByNameContaining(String search, Pageable pageable);

    Page<Product> findAllByNameContainingOrderByName(String search, Pageable pageable);

    Page<Product> findAllByNameContainingOrderByPrice(String search, Pageable pageable);

}
