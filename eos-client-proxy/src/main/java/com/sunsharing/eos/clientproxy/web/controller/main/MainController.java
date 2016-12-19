package com.sunsharing.eos.clientproxy.web.controller.main;


import com.sunsharing.component.utils.web.ResponseUtils;
import com.sunsharing.eos.client.rpc.DynamicRpc;
import com.sunsharing.eos.clientproxy.web.common.BaseController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
public class MainController extends BaseController {
    /**
     *
     *
     * @param model
     * @param request
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/service.do")
    public void service(Model model, HttpServletRequest request, HttpServletResponse response) throws Exception {
        String str = DynamicRpc.invoke(request.getParameter("serviceReqBase64Str"));
        ResponseUtils.renderText(response,str);
    }
}
