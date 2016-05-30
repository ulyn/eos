/**
 * @(#)Main
 * 版权声明 厦门畅享信息技术有限公司, 版权所有 违者必究
 *
 *<br> Copyright:  Copyright (c) 2016
 *<br> Company:厦门畅享信息技术有限公司
 *<br> @author ulyn
 *<br> 16-3-2 下午3:35
 *<br> @version 1.0
 *————————————————————————————————
 *修改记录
 *    修改者：
 *    修改时间：
 *    修改原因：
 *————————————————————————————————
 */
package test.sunsharing.eos.server;

import com.sunsharing.eos.server.EosServer;
import org.junit.Before;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.InputStreamReader;

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
public class ServerMain {

    @Before
    public void startServer() {
//        EosServer.start("test.sunsharing.eos.server",new MapPropReaderConverter(new HashMap<String, Object>()));
        EosServer.start("test.sunsharing.eos.server");
//        EosServer.start("test.sunsharing.eos.server",
//                new HttpPropReaderConverter("http://192.168.0.235:8100/getConfig.do?appCode=test&runCode=test-client"));
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
    @Test
    public void testInt() {

    }
}

