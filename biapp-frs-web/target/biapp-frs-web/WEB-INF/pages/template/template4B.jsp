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
<script type="text/javascript">
    $(function() {
		templateshow();
    });
    function templateshow() {
	var $content = $(document);
	var windowHeight = $("#nodeContent",parent.document).height();
	$("#center").height(windowHeight );
	var centerHeight = $("#center").height();
	var $leftTreeContainer = $("#leftTreeContainer");
	$leftTreeContainer.height(centerHeight - 30);
    }
</script>
<sitemesh:write property='head' />
</head>
<body style="text-align: center;">
	<div id="center" style="background-color: #FFFFFF;">
		<div id="lefttable" width="100%" border="0">
			<div width="100%"
				style=" display: initial; height:30px; background-image: url(${ctx}/css/classics/ligerUI/Gray/images/ui/gridbar.jpg);border-bottom:1px solid #D6D6D6;">
				<div width="8%"
					style="padding-left: 10px; float: left; position: relative; height: 20p; margin-top: 8px">
					<sitemesh:div property='template.left.up.icon' />
				</div>
				<div width="30%">
					<span
						style="font-size: 12; float: left; position: relative; line-height: 30px; padding-left: 2px">
						<sitemesh:div property='template.left.up' />
					</span>
				</div>
				<div width="60%"> 
					<div
						style="float: left; position: relative; padding-right: 3px; padding-top:4px;">
						<sitemesh:div property='template.left.up.right' />
					</div>
				</div>
			</div>
		</div>
		<!-- 修改页面样式 display:inline-block; -->
		<div id="treeToolbar" style="border-bottom-width: 1px; border-bottom-style: solid; border-bottom-color: #D6D6D6;display:inline"></div>
		<div id="treeSearchbar" style="margin-top:2px;padding-left:2px;width:30%;display:inline-block;">
			<ul>
				<li style="text-align:left;">                      
					<div class="l-text-wrapper" style="width: 100%;">                         
						<div class="l-text l-text-date" style="width: 100%;">                    
							<input id="treeSearchInput" type="text" class="l-text-field"  style="width: 100%;" />    
							<div class="l-trigger">                                                                      
								<i id="treeSearchIcon" class = "icon-search search-size" ></i>                                                 
							</div>
						</div>                                                                                                   
					</div>
				</li>
			</ul>                                                                                                         
		</div>       
		<div id="leftTreeContainer"
			style="width: 100%; overflow: auto; clear: both; background-color: #FFFFFF;">
			<ul id="leftTree"
				style="font-size: 12; background-color: #FFFFFF; width: 92%"
				class="ztree"></ul>
		</div>
	</div>
</body>
</html>
