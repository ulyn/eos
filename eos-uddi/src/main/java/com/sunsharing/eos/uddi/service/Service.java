package com.sunsharing.eos.uddi.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sunsharing.component.utils.base.DateUtils;
import com.sunsharing.component.utils.crypto.Md5;
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
        String sql = "from TService where appId=?  ";
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

    public void saveService(String servicename, String appId, String module,String[] lines, int userId,Map functionMap) throws Exception {
        TApp app = appDao.get(new Integer(appId));

        InterfaceServcie s = new InterfaceServcie();
        Map methondVersion = s.getFuntionVersion(lines);
        String infaceName = s.getInterfaceName(lines);

        String sv = getServiceVersion(methondVersion);

        String sql = "from TServiceVersion where status='1' and appCode=? and service.serviceCode=? and serviceVersion=?";
        List<TServiceVersion> l2 = versionDao.find(sql, app.getAppCode(), infaceName, sv);
        if (l2.size() > 0) {
            throw new RuntimeException("该服务已经审批通过，不能再创建，请更改版本号");
        }
        sql = "from TServiceVersion where  appCode=? and service.serviceCode=? and serviceVersion=?";
        l2 = versionDao.find(sql, app.getAppCode(), infaceName, sv);
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
        v.setServiceVersion(sv);
        v.setAppCode(app.getAppCode());
        v.setService(service);
        v.getMethods().clear();
        service.getVersions().add(v);

        Map m = s.getFunction(lines);
        Set<String> keys = m.keySet();
        List<String> allVoidFunction = s.getAllVoidFuntions(lines);
        for (String fun : keys) {
            TMethod me = new TMethod();
            me.setVersionObj(v);
            me.setMethodVersion((String)methondVersion.get(fun));
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
            me.setVersionObj(v);
            me.setMethodVersion((String)methondVersion.get(fun));
            me.setMethodName(fun);
            me.setMockResult("[{\"status\":\""+ Constants.MOCK_VOID+"\",\"content\":\"\"}]");
            v.getMethods().add(me);
        }

        if (service.getServiceId() == 0) {
            serviceDao.saveOrUpdate(service);
        }
        versionDao.saveOrUpdate(v);


        //发送邮件通知
        sql = "from TUserApp where app.appId=?";
        List<TUserApp> list = userAppDao.find(sql,new Integer(appId));
        for(TUserApp userApp : list)
        {
            final String email = userApp.getUser().getEamil();
            final String content = user.getUserName()+"于"+DateUtils.getDisplay(new Date())+"更新了"+service.getServiceName()+
                    "["+service.getServiceCode()+"]，最新的版本号为," +
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
    public String getServiceVersion(String[] lines)
    {
        InterfaceServcie s = new InterfaceServcie();
        Map methodVersion = s.getFuntionVersion(lines);
        return getServiceVersion(methodVersion);
    }
    public String getServiceVersion(TServiceVersion version)
    {
        List<TMethod> methods = version.getMethods();
        Map methodVersion = new HashMap();
        for(TMethod method:methods)
        {
            String methodName = method.getMethodName();
            String v = method.getMethodVersion();
            methodVersion.put(methodName,v);
        }
        return getServiceVersion(methodVersion);
    }
    private String getServiceVersion(Map methodVersion)
    {
        List<String> methodList = new ArrayList(methodVersion.keySet());
        Collections.sort(methodList);
        String source = "";
        for(String m:methodList)
        {
            source+=m+methodVersion.get(m);
        }
        return Md5.MD5(source);
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
        String sql = "select methodId,methodName,mockResult,params,methodVersion from TMethod where  versionObj.appCode=? and versionObj.service.serviceId=? and versionObj.serviceVersion=?";
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

    public File getJavaFile(String versionId){
        TServiceVersion version = versionDao.get(new Integer(versionId));
        String version2 = version.getServiceVersion();
        String serviceCode = version.getService().getServiceCode();
        String appcode = version.getAppCode();

        String destName = SysInit.path + File.separator + "interface" + File.separator +
                appcode + File.separator + serviceCode + "_" + version2 + ".java";
        return new File(destName);
    }

    public String getJava(String versionId) throws Exception {
        File file = getJavaFile(versionId);
        StringBuffer sb = new StringBuffer();

        FileInputStream input = null;
        ByteArrayOutputStream arrOut = new ByteArrayOutputStream();
        try {
            input = new FileInputStream(file);
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
        //String ver = version.getVersion();

        ZookeeperUtils utils = ZookeeperUtils.getInstance();
        utils.createNode(PathConstant.ACL, "", CreateMode.PERSISTENT);
        //utils.createNode(PathConstant.ACL + "/" + (appCode + serviceId),"" , CreateMode.PERSISTENT);
        List<TMethod> methods = version.getMethods();
        for (TMethod me : methods) {
            String arr = "";
            if (StringUtils.isBlank(me.getMockResult())) {
                arr = "[]";
            } else {
                arr = me.getMockResult();
            }
            JSONArray array = JSONArray.parseArray(arr);
            utils.createNode(PathConstant.ACL + "/" + (appCode + serviceId)+me.getMethodName()+me.getMethodVersion(),array.toJSONString() ,
                    CreateMode.PERSISTENT);
        }

        //发送邮件通知
        String sql = "from TUserApp where app.appId=?";
        List<TUserApp> list = userAppDao.find(sql,new Integer(version.getService().getAppId()));
        sql = "from TApp where appId=?";
        List<TApp> apps = appDao.find(sql, version.getService().getAppId());
        for(TUserApp userApp : list)
        {
            final String email = userApp.getUser().getEamil();
            final String content = version.getService().getServiceName()+
                    "["+version.getService().getServiceCode()+"]已经审批通过," +
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
                            String status,String desc,String content,String source_status)
    {
        TMethod method = methodDao.get(new Integer(methodId));
        JSONArray array = JSONArray.parseArray(method.getMockResult());
        JSONObject obj = null;

        if(!StringUtils.isBlank(source_status))
        {
            //更新
            for (int i = 0; i < array.size(); i++) {
                String sta = array.getJSONObject(i).getString("status");
                if (sta.equals(source_status)) {
                    obj = array.getJSONObject(i);
                    obj.put("status",status);
                    if (StringUtils.isBlank(desc)) {
                        desc = array.getJSONObject(i).getString("desc");
                    }
                    obj.put("desc",desc);
                    break;
                }
            }
            //return;
        }else {
            for (int i = 0; i < array.size(); i++) {
                String sta = array.getJSONObject(i).getString("status");
                if (sta.equals(status)) {
                    obj = array.getJSONObject(i);
                    if (StringUtils.isBlank(desc)) {
                        desc = array.getJSONObject(i).getString("desc");
                    }
                    obj.put("desc", desc);
                    obj.put("content", content);

                    break;
                }
            }
            if (obj == null) {
                obj = new JSONObject();
                obj.put("status", status);
                obj.put("desc", desc);
                obj.put("content", content);
                array.add(obj);
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

    public String[] getMock(String methodId)
    {
        List<String> mock = new ArrayList<String>();
        TMethod method = methodDao.get(new Integer(methodId));
        JSONArray array = null;

        List<Map> list = JSON.parseObject(method.getMockResult(),List.class);
        Collections.sort(list, new Comparator<Map>() {
            public int compare(Map t1, Map t2) {
                String desc1 = (String)t1.get("desc");
                String desc2 = (String)t2.get("desc");
                if(StringUtils.isBlank(desc1))
                {
                    desc1 = (String)t1.get("status");
                }
                if(StringUtils.isBlank(desc2))
                {
                    desc2 = (String)t2.get("status");
                }
                return desc1.compareTo(desc2);
            }
        });

        array = JSONArray.parseArray(JSON.toJSONString(list));
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
        String appId = method.getVersionObj().getAppCode();
        String serviceId = method.getVersionObj().getService().getServiceCode();
        String version = method.getVersionObj().getServiceVersion();

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
            if(result!=null) {
                for (int j = 0; j < result.length; j++) {
                    out.write(result[j].getBytes("UTF-8"));
                    out.write("\n".getBytes("UTF-8"));
                }
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
        if (method.getVersionObj().getStatus().equals("0")) {
            throw new RuntimeException("未审批");
        }
        List<TMethod> methods = method.getVersionObj().getMethods();
        String appCode = method.getVersionObj().getAppCode();
        String serviceId = method.getVersionObj().getService().getServiceCode();
        String ver = method.getVersionObj().getServiceVersion();

        ZookeeperUtils utils = ZookeeperUtils.getInstance();
        utils.createNode(PathConstant.ACL, "", CreateMode.PERSISTENT);
        //utils.createNode(PathConstant.ACL + "/" + (appCode + serviceId),"" , CreateMode.PERSISTENT);

        for (TMethod me : methods) {
            String arr = "";
            if (StringUtils.isBlank(me.getMockResult())) {
                arr = "[]";
            } else {
                arr = me.getMockResult();
            }
            JSONArray obj = JSONArray.parseArray(arr);
            utils.createNode(PathConstant.ACL + "/" + (appCode + serviceId)+me.getMethodName()+method.getMethodVersion(),obj.toJSONString() ,
                    CreateMode.PERSISTENT);
        }
    }

    public void changeTest(String versionId) {
        TServiceVersion version = versionDao.get(new Integer(versionId));
        TService service = version.getService();
        service.setTest("1");
        serviceDao.update(service);
    }

}
