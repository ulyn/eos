# Sequel Pro dump
# Version 1191
# http://code.google.com/p/sequel-pro
#
# Host: 127.0.0.1 (MySQL 5.5.29)
# Database: eos
# Generation Time: 2014-02-07 06:53:55 +0000
# ************************************************************

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;


# Dump of table T_APP
# ------------------------------------------------------------

DROP TABLE IF EXISTS `T_APP`;

CREATE TABLE `T_APP` (
  `APP_ID` int(11) NOT NULL AUTO_INCREMENT,
  `APP_NAME` varchar(50) DEFAULT NULL,
  `APP_CODE` varchar(50) DEFAULT NULL,
  `CREATE_TIME` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`APP_ID`)
) ENGINE=InnoDB AUTO_INCREMENT=31 DEFAULT CHARSET=utf8 COMMENT='应用表';

LOCK TABLES `T_APP` WRITE;
/*!40000 ALTER TABLE `T_APP` DISABLE KEYS */;
INSERT INTO `T_APP` (`APP_ID`,`APP_NAME`,`APP_CODE`,`CREATE_TIME`)
VALUES
	(29,'中文','criss','20140131185715'),
	(30,'租车行','criss2','20140131234624');

/*!40000 ALTER TABLE `T_APP` ENABLE KEYS */;
UNLOCK TABLES;


# Dump of table T_METHOD
# ------------------------------------------------------------

DROP TABLE IF EXISTS `T_METHOD`;

CREATE TABLE `T_METHOD` (
  `METHOD_ID` int(11) NOT NULL AUTO_INCREMENT,
  `VERSION_ID` int(11) DEFAULT NULL,
  `METHOD_NAME` varchar(50) DEFAULT NULL,
  `MOCK_RESULT` text,
  PRIMARY KEY (`METHOD_ID`)
) ENGINE=InnoDB AUTO_INCREMENT=79 DEFAULT CHARSET=utf8;

LOCK TABLES `T_METHOD` WRITE;
/*!40000 ALTER TABLE `T_METHOD` DISABLE KEYS */;
INSERT INTO `T_METHOD` (`METHOD_ID`,`VERSION_ID`,`METHOD_NAME`,`MOCK_RESULT`)
VALUES
	(73,41,'sayHello2','[{\"content\":\"{\\\"error\\\":\\\"错误了3\\\"}\",\"desc\":\"当入参为其他时为错误输出\",\"status\":\"error\"},{\"content\":\"{\\\"success\\\":\\\"成功了2\\\",\\\"haha\\\":\\\"haha2\\\"}\",\"desc\":\"当入参name=\\\"criss\\\"为成功输出\",\"status\":\"success\"}]'),
	(74,41,'sayHello','[{\"content\":\"{\\\"error\\\":\\\"错误了sayHello\\\"}\",\"desc\":\"当入参为其他时为错误输出\",\"status\":\"error\"},{\"content\":\"{\\\"success\\\":\\\"成功了\\\"}\",\"desc\":\"当入参name=\\\"criss\\\"为成功输出\",\"status\":\"success\"}]'),
	(75,42,'sayHello2','[{\"content\":\"{\\\"error\\\":\\\"错误了say1.6\\\"}\",\"desc\":\"当入参为其他时为错误输出\",\"status\":\"error\"},{\"content\":\"{\\\"success\\\":\\\"成功了2\\\",\\\"haha\\\":\\\"haha2\\\"}\",\"desc\":\"当入参name=\\\"criss\\\"为成功输出\",\"status\":\"success\"}]'),
	(76,42,'sayHello','[{\"content\":\"{\\\"error\\\":\\\"错误了\\\"}\",\"desc\":\"当入参为其他时为错误输出\",\"status\":\"error\"},{\"content\":\"{\\\"success\\\":\\\"成功了\\\"}\",\"desc\":\"当入参name=\\\"criss\\\"为成功输出\",\"status\":\"success\"}]'),
	(77,43,'sayHello2','[{\"content\":\"{\\\"error\\\":\\\"错误了23456\\\"}\",\"desc\":\"当入参为其他时为错误输出\",\"status\":\"error\"},{\"content\":\"{\\\"success\\\":\\\"成功了2\\\",\\\"haha\\\":\\\"haha2\\\"}\",\"desc\":\"当入参name=\\\"criss\\\"为成功输出\",\"status\":\"success\"}]'),
	(78,43,'sayHello','[{\"content\":\"{\\\"error\\\":\\\"错误了\\\"}\",\"desc\":\"当入参为其他时为错误输出\",\"status\":\"error\"},{\"content\":\"{\\\"success\\\":\\\"成功了\\\"}\",\"desc\":\"当入参name=\\\"criss\\\"为成功输出\",\"status\":\"success\"}]');

/*!40000 ALTER TABLE `T_METHOD` ENABLE KEYS */;
UNLOCK TABLES;


# Dump of table T_MODULE
# ------------------------------------------------------------

DROP TABLE IF EXISTS `T_MODULE`;

CREATE TABLE `T_MODULE` (
  `MODULE_ID` int(11) NOT NULL AUTO_INCREMENT,
  `MODULE_NAME` varchar(50) DEFAULT NULL,
  `APP_ID` char(10) DEFAULT NULL,
  `Column_4` int(11) DEFAULT NULL,
  PRIMARY KEY (`MODULE_ID`)
) ENGINE=InnoDB AUTO_INCREMENT=33 DEFAULT CHARSET=utf8 COMMENT='模块';

