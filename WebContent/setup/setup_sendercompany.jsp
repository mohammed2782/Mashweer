<%@ include file="../Main/Main.jsp"%>
<%@ page import="com.app.setup.SetupSenderCompanies  " %> 
<%
 	String pageName1 = this.getClass().getPackage().getName()+"."+this.getClass().getSimpleName();
	SetupSenderCompanies ssc = new SetupSenderCompanies(); 
 	Render(ssc  , out , request, response , Myglobals , objectState , pageName1); 
 	
%>

<%@ include file="../Main/footer.jsp"%>