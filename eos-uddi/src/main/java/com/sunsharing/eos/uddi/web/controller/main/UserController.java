package com.sunsharing.eos.uddi.web.controller.main;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sunsharing.eos.uddi.model.TApp;
import com.sunsharing.eos.uddi.model.TUser;
import com.sunsharing.eos.uddi.model.TUserApp;
import com.sunsharing.eos.uddi.service.AppService;
import com.sunsharing.eos.uddi.service.UserService;
import com.sunsharing.eos.uddi.web.common.ResponseHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Created by criss on 14-1-30.
 */
@Controller
public class UserController {

    @Autowired
    UserService service;

    @Autowired
    AppService appService;

    @RequestMapping(value="/login.do",method= RequestMethod.POST)
    public void login(Model model,HttpServletRequest request,HttpServletResponse response) throws Exception {


        String userName = request.getParameter("username");
        String pwd = request.getParameter("pwd");
        TUser user = service.login(userName,pwd);

        if(user!=null)
        {
            request.getSession().setAttribute("user",user);
            String str = JSONObject.toJSONString(user);
            JSONObject jonObject = JSONObject.parseObject(str);
            List<TUserApp> apps = user.getUserApps();
            Collections.sort(apps);

            if(apps.size()>0)
            {
                Integer appId = user.getUserApps().get(0).getApp().getAppId();
                jonObject.put("url","servicelist/"+appId+"/0");

            }else
            {
                if(user.getRole().equals("3"))
                {
                    jonObject.put("url","applist");
                }else
                {
                    jonObject.put("url","blank");
                }
            }
            ResponseHelper.printOut(response,true,"",jonObject);
        }else
        {
            ResponseHelper.printOut(response, false, "", "{}");
        }
    }

    @RequestMapping(value="/getUser.do",method= RequestMethod.POST)
    public void getUser(Model model,HttpServletRequest request,HttpServletResponse response)
            throws Exception {
        String[] appCss = new String[]{
                "i_32_dashboard",
                "i_32_charts",
                "i_32_tables",
                "i_32_inbox",
                "i_32_forms"
        };
        Random ran = new Random();
        TUser user = (TUser)request.getSession().getAttribute("user");
        Collections.sort(user.getUserApps());
        String str = JSONObject.toJSONString(user);
        JSONObject jonObject = JSONObject.parseObject(str);
        JSONArray array = jonObject.getJSONArray("userApps");
        for(int i=0;i<array.size();i++)
        {

            JSONObject obj = (JSONObject)array.get(i);
            JSONObject app = obj.getJSONObject("app");
            app.put("url","servicelist/"+app.getString("appId")+"/0");
        }
        if(user.getRole().equals("3"))
        {
            array.clear();
            //管理员
            List<TApp> apps = appService.listApp();
            Collections.sort(apps);
            String listStr = JSONArray.toJSONString(apps);
            JSONArray arr = JSONArray.parseArray(listStr);
            for(int i=0;i<arr.size();i++)
            {
                JSONObject obj = (JSONObject)arr.get(i);
                obj.put("url","servicelist/"+obj.getString("appId")+"/0");
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("app",obj);
                array.add(jsonObject);
            }

            JSONObject jsonObject = new JSONObject();
            JSONObject app = new JSONObject();
            app.put("appName","应用管理");
            app.put("appCode","APP");
            app.put("url","applist");
            jsonObject.put("app",app);

            JSONObject jsonObject2 = new JSONObject();
            JSONObject app2 = new JSONObject();
            app2.put("appName","用户管理");
            app2.put("appCode","USER");
            app2.put("url","userlist");
            jsonObject2.put("app",app2);

            array.add(jsonObject);
            array.add(jsonObject2);
        }

        JSONObject jsonObject = new JSONObject();
        JSONObject app = new JSONObject();
        app.put("appName","系统监控");
        app.put("appCode","MONITOR");
        app.put("url","monitor");
        jsonObject.put("app",app);
        array.add(jsonObject);
        for(int i=0;i<array.size();i++)
        {
            JSONObject obj = (JSONObject)array.get(i);
            if(i==0)
            {
                obj.put("selectcss","active_tab");
            }else
            {
                obj.put("selectcss","");
            }
            obj.put("appcss",appCss[ran.nextInt(appCss.length)]);
        }


        ResponseHelper.printOut(response,true,"",jonObject);
    }

    @RequestMapping(value="/userlist.do",method= RequestMethod.POST)
    public void userList(Model model,HttpServletRequest request,HttpServletResponse response)
    {
        List<TUser> list = service.getUserlist();

        for(TUser user:list)
        {
            String role = user.getRole();
            if(role!=null)
            {
                if(role.equals("1"))
                {
                    user.setRole("研发");
                }
                if(role.equals("2"))
                {
                    user.setRole("小组长");
                }
                if(role.equals("3"))
                {
                    user.setRole("管理员");
                }
                if(role.equals("4"))
                {
                    user.setRole("数据组");
                }
            }
        }

        ResponseHelper.printOut(response,true,"",list);
    }

    @RequestMapping(value="/userEdit.do",method= RequestMethod.POST)
    public void userEdit(String id,Model model,HttpServletRequest request,HttpServletResponse response)
    {
        TUser user = service.loadUser(id);

        List<TApp> list = appService.listApp();

        String userStr = JSONObject.toJSONString(user);
        String listStr = JSONArray.toJSONString(list);
        JSONObject obj = JSONObject.parseObject(userStr);
        JSONArray arr = JSONArray.parseArray(listStr);
        obj.put("apps",arr);
        ResponseHelper.printOut(response,true,"",obj);
    }

    @RequestMapping(value="/updateUser.do",method= RequestMethod.POST)
    public void updateUser(String id,String role,String apps,String isTest,Model model,HttpServletRequest request,HttpServletResponse response)
    {
        service.updateUser(id,role,apps,isTest);
        ResponseHelper.printOut(response,true,"","");
    }

    @RequestMapping(value="/saveUser.do",method= RequestMethod.POST)
    public void saveUser(String username,String pwd,String email,HttpServletRequest request,HttpServletResponse response)
    {
        service.addUser(username,pwd,email);
        ResponseHelper.printOut(response,true,"","");
    }

    @RequestMapping(value="/loginUser.do",method= RequestMethod.POST)
    public void loginUser(HttpServletRequest request,HttpServletResponse response)
    {
        TUser user = (TUser)request.getSession().getAttribute("user");
        ResponseHelper.printOut(response,JSONObject.toJSONString(user));
    }



}
