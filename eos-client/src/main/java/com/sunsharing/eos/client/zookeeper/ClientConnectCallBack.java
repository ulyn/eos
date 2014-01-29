package com.sunsharing.eos.client.zookeeper;

import com.sunsharing.eos.common.zookeeper.PathConstant;
import com.sunsharing.eos.common.zookeeper.ZookeeperCallBack;
import com.sunsharing.eos.common.zookeeper.ZookeeperUtils;
import org.apache.log4j.Logger;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;

import java.util.List;

/**
 * Created by criss on 14-1-27.
 */
public class ClientConnectCallBack implements ZookeeperCallBack {
    Logger logger = Logger.getLogger(ClientConnectCallBack.class);
    @Override
    public void afterConnect(WatchedEvent event) {
        try
        {
            logger.info("登录成功了开始调用回调");
            ZookeeperUtils utils = ZookeeperUtils.getInstance();
            utils.watchNode(PathConstant.EOS_STATE);
            utils.watchNode(PathConstant.SERVICE_STATE);

            ServiceLocation.getInstance().loadAllServices();

            logger.info("````````````````````````````");
            ServiceLocation.getInstance().printCache();
            logger.info("````````````````````````````");

        }catch (Exception e)
        {
            logger.error("初始化EOS出错",e);
        }
    }

    @Override
    public void watchNodeChange(WatchedEvent event) {
        ZookeeperUtils utils = ZookeeperUtils.getInstance();

        if(event.getType()==Watcher.Event.EventType.NodeChildrenChanged)
        {
            if(event.getPath().startsWith(PathConstant.EOS_STATE))
            {
                logger.info("有EOS上线,或者下线了，重新加载");
                try
                {
                    List<String> onlineEos = utils.getChildren(PathConstant.EOS_STATE);
                    ServiceLocation.getInstance().updateEos(onlineEos);
                }catch (Exception e)
                {
                    logger.error("初始化EOS出错",e);
                }
            }
            if(event.getPath().startsWith(PathConstant.SERVICE_STATE))
            {
                logger.info("有Service上线,或者下线了，重新加载");
                try
                {
                    String [] paths = event.getPath().split("\\/");
                    if(paths.length==3)
                    {
                        List<String> onlineService = utils.getChildren(PathConstant.SERVICE_STATE+"/"+paths[2]);
                        ServiceLocation.getInstance().updateEosServices(paths[2],onlineService);
                    }
                }catch (Exception e)
                {
                    logger.error("初始化EOS出错",e);
                }
            }
        }
        logger.info("````````````````````````````");
        ServiceLocation.getInstance().printCache();
        logger.info("````````````````````````````");
    }
}
