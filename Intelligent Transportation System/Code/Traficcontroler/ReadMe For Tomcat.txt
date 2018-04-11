1. install JDK






3: Copy the folder then paste into the Tomcat (Version 5.0 or 6.0)/webapps/(Agent based testing)

4: set the all paths for both JDK and also Tomcat(5.0/6.0)

    ---------------------------------------------------------------------------
My Computer --->Right Click--->Properties-->Advanced--> EnvironmentVariables--->SystemVariables

Click ---->New
First text box ==> JAVA_HOME
Second Text box==> C:\Program Files\Java\jdk1.5.0_01

click---->New
First text box ==>CATALINA_HOME
Second Text box==>C:\Program Files\Apache Software Foundation\Tomcat 5.0


Click---->New
FirstTextbox==>classpath
First====>C:\Program Files\Java\jdk1.5.0_01\lib;.;C:\Program Files\Apache Software Foundation\Tomcat 5.0\common\lib;.;


Text box==>Path ==> is already there just click path and click edit and paste 
Second Text box==>;.;C:\Program Files\Java\jdk1.5.0_01\bin;.;C:\Program Files\Apache Software Foundation\Tomcat 5.0\bin;.;
------------------------------------------------------------------------------

5: create " agent"  as  DSN for specified JDBC drivers 



6. type the bellow url in browser

if same system execution
	http://localhost:8080/Agent based testing

if diff system
	http://(server system name):8080/Agent based testing

	
