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
<script type="text/javascript">
	$(function() {
		templateshow();

	});
	function templateshow() {
		var windowHeight = $("#right",parent.document).height();
		var windowWidth = $("#right",parent.document).width();
		$("#center").height(windowHeight-30 );
		$("#center").width(windowWidth -5 );
		var formheight=20;
		var centerHeight = $("#center").height();
		$("#mainform").height(formheight);
		$("#nodeContent").width($("#center").width());
		$("#nodeContent").height(centerHeight);
	}
</script>
<sitemesh:write property='head' />
<script type="text/javascript">
	$(function() {
		templateshow();
	});
</script>
</head>
<body>
	<div id="center">
		<div id="mainform">
		</div>
		<div id="nodeContent">
		</div>
	</div>
</body>
</html>