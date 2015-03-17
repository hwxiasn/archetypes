/*
Navicat MySQL Data Transfer

Source Server         : localhost
Source Server Version : 50519
Source Host           : localhost:3306
Source Database       : ygb

Target Server Type    : MYSQL
Target Server Version : 50519
File Encoding         : 65001

Date: 2015-03-17 11:36:09
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `account`
-- ----------------------------
DROP TABLE IF EXISTS `account`;
CREATE TABLE `account` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `password` varchar(50) DEFAULT NULL,
  `salt` varchar(50) DEFAULT NULL,
  `create_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `deleted` bit(1) DEFAULT b'0',
  `version` int(11) DEFAULT '1',
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=150127140852010034 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of account
-- ----------------------------

-- ----------------------------
-- Table structure for `account_daily`
-- ----------------------------
DROP TABLE IF EXISTS `account_daily`;
CREATE TABLE `account_daily` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `account_id` bigint(20) DEFAULT NULL,
  `daily` varchar(10) DEFAULT NULL,
  `balance` decimal(20,2) NOT NULL DEFAULT '0.00',
  `freeze_balance` decimal(20,2) NOT NULL DEFAULT '0.00',
  `daily_balance` decimal(20,2) NOT NULL DEFAULT '0.00',
  `daily_freeze_balance` decimal(20,2) NOT NULL DEFAULT '0.00',
  `status` varchar(20) DEFAULT NULL,
  `sub_balances` varchar(255) DEFAULT NULL,
  `daily_sub_balances` varchar(255) DEFAULT NULL,
  `create_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `deleted` bit(1) DEFAULT b'0',
  `version` int(11) DEFAULT '1',
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=150127000307032695 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of account_daily
-- ----------------------------

-- ----------------------------
-- Table structure for `account_log`
-- ----------------------------
DROP TABLE IF EXISTS `account_log`;
CREATE TABLE `account_log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `type` varchar(20) DEFAULT NULL,
  `sub_type` varchar(20) DEFAULT NULL,
  `transfer_type` varchar(20) DEFAULT NULL,
  `trade_id` bigint(20) DEFAULT NULL,
  `sub_account_log_id` bigint(20) DEFAULT NULL,
  `sub_account_id` bigint(20) DEFAULT NULL,
  `other_sub_account_id` bigint(20) DEFAULT NULL,
  `balance` decimal(20,2) NOT NULL DEFAULT '0.00',
  `trans_amount` decimal(20,2) NOT NULL DEFAULT '0.00',
  `fee` decimal(20,2) NOT NULL DEFAULT '0.00',
  `fee_sub_account_id` bigint(20) DEFAULT NULL,
  `memo` varchar(50) DEFAULT NULL,
  `executed` bit(1) DEFAULT b'0',
  `account_balance` decimal(20,2) NOT NULL DEFAULT '0.00',
  `account_freeze_balance` decimal(20,2) NOT NULL DEFAULT '0.00',
  `account_balance2` decimal(20,2) NOT NULL DEFAULT '0.00',
  `account_freeze_balance2` decimal(20,2) NOT NULL DEFAULT '0.00',
  `create_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `deleted` bit(1) DEFAULT b'0',
  `version` int(11) DEFAULT '1',
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=150129111726030003 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of account_log
-- ----------------------------

-- ----------------------------
-- Table structure for `address`
-- ----------------------------
DROP TABLE IF EXISTS `address`;
CREATE TABLE `address` (
  `id` bigint(20) NOT NULL COMMENT '主键ID',
  `user_id` bigint(20) NOT NULL COMMENT 'user表主键',
  `address_type` varchar(4) DEFAULT NULL COMMENT '地址类型',
  `province` varchar(16) DEFAULT NULL COMMENT '省',
  `city` varchar(16) DEFAULT NULL COMMENT '市',
  `district` varchar(16) DEFAULT NULL COMMENT '地区',
  `postcode` varchar(16) DEFAULT NULL COMMENT '邮编',
  `address1` varchar(32) DEFAULT NULL COMMENT '地址1',
  `address2` varchar(32) DEFAULT NULL COMMENT '地址2',
  `memo` varchar(255) DEFAULT NULL COMMENT '备注',
  `create_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `deleted` bit(1) DEFAULT b'0',
  `version` int(11) DEFAULT '1',
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of address
-- ----------------------------

-- ----------------------------
-- Table structure for `article`
-- ----------------------------
DROP TABLE IF EXISTS `article`;
CREATE TABLE `article` (
  `id` bigint(20) NOT NULL COMMENT '主键ID',
  `site` varchar(16) NOT NULL,
  `subject` varchar(6) NOT NULL COMMENT '所属专题',
  `title` varchar(128) NOT NULL COMMENT '标题',
  `author` varchar(32) NOT NULL COMMENT '作者',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `publish_time` datetime DEFAULT NULL COMMENT '发布时间',
  `weight` int(1) NOT NULL DEFAULT '1' COMMENT '权重 1.普通；3.置顶',
  `status` int(1) NOT NULL DEFAULT '1' COMMENT '状态； 1. READY; 2. ONLINE; 3. OFFLINE',
  `link` varchar(256) DEFAULT NULL COMMENT '链接',
  `create_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `deleted` bit(1) DEFAULT b'0',
  `version` int(11) DEFAULT '1',
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of article
-- ----------------------------

-- ----------------------------
-- Table structure for `article_publish_task`
-- ----------------------------
DROP TABLE IF EXISTS `article_publish_task`;
CREATE TABLE `article_publish_task` (
  `id` bigint(20) NOT NULL DEFAULT '0' COMMENT '主键',
  `article_id` bigint(20) NOT NULL COMMENT '文章ID',
  `publish_time` datetime NOT NULL COMMENT '发布时间',
  `status` int(1) NOT NULL DEFAULT '0' COMMENT '任务状态',
  `create_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `deleted` bit(1) DEFAULT b'0',
  `version` int(11) DEFAULT '1',
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of article_publish_task
-- ----------------------------

-- ----------------------------
-- Table structure for `article_text`
-- ----------------------------
DROP TABLE IF EXISTS `article_text`;
CREATE TABLE `article_text` (
  `id` bigint(20) NOT NULL COMMENT '主键ID',
  `article_id` bigint(20) NOT NULL COMMENT '文章ID',
  `text_stuff` mediumtext COMMENT '文章正文',
  `create_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `deleted` bit(1) DEFAULT b'0',
  `version` int(11) DEFAULT '1',
  PRIMARY KEY (`id`),
  UNIQUE KEY `article_id` (`article_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of article_text
-- ----------------------------

-- ----------------------------
-- Table structure for `bank_type`
-- ----------------------------
DROP TABLE IF EXISTS `bank_type`;
CREATE TABLE `bank_type` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(50) DEFAULT NULL,
  `code` varchar(20) DEFAULT NULL,
  `qdd_code` varchar(20) DEFAULT NULL,
  `create_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `deleted` bit(1) DEFAULT b'0',
  `version` int(11) DEFAULT '1',
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=51 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of bank_type
-- ----------------------------
INSERT INTO `bank_type` VALUES ('19', '中国农业银行', 'ABC', '3', '2015-03-17 11:19:30', '', '1');
INSERT INTO `bank_type` VALUES ('20', '中国银行', 'BOC', '1', '2015-03-17 11:19:30', '', '1');
INSERT INTO `bank_type` VALUES ('21', '交通银行', 'COMM', '4', '2015-03-17 11:19:30', '', '1');
INSERT INTO `bank_type` VALUES ('22', '中国建设银行', 'CCB', '7', '2015-03-17 11:19:30', '', '1');
INSERT INTO `bank_type` VALUES ('23', '中国光大银行', 'CEB', '19', '2015-03-17 11:19:30', '', '1');
INSERT INTO `bank_type` VALUES ('24', '兴业银行', 'CIB', '13', '2015-03-17 11:19:30', '', '1');
INSERT INTO `bank_type` VALUES ('25', '招商银行', 'CMB', '10', '2015-03-17 11:19:30', '', '1');
INSERT INTO `bank_type` VALUES ('26', '民生银行', 'CMBC', '12', '2015-03-17 11:19:30', '', '1');
INSERT INTO `bank_type` VALUES ('27', '中信银行', 'CITIC', '17', '2015-03-17 11:19:30', '', '1');
INSERT INTO `bank_type` VALUES ('28', '中国工商银行', 'ICBC', '2', '2015-03-17 11:19:30', '', '1');
INSERT INTO `bank_type` VALUES ('29', '中国邮政储蓄银行', 'PSBC', '11', '2015-03-17 11:19:30', '', '1');
INSERT INTO `bank_type` VALUES ('30', '浦发银行', 'SPDB', '8', '2015-03-17 11:19:30', '', '1');
INSERT INTO `bank_type` VALUES ('31', '重庆银行', 'CQCB', '31', '2015-03-17 11:19:30', '', '1');
INSERT INTO `bank_type` VALUES ('32', '广东发展银行', 'GDB', '14', '2015-03-17 11:19:30', '', '1');
INSERT INTO `bank_type` VALUES ('33', '深圳发展银行', 'SDB', '6', '2015-03-17 11:19:30', '', '1');
INSERT INTO `bank_type` VALUES ('34', '华夏银行', 'HXB', '18', '2015-03-17 11:19:30', '', '1');
INSERT INTO `bank_type` VALUES ('35', '平安银行', 'PINGANBANK', '28', '2015-03-17 11:19:30', '', '1');
INSERT INTO `bank_type` VALUES ('36', '上海银行', 'BANKSH', '21', '2015-03-17 11:19:30', '', '1');
INSERT INTO `bank_type` VALUES ('37', '东莞银行', 'DGCB', '15', '2015-03-17 11:19:30', '', '1');
INSERT INTO `bank_type` VALUES ('38', '北京银行', 'BOBJ', '20', '2015-03-17 11:19:30', '', '1');
INSERT INTO `bank_type` VALUES ('39', '天津银行', 'TJCB', '22', '2015-03-17 11:19:30', '', '1');
INSERT INTO `bank_type` VALUES ('40', '大连银行', 'DLCB', '23', '2015-03-17 11:19:30', '', '1');
INSERT INTO `bank_type` VALUES ('41', '杭州银行', 'HZCB', '24', '2015-03-17 11:19:30', '', '1');
INSERT INTO `bank_type` VALUES ('42', '宁波银行', 'NBCB', '25', '2015-03-17 11:19:30', '', '1');
INSERT INTO `bank_type` VALUES ('43', '厦门银行', 'XMCB', '26', '2015-03-17 11:19:30', '', '1');
INSERT INTO `bank_type` VALUES ('44', '广州银行', 'GZCB', '27', '2015-03-17 11:19:30', '', '1');
INSERT INTO `bank_type` VALUES ('45', '江苏银行', 'JSBK', '32', '2015-03-17 11:19:30', '', '1');
INSERT INTO `bank_type` VALUES ('46', '农村信用合作社', 'RCU', '33', '2015-03-17 11:19:30', '', '1');
INSERT INTO `bank_type` VALUES ('47', '内蒙古银行', 'NMBK', '36', '2015-03-17 11:19:30', '', '1');
INSERT INTO `bank_type` VALUES ('48', '深圳农村商业银行', 'SZRB', '37', '2015-03-17 11:19:30', '', '1');
INSERT INTO `bank_type` VALUES ('49', '贵阳银行', 'GYCB', '38', '2015-03-17 11:19:30', '', '1');
INSERT INTO `bank_type` VALUES ('50', '贵州银行', 'GZBK', '39', '2015-03-17 11:19:30', '', '1');

-- ----------------------------
-- Table structure for `borrower_info`
-- ----------------------------
DROP TABLE IF EXISTS `borrower_info`;
CREATE TABLE `borrower_info` (
  `id` bigint(20) NOT NULL DEFAULT '0' COMMENT '主键',
  `name` varchar(20) NOT NULL COMMENT '借款人姓名',
  `age` int(3) NOT NULL COMMENT '借款人年龄',
  `id_serial` varchar(20) NOT NULL COMMENT '借款人身份证号码',
  `sex` varchar(1) NOT NULL COMMENT '借款人性别',
  `marital_status` int(11) NOT NULL COMMENT '借款人婚姻状况',
  `phone` varchar(20) NOT NULL COMMENT '借款人电话号码',
  `contact_qq` varchar(20) DEFAULT NULL COMMENT '借款人QQ号码',
  `contact1` varchar(30) DEFAULT NULL COMMENT '借款人备用联系方式1',
  `contact2` varchar(30) DEFAULT NULL COMMENT '借款人备用联系方式2',
  `address` varchar(100) DEFAULT NULL COMMENT '借款人现住址',
  `loan_amount` int(11) NOT NULL COMMENT '借款金额',
  `loan_term` int(11) NOT NULL COMMENT '借款期限',
  `purpose` varchar(20) NOT NULL COMMENT '借款用途',
  `return_source` varchar(20) NOT NULL COMMENT '还款来源',
  `business_scope` varchar(255) DEFAULT NULL COMMENT '公司经营范围',
  `register_capital` int(11) DEFAULT NULL COMMENT '公司注册资金',
  `found_year` int(11) DEFAULT NULL COMMENT '公司成立年份',
  `found_month` int(11) DEFAULT NULL COMMENT '公司成立月份',
  `message` varchar(200) DEFAULT NULL COMMENT '留言附注',
  `create_time` datetime NOT NULL COMMENT '信息录入时间',
  `status` int(11) NOT NULL COMMENT '信息状态',
  `create_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `deleted` bit(1) DEFAULT b'0',
  `version` int(11) DEFAULT '1',
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of borrower_info
-- ----------------------------

-- ----------------------------
-- Table structure for `code_list`
-- ----------------------------
DROP TABLE IF EXISTS `code_list`;
CREATE TABLE `code_list` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `type` varchar(50) DEFAULT NULL,
  `name` varchar(50) DEFAULT NULL,
  `code` varchar(20) DEFAULT NULL,
  `create_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `deleted` bit(1) DEFAULT b'0',
  `version` int(11) DEFAULT '1',
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=280 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of code_list
-- ----------------------------
INSERT INTO `code_list` VALUES ('140', 'USER_ROLE', '投资接受人', 'B', '2015-03-17 11:19:25', '', '1');
INSERT INTO `code_list` VALUES ('141', 'USER_ROLE', '经纪人', 'BR', '2015-03-17 11:19:25', '', '1');
INSERT INTO `code_list` VALUES ('142', 'USER_ROLE', '投资人', 'I', '2015-03-17 11:19:25', '', '1');
INSERT INTO `code_list` VALUES ('143', 'USER_ROLE', '保荐人', 'SP', '2015-03-17 11:19:25', '', '1');
INSERT INTO `code_list` VALUES ('144', 'USER_ROLE', '超级经纪人', 'S', '2015-03-17 11:19:25', '', '1');
INSERT INTO `code_list` VALUES ('145', 'AGENCY_ROLE', '营销机构', 'AG', '2015-03-17 11:19:25', '', '1');
INSERT INTO `code_list` VALUES ('146', 'AGENCY_ROLE', '担保机构', 'G', '2015-03-17 11:19:25', '', '1');
INSERT INTO `code_list` VALUES ('147', 'AGENCY_ROLE', '投资机构', 'IE', '2015-03-17 11:19:25', '', '1');
INSERT INTO `code_list` VALUES ('148', 'AGENCY_ROLE', '保荐机构', 'SE', '2015-03-17 11:19:25', '', '1');
INSERT INTO `code_list` VALUES ('149', 'AGENCY_ROLE', '业务账户', 'O', '2015-03-17 11:19:25', '', '1');
INSERT INTO `code_list` VALUES ('150', 'AGENCY_ROLE', '小贷机构', 'L', '2015-03-17 11:19:25', '', '1');
INSERT INTO `code_list` VALUES ('151', 'USER_STATUS', '正常', 'A', '2015-03-17 11:19:25', '', '1');
INSERT INTO `code_list` VALUES ('152', 'USER_STATUS', '冻结', 'L', '2015-03-17 11:19:25', '', '1');
INSERT INTO `code_list` VALUES ('153', 'USER_STATUS', '禁用', 'D', '2015-03-17 11:19:25', '', '1');
INSERT INTO `code_list` VALUES ('154', 'USER_STATUS', '未激活', 'I', '2015-03-17 11:19:25', '', '1');
INSERT INTO `code_list` VALUES ('155', 'USER_STATUS', '解冻', 'J', '2015-03-17 11:19:25', '', '1');
INSERT INTO `code_list` VALUES ('156', 'IDENTITY_STATUS', '已认证', 'I', '2015-03-17 11:19:25', '', '1');
INSERT INTO `code_list` VALUES ('157', 'IDENTITY_STATUS', '未认证', 'NI', '2015-03-17 11:19:25', '', '1');
INSERT INTO `code_list` VALUES ('158', 'USER_RELATIONSHIP', '营销公司与经纪人', 'A-B', '2015-03-17 11:19:25', '', '1');
INSERT INTO `code_list` VALUES ('159', 'USER_RELATIONSHIP', '经纪人', 'B', '2015-03-17 11:19:25', '', '1');
INSERT INTO `code_list` VALUES ('160', 'USER_RELATIONSHIP', '操作员', 'O', '2015-03-17 11:19:25', '', '1');
INSERT INTO `code_list` VALUES ('161', 'USER_RELATIONSHIP', '企业用户与操作员', 'E-O', '2015-03-17 11:19:25', '', '1');
INSERT INTO `code_list` VALUES ('162', 'USER_RELATIONSHIP', '经纪人与投资人', 'B-I', '2015-03-17 11:19:25', '', '1');
INSERT INTO `code_list` VALUES ('163', 'COMMISSION_ROLE', '平台', 'P', '2015-03-17 11:19:25', '', '1');
INSERT INTO `code_list` VALUES ('164', 'COMMISSION_ROLE', '担保机构', 'G', '2015-03-17 11:19:25', '', '1');
INSERT INTO `code_list` VALUES ('165', 'COMMISSION_ROLE', '经纪人', 'BR', '2015-03-17 11:19:25', '', '1');
INSERT INTO `code_list` VALUES ('166', 'COMMISSION_ROLE', '营销机构', 'AG', '2015-03-17 11:19:25', '', '1');
INSERT INTO `code_list` VALUES ('167', 'COMMISSION_ROLE', '保荐机构', 'SE', '2015-03-17 11:19:25', '', '1');
INSERT INTO `code_list` VALUES ('168', 'COMMISSION_ROLE', '投资接受人', 'B', '2015-03-17 11:19:25', '', '1');
INSERT INTO `code_list` VALUES ('169', 'COMMISSION_ROLE', '投资人', 'I', '2015-03-17 11:19:25', '', '1');
INSERT INTO `code_list` VALUES ('170', 'COMMISSION_ROLE', '小贷机构', 'L', '2015-03-17 11:19:25', '', '1');
INSERT INTO `code_list` VALUES ('171', 'COMMISSION_PHASE', '募资阶段', 'F', '2015-03-17 11:19:25', '', '1');
INSERT INTO `code_list` VALUES ('172', 'COMMISSION_PHASE', '还款阶段', 'P', '2015-03-17 11:19:25', '', '1');
INSERT INTO `code_list` VALUES ('173', 'COMMISSION_TYPE', '到期还本付息', 'T', '2015-03-17 11:19:25', '', '1');
INSERT INTO `code_list` VALUES ('174', 'COMMISSION_TYPE', '按月期返', 'M', '2015-03-17 11:19:25', '', '1');
INSERT INTO `code_list` VALUES ('175', 'COMMISSION_TYPE', '按月付息一次性还本', 'RM', '2015-03-17 11:19:25', '', '1');
INSERT INTO `code_list` VALUES ('176', 'COMMISSION_TYPE', '按季付息一次性还本', 'RQ', '2015-03-17 11:19:25', '', '1');
INSERT INTO `code_list` VALUES ('177', 'LOAN_TYPE', '固定期限', 'FIX_PERIOD', '2015-03-17 11:19:25', '', '1');
INSERT INTO `code_list` VALUES ('178', 'LOAN_TYPE', '定制天数', 'DAYS', '2015-03-17 11:19:25', '', '1');
INSERT INTO `code_list` VALUES ('179', 'LOAN_TYPE', '定制月份', 'MONTHS', '2015-03-17 11:19:25', '', '1');
INSERT INTO `code_list` VALUES ('180', 'FIX_PERIOD', '一月期', '1', '2015-03-17 11:19:25', '', '1');
INSERT INTO `code_list` VALUES ('181', 'FIX_PERIOD', '三月期', '3', '2015-03-17 11:19:25', '', '1');
INSERT INTO `code_list` VALUES ('182', 'FIX_PERIOD', '半年期', '6', '2015-03-17 11:19:25', '', '1');
INSERT INTO `code_list` VALUES ('183', 'FIX_PERIOD', '一年期', '12', '2015-03-17 11:19:25', '', '1');
INSERT INTO `code_list` VALUES ('184', 'DAYS', '1天', '1', '2015-03-17 11:19:25', '', '1');
INSERT INTO `code_list` VALUES ('185', 'DAYS', '2天', '2', '2015-03-17 11:19:25', '', '1');
INSERT INTO `code_list` VALUES ('186', 'DAYS', '3天', '3', '2015-03-17 11:19:25', '', '1');
INSERT INTO `code_list` VALUES ('187', 'DAYS', '4天', '4', '2015-03-17 11:19:25', '', '1');
INSERT INTO `code_list` VALUES ('188', 'DAYS', '5天', '5', '2015-03-17 11:19:25', '', '1');
INSERT INTO `code_list` VALUES ('189', 'DAYS', '6天', '6', '2015-03-17 11:19:25', '', '1');
INSERT INTO `code_list` VALUES ('190', 'DAYS', '7天', '7', '2015-03-17 11:19:25', '', '1');
INSERT INTO `code_list` VALUES ('191', 'DAYS', '8天', '8', '2015-03-17 11:19:25', '', '1');
INSERT INTO `code_list` VALUES ('192', 'DAYS', '9天', '9', '2015-03-17 11:19:25', '', '1');
INSERT INTO `code_list` VALUES ('193', 'DAYS', '10天', '10', '2015-03-17 11:19:25', '', '1');
INSERT INTO `code_list` VALUES ('194', 'DAYS', '11天', '11', '2015-03-17 11:19:25', '', '1');
INSERT INTO `code_list` VALUES ('195', 'DAYS', '12天', '12', '2015-03-17 11:19:25', '', '1');
INSERT INTO `code_list` VALUES ('196', 'DAYS', '13天', '13', '2015-03-17 11:19:25', '', '1');
INSERT INTO `code_list` VALUES ('197', 'DAYS', '14天', '14', '2015-03-17 11:19:25', '', '1');
INSERT INTO `code_list` VALUES ('198', 'DAYS', '15天', '15', '2015-03-17 11:19:25', '', '1');
INSERT INTO `code_list` VALUES ('199', 'DAYS', '16天', '16', '2015-03-17 11:19:25', '', '1');
INSERT INTO `code_list` VALUES ('200', 'DAYS', '17天', '17', '2015-03-17 11:19:25', '', '1');
INSERT INTO `code_list` VALUES ('201', 'DAYS', '18天', '18', '2015-03-17 11:19:25', '', '1');
INSERT INTO `code_list` VALUES ('202', 'DAYS', '19天', '19', '2015-03-17 11:19:25', '', '1');
INSERT INTO `code_list` VALUES ('203', 'DAYS', '20天', '20', '2015-03-17 11:19:25', '', '1');
INSERT INTO `code_list` VALUES ('204', 'DAYS', '21天', '21', '2015-03-17 11:19:25', '', '1');
INSERT INTO `code_list` VALUES ('205', 'DAYS', '22天', '22', '2015-03-17 11:19:25', '', '1');
INSERT INTO `code_list` VALUES ('206', 'DAYS', '23天', '23', '2015-03-17 11:19:25', '', '1');
INSERT INTO `code_list` VALUES ('207', 'DAYS', '24天', '24', '2015-03-17 11:19:25', '', '1');
INSERT INTO `code_list` VALUES ('208', 'DAYS', '25天', '25', '2015-03-17 11:19:25', '', '1');
INSERT INTO `code_list` VALUES ('209', 'DAYS', '26天', '26', '2015-03-17 11:19:25', '', '1');
INSERT INTO `code_list` VALUES ('210', 'DAYS', '27天', '27', '2015-03-17 11:19:25', '', '1');
INSERT INTO `code_list` VALUES ('211', 'DAYS', '28天', '28', '2015-03-17 11:19:25', '', '1');
INSERT INTO `code_list` VALUES ('212', 'DAYS', '29天', '29', '2015-03-17 11:19:25', '', '1');
INSERT INTO `code_list` VALUES ('213', 'DAYS', '30天', '30', '2015-03-17 11:19:25', '', '1');
INSERT INTO `code_list` VALUES ('214', 'MONTHS', '1个月', '1', '2015-03-17 11:19:25', '', '1');
INSERT INTO `code_list` VALUES ('215', 'MONTHS', '2个月', '2', '2015-03-17 11:19:25', '', '1');
INSERT INTO `code_list` VALUES ('216', 'MONTHS', '3个月', '3', '2015-03-17 11:19:25', '', '1');
INSERT INTO `code_list` VALUES ('217', 'MONTHS', '4个月', '4', '2015-03-17 11:19:25', '', '1');
INSERT INTO `code_list` VALUES ('218', 'MONTHS', '5个月', '5', '2015-03-17 11:19:25', '', '1');
INSERT INTO `code_list` VALUES ('219', 'MONTHS', '6个月', '6', '2015-03-17 11:19:25', '', '1');
INSERT INTO `code_list` VALUES ('220', 'MONTHS', '7个月', '7', '2015-03-17 11:19:25', '', '1');
INSERT INTO `code_list` VALUES ('221', 'MONTHS', '8个月', '8', '2015-03-17 11:19:25', '', '1');
INSERT INTO `code_list` VALUES ('222', 'MONTHS', '9个月', '9', '2015-03-17 11:19:25', '', '1');
INSERT INTO `code_list` VALUES ('223', 'MONTHS', '10个月', '10', '2015-03-17 11:19:25', '', '1');
INSERT INTO `code_list` VALUES ('224', 'MONTHS', '11个月', '11', '2015-03-17 11:19:25', '', '1');
INSERT INTO `code_list` VALUES ('225', 'MONTHS', '12个月', '12', '2015-03-17 11:19:25', '', '1');
INSERT INTO `code_list` VALUES ('226', 'MIN_AMOUNT', '1万', '1', '2015-03-17 11:19:25', '', '1');
INSERT INTO `code_list` VALUES ('227', 'MIN_AMOUNT', '2万', '2', '2015-03-17 11:19:25', '', '1');
INSERT INTO `code_list` VALUES ('228', 'MIN_AMOUNT', '3万', '3', '2015-03-17 11:19:25', '', '1');
INSERT INTO `code_list` VALUES ('229', 'SETTLE_TYPE', '固定金额', 'FIX_AMOUNT', '2015-03-17 11:19:25', '', '1');
INSERT INTO `code_list` VALUES ('230', 'SETTLE_TYPE', '固定比例', 'FIX_PERCENTAGE', '2015-03-17 11:19:25', '', '1');
INSERT INTO `code_list` VALUES ('231', 'PROJECT_STATUS', '草稿', 'DR', '2015-03-17 11:19:25', '', '1');
INSERT INTO `code_list` VALUES ('232', 'PROJECT_STATUS', '待审核', 'R', '2015-03-17 11:19:25', '', '1');
INSERT INTO `code_list` VALUES ('233', 'PROJECT_STATUS', '发布失败', 'PF', '2015-03-17 11:19:25', '', '1');
INSERT INTO `code_list` VALUES ('234', 'PROJECT_STATUS', '待发布', 'PP', '2015-03-17 11:19:25', '', '1');
INSERT INTO `code_list` VALUES ('235', 'PROJECT_STATUS', '已发布', 'P', '2015-03-17 11:19:25', '', '1');
INSERT INTO `code_list` VALUES ('236', 'PROJECT_STATUS', '募集中', 'F', '2015-03-17 11:19:25', '', '1');
INSERT INTO `code_list` VALUES ('237', 'PROJECT_STATUS', '募集失败', 'FF', '2015-03-17 11:19:25', '', '1');
INSERT INTO `code_list` VALUES ('238', 'PROJECT_STATUS', '募集完成', 'FC', '2015-03-17 11:19:25', '', '1');
INSERT INTO `code_list` VALUES ('239', 'PROJECT_STATUS', '项目成立', 'PD', '2015-03-17 11:19:25', '', '1');
INSERT INTO `code_list` VALUES ('240', 'PROJECT_STATUS', '项目还款中', 'PR', '2015-03-17 11:19:25', '', '1');
INSERT INTO `code_list` VALUES ('241', 'PROJECT_STATUS', '项目已还款', 'RC', '2015-03-17 11:19:25', '', '1');
INSERT INTO `code_list` VALUES ('242', 'PROJECT_STATUS', '项目分润中', 'CR', '2015-03-17 11:19:25', '', '1');
INSERT INTO `code_list` VALUES ('243', 'PROJECT_STATUS', '项目分润完成', 'CRC', '2015-03-17 11:19:25', '', '1');
INSERT INTO `code_list` VALUES ('244', 'PROJECT_STATUS', '司法受理', 'J', '2015-03-17 11:19:25', '', '1');
INSERT INTO `code_list` VALUES ('245', 'PROJECT_STATUS', '担保公司审核中', 'GR', '2015-03-17 11:19:25', '', '1');
INSERT INTO `code_list` VALUES ('246', 'PROJECT_STATUS', '已驳回', 'RP', '2015-03-17 11:19:25', '', '1');
INSERT INTO `code_list` VALUES ('247', 'PROJECT_STATUS', '已审核', 'A', '2015-03-17 11:19:25', '', '1');
INSERT INTO `code_list` VALUES ('248', 'TRANSACTION_TYPE', '募集交易', 'F', '2015-03-17 11:19:25', '', '1');
INSERT INTO `code_list` VALUES ('249', 'TRANSACTION_TYPE', '还款交易', 'P', '2015-03-17 11:19:25', '', '1');
INSERT INTO `code_list` VALUES ('250', 'TRANSACTION_TYPE', '分佣交易', 'C', '2015-03-17 11:19:25', '', '1');
INSERT INTO `code_list` VALUES ('251', 'TRANSACTION_TYPE', '投资交易', 'I', '2015-03-17 11:19:25', '', '1');
INSERT INTO `code_list` VALUES ('252', 'TRANSACTION_TYPE', '快捷充值', 'DD', '2015-03-17 11:19:25', '', '1');
INSERT INTO `code_list` VALUES ('253', 'TRANSACTION_TYPE', '网银充值', 'DN', '2015-03-17 11:19:25', '', '1');
INSERT INTO `code_list` VALUES ('254', 'TRANSACTION_TYPE', '提现交易', 'W', '2015-03-17 11:19:25', '', '1');
INSERT INTO `code_list` VALUES ('255', 'TRANSACTION_TYPE', '转账交易', 'T', '2015-03-17 11:19:25', '', '1');
INSERT INTO `code_list` VALUES ('256', 'TRANSACTION_TYPE', '收费交易', 'CC', '2015-03-17 11:19:25', '', '1');
INSERT INTO `code_list` VALUES ('257', 'TRANSACTION_STATUS', '待执行', 'P', '2015-03-17 11:19:25', '', '1');
INSERT INTO `code_list` VALUES ('258', 'TRANSACTION_STATUS', '已执行', 'E', '2015-03-17 11:19:25', '', '1');
INSERT INTO `code_list` VALUES ('259', 'TRANSACTION_STATUS', '执行失败', 'F', '2015-03-17 11:19:25', '', '1');
INSERT INTO `code_list` VALUES ('260', 'TRANSACTION_STATUS', '执行中', 'I', '2015-03-17 11:19:25', '', '1');
INSERT INTO `code_list` VALUES ('261', 'GUARANTEELETTER_STATUS', '承诺担保中', 'PR', '2015-03-17 11:19:25', '', '1');
INSERT INTO `code_list` VALUES ('262', 'GUARANTEELETTER_STATUS', '失败', 'FL', '2015-03-17 11:19:25', '', '1');
INSERT INTO `code_list` VALUES ('263', 'GUARANTEELETTER_STATUS', '担保公司审核中', 'CH', '2015-03-17 11:19:25', '', '1');
INSERT INTO `code_list` VALUES ('264', 'GUARANTEELETTER_STATUS', '一级审核完成', 'L', '2015-03-17 11:19:25', '', '1');
INSERT INTO `code_list` VALUES ('265', 'GUARANTEELETTER_STATUS', '担保函签发中', 'PS', '2015-03-17 11:19:25', '', '1');
INSERT INTO `code_list` VALUES ('266', 'GUARANTEELETTER_STATUS', '担保函签发审核中', 'PA', '2015-03-17 11:19:25', '', '1');
INSERT INTO `code_list` VALUES ('267', 'GUARANTEELETTER_STATUS', '担保函签发成功', 'PB', '2015-03-17 11:19:25', '', '1');
INSERT INTO `code_list` VALUES ('268', 'GUARANTEELETTER_STATUS', '担保函签发失败', 'PC', '2015-03-17 11:19:25', '', '1');
INSERT INTO `code_list` VALUES ('269', 'GUARANTEELETTER_STATUS', '担保函履约中', 'P', '2015-03-17 11:19:25', '', '1');
INSERT INTO `code_list` VALUES ('270', 'GUARANTEELETTER_STATUS', '担保函履约完毕', 'PD', '2015-03-17 11:19:25', '', '1');
INSERT INTO `code_list` VALUES ('271', 'GUARANTEELETTER_STATUS', '担保函违约', 'F', '2015-03-17 11:19:25', '', '1');
INSERT INTO `code_list` VALUES ('272', 'GUARANTEELETTER_STATUS', '代偿还款', 'FC', '2015-03-17 11:19:25', '', '1');
INSERT INTO `code_list` VALUES ('273', 'GUARANTEELETTER_STATUS', '司法处置中', 'FJ', '2015-03-17 11:19:25', '', '1');
INSERT INTO `code_list` VALUES ('274', 'GUARANTEELETTER_STATUS', '司法处置还款', 'FR', '2015-03-17 11:19:25', '', '1');
INSERT INTO `code_list` VALUES ('275', 'OPERATOR_TYPE', '普通人员', 'L0', '2015-03-17 11:19:25', '', '1');
INSERT INTO `code_list` VALUES ('276', 'OPERATOR_TYPE', '一级审核员', 'L1', '2015-03-17 11:19:25', '', '1');
INSERT INTO `code_list` VALUES ('277', 'OPERATOR_TYPE', '二级审核员', 'L2', '2015-03-17 11:19:25', '', '1');
INSERT INTO `code_list` VALUES ('278', 'COMMISSION_STATUS', '正常', 'A', '2015-03-17 11:19:25', '', '1');
INSERT INTO `code_list` VALUES ('279', 'COMMISSION_STATUS', '冻结', 'D', '2015-03-17 11:19:25', '', '1');

-- ----------------------------
-- Table structure for `commission_detail`
-- ----------------------------
DROP TABLE IF EXISTS `commission_detail`;
CREATE TABLE `commission_detail` (
  `id` bigint(20) NOT NULL DEFAULT '0' COMMENT '统一的ID编码规则',
  `template_id` bigint(20) DEFAULT '0' COMMENT '模板ID',
  `role` varchar(16) DEFAULT '' COMMENT '角色代码',
  `rate` decimal(6,4) DEFAULT '0.0000' COMMENT '费率',
  `create_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `deleted` bit(1) NOT NULL DEFAULT b'0',
  `version` int(11) NOT NULL DEFAULT '1',
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of commission_detail
-- ----------------------------

-- ----------------------------
-- Table structure for `commission_fee`
-- ----------------------------
DROP TABLE IF EXISTS `commission_fee`;
CREATE TABLE `commission_fee` (
  `id` bigint(20) NOT NULL DEFAULT '0' COMMENT '分佣配置信息',
  `fee_rate` decimal(6,4) DEFAULT '0.0000' COMMENT '分佣收益率（年化） ',
  `fee_type` varchar(16) DEFAULT '' COMMENT '费率类型',
  `status` varchar(8) DEFAULT '' COMMENT ' 担保函状态  ',
  `credit_user_id` bigint(20) DEFAULT '0' COMMENT ' 分出人ID',
  `debit_user_id` bigint(20) DEFAULT '0' COMMENT ' 接受人ID',
  `debit_customer_num` varchar(32) DEFAULT '' COMMENT ' 接受人客户编号',
  `debit_user_name` varchar(32) DEFAULT '' COMMENT ' 接受人用户名',
  `debit_user_real_name` varchar(32) DEFAULT '' COMMENT ' 接受人真实姓名',
  `name` varchar(128) DEFAULT '' COMMENT '名称',
  `project_source` varchar(64) DEFAULT '' COMMENT '项目来源',
  `project_type` varchar(64) DEFAULT '' COMMENT '项目类型',
  `memo` varchar(256) DEFAULT '' COMMENT '备注',
  `create_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `deleted` bit(1) DEFAULT b'0',
  `version` int(11) DEFAULT '1',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of commission_fee
-- ----------------------------

-- ----------------------------
-- Table structure for `commission_fee_log`
-- ----------------------------
DROP TABLE IF EXISTS `commission_fee_log`;
CREATE TABLE `commission_fee_log` (
  `id` bigint(20) NOT NULL DEFAULT '0' COMMENT '日志ID',
  `fee_id` bigint(20) DEFAULT '0' COMMENT '分佣配置信息ID',
  `old_fee_rate` decimal(6,4) DEFAULT '0.0000' COMMENT '原分佣收益率（年化） ',
  `old_project_source` varchar(64) DEFAULT '' COMMENT '原项目来源',
  `old_project_type` varchar(64) DEFAULT '' COMMENT '原项目类型',
  `fee_rate` decimal(6,4) DEFAULT '0.0000' COMMENT '分佣收益率（年化） ',
  `project_source` varchar(64) DEFAULT '' COMMENT '项目来源',
  `project_type` varchar(64) DEFAULT '' COMMENT '项目类型',
  `action_type` varchar(32) DEFAULT '' COMMENT '操作',
  `action_user_id` bigint(20) DEFAULT '0' COMMENT '操作人ID',
  `memo` varchar(256) DEFAULT '' COMMENT '备注',
  `create_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `deleted` bit(1) DEFAULT b'0',
  `version` int(11) DEFAULT '1',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of commission_fee_log
-- ----------------------------

-- ----------------------------
-- Table structure for `commission_template`
-- ----------------------------
DROP TABLE IF EXISTS `commission_template`;
CREATE TABLE `commission_template` (
  `id` bigint(20) NOT NULL DEFAULT '0' COMMENT '统一的ID编码规则',
  `name` varchar(32) DEFAULT '' COMMENT ' 模板名称  ',
  `status` varchar(8) DEFAULT '' COMMENT ' 模板状态  ',
  `allot_phase` varchar(16) DEFAULT '' COMMENT ' 分配阶段  ',
  `allot_type` varchar(16) DEFAULT '' COMMENT ' 分配方式  ',
  `memo` varchar(256) DEFAULT '' COMMENT ' 模板描述  ',
  `locked` char(1) DEFAULT '0' COMMENT ' 是否锁定  ',
  `create_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `deleted` bit(1) NOT NULL DEFAULT b'0',
  `version` int(11) NOT NULL DEFAULT '1',
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of commission_template
-- ----------------------------

-- ----------------------------
-- Table structure for `commission_voucher_log`
-- ----------------------------
DROP TABLE IF EXISTS `commission_voucher_log`;
CREATE TABLE `commission_voucher_log` (
  `id` bigint(20) NOT NULL DEFAULT '0' COMMENT '分佣凭证ID',
  `investment_id` bigint(20) DEFAULT '0' COMMENT '投资凭证ID',
  `investment_user_id` bigint(20) DEFAULT '0' COMMENT '投资凭证投资人用户ID,冗余',
  `project_id` bigint(20) DEFAULT '0' COMMENT '项目ID',
  `investor_id` varchar(32) DEFAULT '' COMMENT '投资人用户ID',
  `investor_fee` decimal(20,2) DEFAULT '0.00' COMMENT '投资人额外收益 ',
  `broker_id` varchar(32) DEFAULT '' COMMENT '经纪人用户ID',
  `broker_fee` decimal(20,2) DEFAULT '0.00' COMMENT '经纪人分佣收益 ',
  `super_broker_id` varchar(32) DEFAULT '' COMMENT '超级经纪人用户ID',
  `super_broker_fee` decimal(20,2) DEFAULT '0.00' COMMENT '超级经纪人分佣收益 ',
  `agency_id` varchar(32) DEFAULT '' COMMENT '营销机构用户ID',
  `agency_fee` decimal(20,2) DEFAULT '0.00' COMMENT '营销机构分佣收益 ',
  `status` varchar(16) DEFAULT '' COMMENT '状态',
  `fee_date` datetime DEFAULT NULL COMMENT '分佣时间',
  `investor_date` datetime DEFAULT NULL COMMENT '投资人红利时间',
  `memo` varchar(256) DEFAULT '' COMMENT '备注',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of commission_voucher_log
-- ----------------------------

-- ----------------------------
-- Table structure for `deal`
-- ----------------------------
DROP TABLE IF EXISTS `deal`;
CREATE TABLE `deal` (
  `id` bigint(20) NOT NULL DEFAULT '0' COMMENT '交易执行流水号',
  `batch_id` bigint(20) DEFAULT '0' COMMENT '交易批次',
  `trade_id` bigint(20) DEFAULT '0' COMMENT '交易流水号',
  `source_deal_id` bigint(20) DEFAULT '0' COMMENT '源交易流水号',
  `credit_account` varchar(32) DEFAULT '' COMMENT '出借方交易账户ID（帐务流水号）',
  `debit_account` varchar(32) DEFAULT '' COMMENT '贷入方交易账户ID（帐务流水号）',
  `trade_amount` decimal(20,2) DEFAULT '0.00' COMMENT '实际交易金额，单位：人民币，元，保留2位精度',
  `trade_check` varchar(8) DEFAULT '' COMMENT '交易检验结果（检验通过、检验失败、无需检验）',
  `trade_type` varchar(8) DEFAULT '' COMMENT '交易类型（投资、还款、分佣、充值、提现、转账）',
  `trade_action` varchar(8) DEFAULT '' COMMENT '交易操作类型（冻结、操作）',
  `trade_status` varchar(8) DEFAULT '' COMMENT '交易状态（执行成功、执行失败、待执行、执行中）',
  `trade_param` varchar(256) DEFAULT '' COMMENT '交易输出参数',
  `trade_dispatch` varchar(1024) DEFAULT '' COMMENT '交易目的地址（需要手工参与的业务需要）',
  `deal_date` datetime DEFAULT NULL COMMENT '执行时间  ',
  `call_back` varchar(1024) DEFAULT '' COMMENT '执行反馈 ',
  `message` varchar(64) DEFAULT '' COMMENT ' 执行结果 ',
  `memo` varchar(32) DEFAULT '' COMMENT '备注',
  `create_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `deleted` bit(1) NOT NULL DEFAULT b'0',
  `version` int(11) NOT NULL DEFAULT '1',
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of deal
-- ----------------------------

-- ----------------------------
-- Table structure for `general_project`
-- ----------------------------
DROP TABLE IF EXISTS `general_project`;
CREATE TABLE `general_project` (
  `id` bigint(20) NOT NULL DEFAULT '0' COMMENT '项目流水号',
  `site` varchar(20) DEFAULT NULL COMMENT '来源平台',
  `name` varchar(128) DEFAULT '' COMMENT ' 项目名称  ',
  `total_amount` decimal(20,2) DEFAULT '0.00' COMMENT ' 融资金额（精度到分，单位：元）  ',
  `interest_rate` decimal(6,4) DEFAULT '0.0000' COMMENT ' 投资收益率（年化）  ',
  `period_type` varchar(16) DEFAULT '' COMMENT ' 借款期限类型  ',
  `period` varchar(16) DEFAULT '' COMMENT ' 借款期限（与类型合并可计算出具体周期）  ',
  `period_days` int(10) DEFAULT '0' COMMENT '借款天数-通过计算获取',
  `minimal_investment` decimal(20,2) DEFAULT '0.00' COMMENT ' 最低投资额（精度到分，单位：元）  ',
  `funding_template_id` varchar(32) DEFAULT '' COMMENT ' 分佣模板ID  ',
  `repay_template_id` varchar(32) DEFAULT '' COMMENT ' 分润模板ID  ',
  `loanee_id` bigint(20) DEFAULT '0' COMMENT ' 借款人ID  ',
  `loanee_user_name` varchar(64) DEFAULT '' COMMENT ' 借款人登录名',
  `purpose` varchar(1024) DEFAULT '' COMMENT ' 借款目的  ',
  `sponsor_user_id` bigint(20) DEFAULT '0' COMMENT ' 保荐机构用户ID  ',
  `sponsor_memo` varchar(1024) DEFAULT '' COMMENT ' 保荐机构推荐语  ',
  `type` varchar(16) DEFAULT '' COMMENT ' 项目类型（担保贷还是接力贷等）  ',
  `status` varchar(8) DEFAULT '' COMMENT ' 项目状态  ',
  `progress_amount` decimal(20,2) DEFAULT '0.00' COMMENT ' 已投资金额（精度到分，单位：元）  ',
  `progress` int(11) DEFAULT '0' COMMENT ' 投资百分比（百分数）  ',
  `total_investor` int(11) DEFAULT '0' COMMENT ' 投资总人数  ',
  `guarantee_letter_id` bigint(20) DEFAULT '0' COMMENT ' 担保函ID  ',
  `guarantee_user_id` varchar(32) DEFAULT '' COMMENT '担保公司ID',
  `contract_no` varchar(128) DEFAULT '' COMMENT ' 项目合同编码  ',
  `contract_status` char(4) DEFAULT '' COMMENT '合同制作状态',
  `publish_date` datetime DEFAULT NULL COMMENT ' 项目发布时间  ',
  `invest_start_date` datetime DEFAULT NULL COMMENT ' 项目开始投资时间  ',
  `invest_end_date` datetime DEFAULT NULL COMMENT ' 项目结束投资时间  ',
  `settle_terms` varchar(64) DEFAULT '' COMMENT ' 满标条件  ',
  `settle_terms_amount` decimal(20,2) DEFAULT '0.00' COMMENT ' 满标金额（精度到分，单位：元）  ',
  `settle_date` datetime DEFAULT NULL COMMENT ' 项目成立时间  ',
  `settle_amout` decimal(20,2) DEFAULT '0.00' COMMENT ' 项目实际融资金额（精度到分，单位：元）  ',
  `effective_date` datetime DEFAULT NULL COMMENT ' 项目生效时间  ',
  `due_date` datetime DEFAULT NULL COMMENT ' 项目预计还款时间  ',
  `due_amount` decimal(20,2) DEFAULT '0.00' COMMENT ' 项目还款金额（精度到分，单位：元）  ',
  `fundraise_trade_id` bigint(20) DEFAULT '0' COMMENT ' 募集交易ID  ',
  `repay_trade_id` bigint(20) DEFAULT '0' COMMENT ' 还款交易ID  ',
  `fundraise_fee_trade_id` bigint(20) DEFAULT '0' COMMENT ' 募集分佣父交易  ',
  `weight` int(2) DEFAULT NULL COMMENT '权重',
  `reachable` tinyint(1) DEFAULT NULL COMMENT '是否可见',
  `create_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `deleted` bit(1) DEFAULT b'0',
  `version` int(11) DEFAULT '1',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of general_project
-- ----------------------------

-- ----------------------------
-- Table structure for `guarantee`
-- ----------------------------
DROP TABLE IF EXISTS `guarantee`;
CREATE TABLE `guarantee` (
  `id` bigint(20) NOT NULL DEFAULT '0' COMMENT '担保函流水号',
  `project_id` bigint(20) DEFAULT '0' COMMENT ' 担保函对应的项目ID  ',
  `fee` decimal(20,2) DEFAULT '0.00' COMMENT ' 应收担保费（精度到分，单位：元）  ',
  `status` varchar(8) DEFAULT '' COMMENT ' 担保函状态  ',
  `year` int(11) DEFAULT '0' COMMENT ' 担保函年份  ',
  `serial` int(11) DEFAULT '0' COMMENT ' 担保函顺序号  ',
  `user_id` bigint(20) DEFAULT '0' COMMENT ' 担保公司用户ID  ',
  `commitment_letter_sn` varchar(64) DEFAULT '' COMMENT ' 担保承诺函编号  ',
  `commitment_letter_path` varchar(256) DEFAULT '' COMMENT ' 担保承诺函保存路径  ',
  `guarantee_contract_sn` varchar(64) DEFAULT '' COMMENT ' 履约回购合同编号（与项目中的编号一致）  ',
  `guarantee_contract_path` varchar(256) DEFAULT '' COMMENT ' 履约回购合同保存路径  ',
  `guarantee_letter_sn` varchar(64) DEFAULT '' COMMENT ' 担保函编号  ',
  `guarantee_letter_path` varchar(256) DEFAULT '' COMMENT ' 担保函保存路径  ',
  `create_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `deleted` bit(1) NOT NULL DEFAULT b'0',
  `version` int(11) NOT NULL DEFAULT '1',
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of guarantee
-- ----------------------------

-- ----------------------------
-- Table structure for `investment`
-- ----------------------------
DROP TABLE IF EXISTS `investment`;
CREATE TABLE `investment` (
  `id` bigint(20) NOT NULL DEFAULT '0' COMMENT '统一的ID编码规则',
  `inverstor_id` bigint(20) DEFAULT '0' COMMENT ' 投资人ID',
  `project_id` bigint(20) DEFAULT '0' COMMENT ' 项目ID  ',
  `loanee_id` bigint(20) DEFAULT '0' COMMENT ' 借款人ID',
  `trade_id` bigint(20) DEFAULT '0' COMMENT ' 交易ID',
  `balance` decimal(20,2) DEFAULT '0.00' COMMENT ' 投资金额（精度到分，单位：元）',
  `balance_due` decimal(20,2) DEFAULT '0.00' COMMENT ' 预期收益金额（精度到分，单位：元）',
  `invest_no` varchar(32) DEFAULT '' COMMENT ' 投资流水号  ',
  `invest_acc_no` varchar(32) DEFAULT '' COMMENT ' 借款流水号 ',
  `invest_path` varchar(256) DEFAULT '' COMMENT '投资凭证地址',
  `status` varchar(8) DEFAULT '' COMMENT ' 投资状态',
  `issue_date` datetime DEFAULT NULL COMMENT ' 预投资时间 ',
  `settled_date` datetime DEFAULT NULL COMMENT ' 投资生效时间 ',
  `due_date` datetime DEFAULT NULL COMMENT ' 预计还款时间 ',
  `create_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `deleted` bit(1) NOT NULL DEFAULT b'0',
  `version` int(11) NOT NULL DEFAULT '1',
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of investment
-- ----------------------------

-- ----------------------------
-- Table structure for `permission`
-- ----------------------------
DROP TABLE IF EXISTS `permission`;
CREATE TABLE `permission` (
  `id` bigint(20) NOT NULL DEFAULT '0',
  `name` varchar(30) DEFAULT NULL,
  `name_zh` varchar(30) DEFAULT NULL,
  `parent_id` bigint(20) DEFAULT NULL,
  `create_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `deleted` bit(1) DEFAULT b'0',
  `version` int(11) DEFAULT '1',
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of permission
-- ----------------------------
INSERT INTO `permission` VALUES ('1', '*:*', '资源', '0', '2015-03-17 11:19:54', '', '1');
INSERT INTO `permission` VALUES ('11', 'organization:*', '组织机构管理', '1', '2015-03-17 11:19:54', '', '1');
INSERT INTO `permission` VALUES ('12', 'organization:create', '组织机构新增', '11', '2015-03-17 11:19:54', '', '1');
INSERT INTO `permission` VALUES ('13', 'organization:update', '组织机构修改', '11', '2015-03-17 11:19:54', '', '1');
INSERT INTO `permission` VALUES ('14', 'organization:delete', '组织机构删除', '11', '2015-03-17 11:19:54', '', '1');
INSERT INTO `permission` VALUES ('15', 'organization:view', '组织机构查看', '11', '2015-03-17 11:19:54', '', '1');
INSERT INTO `permission` VALUES ('21', 'user:*', '用户管理', '1', '2015-03-17 11:19:54', '', '1');
INSERT INTO `permission` VALUES ('22', 'user:create', '用户新增', '21', '2015-03-17 11:19:54', '', '1');
INSERT INTO `permission` VALUES ('23', 'user:update', '用户修改', '21', '2015-03-17 11:19:54', '', '1');
INSERT INTO `permission` VALUES ('24', 'user:delete', '用户删除', '21', '2015-03-17 11:19:54', '', '1');
INSERT INTO `permission` VALUES ('25', 'user:view', '用户查看', '21', '2015-03-17 11:19:54', '', '1');
INSERT INTO `permission` VALUES ('31', 'resource:*', '资源管理', '1', '2015-03-17 11:19:54', '', '1');
INSERT INTO `permission` VALUES ('32', 'resource:create', '资源新增', '31', '2015-03-17 11:19:54', '', '1');
INSERT INTO `permission` VALUES ('33', 'resource:update', '资源修改', '31', '2015-03-17 11:19:54', '', '1');
INSERT INTO `permission` VALUES ('34', 'resource:delete', '资源删除', '31', '2015-03-17 11:19:54', '', '1');
INSERT INTO `permission` VALUES ('35', 'resource:view', '资源查看', '31', '2015-03-17 11:19:54', '', '1');
INSERT INTO `permission` VALUES ('41', 'role:*', '角色管理', '1', '2015-03-17 11:19:54', '', '1');
INSERT INTO `permission` VALUES ('42', 'role:create', '角色新增', '41', '2015-03-17 11:19:54', '', '1');
INSERT INTO `permission` VALUES ('43', 'role:update', '角色修改', '41', '2015-03-17 11:19:54', '', '1');
INSERT INTO `permission` VALUES ('44', 'role:delete', '角色删除', '41', '2015-03-17 11:19:54', '', '1');
INSERT INTO `permission` VALUES ('45', 'role:view', '角色查看', '41', '2015-03-17 11:19:54', '', '1');
INSERT INTO `permission` VALUES ('61', 'product:*', '产品管理', '1', '2015-03-17 11:19:54', '', '1');
INSERT INTO `permission` VALUES ('64', 'product:audit', '产品发布审核', '61', '2015-03-17 11:19:54', '', '1');
INSERT INTO `permission` VALUES ('110', 'myAccount:*', '账户管理', '1', '2015-03-17 11:19:54', '', '1');
INSERT INTO `permission` VALUES ('111', 'myAccount:home', '账户首页', '110', '2015-03-17 11:19:54', '', '1');
INSERT INTO `permission` VALUES ('112', 'myAccount:netIn', '网银划入', '110', '2015-03-17 11:19:54', '', '1');
INSERT INTO `permission` VALUES ('113', 'myAccount:quickIn', '快捷划入', '110', '2015-03-17 11:19:54', '', '1');
INSERT INTO `permission` VALUES ('114', 'myAccount:out', '划出', '110', '2015-03-17 11:19:54', '', '1');
INSERT INTO `permission` VALUES ('115', 'myAccount:info', '账户资料', '110', '2015-03-17 11:19:54', '', '1');
INSERT INTO `permission` VALUES ('116', 'myAccount:invest', '投资按钮', '110', '2015-03-17 11:19:54', '', '1');
INSERT INTO `permission` VALUES ('120', 'myTrade:*', '交易查询', '1', '2015-03-17 11:19:54', '', '1');
INSERT INTO `permission` VALUES ('121', 'myTrade:invest', '投资记录', '120', '2015-03-17 11:19:54', '', '1');
INSERT INTO `permission` VALUES ('122', 'myTrade:in', '划入记录', '120', '2015-03-17 11:19:54', '', '1');
INSERT INTO `permission` VALUES ('123', 'myTrade:out', '划出记录', '120', '2015-03-17 11:19:54', '', '1');
INSERT INTO `permission` VALUES ('124', 'myTrade:accept', '投资接受记录', '120', '2015-03-17 11:19:54', '', '1');
INSERT INTO `permission` VALUES ('130', 'myLetter:*', '担保函管理', '1', '2015-03-17 11:19:54', '', '1');
INSERT INTO `permission` VALUES ('131', 'myLetter:agency', '担保机构管理', '130', '2015-03-17 11:19:54', '', '1');
INSERT INTO `permission` VALUES ('132', 'myLetter:fee', '担保费管理', '130', '2015-03-17 11:19:54', '', '1');
INSERT INTO `permission` VALUES ('140', 'myInvestor:*', '客户管理', '1', '2015-03-17 11:19:54', '', '1');
INSERT INTO `permission` VALUES ('141', 'myInvestor:create', '投资人开户', '140', '2015-03-17 11:19:54', '', '1');
INSERT INTO `permission` VALUES ('142', 'myInvestor:view', '投资人管理', '140', '2015-03-17 11:19:54', '', '1');
INSERT INTO `permission` VALUES ('150', 'myBiz:*', '业务管理', '1', '2015-03-17 11:19:54', '', '1');
INSERT INTO `permission` VALUES ('151', 'myBiz:view', '业务记录', '150', '2015-03-17 11:19:54', '', '1');
INSERT INTO `permission` VALUES ('152', 'myBiz:stats', '业务统计', '150', '2015-03-17 11:19:54', '', '1');
INSERT INTO `permission` VALUES ('160', 'myBroker:*', '经纪人管理', '1', '2015-03-17 11:19:54', '', '1');
INSERT INTO `permission` VALUES ('161', 'myBroker:view', '经纪人管理', '160', '2015-03-17 11:19:54', '', '1');
INSERT INTO `permission` VALUES ('162', 'myBroker:create', '经纪人开户', '160', '2015-03-17 11:19:54', '', '1');
INSERT INTO `permission` VALUES ('170', 'myMarket:*', '营销管理', '1', '2015-03-17 11:19:54', '', '1');
INSERT INTO `permission` VALUES ('171', 'myMarket:view', '业务记录', '170', '2015-03-17 11:19:54', '', '1');
INSERT INTO `permission` VALUES ('172', 'myMarket:stats', '业务统计', '170', '2015-03-17 11:19:54', '', '1');
INSERT INTO `permission` VALUES ('180', 'mySecurity:*', '安全中心', '1', '2015-03-17 11:19:54', '', '1');
INSERT INTO `permission` VALUES ('190', 'myConsume:*', '消费中心', '1', '2015-03-17 11:19:54', '', '1');
INSERT INTO `permission` VALUES ('200', 'myActivity:*', '活动中心', '1', '2015-03-17 11:19:54', '', '1');
INSERT INTO `permission` VALUES ('210', 'myOperator:*', '操作员管理', '1', '2015-03-17 11:19:54', '', '1');

-- ----------------------------
-- Table structure for `project`
-- ----------------------------
DROP TABLE IF EXISTS `project`;
CREATE TABLE `project` (
  `id` bigint(20) NOT NULL DEFAULT '0' COMMENT '项目流水号',
  `name` varchar(128) DEFAULT '' COMMENT ' 项目名称  ',
  `total_amount` decimal(20,2) DEFAULT '0.00' COMMENT ' 融资金额（精度到分，单位：元）  ',
  `interest_rate` decimal(6,4) DEFAULT '0.0000' COMMENT ' 投资收益率（年化）  ',
  `period_type` varchar(16) DEFAULT '' COMMENT ' 借款期限类型  ',
  `period` varchar(16) DEFAULT '' COMMENT ' 借款期限（与类型合并可计算出具体周期）  ',
  `period_days` int(10) DEFAULT '0' COMMENT '借款天数-通过计算获取',
  `minimal_investment` decimal(20,2) DEFAULT '0.00' COMMENT ' 最低投资额（精度到分，单位：元）  ',
  `funding_template_id` varchar(32) DEFAULT '' COMMENT ' 分佣模板ID  ',
  `repay_template_id` varchar(32) DEFAULT '' COMMENT ' 分润模板ID  ',
  `loanee_id` bigint(20) DEFAULT '0' COMMENT ' 借款人ID  ',
  `loanee_user_name` varchar(64) DEFAULT '' COMMENT ' 借款人登录名',
  `purpose` varchar(1024) DEFAULT '' COMMENT ' 借款目的  ',
  `sponsor_user_id` bigint(20) DEFAULT '0' COMMENT ' 保荐机构用户ID  ',
  `sponsor_memo` varchar(1024) DEFAULT '' COMMENT ' 保荐机构推荐语  ',
  `type` varchar(16) DEFAULT '' COMMENT ' 项目类型（担保贷还是接力贷等）  ',
  `status` varchar(8) DEFAULT '' COMMENT ' 项目状态  ',
  `progress_amount` decimal(20,2) DEFAULT '0.00' COMMENT ' 已投资金额（精度到分，单位：元）  ',
  `progress` int(11) DEFAULT '0' COMMENT ' 投资百分比（百分数）  ',
  `total_investor` int(11) DEFAULT '0' COMMENT ' 投资总人数  ',
  `guarantee_letter_id` bigint(20) DEFAULT '0' COMMENT ' 担保函ID  ',
  `guarantee_user_id` varchar(32) DEFAULT '' COMMENT '担保公司ID',
  `contract_no` varchar(128) DEFAULT '' COMMENT ' 项目合同编码  ',
  `contract_status` char(4) DEFAULT '' COMMENT '合同制作状态',
  `publish_date` datetime DEFAULT NULL COMMENT ' 项目发布时间  ',
  `invest_start_date` datetime DEFAULT NULL COMMENT ' 项目开始投资时间  ',
  `invest_end_date` datetime DEFAULT NULL COMMENT ' 项目结束投资时间  ',
  `settle_terms` varchar(64) DEFAULT '' COMMENT ' 满标条件  ',
  `settle_terms_amount` decimal(20,2) DEFAULT '0.00' COMMENT ' 满标金额（精度到分，单位：元）  ',
  `settle_date` datetime DEFAULT NULL COMMENT ' 项目成立时间  ',
  `settle_amout` decimal(20,2) DEFAULT '0.00' COMMENT ' 项目实际融资金额（精度到分，单位：元）  ',
  `effective_date` datetime DEFAULT NULL COMMENT ' 项目生效时间  ',
  `due_date` datetime DEFAULT NULL COMMENT ' 项目预计还款时间  ',
  `due_amount` decimal(20,2) DEFAULT '0.00' COMMENT ' 项目还款金额（精度到分，单位：元）  ',
  `fundraise_trade_id` bigint(20) DEFAULT '0' COMMENT ' 募集交易ID  ',
  `repay_trade_id` bigint(20) DEFAULT '0' COMMENT ' 还款交易ID  ',
  `fundraise_fee_trade_id` bigint(20) DEFAULT '0' COMMENT ' 募集分佣父交易  ',
  `create_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `deleted` bit(1) NOT NULL DEFAULT b'0',
  `version` int(11) NOT NULL DEFAULT '1',
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of project
-- ----------------------------

-- ----------------------------
-- Table structure for `project_review`
-- ----------------------------
DROP TABLE IF EXISTS `project_review`;
CREATE TABLE `project_review` (
  `id` bigint(20) NOT NULL DEFAULT '0' COMMENT '统一的ID编码规则',
  `project_id` bigint(20) DEFAULT '0' COMMENT '项目ID',
  `reviewer_id` bigint(20) DEFAULT '0' COMMENT '审核者ID',
  `review_code` varchar(16) DEFAULT '' COMMENT '审核代码',
  `result` varchar(16) DEFAULT '' COMMENT '结果',
  `reason` varchar(256) DEFAULT '' COMMENT '原因',
  `reviewer_user_name` varchar(32) DEFAULT '' COMMENT '审核者用户名',
  `reviewer_name` varchar(32) DEFAULT '' COMMENT '审核者真实姓名',
  `review_name` varchar(32) DEFAULT '' COMMENT '审核操作名称',
  `create_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `deleted` bit(1) NOT NULL DEFAULT b'0',
  `version` int(11) NOT NULL DEFAULT '1',
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of project_review
-- ----------------------------

-- ----------------------------
-- Table structure for `qdd_account_log`
-- ----------------------------
DROP TABLE IF EXISTS `qdd_account_log`;
CREATE TABLE `qdd_account_log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `trade_id` bigint(20) DEFAULT NULL,
  `order_no` varchar(20) DEFAULT NULL,
  `platform_id` varchar(10) DEFAULT NULL,
  `money_more_more_id` varchar(10) DEFAULT NULL,
  `amount` decimal(20,2) NOT NULL DEFAULT '0.00',
  `other_money_more_more_id` varchar(10) DEFAULT NULL,
  `bank_code` varchar(20) DEFAULT NULL,
  `card_no` varchar(30) DEFAULT NULL,
  `fee` decimal(20,2) NOT NULL DEFAULT '0.00',
  `fee_money_more_more_id` varchar(10) DEFAULT NULL,
  `memo` varchar(50) DEFAULT NULL,
  `audit_pass` int(11) DEFAULT '0',
  `update_at` timestamp NULL DEFAULT NULL,
  `loan_no` varchar(30) DEFAULT NULL,
  `result_code` varchar(10) DEFAULT NULL,
  `message` varchar(30) DEFAULT NULL,
  `callback` varchar(2038) DEFAULT NULL,
  `orderquery` varchar(2038) DEFAULT NULL,
  `create_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `deleted` bit(1) DEFAULT b'0',
  `version` int(11) DEFAULT '1',
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=150311175526030002 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of qdd_account_log
-- ----------------------------

-- ----------------------------
-- Table structure for `repayment`
-- ----------------------------
DROP TABLE IF EXISTS `repayment`;
CREATE TABLE `repayment` (
  `id` bigint(20) NOT NULL DEFAULT '0' COMMENT '统一的ID编码规则',
  `loanee_id` bigint(20) DEFAULT '0' COMMENT ' 借款人ID  ',
  `project_id` bigint(20) DEFAULT '0' COMMENT ' 项目ID  ',
  `trade_id` bigint(20) DEFAULT '0' COMMENT ' 交易ID',
  `balance` decimal(20,2) DEFAULT '0.00' COMMENT '  还款金额（精度到分，单位：元）  ',
  `status` varchar(8) DEFAULT '' COMMENT ' 还款状态  ',
  `issue_date` datetime DEFAULT NULL COMMENT ' 预还款时间  ',
  `repay_date` datetime DEFAULT NULL COMMENT ' 还款成功时间  ',
  `create_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `deleted` bit(1) NOT NULL DEFAULT b'0',
  `version` int(11) NOT NULL DEFAULT '1',
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of repayment
-- ----------------------------

-- ----------------------------
-- Table structure for `role`
-- ----------------------------
DROP TABLE IF EXISTS `role`;
CREATE TABLE `role` (
  `id` bigint(20) NOT NULL DEFAULT '0',
  `name` varchar(30) DEFAULT NULL,
  `name_en` varchar(30) DEFAULT NULL,
  `name_zh` varchar(30) DEFAULT NULL,
  `is_front_role` bit(1) DEFAULT b'0',
  `is_admin_role` bit(1) DEFAULT b'0',
  `permission_ids` varchar(255) DEFAULT NULL,
  `create_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `deleted` bit(1) DEFAULT b'0',
  `version` int(11) DEFAULT '1',
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of role
-- ----------------------------
INSERT INTO `role` VALUES ('1', 'AA', 'Super Admin', '超级管理员', '', '', '1', '2015-03-17 11:19:58', '', '1');
INSERT INTO `role` VALUES ('2', 'I', 'Investor', '投资人', '', '', '116,121', '2015-03-17 11:19:58', '', '1');
INSERT INTO `role` VALUES ('3', 'BR', 'Broker', '经纪人', '', '', '140,150', '2015-03-17 11:19:58', '', '1');
INSERT INTO `role` VALUES ('4', 'B', 'Borrower', '融资人', '', '', '124', '2015-03-17 11:19:58', '', '1');
INSERT INTO `role` VALUES ('5', 'SP', 'Sponsor', '保荐人', '', '', '', '2015-03-17 11:19:58', '', '1');
INSERT INTO `role` VALUES ('6', 'AG', 'Agency', '营销机构', '', '', '160,170', '2015-03-17 11:19:58', '', '1');
INSERT INTO `role` VALUES ('7', 'G', 'Guarantee', '担保机构', '', '', '130,210', '2015-03-17 11:19:58', '', '1');
INSERT INTO `role` VALUES ('8', 'IE', 'Investment Enterprise', '投资机构', '', '', '', '2015-03-17 11:19:58', '', '1');
INSERT INTO `role` VALUES ('9', 'SE', 'Sponsor Enterprise', '保荐机构', '', '', '', '2015-03-17 11:19:58', '', '1');
INSERT INTO `role` VALUES ('10', 'O', 'Operator', '操作员', '', '', '130', '2015-03-17 11:19:58', '', '1');
INSERT INTO `role` VALUES ('11', 'NO', 'Normal Operator', '普通运营账户', '', '', '', '2015-03-17 11:19:58', '', '1');
INSERT INTO `role` VALUES ('12', 'AO', 'Audit Operator', '运营审核账户', '', '', '64', '2015-03-17 11:19:58', '', '1');
INSERT INTO `role` VALUES ('13', 'A', 'Admin', '管理员', '', '', '1', '2015-03-17 11:19:58', '', '1');
INSERT INTO `role` VALUES ('14', 'P', 'Platform', '平台分润角色', '', '', '', '2015-03-17 11:19:58', '', '1');
INSERT INTO `role` VALUES ('15', 'L', 'Guarantee xd', '小贷机构', '', '', '130,210', '2015-03-17 11:19:58', '', '1');
INSERT INTO `role` VALUES ('16', 'S', 'Super Broker', '超级经纪人', '', '', '140,150,160,170', '2015-03-17 11:19:58', '', '1');

-- ----------------------------
-- Table structure for `state_city`
-- ----------------------------
DROP TABLE IF EXISTS `state_city`;
CREATE TABLE `state_city` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(50) DEFAULT NULL,
  `parent_name` varchar(50) DEFAULT NULL,
  `code` varchar(10) DEFAULT NULL,
  `parent_code` varchar(10) DEFAULT NULL,
  `qdd_code` varchar(10) DEFAULT NULL,
  `qdd_parent` varchar(10) DEFAULT NULL,
  `create_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `deleted` bit(1) DEFAULT b'0',
  `version` int(11) DEFAULT '1',
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=745 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of state_city
-- ----------------------------
INSERT INTO `state_city` VALUES ('373', '北京市', null, '110000', null, '1', '0', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('374', '天津市', null, '120000', null, '2', '0', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('375', '河北省', null, '130000', null, '3', '0', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('376', '山西省', null, '140000', null, '4', '0', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('377', '内蒙古自治区', null, '150000', null, '5', '0', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('378', '辽宁省', null, '210000', null, '6', '0', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('379', '吉林省', null, '220000', null, '7', '0', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('380', '黑龙江省', null, '230000', null, '8', '0', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('381', '上海市', null, '310000', null, '9', '0', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('382', '江苏省', null, '320000', null, '10', '0', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('383', '浙江省', null, '330000', null, '11', '0', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('384', '安徽省', null, '340000', null, '12', '0', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('385', '福建省', null, '350000', null, '13', '0', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('386', '江西省', null, '360000', null, '14', '0', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('387', '山东省', null, '370000', null, '15', '0', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('388', '河南省', null, '410000', null, '16', '0', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('389', '湖北省', null, '420000', null, '17', '0', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('390', '湖南省', null, '430000', null, '18', '0', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('391', '广东省', null, '440000', null, '19', '0', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('392', '广西壮族自治区', null, '450000', null, '20', '0', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('393', '海南省', null, '460000', null, '21', '0', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('394', '重庆市', null, '500000', null, '22', '0', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('395', '四川省', null, '510000', null, '23', '0', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('396', '贵州省', null, '520000', null, '24', '0', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('397', '云南省', null, '530000', null, '25', '0', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('398', '西藏自治区', null, '540000', null, '26', '0', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('399', '陕西省', null, '610000', null, '27', '0', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('400', '甘肃省', null, '620000', null, '28', '0', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('401', '青海省', null, '630000', null, '29', '0', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('402', '宁夏回族自治区', null, '640000', null, '30', '0', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('403', '新疆维吾尔自治区', null, '650000', null, '31', '0', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('404', '北京市', '北京市', '110100', '110000', '1001', '1', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('405', '天津市', '天津市', '120100', '120000', '1002', '2', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('406', '石家庄市', '河北省', '130100', '130000', '1003', '3', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('407', '唐山市', '河北省', '130200', '130000', '1004', '3', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('408', '秦皇岛市', '河北省', '130300', '130000', '1005', '3', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('409', '邯郸市', '河北省', '130400', '130000', '1006', '3', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('410', '邢台市', '河北省', '130500', '130000', '1007', '3', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('411', '保定市', '河北省', '130600', '130000', '1008', '3', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('412', '张家口市', '河北省', '130700', '130000', '1009', '3', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('413', '承德市', '河北省', '130800', '130000', '1010', '3', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('414', '沧州市', '河北省', '130900', '130000', '1011', '3', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('415', '廊坊市', '河北省', '131000', '130000', '1012', '3', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('416', '衡水市', '河北省', '131100', '130000', '1013', '3', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('417', '太原市', '山西省', '140100', '140000', '1014', '4', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('418', '大同市', '山西省', '140200', '140000', '1015', '4', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('419', '阳泉市', '山西省', '140300', '140000', '1016', '4', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('420', '长治市', '山西省', '140400', '140000', '1017', '4', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('421', '晋城市', '山西省', '140500', '140000', '1018', '4', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('422', '朔州市', '山西省', '140600', '140000', '1019', '4', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('423', '晋中市', '山西省', '140700', '140000', '1020', '4', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('424', '运城市', '山西省', '140800', '140000', '1021', '4', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('425', '忻州市', '山西省', '140900', '140000', '1022', '4', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('426', '临汾市', '山西省', '141000', '140000', '1023', '4', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('427', '吕梁市', '山西省', '141100', '140000', '1024', '4', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('428', '呼和浩特市', '内蒙古自治区', '150100', '150000', '1025', '5', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('429', '包头市', '内蒙古自治区', '150200', '150000', '1026', '5', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('430', '乌海市', '内蒙古自治区', '150300', '150000', '1027', '5', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('431', '赤峰市', '内蒙古自治区', '150400', '150000', '1028', '5', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('432', '通辽市', '内蒙古自治区', '150500', '150000', '1029', '5', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('433', '鄂尔多斯市', '内蒙古自治区', '150600', '150000', '1030', '5', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('434', '呼伦贝尔市', '内蒙古自治区', '150700', '150000', '1031', '5', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('435', '巴彦淖尔市', '内蒙古自治区', '150800', '150000', '1032', '5', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('436', '乌兰察布市', '内蒙古自治区', '150900', '150000', '1033', '5', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('437', '兴安盟', '内蒙古自治区', '152200', '150000', '1034', '5', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('438', '锡林郭勒盟', '内蒙古自治区', '152500', '150000', '1035', '5', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('439', '阿拉善盟', '内蒙古自治区', '152900', '150000', '1036', '5', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('440', '沈阳市', '辽宁省', '210100', '210000', '1037', '6', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('441', '大连市', '辽宁省', '210200', '210000', '1038', '6', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('442', '鞍山市', '辽宁省', '210300', '210000', '1039', '6', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('443', '抚顺市', '辽宁省', '210400', '210000', '1040', '6', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('444', '本溪市', '辽宁省', '210500', '210000', '1041', '6', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('445', '丹东市', '辽宁省', '210600', '210000', '1042', '6', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('446', '锦州市', '辽宁省', '210700', '210000', '1043', '6', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('447', '营口市', '辽宁省', '210800', '210000', '1044', '6', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('448', '阜新市', '辽宁省', '210900', '210000', '1045', '6', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('449', '辽阳市', '辽宁省', '211000', '210000', '1046', '6', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('450', '盘锦市', '辽宁省', '211100', '210000', '1047', '6', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('451', '铁岭市', '辽宁省', '211200', '210000', '1048', '6', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('452', '朝阳市', '辽宁省', '211300', '210000', '1049', '6', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('453', '葫芦岛市', '辽宁省', '211400', '210000', '1050', '6', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('454', '长春市', '吉林省', '220100', '220000', '1051', '7', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('455', '吉林市', '吉林省', '220200', '220000', '1052', '7', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('456', '四平市', '吉林省', '220300', '220000', '1053', '7', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('457', '辽源市', '吉林省', '220400', '220000', '1054', '7', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('458', '通化市', '吉林省', '220500', '220000', '1055', '7', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('459', '白山市', '吉林省', '220600', '220000', '1056', '7', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('460', '松原市', '吉林省', '220700', '220000', '1057', '7', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('461', '白城市', '吉林省', '220800', '220000', '1058', '7', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('462', '延边朝鲜族自治州', '吉林省', '222400', '220000', '1059', '7', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('463', '哈尔滨市', '黑龙江省', '230100', '230000', '1060', '8', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('464', '齐齐哈尔市', '黑龙江省', '230200', '230000', '1061', '8', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('465', '鸡西市', '黑龙江省', '230300', '230000', '1062', '8', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('466', '鹤岗市', '黑龙江省', '230400', '230000', '1063', '8', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('467', '双鸭山市', '黑龙江省', '230500', '230000', '1064', '8', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('468', '大庆市', '黑龙江省', '230600', '230000', '1065', '8', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('469', '伊春市', '黑龙江省', '230700', '230000', '1066', '8', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('470', '佳木斯市', '黑龙江省', '230800', '230000', '1067', '8', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('471', '七台河市', '黑龙江省', '230900', '230000', '1068', '8', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('472', '牡丹江市', '黑龙江省', '231000', '230000', '1069', '8', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('473', '黑河市', '黑龙江省', '231100', '230000', '1070', '8', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('474', '绥化市', '黑龙江省', '231200', '230000', '1071', '8', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('475', '大兴安岭地区', '黑龙江省', '232700', '230000', '1072', '8', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('476', '上海市', '上海市', '310100', '310000', '1073', '9', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('477', '南京市', '江苏省', '320100', '320000', '1074', '10', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('478', '无锡市', '江苏省', '320200', '320000', '1075', '10', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('479', '徐州市', '江苏省', '320300', '320000', '1076', '10', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('480', '常州市', '江苏省', '320400', '320000', '1077', '10', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('481', '苏州市', '江苏省', '320500', '320000', '1078', '10', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('482', '南通市', '江苏省', '320600', '320000', '1079', '10', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('483', '连云港市', '江苏省', '320700', '320000', '1080', '10', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('484', '淮安市', '江苏省', '320800', '320000', '1081', '10', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('485', '盐城市', '江苏省', '320900', '320000', '1082', '10', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('486', '扬州市', '江苏省', '321000', '320000', '1083', '10', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('487', '镇江市', '江苏省', '321100', '320000', '1084', '10', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('488', '泰州市', '江苏省', '321200', '320000', '1085', '10', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('489', '宿迁市', '江苏省', '321300', '320000', '1086', '10', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('490', '杭州市', '浙江省', '330100', '330000', '1087', '11', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('491', '宁波市', '浙江省', '330200', '330000', '1088', '11', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('492', '温州市', '浙江省', '330300', '330000', '1089', '11', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('493', '嘉兴市', '浙江省', '330400', '330000', '1090', '11', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('494', '湖州市', '浙江省', '330500', '330000', '1091', '11', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('495', '绍兴市', '浙江省', '330600', '330000', '1092', '11', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('496', '金华市', '浙江省', '330700', '330000', '1093', '11', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('497', '衢州市', '浙江省', '330800', '330000', '1094', '11', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('498', '舟山市', '浙江省', '330900', '330000', '1095', '11', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('499', '台州市', '浙江省', '331000', '330000', '1096', '11', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('500', '丽水市', '浙江省', '331100', '330000', '1097', '11', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('501', '合肥市', '安徽省', '340100', '340000', '1098', '12', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('502', '芜湖市', '安徽省', '340200', '340000', '1099', '12', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('503', '蚌埠市', '安徽省', '340300', '340000', '1100', '12', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('504', '淮南市', '安徽省', '340400', '340000', '1101', '12', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('505', '马鞍山市', '安徽省', '340500', '340000', '1102', '12', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('506', '淮北市', '安徽省', '340600', '340000', '1103', '12', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('507', '铜陵市', '安徽省', '340700', '340000', '1104', '12', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('508', '安庆市', '安徽省', '340800', '340000', '1105', '12', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('509', '黄山市', '安徽省', '341000', '340000', '1106', '12', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('510', '滁州市', '安徽省', '341100', '340000', '1107', '12', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('511', '阜阳市', '安徽省', '341200', '340000', '1108', '12', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('512', '宿州市', '安徽省', '341300', '340000', '1109', '12', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('513', '巢湖市', '安徽省', '341400', '340000', '1110', '12', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('514', '六安市', '安徽省', '341500', '340000', '1111', '12', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('515', '亳州市', '安徽省', '341600', '340000', '1112', '12', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('516', '池州市', '安徽省', '341700', '340000', '1113', '12', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('517', '宣城市', '安徽省', '341800', '340000', '1114', '12', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('518', '福州市', '福建省', '350100', '350000', '1115', '13', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('519', '厦门市', '福建省', '350200', '350000', '1116', '13', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('520', '莆田市', '福建省', '350300', '350000', '1117', '13', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('521', '三明市', '福建省', '350400', '350000', '1118', '13', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('522', '泉州市', '福建省', '350500', '350000', '1119', '13', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('523', '漳州市', '福建省', '350600', '350000', '1120', '13', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('524', '南平市', '福建省', '350700', '350000', '1121', '13', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('525', '龙岩市', '福建省', '350800', '350000', '1122', '13', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('526', '宁德市', '福建省', '350900', '350000', '1123', '13', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('527', '南昌市', '江西省', '360100', '360000', '1124', '14', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('528', '景德镇市', '江西省', '360200', '360000', '1125', '14', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('529', '萍乡市', '江西省', '360300', '360000', '1126', '14', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('530', '九江市', '江西省', '360400', '360000', '1127', '14', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('531', '新余市', '江西省', '360500', '360000', '1128', '14', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('532', '鹰潭市', '江西省', '360600', '360000', '1129', '14', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('533', '赣州市', '江西省', '360700', '360000', '1130', '14', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('534', '吉安市', '江西省', '360800', '360000', '1131', '14', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('535', '宜春市', '江西省', '360900', '360000', '1132', '14', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('536', '抚州市', '江西省', '361000', '360000', '1133', '14', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('537', '上饶市', '江西省', '361100', '360000', '1134', '14', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('538', '济南市', '山东省', '370100', '370000', '1135', '15', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('539', '青岛市', '山东省', '370200', '370000', '1136', '15', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('540', '淄博市', '山东省', '370300', '370000', '1137', '15', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('541', '枣庄市', '山东省', '370400', '370000', '1138', '15', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('542', '东营市', '山东省', '370500', '370000', '1139', '15', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('543', '烟台市', '山东省', '370600', '370000', '1140', '15', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('544', '潍坊市', '山东省', '370700', '370000', '1141', '15', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('545', '济宁市', '山东省', '370800', '370000', '1142', '15', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('546', '泰安市', '山东省', '370900', '370000', '1143', '15', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('547', '威海市', '山东省', '371000', '370000', '1144', '15', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('548', '日照市', '山东省', '371100', '370000', '1145', '15', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('549', '莱芜市', '山东省', '371200', '370000', '1146', '15', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('550', '临沂市', '山东省', '371300', '370000', '1147', '15', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('551', '德州市', '山东省', '371400', '370000', '1148', '15', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('552', '聊城市', '山东省', '371500', '370000', '1149', '15', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('553', '滨州市', '山东省', '371600', '370000', '1150', '15', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('554', '荷泽市', '山东省', '371700', '370000', '1151', '15', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('555', '郑州市', '河南省', '410100', '410000', '1152', '16', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('556', '开封市', '河南省', '410200', '410000', '1153', '16', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('557', '洛阳市', '河南省', '410300', '410000', '1154', '16', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('558', '平顶山市', '河南省', '410400', '410000', '1155', '16', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('559', '安阳市', '河南省', '410500', '410000', '1156', '16', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('560', '鹤壁市', '河南省', '410600', '410000', '1157', '16', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('561', '新乡市', '河南省', '410700', '410000', '1158', '16', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('562', '焦作市', '河南省', '410800', '410000', '1159', '16', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('563', '濮阳市', '河南省', '410900', '410000', '1160', '16', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('564', '许昌市', '河南省', '411000', '410000', '1161', '16', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('565', '漯河市', '河南省', '411100', '410000', '1162', '16', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('566', '三门峡市', '河南省', '411200', '410000', '1163', '16', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('567', '南阳市', '河南省', '411300', '410000', '1164', '16', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('568', '商丘市', '河南省', '411400', '410000', '1165', '16', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('569', '信阳市', '河南省', '411500', '410000', '1166', '16', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('570', '周口市', '河南省', '411600', '410000', '1167', '16', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('571', '驻马店市', '河南省', '411700', '410000', '1168', '16', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('572', '武汉市', '湖北省', '420100', '420000', '1169', '17', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('573', '黄石市', '湖北省', '420200', '420000', '1170', '17', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('574', '十堰市', '湖北省', '420300', '420000', '1171', '17', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('575', '宜昌市', '湖北省', '420500', '420000', '1172', '17', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('576', '襄樊市', '湖北省', '420600', '420000', '1173', '17', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('577', '鄂州市', '湖北省', '420700', '420000', '1174', '17', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('578', '荆门市', '湖北省', '420800', '420000', '1175', '17', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('579', '孝感市', '湖北省', '420900', '420000', '1176', '17', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('580', '荆州市', '湖北省', '421000', '420000', '1177', '17', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('581', '黄冈市', '湖北省', '421100', '420000', '1178', '17', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('582', '咸宁市', '湖北省', '421200', '420000', '1179', '17', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('583', '随州市', '湖北省', '421300', '420000', '1180', '17', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('584', '恩施土家族苗族自治州', '湖北省', '422800', '420000', '1181', '17', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('585', '长沙市', '湖南省', '430100', '430000', '1183', '18', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('586', '株洲市', '湖南省', '430200', '430000', '1184', '18', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('587', '湘潭市', '湖南省', '430300', '430000', '1185', '18', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('588', '衡阳市', '湖南省', '430400', '430000', '1186', '18', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('589', '邵阳市', '湖南省', '430500', '430000', '1187', '18', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('590', '岳阳市', '湖南省', '430600', '430000', '1188', '18', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('591', '常德市', '湖南省', '430700', '430000', '1189', '18', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('592', '张家界市', '湖南省', '430800', '430000', '1190', '18', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('593', '益阳市', '湖南省', '430900', '430000', '1191', '18', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('594', '郴州市', '湖南省', '431000', '430000', '1192', '18', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('595', '永州市', '湖南省', '431100', '430000', '1193', '18', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('596', '怀化市', '湖南省', '431200', '430000', '1194', '18', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('597', '娄底市', '湖南省', '431300', '430000', '1195', '18', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('598', '湘西土家族苗族自治州', '湖南省', '433100', '430000', '1196', '18', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('599', '广州市', '广东省', '440100', '440000', '1197', '19', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('600', '韶关市', '广东省', '440200', '440000', '1198', '19', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('601', '深圳市', '广东省', '440300', '440000', '1199', '19', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('602', '珠海市', '广东省', '440400', '440000', '1200', '19', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('603', '汕头市', '广东省', '440500', '440000', '1201', '19', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('604', '佛山市', '广东省', '440600', '440000', '1202', '19', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('605', '江门市', '广东省', '440700', '440000', '1203', '19', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('606', '湛江市', '广东省', '440800', '440000', '1204', '19', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('607', '茂名市', '广东省', '440900', '440000', '1205', '19', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('608', '肇庆市', '广东省', '441200', '440000', '1206', '19', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('609', '惠州市', '广东省', '441300', '440000', '1207', '19', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('610', '梅州市', '广东省', '441400', '440000', '1208', '19', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('611', '汕尾市', '广东省', '441500', '440000', '1209', '19', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('612', '河源市', '广东省', '441600', '440000', '1210', '19', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('613', '阳江市', '广东省', '441700', '440000', '1211', '19', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('614', '清远市', '广东省', '441800', '440000', '1212', '19', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('615', '东莞市', '广东省', '441900', '440000', '1213', '19', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('616', '中山市', '广东省', '442000', '440000', '1214', '19', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('617', '潮州市', '广东省', '445100', '440000', '1215', '19', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('618', '揭阳市', '广东省', '445200', '440000', '1216', '19', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('619', '云浮市', '广东省', '445300', '440000', '1217', '19', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('620', '南宁市', '广西壮族自治区', '450100', '450000', '1218', '20', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('621', '柳州市', '广西壮族自治区', '450200', '450000', '1219', '20', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('622', '桂林市', '广西壮族自治区', '450300', '450000', '1220', '20', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('623', '梧州市', '广西壮族自治区', '450400', '450000', '1221', '20', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('624', '北海市', '广西壮族自治区', '450500', '450000', '1222', '20', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('625', '防城港市', '广西壮族自治区', '450600', '450000', '1223', '20', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('626', '钦州市', '广西壮族自治区', '450700', '450000', '1224', '20', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('627', '贵港市', '广西壮族自治区', '450800', '450000', '1225', '20', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('628', '玉林市', '广西壮族自治区', '450900', '450000', '1226', '20', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('629', '百色市', '广西壮族自治区', '451000', '450000', '1227', '20', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('630', '贺州市', '广西壮族自治区', '451100', '450000', '1228', '20', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('631', '河池市', '广西壮族自治区', '451200', '450000', '1229', '20', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('632', '来宾市', '广西壮族自治区', '451300', '450000', '1230', '20', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('633', '崇左市', '广西壮族自治区', '451400', '450000', '1231', '20', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('634', '海口市', '海南省', '460100', '460000', '1232', '21', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('635', '三亚市', '海南省', '460200', '460000', '1233', '21', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('636', '重庆市', '重庆市', '500300', '500000', '1234', '22', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('637', '成都市', '四川省', '510100', '510000', '1235', '23', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('638', '自贡市', '四川省', '510300', '510000', '1236', '23', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('639', '攀枝花市', '四川省', '510400', '510000', '1237', '23', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('640', '泸州市', '四川省', '510500', '510000', '1238', '23', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('641', '德阳市', '四川省', '510600', '510000', '1239', '23', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('642', '绵阳市', '四川省', '510700', '510000', '1240', '23', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('643', '广元市', '四川省', '510800', '510000', '1241', '23', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('644', '遂宁市', '四川省', '510900', '510000', '1242', '23', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('645', '内江市', '四川省', '511000', '510000', '1243', '23', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('646', '乐山市', '四川省', '511100', '510000', '1244', '23', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('647', '南充市', '四川省', '511300', '510000', '1245', '23', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('648', '眉山市', '四川省', '511400', '510000', '1246', '23', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('649', '宜宾市', '四川省', '511500', '510000', '1247', '23', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('650', '广安市', '四川省', '511600', '510000', '1248', '23', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('651', '达州市', '四川省', '511700', '510000', '1249', '23', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('652', '雅安市', '四川省', '511800', '510000', '1250', '23', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('653', '巴中市', '四川省', '511900', '510000', '1251', '23', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('654', '资阳市', '四川省', '512000', '510000', '1252', '23', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('655', '阿坝藏族羌族自治州', '四川省', '513200', '510000', '1253', '23', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('656', '甘孜藏族自治州', '四川省', '513300', '510000', '1254', '23', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('657', '凉山彝族自治州', '四川省', '513400', '510000', '1255', '23', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('658', '贵阳市', '贵州省', '520100', '520000', '1256', '24', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('659', '六盘水市', '贵州省', '520200', '520000', '1257', '24', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('660', '遵义市', '贵州省', '520300', '520000', '1258', '24', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('661', '安顺市', '贵州省', '520400', '520000', '1259', '24', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('662', '铜仁地区', '贵州省', '522200', '520000', '1260', '24', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('663', '黔西南布依族苗族自治州', '贵州省', '522300', '520000', '1261', '24', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('664', '毕节地区', '贵州省', '522400', '520000', '1262', '24', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('665', '黔东南苗族侗族自治州', '贵州省', '522600', '520000', '1263', '24', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('666', '黔南布依族苗族自治州', '贵州省', '522700', '520000', '1264', '24', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('667', '昆明市', '云南省', '530100', '530000', '1265', '25', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('668', '曲靖市', '云南省', '530300', '530000', '1266', '25', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('669', '玉溪市', '云南省', '530400', '530000', '1267', '25', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('670', '保山市', '云南省', '530500', '530000', '1268', '25', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('671', '昭通市', '云南省', '530600', '530000', '1269', '25', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('672', '丽江市', '云南省', '530700', '530000', '1270', '25', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('673', '思茅市', '云南省', '530800', '530000', '1271', '25', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('674', '临沧市', '云南省', '530900', '530000', '1272', '25', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('675', '楚雄彝族自治州', '云南省', '532300', '530000', '1273', '25', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('676', '红河哈尼族彝族自治州', '云南省', '532500', '530000', '1274', '25', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('677', '文山壮族苗族自治州', '云南省', '532600', '530000', '1275', '25', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('678', '西双版纳傣族自治州', '云南省', '532800', '530000', '1276', '25', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('679', '大理白族自治州', '云南省', '532900', '530000', '1277', '25', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('680', '德宏傣族景颇族自治州', '云南省', '533100', '530000', '1278', '25', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('681', '怒江傈僳族自治州', '云南省', '533300', '530000', '1279', '25', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('682', '迪庆藏族自治州', '云南省', '533400', '530000', '1280', '25', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('683', '拉萨市', '西藏自治区', '540100', '540000', '1281', '26', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('684', '昌都地区', '西藏自治区', '542100', '540000', '1282', '26', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('685', '山南地区', '西藏自治区', '542200', '540000', '1283', '26', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('686', '日喀则地区', '西藏自治区', '542300', '540000', '1284', '26', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('687', '那曲地区', '西藏自治区', '542400', '540000', '1285', '26', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('688', '阿里地区', '西藏自治区', '542500', '540000', '1286', '26', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('689', '林芝地区', '西藏自治区', '542600', '540000', '1287', '26', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('690', '西安市', '陕西省', '610100', '610000', '1288', '27', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('691', '铜川市', '陕西省', '610200', '610000', '1289', '27', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('692', '宝鸡市', '陕西省', '610300', '610000', '1290', '27', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('693', '咸阳市', '陕西省', '610400', '610000', '1291', '27', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('694', '渭南市', '陕西省', '610500', '610000', '1292', '27', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('695', '延安市', '陕西省', '610600', '610000', '1293', '27', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('696', '汉中市', '陕西省', '610700', '610000', '1294', '27', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('697', '榆林市', '陕西省', '610800', '610000', '1295', '27', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('698', '安康市', '陕西省', '610900', '610000', '1296', '27', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('699', '商洛市', '陕西省', '611000', '610000', '1297', '27', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('700', '兰州市', '甘肃省', '620100', '620000', '1298', '28', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('701', '嘉峪关市', '甘肃省', '620200', '620000', '1299', '28', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('702', '金昌市', '甘肃省', '620300', '620000', '1300', '28', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('703', '白银市', '甘肃省', '620400', '620000', '1301', '28', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('704', '天水市', '甘肃省', '620500', '620000', '1302', '28', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('705', '武威市', '甘肃省', '620600', '620000', '1303', '28', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('706', '张掖市', '甘肃省', '620700', '620000', '1304', '28', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('707', '平凉市', '甘肃省', '620800', '620000', '1305', '28', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('708', '酒泉市', '甘肃省', '620900', '620000', '1306', '28', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('709', '庆阳市', '甘肃省', '621000', '620000', '1307', '28', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('710', '定西市', '甘肃省', '621100', '620000', '1308', '28', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('711', '陇南市', '甘肃省', '621200', '620000', '1309', '28', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('712', '临夏回族自治州', '甘肃省', '622900', '620000', '1310', '28', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('713', '甘南藏族自治州', '甘肃省', '623000', '620000', '1311', '28', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('714', '西宁市', '青海省', '630100', '630000', '1312', '29', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('715', '海东地区', '青海省', '632100', '630000', '1313', '29', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('716', '海北藏族自治州', '青海省', '632200', '630000', '1314', '29', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('717', '黄南藏族自治州', '青海省', '632300', '630000', '1315', '29', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('718', '海南藏族自治州', '青海省', '632500', '630000', '1316', '29', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('719', '果洛藏族自治州', '青海省', '632600', '630000', '1317', '29', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('720', '玉树藏族自治州', '青海省', '632700', '630000', '1318', '29', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('721', '海西蒙古族藏族自治州', '青海省', '632800', '630000', '1319', '29', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('722', '银川市', '宁夏回族自治区', '640100', '640000', '1320', '30', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('723', '石嘴山市', '宁夏回族自治区', '640200', '640000', '1321', '30', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('724', '吴忠市', '宁夏回族自治区', '640300', '640000', '1322', '30', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('725', '固原市', '宁夏回族自治区', '640400', '640000', '1323', '30', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('726', '中卫市', '宁夏回族自治区', '640500', '640000', '1324', '30', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('727', '乌鲁木齐市', '新疆维吾尔自治区', '650100', '650000', '1325', '31', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('728', '克拉玛依市', '新疆维吾尔自治区', '650200', '650000', '1326', '31', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('729', '吐鲁番地区', '新疆维吾尔自治区', '652100', '650000', '1327', '31', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('730', '哈密地区', '新疆维吾尔自治区', '652200', '650000', '1328', '31', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('731', '昌吉回族自治州', '新疆维吾尔自治区', '652300', '650000', '1329', '31', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('732', '博尔塔拉蒙古自治州', '新疆维吾尔自治区', '652700', '650000', '1330', '31', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('733', '巴音郭楞蒙古自治州', '新疆维吾尔自治区', '652800', '650000', '1331', '31', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('734', '阿克苏地区', '新疆维吾尔自治区', '652900', '650000', '1332', '31', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('735', '克孜勒苏柯尔克孜自治州', '新疆维吾尔自治区', '653000', '650000', '1333', '31', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('736', '喀什地区', '新疆维吾尔自治区', '653100', '650000', '1334', '31', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('737', '和田地区', '新疆维吾尔自治区', '653200', '650000', '1335', '31', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('738', '伊犁哈萨克自治州', '新疆维吾尔自治区', '654000', '650000', '1336', '31', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('739', '塔城地区', '新疆维吾尔自治区', '654200', '650000', '1337', '31', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('740', '阿勒泰地区', '新疆维吾尔自治区', '654300', '650000', '1338', '31', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('741', '石河子市', '新疆维吾尔自治区', '659001', '650000', '1339', '31', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('742', '阿拉尔市', '新疆维吾尔自治区', '659002', '650000', '1340', '31', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('743', '图木舒克市', '新疆维吾尔自治区', '659003', '650000', '1341', '31', '2015-03-17 11:19:20', '', '1');
INSERT INTO `state_city` VALUES ('744', '五家渠市', '新疆维吾尔自治区', '659004', '650000', '1342', '31', '2015-03-17 11:19:20', '', '1');

-- ----------------------------
-- Table structure for `subject`
-- ----------------------------
DROP TABLE IF EXISTS `subject`;
CREATE TABLE `subject` (
  `id` bigint(20) NOT NULL COMMENT '主键ID',
  `site` varchar(16) NOT NULL,
  `code` varchar(10) NOT NULL DEFAULT '' COMMENT '专题编码',
  `name` varchar(32) NOT NULL COMMENT '专题名称',
  `parent_code` varchar(10) NOT NULL COMMENT '父专题编码',
  `leaf` bit(1) NOT NULL COMMENT '是否叶子节点',
  `level` int(1) NOT NULL DEFAULT '1' COMMENT '专题层级',
  `serial` int(2) NOT NULL COMMENT '序列',
  `link` varchar(255) DEFAULT NULL COMMENT '链接',
  `create_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `deleted` bit(1) DEFAULT b'0',
  `version` int(11) DEFAULT '1',
  PRIMARY KEY (`id`),
  UNIQUE KEY `site_code` (`site`,`code`) USING BTREE
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of subject
-- ----------------------------

-- ----------------------------
-- Table structure for `sub_account`
-- ----------------------------
DROP TABLE IF EXISTS `sub_account`;
CREATE TABLE `sub_account` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `account_id` bigint(20) DEFAULT NULL,
  `type` varchar(10) DEFAULT NULL,
  `balance` decimal(20,2) NOT NULL DEFAULT '0.00',
  `freeze_balance` decimal(20,2) NOT NULL DEFAULT '0.00',
  `create_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `deleted` bit(1) DEFAULT b'0',
  `version` int(11) DEFAULT '1',
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=150127140852010034 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of sub_account
-- ----------------------------

-- ----------------------------
-- Table structure for `sub_qdd_account`
-- ----------------------------
DROP TABLE IF EXISTS `sub_qdd_account`;
CREATE TABLE `sub_qdd_account` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `platform_id` varchar(10) DEFAULT NULL,
  `money_more_more_id` varchar(10) DEFAULT NULL,
  `authorised` bit(1) DEFAULT b'0',
  `create_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `deleted` bit(1) DEFAULT b'0',
  `version` int(11) DEFAULT '1',
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=150127140852010034 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of sub_qdd_account
-- ----------------------------

-- ----------------------------
-- Table structure for `trade`
-- ----------------------------
DROP TABLE IF EXISTS `trade`;
CREATE TABLE `trade` (
  `id` bigint(20) NOT NULL DEFAULT '0' COMMENT '交易流水号',
  `batch_id` bigint(20) DEFAULT '0' COMMENT '交易批次',
  `parent_trade_id` bigint(20) DEFAULT '0' COMMENT '父交易流水号',
  `source_trade_id` bigint(20) DEFAULT '0' COMMENT '源交易流水号',
  `trade_name` varchar(64) DEFAULT '' COMMENT '交易名称',
  `trade_subject_id` varchar(32) DEFAULT '' COMMENT '交易标的流水号',
  `trade_subject_info` varchar(64) DEFAULT '' COMMENT '交易标的描述',
  `trade_source` varchar(8) DEFAULT '' COMMENT '交易来源',
  `credit_account` varchar(32) DEFAULT '' COMMENT '出借方交易账户ID（帐务流水号）',
  `credit_bank_name` varchar(64) DEFAULT '' COMMENT '出借方银行名称（银行代码）',
  `credit_account_type` varchar(64) DEFAULT '' COMMENT '出借方银行账户类型',
  `credit_account_no` varchar(64) DEFAULT '' COMMENT '出借方银行账户号码',
  `credit_balance` decimal(20,2) DEFAULT '0.00' COMMENT '出借方余额',
  `credit_freeze_balance` decimal(20,2) DEFAULT '0.00' COMMENT '出借方冻结金额',
  `debit_account` varchar(32) DEFAULT '' COMMENT '贷入方交易账户ID（帐务流水号）',
  `debit_bank_name` varchar(64) DEFAULT '' COMMENT '贷入方银行名称（银行代码）',
  `debit_account_type` varchar(64) DEFAULT '' COMMENT '贷入方银行账户类型',
  `debit_account_no` varchar(64) DEFAULT '' COMMENT '贷入方银行账户号码',
  `debit_balance` decimal(20,2) DEFAULT '0.00' COMMENT '贷入方余额',
  `debit_freeze_balance` decimal(20,2) DEFAULT '0.00' COMMENT '贷入方冻结金额',
  `trade_type` varchar(8) DEFAULT '' COMMENT '交易类型（投资、还款、分佣、充值、提现、转账）',
  `trade_kind` varchar(8) DEFAULT '' COMMENT '交易子类型（子投资、子还款、子分佣）',
  `trade_action` varchar(8) DEFAULT '' COMMENT '交易操作类型（冻结、操作）',
  `trade_relation` varchar(8) DEFAULT '' COMMENT '交易双方关系（一对一、一对多、多对一）',
  `trade_status` varchar(8) DEFAULT '' COMMENT '交易状态（执行成功、执行失败、待执行、执行中）',
  `trade_amount` decimal(20,2) DEFAULT '0.00' COMMENT '实际交易金额，单位：人民币，元，保留2位精度',
  `aim_trade_amount` decimal(20,2) DEFAULT '0.00' COMMENT '目标交易金额，单位：人民币，元，保留2位精度',
  `deal_date` datetime DEFAULT NULL COMMENT '执行结束时间  ',
  `memo` varchar(32) DEFAULT '' COMMENT '备注',
  `create_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `deleted` bit(1) NOT NULL DEFAULT b'0',
  `version` int(11) NOT NULL DEFAULT '1',
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of trade
-- ----------------------------

-- ----------------------------
-- Table structure for `trade_log`
-- ----------------------------
DROP TABLE IF EXISTS `trade_log`;
CREATE TABLE `trade_log` (
  `id` bigint(20) NOT NULL DEFAULT '0' COMMENT '日志流水号',
  `trade_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '交易流水号',
  `trade_param` varchar(256) NOT NULL DEFAULT '' COMMENT '交易输出参数',
  `exe_time` datetime DEFAULT NULL COMMENT '执行时间  ',
  `exe_result` varchar(8) NOT NULL DEFAULT '' COMMENT '执行结果  ',
  `exe_msg` varchar(1024) NOT NULL DEFAULT '' COMMENT '执行反馈  ',
  `exe_engine` varchar(8) NOT NULL DEFAULT '' COMMENT '驱动方式 （手动、自动）',
  `exe_user` varchar(32) NOT NULL DEFAULT '' COMMENT '执行者',
  `memo` varchar(32) NOT NULL DEFAULT '' COMMENT '备注',
  `create_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `deleted` bit(1) NOT NULL DEFAULT b'0',
  `version` int(11) NOT NULL DEFAULT '1',
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of trade_log
-- ----------------------------

-- ----------------------------
-- Table structure for `trade_schedule`
-- ----------------------------
DROP TABLE IF EXISTS `trade_schedule`;
CREATE TABLE `trade_schedule` (
  `id` bigint(20) NOT NULL DEFAULT '0' COMMENT '调度流水号',
  `schedule_info` varchar(64) NOT NULL DEFAULT '' COMMENT '调度信息描述',
  `schedule_code` varchar(32) NOT NULL DEFAULT '' COMMENT '调度指令代码',
  `schedule_status` varchar(8) NOT NULL DEFAULT '' COMMENT '调度指令状态',
  `schedule_time` varchar(32) NOT NULL DEFAULT '' COMMENT '调度时间',
  `schedule_format` varchar(32) NOT NULL DEFAULT '' COMMENT '调度时间格式',
  `schedule_target` varchar(32) NOT NULL DEFAULT '' COMMENT '调度影响范围',
  `schedule_source` varchar(8) NOT NULL DEFAULT '' COMMENT '调度指令来源',
  `schedule_creater` varchar(32) NOT NULL DEFAULT '' COMMENT '调度指令发布人',
  `memo` varchar(32) NOT NULL DEFAULT '' COMMENT '备注',
  `create_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `deleted` bit(1) NOT NULL DEFAULT b'0',
  `version` int(11) NOT NULL DEFAULT '1',
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of trade_schedule
-- ----------------------------

-- ----------------------------
-- Table structure for `upload_image`
-- ----------------------------
DROP TABLE IF EXISTS `upload_image`;
CREATE TABLE `upload_image` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `object_type` varchar(20) DEFAULT NULL,
  `object_id` bigint(20) DEFAULT NULL,
  `image_type` varchar(20) DEFAULT NULL,
  `image_name` varchar(50) DEFAULT NULL,
  `image_path` varchar(255) DEFAULT NULL,
  `create_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `deleted` bit(1) DEFAULT b'0',
  `version` int(11) DEFAULT '1',
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=150116133854030004 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of upload_image
-- ----------------------------

-- ----------------------------
-- Table structure for `user`
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `id` bigint(20) NOT NULL COMMENT '主键ID',
  `user_name` varchar(40) NOT NULL COMMENT '用户名',
  `password` varchar(50) DEFAULT NULL COMMENT '密码',
  `status` varchar(2) DEFAULT NULL COMMENT '用户状态',
  `salt` varchar(50) DEFAULT NULL COMMENT '密码盐',
  `role` varchar(10) DEFAULT NULL COMMENT '角色',
  `register_type` varchar(2) DEFAULT NULL COMMENT '注册类型',
  `activate_code` varchar(40) DEFAULT NULL COMMENT '激活码',
  `reg_source` varchar(32) DEFAULT NULL COMMENT '注册类型',
  `reg_date` datetime DEFAULT NULL COMMENT '注册时间',
  `last_login_time` datetime DEFAULT NULL COMMENT '最后一次登录时间',
  `create_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `deleted` bit(1) DEFAULT b'0',
  `version` int(11) DEFAULT '1',
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES ('1', 'admin', '259907396c78433babf37375469b88e2', 'A', '42v1580n5stfftu9nptrlot1lbadyzaj', 'A', 'P', null, null, null, null, '2015-03-17 11:20:03', '', '1');
INSERT INTO `user` VALUES ('2', 'platform_broker', '97d15f445a55bac5c67b695cf68085fe', 'A', '8d78869f470951332959580424d4bf4f', 'BR', 'P', null, null, null, null, '2015-03-17 11:20:03', '', '1');
INSERT INTO `user` VALUES ('3', 'platform_agency', '561a2c00af936e85f89f0b9b0bb8ae73', 'A', '8d78869f470951332959580424d4bf4f', 'AG', 'E', null, null, null, null, '2015-03-17 11:20:03', '', '1');
INSERT INTO `user` VALUES ('4', 'platform_account', '1a7802da141b15a898e220a9e4f2d7b4', 'A', '8d78869f470951332959580424d4bf4f', 'BR', 'P', null, null, null, null, '2015-03-17 11:20:03', '', '1');
INSERT INTO `user` VALUES ('5', 'platform_temp_account', '3ea93e551141efd3ef39094e793ae445', 'A', '8d78869f470951332959580424d4bf4f', 'BR', 'P', null, null, null, null, '2015-03-17 11:20:03', '', '1');

-- ----------------------------
-- Table structure for `user_bank_card`
-- ----------------------------
DROP TABLE IF EXISTS `user_bank_card`;
CREATE TABLE `user_bank_card` (
  `id` bigint(20) NOT NULL COMMENT '主键ID',
  `user_id` bigint(20) NOT NULL COMMENT 'user表主键',
  `bank_card_type` varchar(10) DEFAULT NULL COMMENT '银行卡类型',
  `bank_code` varchar(16) NOT NULL COMMENT '银行代码',
  `bank_card_num` varchar(32) NOT NULL COMMENT '银行卡号',
  `bank_card_account_name` varchar(32) DEFAULT NULL COMMENT '账户名',
  `bank_branch` varchar(45) DEFAULT NULL COMMENT '开户支行',
  `id_type` varchar(4) DEFAULT NULL COMMENT '证件类型',
  `id_num` varchar(32) DEFAULT NULL COMMENT '证件号',
  `province` varchar(16) DEFAULT NULL COMMENT '省',
  `city` varchar(16) DEFAULT NULL COMMENT '市',
  `address` varchar(32) DEFAULT NULL COMMENT '地区',
  `memo` varchar(255) DEFAULT NULL COMMENT '备注',
  `create_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `deleted` bit(1) DEFAULT b'0',
  `version` int(11) DEFAULT '1',
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of user_bank_card
-- ----------------------------

-- ----------------------------
-- Table structure for `user_enterprise_profile`
-- ----------------------------
DROP TABLE IF EXISTS `user_enterprise_profile`;
CREATE TABLE `user_enterprise_profile` (
  `id` bigint(20) NOT NULL COMMENT '主键ID',
  `user_id` bigint(20) NOT NULL COMMENT 'user表主键',
  `enterprise_brief_code` varchar(32) DEFAULT NULL COMMENT '机构简码',
  `enterprise_subcode_from` varchar(16) DEFAULT NULL COMMENT '机构号段',
  `enterprise_subcode_to` varchar(16) DEFAULT NULL COMMENT '机构号段',
  `contact_name` varchar(50) NOT NULL COMMENT '联系人姓名',
  `contact_email` varchar(50) NOT NULL COMMENT '联系人邮箱',
  `contact_phone` varchar(11) DEFAULT NULL COMMENT '联系人手机',
  `enterprise_name` varchar(50) NOT NULL COMMENT '企业名称',
  `enterprise_short_name` varchar(32) DEFAULT NULL COMMENT '企业简称',
  `legal_person_name` varchar(50) DEFAULT NULL COMMENT '法人代表姓名',
  `legal_person_id_num` varchar(32) DEFAULT NULL COMMENT '法人身份证号',
  `organization_code` varchar(45) DEFAULT NULL COMMENT '组织机构代码',
  `tax_registration_no` varchar(45) DEFAULT NULL COMMENT '税务登记号',
  `license_num` varchar(45) DEFAULT NULL COMMENT '营业执照注册号',
  `register_province` varchar(45) DEFAULT NULL COMMENT '营业执照注册所属省份',
  `register_city` varchar(45) DEFAULT NULL COMMENT '营业执照注册所属市',
  `register_address` varchar(45) DEFAULT NULL COMMENT '公司常用地址',
  `license_valid_period` varchar(45) DEFAULT NULL COMMENT '经营期限',
  `phone_num` varchar(45) DEFAULT NULL COMMENT '公司联系电话',
  `license_path` varchar(45) DEFAULT NULL COMMENT '营业执照副本扫描件',
  `license_cachet_path` varchar(45) DEFAULT NULL COMMENT '盖章的营业执照副本扫描件',
  `legal_person_id_copy_font` varchar(255) DEFAULT NULL COMMENT ' 法人代表身份证正面照',
  `legal_person_id_copy_back` varchar(255) DEFAULT NULL COMMENT '法人代表身份证反面照',
  `openning_license_path` varchar(255) DEFAULT NULL COMMENT '开户许可证照片',
  `memo` varchar(255) DEFAULT NULL COMMENT '备注',
  `create_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `deleted` bit(1) DEFAULT b'0',
  `version` int(11) DEFAULT '1',
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of user_enterprise_profile
-- ----------------------------
INSERT INTO `user_enterprise_profile` VALUES ('3', '3', 'by', '001', '10000', '倍赢', 'by@caifuxiang.com', null, '倍赢财富箱', null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, '2015-03-17 11:20:03', '', '1');

-- ----------------------------
-- Table structure for `user_group`
-- ----------------------------
DROP TABLE IF EXISTS `user_group`;
CREATE TABLE `user_group` (
  `id` bigint(20) NOT NULL COMMENT '主键ID',
  `parent_user_id` bigint(20) NOT NULL COMMENT '父user_id',
  `child_user_id` bigint(20) NOT NULL COMMENT '子user_id',
  `relationship` varchar(4) DEFAULT NULL COMMENT '关系',
  `level` varchar(8) DEFAULT NULL COMMENT '级别',
  `memo` varchar(255) DEFAULT NULL COMMENT '备注',
  `root_id` bigint(20) DEFAULT NULL COMMENT '根id',
  `create_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `deleted` bit(1) DEFAULT b'0',
  `version` int(11) DEFAULT '1',
  `super_broker_id` bigint(20) DEFAULT NULL COMMENT '超级经纪人userId',
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of user_group
-- ----------------------------

-- ----------------------------
-- Table structure for `user_profile`
-- ----------------------------
DROP TABLE IF EXISTS `user_profile`;
CREATE TABLE `user_profile` (
  `id` bigint(20) NOT NULL COMMENT '主键ID',
  `user_id` bigint(20) NOT NULL COMMENT 'user表主键',
  `mobile` varchar(11) DEFAULT NULL COMMENT '手机',
  `real_name` varchar(50) NOT NULL COMMENT '真实姓名',
  `email` varchar(200) NOT NULL COMMENT '邮箱',
  `id_type` varchar(2) DEFAULT NULL COMMENT '证件类型',
  `id_num` varchar(32) DEFAULT NULL COMMENT '证件号',
  `gender` varchar(2) DEFAULT NULL COMMENT '性别',
  `id_valid_to` timestamp NULL DEFAULT NULL COMMENT '证件到期时间',
  `id_copy_front` varchar(255) DEFAULT NULL COMMENT '证件照扫描件正面',
  `id_copy_bank` varchar(255) DEFAULT NULL COMMENT '证件照扫描件反面',
  `audit_password` varchar(255) DEFAULT NULL COMMENT '审核密码',
  `audit_salt` varchar(255) DEFAULT NULL COMMENT '密码盐',
  `customer_num` varchar(32) DEFAULT NULL COMMENT '客户编号',
  `memo` varchar(255) DEFAULT NULL COMMENT '备注',
  `create_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `deleted` bit(1) DEFAULT b'0',
  `version` int(11) DEFAULT '1',
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of user_profile
-- ----------------------------

-- ----------------------------
-- Table structure for `user_status`
-- ----------------------------
DROP TABLE IF EXISTS `user_status`;
CREATE TABLE `user_status` (
  `id` bigint(20) NOT NULL COMMENT '主键ID',
  `user_id` bigint(20) DEFAULT NULL COMMENT 'user表主键',
  `activated` varchar(2) DEFAULT NULL COMMENT '是否激活',
  `emailbinding` varchar(2) DEFAULT NULL COMMENT '邮箱是否绑定',
  `mobilebinding` varchar(2) DEFAULT NULL COMMENT '手机是否绑定',
  `real_name_auth_status` varchar(2) DEFAULT NULL COMMENT '是否实名认证',
  `bankbinding` varchar(2) DEFAULT NULL COMMENT '银行卡绑定',
  `create_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `deleted` bit(1) DEFAULT b'0',
  `version` int(11) DEFAULT '1',
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of user_status
-- ----------------------------
