<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ include file="/common/meta.jsp"%>
<%@ include file="/common/taglibs.jsp"%>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<%@ include file="/common/jquery_load.jsp"%>
<%@ include file="/common/ligerUI_load.jsp"%>
<link rel="stylesheet" type="text/css"
	href="${ctx}/css/classics/template/template1.css" />
<sitemesh:write property='head' />
<script type="text/javascript">
	$(function() {
		templateshow();
	});
	
	function templateshow() {
		var windowHeight = $(window).height();
		var windowWidth = $(window).width();
		$("#center").height(windowHeight-50 );
		var centerHeight = $("#center").height();
		$("#nodeContent").height(centerHeight - $("#mainform").height());
	}
</script>
</head>
<body>
	<div id="center">
		<form name="mainform" method="post" id="mainform"></form>
		<div id="nodeContent">
		</div>
	</div>
	<div id="bottom">
		<div class="form-bar">
			<div class="form-bar-inner" style="padding-top: 5px"></div>
		</div>
	</div>
</body>
</html>