package ru.yandex_practicum.payment_app.api.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.buffer.DefaultDataBuffer;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import ru.yandex_practicum.payment_app.api.GetBalanceApi;
import ru.yandex_practicum.payment_app.api.WithdrawApi;
import ru.yandex_practicum.payment_app.api.service.AccountService;
import ru.yandex_practicum.payment_app.domain.BalanceInfo;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;

@RestController
@RequiredArgsConstructor
public class BalanceController implements GetBalanceApi, WithdrawApi {

    private final AccountService accountService;

    private final ObjectMapper objectMapper;

    @Override
    public Mono<ResponseEntity<BalanceInfo>> getBalance(Integer userId, ServerWebExchange exchange) {
        ServerHttpResponse response = exchange.getResponse();
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        response.setStatusCode(HttpStatus.OK);
        return accountService.getBalance((long) userId)
                .flatMap(bi -> {
                    String balanceInfoStr = null;
                    try {
                        balanceInfoStr = objectMapper.writeValueAsString(bi);
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                    byte[] exampleBytes = balanceInfoStr.getBytes(StandardCharsets.UTF_8);
                    DefaultDataBuffer data = new DefaultDataBufferFactory().wrap(exampleBytes);
                    return response.writeWith(Mono.just(data));
                })
                .then(Mono.empty());
    }

    @Override
    public Mono<ResponseEntity<BalanceInfo>> withdraw(Integer userId, BigDecimal amount, ServerWebExchange exchange) {
        ServerHttpResponse response = exchange.getResponse();
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        response.setStatusCode(HttpStatus.OK);
        return accountService.withdraw((long) userId, amount).flatMap(bi -> {
                    String balanceInfoStr = null;
                    try {
                        balanceInfoStr = objectMapper.writeValueAsString(bi);
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                    byte[] exampleBytes = balanceInfoStr.getBytes(StandardCharsets.UTF_8);
                    DefaultDataBuffer data = new DefaultDataBufferFactory().wrap(exampleBytes);
                    return response.writeWith(Mono.just(data));
                })
                .then(Mono.empty());
    }

}
