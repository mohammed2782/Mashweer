<%@ include file="../Main/Main-popup.jsp"%>
<%@ page import="com.app.cases.SingleCaseInformation" %>
<%  



String pageName1 = this.getClass().getPackage().getName()+"."+this.getClass().getSimpleName();
SingleCaseInformation sci = new SingleCaseInformation(); 
Render(sci  , out , request, response , Myglobals , objectState , pageName1);
%> 

<jsp:include page="../Main/footer-popup.jsp" />