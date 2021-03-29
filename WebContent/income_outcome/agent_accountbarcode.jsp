<%@ include file="../Main/Main.jsp"%>
<%@ page import="com.app.incomeoutcome.AgentsPaymentsBarCode,com.app.incomeoutcome.AllAgentsShipmentsViewBarCode" %> 
<%@ page import="com.app.util.Utilities,java.sql.PreparedStatement,java.sql.ResultSet,com.app.incomeoutcome.AgentsBalanceBarCode" %>
<%
Connection conn = null;
PreparedStatement pst = null;
ResultSet rs = null; 
Utilities ut = new Utilities();
String searchAgent = (String)request.getParameter("myagentsearch");
String agentAcctBarCode = (String)request.getParameter("agentAcctBarCode");
if (agentAcctBarCode !=null){
	Myglobals.smartyGlobalsAssArr.put("agentAcctBarCode", (String)agentAcctBarCode);
	
}else if (Myglobals.smartyGlobalsAssArr.containsKey("agentAcctBarCode") && Myglobals.smartyGlobalsAssArr.get("agentAcctBarCode")!=null){
	agentAcctBarCode = (String)Myglobals.smartyGlobalsAssArr.get("agentAcctBarCode");
}
LinkedHashMap<String,String> agentsList = new LinkedHashMap<String,String>();
LinkedHashMap<String,String> agentInfo = new LinkedHashMap<String,String>();
try{
	conn = mysql.getConn();
	agentsList = ut.getListOfAgents(conn, (String)Myglobals.smartyGlobalsAssArr.get("userstorecode"));
	if (agentAcctBarCode!=null){
		agentInfo= ut.getAgentInfo(conn, agentAcctBarCode); 
	}
}catch(Exception e){
	e.printStackTrace();
}finally{
	try{rs.close();}catch(Exception e){}
	try{pst.close();}catch(Exception e){}
	try{conn.close();}catch(Exception e){}
}
AgentsBalanceBarCode agl = new AgentsBalanceBarCode(); 
%>
<!-- page content -->
        
          <div class="row purple_div">
            <div class="page-title">
              
			 <form action="?myagentsearch=1" method="post" name="search_cust_form" class="form-horizontal form-label-left" >
             
                <div class="col-md-6 col-sm-6 col-xs-12 col-md-offset-4 ">
                  <div class="input-group" style='display:flex'>
                 
                  <select class='select2_single form-control' id='agentAcctBarCode' style="width: 200px;"  name ='agentAcctBarCode' >
                  	<option value='' ></option>
                    <%
                    	for (String agentId : agentsList.keySet()){
                    		if (agentAcctBarCode!=null && agentAcctBarCode.equalsIgnoreCase(agentId)){%>
                    		
                    			<option value='<%=agentId%>' selected><%=agentsList.get(agentId) %></option>
                    		<%}else{%>
                    			<option value='<%=agentId%>' ><%=agentsList.get(agentId) %></option>
                    		<% 
                    		}
                    	}
                    %>
                    </select>
                    <span class="input-group-btn">
                      <button type='submit' class="btn btn-dark" style='margin-right:10px' type="button">عرض التفاصيل الماليه لمندوب التوصيل<i class="fa fa-search m-right-xs"></i>
                      </button>
                    </span>
                    
                  </div>
                </div>
              
               </form>
            </div>
      <%if (agentAcctBarCode!=null && !agentAcctBarCode.trim().equalsIgnoreCase("")){%>
            <div class="clearfix"></div>

            <div class="row">
              <div class="col-md-12 col-sm-12 col-xs-12">
                <div class="x_panel">
                  <div class="x_title">
                    <h2><%=agentInfo.get("name") %> <small></small></h2>
                    
                    <div class="clearfix"></div>
                  </div>
                  <div class="x_content">
                    
                    <div class="col-md-10 col-sm-10 col-xs-12">

                      <div class="" role="tabpanel" data-example-id="togglable-tabs">
                        <ul id="myTab" class="nav nav-tabs bar_tabs bar_tabs_purple" role="tablist">
                          <li role="presentation" class="active"><a href="#tab_content1" id="home-tab" role="tab" data-toggle="tab" aria-expanded="true">كشف حساب</a>
                          </li>
                          <li role="presentation" class=""><a href="#tab_content2" role="tab" id="profile-tab" data-toggle="tab" aria-expanded="false">كل الشحنات الحاليه</a>
                          </li>
                        </ul>
                        <div id="myTabContent" class="tab-content">
                          <div role="tabpanel" class="tab-pane fade active in" id="tab_content1" aria-labelledby="home-tab">
							<div class="row">
								<div class="col-sm-1 col-sm-offset-1"><label>Barcode</label>
								</div>
								<div class="col-sm-6">
									<input type='text' style='color:#424242;background-color:#E9E5E5;' id ='barcode_checker' />
								</div>
							
							</div>
                            <!-- start recent activity -->
                            <%
                            String pageName1 = this.getClass().getPackage().getName()+"."+this.getClass().getSimpleName();
                           
                        	Render(agl  , out , request, response , Myglobals , objectState , pageName1);
                        	
                        	if (agl.getRecords() == 0){
                        		
                        		out.println("<h4>لا توجد مبالغ مستحقة الدفع حاليا</h4>");
                        	}
                        	AgentsPaymentsBarCode ap = new AgentsPaymentsBarCode(); 
                         	Render(ap  , out , request, response , Myglobals , objectState , pageName1); 
                            %>
                            <!-- end recent activity -->

                          </div>
                          <div role="tabpanel" class="tab-pane fade" id="tab_content2" aria-labelledby="profile-tab">
 							<% 
 							AllAgentsShipmentsViewBarCode alagsv = new AllAgentsShipmentsViewBarCode(); 
                        	Render(alagsv  , out , request, response , Myglobals , objectState , pageName1);
                        	%>

                          </div>
                          
                        </div>
                      </div>
                    </div>
                    <div class="col-md-2 col-sm-2 col-xs-12 profile_left" style="box-shadow:-1px 0 8px -4px rgba(101, 16, 1, 0.8); margin-top:20px;">
                      <h3></h3>

                      <ul class="list-unstyled user_data">
                        
                        <li>
                          <i class="fa fa-phone user-profile-icon"></i> 
                        </li>

                        <li class="m-top-xs">
                          <i class="fa fa-calendar user-profile-icon"></i> <%=agentInfo.get("joineddate") %>
                        </li>
                      </ul>

                     
                      <br />

                      <!-- start skills -->
                      
                      <ul class="list-unstyled user_data">
                        <li>
                          <div class="col-md-12"><div class="col-md-9"><p>شحنات سلمت بنجاح</p></div><div class="col-md-3"><p ><%=agl.getDlvItmes()%></p></div></div>
                          <div class="progress progress_sm">
                            <div class="progress-bar bg-blue" role="progressbar"  aria-valuenow='<%=agl.getDlvItmes()%>' aria-valuemax='<%=agl.getRecords()%>' aria-valuemin='0' data-transitiongoal="<%=agl.getDlvItmes()%>"></div>
                          
                          </div>
                         </li>
                         <li>
                          <div class="col-md-12"><div class="col-md-9"><p>شحنات راجعه</p></div><div class="col-md-3"><p ><%=agl.getNoOfRtnitems()%></p></div></div>
                          <div class="progress progress_sm">
                            <div class="progress-bar bg-red" role="progressbar"  aria-valuenow='<%=agl.getNoOfRtnitems()%>' aria-valuemax='<%=agl.getRecords()%>' aria-valuemin='0' data-transitiongoal="<%=agl.getNoOfRtnitems()%>"></div>
                          
                          </div>
                         </li>
                          <li>
                          <div class="col-md-12"><div class="col-md-9"><p>راجع مع دفع النقل من المستلم</p></div><div class="col-md-3"><p ><%=agl.getRtnItemsPaidByCustomer()%></p></div></div>
                          <div class="progress progress_sm">
                            <div class="progress-bar bg-red" role="progressbar"  aria-valuenow='<%=agl.getRtnItemsPaidByCustomer()%>' aria-valuemax='<%=agl.getRecords()%>' aria-valuemin='0' data-transitiongoal="<%=agl.getRtnItemsPaidByCustomer()%>"></div>
                          
                          </div>
                         </li>
                         
                          <li>
                          <div class="col-md-12"><div class="col-md-9"><p>راجع مع دفع النقل من صاحب المحل</p></div><div class="col-md-3"><p ><%=agl.getRtnItemsPaidBySender()%></p></div></div>
                          <div class="progress progress_sm">
                            <div class="progress-bar bg-red" role="progressbar"  aria-valuenow='<%=agl.getRtnItemsPaidBySender()%>' aria-valuemax='<%=agl.getRecords()%>' aria-valuemin='0' data-transitiongoal="<%=agl.getRtnItemsPaidBySender()%>"></div>
                          
                          </div>
                         </li>
                         
                          <li>
                          <div class="col-md-12"><div class="col-md-9"><p>شحنات أطراف</p></div><div class="col-md-3"><p ><%=agl.getRuralAreaItems()%></p></div></div>
                          <div class="progress progress_sm">
                            <div class="progress-bar bg-purple" role="progressbar"  aria-valuenow='<%=agl.getRuralAreaItems()%>' aria-valuemax='<%=agl.getRecords()%>' aria-valuemin='0' data-transitiongoal="<%=agl.getRuralAreaItems()%>"></div>
                          
                          </div>
                         </li>
                      </ul>
                      <!-- end of skills -->

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
function checkBoxPmtClicked (){
	var selectedCases ="";
	var first = true;
	//alert("here");
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

function checkAdvancedBoxPmtClicked ( ){
	var selectedCases ="";
	var first = true;
	//alert("here");
	$('[id^=AdvancedPmtCheck_]').each(function() {
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
	
	$("#selected_casesto_pay_adv_pmt").val(''); 
	$("#selected_casesto_pay_adv_pmt").val($("#selected_casesto_pay_adv_pmt").val()+(selectedCases));	
}



$('#barcode_checker').focus();
var input = document.getElementById("barcode_checker");

input.addEventListener("keyup", function(event) {
	console.log("hi");
  if (event.keyCode === 13) {
	  var barcodeScanned = ((input.value).replace("/", "forwardslash")).replace(/ /g,"");
	 
	  if (barcodeScanned !== null && barcodeScanned !== undefined){
		  var reciept = document.getElementById(barcodeScanned);
		  $(reciept).parent().css({ 
	            "background-color": "#fbd1b0", 
	            "border": "2px solid #dc2b2b",
	            
	   		});
		  var caseid = $(reciept).attr("caseid");
		  $('html, body').stop().animate({
	            scrollTop: $("#"+barcodeScanned).offset().top
	        }, 500);
		  
		  $("#pmtcheck_"+caseid).parent().addClass("checked");
		 
	  }	 
	  input.value = '';
	  $('#barcode_checker').focus();
  } 
});
 </script>