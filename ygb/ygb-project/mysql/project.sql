-- ------------------------------------------
-- Table structure for project 
-- ------------------------------------------
drop table if exists project ;
create table project (
id bigint(20)    default '0' comment '项目流水号',
name varchar(128)    default '' comment ' 项目名称  ',
total_amount decimal(20,2)  default '0.00' comment ' 融资金额（精度到分，单位：元）  ',
interest_rate decimal(6,4)  default '0.00' comment ' 投资收益率（年化）  ',
period_type varchar(16)    default '' comment ' 借款期限类型  ',
period varchar(16)    default '' comment ' 借款期限（与类型合并可计算出具体周期）  ',
period_days int(10)  default 0 comment '借款天数-通过计算获取',
minimal_investment decimal(20,2)  default '0.00' comment ' 最低投资额（精度到分，单位：元）  ',
funding_template_id varchar(32)    default '' comment ' 分佣模板ID  ',
repay_template_id varchar(32)    default '' comment ' 分润模板ID  ',
loanee_id bigint(20)   default '0' comment ' 借款人ID  ',
loanee_user_name varchar(64)  default '' comment ' 借款人登录名',
purpose varchar(1024)    default '' comment ' 借款目的  ',
sponsor_user_id bigint(20)   default '0' comment ' 保荐机构用户ID  ',
sponsor_memo varchar(1024)    default '' comment ' 保荐机构推荐语  ',
type varchar(16)    default '' comment ' 项目类型（担保贷还是接力贷等）  ',
status varchar(8)    default '' comment ' 项目状态  ',
progress_amount decimal(20,2)  default '0.00' comment ' 已投资金额（精度到分，单位：元）  ',
progress int(11)    default '0' comment ' 投资百分比（百分数）  ',
total_investor int(11)    default '0' comment ' 投资总人数  ',
guarantee_letter_id bigint(20)   default '0' comment ' 担保函ID  ',
guarantee_user_id varchar(32)  default '' comment '担保公司ID',
contract_no varchar(128)    default '' comment ' 项目合同编码  ',
contract_status char(4) default '' comment '合同制作状态',
publish_date datetime comment ' 项目发布时间  ',
invest_start_date datetime comment ' 项目开始投资时间  ',
invest_end_date datetime comment ' 项目结束投资时间  ',
settle_terms varchar(64)    default '' comment ' 满标条件  ',
settle_terms_amount decimal(20,2)  default '0.00' comment ' 满标金额（精度到分，单位：元）  ',
settle_date datetime comment ' 项目成立时间  ',
settle_amout decimal(20,2)  default '0.00' comment ' 项目实际融资金额（精度到分，单位：元）  ',
effective_date datetime comment ' 项目生效时间  ',
due_date datetime comment ' 项目预计还款时间  ',
due_amount decimal(20,2)  default '0.00' comment ' 项目还款金额（精度到分，单位：元）  ',
fundraise_trade_id bigint(20)   default '0' comment ' 募集交易ID  ',
repay_trade_id bigint(20)   default '0' comment ' 还款交易ID  ',
fundraise_fee_trade_id bigint(20)   default '0' comment ' 募集分佣父交易  ',
primary key(id)
);



-- ------------------------------------------
-- Table structure for guarantee by project guarantee
-- ------------------------------------------
drop table if exists guarantee ;
create table guarantee (
id bigint(20)   default '0' comment '担保函流水号',
project_id  bigint(20)   default '0' comment ' 担保函对应的项目ID  ',
fee decimal(20,2)  default '0.00' comment ' 应收担保费（精度到分，单位：元）  ',
status  varchar(8)    default '' comment ' 担保函状态  ',
year int(11)    default '0' comment ' 担保函年份  ',
serial int(11)    default '0' comment ' 担保函顺序号  ',
user_id  bigint(20)   default '0' comment ' 担保公司用户ID  ',
commitment_letter_sn  varchar(64)    default '' comment ' 担保承诺函编号  ',
commitment_letter_path  varchar(256)    default '' comment ' 担保承诺函保存路径  ',
guarantee_contract_sn  varchar(64)    default '' comment ' 履约回购合同编号（与项目中的编号一致）  ',
guarantee_contract_path  varchar(256)    default '' comment ' 履约回购合同保存路径  ',
guarantee_letter_sn  varchar(64)    default '' comment ' 担保函编号  ',
guarantee_letter_path  varchar(256)    default '' comment ' 担保函保存路径  ',
primary key(id)
);



