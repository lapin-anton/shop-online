package ru.yandex_practicum.shoponline.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
class SecureShopControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void shouldAccessPublicEndpoints() {
        webTestClient.get()
                .uri("/")
                .exchange()
                .expectStatus().isOk();

        webTestClient.get()
                .uri("/item/1")
                .exchange()
                .expectStatus().isOk();

        webTestClient.get()
                .uri("/images/1")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldAccessToSomeEndpointsForAdminUser() {
        webTestClient.get()
                .uri("/items/add")
                .headers(headers -> headers.setBasicAuth("admin", "admin"))
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    @WithAnonymousUser
    void shouldDenyAnonymousToEndpointsFroAuthorized() {
        webTestClient.post()
                .uri("/main/item/1")
                .exchange()
                .expectStatus()
                .is3xxRedirection()
                .expectHeader().location("/login");

        webTestClient.post()
                .uri("/item/1")
                .exchange()
                .expectStatus()
                .is3xxRedirection()
                .expectHeader().location("/login?error");

        webTestClient.get()
                .uri("/orders")
                .exchange()
                .expectStatus()
                .is3xxRedirection()
                .expectHeader().location("/login");

        webTestClient.get()
                .uri("/order/1/new")
                .exchange()
                .expectStatus()
                .is3xxRedirection()
                .expectHeader().location("/login");

        webTestClient.get()
                .uri("/cart/items")
                .exchange()
                .expectStatus()
                .is3xxRedirection()
                .expectHeader().location("/login");

        webTestClient.post()
                .uri("/cart/item/1")
                .exchange()
                .expectStatus()
                .is3xxRedirection()
                .expectHeader().location("/login");

        webTestClient.post()
                .uri("/buy")
                .exchange()
                .expectStatus()
                .is3xxRedirection()
                .expectHeader().location("/login");

        webTestClient.post()
                .uri("/items/add")
                .exchange()
                .expectStatus()
                .is3xxRedirection()
                .expectHeader().location("/login");
    }

    @Test
    @WithMockUser(roles = "USER")
    void shouldPassAuthorizedUsersToEndpoints() {
        webTestClient.post()
                .uri("/main/item/1")
                .headers(headers -> headers.setBasicAuth("user", "password"))
                .exchange()
                .expectStatus()
                .is3xxRedirection()
                .expectHeader().location("/");

        webTestClient.post()
                .uri("/item/1")
                .headers(headers -> headers.setBasicAuth("user", "password"))
                .exchange()
                .expectStatus()
                .is3xxRedirection()
                .expectHeader().location("/item/1");

        webTestClient.get()
                .uri("/orders")
                .headers(headers -> headers.setBasicAuth("user", "password"))
                .exchange()
                .expectStatus().isOk();

        webTestClient.get()
                .uri("/order/1/new")
                .headers(headers -> headers.setBasicAuth("user", "password"))
                .exchange()
                .expectStatus().isOk();

        webTestClient.get()
                .uri("/cart/items")
                .headers(headers -> headers.setBasicAuth("user", "password"))
                .exchange()
                .expectStatus().isOk();

        webTestClient.post()
                .uri("/cart/item/1")
                .headers(headers -> headers.setBasicAuth("user", "password"))
                .exchange()
                .expectStatus()
                .is3xxRedirection()
                .expectHeader().location("/cart/items");

        webTestClient.post()
                .uri("/buy")
                .headers(headers -> headers.setBasicAuth("user", "password"))
                .exchange()
                .expectStatus().is3xxRedirection();
    }

}