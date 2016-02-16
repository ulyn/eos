package com.sunsharing.eos.uddi.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sunsharing.component.utils.base.DateUtils;
import com.sunsharing.eos.common.Constants;
import com.sunsharing.eos.common.utils.StringUtils;
import com.sunsharing.eos.common.zookeeper.PathConstant;
import com.sunsharing.eos.common.zookeeper.ZookeeperUtils;
import com.sunsharing.eos.uddi.dao.SimpleHibernateDao;
import com.sunsharing.eos.uddi.model.*;
import com.sunsharing.eos.uddi.sys.SysInit;
import com.sunsharing.msgcenter.productClient.MsgSendClient;
import org.apache.log4j.Logger;
import org.apache.zookeeper.CreateMode;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.util.*;

/**
 * Created by criss on 14-2-1.
 */
@org.springframework.stereotype.Service
@Transactional
public class Service {

    private SimpleHibernateDao<TService, Integer> serviceDao;//用户管理
    private SimpleHibernateDao<TApp, Integer> appDao;//用户管理
    private SimpleHibernateDao<TServiceVersion, Integer> versionDao;//用户管理
    private SimpleHibernateDao<TUser, Integer> userDao;//用户管理
    private SimpleHibernateDao<TUserApp, Integer> userAppDao;//用户管理
    private SimpleHibernateDao<TMethod, Integer> methodDao;//用户管理
    private SimpleHibernateDao<TModule, Integer> moduleDao;//用户管理


    @Autowired
    public void setSessionFactory(SessionFactory sessionFactory) {
        serviceDao = new SimpleHibernateDao<TService, Integer>(sessionFactory, TService.class);
        appDao = new SimpleHibernateDao<TApp, Integer>(sessionFactory, TApp.class);
        versionDao = new SimpleHibernateDao<TServiceVersion, Integer>(sessionFactory, TServiceVersion.class);
        userDao = new SimpleHibernateDao<TUser, Integer>(sessionFactory, TUser.class);
        userAppDao = new SimpleHibernateDao<TUserApp, Integer>(sessionFactory, TUserApp.class);
        methodDao = new SimpleHibernateDao<TMethod, Integer>(sessionFactory, TMethod.class);
        moduleDao = new SimpleHibernateDao<TModule, Integer>(sessionFactory, TModule.class);
    }

    public List<TService> query(String appId, String module) {
        String sql = "from TService where appId=?";
        if ("0".equals(module)) {
            module = "";
        }
        if (!StringUtils.isBlank(module)) {
            sql += " and module=?";
            TModule m = moduleDao.get(new Integer(module));
            List<TService> list = serviceDao.find(sql, new Integer(appId), m.getModuleName());
            for(TService service:list)
            {
                service.getUser().getUserName();
            }
            return list;
        } else {
            List<TService> list = serviceDao.find(sql, new Integer(appId));
            for(TService service:list)
            {
               System.out.println("..:" + service.getUser().getUserName());
            }
              
            return list;
        }
    }