-- ------------------------------------------
-- Table structure for investment by project investment
-- ------------------------------------------
drop table if exists investment ;
create table investment (
id bigint(20)   default '0' comment '统一的ID编码规则',
inverstor_id bigint(20)   default '0' comment ' 投资人ID',
project_id bigint(20)   default '0' comment ' 项目ID  ',
loanee_id bigint(20)   default '0' comment ' 借款人ID',
trade_id bigint(20)   default '0' comment ' 交易ID',
balance decimal(20,2)  default '0.00' comment ' 投资金额（精度到分，单位：元）',
balance_due decimal(20,2)  default '0.00' comment ' 预期收益金额（精度到分，单位：元）',
invest_no varchar(32)    default '' comment ' 投资流水号  ',
invest_acc_no varchar(32)    default '' comment ' 借款流水号 ',
invest_path varchar(256)   default '' comment '投资凭证地址',
status varchar(8)    default '' comment ' 投资状态',
issue_date datetime comment ' 预投资时间 ',
settled_date datetime comment ' 投资生效时间 ',
due_date datetime comment ' 预计还款时间 ',
primary key(id)
);


-- ------------------------------------------
-- Table structure for repayment by project repayment
-- ------------------------------------------
drop table if exists repayment ;
create table repayment (
id bigint(20)   default '0' comment '统一的ID编码规则',
loanee_id bigint(20)   default '0' comment ' 借款人ID  ',
project_id bigint(20)   default '0' comment ' 项目ID  ',
trade_id bigint(20)   default '0' comment ' 交易ID',
balance decimal(20,2)  default '0.00' comment '  还款金额（精度到分，单位：元）  ',
status varchar(8)    default '' comment ' 还款状态  ',
issue_date datetime comment ' 预还款时间  ',
repay_date datetime comment ' 还款成功时间  ',
primary key(id)
);


-- ------------------------------------------
-- Table structure for commission_template by project commission_template
-- ------------------------------------------
drop table if exists commission_template ;
create table commission_template (
id bigint(20)   default '0' comment '统一的ID编码规则',
name varchar(32)    default '' comment ' 模板名称  ',
status varchar(8)    default '' comment ' 模板状态  ',
allot_phase varchar(16)    default '' comment ' 分配阶段  ',
allot_type varchar(16)    default '' comment ' 分配方式  ',
memo varchar(256)    default '' comment ' 模板描述  ',
locked char(1)    default '0' comment ' 是否锁定  ',
primary key(id)
);


-- ------------------------------------------
-- Table structure for commission_detail by project commission_detail
-- ------------------------------------------
drop table if exists commission_detail ;
create table commission_detail (
id bigint(20)   default '0' comment '统一的ID编码规则',
template_id bigint(20)   default '0' comment '模板ID',
role varchar(16)  default '' comment '角色代码',
rate decimal(6,4)  default '0' comment '费率' ,
primary key(id)
);


-- ------------------------------------------
-- Table structure for project_review by project project_review
-- ------------------------------------------
drop table if exists project_review ;
create table project_review (
id bigint(20)   default '0' comment '统一的ID编码规则',
project_id bigint(20)   default '0' comment '项目ID',
reviewer_id bigint(20)   default '0' comment '审核者ID',
review_code varchar(16)  default '' comment '审核代码',
result varchar(16)  default '' comment '结果',
reason varchar(256)  default '' comment '原因',
reviewer_user_name varchar(32) default '' comment '审核者用户名',
reviewer_name varchar(32) default '' comment '审核者真实姓名',
review_name varchar(32) default '' comment '审核操作名称',
primary key(id)
);

