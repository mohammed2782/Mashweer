<%@ include file="../Main/Main-popup.jsp"%>
<%@ page import="com.app.bussframework.StepsDecisions,	java.sql.PreparedStatement, java.sql.ResultSet " %>
<% 

String stp_id = (String)request.getParameter("stp_id");

if (stp_id !=null){
	Myglobals.smartyGlobalsAssArr.put("stp_id", (String)stp_id);
}else if (Myglobals.smartyGlobalsAssArr.containsKey("stp_id") && Myglobals.smartyGlobalsAssArr.get("stp_id")!=null){
	stp_id = (String)Myglobals.smartyGlobalsAssArr.get("stp_id");
	stp_id = (String)Myglobals.smartyGlobalsAssArr.get("stp_id");
}

 
%>
<div class="row">
<div class="col-md-12">
          <div class="panel panel-warning">
            <div class="panel-heading">
            	<div class='row'>
	            <div class='col-xs-5'>
	              <h5></h5>
	            </div>
	            <div class='col-xs-5'>
	              <h5></h5>
	            </div>
	            <div class='col-xs-2'>
	            	<h5></h5>
	            </div>
	            </div>
            </div>
            <div class="panel-body" style='padding:3px;'>
            	<div class='row'>
            		<div class='col-xs-6'><h6>Academic Year :</h6></div>
            		<div class='col-xs-6'><h6>Semester : </h6></div>
            		
              	</div>
            </div>
        </div>
	</div>
</div>
<%

	String pageName1 = this.getClass().getPackage().getName()+"."+this.getClass().getSimpleName();
	StepsDecisions sd = new StepsDecisions(); 
	Render(sd  , out , request, response , Myglobals , objectState , pageName1);
%> 
<script>
   /* window.onunload = refreshParent;
    function refreshParent() {
        window.opener.location.reload();
    }
    */
</script>
<jsp:include page="../Main/footer-popup.jsp" />