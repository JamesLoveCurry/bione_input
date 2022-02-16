<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ include file="/common/meta.jsp"%>
<%@ include file="/common/taglibs.jsp"%>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<%-- <link rel="stylesheet" type="text/css" href="${ctx}/js/bootstrap3/css/bootstrap.min.css" /> --%>

<style>
* {
  -webkit-box-sizing: content-box;
  -moz-box-sizing: content-box;
  box-sizing: content-box;
}
#center{
	background-color : #F1F1F1;
}
</style>

<%@ include file="/common/jquery_load.jsp"%>
<%@ include file="/common/ligerUI_load.jsp"%>
<%@ include file="/common/zTree_load.jsp"%>
<link rel="stylesheet" type="text/css"
	href="${ctx}/css/classics/template/template22.css" />
<script type="text/javascript">
	var rightWidth=0;
    $(function() {
		templateshow();
    });
    function templateshow() {
		var $content = $(window);
		$("#right").height($content.height() - 5);
		$("#rightDiv").height($("#right").height() - 1);
		$("#left").height($content.height() - 5);
		var rightHeight = $("#right").height();
		var leftHeight = $("#left").height();
		$("#lefttable").hide();
		var leftTitle =         
			'<div width="8%"                                                                                                             '+
			'	style="float: left; position: relative; height: 20p; margin-top: 5px">    '+
			'		<i class = "icon-guide search-size"></i>                       '+
			'</div>                                                                                                                             '+
			'<div width="90%">                                                                                                         '+
			'	<span                                                                                                                             '+
			'		style="font-size: 12; float: left; position: relative; line-height: 25px; padding-left: 2px"> '+
			'		<span style="font-size: 12"><span id="leftSpan"></span></span>                                     '+
			'	</span>                                                                                                                         '+
			'</div>                                                                                                                             ';
		$("#left").attr("title",leftTitle);
	 	$("body").ligerLayout({
			 leftWidth: 240,
			 allowLeftResize : true ,
			 onEndResize: function(){
				 if(typeof resizeLay == "function"){
					 resizeLay();
				 }
			 }
         });
	 	 $("#left").attr("title","");
		// $(".l-layout-header").hide();
/* 		 rightWidth=$content.width()-$("#left").width() - 8;
		$("#rightDiv").width(rightWidth);
 */
    }
</script>
<sitemesh:write property='head' />
</head>
<body>
	<div id="left" position="left" style="background-color: #FFFFFF">
		<div id="leftDiv" >
		<sitemesh:div property='template.left' />
		</div>
	</div>
	<div id="right" position="center" style="overflow-x: auto;overflow-y: hidden;">
		<div id="rightDiv" style="width:100%">
		<sitemesh:div property='template.right' />
		</div>
	</div>
</body>
</html>
