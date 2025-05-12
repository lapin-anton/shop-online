package ru.yandex_practicum.shoponline.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.yandex_practicum.shoponline.model.entity.Item;
import ru.yandex_practicum.shoponline.model.entity.Order;
import ru.yandex_practicum.shoponline.repository.OrderRepository;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;

    public Flux<Order> findAllOrders() {
        return orderRepository.findAll();
    }

//    public Mono<Order> getCart() {
//        return orderRepository.findByCreatedAtIsNull()
//                .defaultIfEmpty(createNewCart());
//    }

//    private Order createNewCart() {
//        var cart = new Order();
//        cart.setTotalSum(0.0);
//        cart.setItems(new ArrayList<>());
//        return cart;
//    }
//
//    public Mono<Order> findOrder(Long orderId) {
//        return orderRepository.findById(orderId);
//    }
//
//    public void saveCart(Order cart) {
//        cart.setTotalSum(calculateTotalSum(cart.getItems()));
//        orderRepository.save(cart);
//    }
//
//    private Double calculateTotalSum(List<Item> items) {
//        var sum = 0.0;
//        for (var item: items) {
//            sum += item.getProduct().getPrice() * item.getCount();
//        }
//        return sum;
//    }
//
//    public void createOrder(Order newOrder) {
//        newOrder.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));
//        orderRepository.save(newOrder);
//    }
}
