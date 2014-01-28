package com.sunsharing.eos.manager.zookeeper;

import com.sunsharing.eos.common.zookeeper.ZookeeperUtils;
import com.sunsharing.eos.manager.sys.SysProp;
import org.apache.log4j.Logger;

/**
 * Created by criss on 14-1-27.
 */
public class EosState {

    Logger logger = Logger.getLogger(EosState.class);



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
