<%@ page language="java" contentType="text/html; charset=GBK"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ include file="/common/meta.jsp"%>
<%@ include file="/common/taglibs.jsp"%>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<%@ include file="/common/jquery_load.jsp"%>
<%@ include file="/common/ligerUI_load.jsp"%>
<%@ include file="/common/zTree_load.jsp"%>
<link rel="stylesheet" type="text/css"
	href="${ctx}/css/classics/template/template14.css" />
<script type="text/javascript">
	var logicGrid;
	var sumpartGrid;
	var warnGrid;
	
	$(function() {
		templateshow();
	});
	function templateshow(){
		var $content = $(window);
		var height = $content.height();
		$("#center").height(height - 2);
		$("#spread").height(height - 40);
	}
 	function templateinit(taskType, flag) {//true 一次  false 二次
 		//初始化tab
		var tabObj = $("#tab").ligerTab({
		});
		if(flag){
			if('02' == taskType){
				$("li[tabid='sumpart']").css("display", "none");
			}
		}else{
			$("li[tabid='sumpart']").css("display", "none");
		}
	}
</script>
<sitemesh:write property='head' />
</head>
<body>
	<div id="center">
		<div id="top" style="height: 30px;margin-top: 3px;margin-bottom: 0px;padding: 0px auto;">
			<div id="button" style="margin-left: 10px ;padding: 0px auto;position: relative;"></div>
		</div>
		<div id="spread" style="width:99.4%;border:1px solid #D0D0D0;margin: 1.5px;"></div>
	</div>
</body>
</html>
