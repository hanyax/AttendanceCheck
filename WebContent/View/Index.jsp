<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<title>考勤查询</title>
	</head>
	<body>
<%
    String error = (String) request.getAttribute("error");
    if(error != null) {
%>
        <h1><%= error %></h1><br><br>
<%                
    }
%>
        <h1>考勤查询</h1>
        <form method='post' action='checkAttendance.do'>
        		姓名:<br>
  			<input type="text" name="name" value='${param.name}'><br><br>
  			<input type="submit" value="查询">
         </form>
	</body>
</html>