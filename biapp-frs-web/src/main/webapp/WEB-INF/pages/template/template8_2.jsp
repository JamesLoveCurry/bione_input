<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ include file="/common/meta.jsp"%>
<%@ include file="/common/taglibs.jsp"%>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<%@ include file="/common/jquery_load.jsp"%>
<%@ include file="/common/ligerUI_load.jsp"%>
<link rel="stylesheet" type="text/css"
	href="${ctx}/css/classics/template/template8.css" />
<script type="text/javascript">
	var grid=null;
	
	function templateshow(){
		var $content = $(document);
		$("#center").height($content.height()-36);
		if (grid) {
			var centerHeight = $("#center").height();
			grid.setHeight(centerHeight-31);
		}
	}
</script>
<sitemesh:write property='head' />
<script type="text/javascript">
	$(function() {
		templateshow();
	});
</script>
<style>
	.tdstyle {
		background-position: 0px -100px;
		margin: 0;
		padding: 0;
		border: 0;
	}
</style>
</head>
<body>
<div id="center" style="width: 100%">
		<div id="topmenu" class="l-panel-topbar"></div> 
		<div id="maingrid" class="maingrid"></div>
</div>
</body>
</html>