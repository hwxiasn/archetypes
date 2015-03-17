drop table if exists account;
create table account(
id bigint auto_increment,
password varchar(50),
salt varchar(50),
primary key(id)
);

drop table if exists sub_account;
create table sub_account(
id bigint auto_increment,
account_id bigint,
type varchar(10),
balance decimal(20,2) not null default '0.00',
freeze_balance decimal(20,2) not null default '0.00',
primary key(id)
);

drop table if exists sub_qdd_account;
create table sub_qdd_account(
id bigint auto_increment,
platform_id varchar(10),
money_more_more_id varchar(10),
authorised bit default 0,
primary key(id)
);

drop table if exists account_log;
create table account_log(
id bigint auto_increment,
type varchar(20),
sub_type varchar(20),
transfer_type varchar(20),
trade_id bigint,
sub_account_log_id bigint,
sub_account_id bigint,
other_sub_account_id bigint,
balance decimal(20,2) not null default '0.00',
trans_amount decimal(20,2) not null default '0.00',
fee decimal(20,2) not null default '0.00',
fee_sub_account_id bigint,
memo varchar(50),
executed bit default 0,
account_balance decimal(20,2) not null default '0.00',
account_freeze_balance decimal(20,2) not null default '0.00',
account_balance2 decimal(20,2) not null default '0.00',
account_freeze_balance2 decimal(20,2) not null default '0.00',
primary key(id)
);

drop table if exists qdd_account_log;
create table qdd_account_log(
id bigint auto_increment,
trade_id bigint,
order_no varchar(20),
platform_id varchar(10),
money_more_more_id varchar(10),
amount decimal(20,2) not null default '0.00',
other_money_more_more_id varchar(10),
bank_code varchar(20),
card_no varchar(30),
fee decimal(20,2) not null default '0.00',
fee_money_more_more_id varchar(10),
memo varchar(50),
audit_pass int default 0,
update_at timestamp null,
loan_no varchar(30),
result_code varchar(10),
message varchar(30),
callback varchar(2038),
orderquery varchar(2038),
primary key(id)
);

drop table if exists account_daily;
create table account_daily(
id bigint auto_increment,
account_id bigint,
daily varchar(10),
balance decimal(20,2) not null default '0.00',
freeze_balance decimal(20,2) not null default '0.00',
daily_balance decimal(20,2) not null default '0.00',
daily_freeze_balance decimal(20,2) not null default '0.00',
status varchar(20),
sub_balances varchar(255),
daily_sub_balances varchar(255),
primary key(id)
);