create database shop-online;

create table products (
    id bigserial primary key,
    name varchar(1000),
    description text,
    image bytea,
    price decimal
);

create table items (
    id bigserial primary key,
    product_id bigint references products(id),
    count integer
);

create table orders (
    id bigserial primary key,
    total_sum decimal,
    created_at timestamp
);

create table items_orders (
    item_id bigint references items(id),
    order_id bigint references orders(id)
);