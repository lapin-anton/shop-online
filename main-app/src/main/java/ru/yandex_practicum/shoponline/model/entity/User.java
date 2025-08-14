package ru.yandex_practicum.shoponline.model.entity;

import lombok.Getter;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Table(name = "users")
public class User {

    private Long id;

    private String name;

    private String password;

    private String role;

}
