package ru.yandex_practicum.shoponline.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;
import ru.yandex_practicum.shoponline.model.dto.ItemDto;
import ru.yandex_practicum.shoponline.model.entity.Order;
import ru.yandex_practicum.shoponline.repository.OrderRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
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
    void getCart_shouldReturnExistingCart() {
        Order existingCart = new Order();
        existingCart.setTotalSum(0.0);

        when(orderRepository.findByCreatedAtIsNull()).thenReturn(Mono.just(existingCart));

        Mono<Order> cart = orderService.getCart();

        cart.subscribe(result -> {
            assertNotNull(result);
            assertEquals(existingCart.getTotalSum(), result.getTotalSum());
        });

        verify(orderRepository, times(1)).findByCreatedAtIsNull();
    }

    @Test
    void getCart_shouldCreateNewCartIfNoneExists() {
        when(orderRepository.findByCreatedAtIsNull()).thenReturn(Mono.empty());

        Mono<Order> cart = orderService.getCart();

        cart.subscribe(result -> {
            assertNotNull(result);
            assertEquals(0.0, result.getTotalSum());
        });

        verify(orderRepository, times(1)).findByCreatedAtIsNull();
    }

    @Test
    void saveNewCart_shouldSaveCart() {
        Order cart = new Order();
        cart.setTotalSum(0.0);

        when(orderRepository.save(any(Order.class))).thenReturn(Mono.just(cart));

        Mono<Order> savedCart = orderService.saveNewCart(cart);

        savedCart.subscribe(result -> {
            assertNotNull(result);
            assertEquals(cart.getTotalSum(), result.getTotalSum());
        });

        verify(orderRepository, times(1)).save(cart);
    }

    @Test
    void findOrder_shouldReturnOrder() {
        Long orderId = 1L;
        Order order = new Order();

        when(orderRepository.findById(orderId)).thenReturn(Mono.just(order));

        Mono<Order> foundOrder = orderService.findOrder(orderId);

        foundOrder.subscribe(result -> {
            assertNotNull(result);
            assertEquals(order, result);
        });

        verify(orderRepository, times(1)).findById(orderId);
    }

    @Test
    void findOrder_shouldReturnEmptyWhenOrderNotFound() {
        Long orderId = 1L;

        when(orderRepository.findById(orderId)).thenReturn(Mono.empty());

        Mono<Order> foundOrder = orderService.findOrder(orderId);

        foundOrder.subscribe(Assertions::assertNull);

        verify(orderRepository, times(1)).findById(orderId);
    }

    @Test
    void saveCart_shouldSaveUpdatedCart() {
        Order cart = new Order();
        cart.setTotalSum(0.0);

        ItemDto item1 = new ItemDto(10.0, 2);
        ItemDto item2 = new ItemDto(5.0, 3);

        List<ItemDto> cartItems = List.of(item1, item2);

        when(orderRepository.save(any(Order.class))).thenReturn(Mono.just(cart));

        Mono<Order> updatedCart = orderService.saveCart(cart, cartItems);

        updatedCart.subscribe(result -> {
            assertNotNull(result);
            assertEquals(35.0, result.getTotalSum());
        });

        verify(orderRepository, times(1)).save(cart);
    }

    @Test
    void createNewOrder_shouldSaveOrderWithTimestamp() {
        Order newOrder = new Order();
        when(orderRepository.save(any(Order.class))).thenReturn(Mono.just(newOrder));

        Mono<Order> createdOrder = orderService.createNewOrder(newOrder);

        createdOrder.subscribe(result -> {
            assertNotNull(result);
            assertNotNull(result.getCreatedAt());
            assertEquals(newOrder.getCreatedAt(), result.getCreatedAt());
        });

        verify(orderRepository, times(1)).save(newOrder);
    }

}