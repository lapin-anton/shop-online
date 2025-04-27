package ru.yandex_practicum.shoponline.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.yandex_practicum.shoponline.model.entity.Item;
import ru.yandex_practicum.shoponline.model.entity.Product;
import ru.yandex_practicum.shoponline.repository.ItemRepository;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

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
        var product = new Product(1L, "Test Product Title", "Test Product Name", "Test Product Image".getBytes(), 666.66);
        var item = new Item(1L, product, 10);

        itemService.saveItem(item);

        verify(itemRepository, times(1)).save(item);
    }

    @Test
    void deleteItem_shouldDeleteItem() {
        var product = new Product(1L, "Test Product Title", "Test Product Name", "Test Product Image".getBytes(), 666.66);
        var item = new Item(1L, product, 10);

        itemService.deleteItem(item);

        verify(itemRepository, times(1)).delete(item);
    }

}