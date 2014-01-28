package com.sunsharing.eos.manager.zookeeper;

import com.alibaba.fastjson.JSONObject;
import com.sunsharing.eos.common.zookeeper.PathConstant;
import com.sunsharing.eos.common.zookeeper.ZookeeperUtils;
import com.sunsharing.eos.manager.sys.SysProp;
import org.apache.log4j.Logger;

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
    public Map<String,String> serviceMap = new ConcurrentHashMap<String,String>();

    public synchronized void resetServiceMap()
    {
        logger.info("开始更新配置");
        try
        {
            ZookeeperUtils utils = ZookeeperUtils.getInstance();
            List<String> list = utils.getChildren(PathConstant.SERVICE_STATE+"/"+ SysProp.eosId);
            serviceMap.clear();
            for(String path:list)
            {
                String p = new String(utils.getData(PathConstant.SERVICE_STATE+"/"+ SysProp.eosId+"/"+path),"UTF-8");
                logger.info("更新service:"+p);
                JSONObject obj = JSONObject.parseObject(p);
                String appId = obj.getString(PathConstant.APPID_KEY);
                String serviceId = obj.getString(PathConstant.SERVICE_ID_KEY);
                String version = obj.getString(PathConstant.VERSION_KEY);
                serviceMap.put(appId+serviceId+version,p);
            }

        }catch (Exception e)
        {
            logger.error("获取"+SysProp.eosId+"节点信息失败",e);
        }

    }

    /**
     * 根据APPID,ServiceId，Version查找服务
     * @param appId
     * @param serviceId
     * @param version
     * @return 返回服务的注册信息
     */
    public synchronized String getServiceData(String appId,String serviceId,String version)
    {
        return serviceMap.get(appId+serviceId+version);
    }


}
