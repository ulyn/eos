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
import org.junit.Before;
import org.junit.Test;

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

    @Test
    public void testInt(){
        ServiceRequest request = new ServiceRequest
                .Builder("ihome","mainService","testInt","1.0")
                .setParameter("a",1)
                .build();
        int i = DynamicRpc.invoke(request,int.class);
        System.out.println("调用结果："+i);
    }
}

