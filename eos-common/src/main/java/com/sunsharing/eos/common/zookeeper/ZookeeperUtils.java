package com.sunsharing.eos.common.zookeeper;

import org.apache.log4j.Logger;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * Zookeeper操作封装
 * Created by criss on 14-1-27.
 */
public class ZookeeperUtils {
    Logger logger = Logger.getLogger(ZookeeperUtils.class);

    private volatile boolean connected = false;

    private CountDownLatch connectedSignal;

    protected ZooKeeper zookeeper;

    protected ZookeeperCallBack callBack;

    protected String zooKeeperIP;
    protected int zooKeeperPort;

    public int getZooKeeperPort() {
        return zooKeeperPort;
    }

    public void setZooKeeperPort(int zooKeeperPort) {
        this.zooKeeperPort = zooKeeperPort;
    }

    public String getZooKeeperIP() {
        return zooKeeperIP;
    }

    public void setZooKeeperIP(String zooKeeperIP) {
        this.zooKeeperIP = zooKeeperIP;
    }

    public ZookeeperCallBack getCallBack() {
        return callBack;
    }

    public void setCallBack(ZookeeperCallBack callBack) {
        this.callBack = callBack;
    }

    private ZookeeperUtils()
    {

    }

    static ZookeeperUtils utils = new ZookeeperUtils();

    public static ZookeeperUtils getInstance()
    {
        return utils;
    }



    /**
     *建立连接:实例化一个新的ZooKeeper对象，且维护着客户端与ZooKeeper服务的链接。
     * @throwsKeeperException
     * @throwsInterruptedException
     */
    public synchronized void connect() {
        logger.info("ZookeeperCacheSynchronizer:connect");
        if (!connected) {
            try {
                connectedSignal = new CountDownLatch(1);
                //ZooKeeper构造函数有三个参数：1.是ZooKeeper服务的主机地址包括端口2.是会话超时时长
                //3.是Watcher对象的实例。Watcher对象接受ZooKeeper的响应，并通知它各种事件。
                // ZooKeeper客户端正是通过注册Watcher的方法来获取状态变化的信息。
                logger.info("连接"+zooKeeperIP +":"+zooKeeperPort);
                zookeeper =new ZooKeeper(zooKeeperIP +":"+zooKeeperPort, 5000 , new DefaultWatcher());
                logger.info("连接完成");
                //在使用zookeeper对象前，等待连接建立。这里利用Java的CountDownLatch类
                //（java.util.concurrent.CountDownLatch）来阻塞，直到zookeeper实例准备好。
                connectedSignal.await();
                connected = true;
                connectedSignal = null;
            } catch(Exception e) {
                logger.info("连接Zookper失败",e);
                throw new RuntimeException(e);
            }
        }
    }

    public void createNode(String path,String data,CreateMode mode)throws Exception
    {
        Stat stat = zookeeper.exists(path,true);
        if(stat==null)
        {
            logger.info("创建"+path+"节点");
            zookeeper.create(path, data.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE,
                    mode);
        }
    }

    public void deleteNode(String path)throws Exception
    {
        if(zookeeper.exists(path,true)==null)
        {
            return;
        }else
        {
            List<String> childern = zookeeper.getChildren(path,true);
            for(String key:childern)
            {
                if(zookeeper.getChildren(path+"/"+key,true).size()==0)
                {
                    zookeeper.delete(path+"/"+key,-1);
                }else
                {
                    deleteNode(path+"/"+key);
                }
            }
            zookeeper.delete(path,-1);
        }
    }

    public void printNode(String path)throws Exception
    {
        logger.info(path);
        List<String> childern = zookeeper.getChildren(path,true);
        for(String key:childern)
        {
            if(zookeeper.getChildren(path+"/"+key,true).size()==0)
            {
                logger.info(path + "/" + key);
            }else
            {
                printNode(path + "/" + key);
            }
        }
    }

    public void watchNode(String path)throws Exception
    {
        zookeeper.exists(path,true);
        zookeeper.getChildren(path,true);
    }

    public List<String> getChildren(String path) throws Exception
    {
        return zookeeper.getChildren(path,true);
    }

    public byte[] getData(String path) throws Exception
    {
        return zookeeper.getData(path,true,null);
    }



    public synchronized void close()
    {
        if(zookeeper!=null)
        {
            try
            {
                zookeeper.close();
            }catch (Exception e)
            {
                logger.info("关闭Zookper失败",e);
            }
            connected =false;
            connectedSignal = null;
        }
    }

    /**

     * Default watcher

     * Reconnect zookeeperwhen session expired.

     */

    private class DefaultWatcher implements Watcher {
        @Override
        public void process(WatchedEvent event) {
            logger.info("已经触发了" + event.getType() + "事件！::path:"+event.getPath());
            if(event.getState() == Event.KeeperState.SyncConnected && connectedSignal !=null)
            {
                //在收到连接事件KeeperState.SyncConnected时，
                // connectedSignal被创建时，计数为1，代表需要在
                //释放所有等待线程前发生事件的数量。在调用一次countDown()方法后，
                // 此计数器会归零，await操作返回。
                connectedSignal.countDown();
                logger.info("连接结束connectedSignal.countDown...");
                //连接成功
                if(callBack!=null)
                {
                    callBack.afterConnect(event);
                }
            } else if(event.getState() == Event.KeeperState.Expired) {//注意KeeperState的Expired枚举值
                connected =false;
                connect();
            } else if(event.getType() == Event.EventType.NodeChildrenChanged ||
                    event.getType() == Event.EventType.NodeCreated ||
                    event.getType()== Event.EventType.NodeDeleted ||
                    event.getType() == Event.EventType.NodeDataChanged
                    ){
                if(callBack!=null)
                {
                    callBack.watchNodeChange(event);
                }
            }
        }

    }


}
