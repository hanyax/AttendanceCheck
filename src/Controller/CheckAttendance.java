package Controller;

import java.io.IOException;
import java.util.TreeSet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import Model.AttendanceDAO;
import Model.Record;

/**
 * Servlet implementation class CheckAttendance
 */

@WebServlet(
	urlPatterns={"/checkAttendance.do"},
    initParams={
		@WebInitParam(name = "SUCCESS_VIEW", value = "/View/success.jsp"),
		@WebInitParam(name = "ERROR_VIEW", value = "/View/index.jsp")
    }
)

public class CheckAttendance extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private String SUCCESS_VIEW;
    private String ERROR_VIEW;
    
    @Override
    public void init() throws ServletException {
        SUCCESS_VIEW = getServletConfig().getInitParameter("SUCCESS_VIEW");
        ERROR_VIEW = getServletConfig().getInitParameter("ERROR_VIEW");
    }
    
    /**
     * Direct web service to difference page based on the record of attendance
     * 				if attendance record exist, direct to success page to display records
     * 				if attendance record does not exist, direct to index page and prompt to re-enter
     * 
     * 将网络服务导向不同页面，	如有出勤记录，导入成功页面并显示出勤记录
     * 						如无出勤记录，导入index页面并显示无出勤记录
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) 
    		throws ServletException, IOException {
    		request.setCharacterEncoding("UTF-8");
	    String name = request.getParameter("name");
	    String page;
	    AttendanceDAO attendance = (AttendanceDAO) getServletContext().getAttribute("attendanceService");
	    TreeSet<Record> records = attendance.getAttendance(name);
	    TreeSet<Record> abnormal_records = attendance.getAbnormalAttendance(name);
	    if (records.isEmpty() && abnormal_records.isEmpty()) {
	    		request.setAttribute("error", "该姓名无考勤记录");
	    		page = ERROR_VIEW;
	    } else {
	    		request.setAttribute("record", records);
	    		request.setAttribute("abnormal_record", abnormal_records);
	    		page = SUCCESS_VIEW;
	    }
	    request.getRequestDispatcher(page).forward(request, response);
    }
	
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		processRequest(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
