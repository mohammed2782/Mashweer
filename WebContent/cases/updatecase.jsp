<%@ include file="../Main/Main.jsp"%>
<%@ page import="com.app.cases.Updatecase" %> 


<%
 	String pageName1 = this.getClass().getPackage().getName()+"."+this.getClass().getSimpleName();
    Updatecase uc = new Updatecase(); 
 	Render(uc  , out , request, response , Myglobals , objectState , pageName1); 
 	
%>
<%@ include file="../Main/footer.jsp"%>



<script>

$("#c_rcv_state").on("change", calcShipmentCost);
$("#c_rural").on("change",calcShipmentCost);
$("#c_custid").on("change",calcShipmentCost);
$("#c_company_sender").on("change",calcShipmentCost);
function calcShipmentCost(){
	var custid = $("#c_custid").val();
	var destCity= $("#c_rcv_state").val();
	var rural = $("#c_rural").val();
	var specialCase = $("#c_specialcase").val();
	var senderCompany = $("#c_company_sender").val();
	var agentShareSettled =$("#smarty_showonly_c_agentsharesettled").val();
	//alert(agentShareSettled);
	
	var dataToSend = {"destState":destCity, "rural":rural, "custid":custid, "senderCompany":senderCompany};
	if (specialCase !==undefined && specialCase!= null && specialCase !="Y"  ){
		$.post('../TLK_CalculateShipmentChargesSRVLT' , dataToSend, function(data, status){ 
				//alert("Data: " + data +", Status:" + status);
				if (status=='success'){ 
					if(agentShareSettled!='FULL')
						$("#c_shipment_cost").val(data);
				}else{
					alert("Error, please contact Softecha");
				}
		 });
	}
}
$('#c_custreceiptnoori').focus();
</script>