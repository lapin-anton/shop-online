create table products (
    id bigserial primary key,
    name varchar(1000),
    description text,
    image bytea,
    price decimal
);

create table users (
    id bigserial primary key,
    name varchar(100) not null unique,
    password text not null unique,
    role varchar(100)
);

create table accounts (
    id bigint primary key references users(id),
    balance decimal default 10000.00
);

create table orders (
    id bigserial primary key,
    user_id bigint references users(id),
    total_sum decimal,
    created_at timestamp
);

create table items (
    id bigserial primary key,
    order_id bigint references orders(id),
    product_id bigint references products(id),
    count integer
);

insert into users(name, password, role) values
                                            ('user', 'password', 'USER'),
                                            ('admin', 'admin', 'ADMIN');