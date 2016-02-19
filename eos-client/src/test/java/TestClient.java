import com.sunsharing.eos.client.zookeeper.ServiceLocation;
import com.sunsharing.eos.common.zookeeper.PathConstant;
import com.sunsharing.eos.common.zookeeper.ZookeeperUtils;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * Created by criss on 14-1-29.
 */
public class TestClient {

    public static void main(String[]a)
    {
        String path = "/abc/abcd";
        int index = path.lastIndexOf("/");
        String name = path.substring(index);
        System.out.println(name);
    }

}
