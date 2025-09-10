create table users
(
    id      int auto_increment
        primary key,
    userId  varchar(128) not null,
    name    varchar(256) not null,
    balance bigint       not null
);