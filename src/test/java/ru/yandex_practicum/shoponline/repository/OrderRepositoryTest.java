package ru.yandex_practicum.shoponline.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.yandex_practicum.shoponline.model.entity.Order;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@DataJpaTest
class OrderRepositoryTest {

    @Autowired
    private OrderRepository orderRepository;

    @BeforeEach
    void setUp() {
        orderRepository.deleteAll();
    }

    @Test
    void findByCreatedAtIsNull_shouldReturnOrderWithoutCreatedAt() {
        var orders = List.of(
                new Order(1234.56),
                new Order(5432.21, Timestamp.valueOf(LocalDateTime.now())),
                new Order(9999.99, Timestamp.valueOf("2025-04-28 12:30:00"))
        );
        orderRepository.saveAll(orders);

        var cart = orderRepository.findByCreatedAtIsNull().orElse(null);

        assertNotNull(cart);
        assertEquals(1234.56, cart.getTotalSum());
        assertNull(cart.getCreatedAt());
    }

}