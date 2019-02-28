package com.sunsharing.eos.uddi.sys;

import com.sunsharing.component.resvalidate.config.ConfigContext;
import com.sunsharing.eos.uddi.zookeeper.StatZoo;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServlet;
import java.util.Properties;

/**
 *<pre><b><font color="blue">SysInit</font></b></pre>
 *
 * scp -P 32768 /Users/criss/Desktop/projectDev/eos_git/eos-uddi/target/eos-uddi-3.3.1-SNAPSHOT.jar root@192.168.0.62:/opt/bak/eos-uddi-3.3.1.jar
 * scp -P 32768 -r /Users/criss/Desktop/projectDev/eos_git/eos-uddi/src/main/webapp/templates root@192.168.0.62:/opt/bak/templates
 * scp -P 32768 -r /Users/criss/Desktop/projectDev/eos_git/eos-uddi/src/main/webapp/js root@192.168.0.62:/opt/bak/js
 * 249  cp eos-uddi-3.3.1.jar  /opt/eos-uddi/lib/
 250  cp -rf js /opt/eos-uddi/webapp/
 251  cp -rf templates/ /opt/eos-uddi/webapp/
 *
 *
 *<pre><b>&nbsp;--系统初始化--</b></pre>
 * JDK版本：JDK1.5.0
 * @author  <b>李自立</b> 
 */
public class SysInit extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	
	/** 记录日志 */
	private static Logger logger = Logger.getLogger(SysInit.class);

    private static Properties pro;

    public static String path = "";

	public void init(){

        ConfigContext.instancesBean(SysProp.class);
		
		ServletContext sc = this.getServletContext();
		logger.info("系统开始初始化...");
		ApplicationContext ctx = WebApplicationContextUtils.getRequiredWebApplicationContext(sc);
		ServiceLocator.init(ctx);
		logger.info("系统初始化上下文结束...");
		
		logger.info("初始化其它参数...");
		
		//初始化配置文件信息和数据库全局参数url信息
        //URL url = this.getClass().getClassLoader().getResource("/mc");
        path = getServletContext().getRealPath("/");

        new Thread(){
            public void run()
            {
                StatZoo s = new StatZoo();
                s.connect();
            }
        }.start();



	}
	
	@Override
	public void destroy() {
		super.destroy();
		try {

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
