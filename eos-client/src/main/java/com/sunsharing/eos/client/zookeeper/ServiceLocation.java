package com.sunsharing.eos.client.zookeeper;

import com.sunsharing.eos.client.sys.SysProp;
import com.sunsharing.eos.common.zookeeper.ZookeeperUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 返回Service的注册的EOS地址
 * Created by criss on 14-1-29.
 */
public class ServiceLocation {

    static Map<String,String> eosMap = new ConcurrentHashMap<String, String>();

    /**
     * 用线程初始化
     */
    public void connect()
    {
        ZookeeperUtils utils = ZookeeperUtils.getInstance();
        utils.setZooKeeperIP(SysProp.zookeeperIp);
        utils.setZooKeeperPort(SysProp.zookeeperPort);
        utils.setCallBack(new ConnectCallBack());
        utils.connect();
    }

    public synchronized void getAllServices()
    {

    }

}
