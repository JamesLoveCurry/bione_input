<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ include file="/common/meta.jsp"%>
<%@ include file="/common/taglibs.jsp"%>
<%@ include file="/common/jquery_load.jsp"%>
<%@ include file="/common/ligerUI_load.jsp"%>
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
	<script src="${ctx}/js/bione/BIONE.js" type="text/javascript" charset="utf-8"></script>

	<!-- HTML5 Shim and Respond.js IE8 support of HTML5 elements and media queries -->
	<!-- WARNING: Respond.js doesn't work if you view the page via file:// -->
	<!--[if lt IE 9]>
	<script src="/static/js/html5shiv/3.7.3/html5shiv.min.js"></script>
	<script src="/static/js/respond/1.4.2/respond.min.js"></script>
	<![endif]-->


</head>
<body class="hold-transition skin-blue sidebar-mini">
<div class="col-lg-3 col-xs-3">
	<!-- small box -->
	<div class="small-box bg-aqua">
		<div class="inner">
			<h4>指标追溯分析</h4>
		</div>
		<div class="icon">
			<i class="icon ion-ios-shuffle-strong"></i>
		</div>
		<a href="javascript:indexAnalysisButClick();" class="small-box-footer">点击查看 <i class="fa fa-arrow-circle-right"></i></a>
	</div>
</div>
<!-- ./col -->
<div class="col-lg-3 col-xs-3">
	<!-- small box -->
	<div class="small-box bg-green">
		<div class="inner">
			<h4>报表数据查询</h4>
		</div>
		<div class="icon">
			<i class="ion ion-pie-graph"></i>
		</div>
		<a href="javascript:detailAnalysisButClick()" class="small-box-footer">点击查看 <i class="fa fa-arrow-circle-right"></i></a>
	</div>
</div>
<!-- ./col -->
<div class="col-lg-3 col-xs-3">
	<!-- small box -->
	<div class="small-box bg-yellow">
		<div class="inner">
			<h4>明细灵活查询</h4>
		</div>
		<div class="icon">
			<i class="icon ion-ios-color-wand"></i>
		</div>
		<a href="javascript:searchDetailButClick()" class="small-box-footer">点击查看 <i class="fa fa-arrow-circle-right"></i></a>
	</div>
</div>
<!-- ./col -->
<div class="col-lg-3 col-xs-3">
	<!-- small box -->
	<div class="small-box bg-red">
		<div class="inner">
			<h4>指标信息查询</h4>
		</div>
		<div class="icon">
			<i class="icon ion-search"></i>
		</div>
		<a href="javascript:searchIndexButClick()" class="small-box-footer">点击查看 <i class="fa fa-arrow-circle-right"></i></a>
	</div>
</div>

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
	// 快速图标导航
	var detailAnalysisButClick = function(){
		var url = '/frs/integratedquery/rptquery';
		$.ajax({
			async : false,
			dataType : "json",
			type : "post",
			url : '${ctx}/bione/permission/getPremissionByUrl',
			data : { url : url },
			success : function(result) {
				if(result.status == 'success'){
					top.addTabs({
						id: 'detailDataQuery',
						title: '报表数据查询',
						close: true,
						url: url,
						urlType: "relative"
					});
				} else {
					parent.BIONE.tip("用户没有该菜单的访问权限，请联系管理员授权");
				}
			}
		});
	}
	var indexAnalysisButClick = function(){
		var url = '/rpt/frame/idx/idxanalysis';
		$.ajax({
			async : false,
			dataType : "json",
			type : "post",
			url : '${ctx}/bione/permission/getPremissionByUrl',
			data : { url : url },
			success : function(result) {
				if(result.status == 'success'){
					top.addTabs({
						id: 'idxAnalysis',
						title: '指标追溯分析',
						close: true,
						url: url,
						urlType: "relative"
					});
				} else {
					parent.BIONE.tip("用户没有该菜单的访问权限，请联系管理员授权");
				}
			}
		});

	}
	var searchDetailButClick = function(){
		var url = '/report/frame/datashow/detail';
		$.ajax({
			async : false,
			dataType : "json",
			type : "post",
			url : '${ctx}/bione/permission/getPremissionByUrl',
			data : { url : url },
			success : function(result) {
				if(result.status == 'success'){
					top.addTabs({
						id: 'searchDt',
						title: '明细灵活查询',
						close: true,
						url: url,
						urlType: "relative"
					});
				} else {
					parent.BIONE.tip("用户没有该菜单的访问权限，请联系管理员授权");
				}
			}
		});

	}
	var searchIndexButClick = function(){
		var url = '/report/frame/idx/preview';
		$.ajax({
			async : false,
			dataType : "json",
			type : "post",
			url : '${ctx}/bione/permission/getPremissionByUrl',
			data : { url : url },
			success : function(result) {
				if(result.status == 'success'){
					top.addTabs({
						id: 'searchIdxInfo',
						title: '指标信息查询',
						close: true,
						url: url,
						urlType: "relative"
					});
				} else {
					parent.BIONE.tip("用户没有该菜单的访问权限，请联系管理员授权");
				}
			}
		});

	}
</script>

</body>
</html>
