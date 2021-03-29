<%@ include file="../Main/Main.jsp"%>
<%@ page import="report.Items_rpt2" %> 
<%
 	String pageName1 = this.getClass().getPackage().getName()+"."+this.getClass().getSimpleName();
	Items_rpt2 rptItem = new Items_rpt2(); 
 	Render(rptItem  , out , request, response , Myglobals , objectState , pageName1); 
 	
%>
<%@ include file="../Main/footer.jsp"%>