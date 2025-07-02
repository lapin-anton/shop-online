package ru.yandex_practicum.payment_app.api.repository;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import ru.yandex_practicum.payment_app.api.entity.Account;

@Repository
public interface AccountRepository extends R2dbcRepository<Account, Long> {



}
