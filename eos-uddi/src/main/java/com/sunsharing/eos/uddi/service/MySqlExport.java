package com.sunsharing.eos.uddi.service;

import com.sunsharing.eos.uddi.db.MysqlUtils;
import com.sunsharing.eos.uddi.sys.SysInit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
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
        sql+= " ALTER TABLE T_APP AUTO_INCREMENT = "+i+";"+MysqlUtils.enter+MysqlUtils.enter;

        sql2 = "select max(MODULE_ID)+1 from T_MODULE";
        i = jdbc.queryForInt(sql2);
        sql+= " ALTER TABLE T_MODULE AUTO_INCREMENT = "+i+";"+MysqlUtils.enter+MysqlUtils.enter;

        sql2 = "select max(METHOD_ID)+1 from T_METHOD";
        i = jdbc.queryForInt(sql2);
        sql+= " ALTER TABLE T_METHOD AUTO_INCREMENT = "+i+";"+MysqlUtils.enter+MysqlUtils.enter;

        sql2 = "select max(VERSION_ID)+1 from T_SERVICE_VERSION";
        i = jdbc.queryForInt(sql2);
        sql+= " ALTER TABLE T_SERVICE_VERSION AUTO_INCREMENT = "+i+";"+MysqlUtils.enter+MysqlUtils.enter;

        sql2 = "select max(SERVICE_ID)+1 from T_SERVICE";
        i = jdbc.queryForInt(sql2);
        sql+= " ALTER TABLE T_SERVICE AUTO_INCREMENT = "+i+";"+MysqlUtils.enter+MysqlUtils.enter;

        System.out.println("sql:"+sql);
        initPath();
        FileOutputStream f  = new FileOutputStream(new File(SysInit.path+ File.separator+"zip"+
                File.separator+"data.sql"
        ));
        f.write(sql.getBytes());
        f.close();
    }

    private void initPath()
    {
        String zip = SysInit.path+ File.separator+"zip";
        File zipPath = new File(zip);
        if(zipPath.exists())
        {
            deleteDir(zipPath);
        }
        new File(SysInit.path+ File.separator+"zip"+File.separator+"interface").mkdirs();
        copyFolder(SysInit.path + File.separator + "interface",
                SysInit.path + File.separator + "zip" + File.separator + "interface");
    }


    /**
     * 复制整个文件夹内容
     * @param oldPath String 原文件路径 如：c:/fqf
     * @param newPath String 复制后路径 如：f:/fqf/ff
     * @return boolean
     */
    public void copyFolder(String oldPath, String newPath) {

        try {
            (new File(newPath)).mkdirs(); //如果文件夹不存在 则建立新文件夹
            File a=new File(oldPath);
            String[] file=a.list();
            File temp=null;
            for (int i = 0; i < file.length; i++) {
                if(oldPath.endsWith(File.separator)){
                    temp=new File(oldPath+file[i]);
                }
                else{
                    temp=new File(oldPath+File.separator+file[i]);
                }

                if(temp.isFile()){
                    FileInputStream input = new FileInputStream(temp);
                    FileOutputStream output = new FileOutputStream(newPath + "/" +
                            (temp.getName()).toString());
                    byte[] b = new byte[1024 * 5];
                    int len;
                    while ( (len = input.read(b)) != -1) {
                        output.write(b, 0, len);
                    }
                    output.flush();
                    output.close();
                    input.close();
                }
                if(temp.isDirectory()){//如果是子文件夹
                    copyFolder(oldPath+"/"+file[i],newPath+"/"+file[i]);
                }
            }
        }
        catch (Exception e) {
            System.out.println("复制整个文件夹内容操作出错");
            e.printStackTrace();

        }

    }

    private static void doDeleteEmptyDir(String dir) {



        boolean success = (new File(dir)).delete();



        if (success) {

            System.out.println("Successfully deleted empty directory: " + dir);

        } else {

            System.out.println("Failed to delete empty directory: " + dir);

        }



    }



    /**

     * Deletes all files and subdirectories under "dir".

     * @param dir Directory to be deleted

     * @return boolean Returns "true" if all deletions were successful.

     *                 If a deletion fails, the method stops attempting to

     *                 delete and returns "false".

     */

    private static boolean deleteDir(File dir)
    {
        if (dir.isDirectory()) {

            String[] children = dir.list();

            for (int i=0; i<children.length; i++) {

                boolean success = deleteDir(new File(dir, children[i]));

                if (!success) {

                    return false;

                }

            }

        }
        return false;
    }
}
