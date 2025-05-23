package ru.yandex_practicum.shoponline.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.yandex_practicum.shoponline.model.dto.ActionDto;
import ru.yandex_practicum.shoponline.model.dto.ItemDto;
import ru.yandex_practicum.shoponline.model.dto.OrderDto;
import ru.yandex_practicum.shoponline.model.entity.Item;
import ru.yandex_practicum.shoponline.model.entity.Order;
import ru.yandex_practicum.shoponline.model.entity.Product;
import ru.yandex_practicum.shoponline.model.other.Paging;
import ru.yandex_practicum.shoponline.service.CartService;
import ru.yandex_practicum.shoponline.service.ItemService;
import ru.yandex_practicum.shoponline.service.OrderService;
import ru.yandex_practicum.shoponline.service.ProductService;
import ru.yandex_practicum.shoponline.util.CsvParserUtil;

import java.util.HashMap;
import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ShopController {

    private final ProductService productService;

    private final OrderService orderService;

    private final ItemService itemService;

    private final CartService cartService;

    private final CsvParserUtil csvParserUtil;

    @GetMapping("/")
    public Mono<String> showMainPage(Model model,
                                        @RequestParam(value = "pageSize", defaultValue = "5") int pageSize,
                                        @RequestParam(value = "pageNumber", defaultValue = "1") int pageNumber,
                                        @RequestParam(value = "search", defaultValue = "") String search,
                                        @RequestParam(value = "sort", defaultValue = "NO") String sort
    ) {
        Flux<Product> products = productService.findAllBySearchAndSort(search, sort, pageSize, pageNumber);
        Mono<Long> productCountMono = products.count();
        Mono<HashMap<Long, Item>> cartItemMap = cartService.getCartItemMap();
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

    @PostMapping("/main/item/{itemId}")
    public Mono<String> changeItemCountOnMain(
            @PathVariable("itemId") Long itemId,
            @ModelAttribute ActionDto action
    ) {
        return orderService.getCart().flatMap(cart -> cartService.updateCartItem(itemId, action.getAction()))
                .flatMap(cart -> Mono.just("redirect:/"));
    }

    @GetMapping("/item/{itemId}")
    public String showItem(Model model, @PathVariable("itemId") Long itemId) {
        Mono<Product> productMono = productService.findById(itemId);
        Mono<HashMap<Long, Item>> cartItemMap = cartService.getCartItemMap();
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

    @Transactional
    @PostMapping("/item/{itemId}")
    public Mono<String> changeItemCount(
            @PathVariable("itemId") Long itemId,
            @ModelAttribute ActionDto action
    ) {
        return orderService.getCart().flatMap(cart -> cartService.updateCartItem(itemId, action.getAction()))
                .flatMap(cart -> Mono.just("redirect:/item/" + itemId));
    }

    @GetMapping("/orders")
    public Mono<String> showOrders(Model model) {
        Flux<Order> ordersFlux = orderService.findAllOrders();

        Flux<OrderDto> orderDtoFlux = ordersFlux.flatMap(order -> {
            Flux<Item> itemFlux = itemService.findByOrderId(order.getId());
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
            Flux<Item> itemFlux = itemService.findByOrderId(order.getId());
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

    @GetMapping("/cart/items")
    public Mono<String> showCart(Model model) {
        Mono<Order> cartMono = orderService.getCart();
        Mono<OrderDto> orderDtoMono = cartMono.flatMap(cart -> {
            Mono<List<ItemDto>> itemDtoListMono = null;
            if (cart.getId() != null) {
                Flux<Item> itemFlux = itemService.findByOrderId(cart.getId());
                itemDtoListMono = itemFlux.flatMap(item -> {
                    Mono<Product> productMono = productService.findById(item.getProductId());
                    return productMono.map(p -> new ItemDto(item.getId(), p.getId(), p.getName(), p.getDescription(), p.getPrice(), item.getCount()));
                }).collectList();
            }
            if (itemDtoListMono != null) {
                return itemDtoListMono.map(itemDtoList -> new OrderDto(cart.getId(), cart.getTotalSum(), cart.getCreatedAt(), itemDtoList));
            } else {
                return Mono.just(new OrderDto(null, cart.getTotalSum(), cart.getCreatedAt(), List.of()));
            }
        });
        model.addAttribute("items", orderDtoMono.map(OrderDto::getItems));
        model.addAttribute("total", orderDtoMono.map(OrderDto::getTotalSum));
        return Mono.just("cart");
    }

    @Transactional
    @PostMapping("/cart/item/{itemId}")
    public Mono<String> changeItemCountOnCart(
            @PathVariable("itemId") Long itemId,
            @ModelAttribute ActionDto action
    ) {
        return orderService.getCart().flatMap(cart -> cartService.updateCartItem(itemId, action.getAction()))
                .flatMap(cart -> Mono.just("redirect:/cart/items"));
    }

    @Transactional
    @PostMapping("/buy")
    public Mono<String> buy() {
        return orderService.getCart()
                .flatMap(orderService::createNewOrder)
                .flatMap(newOrder -> Mono.just("redirect:/order/" + newOrder.getId() + "/new"));
    }

    @GetMapping("/items/add")
    public Mono<String> showAddItemForm(Model model) {
        return Mono.just("add-item");
    }

    @Transactional
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<String> uploadCsv(@RequestPart("file") Mono<FilePart> fileMono) {
        return fileMono.flux()
                .flatMap(csvParserUtil::parseCsv)
                .flatMap(productService::saveNewProduct)
                .collectList()
                .flatMap(products -> Mono.just("redirect:/"));
    }

}
