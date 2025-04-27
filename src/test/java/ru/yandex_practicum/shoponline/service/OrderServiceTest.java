package ru.yandex_practicum.shoponline.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.yandex_practicum.shoponline.model.entity.Item;
import ru.yandex_practicum.shoponline.model.entity.Order;
import ru.yandex_practicum.shoponline.model.entity.Product;
import ru.yandex_practicum.shoponline.repository.OrderRepository;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderService orderService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getCart_shouldGetCart() {
        var expected = new Order();
        when(orderRepository.findByCreatedAtIsNull()).thenReturn(Optional.of(expected));

        var cart = orderService.getCart();

        assertEquals(expected, cart);
        verify(orderRepository, times(1)).findByCreatedAtIsNull();
    }

    @Test
    void getCart_shouldGetNewCartWhenNotFound() {
        var expected = new Order();
        expected.setTotalSum(0.0);
        expected.setItems(new ArrayList<>());
        when(orderRepository.findByCreatedAtIsNull()).thenReturn(Optional.empty());

        var cart = orderService.getCart();

        assertEquals(expected.getTotalSum(), cart.getTotalSum());
        assertEquals(expected.getItems().size(), cart.getItems().size());
        assertNull(cart.getCreatedAt());
        verify(orderRepository, times(1)).findByCreatedAtIsNull();
    }

    @Test
    void findOrder_shouldGetOrderById() {
        var expected = new Order();
        expected.setId(1L);
        expected.setTotalSum(2000.00);
        expected.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));
        expected.setItems(List.of(new Item(1L, new Product(), 3)));
        when(orderRepository.findById(1L)).thenReturn(Optional.of(expected));

        var order = orderService.findOrder(1L);

        assertEquals(expected.getId(), order.getId());
        assertEquals(expected.getTotalSum(), order.getTotalSum());
        assertEquals(expected.getCreatedAt(), order.getCreatedAt());
        assertEquals(expected.getItems().size(), order.getItems().size());
        assertEquals(expected.getItems().get(0).getId(), order.getItems().get(0).getId());
        assertEquals(expected.getItems().get(0).getProduct(), order.getItems().get(0).getProduct());
        assertEquals(expected.getItems().get(0).getCount(), order.getItems().get(0).getCount());
        verify(orderRepository, times(1)).findById(1L);
    }

    @Test
    void findOrder_shouldReturnExceptionWhenNotFound() {
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(
                NoSuchElementException.class,
                () -> orderService.findOrder(1L),
                "Expected findOrder() to throw, but it didn't"
        );

        verify(orderRepository, times(1)).findById(1L);
    }

    @Test
    void saveCart_shouldSaveCart() {
        var cart = new Order();
        cart.setItems(List.of());

        orderService.saveCart(cart);

        verify(orderRepository, times(1)).save(cart);
    }

    @Test
    void createOrder_shouldSaveNewOrder() {
        var order = new Order();

        orderService.createOrder(order);

        verify(orderRepository, times(1)).save(order);
    }

}