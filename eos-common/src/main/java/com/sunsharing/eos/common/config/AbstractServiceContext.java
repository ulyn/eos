/**
 * @(#)AbstractServiceContext
 * 版权声明 厦门畅享信息技术有限公司, 版权所有 违者必究
 *
 *<br> Copyright:  Copyright (c) 2014
 *<br> Company:厦门畅享信息技术有限公司
 *<br> @author ulyn
 *<br> 14-1-31 下午5:37
 *<br> @version 1.0
 *————————————————————————————————
 *修改记录
 *    修改者：
 *    修改时间：
 *    修改原因：
 *————————————————————————————————
 */
package com.sunsharing.eos.common.config;

import com.sunsharing.eos.common.annotation.EosService;
import com.sunsharing.eos.common.annotation.ParameterNames;
import com.sunsharing.eos.common.filter.FilterManager;
import com.sunsharing.eos.common.exception.ExceptionResolver;
import com.sunsharing.eos.common.utils.*;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
public abstract class AbstractServiceContext {

    public AbstractServiceContext()
    {

    }

    public AbstractServiceContext(String packagePath) {
//        this.ctx = ctx;
        this.packagePath = packagePath;

    }

    Logger logger = Logger.getLogger(AbstractServiceContext.class);

    //    protected ApplicationContext ctx;
    protected String packagePath;

    //存储服务对象,key为服务id
    protected  Map<String, Object> servicesMapByKeyClassName = new HashMap<String, Object>();//key值为接口类名
    protected  Map<String, Object> servicesMapByKeyAppServiceId = new HashMap<String, Object>();//key值为getServiceConfigKey(appId,serviceId)

    protected  Map<String, ServiceConfig> serviceConfigMap = new HashMap<String, ServiceConfig>();//key值为getServiceConfigKey(appId,serviceId)


    //全局异常处理器
    private ExceptionResolver exceptionResolver = null;

    public ExceptionResolver getExceptionResolver() {
        return exceptionResolver;
    }


    /**
     * 取得服务配置map的key
     *
     * @param appId
     * @param serviceId
     * @return
     */
    public static String getServiceConfigKey(String appId, String serviceId) {
        if (StringUtils.isBlank(appId)) {
            return serviceId;
        } else {
            return appId + "-" + serviceId;
        }
    }



    /**
     * 初始化
     */
    public void initConfig() {
        String xmlConfigFileName = "EosServiceConfig.xml";
        //key为接口name
        Map xmlMap = loadXmlServiceConfig(xmlConfigFileName);
        Map beansMap = (Map) xmlMap.get("beansMap");
        List<Map<String, Object>> filters = (List<Map<String, Object>>) xmlMap.get("filters");
        //注册过滤器
        for (Map<String, Object> filter : filters) {
            List<String> pathPatterns = (List<String>) filter.get("pathPatterns");
            List<String> excludePaths = (List<String>) filter.get("excludePaths");
            String filterClassName = (String) filter.get("class");
            FilterManager.registerFilter(filterClassName, pathPatterns, excludePaths);
        }
        //注册全局异常处理器
        String exceptionResolverClassName = (String) xmlMap.get("exceptionResolver");
        if (StringUtils.isNotEmpty(exceptionResolverClassName)) {
            try {
                ExceptionResolver exceptionResolver = (ExceptionResolver) Class.forName(exceptionResolverClassName).newInstance();
                setExceptionResolver(exceptionResolver);
            } catch (Exception e) {
                logger.error(String.format("注册ExceptionResolver异常!：%s", exceptionResolverClassName), e);
            }
        }
    }

    /**
     * 从xml文件加载服务配置,目前只读接口的mock参数
     *
     * @param fileName
     * @return
     */
    private Map loadXmlServiceConfig(String fileName) {
        Map rtnMap = new HashMap();

        Map<String, ServiceConfig> configMap = new HashMap<String, ServiceConfig>();
        InputStream is = ClassHelper.getClassLoader(ServiceConfig.class).getResourceAsStream(fileName);
        if (is == null) {
            logger.info("没有找到eos服务的xml配置...");
            Map beansMap = new HashMap();
            beansMap.put("configMap", configMap);
            rtnMap.put("beansMap", beansMap);
            rtnMap.put("filters", new ArrayList<Map<String, String>>());
        } else {
            try {
                ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
                int ch;
                while ((ch = is.read()) != -1) {
                    byteStream.write(ch);
                }
                byte[] bytes = byteStream.toByteArray();
                byteStream.close();
                String result = new String(bytes, "UTF-8");
                Document doc = DocumentHelper.parseText(result);
                Element root = doc.getRootElement();

                Element beansEl = root.element("beans");
                Element filtersEl = root.element("filters");
                Element exceptionResolverEl = root.element("exceptionResolver");
                if (exceptionResolverEl != null) {
                    rtnMap.put("exceptionResolver", exceptionResolverEl.attributeValue("class"));
                }

                //获取filters配置
                List<Map<String, Object>> filters = new ArrayList<Map<String, Object>>();
                if (filtersEl != null) {
                    List<Element> elements = filtersEl.elements("filter");
                    for (Element el : elements) {
                        List<String> pathPatterns = getListByElement(el.element("path-pattern"));
                        List<String> excludePaths = getListByElement(el.element("exclude-path"));
                        String clazzName = el.elementTextTrim("class");
                        Map<String, Object> filterMap = new HashMap<String, Object>();
                        filterMap.put("pathPatterns", pathPatterns);
                        filterMap.put("excludePaths", excludePaths);
                        filterMap.put("class", clazzName);
                        filters.add(filterMap);
                    }
                }
                rtnMap.put("filters", filters);
            } catch (Exception e) {
                logger.error("读取eos服务的xml配置异常，请检查xml配置是否正确", e);
                throw new RuntimeException(e);
            }
        }
        return rtnMap;
    }

    /**
     * 取得el下的value节点的值
     *
     * @return
     */
    private List<String> getListByElement(Element el) {
        List<String> list = new ArrayList<String>();
        if (el != null) {
            List<Element> values = el.elements("value");
            for (Element element : values) {
                list.add(element.getTextTrim());
            }
        }
        return list;
    }

    /**
     * 创建异常处理器
     *
     * @param exceptionResolver
     * @return
     */
    public void setExceptionResolver(ExceptionResolver exceptionResolver) {
        this.exceptionResolver = exceptionResolver;
    }

}

