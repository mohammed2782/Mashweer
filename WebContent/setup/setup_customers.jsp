<%@ include file="../Main/Main.jsp"%>
<%@ page import="com.app.setup.setup_customers  " %> 
<%
 	String pageName1 = this.getClass().getPackage().getName()+"."+this.getClass().getSimpleName();
	setup_customers sc = new setup_customers(); 
 	Render(sc  , out , request, response , Myglobals , objectState , pageName1); 
 	
%>

<%@ include file="../Main/footer.jsp"%>