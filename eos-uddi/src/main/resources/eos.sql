/*
 Navicat MySQL Data Transfer

 Source Server         : local
 Source Server Version : 50529
 Source Host           : localhost
 Source Database       : eos

 Target Server Version : 50529
 File Encoding         : utf-8

 Date: 02/29/2016 10:07:38 AM
*/

SET NAMES utf8;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
--  Table structure for `T_APP`
-- ----------------------------
DROP TABLE IF EXISTS `T_APP`;
CREATE TABLE `T_APP` (
	`APP_ID` int(11) NOT NULL AUTO_INCREMENT,
	`APP_NAME` varchar(50) DEFAULT NULL,
	`APP_CODE` varchar(50) DEFAULT NULL,
	`CREATE_TIME` varchar(50) DEFAULT NULL,
	`DBS` varchar(100) DEFAULT NULL,
	PRIMARY KEY (`APP_ID`)
) ENGINE=`InnoDB` AUTO_INCREMENT=33 DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci ROW_FORMAT=COMPACT COMMENT='应用表' CHECKSUM=0 DELAY_KEY_WRITE=0;

-- ----------------------------
--  Records of `T_APP`
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
--  Table structure for `T_DB_CHANGE`
-- ----------------------------
DROP TABLE IF EXISTS `T_DB_CHANGE`;
CREATE TABLE `T_DB_CHANGE` (
	`ID` int(11) NOT NULL AUTO_INCREMENT,
	`APP_ID` int(11) DEFAULT NULL,
	`VERSION` varchar(30) DEFAULT NULL COMMENT '版本号',
	`USER` int(11) DEFAULT NULL COMMENT '发布人',
	`CHANGE_LOG` text DEFAULT NULL COMMENT '更新日志',
	`PUBISH_TIME` varchar(14) DEFAULT NULL,
	`DB` varchar(30) DEFAULT NULL COMMENT '所属库',
	`SCRIPT` varchar(255) DEFAULT NULL COMMENT '脚本文件',
	PRIMARY KEY (`ID`)
) ENGINE=`InnoDB` AUTO_INCREMENT=5 DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci ROW_FORMAT=COMPACT CHECKSUM=0 DELAY_KEY_WRITE=0;

-- ----------------------------
--  Records of `T_DB_CHANGE`
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
--  Table structure for `T_DB_CHECKLIST`
-- ----------------------------
DROP TABLE IF EXISTS `T_DB_CHECKLIST`;
CREATE TABLE `T_DB_CHECKLIST` (
	`ID` int(11) NOT NULL AUTO_INCREMENT,
	`CHANGE_ID` int(11) DEFAULT NULL,
	`CHECK_USER` int(11) DEFAULT NULL,
	`CHECK_CONTENT` text DEFAULT NULL,
	`CHECK_STATUS` char(1) DEFAULT NULL COMMENT '1 审批通过  2 审批不通过',
	`CHECK_TIME` char(14) DEFAULT NULL,
	PRIMARY KEY (`ID`)
) ENGINE=`InnoDB` AUTO_INCREMENT=6 DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci ROW_FORMAT=COMPACT CHECKSUM=0 DELAY_KEY_WRITE=0;

-- ----------------------------
--  Records of `T_DB_CHECKLIST`
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
--  Table structure for `T_DB_PDM`
-- ----------------------------
DROP TABLE IF EXISTS `T_DB_PDM`;
CREATE TABLE `T_DB_PDM` (
	`ID` int(11) NOT NULL AUTO_INCREMENT,
	`APP_ID` int(11) DEFAULT NULL,
	`PDM` varchar(30) DEFAULT NULL COMMENT 'PDM文件',
	`IS_LOCK` char(1) DEFAULT NULL COMMENT '0 未锁定 1 已锁定',
	`LOCK_USER_ID` int(11) DEFAULT '0',
	PRIMARY KEY (`ID`)
) ENGINE=`InnoDB` AUTO_INCREMENT=5 DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci ROW_FORMAT=COMPACT CHECKSUM=0 DELAY_KEY_WRITE=0;

-- ----------------------------
--  Records of `T_DB_PDM`
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
--  Table structure for `T_METHOD`
-- ----------------------------
DROP TABLE IF EXISTS `T_METHOD`;
CREATE TABLE `T_METHOD` (
	`METHOD_ID` int(11) NOT NULL AUTO_INCREMENT,
	`VERSION_ID` int(11) DEFAULT NULL,
	`METHOD_NAME` varchar(50) DEFAULT NULL,
	`MOCK_RESULT` text DEFAULT NULL,
	`PARAMS` text DEFAULT NULL,
	PRIMARY KEY (`METHOD_ID`)
) ENGINE=`InnoDB` AUTO_INCREMENT=205 DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci ROW_FORMAT=COMPACT CHECKSUM=0 DELAY_KEY_WRITE=0;

-- ----------------------------
--  Records of `T_METHOD`
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
--  Table structure for `T_MODULE`
-- ----------------------------
DROP TABLE IF EXISTS `T_MODULE`;
CREATE TABLE `T_MODULE` (
	`MODULE_ID` int(11) NOT NULL AUTO_INCREMENT,
	`MODULE_NAME` varchar(50) DEFAULT NULL,
	`APP_ID` char(10) DEFAULT NULL,
	`Column_4` int(11) DEFAULT NULL,
	PRIMARY KEY (`MODULE_ID`)
) ENGINE=`InnoDB` AUTO_INCREMENT=38 DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci ROW_FORMAT=COMPACT COMMENT='模块' CHECKSUM=0 DELAY_KEY_WRITE=0;

