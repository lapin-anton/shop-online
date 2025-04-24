package ru.yandex_practicum.shoponline.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex_practicum.shoponline.model.entity.Order;
import ru.yandex_practicum.shoponline.repository.OrderRepository;

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
        return orderRepository.findByCreatedAtIsNull().orElseThrow(NoSuchElementException::new);
    }

    public Order findOrder(Long orderId) {
        return orderRepository.findById(orderId).orElseThrow(NoSuchElementException::new);
    }

}
