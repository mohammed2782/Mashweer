<%@ include file="../Main/Main.jsp"%>
<%@ page import="com.app.setup.SetupStores" %> 

<%  
	String pageName1 = this.getClass().getPackage().getName()+"."+this.getClass().getSimpleName();
    SetupStores ss = new SetupStores(); 
	Render(ss , out , request, response , Myglobals , objectState , pageName1);

%>
<%@ include file="../Main/footer.jsp"%> 