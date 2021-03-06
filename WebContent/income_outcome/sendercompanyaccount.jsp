<%@ include file="../Main/Main.jsp"%>
<%@ page import="com.app.incomeoutcome.SenderCompanyPaymentsBarCode" %> 
<%@ page import="com.app.util.Utilities,java.sql.PreparedStatement,java.sql.ResultSet,com.app.incomeoutcome.SenderCompanyBalanceBarCode,
com.app.incomeoutcome.ToBeReturnedShipments" %>
<%
	Connection conn = null;
PreparedStatement pst = null;
ResultSet rs = null; 
Utilities ut = new Utilities();

String senderCompanyAcctBarCode = (String)request.getParameter("senderCompanyAcctBarCode");
//System.out.println("request==>"+request.getParameter("myClassBean"));
String tab1Class = "active in";
String tab2Class = " ";
String tab3Class = " ";
if (request.getParameter("myClassBean")!=null){
	if (request.getParameter("myClassBean").equalsIgnoreCase("com.app.incomeoutcome.PickupAgentAllShipmentsViewBarCode")){
		tab1Class = "";
		tab2Class = "  ";
		tab3Class = "active in ";
	}else if (request.getParameter("myClassBean").equalsIgnoreCase("com.app.incomeoutcome.ToBeReturnedShipments")){
		tab1Class = " ";
		tab2Class = " active in";
		tab3Class = "";
	}else if (request.getParameter("myClassBean").equalsIgnoreCase("com.app.incomeoutcome.SenderCompanyBalanceBarCode")){
		tab1Class = "active in ";
		tab2Class = "  ";
		tab3Class = " ";
	}
}
if (senderCompanyAcctBarCode !=null){
	Myglobals.smartyGlobalsAssArr.put("senderCompanyAcctBarCode", (String)senderCompanyAcctBarCode);
	
}else if (Myglobals.smartyGlobalsAssArr.containsKey("senderCompanyAcctBarCode") && Myglobals.smartyGlobalsAssArr.get("senderCompanyAcctBarCode")!=null){
	senderCompanyAcctBarCode = (String)Myglobals.smartyGlobalsAssArr.get("senderCompanyAcctBarCode");
}
LinkedHashMap<String,String> senderCompanyList = new LinkedHashMap<String,String>();
LinkedHashMap<String,String> senderCompanyInfo = new LinkedHashMap<String,String>();
try{
	conn = mysql.getConn();
	senderCompanyList = ut.getListOfCompanies(conn);
	if (senderCompanyAcctBarCode!=null){
		senderCompanyInfo= ut.getSenderCompanyInfo(conn, senderCompanyAcctBarCode); 
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
                 
                  <select class='select2_single form-control' id='senderCompanyAcctBarCode' style="width: 200px;" name ='senderCompanyAcctBarCode' >
                  	<option value='' ></option>
                    <%
                    	for (String compid : senderCompanyList.keySet()){
                        	if (senderCompanyAcctBarCode!=null && senderCompanyAcctBarCode.equalsIgnoreCase(compid)){
                    %>
                    		<option value='<%=compid%>' selected><%=senderCompanyList.get(compid)%></option>
                    		<%
                    			}else{
                    		%>
                    			<option value='<%=compid%>' ><%=senderCompanyList.get(compid)%></option>
                    		<%
                    			}
                    		                    		                    		                    	}
                    		%>
                    </select>
                    <span class="input-group-btn">
                      <button type='submit' class=" btn btn-success" style='margin-right:10px;background-color:#000048;' type="button">?????? ???????????????? ?????????????? ?????????????? ?????????????? <i class="fa fa-search m-right-xs"></i>
                      </button>
                    </span>
                    
                  </div>
                </div>
              
               </form>
            </div>
      <%
      	if (senderCompanyAcctBarCode!=null && !senderCompanyAcctBarCode.trim().equalsIgnoreCase("")){
      %>
            <div class="clearfix"></div>

            <div class="row">
              <div class="col-md-12 col-sm-12 col-xs-12">
                <div class="x_panel">
                  <div class="x_title">
                    <h2><%=senderCompanyInfo.get("name")%> <small></small></h2>
                    
                    <div class="clearfix"></div>
                  </div>
                  <div class="x_content">
                    <div class="col-md-1 col-sm-1 col-xs-12 profile_right">
                      <h3></h3>

                      <ul class="list-unstyled user_data">
                       
                      </ul>

                     
                      <br />
                    </div>
                    <div class="col-md-11 col-sm-11 col-xs-12">

                      <div class="" role="tabpanel" data-example-id="togglable-tabs">
                        <ul id="myTab" class="nav nav-tabs bar_tabs bar_tabs_blue" role="tablist">
                          <li role="presentation" class="<%=tab1Class%>"><a href="#tab_content1" id="dlv-tab" role="tab" data-toggle="tab" aria-expanded="false">?????? ????????</a>
                          </li>
                          
                         
                        </ul>
                        <div id="myTabContent" class="tab-content">
                          <div role="tabpanel" class="tab-pane fade <%=tab1Class%>" id="tab_content1" aria-labelledby="dlv-tab">
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
                            	SenderCompanyBalanceBarCode pab = new SenderCompanyBalanceBarCode(); 
                            	Render(pab  , out , request, response , Myglobals , objectState , pageName1);
                            	

                            	if (pab.getRecords() == 0){
                            		
                            		out.println("<h4>???? ???????? ?????????? ???????????? ?????????? ??????????</h4>");
                            	}
                            	
                            	SenderCompanyPaymentsBarCode pap = new SenderCompanyPaymentsBarCode(); 
                             	Render(pap  , out , request, response , Myglobals , objectState , pageName1);
                            %>
                            <!-- end recent activity -->

                          </div>
                           <div role="tabpanel" class="tab-pane fade <%=tab2Class%>" id="tab_content2" aria-labelledby="ret-tab">
 							

                          </div>
                          <div role="tabpanel" class="tab-pane fade <%=tab3Class%>" id="tab_content3" aria-labelledby="profile-tab">
 							<% 
 							
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

input.addEventListener("keyup",  function(event) {
  if (event.keyCode === 13) {
	  var barcodeScanned = ((input.value).replace("/", "forwardslash")).replace(/ /g,"");
	 
	  if (barcodeScanned !== null && barcodeScanned !== undefined){
		  var reciept = document.getElementById(barcodeScanned);
		  $(reciept).parent().css({ 
	            "background-color": "rgb(175, 157, 52)", 
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
