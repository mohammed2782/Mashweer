<%@ include file="../Main/Main.jsp"%>
<%@ page import="com.app.incomeoutcome.BatchesReport" %> 
<%
 	String pageName1 = this.getClass().getPackage().getName()+"."+this.getClass().getSimpleName();
	BatchesReport br = new BatchesReport();  
 	Render(br  , out , request, response , Myglobals , objectState , pageName1); 
 	
%>
<%@ include file="../Main/footer.jsp"%>

