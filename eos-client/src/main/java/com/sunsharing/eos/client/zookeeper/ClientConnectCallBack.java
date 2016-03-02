package com.sunsharing.eos.client.zookeeper;

import com.sunsharing.eos.client.sys.EosClientProp;
import com.sunsharing.eos.common.utils.StringUtils;
import com.sunsharing.eos.common.zookeeper.PathConstant;
import com.sunsharing.eos.common.zookeeper.ZookeeperCallBack;
import com.sunsharing.eos.common.zookeeper.ZookeeperUtils;
import org.apache.log4j.Logger;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;

import java.util.concurrent.CountDownLatch;

/**
 * Created by criss on 14-1-27.
 */
public class ClientConnectCallBack implements ZookeeperCallBack {

    CountDownLatch connectedSignal = null;

    public ClientConnectCallBack()
    {

    }

    public ClientConnectCallBack(CountDownLatch connectedSignal)
    {
        this.connectedSignal = connectedSignal;
    }

    Logger logger = Logger.getLogger(ClientConnectCallBack.class);
    @Override
    public void afterConnect(WatchedEvent event) {
        try
        {
            logger.info("登录成功了开始调用回调");
            String appIds = EosClientProp.app_id;
            if(StringUtils.isBlank(appIds))
            {
                throw new RuntimeException("E3的配置文件必须增加app_id");
            }
            ZookeeperUtils utils = ZookeeperUtils.getInstance();
            PathConstant.initEOSPath();
            /**
             * 初始化应用
             */
            String[] appArr = appIds.split(",");
            for(int i=0;i<appArr.length;i++)
            {
                String appPath = PathConstant.SERVICE_STATE_APPS+"/"+appArr[i];
                if(!utils.isExists(appPath,false))
                {
                    utils.createNodeNowatch(appPath,"", CreateMode.PERSISTENT);
                }
            }
            ServiceLocation.getInstance().updateEos();

            for(int i=0;i<appArr.length;i++)
            {
                ServiceLocation.getInstance().updateEosServices(appArr[i]);
            }

            logger.info("````````````````````````````");
            ServiceLocation.getInstance().printCache();
            logger.info("````````````````````````````");

            if(connectedSignal!=null)
            {
                connectedSignal.countDown();
            }

        }catch (Exception e)
        {
            logger.error("初始化EOS出错",e);
        }
    }

    @Override
    public void watchNodeChange(WatchedEvent event) {
        ZookeeperUtils utils = ZookeeperUtils.getInstance();
        logger.info("watch:"+event.getType()+":"+event.getPath());
        if(event.getType()==Watcher.Event.EventType.NodeChildrenChanged)
        {
            if(event.getPath().startsWith(PathConstant.EOS_STATE))
            {
                logger.info("有EOS上线,或者下线了，重新加载");
                try
                {

                    ServiceLocation.getInstance().updateEos();
                }catch (Exception e)
                {
                    logger.error("初始化EOS出错",e);
                }
            }
            if(event.getPath().startsWith(PathConstant.SERVICE_STATE_APPS))
            {
                logger.info("有Service上线,或者下线了，重新加载,"+event.getPath());
                try
                {
                    String [] paths = event.getPath().split("\\/");
                    if(paths.length==5)
                    {
                            ServiceLocation.getInstance().updateEosServices(paths[4]);
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
