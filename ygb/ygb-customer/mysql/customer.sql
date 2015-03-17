-- -------------------------------
-- Table structure for `user`(CST)
-- -------------------------------
drop table if exists user;
create table user(
id bigint(20)  not null  comment '主键ID',
user_name varchar(40) not null comment '用户名',
password varchar(50) comment '密码',
status varchar(2) comment '用户状态',
salt varchar(50) comment '密码盐',
role varchar(10) comment '角色',
register_type  varchar(2) comment '注册类型',
activate_code  varchar(40) comment '激活码',
reg_source	   varchar(32) comment '注册类型',
reg_date datetime 	comment '注册时间',
last_login_time datetime comment '最后一次登录时间',
primary key(id)
);

-- ----------------------------------------
-- Table structure for `user_status`(CST)  
-- ----------------------------------------
drop table if exists user_status;
create table user_status(
id bigint(20)  not null  comment '主键ID',
user_id bigint(20) comment 'user表主键',
activated varchar(2) comment '是否激活',
emailbinding varchar(2) comment '邮箱是否绑定',
mobilebinding varchar(2) comment '手机是否绑定',
real_name_auth_status varchar(2) comment '是否实名认证',
bankbinding varchar(2) comment '银行卡绑定',
primary key(id)
);


-- ----------------------------------------
-- Table structure for `user_profile`(CST)  
-- ----------------------------------------
drop table if exists user_profile;
create table user_profile(
id bigint(20)  not null comment '主键ID',
user_id bigint(20) not null comment 'user表主键',
mobile varchar(11)  comment '手机',
real_name varchar(50) not null comment '真实姓名',
email varchar(200) not null comment '邮箱',
id_type varchar(2)  comment '证件类型',
id_num varchar(32)  comment '证件号',
gender varchar(2)   comment '性别',
id_valid_to datetime null comment '证件到期时间',
id_copy_front  varchar(255) comment '证件照扫描件正面',
id_copy_bank   varchar(255) comment '证件照扫描件反面',
audit_password varchar(255) comment '审核密码',
audit_salt varchar(255) comment '密码盐',
customer_num varchar(32)  comment '客户编号',
memo varchar(255) comment '备注',
primary key(id)
);


-- ----------------------------------------------------
-- Table structure for `user_enterprise_profile` (CST) 
-- ----------------------------------------------------
drop table if exists user_enterprise_profile;
create table user_enterprise_profile(
id bigint(20)  not null comment '主键ID',
user_id bigint(20) not null comment 'user表主键',
enterprise_brief_code varchar(32) comment '机构简码',
enterprise_subcode_from varchar(16) comment '机构号段',
enterprise_subcode_to varchar(16) comment '机构号段',
contact_name varchar(50) not null comment '联系人姓名',
contact_email varchar(50) not null comment '联系人邮箱',
contact_phone varchar(11) comment '联系人手机',
enterprise_name varchar(50) not null comment '企业名称',
enterprise_short_name varchar(32) comment '企业简称',
legal_person_name varchar(50) comment '法人代表姓名',
legal_person_id_num varchar(32) comment '法人身份证号',
organization_code varchar(45) comment '组织机构代码',
tax_registration_no varchar(45) comment '税务登记号',
license_num varchar(45) comment '营业执照注册号',
register_province varchar(45) comment '营业执照注册所属省份',
register_city varchar(45) comment '营业执照注册所属市',
register_address varchar(45) comment '公司常用地址',
license_valid_period varchar(45) comment '经营期限',
phone_num varchar(45) comment '公司联系电话',
license_path  varchar(45) comment '营业执照副本扫描件',
license_cachet_path varchar(45) comment '盖章的营业执照副本扫描件',
legal_person_id_copy_font varchar(255) comment ' 法人代表身份证正面照',
legal_person_id_copy_back varchar(255) comment '法人代表身份证反面照',
openning_license_path varchar(255) comment '开户许可证照片',
memo varchar(255) comment '备注',
primary key(id)
);

-- -----------------------------------------------
-- Table structure for `user_bankcard` (CST) 
-- -----------------------------------------------
drop table if exists user_bank_card;
create table user_bank_card(
id bigint(20)  not null comment '主键ID',
user_id bigint(20) not null comment 'user表主键',
bank_card_type varchar(10) comment '银行卡类型',
bank_code varchar(16) not null comment '银行代码',
bank_card_num varchar(32) not null comment '银行卡号',
bank_card_account_name varchar(32) comment '账户名',
bank_branch varchar(45) comment '开户支行',
id_type varchar(4) comment '证件类型',
id_num varchar(32) comment '证件号',
province varchar(16) comment '省',
city varchar(16) comment '市',
address varchar(32) comment '地区',
memo varchar(255) comment '备注',
primary key(id) 
);

-- -----------------------------------------------
-- Table structure for `user_group`  (CST)
-- -----------------------------------------------
drop table if exists user_group;
create table user_group(
id bigint(20)  not null comment '主键ID',
parent_user_id bigint(20) not null comment '父user_id',
child_user_id bigint(20) not null comment '子user_id',
relationship varchar(4) comment '关系',
level varchar(8) comment '级别',
memo varchar(255) comment '备注',
root_id bigint comment '营销机构user_id',
primary key(id)
);

-- ------------------------------------
-- Table structure for `address`(CST) 
-- ------------------------------------
drop table if exists address;
create table address(
id bigint(20)  not null comment '主键ID',
user_id bigint(20) not null comment 'user表主键',
address_type varchar(4) comment '地址类型',
province varchar(16) comment '省',
city varchar(16) comment '市',
district varchar(16) comment '地区',
postcode varchar(16) comment '邮编',
address1 varchar(32) comment '地址1',
address2 varchar(32) comment '地址2',
memo varchar(255) comment '备注',
primary key(id) 
);

-- ------------------------------------
-- Table structure for `role`  
-- ------------------------------------
drop table if exists role;
create table role(
id bigint(20) ,
name varchar(30),
name_en varchar(30),
name_zh varchar(30),
is_front_role bit default 0,
is_admin_role bit default 0,
permission_ids varchar(255),
primary key(id)
);

-- ------------------------------------
-- Table structure for `permission`  
-- ------------------------------------
drop table if exists permission;
create table permission(
id bigint(20) ,
name varchar(30),
name_zh varchar(30),
parent_id bigint(20),
primary key(id)
);
-- ------------------------------------
-- Table structure for `special_user`  
-- ------------------------------------
drop table if exists operator;
create table operator(
id bigint(20) ,
user_id bigint(20) not null comment 'user表主键',
user_name varchar(40) not null comment '用户名',
status varchar(2) comment '用户状态',
audit_password varchar(255) comment '审核密码',
audit_salt varchar(255) comment '审核密码盐',
level varchar(8) comment '级别',
organization_id  bigint(20) not null comment '所属机构id',
display_org_name  varchar(50) not null comment '所属企业名称',
memo varchar(255) comment '备注',
primary key(id)
);

--超级经纪人只存在与吴掌柜中

ALTER TABLE `user_group`
ADD COLUMN `super_broker_id` bigint(20) comment '超级经纪人user_id';

ALTER TABLE `user_group`
ADD COLUMN `broker_id` bigint(20) comment '经纪人user_id';

