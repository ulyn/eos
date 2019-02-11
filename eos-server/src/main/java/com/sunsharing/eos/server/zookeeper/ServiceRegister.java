package com.sunsharing.eos.server.zookeeper;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.sunsharing.eos.common.zookeeper.PathConstant;
import com.sunsharing.eos.common.zookeeper.ZookeeperUtils;
import com.sunsharing.eos.server.sys.EosServerProp;

import org.apache.log4j.Logger;
import org.apache.zookeeper.CreateMode;

import java.util.List;

/**
 * Created by criss on 14-1-28.
 */
public class ServiceRegister {

    Logger logger = Logger.getLogger(ServiceRegister.class);

    static ServiceRegister register = new ServiceRegister();

    private ServiceRegister() {

    }

    public static ServiceRegister getInstance() {
        return register;
    }

    /**
     * 用线程初始化
     */
    public void init() {
        ZookeeperUtils utils = ZookeeperUtils.getInstance();
        utils.setZooKeeperIP(EosServerProp.zookeeperIp);
        utils.setZooKeeperPort(EosServerProp.zookeeperPort);
        utils.addCallBack(new ServerConnectCallBack());
        //如果zookeeper没有启动，会同步重试，请用线程初始化
        utils.connect();
    }

    //    /**
//     * 先注册EOS
//     * @param eosIds
//     */
    public synchronized void registerEos(String eosIds, String appId, String localIp, String localPort, int totalServiceSize) throws Exception {
        ZookeeperUtils utils = ZookeeperUtils.getInstance();

        String[] ids = eosIds.split(",");

        //初始化EOS节点
        for (int i = 0; i < ids.length; i++) {
            String eosId = ids[i];
            try {
                utils.createNodeNowatch(PathConstant.SERVICE_STATE_EOS + "/" + eosId, "", CreateMode.PERSISTENT);
                JSONObject object = new JSONObject();
                object.put("ip", localIp);
                object.put("port", localPort);
                object.put("totalServiceSize", totalServiceSize);

                utils.createEleSerNode(PathConstant.SERVICE_STATE_EOS + "/" + eosId + "/" + appId, object.toJSONString(), comparableData);
            } catch (Exception e) {
                logger.error("注册服务错误，无法创建节点," + PathConstant.SERVICE_STATE_EOS + "/" + eosId, e);
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
            if (ip1.equals(ip2) && port1.equals(port2)) {
                return true;
            }
            return false;
        }
    };

    /**
     *
     * @param appId 用逗号分隔的EOSID
     *  JSONObject obj = new JSONObject();
     *  obj.put("appId",appId);
     *  obj.put("serviceId",serviceId);
     *  obj.put("version", version);
     *
     */
    public synchronized boolean registerServices(String appId, List<JSONObject> jsons) {
        ZookeeperUtils utils = ZookeeperUtils.getInstance();

        String node = EosServerProp.localIp + ":" + EosServerProp.nettyServerPort;

        //处理APP节点
        try {
            utils.createNodeNowatch(PathConstant.SERVICE_STATE_APPS + "/" + appId, "", CreateMode.PERSISTENT);
            while (true) {
                boolean exist = utils.isExists(PathConstant.SERVICE_STATE_APPS + "/" + appId + "/" + node, false);
                if (!exist) {
                    break;
                } else {
                    logger.info(PathConstant.SERVICE_STATE_APPS + "/" + appId + "/" + node + "存在，请等待30秒");
                    Thread.sleep(1000);
                }
            }
            utils.createNode(PathConstant.SERVICE_STATE_APPS + "/" + appId + "/" + node, JSONArray.toJSONString(jsons), CreateMode.EPHEMERAL);
            logger.info("成功注册服务方->" + JSONArray.toJSONString(jsons, SerializerFeature.PrettyFormat));
        } catch (Exception e) {
            logger.error("注册服务失败", e);
            return false;
        }
        return true;
    }


}
