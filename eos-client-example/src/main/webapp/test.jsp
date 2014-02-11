<%@ page import="com.sunsharing.eos.client.EosInit" %>
<%@ page import="com.sunsharing.eos.client.ServiceContext" %>
<%@ page import="com.sunsharing.component.test.TestType" %>
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
        TestType test = ServiceContext.getBean(TestType.class);
        test.testInt(1);
    %>
</body>
</html>
