package ru.yandex_practicum.shoponline.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
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
import ru.yandex_practicum.shoponline.model.entity.Item;
import ru.yandex_practicum.shoponline.model.entity.Order;
import ru.yandex_practicum.shoponline.model.front.ItemDto;
import ru.yandex_practicum.shoponline.model.front.Paging;
import ru.yandex_practicum.shoponline.service.ItemService;
import ru.yandex_practicum.shoponline.service.OrderService;
import ru.yandex_practicum.shoponline.service.ProductService;

import java.io.IOException;
import java.util.HashMap;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ShopController {

    private final ProductService productService;

    private final OrderService orderService;

    private final ItemService itemService;

    @GetMapping("/")
    public String showMainPage(Model model,
                               @RequestParam(value = "pageSize", defaultValue = "5") int pageSize,
                               @RequestParam(value = "pageNumber", defaultValue = "1") int pageNumber,
                               @RequestParam(value = "search", defaultValue = "") String search,
                               @RequestParam(value = "sort", defaultValue = "NO") String sort
    ) {
        var products = productService.findAllBySearchAndSort(search, sort, pageSize, pageNumber);
        var cartItemMap = getCartItemMap();
        var items = products.stream().map(p ->
                new ItemDto(p.getId(), p.getName(), p.getDescription(), p.getPrice(), cartItemMap.containsKey(p.getId()) ?
                        cartItemMap.get(p.getId()).getCount() : 0));
        var paging = new Paging(products.size(), pageNumber, pageSize);
        model.addAttribute("items", items);
        model.addAttribute("paging", paging);
        model.addAttribute("search", search);
        model.addAttribute("sort", sort);
        return "main";
    }

    @GetMapping("/images/{productId}")
    public ResponseEntity<Resource> downloadImage(@PathVariable("productId") Long productId) throws Exception {
        var product = productService.findById(productId);
        return ResponseEntity.ok()
                .headers(new HttpHeaders())
                .contentLength(product.getImage().length)
                .contentType(MediaType.parseMediaType("application/octet-stream"))
                .body(new ByteArrayResource(product.getImage()));
    }

    @Transactional
    @PostMapping("/main/item/{itemId}")
    public String changeItemCountOnMain(
            @PathVariable("itemId") Long itemId,
            @RequestParam("action") String action
    ) {
        var cart = orderService.getCart();
        updateCartItems(itemId, action, cart);
        orderService.saveCart(cart);
        return "redirect:/";
    }

    @GetMapping("/item/{itemId}")
    public String showItem(Model model, @PathVariable("itemId") Long itemId) {
        var product = productService.findById(itemId);
        var cartItemMap = getCartItemMap();
        var item = new ItemDto(product.getId(), product.getName(), product.getDescription(), product.getPrice(),
                cartItemMap.containsKey(product.getId()) ? cartItemMap.get(product.getId()).getCount() : 0);
        model.addAttribute("item", item);
        return "item";
    }

    @Transactional
    @PostMapping("/item/{itemId}")
    public String changeItemCount(
            @PathVariable("itemId") Long itemId,
            @RequestParam("action") String action
    ) {
        var cart = orderService.getCart();
        updateCartItems(itemId, action, cart);
        orderService.saveCart(cart);
        return "redirect:/item/" + itemId;
    }

    @GetMapping("/orders")
    public String showOrders(Model model) {
        var orders = orderService.findAllOrders();
        model.addAttribute("orders", orders);
        return "orders";
    }

    @GetMapping("/order/{orderId}")
    public String showOrder(Model model, @PathVariable("orderId") Long orderId) {
        var order = orderService.findOrder(orderId);
        model.addAttribute("order", order);
        //model.addAttribute("newOrder", false);
        return "order";
    }

    @GetMapping("/cart/items")
    public String showCart(Model model) {
        var cart = orderService.getCart();
        var items = cart.getItems();
        model.addAttribute("items", items);
        model.addAttribute("total", cart.getTotalSum());
        return "cart";
    }

    @GetMapping("/items/add")
    public String showAddItemForm(Model model) {
        return "add-item";
    }

    @PostMapping("/saveItem")
    public String saveNewItem(
            @RequestParam("name") String name,
            @RequestParam(value = "image") MultipartFile image,
            @RequestParam("description") String description,
            @RequestParam("price") double price
    ) throws IOException {
        productService.addNewProduct(name, image, description, price);
        return "redirect:/";
    }

    private void updateCartItems(Long itemId, String action, Order cart) {
        var item = cart.getItems().stream().filter(it -> it.getProduct().getId().equals(itemId)).findFirst()
                .orElse(new Item());
        if (item.getProduct() == null) {
            var product = productService.findById(itemId);
            item.setProduct(product);
            item.setCount(0);
            item = itemService.saveItem(item);
            cart.getItems().add(item);
        }
        item.setCount(action.equals("plus") ? item.getCount() + 1 : item.getCount() - 1);
        if (item.getCount() == 0) {
            itemService.deleteItem(item);
            cart.getItems().remove(item);
        }
    }

    private HashMap<Long, Item> getCartItemMap() {
        var cart = orderService.getCart();
        var productsMap = new HashMap<Long, Item>();
        for (var item: cart.getItems()) {
            productsMap.put(item.getProduct().getId(), item);
        }
        return productsMap;
    }

}
