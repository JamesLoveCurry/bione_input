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
	href="${ctx}/css/classics/template/template32.css" />
<script type="text/javascript">
	$(function() {
		$("#lefttable").hide();
		var leftTitle =         
			'<div width="8%"                                                                                                             '+
			'	style="float: left; position: relative; height: 20p; margin-top: 5px">    '+
			'		<i class = "icon-guide search-size"></i>                       '+
			'</div>                                                                                                                             '+
			'<div width="90%">                                                                                                         '+
			'	<span                                                                                                                             '+
			'		style="font-size: 12; float: left; position: relative; line-height: 25px; padding-left: 2px"> '+
			'		<span style="font-size: 12">'+$("#leftSpan").html()+'</span>                                     '+
			'	</span>                                                                                                                         '+
			'</div>                                                                                                                             ';
			
		$("#left").attr("title",leftTitle);
		$('#layout').ligerLayout({
			leftWidth: 240,
			allowLeftResize : true ,
			minLeftWidth: 200
		});
		 $("#left").attr("title","");
		var $content = $('.l-tab-content');
		$content.css("margin-top","0px");
		$content.height($content.parent().height() - 30 - 22);
		$(window).resize(function() {
			$content.height($content.parent().height() - 30 - 22);
		});
	});
</script>
<sitemesh:write property='head' />
</head>
<body>
	<div id="layout">
		<div id = "left" class="position" position="left">
			<div id="lefttable" width="100%" border="0">
				<div width="100%"
					style="height:30px;background-image: url(${ctx}/css/classics/ligerUI/Gray/images/ui/gridbar.jpg);border-bottom:1px solid #D6D6D6;">
					<div width="8%"
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
			<div id="treeSearchbar" style="width:99%;margin-top:2px;padding-left:2px;">
				<ul>
					<li style="width:98%;text-align:left;">                      
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
			<div id="navtab" class="liger-tab">
				<sitemesh:div property='template.tabitem' />
			</div>
		</div>
		<div id ="center" class="position" position="center">
			<sitemesh:div property='template.right' />
		</div>
	</div>
</body>
</html>
