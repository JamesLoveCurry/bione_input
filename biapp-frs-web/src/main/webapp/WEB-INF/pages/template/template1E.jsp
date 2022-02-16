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
		var windowHeight = $(window).height();
		var windowWidth = $(window).width();
		
		$("#center").height(windowHeight-10 );
		$("#center").width(windowWidth -40 );
		//$("#center").width($(window).height()-310);
		//$("#center").width($(window).width() );
		var formheight=75;
		var centerHeight = $("#center").height();
		$("#mainform").height(formheight);
		$("#nodeContent").width($("#center").width()-5);
		$("#nodeContent").height($("#center").height()-formheight-10);
		$("#rewriteFrame").height($("#nodeContent").height()-10);
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
			<iframe frameborder="0" id="rewriteFrame" name="rewriteFrame" style="width:100%;" src=""></iframe>
		</div>
	</div>
</body>
</html>