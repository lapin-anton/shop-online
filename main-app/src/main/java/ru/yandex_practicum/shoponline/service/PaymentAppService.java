package ru.yandex_practicum.shoponline.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import reactor.core.publisher.Mono;
import ru.yandex_practicum.shoponline.domain.BalanceInfo;
import ru.yandex_practicum.shoponline.model.dto.BalanceInfoDto;

@Service
@RequiredArgsConstructor
public class PaymentAppService {

    private final RestTemplate restTemplate;

    @Value("${payment-app.url}")
    private String paymentAppUrl;

    public Mono<BalanceInfoDto> checkBalance() {
        try {
            ResponseEntity<BalanceInfo> response = restTemplate.getForEntity(paymentAppUrl + "/getBalance", BalanceInfo.class);
            return Mono.just(BalanceInfoDto.builder()
                    .currentValue(response.getBody().getCurrentValue().doubleValue())
                    .build());
        } catch (Exception e) {
            return Mono.just(BalanceInfoDto.builder()
                    .message("Сервис платежей недоступен")
                    .build());
        }
    }

    public Mono<BalanceInfoDto> withdraw(Double amount) {
        try {
            var headers = new HttpHeaders();
            var entity = new HttpEntity<>(headers);
            ResponseEntity<BalanceInfo> response =
                    restTemplate.exchange(paymentAppUrl + "/withdraw?amount=" + amount, HttpMethod.PUT, entity, BalanceInfo.class);
            return Mono.just(BalanceInfoDto.builder()
                    .currentValue(response.getBody().getCurrentValue().doubleValue())
                    .build());
        } catch (Exception e) {
            return Mono.just(BalanceInfoDto.builder()
                    .message("Сервис платежей недоступен")
                    .build());
        }
    }

}
