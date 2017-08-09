package com.sunsharing.eos.manager.zookeeper;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.sunsharing.eos.common.zookeeper.PathConstant;
import com.sunsharing.eos.common.zookeeper.ZookeeperUtils;
import com.sunsharing.eos.manager.sys.SysProp;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
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
            List<String> list = utils.getChildren(PathConstant.SERVICE_STATE+"/"+ SysProp.eosId,true);
            //不能先清除 serviceMap.clear();
            Map<String,JSONArray> serviceTmpMap = new ConcurrentHashMap<String,JSONArray>();
            for(String path:list)
            {
                String p = new String(utils.getData(PathConstant.SERVICE_STATE+"/"+ SysProp.eosId+"/"+path,false),"UTF-8");
                JSONObject jsonObject = JSONObject.parseObject(p);
                String appId = jsonObject.getString(PathConstant.APPID_KEY);
                String serviceId = jsonObject.getString(PathConstant.SERVICE_ID_KEY);
                String version = jsonObject.getString(PathConstant.VERSION_KEY);
                JSONArray obj = null;
                if(serviceTmpMap.get(appId+serviceId+version)!=null)
                {
                    obj = (JSONArray)serviceTmpMap.get(appId+serviceId+version);
                }else
                {
                     obj = new JSONArray();

                }
                obj.add(jsonObject);
                serviceTmpMap.put(appId+serviceId+version,obj);
            }
            //不清除
            //serviceMap.clear();  将serviceTmpMap合并到serviceMap中
            for(Iterator iter = serviceTmpMap.keySet().iterator();iter.hasNext();)
            {
                String serviceLocationTmp = (String)iter.next();
                if(serviceMap.get(serviceLocationTmp) == null)
                {
                    serviceMap.put(serviceLocationTmp,serviceTmpMap.get(serviceLocationTmp));
                }else
                {
                    JSONArray array = (JSONArray)serviceMap.get(serviceLocationTmp);
                    Map realServiceMap = new HashMap();
                    for(int i=0;i<array.size();i++)
                    {
                        JSONObject obj = (JSONObject)array.get(i);
                        realServiceMap.put(obj.get("ip").toString()+obj.get("port").toString(),"BB");
                    }

                    JSONArray onlineArray = serviceTmpMap.get(serviceLocationTmp);
                    for(int i=0;i<onlineArray.size();i++)
                    {
                        JSONObject obj = (JSONObject)onlineArray.get(i);
                        String key = obj.get("ip").toString()+obj.get("port").toString();
                        if(!realServiceMap.containsKey(key))
                        {
                            array.add(obj);
                        }
                    }

                }

            }
            logger.info("更新服务后:"+JSONObject.toJSONString(serviceMap, SerializerFeature.PrettyFormat));
            //serviceMap.putAll(serviceTmpMap);
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
    public synchronized JSONArray getServiceData(String appId,String serviceId,String version)
    {
        return serviceMap.get(appId+serviceId+version);
    }

    /**
     * 获取权限
     * @param appId
     * @param serviceId
     * @param version
     * @return
     * @throws Exception
     */
    public boolean getACL(String appId,String serviceId,String version) throws Exception
    {
        //是否授权
        ZookeeperUtils utils = ZookeeperUtils.getInstance();
        if(utils.isExists(PathConstant.ACL+"/"+appId+serviceId,false))
        {
            if(!utils.isExists(PathConstant.ACL+"/"+appId+serviceId+"/"+version,false))
            {
                return false;
            }else
            {
                List<String> childer = utils.getChildren(PathConstant.ACL+"/"+appId+serviceId,false);
                String max = "";
                for(String str:childer)
                {
                    if(str.compareTo(max)>0)
                    {
                        max = str;
                    }
                }
                if(max.equals(version))
                {
                    return true;
                }else
                {
                    throw new RuntimeException("服务方更新提醒:"+serviceId+"最新版本为:"+max+",你的版本为:"+version+",请更新");
                }
            }

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
