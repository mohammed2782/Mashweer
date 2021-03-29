<%@ include file="../Main/Main.jsp"%>
<%@ page import="report.SuppBalance, report.SuppBalanceDetails" %> 
<%
 	String pageName1 = this.getClass().getPackage().getName()+"."+this.getClass().getSimpleName();
	SuppBalance sb = new SuppBalance(); 
 	Render(sb  , out , request, response , Myglobals , objectState , pageName1); 
 	
%>
<div class='clearfix'></div>
<br>
<div class='row'>
	<div class='col-md-12'>
	<%
		if (request.getParameter("transrptsuppid")!=null){
			if (!request.getParameter("transrptsuppid").isEmpty()){
				Myglobals.smartyGlobalsAssArr.put("transrptsuppid", request.getParameter("transrptsuppid"));
			}else{
				Myglobals.smartyGlobalsAssArr.remove("transrptsuppid");
			}
		}
		
		if (Myglobals.smartyGlobalsAssArr.get("transrptsuppid")!=null){
			SuppBalanceDetails sbd = new SuppBalanceDetails();
			Render(sbd  , out , request, response , Myglobals , objectState , pageName1); 
		}
	%>
</div>
 </div>
<%@ include file="../Main/footer.jsp"%>
