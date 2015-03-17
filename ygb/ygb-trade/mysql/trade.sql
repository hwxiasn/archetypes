-- ------------------------------------------
-- Table structure for trade by project trade
-- ------------------------------------------
drop table if exists trade ;
create table trade (
id bigint(20)   default '0' comment '交易流水号',
batch_id bigint(20)   default '0' comment '交易批次',
parent_trade_id bigint(20)   default '0' comment '父交易流水号',
source_trade_id bigint(20)   default '0' comment '源交易流水号',
trade_name varchar(64)   default '' comment '交易名称',
trade_subject_id varchar(32)   default '' comment '交易标的流水号',
trade_subject_info varchar(64)   default '' comment '交易标的描述',
trade_source varchar(8)   default '' comment '交易来源',
credit_account varchar(32)   default '' comment '出借方交易账户ID（帐务流水号）',
credit_bank_name varchar(64)   default '' comment '出借方银行名称（银行代码）',
credit_account_type varchar(64)   default '' comment '出借方银行账户类型',
credit_account_no varchar(64)   default '' comment '出借方银行账户号码',
credit_balance decimal(20,2) default '0.00' comment '出借方余额',
credit_freeze_balance decimal(20,2) default '0.00' comment '出借方冻结金额',
debit_account varchar(32)   default '' comment '贷入方交易账户ID（帐务流水号）',
debit_bank_name varchar(64)   default '' comment '贷入方银行名称（银行代码）',
debit_account_type varchar(64)   default '' comment '贷入方银行账户类型',
debit_account_no varchar(64)   default '' comment '贷入方银行账户号码',
debit_balance decimal(20,2) default '0.00' comment '贷入方余额',
debit_freeze_balance decimal(20,2) default '0.00' comment '贷入方冻结金额',
trade_type varchar(8)   default '' comment '交易类型（投资、还款、分佣、充值、提现、转账）',
trade_kind varchar(8)   default '' comment '交易子类型（子投资、子还款、子分佣）',
trade_action varchar(8)   default '' comment '交易操作类型（冻结、操作）',
trade_relation varchar(8)   default '' comment '交易双方关系（一对一、一对多、多对一）',
trade_status varchar(8)  default '' comment '交易状态（执行成功、执行失败、待执行、执行中）',
trade_amount decimal(20,2) default '0.00' comment '实际交易金额，单位：人民币，元，保留2位精度',
aim_trade_amount decimal(20,2) default '0.00' comment '目标交易金额，单位：人民币，元，保留2位精度',
deal_date datetime  comment '执行结束时间  ',
memo varchar(32)  default '' comment '备注',
primary key(id)
);

-- ------------------------------------------
-- Table structure for deal by project deal
-- ------------------------------------------
drop table if exists deal ;
create table deal (
id bigint(20)  default '0' comment '交易执行流水号',
batch_id bigint(20)   default '0' comment '交易批次',
trade_id bigint(20)  default '0' comment '交易流水号',
source_deal_id bigint(20)   default '0' comment '源交易流水号',
credit_account varchar(32)   default '' comment '出借方交易账户ID（帐务流水号）',
debit_account varchar(32)   default '' comment '贷入方交易账户ID（帐务流水号）',
trade_amount decimal(20,2) default '0.00' comment '实际交易金额，单位：人民币，元，保留2位精度',
trade_check varchar(8)   default '' comment '交易检验结果（检验通过、检验失败、无需检验）',
trade_type varchar(8)   default '' comment '交易类型（投资、还款、分佣、充值、提现、转账）',
trade_action varchar(8)   default '' comment '交易操作类型（冻结、操作）',
trade_status varchar(8)  default '' comment '交易状态（执行成功、执行失败、待执行、执行中）',
trade_param varchar(256)   default '' comment '交易输出参数',
trade_dispatch varchar(1024)   default '' comment '交易目的地址（需要手工参与的业务需要）',
deal_date datetime  comment '执行时间  ',
call_back varchar(1024)  default '' comment '执行反馈 ',
message varchar(64)  default '' comment ' 执行结果 ',
memo varchar(32)  default '' comment '备注',
primary key(id)
);


-- ------------------------------------------
-- Table structure for trade_log by project trade
-- ------------------------------------------
drop table if exists trade_log ;
create table trade_log (
id bigint(20)  default '0' comment '日志流水号',
trade_id bigint(20)  default '0' comment '交易流水号',
trade_param varchar(256)  default '' comment '交易输出参数',
exe_time datetime  comment '执行时间  ',
exe_result varchar(8)  default '' comment '执行结果  ',
exe_msg varchar(1024)  default '' comment '执行反馈  ',
exe_engine varchar(8)  default '' comment '驱动方式 （手动、自动）',
exe_user varchar(32)  default '' comment '执行者',
memo varchar(32)  default '' comment '备注',
primary key(id)
);



-- ------------------------------------------
-- Table structure for trade_schedule by project trade
-- ------------------------------------------
drop table if exists trade_schedule ;
create table trade_schedule (
id bigint(20)  default '0' comment '调度流水号',
schedule_info varchar(64)  default '' comment '调度信息描述',
schedule_code varchar(32)  default '' comment '调度指令代码',
schedule_status varchar(8)  default '' comment '调度指令状态',
schedule_time varchar(32)  default '' comment '调度时间',
schedule_format varchar(32)  default '' comment '调度时间格式',
schedule_target varchar(32)  default '' comment '调度影响范围',
schedule_source varchar(8)  default '' comment '调度指令来源',
schedule_creater varchar(32)  default '' comment '调度指令发布人',
memo varchar(32)  default '' comment '备注',
primary key(id)
);

