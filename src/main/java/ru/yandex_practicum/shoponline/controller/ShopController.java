package ru.yandex_practicum.shoponline.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.result.view.Rendering;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.yandex_practicum.shoponline.model.dto.OrderDto;
import ru.yandex_practicum.shoponline.model.entity.Item;
import ru.yandex_practicum.shoponline.model.entity.ItemsOrders;
import ru.yandex_practicum.shoponline.model.entity.Order;
import ru.yandex_practicum.shoponline.model.dto.ItemDto;
import ru.yandex_practicum.shoponline.model.entity.Product;
import ru.yandex_practicum.shoponline.model.other.Paging;
import ru.yandex_practicum.shoponline.service.ItemService;
import ru.yandex_practicum.shoponline.service.ItemsOrdersService;
import ru.yandex_practicum.shoponline.service.OrderService;
import ru.yandex_practicum.shoponline.service.ProductService;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ShopController {

    private final ProductService productService;

    private final OrderService orderService;

    private final ItemService itemService;

    private final ItemsOrdersService itemsOrdersService;

    @GetMapping("/")
    public Mono<String> showMainPage(Model model,
                                        @RequestParam(value = "pageSize", defaultValue = "5") int pageSize,
                                        @RequestParam(value = "pageNumber", defaultValue = "1") int pageNumber,
                                        @RequestParam(value = "search", defaultValue = "") String search,
                                        @RequestParam(value = "sort", defaultValue = "NO") String sort
    ) {
        Flux<Product> products = productService.findAllBySearchAndSort(search, sort, pageSize, pageNumber);
        Mono<Long> productCountMono = products.count();
        Mono<HashMap<Long, Item>> cartItemMap = getCartItemMap();
        Flux<ItemDto> items = products.flatMap(p ->
                cartItemMap.flatMap(map -> {
                    Item item = map.get(p.getId());
                    return Mono.just(new ItemDto(
                            p.getId(),
                            p.getId(),
                            p.getName(),
                            p.getDescription(),
                            p.getPrice(),
                            item != null ? item.getCount() : 0
                    ));
                })
        );
        Mono<Paging> paging = productCountMono
                .map(productCount -> new Paging(productCount, pageNumber, pageSize));
        model.addAttribute("items", items);
        model.addAttribute("paging", paging);
        model.addAttribute("search", search);
        model.addAttribute("sort", sort);
        return Mono.just("main");
    }

    @GetMapping("/images/{productId}")
    public Mono<ResponseEntity<Resource>> downloadImage(@PathVariable("productId") Long productId) {
        Mono<Product> productMono = productService.findById(productId);
        return productMono.map(product -> ResponseEntity.ok()
                .headers(new HttpHeaders())
                .contentLength(product.getImage().length)
                .contentType(MediaType.parseMediaType("application/octet-stream"))
                .body(new ByteArrayResource(product.getImage())));
    }
//
//    @Transactional
//    @PostMapping("/main/item/{itemId}")
//    public String changeItemCountOnMain(
//            @PathVariable("itemId") Long itemId,
//            @RequestParam("action") String action
//    ) {
//        var cart = orderService.getCart();
//        updateCartItems(itemId, action, cart);
//        orderService.saveCart(cart);
//        return "redirect:/";
//    }
//
    @GetMapping("/item/{itemId}")
    public String showItem(Model model, @PathVariable("itemId") Long itemId) {
        Mono<Product> productMono = productService.findById(itemId);
        Mono<HashMap<Long, Item>> cartItemMap = getCartItemMap();
        Mono<ItemDto> itemMono = productMono.flatMap(p ->
                cartItemMap.flatMap(map -> {
                    Item item = map.get(p.getId());
                    return Mono.just(new ItemDto(
                            p.getId(),
                            p.getId(),
                            p.getName(),
                            p.getDescription(),
                            p.getPrice(),
                            item != null ? item.getCount() : 0
                    ));
                })
        );
        model.addAttribute("item", itemMono);
        return "item";
    }
//
//    @Transactional
//    @PostMapping("/item/{itemId}")
//    public String changeItemCount(
//            @PathVariable("itemId") Long itemId,
//            @RequestParam("action") String action
//    ) {
//        var cart = orderService.getCart();
//        updateCartItems(itemId, action, cart);
//        orderService.saveCart(cart);
//        return "redirect:/item/" + itemId;
//    }
//
    @GetMapping("/orders")
    public Mono<String> showOrders(Model model) {
        Flux<Order> ordersFlux = orderService.findAllOrders();

        Flux<OrderDto> orderDtoFlux = ordersFlux.flatMap(order -> {
            Flux<ItemsOrders> itemsOrdersFlux = itemsOrdersService.findAllItemOrdersByOrderId(order.getId());
            Flux<Item> itemFlux = itemsOrdersFlux.flatMap(itor -> itemService.findById(itor.getItemId()));
            Mono<List<ItemDto>> itemDtoListMono = itemFlux.flatMap(item -> {
                Mono<Product> productMono = productService.findById(item.getProductId());
                return productMono.map(p -> new ItemDto(item.getId(), p.getId(), p.getName(), p.getDescription(), p.getPrice(), item.getCount()));
            }).collectList();
            return itemDtoListMono.map(itemDtoList -> new OrderDto(order.getId(), order.getTotalSum(), order.getCreatedAt(), itemDtoList));
        });

        model.addAttribute("orders", orderDtoFlux);
        return Mono.just("orders");
    }

    @GetMapping("/order/{orderId}/{newOrder}")
    public Mono<String> showOrder(Model model,
                            @PathVariable("orderId") Long orderId,
                            @PathVariable("newOrder") String newOrder) {
        Mono<Order> orderMono = orderService.findOrder(orderId);

        Mono<OrderDto> orderDtoMono = orderMono.flatMap(order -> {
            Flux<ItemsOrders> itemsOrdersFlux = itemsOrdersService.findAllItemOrdersByOrderId(order.getId());
            Flux<Item> itemFlux = itemsOrdersFlux.flatMap(itor -> itemService.findById(itor.getItemId()));
            Mono<List<ItemDto>> itemDtoListMono = itemFlux.flatMap(item -> {
                Mono<Product> productMono = productService.findById(item.getProductId());
                return productMono.map(p -> new ItemDto(item.getId(), p.getId(), p.getName(), p.getDescription(), p.getPrice(), item.getCount()));
            }).collectList();
            return itemDtoListMono.map(itemDtoList -> new OrderDto(order.getId(), order.getTotalSum(), order.getCreatedAt(), itemDtoList));
        });
        model.addAttribute("order", orderDtoMono);
        model.addAttribute("newOrder", "new".equals(newOrder));
        return Mono.just("order");
    }
//
//    @GetMapping("/cart/items")
//    public String showCart(Model model) {
//        var cart = orderService.getCart();
//        var items = cart.getItems();
//        model.addAttribute("items", items);
//        model.addAttribute("total", cart.getTotalSum());
//        return "cart";
//    }
//
//    @Transactional
//    @PostMapping("/cart/item/{itemId}")
//    public String changeItemCountOnCart(
//            @PathVariable("itemId") Long itemId,
//            @RequestParam("action") String action
//    ) {
//        var cart = orderService.getCart();
//        updateCartItems(itemId, action, cart);
//        orderService.saveCart(cart);
//        return "redirect:/cart/items";
//    }
//
//    @Transactional
//    @PostMapping("/buy")
//    public String buy() {
//        var cart = orderService.getCart();
//        orderService.createOrder(cart);
//        return "redirect:/order/" + cart.getId() + "/new";
//    }
//
//    @GetMapping("/items/add")
//    public String showAddItemForm(Model model) {
//        return "add-item";
//    }
//
//    @Transactional
//    @PostMapping("/saveItem")
//    public String saveNewItem(
//            @RequestParam("name") String name,
//            @RequestParam(value = "image") MultipartFile image,
//            @RequestParam("description") String description,
//            @RequestParam("price") double price
//    ) throws IOException {
//        productService.addNewProduct(name, image, description, price);
//        return "redirect:/";
//    }
//
//    private void updateCartItems(Long itemId, String action, Order cart) {
//        var item = cart.getItems().stream().filter(it -> it.getProduct().getId().equals(itemId)).findFirst()
//                .orElse(new Item());
//        if (item.getProduct() == null) {
//            var product = productService.findById(itemId);
//            item.setProduct(product);
//            item.setCount(0);
//            item = itemService.saveItem(item);
//            cart.getItems().add(item);
//        }
//        item.setCount(action.equals("plus") ? item.getCount() + 1 : item.getCount() - 1);
//        if (action.equals("delete") || item.getCount() == 0) {
//            itemService.deleteItem(item);
//            cart.getItems().remove(item);
//        }
//    }
//
    private Mono<HashMap<Long, Item>> getCartItemMap() {
        return orderService.getCart()
                .flatMap(cart -> {
                    Long cartId = cart.getId();
                    return itemsOrdersService.findAllItemOrdersByOrderId(cartId)
                            .flatMap(itemOrder -> itemService.findById(itemOrder.getItemId()))
                            .collectList()
                            .map(items -> {
                                HashMap<Long, Item> productsMap = new HashMap<>();
                                for (Item item : items) {
                                    productsMap.put(item.getProductId(), item);
                                }
                                return productsMap;
                            });
                });
    }

}
