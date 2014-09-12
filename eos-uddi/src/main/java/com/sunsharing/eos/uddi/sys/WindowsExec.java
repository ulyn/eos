/**
 * @(#)WindowsExec
 * 版权声明 厦门畅享信息技术有限公司, 版权所有 违者必究
 *
 *<br> Copyright:  Copyright (c) 2014
 *<br> Company:厦门畅享信息技术有限公司
 *<br> @author ulyn
 *<br> 14-9-12 下午5:40
 *<br> @version 1.0
 *————————————————————————————————
 *修改记录
 *    修改者：
 *    修改时间：
 *    修改原因：
 *————————————————————————————————
 */
package com.sunsharing.eos.uddi.sys;

import java.io.*;

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
public class WindowsExec {
    public static void main(String args[]) {
//        if (args.length < 1) {
//            System.out.println("USAGE: java GoodWindowsExec <cmd></cmd>");
//            System.exit(1);
//        }
        String cmdStr = "cnpm info ss-eos-test1 version";
        WindowsExec exec = new WindowsExec();
        String result = exec.run(cmdStr);
        System.out.println(result);
    }

    public String run(String runStr) {
        try {
            String[] cmd = new String[3];
            cmd[0] = "cmd.exe";
            cmd[1] = "/C";
            cmd[2] = runStr;
            Runtime rt = Runtime.getRuntime();
            System.out.println("Execing " + cmd[0] + " " + cmd[1] + " " + cmd[2]);
            Process proc = rt.exec(cmd);
            ByteArrayOutputStream errOut = new ByteArrayOutputStream();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            // any error message?
            StreamGobbler errorGobbler = new StreamGobbler(proc.getErrorStream(), "ERROR", errOut);
            // any output?
            StreamGobbler outputGobbler = new StreamGobbler(proc.getInputStream(), "OUTPUT", out);
            // kick them off
            errorGobbler.start();
            outputGobbler.start();
            // any error???
            int exitVal = proc.waitFor();
            System.out.println("ExitValue: " + exitVal);
            if (exitVal != 0) {
                String errorMsg = new String(errOut.toByteArray());
                if (exitVal == 1) {
                    return errorMsg;
                }
                throw new RuntimeException();
            } else {
                return new String(out.toByteArray()).trim();
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return null;
    }

    public class StreamGobbler extends Thread {
        InputStream is;
        OutputStream out;
        String type;

        StreamGobbler(InputStream is, String type, OutputStream out) {
            this.is = is;
            this.out = out;
            this.type = type;
        }

        public void run() {
            try {
                InputStreamReader isr = new InputStreamReader(is);
                BufferedReader br = new BufferedReader(isr);
                String line = null;
                while ((line = br.readLine()) != null) {
                    this.out.write(line.getBytes());
//                    System.out.println(type + ">" + line);
                }
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
    }

}

