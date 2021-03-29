<%@ include file="../Main/Main.jsp"%>
<%@ page import="com.app.cases.ViewOnlyAllCases" %> 


<%
 	String pageName1 = this.getClass().getPackage().getName()+"."+this.getClass().getSimpleName();
 ViewOnlyAllCases uc = new ViewOnlyAllCases(); 
  	Render(uc  , out , request, response , Myglobals , objectState , pageName1);
 %>
<%@ include file="../Main/footer.jsp"%>



<script>


$('#c_custreceiptnoori').focus();
</script>