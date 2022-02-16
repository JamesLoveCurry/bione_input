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
	$("#all").height($content.height() - 60);
	$("#left").height($content.height() - 60);
	$("#right").height($content.height() - 60);
	$("#bottom").height(40);
	$("#bottom").width(120);
	var leftHeight = $("#left").height();
	var $leftTreeContainer = $("#leftTreeContainer");
	//$leftTreeContainer.height(leftHeight - 32 - 26);
	$leftTreeContainer.height(leftHeight - 56 - $("#treeSearchbar").height() );
    }
</script>
<sitemesh:write property='head' />

</head>
<body>
	<div id="all">
		<div id="left" style="background-color: #FFFFFF">
			<div id="lefttable" width="100%" border="0">
				<div width="100%"
					style="height:30px;background-image: url(${ctx}/css/classics/ligerUI/Gray/images/ui/gridbar.jpg);border-bottom:1px solid #D6D6D6;">
					<div width="8%" style="padding-left: 10px;float: left;position: relative;height:20p;margin-top: 8px">
						<sitemesh:div property='template.left.up.icon' />
					</div>
					<div width="90%">
						<span style="font-size: 12;float: left;position: relative;line-height: 30px;padding-left:2px ">
						 <sitemesh:div property='template.left.up' />
						</span>
					</div>
				</div>
			</div>
			<div id="treeToolbar"
				style="border-bottom-width: 1px; border-bottom-style: solid; border-bottom-color: #D6D6D6;"></div>
			<div id="treeSearchbar" style="width:99%;margin-top:2px;padding-left:2px;">
				<ul>
					<li style="width:99%;text-align:left;">                      
						<div class="l-text-wrapper" style="width: 100%;">                         
							<div class="l-text l-text-date" style="width: 100%;">                    
								<input id="treeSearchInput" type="text" class="l-text-field"  style="width: 100%;height: 18px;padding-left: 0px;" />    
								<div class="l-trigger">                                                                      
									<a id="treeSearchIcon" class = "icon-search search-size" ></a>                                                   
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
		<div id="right" style="background-color: #FFFFFF;">
			<sitemesh:div property='template.right' />
		</div>
	</div>
	<div id="bottom">
		<sitemesh:div property='template.bottom' />
	</div>
</body>
</html>
