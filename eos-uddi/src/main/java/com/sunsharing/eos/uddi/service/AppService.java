package com.sunsharing.eos.uddi.service;

import com.sunsharing.component.utils.base.DateUtils;
import com.sunsharing.eos.common.utils.StringUtils;
import com.sunsharing.eos.uddi.dao.SimpleHibernateDao;
import com.sunsharing.eos.uddi.model.*;
import com.sunsharing.eos.uddi.sys.SysInit;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by criss on 14-1-31.
 */
@Service
@Transactional
public class AppService {

    private SimpleHibernateDao<TApp,Integer> appDao;//用户管理
    private SimpleHibernateDao<TModule,String> moduleDao;//用户管理
    private SimpleHibernateDao<TService, Integer> serviceDao;

    @Autowired
    public void setSessionFactory(SessionFactory sessionFactory){
        appDao = new SimpleHibernateDao<TApp,Integer>(sessionFactory,TApp.class);
        moduleDao = new SimpleHibernateDao<TModule,String>(sessionFactory,TModule.class);
        serviceDao = new SimpleHibernateDao<TService,Integer>(sessionFactory,TService.class);
    }

    public List getAppName(String apps)
    {
        String sql = "from TApp where appId in("+apps+")";
       return appDao.find(sql);
    }

    public List<TApp> listApp()
    {
        String app = "from TApp order by creatTime desc";
        return appDao.find(app);
    }

    public void saveApp(String app_en,String app_cn,String modules)
    {
        TApp app = new TApp();
        app.setAppCode(app_en);
        app.setAppName(app_cn);
        app.setCreatTime(DateUtils.getDBString(new Date()));


        String[] moduleArr = modules.split(",");
        for(int i=0;i<moduleArr.length;i++)
        {
            TModule module = new TModule();
            module.setApp(app);
            module.setModuleName(moduleArr[i]);
            app.getModules().add(module);
            //moduleDao.save(module);
        }
        appDao.save(app);
    }

    public void updateApp(String appId,String app_en,String app_cn,String modules)
    {
        TApp app = appDao.get(new Integer(appId));
        app.setAppCode(app_en);
        app.setAppName(app_cn);

        app.getModules().clear();

        String[] moduleArr = modules.split(",");
        for(int i=0;i<moduleArr.length;i++)
        {
            TModule module = new TModule();
            module.setApp(app);
            module.setModuleName(moduleArr[i]);
            app.getModules().add(module);
            //moduleDao.save(module);
        }
    }

    public TApp loadApp(String id)
    {
        return appDao.get(new Integer(id));
    }

