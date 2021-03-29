<%@ include file="../Main/Main.jsp"%>
<%@ page import="extraCosts.MulEditTest" %> 
<%
 	String pageName1 = this.getClass().getPackage().getName()+"."+this.getClass().getSimpleName();
MulEditTest ecm = new MulEditTest(); 
 	Render(ecm  , out , request, response , Myglobals , objectState , pageName1); 
 	
%>
<%@ include file="../Main/footer.jsp"%>

