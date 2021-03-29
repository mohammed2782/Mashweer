<%@ include file="../Main/Main.jsp"%>
<%@ page import="java.sql.PreparedStatement, java.sql.ResultSet,
 com.app.bussframework.SingleQueueFactory, com.app.bussframework.SingleQueue " %>
<% 

	String stg_code = (String)request.getParameter("stg_code");
	String stp_code = (String)request.getParameter("stp_code");
	String c_id = null;
	if ((String)request.getParameter("c_id")!=null && !((String)request.getParameter("stp_code")).trim().isEmpty())
		c_id = (String)request.getParameter("c_id");
	
	String queueName = "";
	if (stg_code !=null){
		Myglobals.smartyGlobalsAssArr.put("stg_code", (String)stg_code);
		Myglobals.smartyGlobalsAssArr.put("stp_code", (String)stp_code);
		if(c_id !=null)
			Myglobals.smartyGlobalsAssArr.put("c_id", (String)c_id);
		else
			Myglobals.smartyGlobalsAssArr.remove("c_id");
			
	}else if (Myglobals.smartyGlobalsAssArr.containsKey("stg_code") && Myglobals.smartyGlobalsAssArr.get("stg_code")!=null){
		stg_code = (String)Myglobals.smartyGlobalsAssArr.get("stg_code");
		stp_code = (String)Myglobals.smartyGlobalsAssArr.get("stp_code");
	}

	
	Connection conn = null;
	PreparedStatement pst = null;
	ResultSet rs = null;
	try{
		conn = mysql.getConn();
		pst = conn.prepareStatement("select stg_name , stp_name from kbstage join kbstep on (stg_code=stp_stgcode)"+
				 " where stg_code=? and stp_code =?");
		pst.setString(1, stg_code);
		pst.setString(2, stp_code);
		rs = pst.executeQuery();
		if (rs.next())
			queueName = rs.getString("stg_name")+" - "+rs.getString("stp_name");
		
		}catch(Exception e){
		e.printStackTrace();
	}finally{
		try{rs.close();}catch(Exception e){}
		try{pst.close();}catch(Exception e){}
		try{conn.close();}catch(Exception e){}
	}


if (stp_code.equalsIgnoreCase("with_agent")){
	%>
	<style>
		.jambo_table>tbody>tr>:nth-child(2){
			 font-weight :600;
			 font-size :12.5px;
			 border : 2px solid #c7140675;
			}
			
			.jambo_table>tbody>tr>:nth-child(4){
			 font-weight :600;
			 font-size :12.5px;
			 border : 2px solid #c7140675;
			}
	</style>

	<%
}
%>

<div class="row">
<div class="col-md-12">
	<a href='./home.jsp' class="btn btn-danger">رجوع</a>
	<br></br>
          <div class="panel panel-warning" style="margin-bottom:0px;">
            <div class="panel-heading">
            	<div class='row'>
	            	<div class='col-xs-12' style='text-align:center'>
	              		<h4 style='color:red'><%=queueName%></h4>
	            	</div>
	            </div>
            </div>
        </div>
	</div>
</div>
<%
	SingleQueueFactory sqf = new SingleQueueFactory();
	String pageName1 = this.getClass().getPackage().getName()+"."+this.getClass().getSimpleName();
	SingleQueue sq = sqf.getSingleQueuObj(stg_code, stp_code); 
	sq.setUserDefinedCaption(queueName);
	Render(sq  , out , request, response , Myglobals , objectState , pageName1);
%> 
<script>
   /* window.onunload = refreshParent;
    function refreshParent() {
        window.opener.location.reload();
    }
    */
</script>
<jsp:include page="../Main/footer.jsp" />
<script>
var returnedAll = 'N';
var deliverAll = 'N';
var archiveAllRtn = 'N';
var returnedAllToRc = 'N';

function changeToArchiveAll(action){
	
	$('[id^=q_action_smartyrow_]').each(function() {
	    var number = this.id.split('_').pop();
	    if (archiveAllRtn =='N'){
	    	$('#q_action_smartyrow_'+number).val('ARCHV');
	    	
	    }else{
	    	$('#q_action_smartyrow_'+number).val('');
	    	
	    }
	});
	if (archiveAllRtn =='N'){
		archiveAllRtn = 'Y';
	}else{
		archiveAllRtn = 'N';
	}
}

