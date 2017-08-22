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
		<table style="width: 100%">
			  <tr>
			    <th>日期</th>
			    <th>上班时长（分钟）</th>
			    <th>到达时间</th>
			    <th>离开时间</th>
			    <th>是否满足8小时</th>
			  </tr>
<%	
	@SuppressWarnings("unchecked")
	TreeSet<Record> records = (TreeSet<Record>) request.getAttribute("record");
	for (Record record :records) {
%>	
		<tr>
			<td align="center"><%=record.getDate() %></td>
			<td align="center"><%=record.getTotalTime() %></td>
			<td align="center"><%=record.getArriveTime() %></td>
			<td align="center"><%=record.getDepartTime() %>></td>
			<td align="center"><%=record.isEightHour() %></td>
		</tr>
<%
	}
%>
		</table>
		<h1><%= request.getParameter("name") %>的不完整考勤记录:</h1><br>
		<table style="width: 100%">
			  <tr>
			    <th>日期</th>
			    <th>打卡时间</th>
			  </tr>
<%
	@SuppressWarnings("unchecked")
	TreeSet<Record> abnornal_records = (TreeSet<Record>) request.getAttribute("abnormal_record");
	for (Record abnormal_record : abnornal_records) {
%>	
		<tr>
			<td align="center"><%=abnormal_record.getDate() %></td>
			<td align="center"><%=abnormal_record.getArriveTime() %></td>
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