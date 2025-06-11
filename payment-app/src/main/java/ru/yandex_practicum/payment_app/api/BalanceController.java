package ru.yandex_practicum.payment_app.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.buffer.DefaultDataBuffer;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import ru.yandex_practicum.payment_app.domain.BalanceInfo;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;

@RestController
@RequiredArgsConstructor
public class BalanceController implements GetBalanceApi, WithdrawApi {

    @Value("${balance}")
    private BigDecimal balance;

    private final ObjectMapper objectMapper;

    @Override
    public Mono<ResponseEntity<BalanceInfo>> getBalance(ServerWebExchange exchange) {
        Mono<Void> result;
        try {
            ServerHttpResponse response = exchange.getResponse();
            response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
            response.setStatusCode(HttpStatus.OK);
            var balanceInfoDto = new BalanceInfo();
            balanceInfoDto.setCurrentValue(balance);
            var balanceInfoStr = objectMapper.writeValueAsString(balanceInfoDto);
            byte[] exampleBytes = balanceInfoStr.getBytes(StandardCharsets.UTF_8);
            DefaultDataBuffer data = new DefaultDataBufferFactory().wrap(exampleBytes);
            result = response.writeWith(Mono.just(data));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return result.then(Mono.empty());
    }

    @Override
    public Mono<ResponseEntity<BalanceInfo>> withdraw(BigDecimal amount, ServerWebExchange exchange) {
        Mono<Void> result;
        try {
            ServerHttpResponse response = exchange.getResponse();
            response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
            response.setStatusCode(HttpStatus.OK);
            var balanceInfoDto = new BalanceInfo();
            var currentBalance = balance.subtract(amount);
            balanceInfoDto.setCurrentValue(currentBalance);
            var balanceInfoStr = objectMapper.writeValueAsString(balanceInfoDto);
            byte[] exampleBytes = balanceInfoStr.getBytes(StandardCharsets.UTF_8);
            DefaultDataBuffer data = new DefaultDataBufferFactory().wrap(exampleBytes);
            result = response.writeWith(Mono.just(data));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return result.then(Mono.empty());
    }
}
