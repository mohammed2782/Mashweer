<%@ include file="../Main/Main-popup.jsp"%>
<%@ page import="com.app.setup.Partners, java.sql.PreparedStatement, java.sql.ResultSet,com.app.util.Utilities" %>
<%  
 
String partnerid = (String)request.getParameter("partnerid");

if (partnerid !=null){
	Myglobals.smartyGlobalsAssArr.put("partnerid", (String)partnerid);
}else if (Myglobals.smartyGlobalsAssArr.containsKey("partnerid") && Myglobals.smartyGlobalsAssArr.get("partnerid")!=null){
	partnerid = (String)Myglobals.smartyGlobalsAssArr.get("partnerid");
}
Connection conn = null;
Utilities ut = new Utilities();
HashMap<String, String> info = new HashMap<String, String>();
try{
	conn = mysql.getConn();
	info = ut.getAgentInfo(conn, partnerid);
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
		              <h5>اسم الشريك : &nbsp<%=info.get("name") %></h5>
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
Partners partnerShare = new Partners(); 
Render(partnerShare  , out , request, response , Myglobals , objectState , pageName1);
%> 
<script>
   /* window.onunload = refreshParent;
    function refreshParent() {
        window.opener.location.reload();
    }
    */
</script>
<jsp:include page="../Main/footer-popup.jsp" />