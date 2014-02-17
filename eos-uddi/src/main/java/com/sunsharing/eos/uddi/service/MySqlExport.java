package com.sunsharing.eos.uddi.service;

import com.sunsharing.eos.uddi.db.MysqlUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Connection;

/**
 * Created by criss on 14-2-17.
 */
@org.springframework.stereotype.Service
@Transactional
public class MySqlExport {

    @Autowired
    JdbcTemplate jdbc;

    public void export() throws Exception
    {
        Connection conn = jdbc.getDataSource().getConnection();
        MysqlUtils utils = new MysqlUtils();
        String sql = utils.exportSqlString(conn,new String[]{
            "T_MODULE","T_APP","T_METHOD","T_SERVICE_VERSION",
            "T_SERVICE"
        });
        String sql2 = "select max(APP_ID)+1 from T_APP";
        int i = jdbc.queryForInt(sql2);
        sql+= " ALTER TABLE T_APP AUTO_INCREMENT = "+i+MysqlUtils.enter+MysqlUtils.enter+";";

        sql2 = "select max(MODULE_ID)+1 from T_MODULE";
        i = jdbc.queryForInt(sql2);
        sql+= " ALTER TABLE T_MODULE AUTO_INCREMENT = "+i+MysqlUtils.enter+MysqlUtils.enter+";";

        sql2 = "select max(METHOD_ID)+1 from T_METHOD";
        i = jdbc.queryForInt(sql2);
        sql+= " ALTER TABLE T_METHOD AUTO_INCREMENT = "+i+MysqlUtils.enter+MysqlUtils.enter+";";

        sql2 = "select max(VERSION_ID)+1 from T_SERVICE_VERSION";
        i = jdbc.queryForInt(sql2);
        sql+= " ALTER TABLE T_SERVICE_VERSION AUTO_INCREMENT = "+i+MysqlUtils.enter+MysqlUtils.enter+";";

        sql2 = "select max(SERVICE_ID)+1 from T_SERVICE";
        i = jdbc.queryForInt(sql2);
        sql+= " ALTER TABLE T_SERVICE AUTO_INCREMENT = "+i+MysqlUtils.enter+MysqlUtils.enter+";";

        System.out.println("sql:"+sql);
    }



}
