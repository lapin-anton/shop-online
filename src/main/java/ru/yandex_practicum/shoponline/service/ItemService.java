package ru.yandex_practicum.shoponline.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import ru.yandex_practicum.shoponline.model.entity.Item;
import ru.yandex_practicum.shoponline.repository.ItemRepository;

@Service
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;

    public Mono<Item> findById(Long itemId) {
        return itemRepository.findById(itemId);
    }

    public Mono<Item> saveItem(Item item) {
        return itemRepository.save(item);
    }

    public Mono<Void> deleteItem(Item item) {
        return itemRepository.delete(item);
    }
}
