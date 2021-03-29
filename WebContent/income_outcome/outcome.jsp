<%@ include file="../Main/Main.jsp"%>
<%@ page import="com.app.incomeoutcome.outcome" %> 
<%
 	String pageName1 = this.getClass().getPackage().getName()+"."+this.getClass().getSimpleName();
outcome ouc = new outcome(); 
  	Render(ouc  , out , request, response , Myglobals , objectState , pageName1);
 %>
<%@ include file="../Main/footer.jsp"%>

