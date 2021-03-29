<%@ include file="../Main/Main.jsp"%>
<%@ page import="com.app.setup.Partners" %> 
<%
 	String pageName1 = this.getClass().getPackage().getName()+"."+this.getClass().getSimpleName();
	Partners ps = new Partners(); 
 	Render(ps  , out , request, response , Myglobals , objectState , pageName1); 
 	
%>
<%@ include file="../Main/footer.jsp"%>
 