<%@ include file="../Main/Main.jsp"%>
<%@ page import="com.app.cases.HistoryRtnSender" %>

	
	<%
		String pageName1 = this.getClass().getPackage().getName()+"."+this.getClass().getSimpleName();
		HistoryRtnSender historyRtnSender = new HistoryRtnSender(); 
 		Render(historyRtnSender  , out , request, response , Myglobals , objectState , pageName1);
 	 %>
	

<%@ include file="../Main/footer.jsp"%> 



