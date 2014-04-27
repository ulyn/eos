package com.sunsharing.eos.manager.zookeeper;

import com.sunsharing.component.utils.base.DateUtils;
import com.sunsharing.eos.common.utils.StringUtils;
import com.sunsharing.eos.common.zookeeper.PathConstant;
import com.sunsharing.eos.common.zookeeper.ZookeeperUtils;
import com.sunsharing.eos.manager.sys.SysProp;
import org.apache.log4j.Logger;
import org.apache.zookeeper.CreateMode;

import java.util.*;

/**
 * Created by criss on 14-2-4.
 */
public class EosMonitor {
    Logger logger = Logger.getLogger(EosMonitor.class);
    private  int eosnum = 0;
    private  Map<String,Integer> serviceNum = new HashMap<String, Integer>();

    static EosMonitor monitor = null;
    private Timer timer = null;

    public static synchronized EosMonitor getInstance()
    {
        if(monitor==null)
        {
            monitor = new EosMonitor();
            monitor.timer = new Timer();
            monitor.timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    monitor.sync();
                }
            },60000,60000);
        }

        return monitor;
    }

    public synchronized void addServiceCallCount(String appId,String serviceI,String version)
    {

        String servicePath = appId+serviceI+version;
        if(serviceNum.get(servicePath)==null)
        {
            serviceNum.put(servicePath,0);
        }
        eosnum++;
        int i = serviceNum.get(servicePath);
        i++;
        serviceNum.put(servicePath,i);
    }

    public  void sync()
    {

        if(eosnum == 0)
        {
            return;
        }
        int eosnum_clone = 0;
        Map<String,Integer> serviceNum_clone = null;
        synchronized (serviceNum)
        {
            eosnum_clone = eosnum;
            serviceNum_clone = new  HashMap<String,Integer>();
            for(String key:serviceNum.keySet())
            {
                int value = serviceNum.get(key);
                serviceNum_clone.put(key,value);
            }
            eosnum = 0;
            serviceNum = new HashMap<String,Integer>();
        }
        try
        {
            ZookeeperUtils utils = ZookeeperUtils.getInstance();
            utils.createNode(PathConstant.EOS_MONITOR,"", CreateMode.PERSISTENT);
            utils.createNode(PathConstant.EOS_MONITOR+"/EOS","", CreateMode.PERSISTENT);
            utils.createNode(PathConstant.EOS_MONITOR+"/SERVICE","", CreateMode.PERSISTENT);
            String eosId = SysProp.eosId;
            if(!utils.isExists(PathConstant.EOS_MONITOR+"/EOS/"+eosId,false))
            {
                utils.createNode(PathConstant.EOS_MONITOR+"/EOS/"+eosId,"",CreateMode.PERSISTENT);
                utils.setData(PathConstant.EOS_MONITOR+"/EOS/"+eosId,"0".getBytes());
            }

            String old = new String(utils.getData(PathConstant.EOS_MONITOR+"/EOS/"+eosId,false),"UTF-8");
            if(StringUtils.isBlank(old))
            {
                old = "0";
            }
            logger.info("eosnum:old:"+old);
            logger.info("eosnum:"+((eosnum_clone + new Integer(old)) + ""));
            utils.setData(PathConstant.EOS_MONITOR + "/EOS/" + eosId,
                    ((eosnum_clone + new Integer(old)) + "").getBytes("UTF-8"));

            for(String servicePath:serviceNum_clone.keySet())
            {
                if(!utils.isExists(PathConstant.EOS_MONITOR+"/SERVICE/"+servicePath,false))
                utils.createNode(PathConstant.EOS_MONITOR+"/SERVICE/"+servicePath,
                        "", CreateMode.PERSISTENT);
                if(!utils.isExists(PathConstant.EOS_MONITOR+"/SERVICE/"+servicePath,false))
                {
                    utils.createNode(PathConstant.EOS_MONITOR+"/SERVICE/"+servicePath,"0",
                            CreateMode.PERSISTENT);
                }
                old = new String(utils.getData(PathConstant.EOS_MONITOR+"/SERVICE/"+servicePath,false),"UTF-8");
                if(StringUtils.isBlank(old))
                {
                    old = "0";
                }
                logger.info("servicenum:old:"+old);
                logger.info("servicenum:"+servicePath+"/"+((serviceNum_clone.get(servicePath)+new Integer(old))));
                utils.setData(PathConstant.EOS_MONITOR+"/SERVICE/"+servicePath,
                        (""+(serviceNum_clone.get(servicePath)+new Integer(old))).getBytes("UTF-8"));
            }


        }catch (Exception e)
        {
            logger.error("监控序列化失败",e);
        }

    }



}
