<%@ include file="../Main/Main-popup.jsp"%>
<%@ page import="com.app.cases.AuditTrailPopUp, java.sql.PreparedStatement, java.sql.ResultSet,com.app.util.Utilities" %>
<%  
 
String auditcaseid = (String)request.getParameter("auditcaseid");
String auditreceiptnoori = (String)request.getParameter("auditreceiptnoori");
if (auditcaseid !=null){
	Myglobals.smartyGlobalsAssArr.put("auditcaseid", (String)auditcaseid);
	Myglobals.smartyGlobalsAssArr.put("auditreceiptnoori", (String)auditreceiptnoori);
}else if (Myglobals.smartyGlobalsAssArr.containsKey("auditcaseid") && Myglobals.smartyGlobalsAssArr.get("auditcaseid")!=null){
	auditcaseid = (String)Myglobals.smartyGlobalsAssArr.get("auditcaseid");
	auditreceiptnoori = (String)Myglobals.smartyGlobalsAssArr.get("auditreceiptnoori");
} 
Connection conn=null;
HashMap<String,String> caseFinInfo = new HashMap<String,String>();
try{
	conn = mysql.getConn();
	Utilities ut = new Utilities();
	caseFinInfo = ut.getCaseFinInfo(conn, auditcaseid);
	
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
		              <h5>العمليات على الشحنه <%=auditreceiptnoori %></h5>
		            </div>
		            <div class='col-xs-5'>
		              <h5></h5>
		            </div>
		            
	            </div>
	            <div class='row'>
		            <div class='col-xs-3'>
		              <h5>مندوب الإستلام :<%=caseFinInfo.get("pickupagent") %></h5>
		            </div>
		             <div class='col-xs-3'>
		               <h5>دفعة مندوب إستلام <%=caseFinInfo.get("c_pickupagentpmtid") %></h5>
		            </div>
		            
		            <div class='col-xs-3'>
		               <h5>رقم محاسبة مندوب التوصيل <%=caseFinInfo.get("c_agentpmtid") %></h5>
		            </div>
		             <div class='col-xs-3'>
		               <h5>دفعة مقدمة رقم <%=caseFinInfo.get("c_advancepmtid") %></h5>
		            </div>
		            
		             <div class='col-xs-3'>
		               <h5>دفعة للزبون رقم <%=caseFinInfo.get("c_pmtid") %></h5>
		            </div>
		            <div class='col-xs-3'>
		               <h5>رقم محاسبة الشركة المرسلة <%=caseFinInfo.get("c_company_senderpmtid") %></h5>
		            </div>
		            
	            </div>
            </div>
        </div>
	</div>
</div>
<%
String pageName1 = this.getClass().getPackage().getName()+"."+this.getClass().getSimpleName();
AuditTrailPopUp auditTrailPopUp = new AuditTrailPopUp(); 
Render(auditTrailPopUp  , out , request, response , Myglobals , objectState , pageName1);
%> 
<script>
   /* window.onunload = refreshParent;
    function refreshParent() {
        window.opener.location.reload();
    }
    */
</script>
<jsp:include page="../Main/footer-popup.jsp" />