<%@ include file="../Main/Main.jsp"%>
<%@ page autoFlush="true" buffer="2048kb"%>
<%@ page import="com.app.incomeoutcome.customer_paymentBarCode,
	com.app.incomeoutcome.CustomerBanlanceInAdvanceBarCode, 
 com.app.incomeoutcome.CustomerPaymentsInAdvanceBarCode" %>
<%@ page import="com.app.util.Utilities,java.sql.PreparedStatement,java.sql.ResultSet,com.app.incomeoutcome.CustomerBalanceBarCode" %>
<input type='hidden'>
<%
 
 
Connection conn = null;
PreparedStatement pst = null;
ResultSet rs = null; 
Utilities ut = new Utilities();
String mycustomersearchbarcode = (String)request.getParameter("mycustomersearchbarcode");
String customerAcctBarCode = (String)request.getParameter("customerAcctBarCode");
//System.out.println("request==>"+request.getParameter("myClassBean"));
String tab1Class = "active in";
String tab2Class = " ";
String tab3Class = " ";
if (request.getParameter("myClassBean")!=null){
	if (request.getParameter("myClassBean").equalsIgnoreCase("com.app.incomeoutcome.CustomerBanlanceInAdvanceBarCode")){
		tab1Class = "";
		tab2Class = "  ";
		tab3Class = "active in ";
	}else if (request.getParameter("myClassBean").equalsIgnoreCase("com.app.incomeoutcome.ToBeReturnedShipments")){
		tab1Class = " ";
		tab2Class = " active in";
		tab3Class = "";
	}else if (request.getParameter("myClassBean").equalsIgnoreCase("com.app.incomeoutcome.customer_paymentBarCode")){
		tab1Class = "active in ";
		tab2Class = "  ";
		tab3Class = " ";
	}
}
if (customerAcctBarCode !=null){
	Myglobals.smartyGlobalsAssArr.put("customerAcctBarCode", (String)customerAcctBarCode);
	
}else if (Myglobals.smartyGlobalsAssArr.containsKey("customerAcctBarCode") && Myglobals.smartyGlobalsAssArr.get("customerAcctBarCode")!=null){
	customerAcctBarCode = (String)Myglobals.smartyGlobalsAssArr.get("customerAcctBarCode");
}
LinkedHashMap<String,String> customerList = new LinkedHashMap<String,String>();
LinkedHashMap<String,String> customerInfo = new LinkedHashMap<String,String>();
try{
	conn = mysql.getConn();
	customerList = ut.getListOfcustomers(conn, (String)Myglobals.smartyGlobalsAssArr.get("userstorecode"));
	if (customerAcctBarCode!=null){
		customerInfo= ut.getcustomerInfo(conn, customerAcctBarCode);
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
              
			 <form action="?mycustomersearchbarcode=1" method="post" name="search_cust_form" class="form-horizontal form-label-left" >
              
                <div class="col-md-6 col-sm-6 col-xs-10 col-md-offset-1 ">
                  <div class="input-group" style='display:flex'>
                 
                  <select class='select2_single form-control' id='customerAcctBarCode' style="width: 200px;" name ='customerAcctBarCode' >
                  	<option value='' ></option>
                    <%
                    	for (String custid : customerList.keySet()){
                        	if (customerAcctBarCode!=null && customerAcctBarCode.equalsIgnoreCase(custid)){
                    %>
                    		<option value='<%=custid%>' selected><%=customerList.get(custid)%></option>
                    		<%
                    			}else{
                    		%>
                    			<option value='<%=custid%>' ><%=customerList.get(custid)%></option>
                    		<%
                    			}
                    		                    		                    		                    	}
                    		%>
                    </select>
                    <span class="input-group-btn">
                      <button type='submit' class="btn btn-danger" style='margin-right:10px' type="button"> عرض التفاصيل الماليه صاحب المحل <i class="fa fa-search m-right-xs"></i>
                      </button>
                    </span>
                    
                  </div>
                </div>
              
               </form>
            </div>
      <%
      	if (customerAcctBarCode!=null && !customerAcctBarCode.trim().equalsIgnoreCase("")){
      %>
            <div class="clearfix"></div>

            <div class="row">
              <div class="col-md-12 col-sm-12 col-xs-12">
                <div class="x_panel">
                  <div class="x_title">
                    <h2><%=customerInfo.get("name")%> <small></small></h2>
                    
                    <div class="clearfix"></div>
                  </div>
                  <div class="x_content">
                    <div class="col-md-1 col-sm-1 col-xs-12 profile_right">
                      <h3></h3>

                      <ul class="list-unstyled user_data">
                        
                        <li>
                          <i class="fa fa-phone user-profile-icon"></i> <%=customerInfo.get("hp")%>
                        </li>
                      </ul>

                     
                      <br />
                    </div>
                    <div class="col-md-11 col-sm-11 col-xs-12">

                      <div class="" role="tabpanel" data-example-id="togglable-tabs">
                        <ul id="myTab" class="nav nav-tabs bar_tabs" role="tablist">
                          <li role="presentation" class="<%=tab1Class%>"><a href="#tab_content1" id="dlv-tab" role="tab" data-toggle="tab" aria-expanded="false">كشف حساب</a>
                          </li>
                          
                          <li role="presentation" class="<%=tab3Class%>"><a href="#tab_content3" role="tab" id="profile-tab" data-toggle="tab" aria-expanded="false">دفع مبالغ مقدما</a>
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
                            	CustomerBalanceBarCode cbl = new CustomerBalanceBarCode(); 
                             	Render(cbl  , out , request, response , Myglobals , objectState , pageName1);
                             	
                             	if (cbl.getRecords() == 0){
                             		out.println("<h4>لا توجد مبالغ مستحقة الدفع حاليا</h4>");
                             	}
                             	
                             	customer_paymentBarCode cp = new customer_paymentBarCode(); 
                              	Render(cp  , out , request, response , Myglobals , objectState , pageName1);
                            %>
                            <!-- end recent activity -->

                          </div>
                           <div role="tabpanel" class="tab-pane fade <%=tab2Class%>" id="tab_content2" aria-labelledby="ret-tab">
 							

                          </div>
                          <div role="tabpanel" class="tab-pane fade <%=tab3Class%>" id="tab_content3" aria-labelledby="profile-tab">
 							<div class="row">
								<div class="col-sm-1 col-sm-offset-1"><label>Barcode</label>
								</div>
								<div class="col-sm-6">
									<input type='text' style='color:#424242;background-color:#E9E5E5;' id ='barcode_checker_adv' />
								</div>
								
							</div>
 							
 							<% 
 							CustomerBanlanceInAdvanceBarCode cbia = new CustomerBanlanceInAdvanceBarCode(); 
                        	Render(cbia  , out , request, response , Myglobals , objectState , pageName1);
                        	
                        	CustomerPaymentsInAdvanceBarCode cpa = new CustomerPaymentsInAdvanceBarCode(); 
                           	Render(cpa  , out , request, response , Myglobals , objectState , pageName1);
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

function checkAdvancedBoxPmtClicked (){
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


$('#barcode_checker_adv').focus();
var inputadv = document.getElementById("barcode_checker_adv");

inputadv.addEventListener("keyup",async  function(event) {
  if (event.keyCode === 13) {
	  var barcodeScanned = ((inputadv.value).replace("/", "forwardslash")).replace(/ /g,"");
	 
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
		  
		  $("#AdvancedPmtCheck_"+caseid).parent().addClass("checked");
		 
	  }	 
	  inputadv.value = '';
	  $('#barcode_checker_adv').focus();
  }
});





$('#barcode_checker').focus();
var input = document.getElementById("barcode_checker");

input.addEventListener("keyup",async  function(event) {
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
