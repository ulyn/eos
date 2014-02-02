/**
 * @(#)TestServiceContext
 * 版权声明 厦门畅享信息技术有限公司, 版权所有 违者必究
 *
 *<br> Copyright:  Copyright (c) 2014
 *<br> Company:厦门畅享信息技术有限公司
 *<br> @author ulyn
 *<br> 14-2-1 上午12:52
 *<br> @version 1.0
 *————————————————————————————————
 *修改记录
 *    修改者：
 *    修改时间：
 *    修改原因：
 *————————————————————————————————
 */

import com.sunsharing.eos.client.ServiceContext;
import com.sunsharing.eos.client.test.TestService;

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
public class TestServiceContext {
    public static void main(String[] args) {
        ServiceContext serviceContext = new ServiceContext(null, "com.sunsharing.eos");
        TestService testService = serviceContext.getBean(TestService.class);
        long m = 0;
        int count = 1000;
        for (int i = 0; i < count; i++) {
            long s = System.currentTimeMillis();
            System.out.println("called:" + testService.sayHello("criss"));
            long e = System.currentTimeMillis();
            m += (e - s);
        }
        System.out.println("执行" + count + "次的平均耗时：" + m / count);
    }
}

