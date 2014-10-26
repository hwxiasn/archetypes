drop table if exists user;
create table user(
id int(11) auto_increment,
user_name varchar(30),
password varchar(50),
salt varchar(50),
status char(1),
role_ids varchar(255),
permission_ids varchar(255),
primary key(id)
);

drop table if exists role;
create table role(
id int(11) auto_increment,
name varchar(30),
name_en varchar(30),
name_zh varchar(30),
is_front_role bit default 0,
is_admin_role bit default 0,
permission_ids varchar(255),
primary key(id)
);

drop table if exists permission;
create table permission(
id int(11) auto_increment,
name varchar(30),
name_zh varchar(30),
parent_id int(11),
primary key(id)
);

drop table if exists user_profile;
create table user_profile(
id int(11) auto_increment,
user_id int(11) not null,
real_name varchar(30),
mobile varchar(11),
email varchar(30),
id_number varchar(20),
age tinyint,
sex char(1),
primary key(id)
);

drop table if exists user_enterprise_profile;
create table user_enterprise_profile(
id int(11) auto_increment,
user_id int(11) not null,
enterprise_name varchar(50),
license_number varchar(50),
primary key(id)
);