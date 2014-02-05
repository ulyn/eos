package com.sunsharing.eos.manager.zookeeper;

import com.sunsharing.eos.common.zookeeper.PathConstant;
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
public class ManagerConnectCallBack implements ZookeeperCallBack {
    Logger logger = Logger.getLogger(ManagerConnectCallBack.class);
    @Override
    public void afterConnect(WatchedEvent event) {
        try
        {
            logger.info("登录成功了开始调用回调");
            ZookeeperUtils utils = ZookeeperUtils.getInstance();
            utils.createNode(PathConstant.SERVICE_STATE,"", CreateMode.PERSISTENT);

            //创建EOS节点
            utils.createNode(PathConstant.SERVICE_STATE+"/"+ SysProp.eosId,"",CreateMode.PERSISTENT);
            //添加监听
            utils.watchNode(PathConstant.SERVICE_STATE + "/" + SysProp.eosId);

            //EOS状态
            utils.createNode(PathConstant.EOS_STATE,"", CreateMode.PERSISTENT);
            while(utils.isExists(PathConstant.EOS_STATE+"/"+SysProp.eosId))
            {
                logger.info("存在节点:"+utils.isExists(PathConstant.EOS_STATE+"/"+SysProp.eosId)+"等待关闭");
                Thread.sleep(1000);
            }
            utils.createNode(PathConstant.EOS_STATE+"/"+SysProp.eosId,SysProp.eosIp+":"+SysProp.eosPort,
                    CreateMode.EPHEMERAL);

            //utils.printNode("/");

        }catch (Exception e)
        {
            logger.error("初始化EOS出错",e);
        }
    }

    @Override
    public void watchNodeChange(WatchedEvent event) {
        if(event.getType()==Watcher.Event.EventType.NodeChildrenChanged)
        {
            if(event.getPath().startsWith(PathConstant.SERVICE_STATE))
            {
                ServiceCache.getInstance().resetServiceMap();
            }
        }
    }
}
