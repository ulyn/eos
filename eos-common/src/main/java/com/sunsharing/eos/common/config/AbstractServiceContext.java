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
import com.sunsharing.eos.common.utils.ClassFilter;
import com.sunsharing.eos.common.utils.ClassHelper;
import com.sunsharing.eos.common.utils.ClassUtils;
import com.sunsharing.eos.common.utils.StringUtils;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.lang.annotation.Annotation;
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
    Logger logger = Logger.getLogger(AbstractServiceContext.class);

    //    protected ApplicationContext ctx;
    protected String packagePath;

    //存储服务对象,key为服务id
    protected static Map<String, Object> servicesMapByKeyClassName = new HashMap<String, Object>();//key值为接口类名
    protected static Map<String, Object> servicesMapByKeyAppServiceId = new HashMap<String, Object>();//key值为getServiceConfigKey(appId,serviceId)
    //为适应废弃的getBean
    protected static Map<String, Object> servicesMapByKeyServiceId = new HashMap<String, Object>(); //key值为ServiceId

    protected static Map<String, ServiceConfig> serviceConfigMap = new HashMap<String, ServiceConfig>();//key值为getServiceConfigKey(appId,serviceId)
    protected static Map<String, List<ServiceConfig>> serviceConfigMapByKeyServiceId = new HashMap<String, List<ServiceConfig>>();//key值为serviceId


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

    public AbstractServiceContext(String packagePath) {
//        this.ctx = ctx;
        this.packagePath = packagePath;

    }

    /**
     * 初始化
     */
    public void init() {
        String xmlConfigFileName = "EosServiceConfig.xml";
        //key为接口name
        Map xmlMap = loadXmlServiceConfig(xmlConfigFileName);
        Map beansMap = (Map) xmlMap.get("beansMap");
        Map<String, ServiceConfig> xmlServiceConfigMap = (Map<String, ServiceConfig>) beansMap.get("configMap");
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

        ClassFilter filter = new ClassFilter() {
            @Override
            public boolean accept(Class clazz) {
                if (Modifier.isInterface(clazz.getModifiers())) {
                    Annotation ann = clazz.getAnnotation(EosService.class);
                    if (ann != null) {
                        return true;
                    }
                }
                return false;
            }
        };
        List<Class> classes = ClassUtils.scanPackage(packagePath, filter);

        for (final Class c : classes) {
            ServiceConfig config = new ServiceConfig();
            EosService ann = (EosService) c.getAnnotation(EosService.class);

            String id = getBeanId(c, ann.id());
            config.setId(id);
            config.setAppId(ann.appId());
            config.setProxy(ann.proxy());
            config.setSerialization(ann.serialization());
            config.setTimeout(ann.timeout());
            config.setTransporter(ann.transporter());
            config.setVersion(ann.version());
            config.setImpl(ann.impl());
            config.setServiceMethodList(getInterfaceMethodList(c));

            if (xmlServiceConfigMap.containsKey(id)) {
                //有xml配置的使用xml配置
                ServiceConfig xmlConfig = xmlServiceConfigMap.get(id);
                if (!StringUtils.isBlank(xmlConfig.getMock())) {
                    config.setMock(xmlConfig.getMock());
                } else if (!StringUtils.isBlank((String) beansMap.get("rootMock"))) {
                    config.setMock((String) beansMap.get("rootMock"));
                }
                Map xmlConfigMethodMockMap = xmlConfig.getMethodMockMap();
                if (xmlConfigMethodMockMap != null) {
                    Map configMethodMockMap = config.getMethodMockMap();
                    if (configMethodMockMap == null) {
                        configMethodMockMap = new HashMap();
                        config.setMethodMockMap(configMethodMockMap);
                    }
                    for (Object mockKey : xmlConfigMethodMockMap.keySet()) {
                        configMethodMockMap.put(mockKey, xmlConfigMethodMockMap.get(mockKey));
                    }
                }

                if (!"".equals(xmlConfig.getImpl())) {
                    config.setImpl(xmlConfig.getImpl());
                }
            }
            Object bean = createBean(c, config);
            if (bean != null) {
                logger.info("加载服务：" + config.getAppId() + "-" + config.getId() + "-" + config.getVersion());
                String key = getServiceConfigKey(config.getAppId(), config.getId());
                //注意 对于server端appid为空
                servicesMapByKeyClassName.put(c.getName(), bean);
                servicesMapByKeyAppServiceId.put(key, bean);
                servicesMapByKeyServiceId.put(config.getId(), bean);

                serviceConfigMap.put(key, config);
                List<ServiceConfig> serviceConfigs = serviceConfigMapByKeyServiceId.get(config.getId());
                if (serviceConfigs == null) {
                    serviceConfigs = new ArrayList<ServiceConfig>();
                    serviceConfigMapByKeyServiceId.put(config.getId(), serviceConfigs);
                }
                serviceConfigs.add(config);
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
                //获取beans配置
                Map beansMap = new HashMap();
                if (beansEl != null) {
                    beansMap.put("rootMock", beansEl.attributeValue("mock"));
                    List<Element> elements = beansEl.elements("bean");
                    for (Element el : elements) {
                        String id = el.attributeValue("id");
                        ServiceConfig config = new ServiceConfig();
                        config.setId(id);
                        config.setMock(el.attributeValue("mock"));
                        config.setImpl(el.attributeValue("impl"));
                        List<Element> methodEls = el.elements();
                        if (methodEls.size() > 0) {
                            //set methodMock
                            Map<String, String> methodMockMap = new HashMap<String, String>();
                            for (Element methodEl : methodEls) {
                                methodMockMap.put(methodEl.getName(), methodEl.attributeValue("mock"));
                            }
                            config.setMethodMockMap(methodMockMap);
                        }

                        configMap.put(id, config);
                    }

                }
                beansMap.put("configMap", configMap);
                rtnMap.put("beansMap", beansMap);
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
     * 根据服务id取得服务bean，接口服务id不能有重复，否则可能得不到想要的结果
     *
     * @param id
     * @param <T>
     * @return
     */
    @Deprecated
    public static <T> T getBean(String id) {
        Object o = servicesMapByKeyServiceId.get(id);
        if (o == null) {
            return null;
        }
        return (T) o;
    }

    private String getBeanId(Class interfaces, String id) {
        if (id.equals("")) {
            id = interfaces.getSimpleName();
            id = Character.toLowerCase(id.charAt(0)) + id.substring(1);
        }
        return id;
    }

    /**
     * 取得类的公有方法
     *
     * @param interfaces
     * @return
     */
    private List<ServiceMethod> getInterfaceMethodList(Class interfaces) {
        List<ServiceMethod> list = new ArrayList<ServiceMethod>();
        Method[] methods = interfaces.getDeclaredMethods();
        for (Method method : methods) {
            //取参数名的注解
            ParameterNames ann = method.getAnnotation(ParameterNames.class);
            String[] parameterNames = null;
            if (ann != null) {
                parameterNames = ann.value();
                int paramSize = method.getParameterTypes() == null ? 0 : method.getParameterTypes().length;
                int annParamSize = parameterNames == null ? 0 : parameterNames.length;
                if (paramSize != annParamSize) {
                    throw new RuntimeException("服务" + interfaces + "的方法ParameterNames参数名注解不正确：参数个数不匹配");
                }
            }

            ServiceMethod serviceMethod = new ServiceMethod(method, parameterNames);
            list.add(serviceMethod);
        }
        return list;
    }

    public static Map<String, ServiceConfig> getServiceConfigMap() {
        return serviceConfigMap;
    }

    public static ServiceConfig getServiceConfig(String appId, String serviceId) {
        return serviceConfigMap.get(getServiceConfigKey(appId, serviceId));
    }

    public static List<ServiceConfig> getServiceConfig(String serviceId) {
        List<ServiceConfig> configs = serviceConfigMapByKeyServiceId.get(serviceId);
        return configs;
    }

    public static List<ServiceConfig> getServiceConfigList() {
        return new ArrayList<ServiceConfig>(serviceConfigMap.values());
    }

    /**
     * 创建bean对象
     *
     * @param interfaces
     * @param config
     * @return
     */
    protected abstract Object createBean(Class interfaces, ServiceConfig config);

    /**
     * 创建异常处理器
     *
     * @param resolver
     * @return
     */
    protected abstract void setExceptionResolver(ExceptionResolver resolver);
}

