package com.sunsharing.eos.uddi.web.controller.main;

import com.sunsharing.component.utils.base.DateUtils;
import com.sunsharing.eos.common.utils.StringUtils;
import com.sunsharing.eos.uddi.db.AntZip;
import com.sunsharing.eos.uddi.model.TApp;
import com.sunsharing.eos.uddi.service.AppService;
import com.sunsharing.eos.uddi.service.MySqlExport;
import com.sunsharing.eos.uddi.sys.SysInit;
import com.sunsharing.eos.uddi.web.common.ResponseHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.Date;
import java.util.List;

/**
 * Created by criss on 14-1-31.
 */
@Controller
public class AppController {
    @Autowired
    AppService appService;

    @Autowired
    MySqlExport mysql;

    @RequestMapping(value="/applist.do",method= RequestMethod.POST)
    public void applist(Model model,HttpServletRequest request,HttpServletResponse response)
            throws Exception {

        List<TApp> apps = appService.listApp();
        ResponseHelper.printOut(response, true, "", apps);

    }
    @RequestMapping(value="/saveApp.do",method= RequestMethod.POST)
    public void saveApp(String app_en,String app_cn,String app_modules,String dbs,HttpServletResponse response)
            throws Exception {

        appService.saveApp(app_en,app_cn,app_modules,dbs);
        ResponseHelper.printOut(response, true, "", "");

    }

    @RequestMapping(value="/updateApp.do",method= RequestMethod.POST)
    public void updateApp(String id,String app_en,String app_cn,String app_modules,String dbs,HttpServletResponse response)
            throws Exception {

        appService.updateApp(id,app_en,app_cn,app_modules,dbs);
        ResponseHelper.printOut(response, true, "", "");

    }

    @RequestMapping(value="/loadApp.do",method= RequestMethod.POST)
    public void loadApp(String appId,HttpServletRequest request,HttpServletResponse response)
            throws Exception {

        TApp app = appService.loadApp(appId);
        ResponseHelper.printOut(response, true, "", app);

    }
    @RequestMapping(value="/export.do",method= RequestMethod.GET)
    public void export(HttpServletRequest request,HttpServletResponse response) throws Exception
    {
        String uuid = StringUtils.genUUID();
        String apps = request.getParameter("apps");
        mysql.export(apps,uuid);
        String zipPath = SysInit.path+ File.separator+"zip"+File.separator+uuid;
        AntZip zip = new AntZip();

        String d = DateUtils.getDBString(new Date());
        //ResponseHelper.printOut(response, true, "", "");
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment;"
                + " filename="+new String((d.substring(0,8)+".zip").getBytes("UTF-8"), "ISO8859-1"));
        zip.doZip(zipPath,response.getOutputStream());
    }



    @RequestMapping(value="/import.do",method= RequestMethod.POST)
    public void importPkg(HttpServletRequest request,HttpServletResponse response) throws Exception
    {
        try
        {
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest)request;
        // 获得文件
        MultipartFile imgFile  =  multipartRequest.getFile("pkg");
        String uuid = com.sunsharing.component.utils.base.StringUtils.generateUUID();
        File f = new File(uuid+".zip");
        imgFile.transferTo(f);
        appService.loadZip(uuid+".zip");
        String script = "<script>parent.alert(\"保存成功\");</script>";
            ResponseHelper.printOut(response,script);
        }catch (Exception e)
        {
            e.printStackTrace();
            String script = "<script>parent.alert(\"保存失败\");</script>";
            ResponseHelper.printOut(response,script);
        }

    }

//    @RequestMapping(value = "/downloadjar.do")
//    public void downloadJar(String appId,HttpServletRequest request,HttpServletResponse response) throws Exception
//    {
//        String tmpId = StringUtils.genUUID();
//        appService.changeJava(appId,tmpId);
//
//        TApp app = appService.loadApp(appId);
//        String filePath = SysInit.path+ File.separator
//                +"jartmp"+File.separator+tmpId+File.separator+tmpId+".jar";
//
//        String d =  DateUtils.getDBString(new Date()).substring(0,8);
//
//        response.setContentType("application/octet-stream");
//        response.setHeader("Content-Disposition", "attachment;"
//                + " filename="+new String((app.getAppCode()+"_"+d+".jar").getBytes("UTF-8"), "ISO8859-1"));
//        FileInputStream in = new FileInputStream(filePath);
//        byte[] array = new byte[1024];
//        int len = 0;
//        while((len = in.read(array))!=-1)
//        {
//            response.getOutputStream().write(array,0,len);
//        }
//        in.close();
//
//
//        //ResponseHelper.printOut(response, true, "", "");
//    }


}
