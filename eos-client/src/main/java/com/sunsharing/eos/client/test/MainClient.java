package com.sunsharing.eos.client.test;

import com.sunsharing.component.resvalidate.config.ConfigContext;
import com.sunsharing.eos.client.ServiceContext;
import com.sunsharing.eos.client.sys.SysProp;
import com.sunsharing.eos.client.zookeeper.ServiceLocation;
import com.sunsharing.eos.common.zookeeper.ZookeeperUtils;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * Created by criss on 14-1-26.
 */
public class MainClient {

    public static void main(String[] a) throws Exception {
        ConfigContext.instancesBean(SysProp.class);
        ServiceContext serviceContext = new ServiceContext(null, "com.sunsharing.eos");

        new Thread() {
            public void run() {
                ServiceLocation.getInstance().connect();
                ZookeeperUtils utils = ZookeeperUtils.getInstance();
            }
        }.start();

        Thread.sleep(5000);
        MainClient client = new MainClient();
        client.test();


        //命令循环
        while (true) {
            BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));

//            Worker w = new Worker();
//            Command sta = new Statistics(w);

            System.out.print("Enter a Command(--help):");
            try {
                String line = stdin.readLine();
                if (line.equals("help")) {
                    System.out.println("sta -> 显示状态");
                } else if (line.equals("sta")) {
//                    sta.run();
                } else {
                    System.out.println("Error command line");
                }

            } catch (Exception e) {

            }
        }

    }

    public void test() {
        TestService testService = ServiceContext.getBean(TestService.class);
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
    }
}
