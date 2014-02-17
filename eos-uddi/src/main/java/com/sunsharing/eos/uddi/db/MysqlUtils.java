package com.sunsharing.eos.uddi.db;

import java.sql.*;

/**
 * Created by criss on 14-2-17.
 */
public class MysqlUtils {
    public static final String enter = "\r\n";

    public String exportSqlString(Connection con,String[] tableNames) throws SQLException {
        StringBuffer tablesql = new StringBuffer();
        for (int i = 0; i < tableNames.length; i++) {
            tablesql.append(exportSqlString(con,tableNames[i]) + enter + enter);
        }
        return tablesql.toString();
    }

    public String exportSqlString(Connection con,String tableName) throws SQLException {
        String tablesql = "";
        String datasql = "";
        Statement stat = null;
        try {
            //con = JdbcManager.getConnection();//得到数据库连接
            stat = con.createStatement();//创建Statement对象
            ResultSet rs = stat.executeQuery("select * from " + tableName);//执行查询语句
            tablesql = getCreateTableSql(rs, tableName);//得到创建表的sql语句
            datasql = getTableDataSql(rs, tableName);//得到插入数据的sql语句
        } catch (SQLException e) {
            throw e;
        } finally {
            //JdbcManager.free(stat, con);
        }
        return tablesql + enter + datasql;
    }

    public String[] getColumns(ResultSet rs) throws SQLException {
    //得到字段的名字，存放到一个数组里
        ResultSetMetaData rsmd = rs.getMetaData();
        int ccount = rsmd.getColumnCount();
        String[] args = new String[ccount];
        for (int i = 1; i <= ccount; i++) {
            String colName = rsmd.getColumnName(i);
            args[i - 1] = colName;
        }
        return args;
    }

    public String getColumnsString(String[] args) {//拼接所有字段名
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < args.length; i++) {
            buffer.append("`" + args[i] + "`,");
        }
        return buffer.deleteCharAt(buffer.length() - 1).toString();
    }

    public String getCreateTableSql(ResultSet rs, String tableName)
            throws SQLException {
        ResultSetMetaData rsmd = rs.getMetaData();//主要的通过这个方法
        int ccount = rsmd.getColumnCount();
        StringBuffer columnBuffer = new StringBuffer("DROP TABLE IF EXISTS `"
                + tableName + "`;" + enter);//为了方便，好多东西是写死的
        columnBuffer.append("CREATE TABLE `" + tableName + "` (" + enter);
        for (int i = 1; i <= ccount; i++) {
            int size = rsmd.getColumnDisplaySize(i);
            String colTypeName = rsmd.getColumnTypeName(i);
            // String colClassName = rsmd.getColumnClassName(i);
            String colName = rsmd.getColumnName(i);
            columnBuffer.append("`" + colName + "` ");
            columnBuffer.append(colTypeName);//在这儿我只做了一些简单的判断
            if (!"double".equalsIgnoreCase(colTypeName)
                    && !"date".equalsIgnoreCase(colTypeName)) {
                columnBuffer.append("(" + size + ") ");
            }
            columnBuffer.append(" DEFAULT NULL," + enter);
        }
        columnBuffer.delete(columnBuffer.length() - 3,
                columnBuffer.length() - 2);
        columnBuffer.append(") ENGINE=InnoDB DEFAULT CHARSET=utf8;");
        return columnBuffer.toString();
    }

    public String getTableDataSql(ResultSet rs, String tableName)
            throws SQLException {
        String[] columns = getColumns(rs);
        StringBuffer columnBuffer = new StringBuffer();
        columnBuffer.append("INSERT INTO `" + tableName + "` ("
                + getColumnsString(columns) + ") VALUES");
        while (rs.next()) {
            columnBuffer.append("(");
            for (int i = 0; i < columns.length; i++) {
                Object obj = rs.getObject(columns[i]);
                String typeName = "";
                if (obj == null) {
                    obj = "";
                }
                if (obj.getClass() != null) {
                    typeName = obj.getClass().getName();
                }//在这儿我只做了一些简单的判断
                if ("java.lang.String".equals(typeName)
                        || "java.sql.Date".equals(typeName)) {
                    columnBuffer.append("'" + obj + "',");
                } else {
                    columnBuffer.append(obj + ",");
                }
            }
            columnBuffer.deleteCharAt(columnBuffer.length() - 1);
            columnBuffer.append("),");
        }
        if (columnBuffer.toString().endsWith("VALUES"))
            return "";
        columnBuffer.deleteCharAt(columnBuffer.length() - 1).append(";");
        return columnBuffer.toString();
    }


}
