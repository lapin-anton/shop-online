package ru.yandex_practicum.shoponline.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.yandex_practicum.shoponline.ShopOnlineApplicationTests;
import ru.yandex_practicum.shoponline.model.dto.ItemDto;
import ru.yandex_practicum.shoponline.model.entity.Order;
import ru.yandex_practicum.shoponline.repository.OrderRepository;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class OrderServiceIntegrationTest extends ShopOnlineApplicationTests {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderRepository orderRepository;

    @BeforeEach
    void setUp() {
        orderRepository.deleteAll().block();
    }

    @Test
    void findAllOrders_shouldReturnAllOrdersWithCreatedAt() {
        List<Order> orders = List.of(
                new Order(1000.0, Timestamp.valueOf(LocalDateTime.now())),
                new Order(1500.0, Timestamp.valueOf(LocalDateTime.now())),
                new Order(3000.0)
        );
        orderRepository.saveAll(orders).subscribe();

        var founded = orderService.findAllOrders().toIterable();

        assertThat(founded)
                .isNotEmpty()
                .hasSize(2)
                .first()
                .extracting(Order::getTotalSum)
                .isEqualTo(1000.0);
    }

    @Test
    void getCart_shouldReturnNewCart() {
        orderService.getCart()
                .doOnNext(cart -> {
                    assertThat(cart)
                            .isNotNull()
                            .extracting(Order::getCreatedAt)
                            .isNull();

                    assertThat(cart)
                            .extracting(Order::getTotalSum)
                            .isEqualTo(0.0);
                })
                .block();
    }

    @Test
    void saveNewCart_shouldSaveCart() {
        var newCart = new Order(0.0);
        orderService.saveNewCart(newCart)
                .doOnNext(cart -> {
                    assertThat(cart)
                            .isNotNull()
                            .extracting(Order::getCreatedAt)
                            .isNull();

                    assertThat(cart)
                            .extracting(Order::getTotalSum)
                            .isEqualTo(0.0);
                })
                .block();
    }

    @Test
    void findOrder_shouldFindOrderById() {
        List<Order> orders = List.of(
                new Order(1000.0, Timestamp.valueOf(LocalDateTime.now())),
                new Order(1500.0, Timestamp.valueOf(LocalDateTime.now())),
                new Order(3000.0)
        );
        orderRepository.saveAll(orders).subscribe();

        orderService.findOrder(1L)
                .doOnNext(order -> {
                    assertThat(order)
                            .isNotNull()
                            .extracting(Order::getId)
                            .isEqualTo(1L);

                    assertThat(order)
                            .extracting(Order::getTotalSum)
                            .isEqualTo(1000.0);

                    assertThat(order)
                            .extracting(Order::getCreatedAt)
                            .isNotNull();
                })
                .block();
    }

    @Test
    void saveCart_shouldSaveCartWithTotalSum() {
        List<ItemDto> itemDtos = List.of(
                new ItemDto(1000.0, 3),
                new ItemDto(2000.0, 2),
                new ItemDto(3000.0, 1)
        );

        Order cart = new Order(0.0);
        orderService.saveCart(cart, itemDtos)
                .doOnNext(c -> {
                    assertThat(c)
                            .isNotNull()
                            .extracting(Order::getTotalSum)
                            .isEqualTo(10000.0);
                }).block();
    }

    @Test
    void createNewOrder_shouldSaveOrderWithCreatedAt() {
        Order cart = new Order(1000.0);

        orderService.createNewOrder(cart)
                .doOnNext(order -> {
                    assertThat(order)
                            .isNotNull()
                            .extracting(Order::getTotalSum)
                            .isEqualTo(1000.0);

                    assertThat(order)
                            .extracting(Order::getCreatedAt)
                            .isNotNull();
                }).block();
    }

}