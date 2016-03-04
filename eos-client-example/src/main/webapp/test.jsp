<%@ page import="com.sunsharing.eos.client.ServiceContext" %>
<%@ page import="com.sunsharing.eos.client.rpc.DynamicRpc" %>
<%@ page import="com.sunsharing.eos.clientexample.test.TestService" %>
<%@ page import="java.util.List" %>
<%--
  Created by IntelliJ IDEA.
  User: criss
  Date: 14-2-11
  Time: 下午12:00
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title></title>
</head>
<body>
<%
    //        ExecutorService service = Executors.newCachedThreadPool();
//        for(int i=0;i<100;i++)
//        {
//            final int k = i;
//            service.execute(new Runnable() {
//                @Override
//                public void run() {
//                    TestType test = ServiceContext.getBean(TestType.class);
//                    test.testString(k+"","2");
//                }
//            });
//        }
    List map = DynamicRpc.create("jedi", "complaint", "1.0")
            .setMock("success")
            .doInvoke(List.class, "getComplaintHistory");
    response.getWriter().println("dong tai diao yong:" + map);
    TestService test = ServiceContext.getBean(TestService.class);
    response.getWriter().println(test.testString("1", "1"));
//        try
//        {
//        Test2 test = ServiceContext.getBean(Test2.class);
//        test.sayHello("abcd");
//        }catch (Exception e)
//        {
//            e.printStackTrace();
//        }
%>
</body>
</html>
