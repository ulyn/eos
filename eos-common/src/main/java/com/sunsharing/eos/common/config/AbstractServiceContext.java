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
        Map xmlConfigMap = loadXmlServiceConfig(xmlConfigFileName);
        Map<String, ServiceConfig> xmlServiceConfigMap = (Map<String, ServiceConfig>) xmlConfigMap.get("configMap");

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
                } else if (!StringUtils.isBlank((String) xmlConfigMap.get("rootMock"))) {
                    config.setMock((String) xmlConfigMap.get("rootMock"));
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
                if (!StringUtils.isBlank(xmlConfig.getAdviceClassName())) {
                    config.setAdviceClassName(xmlConfig.getAdviceClassName());
                }

                //当xml有配置advice时，改写ServiceMethod的切面类。
                Map<String, Advice> servletAdviceMap = xmlConfig.getMethodServletAdviceMap();
                if (xmlConfig.getMethodServletAdviceMap() != null) {
                    List<ServiceMethod> methods = config.getServiceMethodList();
                    for (ServiceMethod method : methods) {
                        Advice advice = servletAdviceMap.get(method.getMethodName());
                        if (advice != null) {
                            method.setAdvice(advice);
                        } else {
                            String adviceClassName = xmlConfig.getAdviceClassName();
                            if (StringUtils.isBlank(adviceClassName)) {
                                adviceClassName = (String) xmlConfigMap.get("rootAdvice");
                            }

                            if (!StringUtils.isBlank(adviceClassName)) {
                                //如果xml服务全局有配，那么则取service的advice
                                try {
                                    Object adviceObj = Class.forName(adviceClassName).newInstance();
                                    method.setAdvice((Advice) adviceObj);
                                    logger.info("取得" + id + "-" + method.getMethodName() + "配置的advice=" + adviceClassName + "的newInstance实现");
                                } catch (Exception e) {
                                    logger.error("初始化配置的advice=" + xmlConfig.getAdviceClassName() + "的newInstance异常!", e);
                                    System.exit(0);
                                }
                            }
                        }
                    }
                }

                if (!"".equals(xmlConfig.getImpl())) {
                    config.setImpl(xmlConfig.getImpl());
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
        Map map = new HashMap();

        Map<String, ServiceConfig> configMap = new HashMap<String, ServiceConfig>();
        InputStream is = ClassHelper.getClassLoader(ServiceConfig.class).getResourceAsStream(fileName);
        if (is == null) {
            logger.info("没有找到eos服务的xml配置...");
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
                map.put("rootAdvice", root.attributeValue("advice"));
                map.put("rootMock", root.attributeValue("mock"));
                List<Element> elements = root.elements();
                for (Element el : elements) {
                    String id = el.attributeValue("id");
                    ServiceConfig config = new ServiceConfig();
                    config.setId(id);
                    config.setMock(el.attributeValue("mock"));
                    config.setImpl(el.attributeValue("impl"));
                    String defaultAdvice = el.attributeValue("advice");
                    config.setAdviceClassName(defaultAdvice);
                    Element methodsEl = el.element("methods");
                    if (methodsEl != null) {
                        //set methodMock
                        List<Element> methodEls = methodsEl.elements();
                        Map<String, String> methodMockMap = new HashMap<String, String>();
                        Map<String, Advice> methodServletAdviceMap = new HashMap<String, Advice>();
                        for (Element methodEl : methodEls) {
                            methodMockMap.put(methodEl.getName(), methodEl.attributeValue("mock"));

                            String servletAdvice = methodEl.attributeValue("advice");
                            if (servletAdvice != null) {
                                try {
                                    Advice advice = (Advice) Class.forName(servletAdvice).newInstance();
                                    methodServletAdviceMap.put(methodEl.getName(), advice);
                                    logger.info("取得" + id + "-" + methodEl.getName() + "配置的advice=" + servletAdvice + "的newInstance实现");
                                } catch (Exception e) {
                                    logger.error("初始化配置的servletAdvice=" + servletAdvice + "的newInstance异常!", e);
                                    System.exit(0);
                                }
                            }

                        }
                        config.setMethodMockMap(methodMockMap);
                        config.setMethodServletAdviceMap(methodServletAdviceMap);
                    }

                    configMap.put(id, config);
                }
            } catch (Exception e) {
                logger.error("读取eos服务的xml配置异常，请检查xml配置是否正确", e);
                throw new RuntimeException(e);
            }
        }

        map.put("configMap", configMap);

        return map;
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
        Annotation defaultAdviceAnn = interfaces.getAnnotation(com.sunsharing.eos.common.annotation.Advice.class);
        Advice defaultAdvice = null;
        if (defaultAdviceAnn != null) {
            try {
                defaultAdvice = (Advice) ((com.sunsharing.eos.common.annotation.Advice) defaultAdviceAnn).value().newInstance();
            } catch (Exception e) {
                logger.error("服务配置的Advice可能有误，value=" + ((com.sunsharing.eos.common.annotation.Advice) defaultAdviceAnn).value() + "的newInstance异常!", e);
                System.exit(0);
            }
        }
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
            //取切面类注解
            com.sunsharing.eos.common.annotation.Advice adviceAnn = method.getAnnotation(com.sunsharing.eos.common.annotation.Advice.class);
            Advice advice = null;
            if (adviceAnn != null) {
                try {
                    advice = (Advice) adviceAnn.value().newInstance();
                } catch (Exception e) {
                    logger.error("服务配置的Advice可能有误，value=" + adviceAnn.value() + "的newInstance异常!", e);
                    System.exit(0);
                }
            } else if (defaultAdvice != null) {
                advice = defaultAdvice;
            }
            ServiceMethod serviceMethod = new ServiceMethod(method, parameterNames, advice);
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

