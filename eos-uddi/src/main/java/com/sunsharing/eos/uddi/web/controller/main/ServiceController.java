package com.sunsharing.eos.uddi.web.controller.main;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sunsharing.component.utils.base.StringUtils;
import com.sunsharing.eos.common.zookeeper.PathConstant;
import com.sunsharing.eos.uddi.model.TApp;
import com.sunsharing.eos.uddi.model.TMethod;
import com.sunsharing.eos.uddi.model.TService;
import com.sunsharing.eos.uddi.model.TUser;
import com.sunsharing.eos.uddi.service.AppService;
import com.sunsharing.eos.uddi.service.InterfaceServcie;
import com.sunsharing.eos.uddi.service.NodeJSService;
import com.sunsharing.eos.uddi.service.Service;
import com.sunsharing.eos.uddi.sys.SysInit;
import com.sunsharing.eos.uddi.web.common.ResponseHelper;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by criss on 14-2-1.
 */
@Controller
public class ServiceController {

    Logger logger = Logger.getLogger(ServiceController.class);

    @Autowired
    Service service;
    @Autowired
    NodeJSService nodeJSService;

    @RequestMapping(value = "/servicelist.do", method = RequestMethod.POST)
    public void servicelist(String appId, String module, Model model, HttpServletRequest request, HttpServletResponse response)
            throws Exception {


        List<TService> services = service.query(appId, module);

        String str = JSONArray.toJSONString(services);
        JSONArray arr = JSONArray.parseArray(str);
        for (int i = 0; i < arr.size(); i++) {
            ((JSONObject) (arr.get(i))).put("user", services.get(i).getUser().getUserName());
        }

        ResponseHelper.printOut(response, true, "", arr);

    }

    @Autowired
    AppService appService;

    /**
     * 图片上传
     *
     * @param request
     * @param response
     * @throws Exception
     */
    @RequestMapping(value = {"/uploadjava.do"}, method = RequestMethod.POST)
    public void upladJava(String appId, String module, String servicename, HttpServletResponse response, HttpServletRequest request) {
        TApp app = null;
        try {
            app = appService.loadApp(appId);
            String appcode = app.getAppCode();
            MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
            // 获得文件
            MultipartFile imgFile = multipartRequest.getFile("java");
            if (imgFile != null && imgFile.getSize() > 0) {
                // 获得文件名
                String filename = imgFile.getOriginalFilename();
                //获取上传文件类型的扩展名,先得到.的位置，再截取从.的下一个位置到文件的最后，最后得到扩展名
                String ext = filename.substring(filename.lastIndexOf("."), filename.length());
                String extArr = ".java";
                if (extArr.indexOf(ext.toLowerCase()) < 0) {

                    //ResponseHelper.printOut(response, false, "只允许上传JAVA文件", "");
                    //return;
                    throw new RuntimeException("只允许上传JAVA文件");
                } else {
                    // 重新命名文件名字
                    File path = new File(SysInit.path + File.separator + "interface" + File.separator + appcode);
                    if (!path.exists()) {
                        path.mkdirs();
                    }
                    // 获得文件名
                    File source = new File(SysInit.path + File.separator + "tmp" + File.separator +
                            StringUtils.generateUUID());
                    if (!source.exists()) {
                        source.mkdirs();
                    }
                    String dest = (SysInit.path + File.separator + "interface" + File.separator + appcode);
                    imgFile.transferTo(source);
                    BufferedReader reader = null;
                    FileOutputStream w = null;
                    try {
                        //reader = new BufferedReader(new FileReader(source));
                        reader = new BufferedReader(new InputStreamReader(new FileInputStream(source), "UTF-8"));
                        List<String> str2 = new ArrayList<String>();
                        String line = "";
                        while ((line = reader.readLine()) != null) {

                            str2.add(line);
                        }
                        String[] lines = str2.toArray(new String[]{});
                        InterfaceServcie service = new InterfaceServcie();
                        service.addAppCode(appcode, lines);
                        lines = service.addParams(lines);
                        for (int i = 0; i < lines.length; i++) {
                            //System.out.println(lines[i]);
                        }
                        String version = service.getVersion(lines);
                        System.out.println(version);
                        String name = service.getInterfaceName(lines);
                        if (name.trim().length() > 20) {
                            throw new RuntimeException("接口名不允许超过20个字符");
                        }
                        String destName = SysInit.path + File.separator + "interface" + File.separator + appcode + File.separator + name + "_" + version + ".java";
                        if (new File(destName).exists()) {
                            new File(destName).delete();
                        }
                        File destFile = new File(destName);
                        w = new FileOutputStream(destFile);
                        for (int i = 0; i < lines.length; i++) {
                            w.write(lines[i].getBytes("UTF-8"));
                            w.write("\n".getBytes("UTF-8"));
                        }
                        TUser u = (TUser) request.getSession().getAttribute("user");
                        this.service.saveService(servicename, appId, module, str2.toArray(new String[]{}), u.getUserId());
                    } catch (Exception e) {
                        logger.error("", e);
                        //ResponseHelper.printOut(response, false, "上传JAVA文件出错", "");
                        throw new RuntimeException(e);
                    } finally {
                        if (reader != null) {
                            reader.close();
                        }
                        if (w != null) {
                            w.close();
                        }
                    }
                    String result = ResponseHelper.covert2Json(true, "", "");
                    result = "<script>parent.upload(" + result + ",'" + app.getAppId() + "')</script>";
                    ResponseHelper.printOut(response, result);
                }
            }
        } catch (Exception e) {
            logger.error("上传图片出错！", e);
            //ResponseHelper.printOut(response, false, "上传JAVA文件出错", "");
            String result = ResponseHelper.covert2Json(false, e.getMessage(), "");
            result = "<script>parent.upload(" + result + ")</script>";
            ResponseHelper.printOut(response, result);
        }
        String result = ResponseHelper.covert2Json(false, "上传报错", "");
        result = "<script>parent.upload(" + result + ")</script>";
        ResponseHelper.printOut(response, result);
    }

