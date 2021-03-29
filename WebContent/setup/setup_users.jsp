<%@ include file="../Main/Main.jsp"%>
<%@ page import="com.app.setup.setup_users" %> 

<%  
	String pageName1 = this.getClass().getPackage().getName()+"."+this.getClass().getSimpleName();
	setup_users su = new setup_users(); 
	Render(su , out , request, response , Myglobals , objectState , pageName1);

%>
<script>
function changeProfitAndPartnerShareBackDated(userId){
	
	if (confirm('سوف يتم التعديل على الربح الكلي وحصة الشريك !.')) {
		  // Save it!
		var dataToSend = {"userId":userId};
		$.post('../ChangerProfitAndPartnerShareSRVL' , dataToSend, function(data, status){ 
			if (status=='success'){
				alert('تم التغيير');
			}else{
				alert("Error, please contact Softecha");
			}
	 	});
	} else {
		  // Do nothing!
	}
	
}
function changeAgentShareBackDated(userId){
	
	if (confirm('سوف يتم التعديل على اجرة المندوب لكل الشحنات !.')) {
		  // Save it!
		var dataToSend = {"userId":userId};
		$.post('../ChangerAgentShareSRVL' , dataToSend, function(data, status){ 
			if (status=='success'){
				alert('تم التغيير');
			}else{
				alert("Error, please contact Softecha");
			}
	 	});
	} else {
		  // Do nothing!
	}
	
}

</script>
<%@ include file="../Main/footer.jsp"%> 