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
			{ display : "解锁原因", name : "rebutDesc", newline : true, type : "textarea",groupicon : groupicon, validate : { required : true,maxlength : 500}},
			{ name : "taskInstanceId", type : "hidden"}]
		});
		$("#taskInstanceId").val('${taskInstanceId}');
		if("${taskInstanceId}"){
			var taskInstanceIds=[];
			if(window.parent.resultList!=null){
				for(var i in window.parent.resultList){
					taskInstanceIds.push(window.parent.resultList[i].taskInstanceId);
				}
			}
			var taskInstanceId="${taskInstanceId}";
			if(taskInstanceIds.length>0){
				taskInstanceId+=","+taskInstanceIds.join(",");
			}
			$("#taskInstanceId").val(taskInstanceId);
			$("textarea").attr("style", "width: 470px;height: 160px;resize: none;");
			
		}
		jQuery.metadata.setType("attr", "validate");
		BIONE.validate(mainform);
	
		var buttons = [];
		buttons.push({ text : '取消', onclick : cancle});
		buttons.push({ text : '确定', onclick : save});
		BIONE.addFormButtons(buttons);
	});
	
	function save() {
		BIONE.submitForm($("#mainform"), function() {
			if(window.parent.resultList!=null&&window.parent.resultList.length>1){
				BIONE.closeDialogAndReloadParent("applyRejWin", "maingrid", "解锁成功,由于存在相关联的上级任务，已将其一并驳回。");
			}
			else if(window.parent.grid&&window.parent.grid!=null)
				BIONE.closeDialogAndReloadParent("applyRejWin", "maingrid", "解锁成功");
			else BIONE.closeDialog("applyRejWin","解锁成功",true,"close");
		}, function() {
			BIONE.closeDialog("applyRejWin", "解锁失败");
		});
	}
	function cancle() {
		if(window.parent&&window.parent.resultList!=null&&window.parent.resultList.length!=0)
			BIONE.closeDialog("applyRejWin");
		else
			BIONE.closeDialog("applyRejWin","",true,"cancel");
	}
</script>
</head>
<body>
	<div id="template.center">
		<form id="mainform" action="${ctx}/frs/rptfill/reject/saveForce" method="post"></form>
	</div>
</body>
</html>