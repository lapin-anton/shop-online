package ru.yandex_practicum.shoponline.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex_practicum.shoponline.config.DataSourceConfig;
import ru.yandex_practicum.shoponline.model.entity.Item;
import ru.yandex_practicum.shoponline.model.entity.Order;
import ru.yandex_practicum.shoponline.model.entity.Product;
import ru.yandex_practicum.shoponline.repository.ItemRepository;
import ru.yandex_practicum.shoponline.repository.OrderRepository;
import ru.yandex_practicum.shoponline.repository.ProductRepository;

import java.sql.Timestamp;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.xpath;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Import(DataSourceConfig.class)
class ShopControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    @BeforeEach
    void setUp() {
        itemRepository.deleteAll();
        orderRepository.deleteAll();
        productRepository.deleteAll();
    }

    @Test
    void showMainPage_shoudReturnMainPage() throws Exception {
        var products = List.of(
                new Product("t-short", "test t-short", "t-short image".getBytes(), 50.00),
                new Product("trousers", "test trousers", "trousers image".getBytes(), 150.00),
                new Product("sneakers", "test sneakers", "sneakers image".getBytes(), 100.00)
        );
        productRepository.saveAll(products);

        mockMvc.perform(get("/")
                        .param("pageSize", "5")
                        .param("pageNumber", "1")
                        .param("search", "")
                        .param("sort", "NO")
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andExpect(view().name("main"))
                .andExpect(model().attributeExists("items"))
                .andExpect(model().attributeExists("paging"))
                .andExpect(model().attributeExists("search"))
                .andExpect(model().attributeExists("sort"))
                .andExpect(xpath("/html/body/table/tr").nodeCount(4)) // 3 rows for products + 1 row search form
                .andExpect(xpath("/html/body/table/tr[2]/td[1]/table/tr[2]/td[1]/b").string("t-short"))
                .andExpect(xpath("/html/body/table/tr[3]/td[1]/table/tr[2]/td[1]/b").string("trousers"))
                .andExpect(xpath("/html/body/table/tr[4]/td[1]/table/tr[2]/td[1]/b").string("sneakers"));
    }

    @Test
    void showMainPage_shoudReturnProductBySearchOnly() throws Exception {
        var products = List.of(
                new Product("t-short", "test t-short", "t-short image".getBytes(), 50.00),
                new Product("trousers", "test trousers", "trousers image".getBytes(), 150.00),
                new Product("sneakers", "test sneakers", "sneakers image".getBytes(), 100.00)
        );
        productRepository.saveAll(products);

        mockMvc.perform(get("/")
                        .param("pageSize", "5")
                        .param("pageNumber", "1")
                        .param("search", "ers")
                        .param("sort", "NO")
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andExpect(view().name("main"))
                .andExpect(model().attributeExists("items"))
                .andExpect(model().attributeExists("paging"))
                .andExpect(model().attributeExists("search"))
                .andExpect(model().attributeExists("sort"))
                .andExpect(xpath("/html/body/table/tr").nodeCount(3)) // 2 rows for products + 1 row search form
                .andExpect(xpath("/html/body/table/tr[2]/td[1]/table/tr[2]/td[1]/b").string("trousers"))
                .andExpect(xpath("/html/body/table/tr[3]/td[1]/table/tr[2]/td[1]/b").string("sneakers"));
    }

    @Test
    void showMainPage_shoudReturnProductOnPageOnly() throws Exception {
        var products = List.of(
                new Product("t-short", "test t-short", "t-short image".getBytes(), 50.00),
                new Product("trousers", "test trousers", "trousers image".getBytes(), 150.00),
                new Product("sneakers", "test sneakers", "sneakers image".getBytes(), 100.00)
        );
        productRepository.saveAll(products);

        mockMvc.perform(get("/")
                        .param("pageSize", "2")
                        .param("pageNumber", "1")
                        .param("search", "")
                        .param("sort", "NO")
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andExpect(view().name("main"))
                .andExpect(model().attributeExists("items"))
                .andExpect(model().attributeExists("paging"))
                .andExpect(model().attributeExists("search"))
                .andExpect(model().attributeExists("sort"))
                .andExpect(xpath("/html/body/table/tr").nodeCount(3)) // 2 rows for products + 1 row search form
                .andExpect(xpath("/html/body/table/tr[2]/td[1]/table/tr[2]/td[1]/b").string("t-short"))
                .andExpect(xpath("/html/body/table/tr[3]/td[1]/table/tr[2]/td[1]/b").string("trousers"));
    }

    @Test
    void showMainPage_shoudReturnProductSortedByName() throws Exception {
        var products = List.of(
                new Product("t-short", "test t-short", "t-short image".getBytes(), 50.00),
                new Product("trousers", "test trousers", "trousers image".getBytes(), 150.00),
                new Product("sneakers", "test sneakers", "sneakers image".getBytes(), 100.00)
        );
        productRepository.saveAll(products);

        mockMvc.perform(get("/")
                        .param("pageSize", "5")
                        .param("pageNumber", "1")
                        .param("search", "")
                        .param("sort", "ALPHA")
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andExpect(view().name("main"))
                .andExpect(model().attributeExists("items"))
                .andExpect(model().attributeExists("paging"))
                .andExpect(model().attributeExists("search"))
                .andExpect(model().attributeExists("sort"))
                .andExpect(xpath("/html/body/table/tr").nodeCount(4)) // 3 rows for products + 1 row search form
                .andExpect(xpath("/html/body/table/tr[2]/td[1]/table/tr[2]/td[1]/b").string("sneakers"))
                .andExpect(xpath("/html/body/table/tr[3]/td[1]/table/tr[2]/td[1]/b").string("t-short"))
                .andExpect(xpath("/html/body/table/tr[4]/td[1]/table/tr[2]/td[1]/b").string("trousers"));
    }

    @Test
    void showMainPage_shoudReturnProductSortedByPrice() throws Exception {
        var products = List.of(
                new Product("t-short", "test t-short", "t-short image".getBytes(), 50.00),
                new Product("trousers", "test trousers", "trousers image".getBytes(), 150.00),
                new Product("sneakers", "test sneakers", "sneakers image".getBytes(), 100.00)
        );
        productRepository.saveAll(products);

        mockMvc.perform(get("/")
                        .param("pageSize", "5")
                        .param("pageNumber", "1")
                        .param("search", "")
                        .param("sort", "PRICE")
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andExpect(view().name("main"))
                .andExpect(model().attributeExists("items"))
                .andExpect(model().attributeExists("paging"))
                .andExpect(model().attributeExists("search"))
                .andExpect(model().attributeExists("sort"))
                .andExpect(xpath("/html/body/table/tr").nodeCount(4)) // 3 rows for products + 1 row search form
                .andExpect(xpath("/html/body/table/tr[2]/td[1]/table/tr[2]/td[2]/b").string("50.0 руб."))
                .andExpect(xpath("/html/body/table/tr[3]/td[1]/table/tr[2]/td[2]/b").string("100.0 руб."))
                .andExpect(xpath("/html/body/table/tr[4]/td[1]/table/tr[2]/td[2]/b").string("150.0 руб."));
    }

    @Test
    void downloadImage_shouldReturnImageResource() throws Exception {
        var image = "test image".getBytes();
        var product = new Product("t-short", "test t-short", image, 50.00);
        product = productRepository.save(product);

        mockMvc.perform(get("/images/{postId}", product.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/octet-stream"))
                .andExpect(header().string("Content-Length", String.valueOf(image.length)))
                .andExpect(content().bytes(image));
    }

    @Test
    void changeItemCountOnMain_shouldIncreaseItemCount() throws Exception {
        var product = new Product("t-short", "test t-short", "t-short image".getBytes(), 50.00);
        product = productRepository.save(product);

        mockMvc.perform(post("/main/item/" + product.getId())
                .param("action", "plus"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));

        mockMvc.perform(get("/")
                        .param("pageSize", "5")
                        .param("pageNumber", "1")
                        .param("search", "")
                        .param("sort", "")
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andExpect(view().name("main"))
                .andExpect(xpath("/html/body/table/tr").nodeCount(2))
                .andExpect(xpath("/html/body/table/tr[2]/td[1]/table/tr[4]/td/form/span").string("1"));
    }

    @Test
    void changeItemCountOnMain_shouldDecreaseItemCount() throws Exception {
        var product = new Product("t-short", "test t-short", "t-short image".getBytes(), 50.00);
        product = productRepository.save(product);

        mockMvc.perform(post("/main/item/" + product.getId())
                        .param("action", "minus"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));

        mockMvc.perform(get("/")
                        .param("pageSize", "5")
                        .param("pageNumber", "1")
                        .param("search", "")
                        .param("sort", "")
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andExpect(view().name("main"))
                .andExpect(xpath("/html/body/table/tr").nodeCount(2))
                .andExpect(xpath("/html/body/table/tr[2]/td[1]/table/tr[4]/td/form/span").string("-1"));
    }

    @Test
    void showItem_shouldShowItemById() throws Exception {
        var product = new Product("t-short", "test t-short", "t-short image".getBytes(), 50.00);
        product = productRepository.save(product);

        mockMvc.perform(get("/item/" + product.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andExpect(view().name("item"))
                .andExpect(model().attributeExists("item"))
                .andExpect(xpath("/html/body/div/p[2]/b").string(product.getName()));
    }

    @Test
    void changeItemCount_shouldIncreaseItemCountOnItemPage() throws Exception {
        var product = new Product("t-short", "test t-short", "t-short image".getBytes(), 50.00);
        product = productRepository.save(product);

        mockMvc.perform(post("/item/" + product.getId())
                        .param("action", "plus"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/item/" + product.getId()));

        mockMvc.perform(get("/item/" + product.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andExpect(view().name("item"))
                .andExpect(xpath("/html/body/div/form/span").string("1"));
    }

    @Test
    void showOrders_shouldReturnAllOrders() throws Exception {
        var product1 = new Product("t-short", "test t-short", "t-short image".getBytes(), 50.00);
        var product2 = new Product("trousers", "test trousers", "trousers image".getBytes(), 150.00);
        var product3 = new Product("sneakers", "test sneakers", "sneakers image".getBytes(), 100.00);
        product1 = productRepository.save(product1);
        product2 = productRepository.save(product2);
        product3 = productRepository.save(product3);
        var orders = List.of(
                new Order(300.0, Timestamp.valueOf("2025-04-29 12:34:56")),
                new Order(200.0, Timestamp.valueOf("2025-04-30 12:34:56"))
        );
        var itemList1 = List.of(
                new Item(product1, 1),
                new Item(product2, 1),
                new Item(product3, 1)
        );
        itemRepository.saveAll(itemList1);
        orders.get(0).setItems(itemList1);
        var itemList2 = List.of(
                new Item(product1, 1),
                new Item(product2, 1)
        );
        itemRepository.saveAll(itemList2);
        orders.get(0).setItems(itemList2);
        orderRepository.saveAll(orders);

        mockMvc.perform(get("/orders"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andExpect(view().name("orders"))
                .andExpect(model().attributeExists("orders"))
                .andExpect(xpath("/html/body/table/tr").nodeCount(2))
                .andExpect(xpath("/html/body/table/tr[1]/td/p/b").string("Сумма: 300.0 руб."))
                .andExpect(xpath("/html/body/table/tr[2]/td/p/b").string("Сумма: 200.0 руб."));
    }

    @Test
    void showOrder_shouldReturnOrder() throws Exception {
        var product1 = new Product("t-short", "test t-short", "t-short image".getBytes(), 50.00);
        product1 = productRepository.save(product1);
        var item1 = new Item(product1, 1);
        item1 = itemRepository.save(item1);
        var order = new Order(300.0, Timestamp.valueOf("2025-04-29 12:34:56"));
        order.setItems(List.of(item1));
        order = orderRepository.save(order);
        // when order is old
        mockMvc.perform(get("/order/" + order.getId() + "/old"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andExpect(view().name("order"))
                .andExpect(model().attributeExists("order"))
                .andExpect(xpath("/html/body/table/tr").nodeCount(3)) //head + item(1) + totalSum
                .andExpect(xpath("/html/body/table/tr[1]/td/h2").string("Заказ №" +order.getId()));
        // when order is new
        mockMvc.perform(get("/order/" + order.getId() + "/new"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andExpect(view().name("order"))
                .andExpect(model().attributeExists("order"))
                .andExpect(xpath("/html/body/table/tr").nodeCount(3)) //head + item(1) + totalSum
                .andExpect(xpath("/html/body/h1").string("Поздравляем! Успешная покупка! \uD83D\uDE42"))
                .andExpect(xpath("/html/body/table/tr[1]/td/h2").string("Заказ №" +order.getId()));
    }

    @Test
    void showCart_shouldReturnCartPage() throws Exception {
        var product1 = new Product("t-short", "test t-short", "t-short image".getBytes(), 50.00);
        product1 = productRepository.save(product1);
        var item1 = new Item(product1, 1);
        item1 = itemRepository.save(item1);
        var cart = new Order(300.0);
        cart.setItems(List.of(item1));
        cart = orderRepository.save(cart);
        mockMvc.perform(get("/cart/items"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andExpect(view().name("cart"))
                .andExpect(model().attributeExists("items"))
                .andExpect(model().attributeExists("total"))
                .andExpect(xpath("/html/body/table/tr").nodeCount(3)) //item(1) + total + buy form
                .andExpect(xpath("/html/body/table/tr[2]/td/b").string("Итого: " + cart.getTotalSum() + " руб."));
    }

    @Test
    void changeItemCountOnCart_shouldDeleteItemFromCart() throws Exception {
        var product1 = new Product("t-short", "test t-short", "t-short image".getBytes(), 50.00);
        product1 = productRepository.save(product1);
        var item1 = new Item(product1, 1);
        item1 = itemRepository.save(item1);
        var cart = new Order(300.0);
        cart.setItems(List.of(item1));
        orderRepository.save(cart);

        mockMvc.perform(post("/cart/item/" + product1.getId())
                        .param("action", "delete"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/cart/items"));

        mockMvc.perform(get("/cart/items"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andExpect(view().name("cart"))
                .andExpect(xpath("/html/body/table/tr").nodeCount(2)) // item(0)
                .andExpect(xpath("/html/body/table/tr[1]/td/b").string("Итого: 0.0 руб."));
    }

    @Test
    void buy_shouldSaveCartAsNewOrder() throws Exception {
        var product1 = new Product("t-short", "test t-short", "t-short image".getBytes(), 50.00);
        product1 = productRepository.save(product1);
        var item1 = new Item(product1, 1);
        item1 = itemRepository.save(item1);
        var cart = new Order(300.0);
        cart.setItems(List.of(item1));
        cart = orderRepository.save(cart);

        mockMvc.perform(post("/buy"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/order/" + cart.getId() + "/new"));

        mockMvc.perform(get("/orders"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andExpect(view().name("orders"))
                .andExpect(model().attributeExists("orders"))
                .andExpect(xpath("/html/body/table/tr").nodeCount(1))
                .andExpect(xpath("/html/body/table/tr[1]/td/h2/a").string("Заказ №" + cart.getId()))
                .andExpect(xpath("/html/body/table/tr[1]/td/p/b").string("Сумма: " + cart.getTotalSum() + " руб."));
    }

    @Test
    void showAddItemForm_shouldReturnAddItemFormPage() throws Exception {
        mockMvc.perform(get("/items/add"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andExpect(view().name("add-item"));
    }

    @Test
    void saveNewItem_shouldSaveItem() throws Exception {
        MockMultipartFile mockImage = new MockMultipartFile(
                "test-image",
                "test-image.png",
                "image/png",
                "Mock Image Content".getBytes()
        );

        mockMvc.perform(multipart("/saveItem")
                        .file("image", mockImage.getBytes())
                        .param("name", "test name")
                        .param("description", "test description")
                        .param("price", "150.0")
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));

        mockMvc.perform(get("/")
                        .param("pageSize", "5")
                        .param("pageNumber", "1")
                        .param("search", "")
                        .param("sort", "NO")
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andExpect(view().name("main"))
                .andExpect(model().attributeExists("items"))
                .andExpect(model().attributeExists("paging"))
                .andExpect(model().attributeExists("search"))
                .andExpect(model().attributeExists("sort"))
                .andExpect(xpath("/html/body/table/tr").nodeCount(2)) // 1 rows for new product + 1 row search form
                .andExpect(xpath("/html/body/table/tr[2]/td[1]/table/tr[2]/td[1]/b").string("test name"))
                .andExpect(xpath("/html/body/table/tr[2]/td[1]/table/tr[2]/td[2]/b").string("150.0 руб."))
                .andExpect(xpath("/html/body/table/tr[2]/td[1]/table/tr[3]/td").string("test description"));
    }

}