<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ include file="/common/meta.jsp"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
	<meta charset="utf-8">
	<meta http-equiv="X-UA-Compatible" content="IE=edge">
	<title>AdminLTE 2 | Dashboard</title>
	<!-- Tell the browser to be responsive to screen width -->
	<meta content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no" name="viewport">
	<!-- Bootstrap 3.3.7 -->
	<link rel="stylesheet" href="${ctx}/static/AdminLTE-With-Iframe/bootstrap/css/bootstrap.min.css">
	<!-- Font Awesome -->
	<link rel="stylesheet" href="${ctx}/static/AdminLTE-With-Iframe/dist/css/font-awesome.min.css">
	<!-- Ionicons -->
	<link rel="stylesheet" href="${ctx}/static/AdminLTE-With-Iframe/dist/css/ionicons.min.css">
	<!-- Theme style -->
	<link rel="stylesheet" href="${ctx}/static/css/bower_components/dist/css/AdminLTE.min.css">
	<!-- AdminLTE Skins. Choose a skin from the css/skins
         folder instead of downloading all of them to reduce the load. -->
	<link rel="stylesheet" href="${ctx}/static/css/bower_components/dist/css/skins/_all-skins.min.css">

	<!-- HTML5 Shim and Respond.js IE8 support of HTML5 elements and media queries -->
	<!-- WARNING: Respond.js doesn't work if you view the page via file:// -->
	<!--[if lt IE 9]>
	<script src="/static/js/html5shiv/3.7.3/html5shiv.min.js"></script>
	<script src="/static/js/respond/1.4.2/respond.min.js"></script>
	<![endif]-->


</head>
<body class="hold-transition skin-blue sidebar-mini">
	<div class="col-md-12">
		<p class="text-center">
			<strong>本月填报任务完成情况</strong>
		</p>

		<div class="progress-group">
			<span class="progress-text">月报一批</span> <span
				class="progress-number"><b>160</b>/200</span>

			<div class="progress sm">
				<div class="progress-bar progress-bar-aqua" style="width: 80%"></div>
			</div>
		</div>
		<!-- /.progress-group -->
		<div class="progress-group">
			<span class="progress-text">月报二批</span> <span
				class="progress-number"><b>310</b>/400</span>

			<div class="progress sm">
				<div class="progress-bar progress-bar-red" style="width: 80%"></div>
			</div>
		</div>
		<!-- /.progress-group -->
		<div class="progress-group">
			<span class="progress-text">季报一批</span> <span
				class="progress-number"><b>480</b>/800</span>

			<div class="progress sm">
				<div class="progress-bar progress-bar-green" style="width: 60%"></div>
			</div>
		</div>
		<!-- /.progress-group -->
		<div class="progress-group">
			<span class="progress-text">季报二批</span> <span
				class="progress-number"><b>250</b>/500</span>

			<div class="progress sm">
				<div class="progress-bar progress-bar-yellow" style="width: 50%"></div>
			</div>
		</div>
		<!-- /.progress-group -->
	</div>
	<!-- /.col -->


<!-- jQuery 2.2.3 -->
<script src="${ctx}/static/AdminLTE-With-Iframe/plugins/jQuery/jquery-2.2.3.min.js"></script>
<!-- Bootstrap 3.3.6 -->
<script src="${ctx}/static/AdminLTE-With-Iframe/bootstrap/js/bootstrap.min.js"></script>
<!-- FastClick -->
<script src="${ctx}/static/AdminLTE-With-Iframe/plugins/fastclick/fastclick.js"></script>
<!-- AdminLTE App -->
<script src="${ctx}/static/AdminLTE-With-Iframe/dist/js/app.js"></script>
<!--tabs-->
<script src="${ctx}/static/AdminLTE-With-Iframe/dist/js/app_iframe.js"></script>


<script type="text/javascript">

</script>

</body>
</html>
