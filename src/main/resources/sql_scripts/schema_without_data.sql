create table goods
(
    id_good     bigint auto_increment
        primary key,
    name        varchar(45)  not null,
    description varchar(256) null,
    price       decimal      not null,
    quantity    int          not null,
    version     int          not null,
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

