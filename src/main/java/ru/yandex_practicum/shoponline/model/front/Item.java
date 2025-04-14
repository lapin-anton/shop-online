package ru.yandex_practicum.shoponline.model.front;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Item {

    private Long id;

    private String name;

    private String description;

    private Double price;

    private int count;

    public Item(Long id, String name, String description, Double price) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
    }
}
