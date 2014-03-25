package com.sunsharing.component.test;

import com.sunsharing.eos.client.EosInit;
import com.sunsharing.eos.client.ServiceContext;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by criss on 14-3-25.
 */
public class TestMain {

    public static void main(String[]a)throws Exception
    {
        EosInit.synStart("com.sunsharing");
        ExecutorService service =  Executors.newFixedThreadPool(100);
        for(int i=0;i<1;i++)
        service.execute(new Runnable() {
            @Override
            public void run() {
                TestType test = ServiceContext.getBean(TestType.class);
                double d = test.testDouble(1);
                if(d!=0)
                {
                    System.out.println("错了");
                }
                String s = test.testString("1","2");
                if(!s.equals("我是正真的服务方"))
                {
                    System.out.println("错了");
                }
            }
        });

    }

}
