import com.sunsharing.eos.client.zookeeper.ServiceLocation;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * Created by criss on 14-1-29.
 */
public class TestClient {

    public static void main(String[]a)
    {
        new Thread(){
            public void run()
            {
                ServiceLocation.getInstance().connect();
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
