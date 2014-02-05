/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     2014-1-30 11:21:27                           */
/*==============================================================*/


drop table if exists T_APP;

drop table if exists T_METHOD;

drop table if exists T_MODULE;

drop table if exists T_SERVICE;

drop table if exists T_USER;

drop table if exists T_USER_APP;

/*==============================================================*/
/* Table: T_APP                                                 */
/*==============================================================*/
create table T_APP
(
  APP_ID               int not null auto_increment,
  APP_NAME             varchar(50),
  APP_CODE             varchar(50),
  CREATE_TIME           varchar(50),
  primary key (APP_ID)
)ENGINE=InnoDB AUTO_INCREMENT=29 DEFAULT CHARSET=utf8;

alter table T_APP comment '应用表';

/*==============================================================*/
/* Table: T_METHOD                                              */
/*==============================================================*/
create table T_METHOD
(
  METHOD_ID            int not null auto_increment,
  VERSION_ID           int,
  METHOD_NAME          varchar(50),
  MOCK_RESULT               text,
  primary key (METHOD_ID)
)ENGINE=InnoDB AUTO_INCREMENT=29 DEFAULT CHARSET=utf8;

/*==============================================================*/
/* Table: T_MODULE                                              */
/*==============================================================*/
create table T_MODULE
(
  MODULE_ID            int not null auto_increment,
  MODULE_NAME          varchar(50),
  APP_ID               char(10),
  Column_4             int,
  primary key (MODULE_ID)
)ENGINE=InnoDB AUTO_INCREMENT=29 DEFAULT CHARSET=utf8;

alter table T_MODULE comment '模块';

/*==============================================================*/
/* Table: T_SERVICE                                             */
/*==============================================================*/
create table T_SERVICE
(
  SERVICE_ID           int not null auto_increment,
  APP_ID               int,
  MODULE_ID            int,
  USER_ID              int,
  SERVICE_CODE         varchar(50),
  VERSION              varchar(50),
  APP_CODE             varchar(50),
  CREATE_TIME           varchar(50),
  SERVICE_NAME           varchar(100),
  primary key (SERVICE_ID)
)ENGINE=InnoDB AUTO_INCREMENT=29 DEFAULT CHARSET=utf8;

create table T_SERVICE_VERSION
(
  VERSION_ID           int not null auto_increment,
  SERVICE_ID               int,
  VERSION              varchar(50),
  APP_CODE             varchar(50),
  STATUS               CHAR(1) comment '0 未审批 1 已审批',
  CREATE_TIME           varchar(50),
  primary key (VERSION_ID)
)ENGINE=InnoDB AUTO_INCREMENT=29 DEFAULT CHARSET=utf8;

/*==============================================================*/
/* Table: T_USER                                                */
/*==============================================================*/
create table T_USER
(
  USER_ID              int not null auto_increment,
  USER_NAME            varchar(50),
  PWD                  varchar(50),
  ROLE                 CHAR(1) comment '1表示 普通员工 2 审核人员 3 管理员',
  DEFAULT_APPID        int,
  CREAT_TIME           varchar(50),
  primary key (USER_ID)
)ENGINE=InnoDB AUTO_INCREMENT=29 DEFAULT CHARSET=utf8;

alter table T_USER comment '用户表';

/*==============================================================*/
/* Table: T_USER_APP                                            */
/*==============================================================*/
create table T_USER_APP
(
  USER_APP_ID          int not null auto_increment,
  USER_ID              int,
  APP_ID               int,
  primary key (USER_APP_ID)
)ENGINE=InnoDB AUTO_INCREMENT=29 DEFAULT CHARSET=utf8;

alter table T_USER_APP comment '用户APP';


alter table T_MODULE add constraint FK_Reference_1 foreign key (APP_ID)
references T_APP (APP_ID) on delete restrict on update restrict;

alter table T_SERVICE add constraint FK_Reference_6 foreign key (APP_ID)
references T_APP (APP_ID) on delete restrict on update restrict;

alter table T_SERVICE add constraint FK_Reference_7 foreign key (MODULE_ID)
references T_MODULE (MODULE_ID) on delete restrict on update restrict;

alter table T_SERVICE add constraint FK_Reference_8 foreign key (USER_ID)
references T_USER (USER_ID) on delete restrict on update restrict;

alter table T_USER add constraint FK_Reference_4 foreign key (DEFAULT_APPID)
references T_APP (APP_ID) on delete restrict on update restrict;

alter table T_USER_APP add constraint FK_Reference_2 foreign key (USER_ID)
references T_USER (USER_ID) on delete restrict on update restrict;

alter table T_USER_APP add constraint FK_Reference_3 foreign key (APP_ID)
references T_APP (APP_ID) on delete restrict on update restrict;