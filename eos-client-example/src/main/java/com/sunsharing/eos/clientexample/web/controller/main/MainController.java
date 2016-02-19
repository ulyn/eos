package com.sunsharing.eos.clientexample.web.controller.main;


import com.sunsharing.eos.client.ServiceContext;
import com.sunsharing.eos.clientexample.test.Test2;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;

import com.sunsharing.eos.clientexample.web.common.BaseController;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

@Controller
public class MainController extends BaseController {
    /**
     * 用户登陆页面
     *
     * @param model
     * @param request
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/test.do", method = RequestMethod.GET)
    public String relogin(Model model, HttpServletRequest request) throws Exception {
//            ConfigContext.instancesBean(SysProp.class);
//            ServiceContext serviceContext = new ServiceContext("com.sunsharing");
//            ServiceLocation.getInstance().synConnect();
        Test2 test2 = ServiceContext.getBean(Test2.class);
        String name = test2.sayHello("abc");
        System.out.println(name);
        return "login";
    }


}
