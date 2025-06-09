package ru.yandex_practicum.shoponline.model.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(exclude = "id")
@Table(name = "products")
public class Product implements Serializable {

    @Id
    private Long id;

    private String name;

    private String description;

    private byte[] image;

    private Double price;

    public Product(String name, String description, byte[] image, Double price) {
        this.name = name;
        this.description = description;
        this.image = image;
        this.price = price;
    }

    public Product(Long id, String name, String description, byte[] image, Double price) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.image = image;
        this.price = price;
    }

}
