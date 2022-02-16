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
	background: url("../../../../images/classics/ligerui/toggle.gif") no-repeat 0px 0px;
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
	
	var grid = null;

	$(function() {
		templateshow();
	});
	
	function templateshow(){
		var $content = $(window);
		var height = $content.height();
		$("#center").height(height - 2);
		if (grid) {
		    grid.setHeight($('#center').height() - $('#mainsearch').height());
		}

		//$("#searchbox").css("display","none");
		$(".searchtitle .togglebtn").live('click',
			function() {
				//var searchbox = $(this).parent().nextAll("div.searchbox:first");
				var searchbox = $(this).parent().nextAll("div.searchbox");
			    var centerHeight = $("#center").height();
			    if ($(this).hasClass("togglebtn-down")) {
					$(this).removeClass("togglebtn-down");  //移除下拉样式
					searchbox.slideToggle('fast', function() {  //通过高度切换元素的可见性
						/* View.spreadDom.height($(window).height()- $("#mainsearch").height()-62);
						if(View){
							View.resize(View.spread);
						} */
						if (grid) {
							grid.setHeight($('#center').height() - $('#mainsearch').height());
						}
					});
			    } else {
					$(this).addClass("togglebtn-down");  //添加下拉样式
					searchbox.slideToggle('fast', function() {
						/* View.spreadDom.height($(window).height()- $("#mainsearch").height()-62);
						if(View){
							View.resize(View.spread);
						} */
						if (grid) {
					    	grid.setHeight($('#center').height()  + 6 - $('#mainsearch').height());
					    }
					});
		    	}
		});
	}

</script>
<sitemesh:write property='head' />
</head>
<body>
	<div id="center" style="width : 100%">
		<div id="search">
			<div class="searchtitle">
				<img src="${ctx}/images/classics/icons/zoom.png"> 
					<%--<span style="color:red;font-size:10px;">--%>
					<%--&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;--%>
					<%--*修改只针对本次报送有效，请同步在源系统修改数据!</span>--%>
				</img>
				<div class="togglebtn togglebtn-down">&nbsp;</div>
			</div>
			<!-- <div class="navline" style="margin-bottom: 4px;"></div> -->
			<div id="mainsearchbox" style="height: 95px;" class="searchbox">
				<form id="mainformsearch" style="width: 850px;float:left">
					<div id="mainsearch"></div>
					<div class="l-clear"></div>
				</form>
				<div id="mainsearchbtn" class="searchbtn" style="width: 50px; margin-top:3px; margin-right: -120px;"></div>
			</div>
		</div>
		<div id="top" style="height: 30px;margin-top: 3px;margin-bottom: 3px;padding: 0px auto;">
			<!-- <div id="top" style="overflow:auto;"> -->
			<div id="button" style="margin: 0px auto; padding:0px auto; position: relative; width:100%; float:left;"></div>
			<!-- <div id="button1" style="margin: 0px auto;padding: 0px auto;position: relative;width: 100%;float: left;"></div>
			<div id="button2" style="margin: 0px auto;padding: 0px auto;position: relative;width: 100%;float: left;"></div> -->
		</div>
		<!-- 
		<div id="spread" style="width:99.4%;border:1px solid #D0D0D0;margin: 1.5px;"></div>
		 -->
		<div id="maingrid" class="maingrid"></div>
	</div>
</body>
</html>