function changeToRecievedAll(action){
	
	$('[id^=q_action_smartyrow_]').each(function() {
	    var number = this.id.split('_').pop();
	    if (deliverAll =='N'){
	    	$('#q_action_smartyrow_'+number).val('SUCS_DLV');
	    	 $("#q_action_smartyrow_"+number).css('backgroundColor','#2f2fd2');
	    	 $("#q_action_smartyrow_"+number).css('color','white');
	    }else{
	    	$('#q_action_smartyrow_'+number).val('');
	    	 $("#q_action_smartyrow_"+number).css('backgroundColor','');
	    	 $("#q_action_smartyrow_"+number).css('color','');
	    }
	});
	if (deliverAll =='N'){
		deliverAll = 'Y';
	}else{
		deliverAll = 'N';
	}
}

function changeActionReturnedAll(action){
	$('#allretorcag').prop('checked', false);
	returnedAllToRc = 'N';
	$('[id^=q_action_smartyrow_]').each(function() {
	    var number = this.id.split('_').pop();
	    if (returnedAll =='N'){
	    	$('#q_action_smartyrow_'+number).val('RETURNED_TO_SNDR');
	    }else{
	    	$('#q_action_smartyrow_'+number).val('');
	    }
	});
	if (returnedAll =='N'){
		returnedAll = 'Y';
	}else{
		returnedAll = 'N';
	}
}
function changeActionReturnedAllToRcAg(action){
	$('#allreturned').prop('checked', false);
	returnedAll = 'N';
	$('[id^=q_action_smartyrow_]').each(function() {
	    var number = this.id.split('_').pop();
	    if (returnedAllToRc =='N'){
	    	$('#q_action_smartyrow_'+number).val('RTN_TORCVAGENT_TORTN');
	    }else{
	    	$('#q_action_smartyrow_'+number).val('');
	    }
	});
	if (returnedAllToRc =='N'){
		returnedAllToRc = 'Y';
	}else{
		returnedAllToRc = 'N';
	}
}


function changeActionReturnedAllFromRcvAgent(){
	
	$('[id^=q_action_smartyrow_]').each(function() {
	    var number = this.id.split('_').pop();
	    if (returnedAll =='N'){
	    	$('#q_action_smartyrow_'+number).val('RETURNED_TO_SNDR');
	    }else{
	    	$('#q_action_smartyrow_'+number).val('');
	    }
	});
	if (returnedAll =='N'){
		returnedAll = 'Y';
	}else{
		returnedAll = 'N';
	}
}

$('#c_custreceiptnoori').focus();


