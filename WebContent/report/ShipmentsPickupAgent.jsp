<%@ include file="../Main/Main.jsp"%>
<%@ page import="com.app.reports.ShipmentsPickupAgent"%>
<%
	String pageName1 = this.getClass().getPackage().getName() + "."
	+ this.getClass().getSimpleName();
	ShipmentsPickupAgent spa = new ShipmentsPickupAgent();
	Render(spa, out, request, response, Myglobals, objectState,
	pageName1);
%>
<%@ include file="../Main/footer.jsp"%>
 