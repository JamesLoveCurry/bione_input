<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ include file="/common/meta.jsp"%>
<%@ include file="/common/taglibs.jsp"%>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<%@ include file="/common/jquery_load.jsp"%>
<%@ include file="/common/ligerUI_load.jsp"%>
<link rel="stylesheet" type="text/css"
	href="${ctx}/css/classics/template/template1.css" />
<script type="text/javascript">
	var grid = null;
	function templateshow() {
		$("#center").height($("#tab",parent.document).height()-52);
		$("#createTabSql").height($("#center").height()/2 - 40);
		$("#colUpdateSql").height($("#createTabSql").height());
	}
</script>
<sitemesh:write property='head' />
<script type="text/javascript">
    $(function() {
		templateshow();
    });
</script>
</head>
<body>
	<div id="center">
		<div style="color : red; margin-left : 50px;">补录表变更后，请在“补录模板定制”功能重新配置模板。（修改模板时在“字段设置”tab页重新选择补录表，刷新表的字段信息。）</div>
		<form id="formsearch2">
			<div style="padding: 5px 0px 0px 50px;">建表sql:</div>
			<textarea id="createTabSql" readonly="readonly" style="width : 80%; height : 200px;margin: 5px 0px 0px 50px;padding-left:20px;"></textarea>
			<div style="padding: 5px 0px 0px 50px;">字段变更sql<span style="color : red;">（只生成字段信息变更SQL，主键索引变更请执行建表SQL）</span>:</div>
			<textarea id="colUpdateSql" readonly="readonly" style="width : 80%; height : 180px;margin: 5px 0px 0px 50px;padding-left:20px;"></textarea>
		</form>
		<div id="mainsearch" >
			<div id="searchbtn" style="float:right;"></div>
		</div>
	</div>
</body>
</html>