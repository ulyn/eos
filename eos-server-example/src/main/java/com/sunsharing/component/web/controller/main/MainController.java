package com.sunsharing.component.eos.clientproxy.web.controller.main;



import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;

import com.sunsharing.component.eos.clientproxy.web.common.BaseController;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

@Controller
public class MainController extends BaseController {
	/**
	 * 用户登陆页面
	 * @param model
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/login.do",method=RequestMethod.GET)
    public String relogin(Model model,HttpServletRequest request) throws Exception {
        return "login";
    }
}
