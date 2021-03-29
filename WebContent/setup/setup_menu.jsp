<%@ include file="../Main/Main.jsp"%>
<%@ page import="com.app.setup.setup_menu ,com.app.setup.setup_sub_menu " %> 
<%
 	String pageName1 = this.getClass().getPackage().getName()+"."+this.getClass().getSimpleName();
    setup_menu sm = new setup_menu(); 
 	Render(sm  , out , request, response , Myglobals , objectState , pageName1);  	
%>

<div class='clearfix'></div>
<br>
<div class='row'>
	<div class='col-md-12'> 
	<%
		if (request.getParameter("mt_id")!=null){
			if (!request.getParameter("mt_id").isEmpty()){
				Myglobals.smartyGlobalsAssArr.put("mt_id", request.getParameter("mt_id"));
			}else{
				Myglobals.smartyGlobalsAssArr.remove("mt_id");
			}
		}
		
		if (Myglobals.smartyGlobalsAssArr.get("mt_id")!=null){
			setup_sub_menu ssm = new setup_sub_menu();
			Render(ssm  , out , request, response , Myglobals , objectState , pageName1); 
		}
	%>
</div>
 </div>
<%@ include file="../Main/footer.jsp"%>