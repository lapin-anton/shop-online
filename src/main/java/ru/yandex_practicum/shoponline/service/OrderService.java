package ru.yandex_practicum.shoponline.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex_practicum.shoponline.model.entity.Item;
import ru.yandex_practicum.shoponline.model.entity.Order;
import ru.yandex_practicum.shoponline.repository.OrderRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;

    public List<Order> findAllOrders() {
        return orderRepository.findAll();
    }

    public Order getCart() {
        return orderRepository.findByCreatedAtIsNull()
                .orElse(createNewCart());
    }

    private Order createNewCart() {
        var cart = new Order();
        cart.setTotalSum(0.0);
        cart.setItems(new ArrayList<>());
        return cart;
    }

    public Order findOrder(Long orderId) {
        return orderRepository.findById(orderId).orElseThrow(NoSuchElementException::new);
    }

    public void saveCart(Order cart) {
        cart.setTotalSum(calculateTotalSum(cart.getItems()));
        orderRepository.save(cart);
    }

    private Double calculateTotalSum(List<Item> items) {
        var sum = 0.0;
        for (var item: items) {
            sum += item.getProduct().getPrice() * item.getCount();
        }
        return sum;
    }

}