    public void saveService(String servicename, String appId, String module, String[] lines, int userId,Map functionMap) throws Exception {
        TApp app = appDao.get(new Integer(appId));

        InterfaceServcie s = new InterfaceServcie();
        String version = s.getVersion(lines);
        String infaceName = s.getInterfaceName(lines);

        String sql = "from TServiceVersion where status='1' and appCode=? and service.serviceCode=? and version=?";
        List<TServiceVersion> l2 = versionDao.find(sql, app.getAppCode(), infaceName, version);
        if (l2.size() > 0) {
            throw new RuntimeException("该服务已经审批通过，不能再创建，请更改版本号");
        }
        sql = "from TServiceVersion where  appCode=? and service.serviceCode=? and version=?";
        l2 = versionDao.find(sql, app.getAppCode(), infaceName, version);
        TService service = null;
        TServiceVersion v = null;
        if (l2.size() > 0) {
            TServiceVersion ss = l2.get(0);
            v = ss;
        }
        sql = "from TService where appCode=? and serviceCode=?";
        List<TService> l3 = serviceDao.find(sql, app.getAppCode(), infaceName);
        if (l3.size() > 0) {
            service = l3.get(0);
        }

        TUser user = userDao.get(new Integer(userId));
        if (service == null)
            service = new TService();
        service.setAppCode(app.getAppCode());
        service.setAppId(app.getAppId());
        service.setModule(module);
        service.setServiceCode(infaceName);
        service.setServiceName(servicename);
        service.setUser(user);
        service.setCreateTime(DateUtils.getDBString(new Date()));
        service.setTest("0");

        if (v == null)
            v = new TServiceVersion();
        v.setStatus("0");
        v.setCreateTime(DateUtils.getDBString(new Date()));
        v.setVersion(version);
        v.setAppCode(app.getAppCode());
        v.setService(service);
        v.getMethods().clear();
        service.getVersions().add(v);

        Map m = s.getFunction(lines);
        Set<String> keys = m.keySet();
        List<String> allVoidFunction = s.getAllVoidFuntions(lines);
        for (String fun : keys) {
            TMethod me = new TMethod();
            me.setVersion(v);
            me.setMethodName(fun);
            me.setParams((String)functionMap.get(fun));
            Map ll = (Map) m.get(fun);
            Collection tmp = ll.values();
            me.setMockResult(JSONArray.toJSONString(tmp));
            v.getMethods().add(me);
            if(allVoidFunction.contains(fun))
            {
                allVoidFunction.remove(fun);
            }
        }

        for(String fun:allVoidFunction)
        {
            TMethod me = new TMethod();
            me.setVersion(v);
            me.setMethodName(fun);
            me.setMockResult("[{\"status\":\""+ Constants.MOCK_VOID+"\",\"content\":\"\"}]");
            v.getMethods().add(me);
        }

        if (service.getServiceId() == 0) {
            serviceDao.saveOrUpdate(service);
        }
        versionDao.saveOrUpdate(v);
        ZookeeperUtils utils = ZookeeperUtils.getInstance();
        if (utils.isExists(PathConstant.ACL + "/" + (app.getAppCode() + infaceName +"/"+version),false)) {
            utils.deleteNode(PathConstant.ACL + "/" + (app.getAppCode() + infaceName +"/"+ version));
        }

        //发送邮件通知
        sql = "from TUserApp where app.appId=?";
        List<TUserApp> list = userAppDao.find(sql,new Integer(appId));
        for(TUserApp userApp : list)
        {
            final String email = userApp.getUser().getEamil();
            final String content = user.getUserName()+"于"+DateUtils.getDisplay(new Date())+"更新了"+service.getServiceName()+
                    "["+service.getServiceCode()+"]，最新的版本号为"+version+"," +
                    "请小组长即时处理";
            final String title = app.getAppName()+"["+app.getAppCode()+"]服务版本更新通知";
            if(!StringUtils.isBlank(email))
            {
                Runnable run = new Runnable(){
                    public void run()
                    {
                        sendEmail(email,title,content);
                    }
                };
                new Thread(run).start();
            }

        }

    }

    public void deleteService(String serviceId)
    {
        TService service = serviceDao.get(new Integer(serviceId));
//        List<TServiceVersion> versions = service.getVersions();
//        for(TServiceVersion version:versions)
//        {
//            List<TMethod> methods = version.getMethods();
//            methods.clear();
//            for(TMethod method:methods)
//            {
//                methodDao.delete(method);
//            }
//            versionDao.delete(version);
//        }
        serviceDao.delete(service);
    }

    private void sendEmail(String email,String title,String content)
    {
        JSONObject jo = new JSONObject();
        jo.put("topic","FT_SUN");
        jo.put("expires","10");
        jo.put("email",email);
        jo.put("title",title);
        jo.put("content",content);
        MsgSendClient send = new MsgSendClient();
        send.sendMsg(jo.toJSONString());
    }

    public List<Object[]> seachmethod(String appId, String serviceId, String version) {
        String sql = "select methodId,methodName,mockResult,params from TMethod where  version.appCode=? and version.service.serviceId=? and version.version=?";
        TApp app = appDao.get(new Integer(appId));
        Query query = versionDao.createQuery(sql, app.getAppCode(), new Integer(serviceId), version);
        return query.list();
    }

    public List<TServiceVersion> searchVersion(String serviceId)
    {
        String sql = "from TServiceVersion where service.serviceId=?";
        List<TServiceVersion> list = versionDao.find(sql,new Integer(serviceId));
        return list;
    }

    public void copyMock(String fromMethodId,String toMethodId)
    {
        //String sql = "select MOCK_RESULT from T_METHOD where METHOD_ID =  "+fromMethodId;
        TMethod oldMethod = methodDao.get(new Integer(fromMethodId));
        TMethod newMethod = methodDao.get(new Integer(toMethodId));
        //String sql = "update T_SERVICE_VERSION set ";
        newMethod.setMockResult(oldMethod.getMockResult());
        methodDao.update(newMethod);
    }

    public void saveMethod(String methodId, String status, String content) {
        TMethod method = methodDao.get(new Integer(methodId));
        String result = method.getMockResult();
        JSONArray array = JSONArray.parseArray(result);
        for (int i = 0; i < array.size(); i++) {
            JSONObject obj = (JSONObject) array.get(i);
            if (obj.getString("status").equals(status)) {
                obj.put("content", content);
            }
        }
        method.setMockResult(array.toJSONString());
        methodDao.saveOrUpdate(method);


    }

    public String getName(String versionId) {
        TServiceVersion version = versionDao.get(new Integer(versionId));
        return version.getService().getServiceCode();
    }

