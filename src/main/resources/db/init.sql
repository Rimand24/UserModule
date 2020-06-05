drop table users_role if exists;
drop table users if exists;
drop sequence if exists hibernate_sequence;
create sequence hibernate_sequence start with 1 increment by 1;

create table users (
    id bigint not null,
    username varchar(255),
    password varchar(255),
    email varchar(255),
    enabled boolean not null,
    account_non_locked boolean not null,
    account_non_expired boolean not null,
    credentials_non_expired boolean not null,
    primary key (id)
);

create table users_role (
    users_id bigint not null,
    authorities varchar(255)
);


alter table users_role
    add constraint users_role_users_fk
    foreign key (users_id) references users;



INSERT INTO users (id, username, password, email, enabled, account_non_locked,  account_non_expired,  credentials_non_expired)
VALUES (1, 'admin', '123', 'email@mail.com', true, true, true, true), (2, 'user', '123', 'email2@mail.com', true, true, true, true) ;

insert into users_role (users_id, authorities)
values (1, 'USER'),(1, 'ADMIN'), (2, 'USER')

