<%@ page import="java.util.List" %><%--
  Created by IntelliJ IDEA.
  User: penghao
  Date: 2018/5/21
  Time: 上午11:56
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>数据统计</title>
</head>
<body>

<form action="/home/start" method="post" id="start">
    开启服务:
    <input class="btn" type="submit" value='开启'/>
</form>
<form action="/home/scan" method="post" id="scan">
    扫描目录:
    <input class="btn" type="submit" value='扫描'/>
</form>

</body>
</html>
