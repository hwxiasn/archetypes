-- ----------------------------
-- Table structure for `article`
-- ----------------------------
DROP TABLE IF EXISTS `article`;
CREATE TABLE `article` (
  `id` bigint(20) NOT NULL  COMMENT '主键ID',
  `site` varchar(16) NOT NULL,
  `subject` varchar(6) NOT NULL COMMENT '所属专题',
  `title` varchar(128) NOT NULL COMMENT '标题',
  `author` varchar(32) NOT NULL COMMENT '作者',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `publish_time` datetime DEFAULT NULL COMMENT '发布时间',
  `weight` int(1) NOT NULL DEFAULT '1' COMMENT '权重 1.普通；3.置顶',
  `status` int(1) NOT NULL DEFAULT '1' COMMENT '状态； 1. READY; 2. ONLINE; 3. OFFLINE',
  `link` varchar(256) DEFAULT NULL COMMENT '链接',
  PRIMARY KEY (`id`)
) ;


-- ----------------------------
-- Table structure for `article_text`
-- ----------------------------
DROP TABLE IF EXISTS `article_text`;
CREATE TABLE `article_text` (
  `id` bigint(20) NOT NULL COMMENT '主键ID',
  `article_id` bigint(20) NOT NULL COMMENT '文章ID',
  `text_stuff` mediumtext COMMENT '文章正文',
  PRIMARY KEY (`id`),
  UNIQUE KEY `article_id` (`article_id`)
) ;


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
  PRIMARY KEY (`id`),
  UNIQUE KEY `site_code` (`site`,`code`) USING BTREE
) ;

-- ----------------------------
-- Table structure for `article_publish_task`
-- ----------------------------
DROP TABLE IF EXISTS `article_publish_task`;
CREATE TABLE `article_publish_task` (
  `id` bigint(20) NOT NULL DEFAULT '0' COMMENT '主键',
  `article_id` bigint(20)  NOT NULL COMMENT '文章ID',
  `publish_time` datetime  NOT NULL COMMENT '发布时间',
  `status` int(1) NOT NULL DEFAULT '0' COMMENT '任务状态',
  PRIMARY KEY (`id`)
);


-- ----------------------------
-- Table structure for `message`
-- ----------------------------
DROP TABLE IF EXISTS `message`;
CREATE TABLE `message` (
  `id` bigint(20) NOT NULL,
  `sender` varchar(20) DEFAULT NULL,
  `receiver_id` bigint(20) DEFAULT NULL,
  `type` int(2) DEFAULT NULL,
  `title` varchar(128) DEFAULT NULL,
  `text` varchar(2056) DEFAULT NULL,
  `status` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
)

