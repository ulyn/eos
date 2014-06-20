package com.sunsharing.eos.manager.main;

import com.sunsharing.component.resvalidate.config.ConfigContext;
import com.sunsharing.eos.common.zookeeper.ZookeeperUtils;
import com.sunsharing.eos.manager.agent.ServiceServer;
import com.sunsharing.eos.manager.sys.SysProp;
import com.sunsharing.eos.manager.zookeeper.EosState;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * Created by criss on 14-5-30.
 */
public class EosNodup {

    public static void main(String[] a) {
        ConfigContext.instancesBean(SysProp.class);
        ServiceServer.startUp();
        new Thread() {
            public void run() {
                EosState state = new EosState();
                state.connect();
//                EosMonitor.getInstance().addServiceCallCount("appId","serviceId","1.0");
//                EosMonitor.getInstance().addServiceCallCount("appId","serviceId","1.0");
//                EosMonitor.getInstance().addServiceCallCount("appId","serviceId","1.0");
            }
        }.start();



    }

}
