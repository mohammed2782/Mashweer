<%@ include file="../Main/Main.jsp"%>
<%@ page import="com.app.setup.setup_item" %> 

<%  
	String pageName1 = this.getClass().getPackage().getName()+"."+this.getClass().getSimpleName();
setup_item si = new setup_item(); 
	Render(si , out , request, response , Myglobals , objectState , pageName1);

%>
<%@ include file="../Main/footer.jsp"%> 