    @RequestMapping(value = {"/getmothod.do"}, method = RequestMethod.POST)
    public void getMethods(String appId, String serviceId, String version,
                           HttpServletResponse response, HttpServletRequest request) {
        List<Object> method = service.seachmethod(appId, serviceId, version);
        List<Map> result = new ArrayList();
        for (int i = 0; i < method.size(); i++) {
            Map m = new HashMap();
            Object[] obj = (Object[]) method.get(i);
            m.put("methodId", obj[0].toString());
            m.put("methodName", obj[1].toString());
            m.put("mockResult", JSONArray.parseArray(obj[2].toString()));
            result.add(m);
        }
        String result2 = ResponseHelper.covert2Json(true, "", result);
        ResponseHelper.printOut(response, result2);
    }

    @RequestMapping(value = {"/saveMethod.do"}, method = RequestMethod.POST)
    public void getMethods(
            HttpServletResponse response, HttpServletRequest request) throws Exception {
        Map<String, String[]> m = request.getParameterMap();
        String m2 = "";
        for (String key : m.keySet()) {
            String content = m.get(key)[0];

            String status = key.split("_")[0];
            String methodId = key.split("_")[1];
            m2 = methodId;
            service.saveMethod(methodId, status, content);
        }
        //TODO 判断是否审批通过，审批通过直接更新测试报文
        service.updateTestCode(m2);

        String result2 = ResponseHelper.covert2Json(true, "", "");
        ResponseHelper.printOut(response, result2);
    }

    @RequestMapping(value = {"/getJava.do"}, method = RequestMethod.POST)
    public void getJava(String versionId,
                        HttpServletResponse response, HttpServletRequest request) throws Exception {
        String result = service.getJava(versionId);
        result = result.replaceAll("<", "&lt;");
        result = result.replaceAll(">", "&gt;");
        result = result.replaceAll("\n", "<br />");
        ResponseHelper.printOut(response, true, "", result);
    }

    @RequestMapping(value = {"/download.do"}, method = RequestMethod.GET)
    public void downloadJava(String versionId,
                             HttpServletResponse response, HttpServletRequest request) throws Exception {
        //application/octet-stream
        String java = service.getJava(versionId);
        String name = service.getName(versionId);
        name = name.substring(0, 1).toUpperCase() + name.substring(1);
        name += ".java";
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment;"
                + " filename=" + new String(name.getBytes("UTF-8"), "ISO8859-1"));
        OutputStream out = response.getOutputStream();
        response.setHeader("Content-Length", java.getBytes("UTF-8").length + "");
        response.getOutputStream().write(java.getBytes("UTF-8"));
        out.close();
    }

    @RequestMapping(value = {"/commit.do"}, method = RequestMethod.GET)
    public void commit(String versionId,
                       HttpServletResponse response, HttpServletRequest request) throws Exception {
        TUser u = (TUser) request.getSession().getAttribute("user");
        if (u.getRole().equals("1")) {
            throw new RuntimeException("对不起你没有审批权限");
        }
        service.commit(versionId);


        ResponseHelper.printOut(response, true, "", "");
    }

    @RequestMapping(value = {"/delete.do"}, method = RequestMethod.POST)
    public void delete(String serviceId,
                       HttpServletResponse response, HttpServletRequest request) throws Exception {
        TUser u = (TUser) request.getSession().getAttribute("user");
        if (!u.getRole().equals("3")) {
            throw new RuntimeException("对不起你没有删除权限");
        }
        service.deleteService(serviceId);
        ResponseHelper.printOut(response, true, "", "");
    }

    @RequestMapping(value = {"/commitAllCommit.do"}, method = RequestMethod.GET)
    public void commitAllCommit(HttpServletResponse response, HttpServletRequest request) throws Exception {
        service.commitAllCommit();
        ResponseHelper.printOut(response, true, "", "");
    }

    @RequestMapping(value = {"/changeTest.do"}, method = RequestMethod.GET)
    public void changeTest(String versionId,
                           HttpServletResponse response, HttpServletRequest request) {
        service.changeTest(versionId);
        ResponseHelper.printOut(response, true, "", "");
    }


    @RequestMapping(value = "/getNpmVersion.do")
    public void getNpmVersion(HttpServletResponse response, String appId) {
        Map map = nodeJSService.getNpmVersion(appId);
        ResponseHelper.printOut(response, true, "", map);
    }

    @RequestMapping(value = "/publishCNPM.do")
    public void publishCNPM(HttpServletResponse response, String appId, String v) throws Exception {
        try {
            Map map = nodeJSService.publishCNPM(appId, v);
            ResponseHelper.printOut(response, true, "", map);

        } catch (Exception e) {
            logger.error("发布失败", e);
            ResponseHelper.printOut(response, false, e.getMessage(), "");
        }
    }
}
