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
<script type="text/javascript" >
	var app = { 
		ctx : '${ctx}',
		folderInfo : '${folderInfo}' ? $.parseJSON('${folderInfo}'):null
	};
</script>
<script type="text/javascript" src="${ctx}/js/uuid/uuid.js" ></script>
<script type="text/javascript" src="${ctx}/frs/js/rptquery/rptquery.js" ></script>
<script type="text/javascript" >
	$(function() {
		app.initFavNmForm();
		app.addSaveFolderButton();
		app.folder_addHotKey();
		$('#queryNm').closest('li').siblings(':first').text('文件夹名称：');
	});
</script>
</head>
<body>
	<div id="center" >
		<div id="mainsearch">
			<div class="searchtitle">
				<%-- <img src="${ctx}/images/classics/icons/icon_favourites.gif" style="display: block; float: left; padding-top: 2px;" />
				<span style="display: block; float: left; margin-left: 3px;">新建文件夹</span> --%>
			</div>
			<div id="searchbox" class="searchbox">
				<form id="formsearch">
					<div id="search"></div>
					<div class="l-clear"></div>
				</form>
				<div id="searchbtn" class="searchbtn"></div>
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