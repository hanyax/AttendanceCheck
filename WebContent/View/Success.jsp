<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>考勤记录</title>
</head>
<body>
        <h1>考勤记录</h1><br>
<%	
	String name = request.getParameter("name");
%>
		<h2><%= name %>:</h2>
		
        <form method='post' action='checkAttendance.do'>
        		姓名:<br>
  			<input type="text" name="firstname" value='${param.name}'><br><br>
  			<input type="submit" value="查询">
         </form>
</body>
</html>