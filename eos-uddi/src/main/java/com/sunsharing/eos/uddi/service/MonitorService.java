package com.sunsharing.eos.uddi.service;

import com.sunsharing.eos.common.zookeeper.PathConstant;
import com.sunsharing.eos.common.zookeeper.ZookeeperUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by criss on 14-2-4.
 */
@org.springframework.stereotype.Service
public class MonitorService {

    public List<Map> getEosStat()throws Exception
    {
        ZookeeperUtils utils = ZookeeperUtils.getInstance();
        if(!utils.isExists(PathConstant.SERVICE_STATE_EOS,false))
        {
            return new ArrayList<Map>();
        }
        List<String> eoss = utils.getChildren(PathConstant.SERVICE_STATE_EOS,false);
        List<Map> result = new ArrayList<Map>();
        for(String eosId : eoss)
        {
            Map m = new HashMap();
            m.put("eosId",eosId);
            m.put("name",eosId);
            m.put("type","eos");
            String path = PathConstant.EOS_MONITOR+"/EOS/"+
                    eosId;
            System.out.println(path);
            if(utils.isExists(path,false))
            {
                m.put("num",new String(utils.getData(path,false),"UTF-8"));
            }else
            {
                m.put("num","0");
            }

            m.put("status","online");

            result.add(m);
        }
        List<String> apps = utils.getChildren(PathConstant.SERVICE_STATE_APPS,false);
        for(String appId : apps)
        {
            Map m = new HashMap();
            m.put("appId",appId);
            m.put("name",appId);
            m.put("type","app");
            List<String> services = utils.getChildren(PathConstant.SERVICE_STATE_APPS+"/"+appId,false);
            //m.put("num","0");
            m.put("num",services.size());
            m.put("status","online");

            result.add(m);
        }

        return result;

    }

    public List<Map> getServices(String appId)throws Exception
    {
        ZookeeperUtils utils = ZookeeperUtils.getInstance();
        if(!utils.isExists(PathConstant.SERVICE_STATE_APPS,false))
        {
            return new ArrayList<Map>();
        }
        List<String> services = utils.getChildren(PathConstant.SERVICE_STATE_APPS+"/"+appId,false);
        List<Map> result = new ArrayList<Map>();
        for(String servicePath : services)
        {
            String path = PathConstant.SERVICE_STATE_APPS+"/"+appId+"/"+servicePath;
            String data = new String(utils.getData(path,false),"UTF-8");
            Map m = new HashMap();
            m.put("servicepath",servicePath);
            m.put("data",data);

            result.add(m);
        }
        return result;
    }

}
