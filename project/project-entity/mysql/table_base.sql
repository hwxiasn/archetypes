drop procedure if exists `table_base`;
create procedure `table_base`()
begin
	declare done int default 0;
	declare a varchar(20);
	declare rs cursor for select `TABLE_NAME` from information_schema.`TABLES` where `TABLE_SCHEMA`='test';
	declare continue handler for sqlstate '02000' set done=1;
	open rs;
	repeat fetch rs into a;
	set @s=concat('alter table ', a, 
		' add column version int(11) default 1,',
		' add column create_at timestamp default current_timestamp'
		-- ,', add column create_by varchar(30)'
		-- ,', add column update_at timestamp null'
		-- ,', add column update_by varchar(30)'
		 ,', add column deleted bit default 0'
		-- ,', add column delete_at timestamp null'
		-- ,', add column delete_by varchar(30)'
		);
	prepare stmt from @s;
	execute stmt;
	until done end repeat;
	close rs;
end