package com.sunsharing.eos.client.zookeeper;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sunsharing.eos.client.sys.SysProp;
import com.sunsharing.eos.common.rpc.RpcException;
import com.sunsharing.eos.common.utils.StringUtils;
import com.sunsharing.eos.common.zookeeper.PathConstant;
import com.sunsharing.eos.common.zookeeper.ZookeeperUtils;
import org.apache.log4j.Logger;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

/**
 * 返回Service的注册的EOS地址
 * Created by criss on 14-1-29.
 */
public class ServiceLocation {

    Logger logger = Logger.getLogger(ServiceLocation.class);

    //private static Map<String, JSONObject> eosMap = new ConcurrentHashMap<String, JSONObject>();

    private static Map<String,JSONObject> onlineEos = new ConcurrentHashMap<String, JSONObject>();
    private static Map<String,JSONObject> serviceMap = new ConcurrentHashMap<String,JSONObject>();

    private ServiceLocation() {

    }

    static ServiceLocation serviceLocation = new ServiceLocation();

    public static ServiceLocation getInstance() {
        return serviceLocation;
    }



    /**
     * 用线程初始化
     */
    public void connect() {
        if (StringUtils.isBlank(SysProp.zookeeperIp)) {
            logger.warn("zookeeperIp未配置，系统不进行连接 ^_^ ");
            return;
        }
        ZookeeperUtils utils = ZookeeperUtils.getInstance();
        utils.setZooKeeperIP(SysProp.zookeeperIp);
        utils.setZooKeeperPort(SysProp.zookeeperPort);
        utils.addCallBack(new ClientConnectCallBack(null));
        utils.connect();
    }

    /**
     * 同步初始化方法
     */
    public void synConnect(){
        CountDownLatch connectedSignal = new CountDownLatch(1);
        ZookeeperUtils utils = ZookeeperUtils.getInstance();
        utils.setZooKeeperIP(SysProp.zookeeperIp);
        utils.setZooKeeperPort(SysProp.zookeeperPort);
        utils.addCallBack(new ClientConnectCallBack(connectedSignal));
        utils.connect();
        try
        {
            connectedSignal.await();
        }catch (Exception e)
        {
            logger.error("初始化错了",e);
        }
        logger.info("成功初始化...");
    }



    /**
     * 更新Eos所有服务
     *
     * @throws Exception
     */
    public synchronized void updateEos() throws Exception {
        ZookeeperUtils utils = ZookeeperUtils.getInstance();
        List<String> onlineEosTmp = utils.getChildren(PathConstant.EOS_STATE,true);
        Map tmplist = new HashMap();
        for (String eosId : onlineEosTmp) {
            byte[] data = utils.getData(PathConstant.EOS_STATE+"/"+eosId,false);
            String eosData = new String(data,"UTF-8");
            String ip = eosData.split(":")[0];
            String port = eosData.split(":")[1];
            JSONObject object = new JSONObject();
            object.put("eosId",eosId);
            object.put("eosIp", ip);
            object.put("eosPort", port);
            tmplist.put(eosId, object);
        }
        onlineEos.clear();
        onlineEos.putAll(tmplist);
        logger.info("更新线上EOS:"+JSONArray.toJSONString(tmplist));
    }

    /**
     * 更新服务变化
     *
     * @param appId
     * @throws Exception
     */
    public synchronized void updateEosServices(String appId) throws Exception {
        ZookeeperUtils utils = ZookeeperUtils.getInstance();
        List<String> onlineServices = utils.getChildren(PathConstant.SERVICE_STATE_APPS+"/"+appId,true);
        Map tmp = new HashMap();
        //处理online
        for (String servicePath : onlineServices) {
            byte[] data = utils.getData(PathConstant.SERVICE_STATE_APPS+"/"+appId+"/"+servicePath,false);
            JSONObject serviceData = JSONObject.parseObject(new String(data, "UTF-8"));
            String eosIds = (String)serviceData.get("eosIds");

            int i = servicePath.lastIndexOf("_");
            String real = servicePath.substring(0, i);

            JSONArray serviceArray = null;
            if(tmp.get(real)!=null)
            {
                serviceArray = (JSONArray)tmp.get(real);
            }else
            {
                serviceArray = new JSONArray();
            }
            JSONObject methodMap = (JSONObject)serviceData.get("methodMap");
            JSONObject object = new JSONObject();
            object.put("servicePath",real);
            object.put("eosIds",eosIds);
            object.put("methodMap",methodMap);
            serviceArray.add(object);
            tmp.put(real, serviceArray);
        }
        serviceMap.clear();
        serviceMap.putAll(tmp);
    }




