/*
 Navicat Premium Data Transfer

 Source Server         : localhost
 Source Server Type    : MySQL
 Source Server Version : 50529
 Source Host           : localhost:3306
 Source Schema         : eos2

 Target Server Type    : MySQL
 Target Server Version : 50529
 File Encoding         : 65001

 Date: 30/10/2018 15:53:40
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for T_APP
-- ----------------------------
DROP TABLE IF EXISTS `T_APP`;
CREATE TABLE `T_APP` (
  `APP_ID` varchar(255) COLLATE utf8_bin NOT NULL,
  `APP_CODE` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `APP_NAME` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `CREATE_TIME` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `DBS` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `YW` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`APP_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Table structure for T_CONFIG
-- ----------------------------
DROP TABLE IF EXISTS `T_CONFIG`;
CREATE TABLE `T_CONFIG` (
  `CONFIG_ID` varchar(255) COLLATE utf8_bin NOT NULL,
  `_DEL` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `APP_ID` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `ATT` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `CHLID_APP_ID` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `CON_DESC` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `DEFAULT_VALUE` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `GROUP_ID` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `IS_BASIC` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `IS_COMMIT` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `CON_KEY` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `REL_CONFIG_ID` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`CONFIG_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Table structure for T_CONFIG_CHILD_APP
-- ----------------------------
DROP TABLE IF EXISTS `T_CONFIG_CHILD_APP`;
CREATE TABLE `T_CONFIG_CHILD_APP` (
  `CHILD_APP_ID` varchar(255) COLLATE utf8_bin NOT NULL,
  `APP_ID` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `CHILD_APP_NAME` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`CHILD_APP_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Table structure for T_CONFIG_GROUP
-- ----------------------------
DROP TABLE IF EXISTS `T_CONFIG_GROUP`;
CREATE TABLE `T_CONFIG_GROUP` (
  `GROUP_ID` varchar(255) COLLATE utf8_bin NOT NULL,
  `_DEL` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `APP_ID` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `CHILD_APP_ID` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `GROUP_NAME` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `IS_COMMON` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`GROUP_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Table structure for T_CONFIG_RUN
-- ----------------------------
DROP TABLE IF EXISTS `T_CONFIG_RUN`;
CREATE TABLE `T_CONFIG_RUN` (
  `RUN_ID` varchar(255) COLLATE utf8_bin NOT NULL,
  `APP_ID` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `BSWZ` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `CHILD_APP_ID` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `RUN_KEY` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`RUN_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Table structure for T_CONFIG_RUN_VAL
-- ----------------------------
DROP TABLE IF EXISTS `T_CONFIG_RUN_VAL`;
CREATE TABLE `T_CONFIG_RUN_VAL` (
  `RUN_VAL_ID` varchar(255) COLLATE utf8_bin NOT NULL,
  `CONFIG_ID` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `RUN_ID` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `VAL` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`RUN_VAL_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Table structure for T_DB_CHANGE
-- ----------------------------
DROP TABLE IF EXISTS `T_DB_CHANGE`;
CREATE TABLE `T_DB_CHANGE` (
  `ID` varchar(255) COLLATE utf8_bin NOT NULL,
  `CHANGE_LOG` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `DB` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `DB_TYPE` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `MODULE` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `PUBISH_TIME` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `SCRIPT` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `VERSION` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `APP_ID` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `USER` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `HAS_SEND` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `FKC460B466E068EF` (`APP_ID`),
  KEY `FKC460B4664ABA7B61` (`USER`),
  CONSTRAINT `FKC460B4664ABA7B61` FOREIGN KEY (`USER`) REFERENCES `T_USER` (`USER_ID`),
  CONSTRAINT `FKC460B466E068EF` FOREIGN KEY (`APP_ID`) REFERENCES `T_APP` (`APP_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Table structure for T_DB_CHECKLIST
-- ----------------------------
DROP TABLE IF EXISTS `T_DB_CHECKLIST`;
CREATE TABLE `T_DB_CHECKLIST` (
  `ID` varchar(255) COLLATE utf8_bin NOT NULL,
  `CHECK_CONTENT` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `CHECK_STATUS` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `CHECK_TIME` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `CHANGE_ID` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `CHECK_USER` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `FK72B17110EB817C23` (`CHANGE_ID`),
  KEY `FK72B171102B9F3438` (`CHECK_USER`),
  CONSTRAINT `FK72B171102B9F3438` FOREIGN KEY (`CHECK_USER`) REFERENCES `T_USER` (`USER_ID`),
  CONSTRAINT `FK72B17110EB817C23` FOREIGN KEY (`CHANGE_ID`) REFERENCES `T_DB_CHANGE` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Table structure for T_DB_PDM
-- ----------------------------
DROP TABLE IF EXISTS `T_DB_PDM`;
CREATE TABLE `T_DB_PDM` (
  `ID` varchar(255) COLLATE utf8_bin NOT NULL,
  `IS_LOCK` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `PDM` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `APP_ID` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `LOCK_USER_ID` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `FK2F2AB3E3E068EF` (`APP_ID`),
  KEY `FK2F2AB3E3D6A8E91` (`LOCK_USER_ID`),
  CONSTRAINT `FK2F2AB3E3D6A8E91` FOREIGN KEY (`LOCK_USER_ID`) REFERENCES `T_USER` (`USER_ID`),
  CONSTRAINT `FK2F2AB3E3E068EF` FOREIGN KEY (`APP_ID`) REFERENCES `T_APP` (`APP_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Table structure for T_METHOD
-- ----------------------------
DROP TABLE IF EXISTS `T_METHOD`;
CREATE TABLE `T_METHOD` (
  `METHOD_ID` varchar(255) COLLATE utf8_bin NOT NULL,
  `METHOD_NAME` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `VERSION` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `MOCK_RESULT` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `PARAMS` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `VERSION_ID` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`METHOD_ID`),
  KEY `FK3EAB7B2CFDF39050` (`VERSION_ID`),
  CONSTRAINT `FK3EAB7B2CFDF39050` FOREIGN KEY (`VERSION_ID`) REFERENCES `T_SERVICE_VERSION` (`VERSION_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Table structure for T_MODULE
-- ----------------------------
DROP TABLE IF EXISTS `T_MODULE`;
CREATE TABLE `T_MODULE` (
  `MODULE_ID` varchar(255) COLLATE utf8_bin NOT NULL,
  `MODULE_NAME` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `APP_ID` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`MODULE_ID`),
  KEY `FK3F3150B7E068EF` (`APP_ID`),
  CONSTRAINT `FK3F3150B7E068EF` FOREIGN KEY (`APP_ID`) REFERENCES `T_APP` (`APP_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Table structure for T_SERVICE
-- ----------------------------
DROP TABLE IF EXISTS `T_SERVICE`;
CREATE TABLE `T_SERVICE` (
  `SERVICE_ID` varchar(255) COLLATE utf8_bin NOT NULL,
  `APP_CODE` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `APP_ID` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `CREATE_TIME` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `MODULE` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `SERVICE_CODE` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `SERVICE_NAME` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `TEST` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `USER_ID` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`SERVICE_ID`),
  KEY `FKD41365AA41BE7DA5` (`USER_ID`),
  CONSTRAINT `FKD41365AA41BE7DA5` FOREIGN KEY (`USER_ID`) REFERENCES `T_USER` (`USER_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Table structure for T_SERVICE_VERSION
-- ----------------------------
DROP TABLE IF EXISTS `T_SERVICE_VERSION`;
CREATE TABLE `T_SERVICE_VERSION` (
  `VERSION_ID` varchar(255) COLLATE utf8_bin NOT NULL,
  `APP_CODE` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `CREATE_TIME` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `VERSION` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `STATUS` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `SERVICE_ID` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`VERSION_ID`),
  KEY `FK52A06B235C1EBDEF` (`SERVICE_ID`),
  CONSTRAINT `FK52A06B235C1EBDEF` FOREIGN KEY (`SERVICE_ID`) REFERENCES `T_SERVICE` (`SERVICE_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Table structure for T_USER
-- ----------------------------
DROP TABLE IF EXISTS `T_USER`;
CREATE TABLE `T_USER` (
  `USER_ID` varchar(255) COLLATE utf8_bin NOT NULL,
  `CREATE_TIME` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `EMAIL` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `IS_TEST` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `PWD` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `ROLE` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `USER_NAME` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `DEFAULT_APPID` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `YW` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`USER_ID`),
  KEY `FK94B9B0D6A273F874` (`DEFAULT_APPID`),
  CONSTRAINT `FK94B9B0D6A273F874` FOREIGN KEY (`DEFAULT_APPID`) REFERENCES `T_APP` (`APP_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of T_USER
-- ----------------------------
BEGIN;
INSERT INTO `T_USER` VALUES ('1', NULL, NULL, '0', '123456', '3', 'criss', NULL, NULL);
COMMIT;

-- ----------------------------
-- Table structure for T_USER_APP
-- ----------------------------
DROP TABLE IF EXISTS `T_USER_APP`;
CREATE TABLE `T_USER_APP` (
  `USER_APP_ID` varchar(255) COLLATE utf8_bin NOT NULL,
  `APP_ID` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `USER_ID` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`USER_APP_ID`),
  KEY `FK4C55831841BE7DA5` (`USER_ID`),
  KEY `FK4C558318E068EF` (`APP_ID`),
  CONSTRAINT `FK4C55831841BE7DA5` FOREIGN KEY (`USER_ID`) REFERENCES `T_USER` (`USER_ID`),
  CONSTRAINT `FK4C558318E068EF` FOREIGN KEY (`APP_ID`) REFERENCES `T_APP` (`APP_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

SET FOREIGN_KEY_CHECKS = 1;
