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
import com.sunsharing.eos.common.utils.ClassFilter;
import com.sunsharing.eos.common.utils.ClassUtils;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;

import java.lang.annotation.Annotation;
import java.lang.reflect.Modifier;
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

    protected ApplicationContext ctx;
    protected String packagePath;

    //存储服务对象
    protected static Map<String, Object> services = new HashMap<String, Object>();

    public AbstractServiceContext(ApplicationContext ctx, String packagePath) {
        this.ctx = ctx;
        this.packagePath = packagePath;
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

            String id = ann.id();
            if (id.equals("")) {
                id = c.getSimpleName();
                id = Character.toLowerCase(id.charAt(0)) + id.substring(1);
            }
            config.setId(id);
            config.setAppId(ann.appId());
            config.setMock(ann.mock());
            config.setProxy(ann.proxy());
            config.setSerialization(ann.serialization());
            config.setTimeout(ann.timeout());
            config.setTransporter(ann.transporter());
            config.setVersion(ann.version());
            config.setImpl(ann.impl());

            createBean(c, config);
        }
    }

    protected abstract void createBean(Class interfaces, ServiceConfig config);
}

