package ru.yandex_practicum.shoponline.model.redis;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@RedisHash(
        value = "product",
        timeToLive = 60
)
public record Product(
    @Id
    Long id,
    String name,
    String description,
    byte[] image,
    Double price
){}
