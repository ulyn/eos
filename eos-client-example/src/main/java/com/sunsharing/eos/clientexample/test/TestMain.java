package com.sunsharing.eos.clientexample.test;

import com.sunsharing.eos.client.EosClient;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by criss on 14-3-25.
 */
public class TestMain {

    public static void main(String[] a) throws Exception {
        EosClient.synStart();
        ExecutorService service = Executors.newFixedThreadPool(100);
        for (int i = 0; i < 100000; i++) {
            final int k = i;
            service.execute(new Runnable() {
                @Override
                public void run() {
//                    TestType test = ServiceContext.getBean(TestType.class);
//                    test.testString(k+"","2");
                }
            });
        }

    }

}
