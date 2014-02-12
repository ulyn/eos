package com.sunsharing.eos.uddi.main;

import com.sunsharing.component.resvalidate.config.ConfigContext;
import com.sunsharing.eos.uddi.sys.SysProp;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;

import java.io.File;
import java.net.URLDecoder;

/**
 * Created by criss on 14-1-29.
 */
public class Main {

    public static void main(String[] a) throws Exception {
        start(8100);
    }

    public static void start(int port) throws Exception {

        ConfigContext.instancesBean(SysProp.class);

        String webapp = "/Users/criss/Desktop/projectDev/eosgit/eos-uddi/src/main/webapp";

        if (port == 0) {
            port = 8099;
        }

        Server server = new Server(port);

        WebAppContext context = new WebAppContext();
        context.setContextPath("/");
        context.setResourceBase(getPath(webapp));
        //context.setWar("E:/share/test/struts2-blank.war");
        server.setHandler(context);
        server.start();
    }

    public static String getPath(String webapp) {
        String classPath = null;
        try {
            classPath = getClassPath();
            if (classPath.endsWith("classes/")) {
                classPath = webapp;
            } else {
                classPath = classPath.substring(0, classPath.length() - 4) + "webapp/";
            }
        } catch (Exception e) {
            throw new RuntimeException("获取ClassPath路径出错");
        }
        if (!new File(classPath).exists()) {
            new File(classPath).mkdirs();
        }
        return classPath;
    }

    public static String getClassPath() throws Exception {

        String keyfilePath = URLDecoder.decode(Main.class.getProtectionDomain().
                getCodeSource().getLocation().getFile(), "UTF-8");
        keyfilePath = keyfilePath.replaceAll("\\\\", "/");
        File temp = new File(keyfilePath);
        if (temp.isFile() && keyfilePath.endsWith("jar") == true) {
            keyfilePath = keyfilePath.substring(0, keyfilePath.lastIndexOf("/")) + "/";
        } else if (keyfilePath.indexOf("classes") != -1) {
            keyfilePath = keyfilePath.substring(0, keyfilePath.indexOf("classes") + 7) + "/";
        }
        return keyfilePath;
    }

}
