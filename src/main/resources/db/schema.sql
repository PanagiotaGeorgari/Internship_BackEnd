--create the table cards
create table cards(
    card_id int auto_increment primary key,
    name varchar(50) not null,
    description varchar(255),
    color varchar(50),
    status varchar(50) not null default "TODO",
    user_id int
);
