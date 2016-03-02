package com.sunsharing.eos.server.zookeeper;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sunsharing.eos.common.zookeeper.PathConstant;
import com.sunsharing.eos.common.zookeeper.ZookeeperUtils;
import com.sunsharing.eos.server.sys.SysProp;
import org.apache.log4j.Logger;
import org.apache.zookeeper.CreateMode;

import java.util.List;

/**
 * Created by criss on 14-1-28.
 */
public class ServiceRegister {

    Logger logger = Logger.getLogger(ServiceRegister.class);

    static ServiceRegister register = new ServiceRegister();

    private ServiceRegister()
    {

    }

    public static ServiceRegister getInstance()
    {
        return register;
    }

    /**
     * 用线程初始化
     */
    public void init()
    {
        ZookeeperUtils utils = ZookeeperUtils.getInstance();
        utils.setZooKeeperIP(SysProp.zookeeperIp);
        utils.setZooKeeperPort(SysProp.zookeeperPort);
        utils.addCallBack(new ServerConnectCallBack());
        //如果zookeeper没有启动，会同步重试，请用线程初始化
        utils.connect();
    }

//    /**
//     * 先注册EOS
//     * @param eosIds
//     */
    public synchronized void registerEos(String eosIds,String appId,String localIp,String localPort) throws Exception
    {
        ZookeeperUtils utils = ZookeeperUtils.getInstance();

        String[] ids = eosIds.split(",");

        //初始化EOS节点
        for(int i=0;i<ids.length;i++)
        {
            String eosId = ids[i];
            try {
                utils.createNodeNowatch(PathConstant.SERVICE_STATE_EOS+"/" + eosId, "", CreateMode.PERSISTENT);
                JSONObject object = new JSONObject();
                object.put("ip",localIp);
                object.put("port",localPort);

                utils.createEleSerNode(PathConstant.SERVICE_STATE_EOS + "/" + eosId + "/" + appId, object.toJSONString(),comparableData);
            }catch (Exception e)
            {
                logger.error("注册服务错误，无法创建节点,"+PathConstant.SERVICE_STATE_EOS + "/" + eosId,e);
            }
        }

    }
    ZookeeperUtils.CompareData comparableData = new ZookeeperUtils.CompareData() {
        public boolean compare(String d1, String d2) {
            JSONObject obj1 = JSONObject.parseObject(d1);
            JSONObject obj2 = JSONObject.parseObject(d2);
            String ip1 = obj1.getString("ip");
            String port1 = obj1.getString("port");
            String ip2 = obj2.getString("ip");
            String port2 = obj2.getString("port");
            if(ip1.equals(ip2) && port1.equals(port2))
            {
                return true;
            }
            return false;
        }
    };

    /**
     *
     * @param appId 用逗号分隔的EOSID
     * @param json 必须要有的字段包括如下：
     *  JSONObject obj = new JSONObject();
     *  obj.put("appId",appId);
     *  obj.put("serviceId",serviceId);
     *  obj.put("version", version);
     *
     */
    public synchronized boolean registerService(String eosIds,String appId,String json)
    {
        ZookeeperUtils utils = ZookeeperUtils.getInstance();

        String[] ids = eosIds.split(",");
        JSONObject obj = JSONObject.parseObject(json);
        String servicePath = obj.getString(PathConstant.APPID_KEY)+
                obj.getString(PathConstant.SERVICE_ID_KEY)+"_";

        //处理APP节点
        try {
            utils.createNodeNowatch(PathConstant.SERVICE_STATE_APPS + "/" + appId, "", CreateMode.PERSISTENT);
            utils.createEleSerNode(PathConstant.SERVICE_STATE_APPS + "/" + appId + "/" + servicePath, json,comparableData);
        }catch (Exception e)
        {
            logger.error("注册服务失败",e);
            return false;
        }
        return true;
    }


}
