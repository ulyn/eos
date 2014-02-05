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
        long m = 0, l = 0;
        int count = 1000, size = 1000;
        for (int i = 0; i < count; i++) {
            long s = System.currentTimeMillis();
            testService.sayHello("criss");
            long e = System.currentTimeMillis();
            m += (e - s);
        }
        for (int i = 0; i < count; i++) {
            long s = System.currentTimeMillis();
            testService.getList(size);
            long e = System.currentTimeMillis();
            l += (e - s);
        }

        System.out.println("执行sayHello(" + count + "次)的平均耗时：" + m / count);
        System.out.println("执行getList(" + size + ")(" + count + "次)的平均耗时：" + l / count);

        System.out.println("执行测试多线程");
        for (int i = 0; i < 100; i++) {
            new Thread(new Test(testService)).start();
        }
        System.out.println("执行测试多线程结束");
    }

}

class Test implements Runnable {
    private TestService testService;

    public Test(TestService testService) {
        this.testService = testService;
    }

    @Override
    public void run() {
        testService.getList(100);
    }
}

