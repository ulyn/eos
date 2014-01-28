package com.sunsharing.eos.manager.zookeeper;

import com.sunsharing.eos.common.zookeeper.ZookeeperCallBack;
import com.sunsharing.eos.common.zookeeper.ZookeeperUtils;
import com.sunsharing.eos.manager.sys.SysProp;
import org.apache.log4j.Logger;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;

/**
 * Created by criss on 14-1-27.
 */
public class ConnectCallBack implements ZookeeperCallBack {
    Logger logger = Logger.getLogger(ConnectCallBack.class);
    @Override
    public void afterConnect(WatchedEvent event) {
        try
        {
            logger.info("登录成功了开始调用回调");
            ZookeeperUtils utils = ZookeeperUtils.getInstance();
            utils.printNode(EosState.SERVICE_STATE);
            utils.createNode(EosState.SERVICE_STATE,"", CreateMode.PERSISTENT);
            //创建EOS节点
            utils.createNode(EosState.SERVICE_STATE+"/"+ SysProp.eosId,"",CreateMode.PERSISTENT);
            //添加监听
            utils.watchNode(EosState.SERVICE_STATE + "/" + SysProp.eosId);
            //添加子路径监听
            utils.getChildren(EosState.SERVICE_STATE+"/"+ SysProp.eosId);
        }catch (Exception e)
        {
            logger.error("初始化EOS出错",e);
        }
    }

    @Override
    public void watchNodeChange(WatchedEvent event) {
        if(event.getType()==Watcher.Event.EventType.NodeChildrenChanged)
        {
            ServiceCache.getInstance().resetServiceMap();
        }
    }
}