LOCK TABLES `T_MODULE` WRITE;
/*!40000 ALTER TABLE `T_MODULE` DISABLE KEYS */;
INSERT INTO `T_MODULE` (`MODULE_ID`,`MODULE_NAME`,`APP_ID`,`Column_4`)
VALUES
	(29,'中文','29',NULL),
	(30,'中文2','29',NULL),
	(31,'用户管理','30',NULL),
	(32,'租车管理','30',NULL);

/*!40000 ALTER TABLE `T_MODULE` ENABLE KEYS */;
UNLOCK TABLES;


# Dump of table T_SERVICE
# ------------------------------------------------------------

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
) ENGINE=InnoDB AUTO_INCREMENT=39 DEFAULT CHARSET=utf8;

LOCK TABLES `T_SERVICE` WRITE;
/*!40000 ALTER TABLE `T_SERVICE` DISABLE KEYS */;
INSERT INTO `T_SERVICE` (`SERVICE_ID`,`APP_ID`,`MODULE`,`USER_ID`,`SERVICE_CODE`,`VERSION`,`APP_CODE`,`CREATE_TIME`,`SERVICE_NAME`,`TEST`)
VALUES
	(38,29,'中文',29,'TestInterfaceAnno',NULL,'criss','20140205192855','hexin','1');

/*!40000 ALTER TABLE `T_SERVICE` ENABLE KEYS */;
UNLOCK TABLES;


# Dump of table T_SERVICE_VERSION
# ------------------------------------------------------------

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
) ENGINE=InnoDB AUTO_INCREMENT=44 DEFAULT CHARSET=utf8;

LOCK TABLES `T_SERVICE_VERSION` WRITE;
/*!40000 ALTER TABLE `T_SERVICE_VERSION` DISABLE KEYS */;
INSERT INTO `T_SERVICE_VERSION` (`VERSION_ID`,`SERVICE_ID`,`VERSION`,`APP_CODE`,`STATUS`,`CREATE_TIME`,`CHECK_USER`)
VALUES
	(41,38,'1.5','criss','1','20140203114821',NULL),
	(42,38,'1.6','criss','1','20140203114906',NULL),
	(43,38,'1.7','criss','1','20140205192855',NULL);

/*!40000 ALTER TABLE `T_SERVICE_VERSION` ENABLE KEYS */;
UNLOCK TABLES;


# Dump of table T_USER
# ------------------------------------------------------------

DROP TABLE IF EXISTS `T_USER`;

CREATE TABLE `T_USER` (
  `USER_ID` int(11) NOT NULL AUTO_INCREMENT,
  `USER_NAME` varchar(50) DEFAULT NULL,
  `PWD` varchar(50) DEFAULT NULL,
  `ROLE` char(1) DEFAULT NULL COMMENT '1表示 普通员工 2 审核人员 3 管理员',
  `DEFAULT_APPID` int(11) DEFAULT NULL,
  `CREATE_TIME` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`USER_ID`),
  KEY `FK_Reference_4` (`DEFAULT_APPID`),
  CONSTRAINT `FK_Reference_4` FOREIGN KEY (`DEFAULT_APPID`) REFERENCES `T_APP` (`APP_ID`)
) ENGINE=InnoDB AUTO_INCREMENT=34 DEFAULT CHARSET=utf8 COMMENT='用户表';

LOCK TABLES `T_USER` WRITE;
/*!40000 ALTER TABLE `T_USER` DISABLE KEYS */;
INSERT INTO `T_USER` (`USER_ID`,`USER_NAME`,`PWD`,`ROLE`,`DEFAULT_APPID`,`CREATE_TIME`)
VALUES
	(29,'criss','123456','3',NULL,NULL),
	(33,'hexin','123456','2',NULL,'20140131232942');

/*!40000 ALTER TABLE `T_USER` ENABLE KEYS */;
UNLOCK TABLES;


# Dump of table T_USER_APP
# ------------------------------------------------------------

DROP TABLE IF EXISTS `T_USER_APP`;

CREATE TABLE `T_USER_APP` (
  `USER_APP_ID` int(11) NOT NULL AUTO_INCREMENT,
  `USER_ID` int(11) DEFAULT NULL,
  `APP_ID` int(11) DEFAULT NULL,
  PRIMARY KEY (`USER_APP_ID`),
  KEY `FK_Reference_2` (`USER_ID`),
  KEY `FK_Reference_3` (`APP_ID`),
  CONSTRAINT `FK_Reference_3` FOREIGN KEY (`APP_ID`) REFERENCES `T_APP` (`APP_ID`),
  CONSTRAINT `FK_Reference_2` FOREIGN KEY (`USER_ID`) REFERENCES `T_USER` (`USER_ID`)
) ENGINE=InnoDB AUTO_INCREMENT=35 DEFAULT CHARSET=utf8 COMMENT='用户APP';

LOCK TABLES `T_USER_APP` WRITE;
/*!40000 ALTER TABLE `T_USER_APP` DISABLE KEYS */;
INSERT INTO `T_USER_APP` (`USER_APP_ID`,`USER_ID`,`APP_ID`)
VALUES
	(30,29,29),
	(34,33,29);

/*!40000 ALTER TABLE `T_USER_APP` ENABLE KEYS */;
UNLOCK TABLES;





/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
