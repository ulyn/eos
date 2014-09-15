/**
 * @(#)NodeJSService
 * 版权声明 厦门畅享信息技术有限公司, 版权所有 违者必究
 *
 *<br> Copyright:  Copyright (c) 2014
 *<br> Company:厦门畅享信息技术有限公司
 *<br> @author ulyn
 *<br> 14-9-10 下午6:48
 *<br> @version 1.0
 *————————————————————————————————
 *修改记录
 *    修改者：
 *    修改时间：
 *    修改原因：
 *————————————————————————————————
 */
package com.sunsharing.eos.uddi.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sunsharing.component.utils.file.FileUtil;
import com.sunsharing.eos.common.utils.StringUtils;
import com.sunsharing.eos.uddi.dao.SimpleHibernateDao;
import com.sunsharing.eos.uddi.model.*;
import com.sunsharing.eos.uddi.sys.SysInit;
import com.sunsharing.eos.uddi.sys.WindowsExec;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.apache.tools.ant.taskdefs.Sleep;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.*;

import java.io.*;
import java.util.*;

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
@org.springframework.stereotype.Service
public class NodeJSService {
    Logger logger = Logger.getLogger(NodeJSService.class);
    @Autowired
    JdbcTemplate jdbc;
    private SimpleHibernateDao<TService, Integer> serviceDao;//用户管理
    private SimpleHibernateDao<TApp, Integer> appDao;//用户管理
    private SimpleHibernateDao<TServiceVersion, Integer> versionDao;//用户管理
    private SimpleHibernateDao<TUser, Integer> userDao;//用户管理
    private SimpleHibernateDao<TUserApp, Integer> userAppDao;//用户管理
    private SimpleHibernateDao<TMethod, Integer> methodDao;//用户管理
    private SimpleHibernateDao<TModule, Integer> moduleDao;//用户管理


    @Autowired
    public void setSessionFactory(SessionFactory sessionFactory) {
        serviceDao = new SimpleHibernateDao<TService, Integer>(sessionFactory, TService.class);
        appDao = new SimpleHibernateDao<TApp, Integer>(sessionFactory, TApp.class);
        versionDao = new SimpleHibernateDao<TServiceVersion, Integer>(sessionFactory, TServiceVersion.class);
        userDao = new SimpleHibernateDao<TUser, Integer>(sessionFactory, TUser.class);
        userAppDao = new SimpleHibernateDao<TUserApp, Integer>(sessionFactory, TUserApp.class);
        methodDao = new SimpleHibernateDao<TMethod, Integer>(sessionFactory, TMethod.class);
        moduleDao = new SimpleHibernateDao<TModule, Integer>(sessionFactory, TModule.class);
    }

    public Map getNpmVersion(String appId) {
        Map map = new HashMap();
        TApp app = appDao.get(new Integer(appId));
        String queryStr = "ss-eos-" + app.getAppCode();
        map.put("name", queryStr);
        String result = new WindowsExec().run("cnpm.cmd info " + queryStr + " version");
        if (result.indexOf(queryStr) != -1) {
            map.put("v", "");
        } else {
            map.put("v", result);
        }
        String[] arr = result.split(".");
        for (String s : arr) {
            if (!StringUtils.isInteger(s)) {
                throw new RuntimeException(result);
            }
        }
        return map;
    }

    public Map publishCNPM(String appId, String oldVersion) throws Exception {
        TApp app = appDao.get(new Integer(appId));
        List<TService> servicesTemp = serviceDao.find("from TService where appId=?", new Integer(appId));
        List<TService> services = new ArrayList<TService>();
        for (TService service : servicesTemp) {
            TServiceVersion serviceVersion = service.getVersions().get(0);
            if (serviceVersion.getStatus().equals("1")) {
                services.add(service);
            }
        }
        if (services.size() > 0) {
            List changeServices = new ArrayList();
            String v = "0.0.1";
            if (!StringUtils.isBlank(oldVersion)) {
                String versionStr = oldVersion.split("\\.")[2];
                int verUpdate = Integer.parseInt(versionStr) + 1;
                v = "0.0." + verUpdate;
            }
            //判断当前版本与上一版本的服务变化情况。
            String oldVersionPath = SysInit.path + File.separator + "node_service_modules"
                    + File.separator + app.getAppCode() + File.separator + oldVersion;
            changeServices = getChangeServices(oldVersionPath, services);
            if (changeServices.size() == 0) {
                throw new RuntimeException("服务版本没有变化，不需要重复发布服务！");
            }
            String path = createDir(app, v, services, changeServices);
            String result = new WindowsExec().run("cnpm publish " + path);
            if (result.indexOf("ERR") != -1) {
                throw new RuntimeException("发布服务失败：" + result);
            }
            Map map = new HashMap();
            map.put("name", "ss-eos-" + app.getAppCode());
            map.put("v", result);
            logger.info("cnpm发布服务成功：" + map);
            return map;
        } else {
            throw new RuntimeException("没有上传服务接口，不能发布！");
        }
    }

