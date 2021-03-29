<%@ include file="../Main/Main-popup.jsp"%>
<%@ page import="com.app.setup.CompainesSpecialPrices, java.sql.PreparedStatement, java.sql.ResultSet,com.app.util.Utilities" %>
<%  

String compidspecialprice = (String)request.getParameter("compidspecialprice");

if (compidspecialprice !=null){
	Myglobals.smartyGlobalsAssArr.put("compidspecialprice", (String)compidspecialprice);
}else if (Myglobals.smartyGlobalsAssArr.containsKey("compidspecialprice") && Myglobals.smartyGlobalsAssArr.get("compidspecialprice")!=null){
	compidspecialprice = (String)Myglobals.smartyGlobalsAssArr.get("compidspecialprice");
}
Connection conn = null;
Utilities ut = new Utilities();
HashMap<String, String> info = new HashMap<String, String>();
try{
	conn = mysql.getConn();
	//info = ut.getAgentInfo(conn, districtsusid);
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
		              <h5></h5>
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
CompainesSpecialPrices cprices = new CompainesSpecialPrices(); 
Render(cprices  , out , request, response , Myglobals , objectState , pageName1);
%> 
<script>
   /* window.onunload = refreshParent;
    function refreshParent() {
        window.opener.location.reload();
    }
    */
</script>
<jsp:include page="../Main/footer-popup.jsp" />