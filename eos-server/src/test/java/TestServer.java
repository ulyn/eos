import com.alibaba.fastjson.JSONObject;
import com.sunsharing.eos.server.zookeeper.ServiceRegister;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * Created by criss on 14-1-26.
 */
public class TestServer {

    public static void main(String[]a) throws Exception
    {
        new Thread(){
            public void run()
            {
                ServiceRegister serviceRegister =  ServiceRegister.getInstance();
                serviceRegister.init();
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
