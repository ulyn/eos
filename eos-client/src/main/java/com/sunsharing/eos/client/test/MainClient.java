package com.sunsharing.eos.client.test;

import com.sunsharing.component.resvalidate.config.ConfigContext;
import com.sunsharing.eos.client.ServiceContext;
import com.sunsharing.eos.client.sys.SysProp;
import com.sunsharing.eos.client.zookeeper.ServiceLocation;
import com.sunsharing.eos.common.zookeeper.ZookeeperUtils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by criss on 14-1-26.
 */
public class MainClient {

    public static void main(String[] a) throws Exception {
        ConfigContext.instancesBean(SysProp.class);
        ServiceContext serviceContext = new ServiceContext("com.sunsharing.eos");

        new Thread() {
            public void run() {
                ServiceLocation.getInstance().connect();
                ZookeeperUtils utils = ZookeeperUtils.getInstance();
            }
        }.start();

        Thread.sleep(5000);
//        MainClient client = new MainClient();
//        client.test();
        ExecutorService excutorService = Executors.newFixedThreadPool(10);
        Date d = new Date();
        for(int i=0;i<1;i++)
        {
//            excutorService.execute(new Runnable() {
//                @Override
//                public void run() {
//                    TestService testService = ServiceContext.getBean(TestService.class);
//                    String msg = testService.sayHello("criss");
//                    //System.out.println("返回参数:"+msg);
//                    if(!msg.equals("criss，你好，我是ulyn"))
//                    {
//                        System.out.println("返回错误参数:"+msg);
//                    }
//                }
//            });
//            excutorService.execute(new Runnable() {
//                @Override
//                public void run() {
//                    TestService testService = ServiceContext.getBean(TestService.class);
//                    String msg = testService.sayHello("hexin");
//                    //System.out.println("返回参数:"+msg);
//                    if(!msg.equals("hexin2，你好，我是ulyn"))
//                    {
//                        System.out.println("返回错误参数:"+msg);
//                    }
//                }
//            });
            TestService testService = ServiceContext.getBean(TestService.class);
            testService.sayHello("hexin");

        }

        Date d2 = new Date();

        System.out.println("处理时间:"+(d2.getTime()-d.getTime()));






        //命令循环
        while (true) {
            BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));

//            Worker w = new Worker();
//            Command sta = new Statistics(w);

            System.out.print("Enter a Command(--help):");
            try {
                String line = stdin.readLine();
                if(line==null)
                {
                    System.exit(0);
                }
                if (line.equals("help")) {
                    System.out.println("sta -> 显示状态");
                } else if (line.equals("send")) {
//                    sta.run();

                } else {
                    System.out.println("Error command line");
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    public void test() {

        TestService testService = ServiceContext.getBean(TestService.class);
        long m = 0, l = 0;
        int count = 1000, size = 1000;
        long s = System.currentTimeMillis();
        for (int i = 0; i < count; i++) {
            testService.sayHello("criss");
        }
        long e = System.currentTimeMillis();
        for (int i = 0; i < count; i++) {
            testService.getList(size);
        }
        long ll = System.currentTimeMillis();

        System.out.println("执行sayHello(" + count + "次)的平均耗时：" + (e - s) / count);
        System.out.println("执行getList(" + size + ")(" + count + "次)的平均耗时：" + (ll - e) / count);
    }
}
