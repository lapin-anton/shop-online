package ru.yandex_practicum.shoponline.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import ru.yandex_practicum.shoponline.model.entity.ItemsOrders;
import ru.yandex_practicum.shoponline.repository.ItemsOrdersRepository;

@Service
@RequiredArgsConstructor
public class ItemsOrdersService {

    private final ItemsOrdersRepository itemsOrdersRepository;

    public Flux<ItemsOrders> findAllItemOrdersByOrderId(Long orderId) {
        return itemsOrdersRepository.findAllByOrderId(orderId);
    }

}
