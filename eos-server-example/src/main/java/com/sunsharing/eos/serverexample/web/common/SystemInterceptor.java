package com.sunsharing.eos.serverexample.web.common;

import com.sunsharing.eos.serverexample.web.exception.AuthException;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * <pre>
 * <b><font color="blue">SystemInterceptor</font></b>
 * </pre>
 * <p/>
 * <pre>
 * <b>&nbsp;--系统拦截器--</b>
 * </pre>
 * <p/>
 * <pre></pre>
 * <p/>
 * JDK版本：JDK1.5.0
 *
 * @author <b>ulyn</b>
 */
@Component("systemInterceptor")
@Repository
public class SystemInterceptor extends HandlerInterceptorAdapter {

    /**
     * 在Controller方法前进行拦截 如果返回false 从当前拦截器往回执行所有拦截器的afterCompletion方法,再退出拦截器链.
     * 如果返回true 执行下一个拦截器,直到所有拦截器都执行完毕. 再运行被拦截的Controller.
     * 然后进入拦截器链,从最后一个拦截器往回运行所有拦截器的postHandle方法.
     * 接着依旧是从最后一个拦截器往回执行所有拦截器的afterCompletion方法.
     */
    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response, Object handler) throws Exception {
        Cookie[] cookies = request.getCookies();
        boolean hasMySessionId = false;
        if (cookies != null) {
            for (int i = 0; i < cookies.length; i++) {
                if (cookies[i].getName().equals("MYSID")) {
                    //return cookies[i].getValue();
                    hasMySessionId = true;
                    break;
                }
            }
        }
        if (!hasMySessionId) {
            String sid = generate();
            Cookie c = new Cookie("MYSID", sid);
            c.setMaxAge(-1);
            c.setPath("/");
//            System.out.println("生成SID:"+sid);
            request.setAttribute("MYSID", sid);
            response.addCookie(c);
        }

        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");

        String uri = request.getRequestURI();
        System.out.println("SystemInterceptor拦截到请求：" + uri);
        //设置当前请求的uri，供jsp页面获取
        request.setAttribute("getRequestURI", request.getRequestURI());

        String path = request.getContextPath();
        String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + path + "/";
        //判断拦截的请求路径 uri.indexOf("background") != -1
        if (!uri.equals(path + "/") && !uri.equals(path)) {
            String[] noFilters = new String[]{"login.do", "logout.do", "modifyPwd.do", "getSysStatus.do", "testJSONP.do"};
            boolean beFilter = true;
            for (String s : noFilters) {
                if (uri.indexOf(s) != -1) {
                    beFilter = false;
                    break;
                }
            }
            if (beFilter) {
                //Object obj = Session.getInstance().getAttribute(request,CacheConstants.SESSIONUSERKEY);
                Object obj = null;
                if (null == obj && uri.indexOf("webapp") == -1) {
                    // 未登录
                    throw new AuthException("您还没有登陆或者页面过期，请重新登录");
                } else {
                    if (uri.indexOf("webapp") == -1) {
                        //以下判断是否有权限访问,不是APP的url
//                        ArrayList<String> permitUrlList = (ArrayList<String>)Session.getInstance().getAttribute(request,Constants.PERMITURL);
//                        for (String url : permitUrlList) {
//                            if (uri.indexOf(url) != -1) {
//                                beFilter = false;
//                                break;
//                            }
//                        }
                        //ture说明没有url访问权限
//                        if(beFilter){
//                            StringBuilder builder = new StringBuilder();
//                            builder.append("<script type=\"text/javascript\" charset=\"UTF-8\">");
//                            builder.append("alert('您没有权限访问本页面');");
//                            builder.append("top.location.href='");
//                            builder.append(basePath);
//                            builder.append("login.do';</script>");
//                            printWriter(response,builder.toString());
//                            return false;
//                        }
                    } else {
                        //此处过滤webApp的处理
                    }
                }
                //通过拦截


                Cookie visitorTime = new Cookie("visitorTime", getDateString(new Date()));
                visitorTime.setMaxAge(-1);
                visitorTime.setPath("/");
                response.addCookie(visitorTime);
            }
        }

        return super.preHandle(request, response, handler);
    }

    public static String getDateString(Date date) {

        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String str = "";
        if (date != null) {
            try {
                str = format.format(date);
            } catch (Exception e) {
                System.err.println("日期转换为yyyy-MM-dd HH:mm:ss格式的字符串出错!");
            }
        }
        return str;
    }


    @Override
    public void postHandle(HttpServletRequest request,
                           HttpServletResponse response, Object handler,
                           ModelAndView modelAndView) throws Exception {
//
//		if (modelAndView != null) {
//			String viewName = modelAndView.getViewName();
//		//	System.out.println("view name : " + viewName);
//		} else {
//		//	System.out.println("view null ");
//		}
    }

    /**
     * 在Controller方法后进行拦截 当有拦截器抛出异常时,会从当前拦截器往回执行所有拦截器的afterCompletion方法
     */
    @Override
    public void afterCompletion(HttpServletRequest httpservletrequest,
                                HttpServletResponse httpservletresponse, Object obj,
                                Exception exception) throws Exception {

    }

    /**
     * 产生一个32位的UUID
     *
     * @return
     */

    public static String generate() {
        String s = UUID.randomUUID().toString();
        //去掉“-”符号
        return s.substring(0, 8) + s.substring(9, 13) + s.substring(14, 18) + s.substring(19, 23) + s.substring(24);


    }

}