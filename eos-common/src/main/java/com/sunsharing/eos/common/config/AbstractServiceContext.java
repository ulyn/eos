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
import com.sunsharing.eos.common.aop.Advice;
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
    protected static Map<String, Object> services = new HashMap<String, Object>();
    protected static Map<String, ServiceConfig> serviceConfigMap = new HashMap<String, ServiceConfig>();

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
        List advices = (List) xmlMap.get("advices");

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

            String adviceClassName = getAdviceClassName(advices, c, id);
            if (!StringUtils.isBlank(adviceClassName)) {
                try {
                    Advice advice = (Advice) Class.forName(adviceClassName).newInstance();
                    config.setAdvice(advice);
                    logger.info("取得" + id + "配置的advice=" + adviceClassName + "的newInstance实现");
                } catch (Exception e) {
                    logger.error("初始化配置的advice=" + adviceClassName + "的newInstance异常!", e);
                    System.exit(0);
                }
            }

            Object bean = createBean(c, config);
            if (bean != null) {
                logger.info("加载服务：" + config.getAppId() + "-" + config.getId() + "-" + config.getVersion());
                serviceConfigMap.put(config.getId(), config);
                services.put(config.getId(), bean);
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
            rtnMap.put("advices", new ArrayList());
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
                Element advicesEl = root.element("advices");

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
                            Map<String, Advice> methodServletAdviceMap = new HashMap<String, Advice>();
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
                //获取advices配置
                List advices = new ArrayList();
                if (advicesEl != null) {
                    List<Element> elements = advicesEl.elements("advice");
                    for (Element el : elements) {
                        String adviceClassName = el.elementTextTrim("class");
                        Element packageEl = el.element("packagesToScan");
                        Element excludeBeanEl = packageEl == null ? null : packageEl.element("excludeBean");
                        Element beanEl = el.element("beansToScan");
                        List<String> packages = getListByListElement(packageEl);
                        List<String> excludeBeans = getListByListElement(excludeBeanEl);
                        List<String> beans = getListByListElement(beanEl);

                        Map temp = new HashMap();
                        temp.put("advice", adviceClassName);
                        temp.put("packages", packages);
                        temp.put("excludeBeans", excludeBeans);
                        temp.put("beans", beans);
                        advices.add(temp);
                    }
                }
                rtnMap.put("advices", advices);
            } catch (Exception e) {
                logger.error("读取eos服务的xml配置异常，请检查xml配置是否正确", e);
                throw new RuntimeException(e);
            }
        }
        return rtnMap;
    }

    /**
     * 取得el下的list节点的值
     *
     * @return
     */
    private List<String> getListByListElement(Element el) {
        List<String> list = new ArrayList<String>();
        if (el != null) {
            Element listEl = el.element("list");
            if (listEl != null) {
                List<Element> values = listEl.elements("value");
                for (Element element : values) {
                    list.add(element.getTextTrim());
                }
            }
        }
        return list;
    }

    /**
     * 从配置的advice中取得class的advice
     *
     * @param advices
     * @param c
     * @return
     */
    private String getAdviceClassName(List advices, Class c, String beanId) {
        for (Object o : advices) {
            Map map = (Map) o;

            //包含在bean的配置
            List<String> beans = (List<String>) map.get("beans");
            for (String bean : beans) {
                if (beanId.equals(bean)) {
                    return (String) map.get("advice");
                }
            }
            //包含在包n的配置
            List<String> packages = (List<String>) map.get("packages");
            List<String> excludeBeans = (List<String>) map.get("excludeBeans");
            boolean isExclude = false;
            for (String bean : excludeBeans) {
                if (beanId.equals(bean)) {
                    isExclude = true;
                }
            }
            if (isExclude) {
                continue;
            }
            for (String pac : packages) {
                if (c.getName().startsWith(pac)) {
                    return (String) map.get("advice");
                }
            }
        }
        return null;
    }

    /**
     * 根据服务id取得服务bean，接口服务id不能有重复，否则可能得不到想要的结果
     *
     * @param id
     * @param <T>
     * @return
     */
    public static <T> T getBean(String id) {
        Object o = services.get(id);
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

    public static ServiceConfig getServiceConfig(String beanId) {
        return serviceConfigMap.get(beanId);
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
}

