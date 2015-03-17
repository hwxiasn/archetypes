drop table if exists bank_type;
create table bank_type(
id bigint auto_increment,
name varchar(50),
code varchar(20),
qdd_code varchar(20),
primary key(id)
);

drop table if exists code_list;
create table code_list(
id bigint auto_increment,
type varchar(50),
name varchar(50),
code varchar(20),
primary key(id)
);

drop table if exists state_city;
create table state_city(
id bigint auto_increment,
name varchar(50),
parent_name varchar(50),
code varchar(10),
parent_code varchar(10),
qdd_code varchar(10),
qdd_parent varchar(10),
primary key(id)
);

drop table if exists upload_image;
create table upload_image(
id bigint auto_increment,
object_type varchar(20),
object_id bigint,
image_type varchar(20),
image_name varchar(50),
image_path varchar(255),
primary key(id)
);
