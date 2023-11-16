create table goods
(
    id_good     bigint auto_increment
        primary key,
    name        varchar(45)  null,
    description varchar(256) null,
    price       decimal      null,
    quantity    int          null
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

-- Insert data into orders table
INSERT INTO orders (created, status)
VALUES ('2023-11-16 08:00:00', 'PROCESSING'),
       ('2023-11-16 09:30:00', 'PROCESSING'),
       ('2023-11-16 10:45:00', 'PAID'),
       ('2023-11-16 11:15:00', 'PROCESSING'),
       ('2023-11-16 12:30:00', 'PROCESSING'),
       ('2023-11-16 14:00:00', 'PAID'),
       ('2023-11-16 15:20:00', 'PROCESSING'),
       ('2023-11-16 16:45:00', 'PAID'),
       ('2023-11-16 17:30:00', 'PROCESSING'),
       ('2023-11-16 18:15:00', 'PAID');

-- Insert data into order_details table
INSERT INTO order_details (quantity_buy, good_id, order_id)
VALUES (2, 1, 1),
       (3, 4, 1),
       (1, 7, 2),
       (2, 2, 3),
       (5, 5, 3),
       (1, 9, 4),
       (4, 3, 5),
       (2, 8, 5),
       (3, 10, 6),
       (1, 6, 7);