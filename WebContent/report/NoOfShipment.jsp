<%@ include file="../Main/Main.jsp"%>
<%@ page import="com.app.reports.NoOfShipment" %> 
<%
 	String pageName1 = this.getClass().getPackage().getName()+"."+this.getClass().getSimpleName();
	NoOfShipment noOfShipment = new NoOfShipment(); 
 	Render(noOfShipment  , out , request, response , Myglobals , objectState , pageName1); 
 	
%>
<%@ include file="../Main/footer.jsp"%> 

 