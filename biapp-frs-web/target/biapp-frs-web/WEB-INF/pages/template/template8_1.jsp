<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ include file="/common/meta.jsp"%>
<%@ include file="/common/taglibs.jsp"%>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<%@ include file="/common/jquery_load.jsp"%>
<%@ include file="/common/ligerUI_load.jsp"%>
<%@ include file="/common/zTree_load.jsp"%>
<style>
	#all {
		width: 100%;
		position: relative;
	}
	
	#center {
		float: left;
		width: 98%;
		border-width: 1px;
		border-style: solid;
		border-color: #D6D6D6;
		margin-left: 1%;
	}
	
	#center .ztree {
		background-color: #F1F1F1;
	}
	#bottom {
		margin:0 auto;
		overflow: hidden;
		position: relative;
		margin-top: 10px;
	}

</style>
<script type="text/javascript">
    $(function() {
		templateshow();
    });
    function templateshow() {
		var $content = $(document);
		$("#all").height($content.height() - 60);
		$("#center").height($content.height() - 60);
		$("#bottom").height(40);
		var centerHeight = $("#center").height();
		//26px是.l-treetoolbar中height
		var tb = $("#treeToolbar").height()-1;
		var $centerTreeContainer = $("#centerTreeContainer");
		$centerTreeContainer.height(centerHeight - 32);
    }
</script>
<sitemesh:write property='head' />
</head>
<body style="text-align: center;">
	<div id="all">
		<div id="center" style="background-color: #FFFFFF">
			<sitemesh:write property='div.template.center' />
		</div>
	</div>
	<div id="bottom">
		<sitemesh:write property='div.template.bottom' />
	</div>
</body>
</html>