-- ----------------------------
-- Table structure for `borrower_info`
-- ----------------------------
DROP TABLE IF EXISTS `borrower_info`;
CREATE TABLE `borrower_info` (
  `id` bigint(20)  DEFAULT '0' COMMENT '主键',
  `name` varchar(20)  COMMENT '借款人姓名',
  `age` int(3)  COMMENT '借款人年龄',
  `id_serial` varchar(20)  COMMENT '借款人身份证号码',
  `sex` varchar(1)  COMMENT '借款人性别',
  `marital_status` int(11)  COMMENT '借款人婚姻状况',
  `phone` varchar(20)  COMMENT '借款人电话号码',
  `contact_qq` varchar(20) DEFAULT NULL COMMENT '借款人QQ号码',
  `contact1` varchar(30) DEFAULT NULL COMMENT '借款人备用联系方式1',
  `contact2` varchar(30) DEFAULT NULL COMMENT '借款人备用联系方式2',
  `address` varchar(100) DEFAULT NULL COMMENT '借款人现住址',
  `loan_amount` int(11)  COMMENT '借款金额',
  `loan_term` int(11)  COMMENT '借款期限',
  `purpose` varchar(20)  COMMENT '借款用途',
  `return_source` varchar(20)  COMMENT '还款来源',
  `business_scope` varchar(255) DEFAULT NULL COMMENT '公司经营范围',
  `register_capital` int(11) DEFAULT NULL COMMENT '公司注册资金',
  `found_year` int(11) DEFAULT NULL COMMENT '公司成立年份',
  `found_month` int(11) DEFAULT NULL COMMENT '公司成立月份',
  `message` varchar(200) DEFAULT NULL COMMENT '留言附注',
  `create_time` datetime  COMMENT '信息录入时间',
  `status` int(11)  COMMENT '信息状态',
  PRIMARY KEY (`id`)
);

-- ----------------------------
-- Table structure for `loanee_info`
-- ----------------------------
DROP TABLE IF EXISTS `loanee_info`;
CREATE TABLE `loanee_info` (
  `id` bigint(20)  DEFAULT '0' COMMENT '主键',
project_id	bigint(20)	COMMENT	'项目ID',
loanee_name	varchar(40)	COMMENT	'借款人姓名',
loanee_age	int(3)	COMMENT	'借款人年龄',
loanee_id_serial	varchar(40)	COMMENT	'借款人身份证号码',
loanee_sex	varchar(3)	COMMENT	'借款人性别',
loanee_marital_status	varchar(3)	COMMENT	'借款人婚姻状况',
loanee_address	varchar(200)	COMMENT	'借款人居住地',
loanee_had_count	int(11)	COMMENT	'累计成功借款：*笔',
loanee_had_amount	int(11)	COMMENT	'累计借款金额：***万',
loanee_need_repay	int(11)	COMMENT	'当前待还金额：***万',
loanee_missed_repay	int(11)	COMMENT	'累计逾期还款：0元',
loanee_purpose	varchar(200)	COMMENT	'资金用途',
loanee_source	varchar(200)	COMMENT	'还款来源',
loanee_ent_address	varchar(200)	COMMENT	'经营地址',
loanee_ent_scope	varchar(200)	COMMENT	'经营范围',
loanee_ent_money	int(11)	COMMENT	'注册资金',
loanee_ent_born	varchar(200)	COMMENT	'成立时间',
loanee_ent_last_year	int(11)	COMMENT	'上年营业收入',
loanee_ent_this_year	int(11)	COMMENT	'本年**月止营业收入',
loanee_pb_voucher	varchar(200)	COMMENT	'人民银行征信查询',
loanee_loanee_voucher	varchar(200)	COMMENT	'全国法院被执行人信息查询',
loanee_ent_voucher	varchar(200)	COMMENT	'全国企业信用信息公示系统查询',
--  `create_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
--  `deleted` bit(1) DEFAULT b'0',
--  `version` int(11) DEFAULT '1',
  PRIMARY KEY (`id`)
);


