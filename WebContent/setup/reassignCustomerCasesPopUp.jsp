<%@ include file="../Main/Main-popup.jsp"%>
<%@ page import="com.app.setup.ReAssignCasesFromCustomers, java.sql.PreparedStatement, java.sql.ResultSet,com.app.util.Utilities" %>
<%  

String custidreassign = (String)request.getParameter("custidreassign");

if (custidreassign !=null){
	Myglobals.smartyGlobalsAssArr.put("custidreassign", (String)custidreassign);
}else if (Myglobals.smartyGlobalsAssArr.containsKey("custidreassign") && Myglobals.smartyGlobalsAssArr.get("custidreassign")!=null){
	custidreassign = (String)Myglobals.smartyGlobalsAssArr.get("custidreassign");
}
Connection conn = null;
Utilities ut = new Utilities();
String custName = "";
try{
	conn = mysql.getConn();
	custName = ut.getCustomerName(conn, Integer.parseInt(custidreassign));
}catch(Exception e){
	e.printStackTrace();
}finally{ 
	try{conn.close();}catch(Exception e){}
}
%>
<div class="row">
<div class="col-md-12">
          <div class="panel panel-warning">
            <div class="panel-heading">
            	<div class='row'>
		            <div class='col-xs-5'>
		              <h5><%=custName %></h5>
		            </div>
		            <div class='col-xs-5'>
		              <h5></h5>
		            </div>
		            
	            </div>
            </div>
        </div>
	</div>
</div>
<%
String pageName1 = this.getClass().getPackage().getName()+"."+this.getClass().getSimpleName();
ReAssignCasesFromCustomers reAssignCasesFromCustomers = new ReAssignCasesFromCustomers(); 
Render(reAssignCasesFromCustomers  , out , request, response , Myglobals , objectState , pageName1);
%> 
<script>
   /* window.onunload = refreshParent;
    function refreshParent() {
        window.opener.location.reload();
    }
    */
</script>
<jsp:include page="../Main/footer-popup.jsp" />