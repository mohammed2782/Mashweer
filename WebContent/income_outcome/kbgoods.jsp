<%@ include file="../Main/Main.jsp"%>
<%@ page import="extraCosts.Trykbgoods" %> 
<%
 	String pageName1 = this.getClass().getPackage().getName()+"."+this.getClass().getSimpleName();
Trykbgoods ecm = new Trykbgoods(); 
 	Render(ecm  , out , request, response , Myglobals , objectState , pageName1); 
 	
%>
<%@ include file="../Main/footer.jsp"%>

