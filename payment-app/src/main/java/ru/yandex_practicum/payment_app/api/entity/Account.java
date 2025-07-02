package ru.yandex_practicum.payment_app.api.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;

@Getter
@Setter
@Table(name = "accounts")
@NoArgsConstructor
public class Account {

    @Id
    private Long id;

    private BigDecimal balance;

}