-- ----------------------------
--  Records of `T_MODULE`
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
--  Table structure for `T_SERVICE`
-- ----------------------------
DROP TABLE IF EXISTS `T_SERVICE`;
CREATE TABLE `T_SERVICE` (
	`SERVICE_ID` int(11) NOT NULL AUTO_INCREMENT,
	`APP_ID` int(11) DEFAULT NULL,
	`MODULE` varchar(50) DEFAULT NULL,
	`USER_ID` int(11) DEFAULT NULL,
	`SERVICE_CODE` varchar(50) DEFAULT NULL,
	`VERSION` varchar(50) DEFAULT NULL,
	`APP_CODE` varchar(50) DEFAULT NULL,
	`CREATE_TIME` varchar(50) DEFAULT NULL,
	`SERVICE_NAME` varchar(100) DEFAULT NULL,
	`TEST` varchar(1) DEFAULT NULL,
	PRIMARY KEY (`SERVICE_ID`)
) ENGINE=`InnoDB` AUTO_INCREMENT=51 DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci ROW_FORMAT=COMPACT CHECKSUM=0 DELAY_KEY_WRITE=0;

-- ----------------------------
--  Records of `T_SERVICE`
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
--  Table structure for `T_SERVICE_VERSION`
-- ----------------------------
DROP TABLE IF EXISTS `T_SERVICE_VERSION`;
CREATE TABLE `T_SERVICE_VERSION` (
	`VERSION_ID` int(11) NOT NULL AUTO_INCREMENT,
	`SERVICE_ID` int(11) DEFAULT NULL,
	`VERSION` varchar(50) DEFAULT NULL,
	`APP_CODE` varchar(50) DEFAULT NULL,
	`STATUS` char(1) DEFAULT NULL COMMENT '0 未审批 1 已审批',
	`CREATE_TIME` varchar(50) DEFAULT NULL,
	`CHECK_USER` int(11) DEFAULT NULL,
	PRIMARY KEY (`VERSION_ID`)
) ENGINE=`InnoDB` AUTO_INCREMENT=64 DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci ROW_FORMAT=COMPACT CHECKSUM=0 DELAY_KEY_WRITE=0;

-- ----------------------------
--  Records of `T_SERVICE_VERSION`
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
--  Table structure for `T_USER`
-- ----------------------------
DROP TABLE IF EXISTS `T_USER`;
CREATE TABLE `T_USER` (
	`USER_ID` int(11) NOT NULL AUTO_INCREMENT,
	`USER_NAME` varchar(50) DEFAULT NULL,
	`PWD` varchar(50) DEFAULT NULL,
	`ROLE` char(1) DEFAULT NULL COMMENT '1表示 普通员工 2 审核人员 3 管理员 4 数据组',
	`DEFAULT_APPID` int(11) DEFAULT NULL,
	`CREATE_TIME` varchar(50) DEFAULT NULL,
	`EMAIL` varchar(100) DEFAULT NULL,
	PRIMARY KEY (`USER_ID`),
	CONSTRAINT `FK_Reference_4` FOREIGN KEY (`DEFAULT_APPID`) REFERENCES `T_APP` (`APP_ID`),
	INDEX `FK_Reference_4` (DEFAULT_APPID)
) ENGINE=`InnoDB` AUTO_INCREMENT=38 DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci ROW_FORMAT=COMPACT COMMENT='用户表' CHECKSUM=0 DELAY_KEY_WRITE=0;

-- ----------------------------
--  Records of `T_USER`
-- ----------------------------
BEGIN;
INSERT INTO `T_USER` VALUES ('29', 'hexin', '123456', '3', null, null, 'hexin@sunsharing.com.cn');
COMMIT;

-- ----------------------------
--  Table structure for `T_USER_APP_copy`
-- ----------------------------
DROP TABLE IF EXISTS `T_USER_APP_copy`;
CREATE TABLE `T_USER_APP_copy` (
	`USER_APP_ID` int(11) NOT NULL AUTO_INCREMENT,
	`USER_ID` int(11) DEFAULT NULL,
	`APP_ID` int(11) DEFAULT NULL,
	PRIMARY KEY (`USER_APP_ID`),
	CONSTRAINT `t_user_app_copy_ibfk_1` FOREIGN KEY (`USER_ID`) REFERENCES `T_USER` (`USER_ID`),
	CONSTRAINT `t_user_app_copy_ibfk_2` FOREIGN KEY (`APP_ID`) REFERENCES `T_APP` (`APP_ID`),
	INDEX `FK_Reference_2` (USER_ID),
	INDEX `FK_Reference_3` (APP_ID)
) ENGINE=`InnoDB` AUTO_INCREMENT=1 DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci ROW_FORMAT=COMPACT COMMENT='用户APP' CHECKSUM=0 DELAY_KEY_WRITE=0;

SET FOREIGN_KEY_CHECKS = 1;
