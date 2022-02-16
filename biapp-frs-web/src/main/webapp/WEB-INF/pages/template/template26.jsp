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
	href="${ctx}/css/classics/template/template14.css" />
<style type="text/css">
.searchtitle {
 	margin: auto; 
 	display: inline-block; 
 	*display: inline; 
 	zoom: 1; 
 	width: 100%;
}

.searchtitle .togglebtn {
	position: absolute;
	background: url("../../../images/classics/ligerui/toggle.gif") no-repeat 0px 0px;
	top: 5px;
	width: 9px;
	height: 10px;
	right: 50px;
	cursor: pointer;
	font-size: 0;
	margin: 0;
	padding: 0;
	border: 0;
}

.searchtitle .togglebtn-down {
	background-position: 0px -10px;
}

.searchbox {
	width: 100%;
	text-align: center;
	margin: auto;
	margin-bottom: 5px;
	border-style: solid;
	border-width: 1px;
	border-color: #D6D6D6;
}

</style>
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
		//$("#searchbox").css("display","none");
		$(".searchtitle .togglebtn").live(
				'click',
				function() {
				    var searchbox = $(this).parent().nextAll(
					    "div.searchbox:first");
				    var centerHeight = $("#center").height();
				    if ($(this).hasClass("togglebtn-down")) {
					$(this).removeClass("togglebtn-down");
						searchbox.slideToggle('fast', function() {
							View.spreadDom.height($(window).height()- $("#mainsearch").height()-62);
							if(View){
								View.resize(View.spread);
							}
						});
				    } else {
					$(this).addClass("togglebtn-down");
						searchbox.slideToggle('fast', function() {
							View.spreadDom.height($(window).height()- $("#mainsearch").height()-62);
							if(View){
								View.resize(View.spread);
							}
						});
				    }
		});
		
	}
 	function templateinit(taskType, flag) {//true 一次  false 二次
	}
</script>
<sitemesh:write property='head' />
</head>
<body>
	<div id="center">
		<div id="mainsearch">
			<div class="searchtitle">
				<i id="treeSearchIcon" class="icon-search search-size" style="position: absolute;"></i>
				<div class="togglebtn togglebtn-down">&nbsp;</div>
			</div>
			<!-- <div class="navline" style="margin-bottom: 4px;"></div> -->
			<div id="searchbox" class="searchbox">
				<form id="formsearch" style="width: 920px;float:left">
					<div id="search"></div>
					<div class="l-clear"></div>
				</form>
				<div id="searchbtn" class="searchbtn"  style="width: 80px; margin-top:3px;"></div>
			</div>
			
		</div>
		<div id="top" style="height: 30px;margin-top: 3px;margin-bottom: 0px;padding: 0px auto;">
<!-- 		<div id="top" style="overflow:auto;"> -->
			<div id="button" style="margin: 0px auto;padding: 0px auto;position: relative;"></div>
			<!-- <div id="button1" style="margin: 0px auto;padding: 0px auto;position: relative;width: 100%;float: left;"></div>
			<div id="button2" style="margin: 0px auto;padding: 0px auto;position: relative;width: 100%;float: left;"></div> -->
		</div>
		<div id="spread" style="width:99.4%;border:1px solid #D0D0D0;margin: 1.5px;"></div>
	</div>
</body>
</html>
