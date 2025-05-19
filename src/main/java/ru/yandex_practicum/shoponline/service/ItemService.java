package ru.yandex_practicum.shoponline.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.yandex_practicum.shoponline.model.entity.Item;
import ru.yandex_practicum.shoponline.model.entity.Order;
import ru.yandex_practicum.shoponline.repository.ItemRepository;

@Service
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;

    public Mono<Item> findById(Long itemId) {
        return itemRepository.findById(itemId);
    }

    @Transactional
    public Mono<Item> saveItem(Item item) {
        return itemRepository.save(item);
    }

    @Transactional
    public Mono<Void> deleteItem(Item item) {
        return itemRepository.delete(item);
    }

    public Mono<Item> findByIdAndProductId(Long itemId, Long productId) {
        return itemRepository.findByIdAndProductId(itemId, productId);
    }

    public Flux<Item> findByOrderId(Long orderId) {
        return itemRepository.findAllByOrderId(orderId);
    }

    @Transactional
    public Mono<Item> updateItemQuantity(String action, Item it) {
        it.setCount(action.equals("plus") ? it.getCount() + 1 : it.getCount() - 1);
        if (action.equals("delete") || it.getCount() == 0) {
            return itemRepository.delete(it).thenReturn(it);
        } else {
            return itemRepository.save(it);
        }
    }
}
