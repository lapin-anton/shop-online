package ru.yandex_practicum.shoponline.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import ru.yandex_practicum.shoponline.model.dto.ItemDto;
import ru.yandex_practicum.shoponline.model.entity.Item;
import ru.yandex_practicum.shoponline.model.entity.Order;
import ru.yandex_practicum.shoponline.model.entity.Product;

import java.util.HashMap;

@Service
@RequiredArgsConstructor
public class CartService {

    private final OrderService orderService;

    private final ItemService itemService;

    private final ProductService productService;

    public Mono<Order> updateCartItem(Long productId, String action) {
        return orderService.getCart()
                .flatMap(orderService::saveNewCart)
                .flatMap(cart -> itemService.findByOrderId(cart.getId())
                        .filter(it -> it.getProductId().equals(productId))
                        .next()
                        .flatMap(it -> itemService.updateItemQuantity(action, it))
                        .switchIfEmpty(
                                Mono.defer(() -> {
                                    Item item = new Item();
                                    item.setProductId(productId);
                                    item.setOrderId(cart.getId());
                                    item.setCount(1);
                                    return itemService.saveItem(item).then(Mono.just(item));
                                })
                        ).flatMap(it ->
                                itemService.findByOrderId(cart.getId())
                                        .flatMap(item -> {
                                            Mono<Product> productMono = productService.findById(item.getProductId());
                                            return productMono.map(p -> new ItemDto(item.getId(), p.getId(), p.getName(), p.getDescription(), p.getPrice(), item.getCount()));
                                        })
                                        .collectList()
                                        .flatMap(itemDtos -> orderService.saveCart(cart, itemDtos))
                        )
                );
    }

    public Mono<HashMap<Long, Item>> getCartItemMap() {
        return orderService.getCart()
                .flatMap(cart -> {
                    Long cartId = cart.getId();
                    return itemService.findByOrderId(cartId)
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
