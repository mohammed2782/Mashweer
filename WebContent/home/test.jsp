<%@ page pageEncoding="utf-8" %>
<%
ServletContext servletContext = getServletContext();
String mainProjectPath = servletContext.getContextPath();
%>
<html>
<head>
<script src="<%=mainProjectPath%>/smartyresources/js/jquery-2.1.3.min.js"></script>
 <link href="<%=mainProjectPath%>/smartyresources/css/bootstrap-datetimepicker.min.css" rel="stylesheet">
     <script src="<%=mainProjectPath%>/smartyresources/js/moment.min.js"></script>
    <script src="<%=mainProjectPath%>/smartyresources/js/bootstrap-datetimepicker.min.js"></script>
    <link href="<%=mainProjectPath%>/smartyresources/css/bootstrap-rtl.min.css" rel="stylesheet">
    <script src="<%=mainProjectPath%>/smartyresources/js/bootstrap-rtl.min.js"></script>
<meta charset="windows-1256">
<title>Insert title here</title>
</head>
<body>
 <div class="container">
    <div class="row">
        <div class='col-sm-6'>
            <div class="form-group">
                <div class='input-group date' id='datetimepicker1'>
                    <input type='text' class="form-control" />
                    <span class="input-group-addon">
                        <span class="glyphicon glyphicon-calendar"></span>
                    </span>
                </div>
            </div>
        </div>
        <script type="text/javascript">
            $(function () {
                $('#datetimepicker1').datetimepicker();
            });
        </script>
    </div>
</div>
</body>
</html>
