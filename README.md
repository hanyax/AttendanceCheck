# AttendanceCheck
AttendanceCheck reads excel files of attendance records from certain folder, stores them in database and provide JSPs for user to check attendance record based on name.

## To use this program:
- Change DEFAULT_DIR_PATH in AttendanceCheckListener to the folder where the attendance record is stored
- Configure context.xml which provide Database object to customer Mysql setting
- Build tables with default names normal_record and abnormal_record
- The excel files should store records in following format:

  name:&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; time:

  xxx&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; 2017-5-26 上午 01:00:04

## To reuse code in this program:
- Write new class which implement AttendanceDAO and use Data Access Object mode to implement database IO methods. The class provided here is AttendanceDAOImplement.
- Rewrite web.xml and context.xml to provide DataSource object for MySQL connection
- Change the default parameters in AttendanceCheckListener to custom settings so that database could be built when index.jsp initialized.
