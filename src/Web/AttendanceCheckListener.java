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
	private final String DEFAULT_DIR_PATH = "/Users/shawn-xu/Desktop";
    private final String DEFAULT_NORMAL_TABLE_NAME = "test";
    private final String DEFAULT_ABNORMAL_TABLE_NAME = "abnormal";
    private final String DEFAULT_JDNI_NAME = "jdbc/attendance";
    
	/**
	 * Change default settings above to reuse code
	 * DEFAULT_DIR_PATH: the default directory where all .xlsx file will be read and used
	 * DEFAULT_NORMAL_TABLE_NAME: the default database table name for storing normal attendance record
	 * DEFAULT_ABNORMAL_TABLE_NAME: the default database table name for storing abnormal attendance record
	 * DEFAULT_JDNI_NAME: the default JDNI name for datasource configuration
	 * 
     * @see ServletContextListener#contextInitialized(ServletContextEvent)
     */
    public void contextInitialized(ServletContextEvent arg)  { 
         ServletContext context = arg.getServletContext();
         try {
        	 	Context initContext = new InitialContext();
	    	 	Context envContext = (Context) initContext.lookup("java:/comp/env");
	    	 	DataSource dataSource = (DataSource) envContext.lookup(DEFAULT_JDNI_NAME);
	        AttendanceDAOImplement attendance = new AttendanceDAOImplement(DEFAULT_DIR_PATH, dataSource, 
	        		DEFAULT_NORMAL_TABLE_NAME, DEFAULT_ABNORMAL_TABLE_NAME);
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
