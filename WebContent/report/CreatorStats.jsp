<%@ include file="../Main/Main.jsp"%>
<%@ page import="com.app.reports.CreatorStats" %> 
<%
 	String pageName1 = this.getClass().getPackage().getName()+"."+this.getClass().getSimpleName();
	CreatorStats mp = new CreatorStats(); 
 	Render(mp  , out , request, response , Myglobals , objectState , pageName1); 
 	
%>
<%@ include file="../Main/footer.jsp"%> 