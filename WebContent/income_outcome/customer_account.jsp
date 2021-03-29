<%@ include file="../Main/Main.jsp"%>
<%@ page autoFlush="true" buffer="2048kb"%>
<%@ page import="com.app.incomeoutcome.customer_payment,com.app.incomeoutcome.CustomerBanlanceInAdvance,com.app.incomeoutcome.CustomerPaymentsInAdvance" %> 
<%@ page import="com.app.util.Utilities,java.sql.PreparedStatement,java.sql.ResultSet,com.app.incomeoutcome.CustomerBalance" %>
<%
	Connection conn = null;
PreparedStatement pst = null;
ResultSet rs = null; 
Utilities ut = new Utilities();
String searchCustomer = (String)request.getParameter("mycustomersearch");
String customerAcct = (String)request.getParameter("customerAcct");
//System.out.println("request==>"+request.getParameter("myClassBean"));
String tab1Class = "active in";
String tab2Class = " ";
String tab3Class = " ";
if (request.getParameter("myClassBean")!=null){
	if (request.getParameter("myClassBean").equalsIgnoreCase("com.app.incomeoutcome.CustomerBanlanceInAdvance")){
		tab1Class = "";
		tab2Class = "  ";
		tab3Class = "active in ";
	}else if (request.getParameter("myClassBean").equalsIgnoreCase("com.app.incomeoutcome.ToBeReturnedShipments")){
		tab1Class = " ";
		tab2Class = " active in";
		tab3Class = "";
	}else if (request.getParameter("myClassBean").equalsIgnoreCase("com.app.incomeoutcome.customer_payment")){
		tab1Class = "active in ";
		tab2Class = "  ";
		tab3Class = " ";
	}
}
if (customerAcct !=null){
	Myglobals.smartyGlobalsAssArr.put("customerAcct", (String)customerAcct);
	
}else if (Myglobals.smartyGlobalsAssArr.containsKey("customerAcct") && Myglobals.smartyGlobalsAssArr.get("customerAcct")!=null){
	customerAcct = (String)Myglobals.smartyGlobalsAssArr.get("customerAcct");
}
LinkedHashMap<String,String> customerList = new LinkedHashMap<String,String>();
LinkedHashMap<String,String> customerInfo = new LinkedHashMap<String,String>();
try{
	conn = mysql.getConn();
	customerList = ut.getListOfcustomers(conn, (String)Myglobals.smartyGlobalsAssArr.get("userstorecode"));
	if (customerAcct!=null){
		customerInfo= ut.getcustomerInfo(conn, customerAcct);
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
              
			 <form action="?mycustomersearch=1" method="post" name="search_cust_form" class="form-horizontal form-label-left" >
              
                <div class="col-md-6 col-sm-6 col-xs-10 col-md-offset-1 ">
                  <div class="input-group" style='display:flex'>
                 
                  <select class='select2_single form-control' id='customerAcct' style="width: 200px;" name ='customerAcct' >
                  	<option value='' ></option>
                    <%
                    	for (String custid : customerList.keySet()){
                        	if (customerAcct!=null && customerAcct.equalsIgnoreCase(custid)){
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
                      <button type='submit' class="btn btn-danger" style='margin-right:10px;background-color:#ba8134;' type="button"> عرض التفاصيل الماليه صاحب المحل <i class="fa fa-search m-right-xs"></i>
                      </button>
                    </span>
                    
                  </div>
                </div>
              
               </form>
            </div>
      <%
      	if (customerAcct!=null && !customerAcct.trim().equalsIgnoreCase("")){
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

                            <!-- start recent activity -->
                            <%
                            	String pageName1 = this.getClass().getPackage().getName()+"."+this.getClass().getSimpleName();
                                CustomerBalance cbl = new CustomerBalance(); 
                             	Render(cbl  , out , request, response , Myglobals , objectState , pageName1);
                             	
                             	if (cbl.getRecords() == 0){
                             		out.println("<h4>لا توجد مبالغ مستحقة الدفع حاليا</h4>");
                             	}
                             	
                                customer_payment cp = new customer_payment(); 
                              	Render(cp  , out , request, response , Myglobals , objectState , pageName1);
                            %>
                            <!-- end recent activity -->

                          </div>
                           <div role="tabpanel" class="tab-pane fade <%=tab2Class%>" id="tab_content2" aria-labelledby="ret-tab">
 							

                          </div>
                          <div role="tabpanel" class="tab-pane fade <%=tab3Class%>" id="tab_content3" aria-labelledby="profile-tab">
 							<% 
 							CustomerBanlanceInAdvance cbia = new CustomerBanlanceInAdvance(); 
                        	Render(cbia  , out , request, response , Myglobals , objectState , pageName1);
                        	
                        	CustomerPaymentsInAdvance cpa = new CustomerPaymentsInAdvance(); 
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

</script>
