<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ include file="/common/meta.jsp"%>
<%@ include file="/common/taglibs.jsp"%>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<link rel="stylesheet" type="text/css" href="${ctx}/js/bootstrap3/css/bootstrap.min.css" />
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
		$("#tab").height($content.height() - 5);
		var rightHeight = $("#right").height();
		var leftHeight = $("#left").height();
		var $treeContainer = $("#treeContainer");
		$treeContainer.height(leftHeight - 56 - $("#treeSearchbar").height() );
		$("#lefttable").hide();
		var leftTitle =         
			'<div width="8%"                                                                                                             '+
			'	style="float: left; position: relative; height: 20p; margin-top: 5px">    '+
			'		<i class = "icon-guide search-size"></i>                      '+
			'</div>                                                                                                                             '+
			'<div width="90%">                                                                                                         '+
			'	<span                                                                                                                             '+
			'		style="font-size: 12; float: left; position: relative; line-height: 25px; padding-left: 2px"> '+
			'		<span style="font-size: 12">'+$("#leftSpan").html()+'</span>                                     '+
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
		//rightWidth=$content.width()-$("#left").width() - 8;
		//$("#rightDiv").width(rightWidth);
		$(document).on("resize","#right",function(){
			
		})

    }
</script>
<sitemesh:write property='head' />
</head>
<body>
	<div id="left" position="left" style="background-color: #FFFFFF">
		<div id="lefttable" width="100%" border="0">
			<div id="leftTitle" width="100%"
				style="height:30px;background-image: url(${ctx}/css/classics/ligerUI/Gray/images/ui/gridbar.jpg);border-bottom:1px solid #D6D6D6;">
				<div  width="8%"
					style="padding-left: 10px; float: left; position: relative; height: 20p; margin-top: 8px">
					<sitemesh:div property='template.left.up.icon' />
				</div>
				<div width="90%">
					<span id="leftSpan"
						style="font-size: 12; float: left; position: relative; line-height: 30px; padding-left: 2px">
						<sitemesh:div property='template.left.up' />
					</span>
				</div>
			</div>
		</div>
		<div id="treeToolbar"
			style="border-bottom-width: 1px; border-bottom-style: solid; border-bottom-color: #D6D6D6;"></div>
		<div id="treeSearchbar" style="width:99%;margin-top:2px;padding-left:2px;">
			<ul>
				<li style="width:98%;text-align:left;">                      
					<div class="l-text-wrapper" style="width: 100%;">                         
						<div class="l-text l-text-date" style="width: 100%;">                    
							<input id="treeSearchInput" type="text" class="l-text-field"  style="width: 95%;" />    
							<div class="l-trigger">                                                                      
								<i id="treeSearchIcon" class = "icon-search search-size" ></i>                                                 
							</div>
						</div>                                                                                                   
					</div>
				</li>
			</ul>                                                                                                         
		</div>                                                                                                      
		<div id="treeContainer"
			style="width: 100%; overflow: auto; clear: both; background-color: #FFFFFF;">
			<ul id="tree"
				style="font-size: 12; background-color: #FFFFFF; width: 92%"
				class="ztree"></ul>
		</div>
		<sitemesh:div property='template.left.down' />
	</div>
	<div id="right" position="center" style="overflow-x: auto;overflow-y: hidden;">
		<div id="rightDiv" style="width:100%">
		<sitemesh:div property='template.right' />
		</div>
	</div>
</body>
</html>
