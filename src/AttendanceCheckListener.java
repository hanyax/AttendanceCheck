

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

/**
 * Application Lifecycle Listener implementation class AttendanceCheckListener
 *
 */
@WebListener
public class AttendanceCheckListener implements ServletContextListener {
	/**
     * @see ServletContextListener#contextInitialized(ServletContextEvent)
     */
    public void contextInitialized(ServletContextEvent arg)  { 
         ServletContext context = arg.getServletContext();
         // Set XML later 
         AttendanceDAOImplement attendance = new AttendanceDAOImplement("/Users/shawn-xu/Desktop/5.26-6.25.xlsx");
         context.setAttribute("attendanceService", attendance);
    }
    
	/**
     * @see ServletContextListener#contextDestroyed(ServletContextEvent)
     */
    public void contextDestroyed(ServletContextEvent arg)  {
    }
	
}