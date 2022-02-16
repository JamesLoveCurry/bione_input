<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
<%@ include file="/common/meta.jsp"%>
<%@ include file="/common/jquery_load.jsp"%>
<%@ include file="/common/ligerUI_load.jsp"%>
<style type="text/css">
.l-dialog-win .l-dialog-content {
	overflow: hidden;
}
</style>
<script type="text/javascript">
$(function() {
	var buttons = [];
	buttons.push({
		text : '确定',
		onclick : onOK
	});
	BIONE.addFormButtons(buttons);
	var height = $(document).height() - 100;
	$("#mainform").height(height);
});
function onOK() {
	parent.setCheckOption($('#emptyCellInFormula').prop('checked'),
			$('#emptyIndexInFormula').prop('checked'),
			$('#emptyCellInVerifyWarn').prop('checked'),
			$('#fromSystemRptCfg').prop('checked'));
}
</script>
</head>
<body style="padding: 20px;">
	<form id="mainform" method="post">
		<ul>
			<li style="margin: 10px"><input id="emptyCellInFormula" type="checkbox" checked/>允许报表公式和校验公式中引用空单元格或一般单元格</li>
			<li style="margin: 10px"><input id="emptyIndexInFormula" type="checkbox" checked />允许报表公式和校验公式中引用空指标单元格</li>
			<li style="margin: 10px"><input id="emptyCellInVerifyWarn" type="checkbox" checked />允许对空单元格或一般单元格进行预警值校验</li>
			<!-- 功能未测试，先不开放使用 -->
			<li style="margin: 10px"><input id="fromSystemRptCfg" type="checkbox" />沿用现有报表指标配置导入(模型配置无法沿用)</li>
		</ul>
	</form>
	<div id="bottom">
		<div class="form-bar">
			<div class="form-bar-inner" style="padding-top:5px;padding-right:20px"></div>
		</div>
	</div>
</body>
</html>