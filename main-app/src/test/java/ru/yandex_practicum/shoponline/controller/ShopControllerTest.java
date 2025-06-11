package ru.yandex_practicum.shoponline.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.yandex_practicum.shoponline.model.entity.Item;
import ru.yandex_practicum.shoponline.model.entity.Order;
import ru.yandex_practicum.shoponline.model.entity.Product;
import ru.yandex_practicum.shoponline.service.CartService;
import ru.yandex_practicum.shoponline.service.ItemService;
import ru.yandex_practicum.shoponline.service.OrderService;
import ru.yandex_practicum.shoponline.service.ProductService;
import ru.yandex_practicum.shoponline.util.CsvParserUtil;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@WebFluxTest(ShopController.class)
class ShopControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private ItemService itemService;

    @MockBean
    private OrderService orderService;

    @MockBean
    private ProductService productService;

    @MockBean
    private CartService cartService;

    @MockBean
    private CsvParserUtil csvParserUtil;

    @BeforeEach
    void setUp() {
    }

    @Test
    void showMainPage_shouldReturnMainPage() throws Exception {
        var search = "test";
        var sort = "NO";
        var pageSize = 5;
        var pageNumber = 1;

        var products = Arrays.asList(
                new Product("t-short", "test t-short", "t-short image".getBytes(), 50.00),
                new Product("trousers", "test trousers", "trousers image".getBytes(), 150.00),
                new Product("sneakers", "test sneakers", "sneakers image".getBytes(), 100.00)
        );

        when(productService.findAllBySearchAndSort(search, sort, pageSize, pageNumber))
                .thenReturn(Flux.fromIterable(products));

        when(cartService.getCartItemMap()).thenReturn(Mono.just(new HashMap<>()));

        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/")
                        .queryParam("pageSize", pageSize)
                        .queryParam("pageNumber", pageNumber)
                        .queryParam("search", search)
                        .queryParam("sort", sort)
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("text/html")
                .expectBody(String.class).consumeWith(response -> {
                    String body = response.getResponseBody();
                    assertNotNull(body);
                    assertTrue(body.contains("t-short"));
                    assertTrue(body.contains("trousers"));
                    assertTrue(body.contains("sneakers"));
                });

        Mockito.verify(productService, Mockito.times(1)).findAllBySearchAndSort(search, sort, pageSize, pageNumber);
    }


    @Test
    void downloadImage_shouldReturnImageResource() throws Exception {
        var productId = 1L;
        var image = "test image".getBytes();
        var product = new Product("t-short", "test t-short", image, 50.00);

        when(productService.findById(productId)).thenReturn(Mono.just(product));

        webTestClient.get()
                .uri("/images/" + productId)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/octet-stream")
                .expectHeader().contentLength(image.length)
                .expectBody(byte[].class).consumeWith(response -> {
                    byte[] binary = response.getResponseBody();
                    assertArrayEquals(image, binary);
                });

        Mockito.verify(productService, Mockito.times(1)).findById(productId);
    }

    @Test
    void changeItemCountOnMain_shouldRedirectToMainPage() throws Exception {
        var itemId = 1L;
        var action = "plus";
        var cart = new Order(1L, 0.0, null);

        when(orderService.getCart()).thenReturn(Mono.just(cart));

        when(cartService.updateCartItem(itemId, action))
                .thenReturn(Mono.just(cart));

        webTestClient.post().uri(uriBuilder -> uriBuilder
                .path("/main/item/" + itemId)
                .queryParam("action", action)
                .build())
                .exchange()
                .expectStatus().is3xxRedirection()
                .expectHeader().location("/");
    }

    @Test
    void showItem_shouldShowItemById() throws Exception {
        var itemId = 1L;
        var product = new Product("t-short", "test t-short", "t-short image".getBytes(), 50.00);

        when(productService.findById(itemId)).thenReturn(Mono.just(product));

        when(cartService.getCartItemMap()).thenReturn(Mono.just(new HashMap<>()));

        webTestClient.get().uri("/item/" + itemId)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("text/html")
                .expectBody(String.class).consumeWith(response -> {
                    String body = response.getResponseBody();
                    assertNotNull(body);
                    assertTrue(body.contains(product.getName()));
                    assertTrue(body.contains(product.getDescription()));
                });

        Mockito.verify(productService, Mockito.times(1)).findById(itemId);
    }

    @Test
    void changeItemCount_shouldRedirectOnItemPage() throws Exception {
        var itemId = 1L;
        var action = "plus";
        var cart = new Order(1L, 0.0, null);

        when(orderService.getCart()).thenReturn(Mono.just(cart));

        when(cartService.updateCartItem(itemId, action))
                .thenReturn(Mono.just(cart));

        webTestClient.post().uri(uriBuilder -> uriBuilder
                        .path("/item/" + itemId)
                        .queryParam("action", action)
                        .build())
                .exchange()
                .expectStatus().is3xxRedirection()
                .expectHeader().location("/item/" + itemId);
    }

    @Test
    void showOrders_shouldReturnAllOrders() throws Exception {
        var orders = Arrays.asList(
                new Order(1L, 1000.00, Timestamp.valueOf(LocalDateTime.now()))
        );

        when(orderService.findAllOrders()).thenReturn(Flux.fromIterable(orders));

        when(itemService.findByOrderId(1L)).thenReturn(Flux.just(new Item(1L, 1L, 1)));

        when(productService.findById(1L))
                .thenReturn(Mono.just(new Product(1L, "t-short", "test t-short", "t-short image".getBytes(), 50.00)));

        webTestClient.get()
                .uri("/orders")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("text/html")
                .expectBody(String.class).consumeWith(response -> {
                    String body = response.getResponseBody();
                    assertNotNull(body);
                    assertTrue(body.contains("Заказ №1"));
                    assertTrue(body.contains("t-short"));
                });
    }

    @Test
    void showOrder_shouldReturnOrder() throws Exception {
        var order = new Order(1L, 1000.00, Timestamp.valueOf(LocalDateTime.now()));

        when(orderService.findOrder(order.getId())).thenReturn(Mono.just(order));

        when(itemService.findByOrderId(order.getId()))
                .thenReturn(Flux.just(new Item(1L, 1L, 1)));

        when(productService.findById(1L))
                .thenReturn(Mono.just(new Product(1L, "t-short", "test t-short", "t-short image".getBytes(), 50.00)));

        webTestClient.get()
                .uri("/order/" + order.getId() + "/new")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("text/html")
                .expectBody(String.class).consumeWith(response -> {
                    String body = response.getResponseBody();
                    assertNotNull(body);
                    assertTrue(body.contains("Поздравляем! Успешная покупка! &#128578;"));
                    assertTrue(body.contains("Заказ №" + order.getId()));
                    assertTrue(body.contains("t-short"));
                });
    }

    @Test
    void showCart_shouldReturnCartPage() throws Exception {
        var cart = new Order(1L, 150.00, null);
        var item = new Item(1L, 1L, 3);

        when(orderService.getCart()).thenReturn(Mono.just(cart));

        when(itemService.findByOrderId(cart.getId())).thenReturn(Flux.just(item));

        when(productService.findById(item.getProductId()))
                .thenReturn(Mono.just(new Product(1L, "t-short", "test t-short", "t-short image".getBytes(), 50.00)));

        webTestClient.get()
                .uri("/cart/items")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("text/html")
                .expectBody(String.class).consumeWith(response -> {
                    String body = response.getResponseBody();
                    assertNotNull(body);
                    assertTrue(body.contains("t-short"));
                    assertTrue(body.contains("Итого: " + cart.getTotalSum() + " руб."));
                });
    }

    @Test
    void changeItemCountOnCart_shouldRedirectOnCartPage() throws Exception {
        var itemId = 1L;
        var action = "plus";
        var cart = new Order(1L, 0.0, null);

        when(orderService.getCart()).thenReturn(Mono.just(cart));

        when(cartService.updateCartItem(itemId, action))
                .thenReturn(Mono.just(cart));

        webTestClient.post().uri(uriBuilder -> uriBuilder
                        .path("/cart/item/" + itemId)
                        .queryParam("action", action)
                        .build())
                .exchange()
                .expectStatus().is3xxRedirection()
                .expectHeader().location("/cart/items");
    }

    @Test
    void buy_shouldRedirectOnNewOrderPage() throws Exception {
        var cart = new Order(1L, 150.00, null);

        when(orderService.getCart()).thenReturn(Mono.just(cart));
        when(orderService.createNewOrder(cart)).thenReturn(Mono.just(cart));

        webTestClient.post()
                .uri("/buy")
                .exchange()
                .expectStatus().is3xxRedirection()
                .expectHeader().location("/order/" + cart.getId() + "/new");
    }

    @Test
    void showAddItemForm_shouldReturnAddItemFormPage() throws Exception {
        webTestClient.get()
                .uri("/items/add")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("text/html")
                .expectBody(String.class).consumeWith(response -> {
                    String body = response.getResponseBody();
                    assertNotNull(body);
                    assertTrue(body.contains("<h3>Добавьте csv-файл для загрузки новых товаров</h3>"));
                });
    }

}