    public synchronized void printCache() {
        logger.info("成功加载EOS："+JSONArray.toJSONString(onlineEos));
        for (String servicePath : serviceMap.keySet()) {
            logger.info("成功加载服务:" + servicePath + "--" + serviceMap.get(servicePath).toString());
        }
    }

    /**
     * 获取服务所在EOS的IP PORT
     *
     * @param appId
     * @param serviceId
     * @return {“ip”:"","port":""} 如果是null，表示不存在这个
     */
    public synchronized JSONObject getServiceLocation(String appId, String serviceId,String methodName ,String methodVersion)throws RpcException
    {
        return getServiceLocation(appId, serviceId, methodName,methodVersion,false);
    }

    /**
     * 获得在线的EOS
     *
     * @return {“ip”:"","port":""} 如果是null，表示不存在这个
     */
    public synchronized JSONObject getOnlineEOS() {
        return getServiceLocation(null, null, null,null, true);
    }

    private JSONObject getServiceLocation
            (String appId, String serviceId, String methodName ,String methodVersion, boolean mock)
            throws RpcException{
        JSONArray ips = new JSONArray();
        if(mock)
        {
            Set<String> eosIds = onlineEos.keySet();
            for(String eosId:eosIds)
            {
                JSONObject ipport = (JSONObject) onlineEos.get(eosId);
                JSONObject obj = new JSONObject();
                obj.put("ip", ipport.get("eosIp"));
                obj.put("port", ipport.get("eosPort"));
                ips.add(obj);
            }
        }else {
            String servicePath = appId + serviceId;
            JSONObject array = serviceMap.get(servicePath);
            if(array == null)
            {
                logger.info("打印服务缓存信息.");
                printCache();
                throw new RpcException(RpcException.SERVICE_NO_FOUND_EXCEPTION, "找不到appId:"+appId+",serviceId:"+serviceId+"注册信息");
            }
            Set<String> eosIds = new HashSet<String>();
            for(int i=0;i<array.size();i++)
            {
                JSONObject jsonObject = (JSONObject)array.get(i);
                String eosIdsTmp = (String)jsonObject.get("eosIds");
                JSONObject methodMap = (JSONObject)jsonObject.get("methodMap");
                String methodV=(String)methodMap.get(methodName);
                if(methodV.equals(methodVersion))
                {
                    String[] arr = eosIdsTmp.split(",");
                    for(int j=0;j<arr.length;j++)
                    {
                        eosIds.add(arr[j]);
                    }
                }else
                {
                    logger.error("找到不同版本服务，appId:" + appId + ",serviceId:" + serviceId + ",函数名:" + methodName +
                            ",服务器版本:" + methodV + ",调用版本:" + methodVersion + ",eosId:" + eosIdsTmp);
                }
            }
            if(eosIds.size()==0)
            {
                throw new RpcException(RpcException.SERVICE_NO_FOUND_EXCEPTION, "找不到appId:"+appId+",serviceId:"+serviceId+"," +
                        "methodName:"+methodName+",methodVersion:"+methodVersion+",注册的EOSID");
            }


            for (String eosId : eosIds) {
                if (onlineEos.get(eosId) != null) {
                    //在线
                    JSONObject ipport = (JSONObject) onlineEos.get(eosId);
                    JSONObject obj = new JSONObject();
                    obj.put("ip", ipport.get("eosIp"));
                    obj.put("port", ipport.get("eosPort"));
                    ips.add(obj);
                }
            }
        }

        if (ips.size() == 0) {
            return null;
        }
        Random r = new Random();
        int index = r.nextInt(ips.size());
        if (index >= ips.size()) {
            index = 0;
        }
        return ips.getJSONObject(index);
    }

    public static void main(String[] a) {
        String str = "/abc/abc/mit";
        String arr[] = str.split("\\/");
        System.out.println(arr.length);
    }


}
