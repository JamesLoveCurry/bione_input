<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<meta name="decorator" content="/template/template14.jsp">
<script type="text/javascript">
	$(function() {
		initForm();
	});
	
	//创建表单结构 
	function initForm() {
		var field = [ {
			display : "报表指标导出方式",
			name : 'exportIdxType',
			type : "select",
			newline : false,
			width : 170,
			options : {
				initValue : "N",
				data : [ {
					text : '全部导为空指标',
					id : 'Y'
				}, {
					text : '导出实际配置信息',
					id : 'N'
				} ]
			}
		}];
		
		var mainform = $("#mainform").ligerForm({
			inputWidth : 130,
			labelWidth : 130,
			space : 40,
			fields : field
		});
		
		var buttons = [];
		buttons.push({
			text : '确定',
			onclick : onConfirm
		});
		BIONE.addFormButtons(buttons);
	}
	
	function onConfirm() {
		var exportIdxType = $.ligerui.get("exportIdxType").getValue();
		parent.start_export(exportIdxType);
		BIONE.closeDialog("exportSystem");
	}
</script>
</head>
<body>
	<div id="template.center">
		<form name="mainform" id="mainform"></form>
		<div id="bottom">
			<div class="form-bar">
				<div class="form-bar-inner" style="padding-top:190px;padding-right:20px"></div>
			</div>
		</div>
	</div>
</body>
</html>