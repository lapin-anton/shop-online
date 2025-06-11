package ru.yandex_practicum.shoponline.repository.redis;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.yandex_practicum.shoponline.model.redis.Product;

@Repository
public interface ProductRedisRepository extends CrudRepository<Product, Long> {

}
