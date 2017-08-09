/*
 * Copyright (c) 2016. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.sunsharing.eos.clientproxy.web.controller.main;


import com.sunsharing.eos.client.rpc.RpcInvoker;
import com.sunsharing.eos.clientproxy.web.common.ResponseHelper;
import net.sf.ehcache.constructs.web.ResponseUtil;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.sunsharing.eos.clientproxy.web.common.BaseController;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

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
    public void service(Model model, HttpServletRequest request, HttpServletResponse res) throws Exception {
        String str = RpcInvoker.invoke(request.getParameter("serviceReqBase64Str")
                , request.getParameter("serialization"));
        ResponseHelper.printOut(res, str);
    }
}
