package ru.yandex_practicum.shoponline.model.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Setter
@Table(name = "items")
@NoArgsConstructor
public class Item {

    @Id
    private Long id;

    @Column("product_id")
    private Long productId;

    private Integer count;

    public Item(Integer count) {
        this.count = count;
    }

    public Item(Long id, Integer count) {
        this.id = id;
        this.count = count;
    }
}
