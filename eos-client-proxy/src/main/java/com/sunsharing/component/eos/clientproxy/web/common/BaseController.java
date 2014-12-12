/**
 *====================================================
 * 文件名称: BaseController.java
 * 修订记录：
 * No    日期				作者(操作:具体内容)
 * 1.    2012-9-21			ulyn(创建:创建文件)
 *====================================================
 * 类描述：Controller的基类
 *
 */
package com.sunsharing.component.eos.clientproxy.web.common;

import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <pre>
 * <b><font color="blue">BaseController</font></b>
 * </pre>
 * <p/>
 * <pre>
 * <b>&nbsp;--Controller的基类，主要集成几个常用的方法--</b>
 * </pre>
 * <p/>
 * <pre></pre>
 * <p/>
 * JDK版本：JDK1.5.0
 *
 * @author <b>ulyn</b>
 */
public class BaseController {

    protected static Logger logger = Logger.getLogger(BaseController.class);

    /**
     * 获取当前用户的单位ID
     *
     * @param request
     * @return orgId
     */
    protected String getOrgId(HttpServletRequest request) {
        /*User user = (User) Session.getInstance().getAttribute(request,
				Constants.SESSIONUSERKEY);
		DevOrg org = user.getDevOrg();
        if(org!=null){
            return org.getOrgId();
        }*/
        return "";
    }

    /**
     * 输出响应流
     *
     * @param response
     * @param content：要输出的内容
     * @return
     */
    protected void printOut(HttpServletResponse response, String content) {
        ResponseHelper.printOut(response, content);
    }

    protected void printOutJSONP(HttpServletResponse response, String callback, String content) {
        printOut(response, callback + "(" + content + ");");
    }
}
