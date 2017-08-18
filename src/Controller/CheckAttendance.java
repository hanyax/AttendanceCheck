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
		@WebInitParam(name = "SUCCESS_VIEW", value = "Success.jsp"),
		@WebInitParam(name = "ERROR_VIEW", value = "Index.jsp")
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
     * 
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) 
    		throws ServletException, IOException {
	    String name = request.getParameter("name");
	    String page;
	    AttendanceDAO attendance = (AttendanceDAO) getServletContext().getAttribute("attendanceService");
	    TreeSet<Record> records = attendance.getAttendance(name);
	    TreeSet<Record> abnormal_records = attendance.getAbnormalAttendance(name);
	    if (records.isEmpty()) {
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
