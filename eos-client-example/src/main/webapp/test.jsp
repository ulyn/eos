<%@ page import="com.sunsharing.eos.client.EosInit" %>
<%@ page import="com.sunsharing.eos.client.ServiceContext" %>
<%@ page import="com.sunsharing.component.test.TestType" %>
<%@ page import="java.util.concurrent.ExecutorService" %>
<%@ page import="java.util.concurrent.Executors" %>
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
        ExecutorService service = Executors.newCachedThreadPool();
        for(int i=0;i<100;i++)
        {
            final int k = i;
            service.execute(new Runnable() {
                @Override
                public void run() {
                    TestType test = ServiceContext.getBean(TestType.class);
                    test.testString(k+"","2");
                }
            });
        }

    %>
</body>
</html>
