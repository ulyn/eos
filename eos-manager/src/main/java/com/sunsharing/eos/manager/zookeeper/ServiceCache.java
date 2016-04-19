package com.sunsharing.eos.manager.zookeeper;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sunsharing.eos.common.zookeeper.PathConstant;
import com.sunsharing.eos.common.zookeeper.ZookeeperUtils;
import com.sunsharing.eos.manager.sys.SysProp;
import org.apache.log4j.Logger;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by criss on 14-1-27.
 */
public class ServiceCache {

    Logger logger = Logger.getLogger(ServiceCache.class);

    private ServiceCache()
    {

    }

    static ServiceCache cache = new ServiceCache();

    public static  ServiceCache getInstance()
    {
        return cache;
    }

    /*服务方的缓存*/
    public Map<String,JSONArray> serviceMap = new ConcurrentHashMap<String,JSONArray>();

    public synchronized void resetServiceMap()
    {
        logger.info("开始更新配置");
        try
        {
            ZookeeperUtils utils = ZookeeperUtils.getInstance();
            List<String> applist = utils.getChildren(PathConstant.SERVICE_STATE_EOS+"/"+ SysProp.eosId,true);


            Map<String,JSONArray> serviceTmpMap = new ConcurrentHashMap<String,JSONArray>();
            Set<String> apps = new HashSet();
            for(String app:applist) {
                app = app.substring(0, app.length() - 10);
                apps.add(app);
            }
            for(String app:apps)
            {
                List<String> services =  utils.getChildren(PathConstant.SERVICE_STATE_APPS+"/"+app,false);
                for(String path:services)
                {

                    String p = new String(utils.getData(PathConstant.SERVICE_STATE_APPS+"/"+ app+"/"+path,false),"UTF-8");
                    logger.info("更新service:"+p);
                    JSONObject jsonObject = JSONObject.parseObject(p);
                    String appId = jsonObject.getString(PathConstant.APPID_KEY);
                    String serviceId = jsonObject.getString(PathConstant.SERVICE_ID_KEY);
                    String ip = jsonObject.getString("ip");
                    String port = jsonObject.get("port").toString();
                    String eosIds = jsonObject.get("eosIds").toString();
                    String[] eosArr = eosIds.split(",");
                    List eosList = Arrays.asList(eosArr);
                    if(!eosList.contains(SysProp.eosId))
                    {
                        continue;
                    }

                    JSONObject methodVersion = jsonObject.getJSONObject("methodVersion");
                    Set<String> methods = methodVersion.keySet();
                    for(String method:methods)
                    {
                        String v = (String)methodVersion.get(method);

                        JSONArray obj = null;
                        if(serviceTmpMap.get(appId+serviceId+method+v)!=null)
                        {
                            obj = (JSONArray)serviceTmpMap.get(appId+serviceId+method+v);
                        }else
                        {
                            obj = new JSONArray();

                        }
                        boolean exist = false;
                        for(int i=0;i<obj.size();i++)
                        {
                            JSONObject tmp = (JSONObject)obj.get(i);
                            String ip1 = tmp.get("ip").toString();
                            String port1 = tmp.get("port").toString();
                            if(ip.equals(ip1) && port.equals(port1))
                            {
                                exist = true;
                            }
                        }
                        if(!exist) {
                            obj.add(jsonObject);
                        }
                        serviceTmpMap.put(appId+serviceId+method+v,obj);
                    }


                }
            }

            //不能先清除 serviceMap.clear();
            serviceMap.clear();
            serviceMap.putAll(serviceTmpMap);

            Set keys = serviceMap.keySet();
            for(Iterator iter = keys.iterator();iter.hasNext();)
            {
                String key = (String)iter.next();
                int size = ((JSONArray)serviceMap.get(key)).size();
                logger.info("同步后:"+key+":个数:"+size);
            }

        }catch (Exception e)
        {
            logger.error("获取"+SysProp.eosId+"节点信息失败",e);
        }

        logger.info("更新完成");

    }

    /**
     * 根据APPID,ServiceId，Version查找服务
     * @param appId
     * @param serviceId
     * @param version
     * @return 返回服务的注册信息
     */
    public synchronized JSONArray getServiceData(String appId,String serviceId,String methodName,String version)
    {
        return serviceMap.get(appId+serviceId+methodName+version);
    }

    /**
     * 获取权限
     * @param appId
     * @param serviceId
     * @param version
     * @return
     * @throws Exception
     */
    public boolean getACL(String appId,String serviceId,String method,String version) throws Exception
    {
        //是否授权
        ZookeeperUtils utils = ZookeeperUtils.getInstance();
        String path = PathConstant.ACL+"/"+appId+serviceId+method+version;
        if(utils.isExists(path,false))
        {
            return true;

        }else
        {
            return false;
        }

        //return utils.isExists(PathConstant.ACL+"/"+appId+serviceId+version);
    }

    /**
     * 获得测试代码
     * @param appId
     * @param serviceId
     * @param version
     * @param method 方法名
     * @return
     * [{"content":"{\"error\":\"错误了3\"}","desc":"当入参为其他时为错误输出","status":"error"},
     * {"content":"{\"success\":\"成功了2\",\"haha\":\"haha2\"}","desc":"当入参name=\"criss\"为成功输出",
     * "status":"success"}]
     */
    public JSONArray getTestCode(String appId,String serviceId,String version,String method)throws Exception
    {
        logger.info("appId:"+appId);
        logger.info("serviceId:"+serviceId);
        logger.info("version:"+version);
        logger.info("method:"+method);

        ZookeeperUtils utils = ZookeeperUtils.getInstance();
        if(utils.isExists(PathConstant.ACL+"/"+appId+serviceId+"/"+version,false))
        {
            String obj = new String(utils.getData(PathConstant.ACL+"/"+appId+serviceId+"/"+version,false),"UTF-8");
            logger.info("obj:"+obj);
            return (JSONArray)(JSONObject.parseObject(obj).get(method));
        }
        return null;
    }


}
