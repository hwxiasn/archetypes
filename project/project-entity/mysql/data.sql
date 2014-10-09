insert into permission(parent_id, id, name, name_zh) values(0, 1, '*:*', '管理员权限');
insert into role(id, name, name_en, name_zh, permission_ids) values(1, 'A', 'admin', '管理员', '1');
insert into user(user_id, user_name, password, salt, status, role_ids, permission_ids) values(1, 'admin', '259907396c78433babf37375469b88e2', '42v1580n5stfftu9nptrlot1lbadyzaj', 'A', '1', '1');