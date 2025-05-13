package ru.yandex_practicum.shoponline.model.dto;

import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.List;

@Getter
@Setter
public class OrderDto {

    private Long id;

    private Double totalSum;

    private Timestamp createdAt;

    private List<ItemDto> items;

    public OrderDto(Long id, Double totalSum, Timestamp createdAt, List<ItemDto> items) {
        this.id = id;
        this.totalSum = totalSum;
        this.createdAt = createdAt;
        this.items = items;
    }
}
