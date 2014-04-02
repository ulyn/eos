package com.sunsharing.component.test;

import com.sunsharing.eos.common.rpc.RpcContext;
import com.sunsharing.eos.common.rpc.RpcContextContainer;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by criss on 14-2-11.
 */
@Service
public class TestServiceImpl implements TestType {
    @Override
    public int testInt(int i) {

        System.out.println(RpcContextContainer.getRpcContext());
        try
        {
            Thread.sleep(50000);
        }catch (Exception e)
        {

        }
        return 0;
    }

    @Override
    public double testDouble(double d) {
        return 0;
    }

    @Override
    public float testFloat(float f) {
        return 0;
    }

    @Override
    public String testString(String s, String sw) {
        RpcContext rpcContext = RpcContextContainer.getRpcContext();
        //rpcContext.getAttribute("")
        if(!s.equals(rpcContext.getAttribute("a")))
        {
            System.out.println("rpcContext:"+rpcContext.getAttributeMap());
            throw new RuntimeException("参数错了");
        }else
        {
            //System.out.println("a:"+rpcContext.getAttribute("a"));
        }
        return "我是正真的服务方";
    }

    @Override
    public Map testMap(Map m, String l2) {
        Map n = new HashMap();
        n.put("real","resasddl");
        return n;
    }

    @Override
    public List testListMap(List list) {
        return null;
    }

    public void testVoid(String name)
    {

    }
}
