<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page
	import="com.app.site.security.DoLogin,com.app.site.security.*"%>
<% 
LoginUser user = new LoginUser();
HttpSession sessionRQS = request.getSession();
if (sessionRQS.getAttribute("lu")!=null){
	user = (LoginUser)sessionRQS.getAttribute("lu");
}
	if(user.isLoggedIn()){
		String redirectURL = "";
		if (user.getRank_code().equalsIgnoreCase("COMPVIEWONLY")){
			redirectURL ="./cases/ViewOnlyAllCases.jsp";
		}else{
			redirectURL ="./home/home.jsp";
		}
		response.sendRedirect(redirectURL); 
	}else{
		out.println(user.getErrorMsg());
	}
%>
<!DOCTYPE html>
<html lang="en">
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <!-- Meta, title, CSS, favicons, etc. -->
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">

    <title>مشاوير</title>

    <!-- Bootstrap -->
    <link href="./smartyresources/css/bootstrap-rtl.min.css" rel="stylesheet">
    <!-- Font Awesome -->
    <link href="./smartyresources/css/font-awesome.min.css" rel="stylesheet">
    <!-- NProgress -->
    <link href="./smartyresources/css/nprogress.css" rel="stylesheet">
   
    <!-- Custom Theme Style -->
    <link href="./smartyresources/css/custom.min.css" rel="stylesheet">
  </head>
 <body class="login" dir = "rtl">
	<div style="
    background-color:white;
    position:fixed;
    width:100%;
    height:100%;
    top:0px;
    left:0px;
    z-index:1000;">
   		<div class="col-md-6 " style='padding-top:7%; background-color:#d49335; height:100%; color:white' >
      		<div class="col-md-11 col-md-offset-1 "  >
	      		<div class="login_wrapper ">
	        		<div class="animate form login_form">
	          			<section class="login_content">
				            <form action='./DoLogin' method ='post'>
				              <h1>مشاوير- البصرة</h1>
				              <div>
				                <input type="text" class="form-control" name="userid" placeholder="المستخدم" required />
				              </div>
				              <div>
				                <input type="password" name="userpassword"  class="form-control" style="background-color : #d1d1d1; " placeholder="كلمة المرور" required />
				              </div>
				              <div>
				                <button type="submit" class="btn btn-default submit" >دخول</button>
				              </div>
				
				              <div class="clearfix"></div>
				
				              <div class="separator">
				               
				                <div class="clearfix"></div>
				                <br />
				
				                <div>
				                <p style='margin-top:20%;'>DoTS! is a special management system designed by Softecha, <a href='www.softecha.com'>www.softecha.com</a></br>©2019 All Rights Reserved. </p>
				                </div>
				              </div>
				            </form>
          				</section>
	        		</div>
	      		</div>
	     	</div>
	     </div>
      <div class="col-md-6 " style='padding-top:3%;background-color:white;'>
      	<div class="col-md-11 col-md-offset-1" style='margin-right: 10px;'>
        	<h1><img src='./smartyresources/img/logo_xl.png'></img></h1>
        </div>
        
      </div>
    </div>
  </body>
 
</html>