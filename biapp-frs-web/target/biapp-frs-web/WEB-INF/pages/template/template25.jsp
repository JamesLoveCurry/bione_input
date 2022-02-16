<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ include file="/common/taglibs.jsp"%>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<%@ include file="/common/meta.jsp"%>
<%@ include file="/common/jquery_load.jsp"%>
<%@ include file="/common/ligerUI_load.jsp"%>
<%@ include file="/common/zTree_load.jsp"%>
<link rel="stylesheet" type="text/css" href="${ctx}/css/classics/template/template2.css" />
<script type="text/javascript">
    var grid = null;
    $(function() {
    	$("#left").height($(window).height() - 2);
		$("#right").height($(window).height() - 2);
		var leftHeight = $("#left").height();
		var rightHeight = $("#right").height();
		var $treeContainer = $("#treeContainer");
    	var mainsearchHeight = $('#mainsearch').height();
		$treeContainer.height(leftHeight - $("#treeSearchbar").height() - 35);
    	$('#content').height(rightHeight - mainsearchHeight - 55);
		$('#navtab1').height(rightHeight - mainsearchHeight - 55);
    });
    (function($) {
		$(".l-dialog-btn").live('mouseover', function() {
		    $(this).addClass("l-dialog-btn-over");
		}).live('mouseout', function() {
		    $(this).removeClass("l-dialog-btn-over");
		});
		$(".l-dialog-tc .l-dialog-close").live('mouseover', function() {
		    $(this).addClass("l-dialog-close-over");
		}).live('mouseout', function() {
		    $(this).removeClass("l-dialog-close-over");
		});
		$(".searchtitle .togglebtn").live('click', function() {
		    var searchbox = $(this).parent().nextAll("div.searchbox:first");
		    var rightHeight = $("#right").height();
		    if ($(this).hasClass("togglebtn-down")) {
				$(this).removeClass("togglebtn-down");
				searchbox.slideToggle('fast', function() {
					var formHeight = $('#formsearch').height(), searchboxWidth = $('#searchbox').width();
				    if (grid) {
				    	grid.setHeight(formHeight - 1);
				    }
					setContentHeight();
				});
		    } else {
				$(this).addClass("togglebtn-down");
				searchbox.slideToggle('fast', function() {
					var formHeight = $('#formsearch').height(), searchboxWidth = $('#searchbox').width();
				    if (grid) {
				    	grid.setHeight(formHeight - 1);
				    }
					setContentHeight();
				});
		    }
		});
    })(jQuery);
</script>
<sitemesh:write property='head' />
</head>
<body>
	<div id="left" style="background-color: #FFFFFF">
		<div id="lefttable" width="100%" border="0">
			<div width="100%"
				style="height:30px;background-image: url(${ctx}/css/classics/ligerUI/Gray/images/ui/gridbar.jpg);border-bottom:1px solid #D6D6D6;">
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
						style="float: right; position: relative; padding-right: 3px; padding-top:4px;">
						<sitemesh:div property='template.left.up.right' />
					</div>
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
							<input id="treesearchtext" type="text" class="l-text-field"  style="width: 100%;padding-left: 0px;" />    
							<div class="l-trigger">                                                                      
								<a id="treeSearchIcon" class = "icon-search" ></a>                                                 
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
	</div>
	<div id="right">
		<div id="mainsearch">
			<div class="searchtitle">
				<a class = "icon-search search-size" ></a> <span>搜索</span>
				<div class="togglebtn">&nbsp;</div>
			</div>
			<!-- <div class="navline" style="margin-bottom: 4px;"></div> -->
			<div id="searchbox" class="searchbox">
				<form id="formsearch">
					<div id="search"></div>
					<div class="l-clear"></div>
				</form>
				<div id="maingrid" class="maingrid"></div>
				<div id="searchbtn" class="searchbtn"></div>
			</div>
		</div>
		<div id="content" class="content">
			<div id="navtab1" style="width: 100%;overflow:hidden; border:1px solid #D6D6D6; ">
				
			</div>
		</div>
	</div>
</body>
</html>
