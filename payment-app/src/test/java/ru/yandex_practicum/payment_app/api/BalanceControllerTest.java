package ru.yandex_practicum.payment_app.api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.test.web.reactive.server.WebTestClient;
import ru.yandex_practicum.payment_app.domain.BalanceInfo;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@WebFluxTest(BalanceController.class)
class BalanceControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @BeforeEach
    void setUp() {
    }

    @Test
    void getBalance_shouldReturnCurrentBalance() {
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/getBalance")
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json")
                .expectBody(BalanceInfo.class).consumeWith(response -> {
                    var balanceInfo = response.getResponseBody();
                    assertNotNull(balanceInfo);
                    assertNotNull(balanceInfo.getCurrentValue());
                    assertEquals(0, balanceInfo.getCurrentValue().compareTo(new BigDecimal("10000.00")));
                });
    }

    @Test
    void withdraw_shouldUpdateAndReturnCurrentBalance() {
        webTestClient.put()
                .uri(uriBuilder -> uriBuilder
                        .path("/withdraw")
                        .queryParam("amount", new BigDecimal("500.00"))
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json")
                .expectBody(BalanceInfo.class).consumeWith(response -> {
                    var balanceInfo = response.getResponseBody();
                    assertNotNull(balanceInfo);
                    assertNotNull(balanceInfo.getCurrentValue());
                    assertEquals(0, balanceInfo.getCurrentValue().compareTo(new BigDecimal("9500.00")));
                });
    }

}