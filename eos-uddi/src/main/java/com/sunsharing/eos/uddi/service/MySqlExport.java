package com.sunsharing.eos.uddi.service;

import com.sunsharing.eos.uddi.db.MysqlUtils;
import com.sunsharing.eos.uddi.model.TApp;
import com.sunsharing.eos.uddi.sys.ServiceLocator;
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
import java.util.List;

/**
 * Created by criss on 14-2-17.
 */
@org.springframework.stereotype.Service
@Transactional
public class MySqlExport {

    @Autowired
    JdbcTemplate jdbc;

    public void export(String apps,String uuid) throws Exception
    {
        Connection conn = jdbc.getDataSource().getConnection();
        MysqlUtils utils = new MysqlUtils();
        String sql = utils.exportSqlString(conn,new String[]{
            "T_MODULE","T_APP","T_METHOD","T_SERVICE_VERSION",
            "T_SERVICE"
        },new String[]{ "APP_ID in("+apps+")","APP_ID in ("+apps+")"
        ,"VERSION_ID in (select VERSION_ID from T_SERVICE,T_SERVICE_VERSION where " +
                "T_SERVICE.SERVICE_ID=T_SERVICE_VERSION.VERSION_ID and APP_ID in("+apps+"))",
                "SERVICE_ID in(select SERVICE_ID from T_SERVICE where APP_ID in("+apps+"))","APP_ID in("+apps+")"});
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
        AppService service = (AppService)ServiceLocator.getBean("appService");
        List<TApp> tapps = service.getAppName(apps);
        initPath(tapps,uuid);
        new File(SysInit.path+ File.separator+"zip"+
                File.separator+uuid).mkdirs();
        FileOutputStream f  = new FileOutputStream(new File(SysInit.path+ File.separator+"zip"+File.separator
                +uuid+
                File.separator+"data.sql"
        ));
        f.write(sql.getBytes());
        f.close();
    }

    private void initPath(List<TApp> apps,String uuid)
    {
        String zip = SysInit.path+ File.separator+"zip";
        File zipPath = new File(zip);
        if(zipPath.exists())
        {
            System.out.println("zipPath:"+zipPath);
            deleteDir(zipPath);
        }
        for(int i=0;i<apps.size();i++)
        {
            TApp app = apps.get(i);
            new File(SysInit.path+ File.separator+"zip"+File.separator+uuid+File.separator+"interface"+File.separator+
                    app.getAppCode()).mkdirs();
            copyFolder(SysInit.path + File.separator + "interface"+File.separator+app.getAppCode(),
                SysInit.path +  "zip"+File.separator+ uuid + File.separator + "interface"+File.separator+app.getAppCode());
        }
    }


    /**
     * 复制整个文件夹内容
     * @param oldPath String 原文件路径 如：c:/fqf
     * @param newPath String 复制后路径 如：f:/fqf/ff
     * @return boolean
     */
    public void copyFolder(String oldPath, String newPath) {
        System.out.println(oldPath);
        System.out.println(newPath);

        try {
            (new File(newPath)).mkdirs(); //如果文件夹不存在 则建立新文件夹
            File a=new File(oldPath);
            String[] file=a.list();
            if(file==null)
            {
                file = new String[]{};
            }
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
