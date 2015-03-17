insert into account(id, password, salt) values(2, 'f6144a83838d06e69c536f9ad0a48adc', 'c704c066839cf5e5323638e8aef58155');
insert into account(id, password, salt) values(3, 'a3b059fc2939ce2d67301961bdae9a74', 'd7c928364a97390f9925fa4dfe757960');
insert into account(id, password, salt) values(4, '60e51400167b48d3cd016ca1734c9864', 'c704c066839cf5e5323638e8aef58155');
insert into account(id, password, salt) values(5, 'a06d6e064cfd1894136d3820ce813f5a', 'c704c066839cf5e5323638e8aef58155');

insert into sub_account(id, account_id, type) values(2, 2, 'QDD_GH');
insert into sub_account(id, account_id, type) values(3, 3, 'QDD_GH');
insert into sub_account(id, account_id, type) values(4, 4, 'QDD_GH');
insert into sub_account(id, account_id, type) values(5, 5, 'QDD_GH');

insert into sub_qdd_account(id, platform_id, money_more_more_id, authorised) values(2, 'p289', 'm6470', true);
insert into sub_qdd_account(id, platform_id, money_more_more_id, authorised) values(3, 'p289', 'm7824', true);
insert into sub_qdd_account(id, platform_id, money_more_more_id, authorised) values(4, 'p289', 'p289', true);
insert into sub_qdd_account(id, platform_id, money_more_more_id, authorised) values(5, 'p289', 'm6467', true);
