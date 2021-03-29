<%@ include file="../Main/Main.jsp"%>
<%@ page import="com.app.setup.KbgeneralSetup" %> 
<%
 	String pageName1 = this.getClass().getPackage().getName()+"."+this.getClass().getSimpleName();
	KbgeneralSetup ecm = new KbgeneralSetup(); 
 	Render(ecm  , out , request, response , Myglobals , objectState , pageName1); 
 	
%>
<%@ include file="../Main/footer.jsp"%>
