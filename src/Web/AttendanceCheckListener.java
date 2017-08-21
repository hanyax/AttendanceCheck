package Web;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import javax.sql.DataSource;

import Model.AttendanceDAOImplement;

/**
 * Application Lifecycle Listener implementation class AttendanceCheckListener
 */
@WebListener
public class AttendanceCheckListener implements ServletContextListener {
	/**
     * @see ServletContextListener#contextInitialized(ServletContextEvent)
     */
    public void contextInitialized(ServletContextEvent arg)  { 
         ServletContext context = arg.getServletContext();
         try {
        	 	Context initContext = new InitialContext();
	    	 	Context envContext = (Context) initContext.lookup("java:/comp/env");
	    	 	DataSource dataSource = (DataSource) envContext.lookup("jdbc/attendance");
	        AttendanceDAOImplement attendance = new AttendanceDAOImplement("/Users/shawn-xu/Desktop", dataSource, "test", "abnormal");
	        context.setAttribute("attendanceService", attendance);
         } catch(NamingException ex) {
        	 	throw new RuntimeException(ex);
         }
    }
    
	/**
     * @see ServletContextListener#contextDestroyed(ServletContextEvent)
     */
    public void contextDestroyed(ServletContextEvent arg)  {
    }
	
}
