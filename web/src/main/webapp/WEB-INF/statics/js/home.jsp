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

<form action="/home/" method="post" id="content">
    报名意向:
    <select name="selectedSale" form="content">
        <%
            List<String> besales = (List<String>)request.getAttribute("beSale");
            for (String besale : besales) {
                out.print(String.format("<option value=\"%s\" selected = \"selected\">%s</option>", besale, besale));
            }
        %>
    </select>
    <input class="btn" type="submit" value='保存'/>
</form>

</body>
</html>
