package com.sunsharing.eos.serverexample.web.controller.main;


import com.sunsharing.eos.serverexample.web.common.BaseController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;

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
    @RequestMapping(value = "/login.do", method = RequestMethod.GET)
    public String relogin(Model model, HttpServletRequest request) throws Exception {
        return "login";
    }
}
