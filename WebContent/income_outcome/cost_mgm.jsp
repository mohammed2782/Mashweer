<%@ include file="../Main/Main.jsp"%>
<%@ page import="extraCosts.ExtraCostMgm" %> 
<%
 	String pageName1 = this.getClass().getPackage().getName()+"."+this.getClass().getSimpleName();
ExtraCostMgm ecm = new ExtraCostMgm(); 
 	Render(ecm  , out , request, response , Myglobals , objectState , pageName1); 
 	
%>
<%@ include file="../Main/footer.jsp"%>

