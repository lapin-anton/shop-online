package ru.yandex_practicum.shoponline.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.yandex_practicum.shoponline.model.entity.Item;
import ru.yandex_practicum.shoponline.repository.ItemRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ItemServiceTest {

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private ItemService itemService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void saveItem_shouldSaveItem() {
        Item item = new Item();
        item.setId(1L);
        item.setCount(10);

        when(itemRepository.save(any(Item.class))).thenReturn(Mono.just(item));

        Mono<Item> savedItem = itemService.saveItem(item);

        savedItem.subscribe(result -> {
            assertEquals(item.getId(), result.getId());
            assertEquals(item.getCount(), result.getCount());
        });

        verify(itemRepository, times(1)).save(item);
    }

    @Test
    void findByOrderId_shouldReturnItems() {
        Long orderId = 1L;
        Item item1 = new Item();
        item1.setId(1L);
        item1.setCount(10);

        Item item2 = new Item();
        item2.setId(2L);
        item2.setCount(5);

        when(itemRepository.findAllByOrderId(orderId)).thenReturn(Flux.just(item1, item2));

        Flux<Item> items = itemService.findByOrderId(orderId);

        items.collectList().subscribe(results -> {
            assertEquals(2, results.size());
            assertTrue(results.contains(item1));
            assertTrue(results.contains(item2));
        });

        verify(itemRepository, times(1)).findAllByOrderId(orderId);
    }

    @Test
    void updateItemQuantity_shouldIncreaseCount() {
        Item item = new Item();
        item.setId(1L);
        item.setCount(10);

        when(itemRepository.save(any(Item.class))).thenReturn(Mono.just(item));

        Mono<Item> updatedItem = itemService.updateItemQuantity("plus", item);

        updatedItem.subscribe(result -> {
            assertEquals(11, result.getCount());
        });

        verify(itemRepository, times(1)).save(item);
    }

    @Test
    void updateItemQuantity_shouldDecreaseCount() {
        Item item = new Item();
        item.setId(1L);
        item.setCount(10);

        when(itemRepository.save(any(Item.class))).thenReturn(Mono.just(item));

        Mono<Item> updatedItem = itemService.updateItemQuantity("minus", item);

        updatedItem.subscribe(result -> {
            assertEquals(9, result.getCount());
        });

        verify(itemRepository, times(1)).save(item);
    }

    @Test
    void updateItemQuantity_shouldDeleteItemWhenCountIsZero() {
        Item item = new Item();
        item.setId(1L);
        item.setCount(0);

        when(itemRepository.delete(any(Item.class))).thenReturn(Mono.empty());

        Mono<Item> deletedItem = itemService.updateItemQuantity("delete", item);

        deletedItem.subscribe(result -> {
            assertEquals(item.getId(), result.getId());
            assertEquals(0, result.getCount());
        });

        verify(itemRepository, times(1)).delete(item);
    }
}
