<%@ include file="../Main/Main-popup.jsp"%>
<%@ page import="com.app.setup.AssignBookReceipts, java.sql.PreparedStatement, java.sql.ResultSet, com.app.util.Utilities" %>
<%  

String bookid = (String)request.getParameter("bookid");

if (bookid !=null){
	Myglobals.smartyGlobalsAssArr.put("bookid", (String)bookid);
}else if (Myglobals.smartyGlobalsAssArr.containsKey("bookid") && Myglobals.smartyGlobalsAssArr.get("bookid")!=null){
	bookid = (String)Myglobals.smartyGlobalsAssArr.get("bookid");
}
Connection conn = null;
Utilities ut = new Utilities();
HashMap<String, String> info = new HashMap<String, String>();
try{
	conn = mysql.getConn();
	
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
		              <h5>رقم الدفتر<%=bookid %></h5>
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
AssignBookReceipts assignBookReceipts = new AssignBookReceipts(); 
Render(assignBookReceipts  , out , request, response , Myglobals , objectState , pageName1);
%> 
<script>
   /* window.onunload = refreshParent;
    function refreshParent() {
        window.opener.location.reload();
    }
    */
</script>
<jsp:include page="../Main/footer-popup.jsp" />