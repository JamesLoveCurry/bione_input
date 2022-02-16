<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<meta name="decorator" content="/template/template5.jsp">
<head>
<script type="text/javascript">
	var groupicon = "${ctx}/images/classics/icons/communication.gif";
	//创建表单结构 
	var mainform;
	var taskInstanceId;
	var rebutId;
	$(function() {
		mainform = $("#mainform");
		mainform.ligerForm({
			fields : [ 
			{ display : "审批状态", name : "sts", newline : true, type : "select", group : "审批解锁信息", groupicon : groupicon, validate : { required : true}, comboboxName : "collateSts_sel", options : { initValue: "1",data : [{ text : '同意', id : "1"}, { text : '不同意', id : "2"}]}},
			{ display : "错误来源", name : "errorRes", newline : true, type : "select", validate : { required : true}, comboboxName : "errorRes_sel", options : { initValue: "02",data : [{ text : '监管发现', id : "01"}, { text : '行内发现', id : "02"}]}},
			{ display : "审批原因", name : "rebutDesc", newline : true, type : "textarea", validate : { required : true,maxlength : 500}},
			{ name : "rebutId", type : "hidden"},
			{ name : "taskInstanceId", type : "hidden"}]
		});
		
		if("${rebutId}" && "${taskInstanceId}"){
			if(window.parent.grid&&window.parent.grid.rows){
				var taskInstanceIds=[];
				if(window.parent.resultList!=null){
					for(var i in window.parent.resultList){
						var results = window.parent.resultList[i].resultList;
						for(var j in results){
							taskInstanceIds.push(results[j].taskInstanceId); 
						}
					}
				}
				taskInstanceId="${taskInstanceId}".split(",");
				rebutId="${rebutId}".split(",");
				for(var i = 0; i < taskInstanceIds.length; i++){
					if($.inArray(taskInstanceIds[i],taskInstanceId) == -1){
						taskInstanceId.push(taskInstanceIds[i]);
					}
				}
				var rows = window.parent.grid.rows;
				for(var row in rows){
					for(var i = 0; i < taskInstanceIds.length; i++){
						if(rows[row].collateSts == 0 && rows[row].taskInstanceId == taskInstanceIds[i]){
							if($.inArray(rows[row].rebutId,rebutId) == -1){
								rebutId.push(rows[row].rebutId);
							}
						}
					}
				} 
				$("#rebutId").val(rebutId.join(","));
				$("#taskInstanceId").val(taskInstanceId.join(","));
				$("#collateSts").val($.ligerui.get('collateSts_sel').getValue());
				$("#errorRes").val($.ligerui.get('errorRes_sel').getValue());
				$("textarea").attr("style", "width: 318px;height: 130px;resize: none;");
			}else{
				taskInstanceId='${taskInstanceId}';
				rebutId='${rebutId}';
				$("#rebutId").val(rebutId);
				$("#taskInstanceId").val(taskInstanceId);
				$("#collateSts").val($.ligerui.get('collateSts_sel').getValue());
				$("#errorRes").val($.ligerui.get('errorRes_sel').getValue());
				$("textarea").attr("style", "width: 318px;height: 130px;resize: none;");
			}
			
		}
		jQuery.metadata.setType("attr", "validate");
		BIONE.validate(mainform);
	
		var buttons = [];
		buttons.push({ text : '取消', onclick : cancle});
		buttons.push({ text : '审批', onclick : save});
		BIONE.addFormButtons(buttons);
	});
	function save() {
		if(window.parent.grid&&window.parent.grid.rows){
			if($.ligerui.get('collateSts_sel').getValue() == "1"){
				$("#rebutId").val(rebutId.join(","));
				$("#taskInstanceId").val(taskInstanceId.join(","));
			}else{
				$("#rebutId").val("${rebutId}");
				$("#taskInstanceId").val("${taskInstanceId}");
			}
		}else{
			$("#rebutId").val("${rebutId}");
			$("#taskInstanceId").val("${taskInstanceId}");
		}
		BIONE.submitForm($("#mainform"), function() {
			if(window.parent.resultList!=null&&($("#taskInstanceId").val().split(",").length>"${rebutId}".split(",").length)){
				BIONE.closeDialogAndReloadParent("approveRejWin", "maingrid", "审批成功,由于存在相关联的上级任务，已将其一并驳回。");
			}
			else if(window.parent.grid&&window.parent.grid!=null)
				BIONE.closeDialogAndReloadParent("approveRejWin", "maingrid", "审批成功");
			else 
				BIONE.closeDialog("approveRejWin","解锁成功",true,"close");
		}, function() {
			BIONE.closeDialog("approveRejWin", "审批失败");
		});
	}
	function cancle() {
		if(window.parent&&window.parent.resultList!=null)
			BIONE.closeDialog("approveRejWin");
		else
			BIONE.closeDialog("approveRejWin","",true,"cancel");
	}
</script>
</head>
<body>
	<div id="template.center">
		<form id="mainform" action="${ctx}/frs/rptfill/reject/saveApprove" method="post"></form>
	</div>
</body>
</html>