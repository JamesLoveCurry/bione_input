<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<meta name="decorator" content="/template/template24_BS.jsp">
<head>
<style type="text/css">
.searchbox {
	margin-top: 5px;
}
.searchtitle img {
	display: block;
	float: left;
}
.searchtitle span {
	display: block;
	float: left;
	margin-top: 1px;
	margin-left: 2px;
	line-height: 19px;
}
.query {
 	color: #0071D7;
}
</style>
<script type="text/javascript">
	var app = { 
		ctx : '${ctx}', 
		folderInfo : '${folderInfo}'? $.parseJSON('${folderInfo}') : null
	};
	var grid = null;
</script>
<script type="text/javascript" src="${ctx}/frs/js/rptquery/rptquery.js" ></script>
<script type="text/javascript">
	$.extend(app, {
		groupicon : app.ctx + '/images/classics/icons/communication.gif'
	});
	$(function() {
		$('div.searchtitle').hide();
		app.myQuery_initGrid();
		app.myQuery_addSearchForm();
		app.myQuery_iniAsyncTree();
		app.myQuery_addDomEvent();
		app.myQuery_treeToolbar();
	});
</script>
</head>
<body>
	<div id="template.left.up.icon">
		<a class = "icon-guide "></a>
	</div>
	<div id="template.left.up">
		<span id="treeTitle" style="font-size: 12">目录</span>
	</div>
	<div id="template.left.up.right">
		<div width="130" class="l-text" style="display: block; float: left; height: 17px; margin-top: 2px; width: 127px;">
			<input id="treesearchtext" name="treesearch" type="text" class="l-text l-text-field" style="float: left; height: 16px; width: 127px;" />
		</div>
		<div width="30%" style="display: block; float: left; margin-left: 8px; margin-top: 3px; margin-right: 3px;">
			<a id="treesearchbtn" class = "icon-search search-size"></a>
		</div>
	</div>
</body>
</html>