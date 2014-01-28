package com.sunsharing.eos.manager.main;

import com.sunsharing.component.resvalidate.config.ConfigContext;
import com.sunsharing.eos.manager.sys.SysProp;
import com.sunsharing.eos.manager.zookeeper.EosState;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * Created by criss on 14-1-27.
 */
public class Eos {

    public static void main(String []a)
    {
        ConfigContext.instancesBean(SysProp.class);

        new Thread(){
            public void run()
            {
                EosState state = new EosState();
                state.connect();
            }
        }.start();


        //命令循环
        while(true)
        {
            BufferedReader stdin =new BufferedReader(new InputStreamReader(System.in));

//            Worker w = new Worker();
//            Command sta = new Statistics(w);

            System.out.print("Enter a Command(--help):");
            try
            {
                String line = stdin.readLine();
                if(line.equals("help"))
                {
                    System.out.println("sta -> 显示状态");
                }else if(line.equals("sta"))
                {
//                    sta.run();
                }
                else
                {
                    System.out.println("Error command line");
                }

            }catch (Exception e)
            {

            }
        }
    }

}
