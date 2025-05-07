package ru.yandex_practicum.shoponline.model.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Setter
@Table("items_orders")
public class ItemsOrders {

    @Column("item_id")
    private Long itemId;

    @Column("order_id")
    private Long orderId;

}
