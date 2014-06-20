#!/bin/bash

DEPLOY_DIR=/opt/eos
CONF_DIR=$DEPLOY_DIR/conf

LIB_DIR=$DEPLOY_DIR/lib
LIB_JARS=`ls $LIB_DIR|grep .jar|awk '{print "'$LIB_DIR'/"$0}'|tr "\n" ":"`

nohup java -Xms64m -Xmx1024m -XX:MaxPermSize=64M  -classpath ../conf:$CONF_DIR:$LIB_JARS  com.sunsharing.eos.manager.main.EosNodup >> /opt/eos/eos.out &
