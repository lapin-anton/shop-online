package ru.yandex_practicum.shoponline.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientManager;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import reactor.core.publisher.Mono;
import ru.yandex_practicum.shoponline.domain.BalanceInfo;
import ru.yandex_practicum.shoponline.model.dto.BalanceInfoDto;

@Service
@RequiredArgsConstructor
public class PaymentAppService {

    private final RestTemplate restTemplate;

    private final ReactiveOAuth2AuthorizedClientManager manager;

    @Value("${payment-app.url}")
    private String paymentAppUrl;

    public Mono<BalanceInfoDto> checkBalance(Long userId) {
            return manager.authorize(OAuth2AuthorizeRequest
                            .withClientRegistrationId("shoponline")
                            .principal("system")
                            .build()
                    )
                    .flatMap(client -> {
                        var accessToken = client.getAccessToken().getTokenValue();
                        var url = paymentAppUrl + "/getBalance?userId={userId}";
                        HttpHeaders headers = new HttpHeaders();
                        headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
                        HttpEntity<Void> request = new HttpEntity<>(headers);
                        try {
                            var response = restTemplate.exchange(url, HttpMethod.GET, request, BalanceInfo.class, userId);
                            return Mono.just(BalanceInfoDto.builder()
                                    .currentValue(response.getBody().getCurrentValue().doubleValue())
                                    .build());
                        } catch (Exception e) {
                            return Mono.just(BalanceInfoDto.builder()
                                    .message("Сервис платежей недоступен")
                                    .build());
                        }
                    });
    }

    public Mono<BalanceInfoDto> withdraw(Long userId, Double amount) {
        return manager.authorize(OAuth2AuthorizeRequest
                        .withClientRegistrationId("shoponline")
                        .principal("system")
                        .build()
                )
                .flatMap(client -> {
                    var accessToken = client.getAccessToken().getTokenValue();
                    var url = paymentAppUrl + "/withdraw?userId={userId}&amount={amount}";
                    HttpHeaders headers = new HttpHeaders();
                    headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
                    HttpEntity<Void> request = new HttpEntity<>(headers);
                    try {
                        var response = restTemplate.exchange(url, HttpMethod.PUT, request, BalanceInfo.class, userId, amount);
                        return Mono.just(BalanceInfoDto.builder()
                                .currentValue(response.getBody().getCurrentValue().doubleValue())
                                .build());
                    } catch (Exception e) {
                        return Mono.just(BalanceInfoDto.builder()
                                .message("Сервис платежей недоступен")
                                .build());
                    }
                });
    }

}
