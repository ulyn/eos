package com.sunsharing.eos.client.zookeeper;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sunsharing.eos.client.sys.SysProp;
import com.sunsharing.eos.common.zookeeper.PathConstant;
import com.sunsharing.eos.common.zookeeper.ZookeeperUtils;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

/**
 * 返回Service的注册的EOS地址
 * Created by criss on 14-1-29.
 */
public class ServiceLocation {

    Logger logger = Logger.getLogger(ServiceLocation.class);

    static Map<String, JSONObject> eosMap = new ConcurrentHashMap<String, JSONObject>();

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
        ZookeeperUtils utils = ZookeeperUtils.getInstance();
        utils.setZooKeeperIP(SysProp.zookeeperIp);
        utils.setZooKeeperPort(SysProp.zookeeperPort);
        utils.setCallBack(new ClientConnectCallBack(null));
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
        utils.setCallBack(new ClientConnectCallBack(connectedSignal));
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
     * 加载所有服务
     *
     * @throws Exception
     */
    public synchronized void loadAllServices() throws Exception {
        ZookeeperUtils utils = ZookeeperUtils.getInstance();
        List<String> onlineEos = utils.getChildren(PathConstant.EOS_STATE);
        for (String eosId : onlineEos) {
            logger.info("jiazai:" + eosId);
            addEos(eosId);
        }
    }

    /**
     * 更新Eos所有服务
     *
     * @throws Exception
     */
    public synchronized void updateEos() throws Exception {
        ZookeeperUtils utils = ZookeeperUtils.getInstance();
        List<String> onlineEos = utils.getChildren(PathConstant.EOS_STATE);
        for (String eosId : onlineEos) {
            System.out.println("online:" + eosId);
            if (!eosMap.containsKey(eosId)) {
                logger.info("EOS:" + eosId + "上线");
                addEos(eosId);
            }
        }
        for (String eosId : eosMap.keySet()) {
            if (!onlineEos.contains(eosId)) {
                logger.info("EOS:" + eosId + "下线");
                removeEos(eosId);
            }
        }
    }

    /**
     * 更新服务变化
     *
     * @param eosId
     * @throws Exception
     */
    public synchronized void updateEosServices(String eosId) throws Exception {
        ZookeeperUtils utils = ZookeeperUtils.getInstance();
        List<String> onlineServices = utils.getChildren(PathConstant.SERVICE_STATE+"/"+eosId);
        List<String> realOnline = new ArrayList<String>();
        //处理online
        for (String servicePath : onlineServices) {
            int i = servicePath.lastIndexOf("_");
            String real = servicePath.substring(0, i);
            realOnline.add(real);
        }

        JSONObject eos = eosMap.get(eosId);
        JSONObject service = eos.getJSONObject("services");
        for (String online : realOnline) {
            if (!service.containsKey(online)) {
                logger.info("Service:" + online + "上线");
                service.put(online, "AA");
            }
        }
        for (String ser : service.keySet()) {
            if (!realOnline.contains(ser)) {
                logger.info("Service:" + ser + "下线");
                service.remove(ser);
            }
        }
    }


    /**
     * 加载某个EOS的所有服务
     *
     * @param eosId
     * @throws Exception
     */
    public synchronized void addEos(String eosId) throws Exception {
        ZookeeperUtils utils = ZookeeperUtils.getInstance();
        //判断EOS是否在线
        boolean isonline = utils.isExists(PathConstant.EOS_STATE + "/" + eosId);
        logger.info("监听:" + PathConstant.SERVICE_STATE + "/" + eosId);
        utils.watchNode(PathConstant.SERVICE_STATE + "/" + eosId);
        logger.info("isonline:" + isonline);
        if (!isonline) {
            return;
        }
        JSONObject eosObj = new JSONObject();
        String eosIpPort = new String(utils.getData(PathConstant.EOS_STATE + "/" + eosId), "UTF-8");
        JSONObject servicesObj = new JSONObject();
        eosObj.put("eos_ip", eosIpPort.split(":")[0]);
        eosObj.put("eos_port", eosIpPort.split(":")[1]);
        List<String> services = utils.getChildren(PathConstant.SERVICE_STATE + "/" + eosId);
        for (String service : services) {
            int i = service.lastIndexOf("_");
            String realpath = service.substring(0, i);
            logger.info("sercice:" + realpath);
            servicesObj.put(realpath, "AA");
        }
        eosObj.put("services", servicesObj);
        eosMap.put(eosId, eosObj);
    }

    /**
     * 移除EOS
     *
     * @param eosId
     * @throws Exception
     */
    public synchronized void removeEos(String eosId) throws Exception {
        ZookeeperUtils utils = ZookeeperUtils.getInstance();
        //判断EOS是否在线
        boolean isonline = utils.isExists(PathConstant.EOS_STATE + "/" + eosId);
        if (!isonline) {
            eosMap.remove(eosId);
        }
    }

    public synchronized void printCache() {
        for (String eosId : eosMap.keySet()) {
            logger.info("eosId:" + eosId + "--" + eosMap.get(eosId).toString());
        }
    }

    /**
     * 获取服务所在EOS的IP PORT
     *
     * @param appId
     * @param serviceId
     * @param version
     * @return {“ip”:"","port":""} 如果是null，表示不存在这个
     */
    public synchronized JSONObject getServiceLocation(String appId, String serviceId, String version) {
        return getServiceLocation(appId, serviceId, version, false);
    }

    /**
     * 获得在线的EOS
     *
     * @return {“ip”:"","port":""} 如果是null，表示不存在这个
     */
    public synchronized JSONObject getOnlineEOS() {
        return getServiceLocation(null, null, null, true);
    }

    private JSONObject getServiceLocation
            (String appId, String serviceId, String version, boolean mock) {
        JSONArray ips = new JSONArray();
        String servicePath = appId + serviceId + version;
        for (String eosId : eosMap.keySet()) {
            JSONObject eos = eosMap.get(eosId);
            if (mock || eos.getJSONObject("services").get(servicePath) != null) {
                JSONObject obj = new JSONObject();
                obj.put("ip", eos.get("eos_ip"));
                obj.put("port", eos.get("eos_port"));
                ips.add(obj);
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
