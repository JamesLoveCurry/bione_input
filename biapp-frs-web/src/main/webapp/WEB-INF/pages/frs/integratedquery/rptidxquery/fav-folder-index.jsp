<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ include file="/common/taglibs.jsp"%>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<%@ include file="/common/meta.jsp"%>
<%@ include file="/common/jquery_load.jsp"%>
<%@ include file="/common/ligerUI_load.jsp"%>
<%@ include file="/common/zTree_load.jsp"%>
<link rel="stylesheet" type="text/css" href="${ctx}/css/classics/template/template12.css" />
<style type="text/css">
#search {
	margin-top: 14px;
}
.searchbox {
	width: inherit;
	text-align: center;
	margin: auto;
	margin-bottom: 5px;
	border-style: solid;
	border-width: 1px;
	border-color: #D6D6D6;
}
</style>
<script type="text/javascript">
	var app = { 
		ctx : '${ctx}',
		folderInfo : '${folderInfo}' ? $.parseJSON('${folderInfo}') : null
	};
</script>
<script type="text/javascript" src="${ctx}/js/uuid/uuid.js" ></script>
<script type="text/javascript" src="${ctx}/frs/js/rptquery/rptquery.js" ></script>
<script type="text/javascript">
	$(function() {
		app.initFavNmForm();
		app.myQuery_iniAsyncTree();
		app.addSaveFavButton();
		app.initContentHeight();
	});
</script>
</head>
<body>
	<div id="center" >
		<div id="mainsearch">
			<div class="searchtitle">
				<%-- <img src="${ctx}/images/classics/icons/icon_favourites.gif" style="display: block; float: left; padding-top: 2px;" />
				<span style="display: block; float: left; margin-left: 3px;">添加收藏</span> --%>
			</div>
			<div id="searchbox" class="searchbox">
				<form id="formsearch">
					<div id="search"></div>
					<div class="l-clear"></div>
				</form>
				<div id="searchbtn" class="searchbtn"></div>
			</div>
		</div>
		<div id="content" class="content" style="border: 1px solid #D6D6D6; ">
			<div id="lefttable" width="100%" border="0">
				<div width="100%"
					style="height:30px;background-image: url(${ctx}/css/classics/ligerUI/Gray/images/ui/gridbar.jpg);border-bottom:1px solid #D6D6D6;">
					<div width="8%"
						style="padding-left: 10px; float: left; position: relative; height: 20p; margin-top: 8px">
						<img src="${ctx}/images/classics/icons/application_side_tree.png" />
					</div>
					<div width="30%">
						<span
							style="font-size: 12; float: left; position: relative; line-height: 30px; padding-left: 2px">
							<span id="treeTitle" style="font-size: 12">创建位置</span>
						</span>
					</div>
					<div width="60%"> 
						<%-- <div style="float: right; position: relative; padding-right: 3px; padding-top:4px; width: 49%; ">
							<div width="70%" class="l-text" style="display: block; float: left; height: 17px; margin-top: 2px; width: 190px;">
								<input id="treesearchtext" name="treesearch" type="text" class="l-text l-text-field" style="float: left; height: 16px; width: 190px;" />
							</div>
							<div width="30%" style="display: block; float: left; margin-left: 8px; margin-top: 3px; margin-right: 3px;">
								<a id="treesearchbtn"><img src="${ctx}/images/classics/icons/find.png" /></a>
							</div>
						</div> --%>
						<div class="l-dialog-btn" style="width: 90px; height: 20px; margin-top: 4px;">
							<div class="l-dialog-btn-inner" style="margin-top: -2px;" onclick="app.newFolder();">新建文件夹</div>
						</div>
					</div>
				</div>
			</div>
			<div id="treeToolbar"
				style="border-bottom-width: 1px; border-bottom-style: solid; border-bottom-color: #D6D6D6;"></div>
			<div id="treeContainer"
				style="width: 100%; overflow: auto; clear: both; background-color: #FFFFFF;">
				<ul id="tree"
					style="font-size: 12; background-color: F1F1F1; width: 90%"
					class="ztree"></ul>
			</div>
		</div>
	</div>
	<div id="bottom">
		<div class="form-bar">
			<div class="form-bar-inner" style="padding-top: 5px"></div>
		</div>
	</div>
</body>
</html>