package ru.yandex_practicum.shoponline.model.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class BalanceInfoDto {

    private double currentValue;

    private String message;

}
