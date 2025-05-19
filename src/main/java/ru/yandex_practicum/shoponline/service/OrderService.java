package ru.yandex_practicum.shoponline.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.yandex_practicum.shoponline.model.dto.ItemDto;
import ru.yandex_practicum.shoponline.model.entity.Item;
import ru.yandex_practicum.shoponline.model.entity.Order;
import ru.yandex_practicum.shoponline.repository.OrderRepository;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;

    public Flux<Order> findAllOrders() {
        return orderRepository.findByCreatedAtIsNotNull();
    }

    public Mono<Order> getCart() {
        return orderRepository.findByCreatedAtIsNull()
                .defaultIfEmpty(createNewCart());
    }

    private Order createNewCart() {
        var cart = new Order();
        cart.setTotalSum(0.0);
        return cart;
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
//
//    public void createOrder(Order newOrder) {
//        newOrder.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));
//        orderRepository.save(newOrder);
//    }
}
