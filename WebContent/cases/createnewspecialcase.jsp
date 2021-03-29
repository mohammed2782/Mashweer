<%@ include file="../Main/Main.jsp"%>
<%@ page import="com.app.cases.NewSpecialCases" %> 
<div class="row" style="margin-right:0px;">
<div id="map" class="col-md-10" style="width:1100px;height:50px; margin-right:10px;"></div>

<div class="col-md-12" id="123">
<%
 	String pageName1 = this.getClass().getPackage().getName()+"."+this.getClass().getSimpleName();
	NewSpecialCases npc = new NewSpecialCases(); 
 	Render(npc  , out , request, response , Myglobals , objectState , pageName1); 
 	
%>
</div>
</div>


<script charset="utf-8">
smarty_submitButton_allow_disable = false;
var RCVno = 1;
var rowNum = 1;
var RCVtable = document.getElementById("rcv_dtls");
$('#add_rcv_dtls').click(function(){
	RCVno++;
	rowNum++;
	dataToSend = {};
	//var row  =RCVtable.insertRow(rowNum);
	$.get('../TLKMultiRowsSpecialCases?loadRcvRow='+RCVno , dataToSend,function(data, status){ 
		if (status=='success'){
			$('#rcv_dtls tr:last').after(data);
			 $(".select2_single").select2({
				    placeholder: "Type to Search",
				    allowClear: true
				  });
				  
			init_InputMask();
		}
		
	});
	
	
	//$('#smarty_new_row_seq').val(RCVno);
    	
});
$("#com_dot_app_dot_cases_dot_NewSpecialCases").submit(function(){
	
	var selectVal= $("#span_es_c_cust_name .es-list li.selected").attr("value");
	if (selectVal === undefined)
		selectVal = $("#span_es_c_cust_name #editable_c_cust_name").val();
	
	$("#c_cust_name").val(selectVal);
});

function remove_row( rownum){
	$('table#rcv_dtls tr#smartyNewRow_'+rownum).remove();
}


function loadDistrict(seq){
	var destCity= $("#rcv_city_smartyNewRow_"+seq).val();
	if (destCity !== null && destCity=='BAS'){
		$("#rcv_district_smartyNewRow_"+seq).attr("required", "");
		
	}else{
		$("#rcv_district_smartyNewRow_"+seq).removeAttr("required");
		
	}
}
function calcShipmentCost(seq){
	
	var custName = $('#editable_c_cust_name').val();
	var destCity= $("#rcv_city_smartyNewRow_"+seq).val();
	var originState = $("#c_pickup_state").val();;
	//var weight = $("#c_weight_smartyNewRow_"+seq).val();
	var rural = "N";
	if ($('#c_rural_smartyNewRow_'+seq).is(":checked")){
		rural = "Y";
	}
	var dataToSend = {"destState":destCity, "originState" :originState, "rural":rural, "custName":custName};
	
		$.post('../TLK_CalculateShipmentChargesSRVLT' , dataToSend, function(data, status){ 
				//alert("Data: " + data +", Status:" + status);
				if (status=='success'){ 
					$("#c_shipment_cost_smartyNewRow_"+seq).val(data);
				}else{
					alert("Error, please contact Softecha");
				}
		 });
	
}

$("#com_dot_app_dot_cases_dot_NewSpecialCases").submit(function(event){
    var isValid = true;
	var errorMsg = '';
    // do all your validation if need here
    var phoneNo = '';
    var otherPhoneNo ='';
    outerloop: for (i = 1; i<=RCVno; i++){
    	phoneNo = $("#rcv_phone_smartyNewRow_"+i).val();
    	//console.log('phoneNo-------'+phoneNo+', where i is '+i);
    	if (phoneNo === undefined)
    		continue;
    	for (j=i+1 ; j<=RCVno ; j++){
    		otherPhoneNo = $("#rcv_phone_smartyNewRow_"+j).val();
    		//console.log('otherPhoneNo-------'+otherPhoneNo+', where j is '+j);
    		if (otherPhoneNo === undefined)
    			continue;
    			
    		if (phoneNo === otherPhoneNo){
    			//console.log('otherPhoneNo-------'+otherPhoneNo+', is equal to '+phoneNo);
    			isValid = false;
    			errorMsg = 'هنالك تشابه بأرقام الهواتف';
    			$("#rcv_phone_smartyNewRow_"+j).css( 'background-color','#f5bed8');
    			$("#rcv_phone_smartyNewRow_"+i).css( 'background-color','#f5bed8');
    			break outerloop;
    		}else{
    			
    			$("#rcv_phone_smartyNewRow_"+i).css( 'background-color','#FFFFB8');
    			$("#rcv_phone_smartyNewRow_"+j).css( 'background-color','#FFFFB8');
    		}
    	}
    }
    //rcv_phone_smartyNewRow_1
	//alert(RCVno);
    if (!isValid) {
        event.preventDefault();
        alert(errorMsg);
    }
});

function formatMe(xrcp){
	/* old code
	var z = x.value;
	var afterReplace = z.replace(/,/g,'');
    var n = parseInt(afterReplace); 
   
    x.value = (n.toLocaleString('en-UK'));
   // alert(x.value);
    if (x.value=='NaN')
    	x.value = 0;
    */
	var z = xrcp.value.replace(/,/g,'');
	 var parts = z.toString().split(".");
	    parts[0] = parts[0].replace(/\B(?=(\d{3})+(?!\d))/g, ",");
	    xrcp.value = parts.join(".");
   if (xrcp.value=='NaN')
   	xrcp.value = 0;
}

$('#editable_c_cust_name').focus();


</script>

<%@ include file="../Main/footer.jsp"%>
