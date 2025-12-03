--create the table cards--
create table cards(
    card_id int auto_increment primary key,
    name varchar(50) not null,
    description varchar(255),
    color varchar(50),
    status varchar(50) not null default "Todo",
    user_id int
);

--insert an item to the table --
insert into cards(name,description,color,user_id)
values("first task","connect sql base with spring","red",1);