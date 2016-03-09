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
        if(!utils.isExists(PathConstant.SERVICE_STATE,false))
        {
            return new ArrayList<Map>();
        }
        List<String> eoss = utils.getChildren(PathConstant.SERVICE_STATE,false);
        List<Map> result = new ArrayList<Map>();
        for(String eosId : eoss)
        {
            Map m = new HashMap();
            m.put("eosId",eosId);
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

            if(utils.isExists(PathConstant.EOS_STATE+"/"+eosId,false))
            {
                m.put("status","online");
            }else
            {
                m.put("status","outline");
            }
            result.add(m);
        }

        return result;

    }

    public List<Map> getServices(String eosId)throws Exception
    {
        ZookeeperUtils utils = ZookeeperUtils.getInstance();
        if(!utils.isExists(PathConstant.SERVICE_STATE,false))
        {
            return new ArrayList<Map>();
        }
        List<String> services = utils.getChildren(PathConstant.SERVICE_STATE+"/"+eosId,false);
        List<Map> result = new ArrayList<Map>();
        for(String servicePath : services)
        {
            String path = PathConstant.SERVICE_STATE+"/"+eosId+"/"+servicePath;
            String data = new String(utils.getData(path,false),"UTF-8");
            Map m = new HashMap();
            m.put("servicepath",servicePath);
            m.put("data",data);
            //去掉序列号
            int i = servicePath.lastIndexOf("_");
            String realpath = servicePath.substring(0,i);
            if(utils.isExists(PathConstant.EOS_MONITOR+"/SERVICE/"+realpath,false))
            {
                byte[]a = utils.getData(PathConstant.EOS_MONITOR+"/SERVICE/"+realpath,false);
                System.out.println(PathConstant.EOS_MONITOR+"/SERVICE/"+realpath+"::....存在:"+new String(a,"UTF-8"));
                m.put("num",new String(a,"UTF-8"));
            }else
            {
                System.out.println(PathConstant.EOS_MONITOR+"/SERVICE/"+realpath+"::....不存在:");
                m.put("num","0");
            }
            result.add(m);
        }
        return result;
    }

}
