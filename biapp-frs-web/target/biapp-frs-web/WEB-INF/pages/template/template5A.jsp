<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ include file="/common/meta.jsp"%>
<%@ include file="/common/taglibs.jsp"%>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<%@ include file="/common/jquery_load.jsp"%>
<%@ include file="/common/ligerUI_load.jsp"%>
<%@ include file="/common/zTree_load.jsp"%>
<link rel="stylesheet" type="text/css"
	href="${ctx}/css/classics/template/template6.css" />
<script type="text/javascript">
    $(function() {
	templateshow();
    });
    function templateshow() {
	var $content = $(document);
	
	var topHeight = 60;
	$("#top").height(topHeight);
	$("#center").height($content.height() -topHeight- 60);
	
	
	var width = $content.width();
	var leftWidth = 200;
	$("#left").width(leftWidth);
	$("#right").width(width-leftWidth-150);
	
	var centerHeight = $("#center").height();
	$("#left").height(centerHeight);
	$("#right").height(centerHeight);
	
	
    }
</script>
<sitemesh:write property='head' />

</head>
<body>
	<div id="all"  >
		<div id="top">
			<div id="taskChoose"></div>
		</div>
		<div id="center" style="background-color: #FFFFFF" >
			<div id="left">
				<div id ="taskList" ></div>
			</div>
			<div id="right">
				<div id ="taskNodeList"  ></div>
			</div>
		</div>
	</div>
</body>
</html>