    private String createDir(TApp app, String v, List<TService> services, List changeServices) throws Exception {
        String path = SysInit.path + File.separator + "node_service_modules"
                + File.separator + app.getAppCode() + File.separator + v;
        File dir = new File(path);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        createPackageJsonFile(app, v, path);
        createReadme(app, v, path, services, changeServices);
        createJs(services, path);
        createVersionCheckFile(path, services);
        return path;
    }

    private void createPackageJsonFile(TApp app, String v, String path) throws Exception {
        String content = "{\n" +
                "  \"author\": \"sunsharing\",\n" +
                "  \"name\": \"ss-eos-" + app.getAppCode() + "\",\n" +
                "  \"description\": \"" + app.getAppName() + " nodejs client for eos\",\n" +
                "  \"keywords\": [\n" +
                "    \"ss-eos\",\n" +
                "    \"" + app.getAppCode() + "\",\n" +
                "    \"" + app.getAppName() + "\"\n" +
                "  ],\n" +
                "  \"version\": \"" + v + "\",\n" +
                "  \"repository\": {\n" +
                "    \"type\": \"git\",\n" +
                "    \"url\": \"https://github.com/ulyn/node-eos.git\"\n" +
                "  },\n" +
                "  \"main\": \"index\",\n" +
                "  \"dependencies\": {\n" +
                "  },\n" +
                "  \"bugs\": {\n" +
                "    \"url\": \"https://github.com/ulyn/node-eos/issues\"\n" +
                "  },\n" +
                "  \"homepage\": \"https://github.com/ulyn/node-eos\",\n" +
                "  \"directories\": {\n" +
                "    \"test\": \"test\"\n" +
                "  },\n" +
                "  \"devDependencies\": {},\n" +
                "  \"scripts\": {\n" +
                "    \"test\": \"echo \\\"Error: no test specified\\\" && exit 1\"\n" +
                "  },\n" +
                "  \"license\": \"ISC\"\n" +
                "}\n";
        FileUtil.createFile(path + File.separator + "package.json", content, "utf-8");
    }

    /**
     * 保存服务的版本号
     *
     * @param path
     * @param services
     * @throws Exception
     */
    private void createVersionCheckFile(String path, List<TService> services) throws Exception {
        JSONObject jo = new JSONObject();
        for (TService service : services) {
            jo.put(service.getServiceCode(), service.getVersions().get(0).getVersion());
        }
        FileUtil.createFile(path + File.separator + "services.version", JSON.toJSONString(jo, true), "utf-8");
    }

    private List getChangeServices(String path, List<TService> services) throws IOException {
        JSONObject jo = new JSONObject();
        try {
            File file = new File(path + File.separator + "services.version");
            if (file.exists()) {
                InputStream inputStream = new FileInputStream(file);
                StringWriter writer = new StringWriter();
                IOUtils.copy(inputStream, writer, "utf-8");
                String txt = writer.toString();
                if (!StringUtils.isBlank(txt)) {
                    jo = JSON.parseObject(txt);
                }
            }
        } catch (Exception e) {
            logger.error("取得版本文件异常", e);
        }
        List list = new ArrayList();
        for (TService service : services) {
            String oldV = jo.getString(service.getServiceCode());
            String newV = service.getVersions().get(0).getVersion();
            Map map = new HashMap();
            if (StringUtils.isBlank(oldV)) {
                map.put("name", service.getServiceCode());
                map.put("oldV", "未发布");
                map.put("newV", newV);
                list.add(map);
            } else if (!oldV.equals(newV)) {
                map.put("name", service.getServiceCode());
                map.put("oldV", oldV);
                map.put("newV", newV);
                list.add(map);
            }
        }
        return list;
    }

