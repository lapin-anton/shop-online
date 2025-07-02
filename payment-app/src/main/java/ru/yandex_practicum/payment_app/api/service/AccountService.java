package ru.yandex_practicum.payment_app.api.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;
import ru.yandex_practicum.payment_app.api.repository.AccountRepository;
import ru.yandex_practicum.payment_app.domain.BalanceInfo;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;

    public Mono<BalanceInfo> getBalance(Long userId) {
        return accountRepository.findById(userId).map(account -> {
                            var balanceInfo = new BalanceInfo();
                            balanceInfo.setUserId(userId.intValue());
                            balanceInfo.setCurrentValue(account.getBalance());
                            return balanceInfo;
                        })
                        .switchIfEmpty(Mono.just(new BalanceInfo()));
    }

    @Transactional
    public Mono<BalanceInfo> withdraw(Long userId, BigDecimal amount) {
        return accountRepository.findById(userId)
                .flatMap(account -> {
                    account.setBalance(account.getBalance().subtract(amount));
                    return accountRepository.save(account);
                }).map(account -> {
                    var balanceInfo = new BalanceInfo();
                    balanceInfo.setUserId(userId.intValue());
                    balanceInfo.setCurrentValue(account.getBalance());
                    return balanceInfo;
                }).switchIfEmpty(Mono.just(new BalanceInfo()));
    }

}