    public String getJava(String versionId) throws Exception {
        TServiceVersion version = versionDao.get(new Integer(versionId));
        String version2 = version.getVersion();
        String serviceCode = version.getService().getServiceCode();
        String appcode = version.getAppCode();

        String destName = SysInit.path + File.separator + "interface" + File.separator +
                appcode + File.separator + serviceCode + "_" + version2 + ".java";
        StringBuffer sb = new StringBuffer();

        FileInputStream input = null;
        ByteArrayOutputStream arrOut = new ByteArrayOutputStream();
        try {
            input = new FileInputStream(new File(destName));
            byte[] arr = new byte[1024];
            int len = 0;

            while ((len = input.read(arr)) != -1) {
                arrOut.write(arr, 0, len);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (input != null) {
                input.close();
            }
        }
        String result = new String(arrOut.toByteArray(), "UTF-8");


        return result;
    }

    public void commitAllCommit()throws Exception
    {
        String sql = "from TServiceVersion where status='1'";
        List<TServiceVersion> versions = versionDao.find(sql);
        for(TServiceVersion v:versions)
        {
            commit(v.getVersionId()+"");
        }
    }

    public void commit(String versionId) throws Exception {
        TServiceVersion version = versionDao.get(new Integer(versionId));
        version.setStatus("1");

        String appCode = version.getAppCode();
        String serviceId = version.getService().getServiceCode();
        String ver = version.getVersion();

        List<TMethod> methods = version.getMethods();
        JSONObject obj = new JSONObject();
        for (TMethod me : methods) {
            String arr = "";
            if (StringUtils.isBlank(me.getMockResult())) {
                arr = "[]";
            } else {
                arr = me.getMockResult();
            }
            obj.put(me.getMethodName(), JSONArray.parseArray(arr));
        }
        ZookeeperUtils utils = ZookeeperUtils.getInstance();
        utils.createNode(PathConstant.ACL, "", CreateMode.PERSISTENT);
        utils.createNode(PathConstant.ACL + "/" + (appCode + serviceId),"" , CreateMode.PERSISTENT);
        utils.createNode(PathConstant.ACL + "/" + (appCode + serviceId)+"/"+ver,obj.toJSONString() ,
                CreateMode.PERSISTENT);

        //发送邮件通知
        String sql = "from TUserApp where app.appId=?";
        List<TUserApp> list = userAppDao.find(sql,new Integer(version.getService().getAppId()));
        sql = "from TApp where appId=?";
        List<TApp> apps = appDao.find(sql,version.getService().getAppId());
        for(TUserApp userApp : list)
        {
            final String email = userApp.getUser().getEamil();
            final String content = version.getService().getServiceName()+
                    "["+version.getService().getServiceCode()+"]的版本号为"+ver+"已经审批通过," +
                    "请相关人员即时处理";
            final String title = apps.get(0).getAppName()+"["+apps.get(0).getAppCode()+"]服务审批通知";
            if(!StringUtils.isBlank(email))
            {
                Runnable run = new Runnable(){
                    public void run()
                    {
                        sendEmail(email,title,content);
                    }
                };
                new Thread(run).start();
            }

        }

    }

    Logger logger = Logger.getLogger(Service.class);

    public void addTestCode(String methodId,
                            String status,String desc,String content)
    {
        TMethod method = methodDao.get(new Integer(methodId));
        JSONArray array = JSONArray.parseArray(method.getMockResult());
        JSONObject obj = null;

        for(int i=0;i<array.size();i++)
        {
            String  sta = array.getJSONObject(i).getString("status");
            if(sta.equals(status))
            {
                obj = array.getJSONObject(i);
                if(StringUtils.isBlank(desc)) {
                    desc = array.getJSONObject(i).getString("desc");
                }
                obj.put("desc",desc);
                obj.put("content",content);

                break;
            }
        }
        if(obj==null){
            obj = new JSONObject();
            obj.put("status",status);
            obj.put("desc",desc);
            obj.put("content",content);
            array.add(obj);
        }


        method.setMockResult(array.toString());
        methodDao.update(method);

        try {
            updateTestCode(methodId);
        }catch (Exception e)
        {
            logger.error("",e);
        }

    }

    public String[] getMock(String methodId)
    {
        List<String> mock = new ArrayList<String>();
        TMethod method = methodDao.get(new Integer(methodId));
        JSONArray array = JSONArray.parseArray(method.getMockResult());
        for(int i=0;i<array.size();i++)
        {
            JSONObject jsonObject = array.getJSONObject(i);
            String sta = jsonObject.getString("status");
            String de = jsonObject.getString("desc");
            mock.add("${" + sta + "}" + de);
            String con = jsonObject.getString("content");
            con = con.replaceAll("：",":");
            con = con.replaceAll("“","\"");
            con = con.replaceAll("”","\"");
            con = con.replaceAll("，",",");
            con = con.replaceAll("\\{","{");
            con = con.replaceAll("\\}","}");
            con = con.replaceAll("\\【","[");
            con = con.replaceAll("\\】","]");
            if(con.startsWith("{"))
            {
                String l = (JSON.toJSONString(JSONObject.parseObject(con), true));
                String arr [] =  l.split("\n");
                for(int j=0;j<arr.length;j++)
                {
                    mock.add(arr[j]);
                }
            }else if(con.startsWith("["))
            {
                JSONArray arrayTmp = JSONArray.parseArray(con);
                String l = JSONArray.toJSONString(arrayTmp,true);
                String arr [] =  l.split("\n");
                for(int j=0;j<arr.length;j++)
                {
                    mock.add(arr[j]);
                }
            }
            else
            {
                mock.add(con);
            }
            mock.add(" ");

        }
        return mock.toArray(new String[]{});
    }


    public void save2JavaFile(String methodId,String[] mocks)
    {
        TMethod method = methodDao.get(new Integer(methodId));
        String methodName = method.getMethodName();
        String appId = method.getVersion().getAppCode();
        String serviceId = method.getVersion().getService().getServiceCode();
        String version = method.getVersion().getVersion();

        File path = new File(SysInit.path + File.separator + "interface" + File.separator + appId
        + File.separator+serviceId+"_"+version+".java");
        BufferedReader reader = null;
        FileOutputStream w = null;
        String [] result = null;
        try {
            //reader = new BufferedReader(new FileReader(source));
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(path), "UTF-8"));
            List<String> str2 = new ArrayList<String>();
            String line = "";
            while ((line = reader.readLine()) != null) {
                str2.add(line);
            }
            InterfaceServcie servcie = new InterfaceServcie();
            result = servcie.synMock(methodName,str2.toArray(new String[]{}),mocks);

        } catch (Exception e) {
            logger.error("", e);
            throw new RuntimeException(e);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                }catch (Exception e)
                {

                }
            }
            if (w != null) {
                try {
                    w.close();
                }catch (Exception e)
                {

                }
            }
        }
        boolean rst = false;
        int i = 0;
        while(!rst)
        {
            i++;
            rst = path.delete();
            try {
                Thread.sleep(100);
            }catch (Exception e)
            {

            }
            if(i>20)
            {
                break;
            }
        }
        FileOutputStream out = null;
        try
        {
            out = new FileOutputStream(path);
            for(int j=0;j<result.length;j++)
            {
                out.write(result[j].getBytes("UTF-8"));
                out.write("\n".getBytes("UTF-8"));
            }

        }catch (Exception e)
        {
            logger.error("", e);
            throw new RuntimeException(e);
        }finally {
            try {
                out.close();
            }catch (Exception e)
            {

            }
        }


    }

    public void deleteTestCode(String methodId,String status)
    {
        TMethod method = methodDao.get(new Integer(methodId));
        JSONArray array = JSONArray.parseArray(method.getMockResult());
        for(int i=0;i<array.size();i++)
        {
            String  sta = array.getJSONObject(i).getString("status");
            if(sta.equals(status))
            {
                array.remove(i);
                break;
            }
        }
        method.setMockResult(array.toString());
        methodDao.update(method);

        try {
            updateTestCode(methodId);
        }catch (Exception e)
        {
            logger.error("",e);
        }
    }

    public void updateTestCode(String methodId) throws Exception {
        TMethod method = methodDao.get(new Integer(methodId));
        if (method.getVersion().getStatus().equals("0")) {
            throw new RuntimeException("未审批");
        }
        List<TMethod> methods = method.getVersion().getMethods();
        String appCode = method.getVersion().getAppCode();
        String serviceId = method.getVersion().getService().getServiceCode();
        String ver = method.getVersion().getVersion();
        JSONObject obj = new JSONObject();
        for (TMethod me : methods) {
            String arr = "";
            if (StringUtils.isBlank(me.getMockResult())) {
                arr = "[]";
            } else {
                arr = me.getMockResult();
            }
            obj.put(me.getMethodName(), JSONArray.parseArray(arr));
        }


        ZookeeperUtils utils = ZookeeperUtils.getInstance();
        utils.createNode(PathConstant.ACL, "", CreateMode.PERSISTENT);
        utils.createNode(PathConstant.ACL + "/" + (appCode + serviceId),"" , CreateMode.PERSISTENT);
        utils.createNode(PathConstant.ACL + "/" + (appCode + serviceId)+"/"+ver,obj.toJSONString() ,
                CreateMode.PERSISTENT);

    }

    public void changeTest(String versionId) {
        TServiceVersion version = versionDao.get(new Integer(versionId));
        TService service = version.getService();
        service.setTest("1");
        serviceDao.update(service);
    }

}
