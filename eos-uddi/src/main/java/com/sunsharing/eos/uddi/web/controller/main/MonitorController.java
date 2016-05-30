package com.sunsharing.eos.uddi.web.controller.main;

import com.sunsharing.eos.uddi.service.MonitorService;
import com.sunsharing.eos.uddi.web.common.ResponseHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * Created by criss on 14-2-4.
 */
@Controller
public class MonitorController {

    @Autowired
    MonitorService service;

    @RequestMapping(value="/eosState.do",method= RequestMethod.POST)
    public void eos(Model model,HttpServletRequest request,HttpServletResponse response) throws Exception {
        List<Map> m = service.getEosStat();
        ResponseHelper.printOut(response, true, "", m);
    }

    @RequestMapping(value="/getServices.do",method= RequestMethod.POST)
    public void getServices(String appId,Model model,HttpServletRequest request,HttpServletResponse response) throws Exception {
        List<Map> m = service.getServices(appId);
        ResponseHelper.printOut(response, true, "", m);
    }

}