    public void changeJava(String appId,String dirId)
    {
//        TApp app = appDao.get(new Integer(appId));
//        String appCode = app.getAppCode();
        String sql = "from TService where appId='"+appId+"'";
        List<TService> services = serviceDao.find(sql);
        String dirPath = SysInit.path+ File.separator+"jartmp"+File.separator+dirId;
        if(!new File(dirPath).exists())
        {
            new File(dirPath).mkdirs();
        }

        nioTransferCopy(new File(SysInit.path+File.separator+"compile.bat"),
                new File(dirPath+File.separator+"compile.bat"));
        FileOutputStream out = null;
        try
        {
            out = new FileOutputStream(dirPath+File.separator+"compile.bat",true);
            for(TService ser:services)
            {
                List<TServiceVersion> versions = ser.getVersions();
                String max = "";
                TServiceVersion maxVersion = null;
                for(TServiceVersion v:versions)
                {
                    if(v.getStatus().equals("1"))
                    {
                        if(v.getVersion().compareTo(max)>0)
                        {
                            maxVersion = v;
                        }
                    }
                }
                if(maxVersion!=null)
                {
                    String sourceApp = SysInit.path+ File.separator+"interface"+
                            File.separator+ser.getAppCode()+File.separator+ser.getServiceCode()+"_"+
                            maxVersion.getVersion()+".java";
                    boolean isExist = new File(sourceApp).exists();
                    if(isExist)
                    {
                        String pkg = getPakage(new File(sourceApp));
                        String tmp = dirPath+File.separator+pkg;
                        if(!new File(tmp).exists())
                        {
                            new File(tmp).mkdirs();
                        }
                        String sl = ser.getServiceCode().substring(0,1).toUpperCase()+
                                ser.getServiceCode().substring(1);
                        if(!StringUtils.isBlank(pkg))
                        {
                            pkg = pkg+File.separator;
                        }
                        nioTransferCopy(new File(sourceApp),new File(tmp+File.separator+sl+".java"));
                        String javac = "d:\\jdk1.6\\bin\\javac -encoding utf-8   -classpath %LIB_JARS% "+dirPath+File.separator+pkg+sl+".java\n";
                        out.write(javac.getBytes());
                    }
                }

            }

            out.write("del lock\n".getBytes());
        }catch (Exception e)
        {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }finally {
            try
            {
                out.close();
            }catch (Exception e)
            {

            }
        }

        //执行编译打包
        run(dirPath + File.separator + "compile.bat " + SysInit.path.substring(0,SysInit.path.length()-6)+"lib");
        run(SysInit.path+ File.separator+"myjar.bat "+dirId+" "+dirPath+File.separator+dirId+".jar");
        new File(dirPath + File.separator + "compile.bat ").delete();
    }
    private void runNoExist(String cmd)
    {
        System.out.println(cmd);
        Runtime run = Runtime.getRuntime();//返回与当前 Java 应用程序相关的运行时对象
        try {
            Process p = run.exec(cmd);// 启动另一个进程来执行命令
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void run(String cmd)
    {
        System.out.println(cmd);
        Runtime run = Runtime.getRuntime();//返回与当前 Java 应用程序相关的运行时对象
        try {
            Process p = run.exec(cmd);// 启动另一个进程来执行命令
            BufferedInputStream in = new BufferedInputStream(p.getInputStream());
            BufferedReader inBr = new BufferedReader(new InputStreamReader(in));
            String lineStr;
            while ((lineStr = inBr.readLine()) != null)
                //获得命令执行后在控制台的输出信息
                System.out.println(lineStr);// 打印输出信息
            //检查命令是否执行失败。
            if (p.waitFor() != 0) {
                if (p.exitValue() == 1)//p.exitValue()==0表示正常结束，1：非正常结束
                    System.err.println("命令执行失败!");
            }
            inBr.close();
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private  String getPakage(File source)
    {
        BufferedReader reader = null;
        try
        {
            //reader = new BufferedReader(new FileReader(source));
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(source),"UTF-8"));
            String line = "";
            while((line = reader.readLine())!=null)
            {
               if( line.trim().startsWith("package"))
               {
                   System.out.println(line.trim());
                   String pk = line.trim().substring(7).trim();
                   System.out.println(pk);
                   pk = pk.substring(0,pk.length()-1);
                   System.out.println(pk);
                   if(File.separator.equals("\\"))
                   {
                       return pk.replaceAll("\\.",File.separator+File.separator);
                   }else
                   {
                       return pk.replaceAll("\\.",File.separator);
                   }

               }
            }
        }catch (Exception e)
        {
            e.printStackTrace();
        }finally {
            try
            {
                reader.close();
            }catch (Exception e)
            {

            }
        }
        return "";
    }

    private  void nioTransferCopy(File source, File target) {
        FileChannel in = null;
        FileChannel out = null;
        FileInputStream inStream = null;
        FileOutputStream outStream = null;
        try {
            inStream = new FileInputStream(source);
            outStream = new FileOutputStream(target);
            in = inStream.getChannel();
            out = outStream.getChannel();
            in.transferTo(0, in.size(), out);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try{
                inStream.close();
            }catch (Exception e)
            {

            }
            try
            {
                in.close();
            }catch (Exception e)
            {

            }
            try
            {
                outStream.close();
            }catch (Exception e)
            {

            }
            try
            {
                out.close();
            }catch (Exception e)
            {

            }
        }
    }

    public static void main(String[]a)
    {
        String ki = "com.sunsharing.componetent.test".replaceAll("\\.","\\\\");
        System.out.println(ki);
    }

}
