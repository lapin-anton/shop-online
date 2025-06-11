package ru.yandex_practicum.shoponline.model.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.sql.Timestamp;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "orders")
public class Order {

    @Id
    private Long id;

    @Column("total_sum")
    private Double totalSum;

    @Column("created_at")
    private Timestamp createdAt;

    public Order(Double totalSum) {
        this.totalSum = totalSum;
    }

    public Order(Double totalSum, Timestamp createdAt) {
        this.totalSum = totalSum;
        this.createdAt = createdAt;
    }
}
