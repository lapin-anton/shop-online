package ru.yandex_practicum.shoponline.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.yandex_practicum.shoponline.ShopOnlineApplicationTests;
import ru.yandex_practicum.shoponline.model.entity.Item;
import ru.yandex_practicum.shoponline.model.entity.Order;
import ru.yandex_practicum.shoponline.model.entity.Product;
import ru.yandex_practicum.shoponline.repository.ItemRepository;
import ru.yandex_practicum.shoponline.repository.OrderRepository;
import ru.yandex_practicum.shoponline.repository.ProductRepository;

import static org.assertj.core.api.Assertions.assertThat;

class ItemServiceIntegrationTest extends ShopOnlineApplicationTests {

    @Autowired
    private ItemService itemService;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderRepository orderRepository;

    @BeforeEach
    void setUp() {
        itemRepository.deleteAll();
        orderRepository.deleteAll();
        productRepository.deleteAll();
    }

    @Test
    void saveItem_shouldSaveNewItem() {
        Product product = productRepository.save(new Product()).block();
        Order order = orderRepository.save(new Order()).block();
        Item item = new Item(product.getId(), order.getId(), 5);
        itemService.saveItem(item)
                .doOnNext(it -> assertThat(it)
                        .isNotNull()
                        .extracting(Item::getId)
                        .isNotNull()
                ).block();
    }

    @Test
    void findByOrderId_shouldGetAllItemsByOrder() {
        Product product1 = productRepository.save(new Product()).block();
        Order order = orderRepository.save(new Order()).block();
        Item item1 = new Item(order.getId(), product1.getId(), 3);
        item1 = itemService.saveItem(item1).block();

        var foundedItems = itemService.findByOrderId(order.getId())
                .toIterable();

        assertThat(foundedItems)
                .isNotEmpty()
                .hasSize(1)
                .extracting(Item::getOrderId)
                .containsExactlyInAnyOrder(item1.getOrderId());

        assertThat(foundedItems)
                .extracting(Item::getProductId)
                .containsExactlyInAnyOrder(item1.getProductId());
    }

    @Test
    void updateItemQuantity_shouldIncreaseItemQuantity() {
        Product product = productRepository.save(new Product()).block();
        Order order = orderRepository.save(new Order()).block();
        Item item = new Item(order.getId(), product.getId(), 3);
        item = itemService.saveItem(item).block();

        itemService.updateItemQuantity("plus", item)
                .doOnNext(it -> assertThat(it)
                .extracting(Item::getCount)
                .isEqualTo(4)
        ).block();
    }

    @Test
    void updateItemQuantity_shouldDecreaseItemQuantity() {
        Product product = productRepository.save(new Product()).block();
        Order order = orderRepository.save(new Order()).block();
        Item item = new Item(order.getId(), product.getId(), 3);
        item = itemService.saveItem(item).block();

        itemService.updateItemQuantity("minus", item)
                .doOnNext(it -> assertThat(it)
                        .extracting(Item::getCount)
                        .isEqualTo(2)
                ).block();
    }

    @Test
    void updateItemQuantity_shouldDeleteItemFromOrder() {
        Product product = productRepository.save(new Product()).block();
        Order order = orderRepository.save(new Order()).block();
        Item item = new Item(order.getId(), product.getId(), 1);
        item = itemService.saveItem(item).block();

        itemService.updateItemQuantity("delete", item).block();

        var foundedItems = itemService.findByOrderId(order.getId())
                .toIterable();

        assertThat(foundedItems)
                .isEmpty();
    }
}