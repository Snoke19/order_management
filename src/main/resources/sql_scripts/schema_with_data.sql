create table goods
(
    id_good     bigint auto_increment
        primary key,
    name        varchar(45)  not null,
    description varchar(256) null,
    price       decimal      not null,
    quantity    int          not null,
    constraint goods_name_pk
        unique (name)
);

create table orders
(
    id_order bigint auto_increment
        primary key,
    created  datetime    not null,
    status   varchar(20) not null
);

create table order_details
(
    id_order_detail int auto_increment
        primary key,
    quantity_buy    int    not null,
    good_id         bigint not null,
    order_id        bigint not null,
    constraint order_details_good_id__fk
        foreign key (good_id) references goods (id_good),
    constraint order_details_order_id__fk
        foreign key (order_id) references orders (id_order)
);

-- Insert data into goods table (phones, tablets, and other electronic devices)
INSERT INTO goods (name, description, price, quantity)
VALUES ('iPhone 12', 'Flagship smartphone from Apple', 999.99, 50),
       ('Samsung Galaxy S21', 'Powerful Android smartphone', 899.99, 40),
       ('iPad Air', '10.9-inch tablet with A14 Bionic chip', 699.99, 30),
       ('Google Pixel 6', 'Android phone with advanced camera features', 799.99, 35),
       ('Microsoft Surface Pro 7', '12.3-inch 2-in-1 Laptop', 1099.99, 25),
       ('OnePlus 9', 'High-performance Android phone', 749.99, 45),
       ('Samsung Galaxy Tab S7', '11-inch Android tablet', 599.99, 20),
       ('Dell XPS 13', '13.4-inch Laptop with InfinityEdge display', 1299.99, 15),
       ('Sony Xperia 5 II', 'Compact Android phone with impressive camera', 899.99, 30),
       ('Amazon Kindle Paperwhite', '6-inch e-reader with built-in light', 129.99, 60);