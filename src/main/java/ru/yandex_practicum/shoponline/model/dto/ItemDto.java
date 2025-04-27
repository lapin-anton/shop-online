package ru.yandex_practicum.shoponline.model.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ItemDto {

    private Long id;

    private String name;

    private String description;

    private Double price;

    private int count;

    public ItemDto(Long id, String name, String description, Double price, int count) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.count = count;
    }
}
