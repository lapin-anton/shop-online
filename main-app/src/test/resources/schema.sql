drop table if exists items;
drop table if exists orders;
drop table if exists products;

create table products (
    id bigserial primary key,
    name varchar(1000),
    description text,
    image bytea,
    price decimal
);

create table orders (
    id bigserial primary key,
    total_sum decimal,
    created_at timestamp
);

create table items (
    id bigserial primary key,
    order_id bigint references orders(id),
    product_id bigint references products(id),
    count integer
);