function change_q_actionColor(that, seq){
	 var value = $("#q_action_smartyrow_"+seq).val();
	 if (value == 'SUCS_DLV' || value == 'SUCS_DLV_CHANGEAMT'){
		 $("#q_action_smartyrow_"+seq).css('backgroundColor','#2f2fd2');
		 $("#q_action_smartyrow_"+seq).css('color','white');
		 $("#returnreasons_"+seq).css('display','none');
		 $("#new_receiptamtrtn_smartyrow_"+seq).css('display','none');
		 if (value == 'SUCS_DLV_CHANGEAMT'){ // if successfully delviered and receipt amount have to change then show field
			 $("#new_receiptamt_smartyrow_"+seq).css('display','block');
			 $("#q_action_smartyrow_"+seq).css('backgroundColor',' rgb(170 173 16)');
		 }else{
			 $("#new_receiptamt_smartyrow_"+seq).css('display','none');
		 }
	 }else if (value == 'RTN_WITHSHP_CHARGE_SNDR' || value == 'RTN_INSTORE'){
		 $("#q_action_smartyrow_"+seq).css('backgroundColor','#f57b7b');
		 $("#q_action_smartyrow_"+seq).css('color','white');
		 $("#new_receiptamt_smartyrow_"+seq).css('display','none');
		 $("#new_receiptamtrtn_smartyrow_"+seq).css('display','none');
		 $("#returnreasons_"+seq).css('display','block');
		 $("#returnreasons_"+seq+" td select").css('backgroundColor','rgb(247 249 168)');
	 }else if(value == 'RTN_WITHSHIPMENT_CHRG'){
		 $("#q_action_smartyrow_"+seq).css('backgroundColor','#f57b7b');
		 $("#q_action_smartyrow_"+seq).css('color','white');
		 $("#new_receiptamt_smartyrow_"+seq).css('display','none');
		 $("#returnreasons_"+seq).css('display','block');
		 $("#returnreasons_"+seq+" td select").css('backgroundColor','rgb(247 249 168)');
		 $("#new_receiptamtrtn_smartyrow_"+seq).css('display','block');
		 $("#new_receiptamtrtn_smartyrow_"+seq+" td select").css('backgroundColor','rgb(247 249 168)');
	 }else if (value ==''){
		 $("#q_action_smartyrow_"+seq).css('backgroundColor','#F0FFF0');
		 $("#q_action_smartyrow_"+seq).css('color','black');
		 $("#new_receiptamt_smartyrow_"+seq).css('display','none');
		 $("#returnreasons_"+seq).css('display','none');
		 $("#new_receiptamtrtn_smartyrow_"+seq).css('display','none');
	 }else {
		 $("#q_action_smartyrow_"+seq).css('backgroundColor','#f57b7b');
		 $("#q_action_smartyrow_"+seq).css('color','white');
		 $("#new_receiptamt_smartyrow_"+seq).css('display','none');
		 $("#returnreasons_"+seq).css('display','none');
		 $("#new_receiptamtrtn_smartyrow_"+seq).css('display','none');
		 }
	}
<% if (stp_code.equalsIgnoreCase("instorage")){%>
$('[id*="c_assignedagent_smartyrow_"]').each(function(){
	   //console.log($(this));
	   var i =0;
	   var that;
	   $(this).children().each(function(){
		   	if (i==0){
		   		that = $(this);
		   	}
		  	i++;
	   	}
	   
	   );
	   console.log(i);
	   if(i<3){
		  $(that).remove();
	   }
});
<%}%>

$("#c_rcv_hp").change(function(){
	if($('#c_rcv_hp').val().length==11){
		$('#c_rcv_hp').val($('#c_rcv_hp').val().replace(/(\d{4})(\d{3})(\d{4})/, "$1-$2-$3"));
	}
});


function doGlobalSelectForAgents(){
	
	var globalAgentid = $("#globalagentselect").val();
	
	$('[id^=c_assignedagent_smartyrow_]').each(function() {
		 var number = this.id.split('_').pop();
		 $('#c_assignedagent_smartyrow_'+number).val(globalAgentid);
	});
	
}
function doGlobalSelectForRural(){
	
	var globalRuralCode = $("#globalruralselect").val();
	
	$('[id^=c_rural_smartyrow_]').each(function() {
		 var number = this.id.split('_').pop();
		 $('#c_rural_smartyrow_'+number).val(globalRuralCode);
	});
	
}


function doGlobalDistrictSelect(){
	
	var globalDistrictCode = $("#globalDistrictSelect").val();
	
	$('[id^=c_rcv_district_smartyrow_]').each(function() {
		 var number = this.id.split('_').pop();
		 $('#c_rcv_district_smartyrow_'+number).val(globalDistrictCode);
	});
	
}
<% if (stp_code.equalsIgnoreCase("return_to_cust")){%>
function changeDropListRtn(that, seq){
	 var value = $("#q_action_smartyrow_"+seq).val();
	 if (value == 'RESEND'){
		 $("#q_action_smartyrow_"+seq).css('backgroundColor','#2f2fd2');
		 $("#q_action_smartyrow_"+seq).css('color','white');
		 $("#agentlist_"+seq).css('display','block');
		 $("#agentlist_"+seq+" td select").css('backgroundColor','rgb(247 249 168)');
	 }else {
		 $("#q_action_smartyrow_"+seq).css('backgroundColor','#F0FFF0');
		 $("#q_action_smartyrow_"+seq).css('color','#424242');
		 $("#agentlist_"+seq).css('display','none');
		 $("#returnreasons_"+seq).css('display','none');
	}
}
<%}%>




</script>