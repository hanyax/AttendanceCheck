<%@page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" import="java.util.*, Model.Record"
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<title>考勤记录</title>
	</head>
	<body>
		<h1><%= request.getParameter("name") %>的考勤记录:</h1><br>
		<table>
			  <tr>
			    <th>日期</th>
			    <th>到达时间</th>
			    <th>离开时间</th>
			  </tr>
<%
	TreeSet<Record> records = (TreeSet<Record>) request.getAttribute("record");
	for (Record record :records) {
%>	
		<tr>
			<td><%=record.getDate() %></td>
			<td><%=record.getArriveTime() %></td>
			<td><%=record.getDepartTime() %>></td>
		</tr>
<%
	}
%>
		</table>
		<h1><%= request.getParameter("name") %>的不完整考勤记录:</h1><br>
		<table>
			  <tr>
			    <th>日期</th>
			    <th>打卡时间</th>
			  </tr>
<%
	TreeSet<Record> abnornal_records = (TreeSet<Record>) request.getAttribute("abnormal_record");
	for (Record abnormal_record : abnornal_records) {
%>	
		<tr>
			<td><%=abnormal_record.getDate() %></td>
			<td><%=abnormal_record.getArriveTime() %></td>
		</tr>
<%
	}
%>
		</table>
		<br>
		
		<form action="View/index.jsp">
		    <input type="submit" value="返回" />
		</form>
	</body>
</html>