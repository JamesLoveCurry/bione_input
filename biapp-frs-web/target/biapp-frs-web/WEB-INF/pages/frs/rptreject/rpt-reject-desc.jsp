<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<meta name="decorator" content="/template/template5.jsp">
<head>
<script type="text/javascript">
	var groupicon = "${ctx}/images/classics/icons/communication.gif";
	//创建表单结构 
	var mainform;
	$(function() {
		mainform = $("#mainform");
		mainform.ligerForm({
			fields : [ 
			{  name : "applyDesc", newline : true, type : "textarea", groupicon : groupicon, validate : { required : true,maxlength : 500}},
			{ name : "taskInstanceId", type : "hidden"}]
		});
		if("${taskInstanceId}"){
			$("#taskInstanceId").val("${taskInstanceId}");
			$("textarea").attr("style", "width: 470px;height: 200px;resize: none;");
		}
		jQuery.metadata.setType("attr", "validate");
		BIONE.validate(mainform);
		var buttons = [];
		buttons.push({ text : '取消', onclick : cancle});
		buttons.push({ text : '申请', onclick : save});
		BIONE.addFormButtons(buttons);
	});
	function save() {
		BIONE.submitForm($("#mainform"), function() {
			BIONE.closeDialogAndReloadParent("applyRejWin", "申请失败",true,"close");
		}, function() {
			BIONE.closeDialog("applyRejWin", "申请失败",true,"close");
		});
	}
	function cancle() {
		BIONE.closeDialog("applyRejWin", "",true,"close");
	}
</script>
</head>
<body>
	<div id="template.center">
		<form id="mainform" action="${ctx}/frs/rptfill/reject/saveApply" method="post"></form>
	</div>
</body>
</html>