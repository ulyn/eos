package com.sunsharing.eos.common.zookeeper;

import org.apache.log4j.Logger;
import org.apache.zookeeper.CreateMode;

/**
 * Created by criss on 14-1-28.
 */
public class PathConstant {

    static Logger logger = Logger.getLogger(PathConstant.class);

    public static String BASE = "/E3";
    /**存储服务状态的所有节点*/
    public static String SERVICE_STATE="/E3/SERVICE_STATE";
    public static String SERVICE_STATE_APPS="/E3/SERVICE_STATE/APPS";
    public static String SERVICE_STATE_EOS="/E3/SERVICE_STATE/EOS";
    /**存储EOS状态*/
    public static String EOS_STATE = "/E3/EOS_STATE";
    /**监控数据节点*/
    public static String EOS_MONITOR = "/E3/EOS_MONITOR";
    /**授权*/
    public static String ACL = "/E3/ACL";
    /**应用KEY*/
    public static String APPID_KEY="appId";
    /**ServiceId的KEY*/
    public static String SERVICE_ID_KEY="serviceId";
    /**Version的KEY*/
    public static String VERSION_KEY="version";
    /**存储服务状态更改*/
    public static String SERVICE_STATE_PATH = "/E3/SERVICE_STATE/APPS/${app_id}/${service_path}";
    /**EOS服务状态*/
    public static String EOS_STATE_PATH = "/E3/SERVICE_STATE/EOS/${eos_id}/${app_id}";
    /**服务授权*/
    public static String ACL_PATH = "/E3/SERVICE_STATE/ACL/${appcode}${serviceId}/${methodVersion}";

    public static void initEOSPath()
    {
        ZookeeperUtils utils = ZookeeperUtils.getInstance();
        //初始化创建节点
        try {
            if (!utils.isExists(PathConstant.BASE, false)) {
                utils.createNodeNowatch(PathConstant.BASE,"", CreateMode.PERSISTENT);
                utils.createNodeNowatch(PathConstant.SERVICE_STATE,"",CreateMode.PERSISTENT);
                utils.createNodeNowatch(PathConstant.EOS_STATE,"",CreateMode.PERSISTENT);
                utils.createNodeNowatch(PathConstant.EOS_MONITOR,"",CreateMode.PERSISTENT);
                utils.createNodeNowatch(PathConstant.ACL,"",CreateMode.PERSISTENT);
                utils.createNodeNowatch(PathConstant.SERVICE_STATE_APPS,"",CreateMode.PERSISTENT);
                utils.createNodeNowatch(PathConstant.SERVICE_STATE_EOS,"",CreateMode.PERSISTENT);
            }
        }catch (Exception e)
        {
            logger.info("初始化节点失败",e);
        }
    }

}
