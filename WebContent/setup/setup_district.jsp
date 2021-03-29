<%@ include file="../Main/Main.jsp"%>
<%@ page import="com.app.setup.SetupDistricts " %> 
<%
 	String pageName1 = this.getClass().getPackage().getName()+"."+this.getClass().getSimpleName();
	SetupDistricts sd = new SetupDistricts(); 
 	Render(sd  , out , request, response , Myglobals , objectState , pageName1); 
 	
%>

<%@ include file="../Main/footer.jsp"%>