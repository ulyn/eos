package com.sunsharing.eos.uddi.sys;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 *<pre><b><font color="blue">ServiceLocator</font></b></pre>
 *
 *<pre><b>&nbsp;--描述说明--</b></pre>
 * JDK版本：JDK1.5.0
 * @author  <b>李自立</b> 
 */
public class ServiceLocator {
	
	private static Logger logger = Logger.getLogger(ServiceLocator.class);
	
	private static ServiceLocator selfLocator = null;

	private static ApplicationContext ctx = null;

	private ServiceLocator(ApplicationContext ctx) {
		ServiceLocator.ctx = ctx;
	}

	/**
	 * 根据传入的容器Context进行初始化
	 * @param ctx ApplicationContext
	 */
	public static synchronized void init(ApplicationContext ctx) {
		if (selfLocator == null) {
			selfLocator = new ServiceLocator(ctx);
			logger.info("初始化ApplicationContext对象完毕...");
		}
	}

	
	/**
     * 根据类名查找Bean
     *
     * @param clazz bean的clazz
     * @return 返回查找的Bean
     */
    public static Object getBeanByClass(Class clazz) {
		if (ctx == null) {
			throw new RuntimeException("ServiceLocator没有被初始化，ApplicationContext为空");
		}

		Map map = ctx.getBeansOfType(clazz);
		Set keySet = map.keySet();
		if (keySet.size() == 1) {
			Object obj = null;
			for (Iterator iterator = keySet.iterator(); iterator.hasNext();) {
				obj = map.get(iterator.next());
			}
			return obj;
		} else if (keySet.size() == 0) {
			logger.warn("容器中不存在" + clazz + "对应的bean");
			return null;
		} else {
			logger.warn("容器中有多个" + clazz + "对应的bean");
			return null;
		}
    }
    
    /**
     * 根据名称获取Bean
     * @param name Bean的名称
     * @return 返回查找的Bean
     */
	public static Object getBean(String name) {
		if (selfLocator == null) {
			throw new RuntimeException("ServiceLocator没有被初始化，ApplicationContext为空");
		}
		return ctx.getBean(name);
	}
}
