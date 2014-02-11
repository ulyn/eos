package com.sunsharing.component.sys;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;


/**
 *<pre><b><font color="blue">SysParam</font></b></pre>
 *
 *<pre><b>&nbsp;--描述说明--</b></pre>
 * JDK版本：JDK1.5.0
 * @author  <b>李自立</b> 
 */
public class SysParam {
	
	/** 记录日志 */
	private static Logger logger = Logger.getLogger(SysInit.class);
	
	/** 应用系统路径 */
	private static String sysPath = "";
	
	/** 全局参数缓存Map信息 */
	private static Map<String, String> configMap;
	
	/** 构造函数 */
	public SysParam() {}
	
	public static void setSysPath(String sysPath) {
		SysParam.sysPath = sysPath;
	}
	public static String getSysPath() {
		return sysPath;
	}

	/** 初始化配置文件和数据库的配置信息 */
	public static void init(){
		configMap = new HashMap<String, String>();
		try {
			
			//读取数据库中的全局参数的配置，存到缓存中
			initSysParam();
			
			//读取配置文件中的全局参数的配置，存到缓存中
			initProperties();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/** 重新加载配置文件和数据库的配置信息 */
	public static void reload(){
		configMap = null;
		init();
	}	
	
	/**  读取数据库中的全局参数的配置，存到缓存中 */
	private static void initSysParam(){
		try{
			String sql = "select * from T_SYS_PARAM";
            JdbcTemplate jdbc = (JdbcTemplate)ServiceLocator.getBean("jdbcTemplate");
            List list = jdbc.queryForList(sql);
            Map keyValue = new HashMap();
            for(Iterator iter = list.iterator();iter.hasNext();)
            {
                Map m = (Map)iter.next();
                keyValue.put(m.get("CODE"),m.get("PAR_VALUE"));
            }
            setConfigMap(keyValue);
		} catch (Exception e) {
			logger.error("读取数据库中的全局参数的配置出错!");
			e.printStackTrace();
		}
	}
	
	/** 读取配置文件中的全局参数的配置，存到缓存中 */
	private static void initProperties(){
		try {

			logger.debug("加载属性配置文件开始！");
			String propUrl = getSysPath() + "config.properties";
			logger.debug("系统全局参数的路径是:"+ propUrl);
			FileInputStream fis = new FileInputStream(propUrl);//属性文件流
			Properties prop = new Properties();//属性集合对象
			prop.load(fis);//将属性文件流装载到Properties对象中
			logger.debug("属性的长度："+ prop.size());
			
			Map<String, String> paramMap = new HashMap<String, String>();
			Iterator<Object> it = prop.keySet().iterator();
			while (it.hasNext()) {
				String key = (String)(it.next());
				String value = prop.getProperty(key);
				logger.debug(key + "="+ value);
				//global.ip=192.168.0.242的格式
				//map.put(key, prop.getProperty(key));
				paramMap.put(key, value);
			}
			logger.debug("加载属性配置文件结束！");
			setConfigMap(paramMap);
			
		} catch (FileNotFoundException e) {
			logger.error("文件未找到!");
			e.printStackTrace();
		} catch (IOException e) {
			logger.error("读取文件的时候异常!");
			e.printStackTrace();
		} catch (Exception e) {
			logger.error("其它异常!");
			e.printStackTrace();
		}
	}
	
	/** 获得属性文件的位置 */
	
	//初始化缓存中全局参数map的值
	public static void setConfigMap(Map<String, String> map){
		if(configMap == null){
			configMap = new HashMap<String, String>();
		}
		configMap.putAll(map);
	}

	//从缓存属性配置文件中取值
	public static String getConfigMapValue(String properKey){
		String value = null;
		if(configMap != null){
			if(configMap.containsKey(properKey)){
				value = configMap.get(properKey);
			}
			else{
				logger.error("别名为 "+ properKey +" 的配置信息不存在或未加载,请刷新全局配置参数！");
			}				
		}else{
			logger.error("全局配置参数未加载，请刷新全局配置参数或重新启动应用系统！");
		}
		return value;
	}
    public static boolean containsKey(String properKey)
    {
        if(configMap != null){
            return configMap.containsKey(properKey) ;
        } else
        {
            logger.error("全局配置参数未加载，请刷新全局配置参数或重新启动应用系统！");
            return false;
        }
    }
	
	/*
	public static void main(String[] args) {
		SysParam sysParam = new SysParam();
		SysParam.setSysPath("D:\\Eclipse3.3\\workspace\\dragonspace\\SOAkage_CFGL\\web\\");
		SysParam.init();
		logger.info("" + SysParam.getConfigMapValue("cfgl.init"));
	}*/
	
}
