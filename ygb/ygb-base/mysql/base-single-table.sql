alter table role add column create_at timestamp default current_timestamp, add column deleted bit default 0, add column version int(11) default 1;
