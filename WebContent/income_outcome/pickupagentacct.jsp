<%@ include file="../Main/Main.jsp"%>
<%@ page import="com.app.incomeoutcome.PickUpAgentPayments,com.app.incomeoutcome.PickupAgentAllShipmentsView" %> 
<%@ page import="com.app.util.Utilities,java.sql.PreparedStatement,java.sql.ResultSet,com.app.incomeoutcome.PickUpAgentBalance,com.app.incomeoutcome.ToBeReturnedShipments" %>
<%
	Connection conn = null;
PreparedStatement pst = null;
ResultSet rs = null; 
Utilities ut = new Utilities();

String pickupAgentAcct = (String)request.getParameter("pickupAgentAcct");
//System.out.println("request==>"+request.getParameter("myClassBean"));
String tab1Class = "active in";
String tab2Class = " ";
String tab3Class = " ";
if (request.getParameter("myClassBean")!=null){
	if (request.getParameter("myClassBean").equalsIgnoreCase("com.app.incomeoutcome.PickupAgentAllShipmentsView")){
		tab1Class = "";
		tab2Class = "  ";
		tab3Class = "active in ";
	}else if (request.getParameter("myClassBean").equalsIgnoreCase("com.app.incomeoutcome.ToBeReturnedShipments")){
		tab1Class = " ";
		tab2Class = " active in";
		tab3Class = "";
	}else if (request.getParameter("myClassBean").equalsIgnoreCase("com.app.incomeoutcome.PickUpAgentBalance")){
		tab1Class = "active in ";
		tab2Class = "  ";
		tab3Class = " ";
	}
}
if (pickupAgentAcct !=null){
	Myglobals.smartyGlobalsAssArr.put("pickupAgentAcct", (String)pickupAgentAcct);
	
}else if (Myglobals.smartyGlobalsAssArr.containsKey("pickupAgentAcct") && Myglobals.smartyGlobalsAssArr.get("pickupAgentAcct")!=null){
	pickupAgentAcct = (String)Myglobals.smartyGlobalsAssArr.get("pickupAgentAcct");
}
LinkedHashMap<String,String> pickUpAgentList = new LinkedHashMap<String,String>();
LinkedHashMap<String,String> pickUpAgentInfo = new LinkedHashMap<String,String>();
try{
	conn = mysql.getConn();
	pickUpAgentList = ut.getListOfPickUpAgents(conn);
	if (pickupAgentAcct!=null){
		pickUpAgentInfo= ut.getPickUpAgentInfo(conn, pickupAgentAcct); 
	}
}catch(Exception e){
	e.printStackTrace();
}finally{
	try{rs.close();}catch(Exception e){}
	try{pst.close();}catch(Exception e){}
	try{conn.close();}catch(Exception e){}
}
%>
<!-- page content -->
        
          <div class="row">
            <div class="page-title">
              
			 <form action="?mypickupagentsearch=1" method="post" name="search_cust_form" class="form-horizontal form-label-left" >
              
                <div class="col-md-6 col-sm-6 col-xs-10 col-md-offset-1 ">
                  <div class="input-group" style='display:flex'>
                 
                  <select class='select2_single form-control' id='pickupAgentAcct' style="width: 200px;" name ='pickupAgentAcct' >
                  	<option value='' ></option>
                    <%
                    	for (String usid : pickUpAgentList.keySet()){
                        	if (pickupAgentAcct!=null && pickupAgentAcct.equalsIgnoreCase(usid)){
                    %>
                    		<option value='<%=usid%>' selected><%=pickUpAgentList.get(usid)%></option>
                    		<%
                    			}else{
                    		%>
                    			<option value='<%=usid%>' ><%=pickUpAgentList.get(usid)%></option>
                    		<%
                    			}
                    		                    		                    		                    	}
                    		%>
                    </select>
                    <span class="input-group-btn">
                      <button type='submit' class=" btn btn-success" style='margin-right:10px' type="button"> عرض التفاصيل الماليه لمندوب الأستلام <i class="fa fa-search m-right-xs"></i>
                      </button>
                    </span>
                    
                  </div>
                </div>
              
               </form>
            </div>
      <%
      	if (pickupAgentAcct!=null && !pickupAgentAcct.trim().equalsIgnoreCase("")){
      %>
            <div class="clearfix"></div>

            <div class="row">
              <div class="col-md-12 col-sm-12 col-xs-12">
                <div class="x_panel">
                  <div class="x_title">
                    <h2><%=pickUpAgentInfo.get("name")%> <small></small></h2>
                    
                    <div class="clearfix"></div>
                  </div>
                  <div class="x_content">
                    <div class="col-md-1 col-sm-1 col-xs-12 profile_right">
                      <h3></h3>

                      <ul class="list-unstyled user_data">
                        
                        <li>
                          <i class="fa fa-phone user-profile-icon"></i> <%=pickUpAgentInfo.get("hp")%>
                        </li>
                      </ul>

                     
                      <br />
                    </div>
                    <div class="col-md-11 col-sm-11 col-xs-12">

                      <div class="" role="tabpanel" data-example-id="togglable-tabs">
                        <ul id="myTab" class="nav nav-tabs bar_tabs bar_tabs_green" role="tablist">
                          <li role="presentation" class="<%=tab1Class%>"><a href="#tab_content1" id="dlv-tab" role="tab" data-toggle="tab" aria-expanded="false">كشف حساب</a>
                          </li>
                          
                          <li role="presentation" class="<%=tab3Class%>"><a href="#tab_content3" role="tab" id="profile-tab" data-toggle="tab" aria-expanded="false">كل الشحنات الحاليه لمندوب الإستلام</a>
                          </li>
                        </ul>
                        <div id="myTabContent" class="tab-content">
                          <div role="tabpanel" class="tab-pane fade <%=tab1Class%>" id="tab_content1" aria-labelledby="dlv-tab">

                            <!-- start recent activity -->
                            <%
                            	String pageName1 = this.getClass().getPackage().getName()+"."+this.getClass().getSimpleName();
                            	PickUpAgentBalance pab = new PickUpAgentBalance(); 
                            	Render(pab  , out , request, response , Myglobals , objectState , pageName1);
                            	

                            	if (pab.getRecords() == 0){
                            		
                            		out.println("<h4>لا توجد مبالغ مستحقة الدفع حاليا</h4>");
                            	}
                            	
                            	PickUpAgentPayments pap = new PickUpAgentPayments(); 
                             	Render(pap  , out , request, response , Myglobals , objectState , pageName1);
                            %>
                            <!-- end recent activity -->

                          </div>
                           <div role="tabpanel" class="tab-pane fade <%=tab2Class%>" id="tab_content2" aria-labelledby="ret-tab">
 							

                          </div>
                          <div role="tabpanel" class="tab-pane fade <%=tab3Class%>" id="tab_content3" aria-labelledby="profile-tab">
 							<% 
 							PickupAgentAllShipmentsView paas = new PickupAgentAllShipmentsView(); 
                        	Render(paas  , out , request, response , Myglobals , objectState , pageName1);
                        	%>

                          </div>
                          
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
    
        <!-- /page content -->

<%} %>

<%@ include file="../Main/footer.jsp"%>
<script>
function checkBoxPmtClicked ( ){
	var selectedCases ="";
	var first = true;
	
	$('[id^=pmtcheck_]').each(function() {
		console.log($(this).parent());
		if ($(this).parent().hasClass("checked") == true){
			
		    var number = this.id.split('_').pop();
		    if (!first){
		    	selectedCases +=",";
		    }
		    selectedCases +=number;
		    first = false;
		}
	});
	
	$("#selected_casesto_pay").val('');
	$("#selected_casesto_pay").val($("#selected_casesto_pay").val()+(selectedCases));
	
}
</script>
