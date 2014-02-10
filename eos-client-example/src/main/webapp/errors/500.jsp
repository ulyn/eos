<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>服务器处理内部错误</title>
<style>
.itpub_404{width:750px; margin:0 auto; padding:58px 0 74px; overflow:hidden}
.itpub_404_l{width:480px; float:left}
.itpub_404_r{width:222px; float:right}
.itpub_404_r p{color:#666; font-size:12px;}
.itpub_404_r p.text1{color:#cc0000; font-weight:bold; font-size:16px; padding:20px 0 10px}
.itpub_404_r p.text2{padding-top:20px; line-height:22px}
.itpub_404_r p.text2 a{text-decoration:underline}
</style>
</head>
<body>
<div class="itpub_404">
    	<div class="itpub_404_l">	<img title="找不到请求的页面" src="<c:url value="/styles/img/error/500page.jpg"/>"  alt="500page " width="480" height="202" /></div>
        <div class="itpub_404_r">
        	<p class="text1">哎呀！服务器处理内部错误!</p>
            <p>可能是您的操作过快，请再试试下吧^-^</p>
            <p>如果还有问题，您在意见反馈栏目给我们反馈，谢谢!</p>
            <p class="text2">
                <a href="javascript:history.back();" target="_top"/>请点击后退一页</a><br/>
                <a href="<c:url value="/login.do"/>" target="_top">请点击返回登陆页</a><br/>
            </p>
        </div>
    </div>
</body>
</html>