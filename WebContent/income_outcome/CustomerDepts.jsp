<%@ include file="../Main/Main.jsp"%>
<%@ page import="com.app.incomeoutcome.CustomerDepts" %> 
<%
 	String pageName1 = this.getClass().getPackage().getName()+"."+this.getClass().getSimpleName();
	CustomerDepts cd = new CustomerDepts(); 
 	Render(cd  , out , request, response , Myglobals , objectState , pageName1); 
 	
%>
<script type="text/javascript">

    $(document).ready(function(){
    	$("tr.pointer:contains('-'):even").css('background-color', '#cc8320');
    	$("tr.pointer:contains('-'):odd").css('background-color', '#cc83209c');
    }); 

</script>

<%@ include file="../Main/footer.jsp"%> 