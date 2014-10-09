create table user(
user_id int(11) auto_increment,
user_name varchar(30),
password varchar(50),
salt varchar(50),
status char(1),
role_ids varchar(255),
permission_ids varchar(255),
version int(11) default 1,
primary key(user_id)
);

create table role(
id int(11) auto_increment,
name varchar(30),
name_en varchar(30),
name_zh varchar(30),
permission_ids varchar(255),
version int(11) default 1,
primary key(id)
);

create table permission(
id int(11) auto_increment,
name varchar(30),
name_zh varchar(30),
parent_id int(11),
version int(11) default 1,
primary key(id)
);