package com.sunsharing.eos.manager.zookeeper;

import com.sunsharing.eos.common.zookeeper.ZookeeperUtils;
import com.sunsharing.eos.manager.sys.SysProp;
import org.apache.log4j.Logger;

/**
 * Created by criss on 14-1-27.
 */
public class EosState {

    Logger logger = Logger.getLogger(EosState.class);

    /**存储服务状态的所有节点*/
    public static String SERVICE_STATE="/SERVICE_STATE";
    /**存储EOS状态*/
    public static String EOS_STATE = "/EOS_STATE";
    /**监控数据节点*/
    public static String EOS_MONITOR="/EOS_MONITOR";
    /**应用KEY*/
    public static String APPID_KEY="appId";
    /**ServiceId的KEY*/
    public static String SERVICE_ID_KEY="serviceId";
    /**Version的KEY*/
    public static String VERSION_KEY="version";

    public void connect()
    {
        ZookeeperUtils utils = ZookeeperUtils.getInstance();
        utils.setZooKeeperIP(SysProp.zookeeperIp);
        utils.setZooKeeperPort(SysProp.zookeeperPort);
        utils.setCallBack(new ConnectCallBack());
        //如果ZoomKeeper没启动的话是会同步等待的
        utils.connect();
        logger.info("connect end");
    }

}
