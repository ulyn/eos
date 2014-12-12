/**
 * @(#)$CurrentFile
 * 版权声明 厦门畅享信息技术有限公司, 版权所有 违者必究
 *
 *<br> Copyright:  Copyright (c) 2013
 *<br> Company:厦门畅享信息技术有限公司
 *<br> @author criss
 *<br> 13-9-11 下午2:30
 *<br> @version 1.0
 *————————————————————————————————
 *修改记录
 *    修改者：
 *    修改时间：
 *    修改原因：
 *————————————————————————————————
 */
package com.sunsharing.component.eos.clientproxy.web.common;

import com.alibaba.fastjson.JSON;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

/**
 * <pre></pre>
 * <br>----------------------------------------------------------------------
 * <br> <b>功能描述:</b>
 * <br>
 * <br> 注意事项:
 * <br>
 * <br>
 * <br>----------------------------------------------------------------------
 * <br>
 */
public class ResponseHelper {
    static Logger logger = Logger.getLogger(ResponseHelper.class);

    /**
     * 组装返回的json对象
     *
     * @param status
     * @param msg
     * @param o
     * @return
     */
    public static String covert2Json(boolean status, String msg, Object o) {
        JsonResponse jr = new JsonResponse();
        jr.setStatus(status);
        jr.setMsg(msg);
        jr.setData(o);
        return covert2Json(jr);
    }

    /**
     * 组装返回的json对象
     *
     * @param jsonResponse
     * @return
     */
    public static String covert2Json(JsonResponse jsonResponse) {
        return JSON.toJSONString(jsonResponse);
    }

    /**
     * 输出特定json对象的响应流
     *
     * @param response
     */
    public static void printOut(HttpServletResponse response, JsonResponse jsonResponse) {
        printOut(response, covert2Json(jsonResponse));
    }

    /**
     * 输出特定json对象的响应流
     *
     * @param response
     * @param status
     * @param msg
     * @param o
     */
    public static void printOut(HttpServletResponse response, boolean status, String msg, Object o) {
        printOut(response, covert2Json(status, msg, o));
    }

    /**
     * 输出响应流
     *
     * @param response
     * @param content：要输出的内容
     * @return
     */
    public static void printOut(HttpServletResponse response, String content) {
        response.setCharacterEncoding("UTF-8");
        PrintWriter writer = null;
        try {
            writer = response.getWriter();
            // response.setContentLength(responseContent.length());
            writer.write(content);
            writer.flush();
            writer.close();
        } catch (Exception e) {
            logger.error("消息输出出错", e);
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }

    protected void printOutJSONP(HttpServletResponse response, String callback, String content) {
        printOut(response, callback + "(" + content + ");");
    }
}

