create table users
(
    user_id int auto_increment primary key,
    email varchar(50) ,
    name varchar(50) ,
    role varchar(50) not null ,
    password varchar(255) not null
);