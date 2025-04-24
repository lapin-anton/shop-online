package ru.yandex_practicum.shoponline.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "products")
@NoArgsConstructor
public class Product implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String description;

    private byte[] image;

    private Double price;

    @OneToMany(mappedBy = "product")
    private List<Item> items;

    public Product(String name, String description, byte[] image, Double price) {
        this.name = name;
        this.description = description;
        this.image = image;
        this.price = price;
    }

}
