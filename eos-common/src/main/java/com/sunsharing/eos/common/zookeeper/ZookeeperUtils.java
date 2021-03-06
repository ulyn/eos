package com.sunsharing.eos.common.zookeeper;

import com.alibaba.fastjson.JSONObject;
import org.apache.log4j.Logger;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.util.*;
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

    protected List<ZookeeperCallBack> callBacks= new ArrayList<ZookeeperCallBack>();

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

//    public List<ZookeeperCallBack> getCallBacks() {
//        return callBacks;
//    }

    public synchronized void addCallBack(ZookeeperCallBack callBack )
    {
        boolean contains = false;
        for(Iterator iter = callBacks.iterator();iter.hasNext();)
        {
            ZookeeperCallBack call = (ZookeeperCallBack)iter.next();
            if(call.getClass().getName().equals(callBack.getClass().getName()))
            {
                contains = true;
            }
        }
        if(!contains)
        {
            callBacks.add(callBack);
        }
        logger.info("callBacksize:"+callBacks.size());
    }

//    public void setCallBacks(List<ZookeeperCallBack> callBacks) {
//        this.callBacks = callBacks;
//    }

    private ZookeeperUtils()
    {

    }

    static ZookeeperUtils utils = new ZookeeperUtils();

    public static ZookeeperUtils getInstance()
    {
        return utils;
    }

    public static ZookeeperUtils newInstance()
    {
        return new ZookeeperUtils();
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
                String connectString = zooKeeperIP +":"+zooKeeperPort;
                if(zooKeeperIP.indexOf(",")!=-1)
                {
                    connectString = zooKeeperIP;
                }
                if(zookeeper != null && zookeeper.getState().isAlive()){
                    zookeeper.close();
                }
                zookeeper = new ZooKeeper(connectString , 30000 , new DefaultWatcher());
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
    public void createNodeNowatch(String path,String data,CreateMode mode)throws Exception
    {
        Stat stat = zookeeper.exists(path,false);
        if(stat==null)
        {
            logger.info("创建"+path+"节点");
            zookeeper.create(path, data.getBytes("UTF-8"), ZooDefs.Ids.OPEN_ACL_UNSAFE,
                    mode);
        }else
        {
            zookeeper.setData(path,data.getBytes("UTF-8"),-1);
        }
    }


    public void createNode(String path,String data,CreateMode mode)throws Exception
    {
        Stat stat = zookeeper.exists(path,false);
        if(stat==null)
        {
            logger.info("创建"+path+"节点");
            zookeeper.create(path, data.getBytes("UTF-8"), ZooDefs.Ids.OPEN_ACL_UNSAFE,
                    mode);
        }else
        {
            zookeeper.setData(path,data.getBytes("UTF-8"),-1);
        }
    }

    public void deleteNode(String path)throws Exception
    {
        if(zookeeper.exists(path,false)==null)
        {
            return;
        }else
        {
            List<String> childern = zookeeper.getChildren(path,false);
            for(String key:childern)
            {
                if(zookeeper.getChildren(path+"/"+key,false).size()==0)
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

        if(zookeeper.exists(path,false)==null)
        {
            return;
        }
        logger.info(path);
        List<String> childern = zookeeper.getChildren(path,false);
        for(String key:childern)
        {
            String pre = "";
            if(path.equals("/"))
            {
                pre = "";
            }else
            {
                pre = path;
            }
            if(zookeeper.getChildren(pre+"/"+key,false).size()==0)
            {
                logger.info(pre + "/" + key);
            }else
            {
                printNode(pre + "/" + key);
            }
        }
    }

    public void watchNode(String path)throws Exception
    {
        Stat s = zookeeper.exists(path,true);
//        if(s!=null)
//        {
//            zookeeper.getChildren(path,true);
//        }
    }
    public void watchChildren(String path) throws Exception
    {
        Stat s = zookeeper.exists(path,false);
        if(s!=null)
        {
            zookeeper.getChildren(path,true);
        }
    }

    public boolean isExists(String path,boolean watch)throws Exception
    {
        return zookeeper.exists(path,watch)!=null;
    }


    public List<String> getChildren(String path,boolean watch) throws Exception
    {
        return zookeeper.getChildren(path,watch);
    }

    public List<String> getChildrenNotWatch(String path,boolean watch) throws Exception
    {
        return zookeeper.getChildren(path,watch);
    }

    public byte[] getData(String path,boolean watch) throws Exception
    {
        return zookeeper.getData(path,watch,null);
    }

    public void setData(String path,byte[]arr)throws Exception
    {
         zookeeper.setData(path,arr,-1);
    }

    public boolean createEleSerNode(String path,String data,CompareData compareData) throws Exception
    {
        int index = path.lastIndexOf("/");
        String name = path.substring(index+1);
        String parentPath = path.substring(0,index);
        List<String> list = utils.getChildrenNotWatch(parentPath,false);
        boolean exist = false;
        for(String p:list) {
            byte[] bytes = utils.getData(parentPath+"/"+p,false);
            if (p.startsWith(name)
                    && compareData.compare(data,new String(bytes,"UTF-8")))
            {
                logger.info("已经存在删除节点:" + parentPath + "/" + p);
                deleteNode(parentPath + "/" + p);
                break;
            }
        }
        createNodeNowatch(path, data, CreateMode.EPHEMERAL_SEQUENTIAL);
        return true;
    }

    public interface CompareData
    {
        boolean compare(String d1,String d2);
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
            logger.info("已经触发了" + event.getType() + "事件！" + event.toString());
            if(event.getState() == Event.KeeperState.SyncConnected && connectedSignal !=null)
            {
                //在收到连接事件KeeperState.SyncConnected时，
                // connectedSignal被创建时，计数为1，代表需要在
                //释放所有等待线程前发生事件的数量。在调用一次countDown()方法后，
                // 此计数器会归零，await操作返回。
                connectedSignal.countDown();
                logger.info("连接结束connectedSignal.countDown...:callBacks:"+callBacks.size());
                //连接成功
                if(callBacks!=null)
                {
                    synchronized (callBacks)
                    {
                        for(ZookeeperCallBack callBack:callBacks)
                        {
                            callBack.afterConnect(event);
                        }
                    }
                }
            } else if(event.getState() == Event.KeeperState.Expired
                || event.getState() == Event.KeeperState.Disconnected) {//注意KeeperState的Expired枚举值
                connected =false;
                connect();
            } else if(event.getType() == Event.EventType.NodeChildrenChanged ||
                    event.getType() == Event.EventType.NodeCreated ||
                    event.getType()== Event.EventType.NodeDeleted ||
                    event.getType() == Event.EventType.NodeDataChanged
                    ){
                logger.info("节点有变化...:callBacks:"+callBacks.size());
                if(callBacks!=null)
                {
                    for(ZookeeperCallBack callBack:callBacks)
                    {
                        callBack.watchNodeChange(event);
                    }

                }
            }
        }

    }

    public  boolean isFull(String appId) throws Exception
    {
        int continueTimes = 0;
        List<String> onlineServices = null;

        while(true) {
            Map dataMap = new HashMap();
            Map ipPortServiceNum = new HashMap();
            Map realServiceNum = new HashMap();
            try {
                onlineServices = utils.getChildren(PathConstant.SERVICE_STATE_APPS + "/" + appId, true);
                //校验完整性

                for (String servicePath : onlineServices) {
                    byte[] data = utils.getData(PathConstant.SERVICE_STATE_APPS + "/" + appId + "/" + servicePath, false);
                    JSONObject serviceData = JSONObject.parseObject(new String(data, "UTF-8"));
                    String ip = (String) serviceData.get("ip");
                    String port = serviceData.get("port").toString();
                    if (serviceData.get("totalServiceSize") == null) {
                        logger.error("appId:" + appId + "服务注册的版本不兼容，请先升级服务EOS版本3.1.0");
                        return true;
                        //throw new RuntimeException("服务注册的版本不兼容，请先升级服务EOS版本");
                    }
                    int totalServiceSize = serviceData.getInteger("totalServiceSize");
                    ipPortServiceNum.put(ip + port, totalServiceSize);

                    if (realServiceNum.get(ip + port) == null) {
                        realServiceNum.put(ip + port, new Integer(0));
                    }
                    Integer real = (Integer) realServiceNum.get(ip + port);
                    real++;
                    realServiceNum.put(ip + port, real);
                    dataMap.put(servicePath, serviceData);
                }
            }catch (Exception e)
            {
                logger.info("加载APP出错了,ZooKeeper连接有点问题,重试...");
                Thread.sleep(1000);
                continue;
            }

            int rst = isAllAppRight(appId,ipPortServiceNum,realServiceNum);
            if(rst == 1)
            {
                //全部正常
                break;
            }else{
                Thread.sleep(1000);
                continueTimes++;
            }

            if(continueTimes >10)
            {
                if(rst == 2)
                {
                    logger.error("appId:"+appId+"注册服务完整性校验部分不完整，...");
                    return true;
                }else {
                    logger.error("appId:"+appId+"注册服务完整性校验有问题，不更新服务...");
                }
                return false;
            }

        }
        return true;
    }

    /**
     * 判断是否所有App返回
     * @return
     * 1 所有都正常
     * 2 部分正常
     * 3 异常
     *
     */
    private int isAllAppRight(String appId,Map ipPortServiceNum,Map realServiceNum)
    {
        int match = 0;
        int notMatch = 0;
        for(Iterator ipport = ipPortServiceNum.keySet().iterator();ipport.hasNext();)
        {
            String tmp = (String)ipport.next();
            Integer serviceNum = (Integer)ipPortServiceNum.get(tmp);
            Integer real = (Integer)realServiceNum.get(tmp);
            if(serviceNum == real )
            {
                logger.info(tmp+":appId:"+appId+":"+tmp+":加载服务个数为"+real+",和真实一致");
                match++;
            }else
            {
                logger.info(tmp+":appId:"+appId+":"+tmp+":加载服务个数为"+real+",和服务注册为"+serviceNum+"不一致");
                notMatch++;
            }
        }
        if(match>0 && notMatch==0)
        {
            return 1;
        }else if(notMatch>0 && match==0)
        {
            return 3;
        }else if(match>0 && notMatch>=0)
        {
            return 2;
        }else {
            return 1;
        }
    }


}
