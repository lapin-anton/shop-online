package ru.yandex_practicum.shoponline.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.yandex_practicum.shoponline.model.dto.ItemDto;
import ru.yandex_practicum.shoponline.model.entity.Order;
import ru.yandex_practicum.shoponline.repository.OrderRepository;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;

    public Flux<Order> findAllOrders() {
        return orderRepository.findByCreatedAtIsNotNull();
    }

    public Mono<Order> getCart() {
        Mono<Order> cart = orderRepository.findByCreatedAtIsNull();

        return cart
                .flatMap(Mono::just)
                .defaultIfEmpty(createNewCart());
    }

    private Order createNewCart() {
        var cart = new Order();
        cart.setTotalSum(0.0);
        return cart;
    }

    public Mono<Order> saveNewCart(Order cart) {
        return orderRepository.save(cart);
    }

    public Mono<Order> findOrder(Long orderId) {
        return orderRepository.findById(orderId);
    }

    public Mono<Order> saveCart(Order cart, List<ItemDto> cartItems) {
        cart.setTotalSum(calculateTotalSum(cartItems));
        return orderRepository.save(cart);
    }

    private Double calculateTotalSum(List<ItemDto> items) {
        var sum = 0.0;
        for (var item: items) {
            sum += item.getPrice() * item.getCount();
        }
        return sum;
    }

    public Mono<Order> createNewOrder(Order newOrder) {
        newOrder.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));
        return orderRepository.save(newOrder);
    }

}
