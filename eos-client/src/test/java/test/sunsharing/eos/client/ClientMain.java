/**
 * @(#)Main
 * 版权声明 厦门畅享信息技术有限公司, 版权所有 违者必究
 *
 *<br> Copyright:  Copyright (c) 2016
 *<br> Company:厦门畅享信息技术有限公司
 *<br> @author ulyn
 *<br> 16-3-2 下午4:00
 *<br> @version 1.0
 *————————————————————————————————
 *修改记录
 *    修改者：
 *    修改时间：
 *    修改原因：
 *————————————————————————————————
 */
package test.sunsharing.eos.client;

import com.sunsharing.eos.client.EosClient;
import com.sunsharing.eos.client.rpc.DynamicRpc;
import com.sunsharing.eos.common.ServiceRequest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <pre></pre>
 * <br>----------------------------------------------------------------------
 * <br> <b>功能描述:</b>
 * <br>
 * <br> 注意事项:
 * <br>
 * <br>
 * <br>----------------------------------------------------------------------
 * <br>
 */
public class ClientMain {

    @Before
    public void startClient(){
        EosClient.synStart();
    }
    @After
    public void closeClient(){
        EosClient.start();
    }
    @Test
    public void testEmptyArgs(){
        ServiceRequest request = new ServiceRequest
                .Builder("ihome","mainService","testEmptyArgs","1.0")
                .build();
        DynamicRpc.invoke(request);
        System.out.println("testEmptyArgs调用结束");
    }


    @Test
    public void testInt(){
        ServiceRequest request = new ServiceRequest
                .Builder("ihome","mainService","testInt","1.0")
                .setParameter("i",1)
                .build();
        int i = DynamicRpc.invoke(request,int.class);
        System.out.println("testInt调用结果："+i);
    }

    @Test
    public void testFloat(){
        ServiceRequest request = new ServiceRequest
                .Builder("ihome","mainService","testFloat","1.0")
                .setParameter("f",1.32)
                .build();
        float i = DynamicRpc.invoke(request,float.class);
        System.out.println("testFloat调用结果："+i);
    }

    @Test
    public void testString(){
        ServiceRequest request = new ServiceRequest
                .Builder("ihome","mainService","testString","1.0")
                .setParameter("s",1)
                .setParameter("sw", "12334")
                .build();
        int i = DynamicRpc.invoke(request,int.class);
        System.out.println("调用结果："+i);
    }

    @Test
    public void testMap(){
        Map map = new HashMap();
        map.put("wan","社区");
        ServiceRequest request = new ServiceRequest
                .Builder("ihome","mainService","testMap","1.0")
                .setParameter("m",map)
                .setParameter("l2","智慧")
                .build();
        Map result = DynamicRpc.invoke(request,Map.class);
        System.out.println("testMap调用结果："+ result);
    }

    @Test
    public void testListMap(){
        List list = new ArrayList();
        Map map = new HashMap();
        map.put("wan","社区");
        list.add(map);
        ServiceRequest request = new ServiceRequest
                .Builder("ihome","mainService","testListMap","1.0")
                .setParameter("list",list)
                .build();
        List result = DynamicRpc.invoke(request,List.class);
        System.out.println("testListMap调用结果："+ result);
    }


    @Test
    public void testVoid(){
        ServiceRequest request = new ServiceRequest
                .Builder("ihome","mainService","testVoid","1.0")
                .setParameter("name","幼幼")
                .build();
        DynamicRpc.invoke(request);
        System.out.println("testVoid调用结果结束");
    }
}