    private void createReadme(TApp app, String v, String path, List<TService> services, List changeServices) throws Exception {
        StringBuilder sb = new StringBuilder(app.getAppName() + "-" + app.getAppCode() + "\n" +
                "========\n" +
                "\n" +
                app.getAppName() + "-" + app.getAppCode() + " 的eos服务,详见http://192.168.0.169:8100/index.html#/servicelist/" + app.getAppId() + "/0 \n" +
                "\n");
        sb.append("## 服务更新情况 \n");
        for (int i = 0; i < changeServices.size(); i++) {
            sb.append(i + ". ");
            Map map = (Map) changeServices.get(i);
            sb.append(map.get("name"));
            sb.append("  ");
            sb.append(map.get("oldV") + "->" + map.get("newV"));
            sb.append("\n");
        }
        sb.append("\n## 服务接口\n");
        for (TService service : services) {
            sb.append("+ " + service.getServiceCode() + "-" + service.getVersions().get(0).getVersion() + "-" + service.getServiceName() + "\n");
        }
        FileUtil.createFile(path + File.separator + "README.md", sb.toString(), "utf-8");
    }

    private void createJs(List<TService> services, String path) throws Exception {
        createIndexJs(services, path);
        for (TService service : services) {
            createServiceJs(service, path);
        }
    }

    private void createIndexJs(List<TService> services, String path) throws Exception {
        StringBuilder sb = new StringBuilder();
        sb.append("module.exports = function(eos){\n" +
                "    eos = eos || require(\"node-eos\");\n" +
                "    return {\n" +
                "        eos:eos,\n");
        for (int i = 0, l = services.size(); i < l; ) {
            TService service = services.get(i);
            sb.append("        " + service.getServiceCode() + ":require(\"./" + service.getServiceCode() + "\")(eos)");
            i++;
            if (i < l) {
                sb.append(",");
            }
            sb.append("\n");
        }
        sb.append("    }\n" +
                "}");
        FileUtil.createFile(path + File.separator + "index.js", sb.toString(), "utf-8");
    }

    private void createServiceJs(TService service, String path) throws Exception {
        String file = path + File.separator + service.getServiceCode() + ".js";
        TServiceVersion serviceVersion = service.getVersions().get(0);
        StringBuilder sb = new StringBuilder("/** \n");
        sb.append("* " + service.getModule() + " - " + service.getServiceName() + " \n");
        sb.append("* " + service.getServiceCode() + " - " + serviceVersion.getVersion() + " \n*/\n");

        sb.append("module.exports = function(eos){\n" +
                "    function " + service.getServiceCode() + "(){\n" +
                "        eos.Service.call(this);\n" +
                "        this.appId = \"" + serviceVersion.getAppCode() + "\";\n" +
                "        this.serviceId = \"" + service.getServiceCode() + "\";\n" +
                "        this.serviceVersion = \"" + serviceVersion.getVersion() + "\";\n" +
                "    }\n" +
                "    eos.util.inherits(" + service.getServiceCode() + ",eos.Service);\n\n");
        for (TMethod method : serviceVersion.getMethods()) {
            if (!StringUtils.isBlank(method.getMockResult())) {
                //注释部分
                JSONArray methodMockResult = JSON.parseArray(method.getMockResult());
                if (methodMockResult.size() > 0) {
                    sb.append("   /**\n");
                    sb.append("    * @return\n");
                    for (int i = 0, l = methodMockResult.size(); i < l; i++) {
                        JSONObject jo = methodMockResult.getJSONObject(i);
                        sb.append("    *  ${");
                        sb.append(jo.getString("status"));
                        sb.append("}  " + jo.getString("desc") + "\n");
                        sb.append("    *     ");
                        sb.append(jo.getString("content"));
                        sb.append("\n");
                    }
                    sb.append("    */\n");
                }
            }
            String methodName = method.getMethodName();
            if (methodName.startsWith("void ")) {
                methodName = methodName.replace("void ", "");
            }
            String paramsStr = method.getParams();
            sb.append("    " + service.getServiceCode() + ".prototype." + methodName + " = " +
                    "function(" + (StringUtils.isBlank(paramsStr) ? "" : paramsStr + ",") + "successFunc,errorFunc){\n" +
                    "        var req = this._createReqPro(\"" + methodName + "\"" + (StringUtils.isBlank(paramsStr) ? "" : "," + paramsStr) + ");\n" +
                    "        eos.call(req,successFunc,errorFunc);\n" +
                    "    }\n\n");
        }
        sb.append("    return " + service.getServiceCode() + ";\n" +
                "}");
        FileUtil.createFile(file, sb.toString(), "utf-8");
    }
}

