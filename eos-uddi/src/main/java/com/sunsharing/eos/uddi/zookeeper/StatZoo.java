package com.sunsharing.eos.uddi.zookeeper;

import com.sunsharing.eos.common.zookeeper.ZookeeperUtils;
import com.sunsharing.eos.uddi.sys.SysProp;
import org.apache.log4j.Logger;

/**
 * Created by criss on 14-2-4.
 */
public class StatZoo {

    Logger logger  = Logger.getLogger(StatZoo.class);

    public void connect()
    {
        ZookeeperUtils utils = ZookeeperUtils.getInstance();
        utils.setZooKeeperIP(SysProp.zookeeperIp);
        utils.setZooKeeperPort(SysProp.zookeeperPort);
        //如果ZoomKeeper没启动的话是会同步等待的
        utils.connect();
        logger.info("connect end");
    }

}
