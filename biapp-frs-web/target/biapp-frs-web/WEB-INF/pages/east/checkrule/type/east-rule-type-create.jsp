<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<meta name="decorator" content="/template/template5.jsp">

<script type="text/javascript">

	$(function() {
		initForm();
		initBtn();
	});
	
	function initForm(){
		$("#mainform").ligerForm({
			labelWidth : 100,
			inputWidth : 280,
			fields : [{
				display : "规则类型",
				name : "typeName",
				newline : true,
				type : "text",
				cssClass : "field",
				group : "基本信息", 
				groupicon : "${ctx}/images/classics/icons/communication.gif",
				validate : {
					required : true
				}
			},{
				display:"检核逻辑说明",
				name : "typeDesc",
				newline : true,
				type:"textarea",
				cssClass:"field",
				validate : {
					required : true
				}
			}]
		});
	}

	//初始化按钮
	function initBtn(){
		var buttons = [];
		buttons.push({
		    text : '取消',
		    onclick : function() {
				BIONE.closeDialog("addRuleType", null);
		    }
		});
		buttons.push({
		    text : '保存',
		    onclick : save
		});
		BIONE.addFormButtons(buttons);
	}
	
	//保存按钮-任务保存方法
	function save() {
		if ($("#typeName").val() == "") {
			BIONE.tip("规则类型不能为空，请修改!");
			return;
		}

		if ($("#typeDesc").val() == "") {
			BIONE.tip("检核逻辑说明不能为空，请修改!");
			return;
		}
		BIONE.submitForm($("#mainform"), function() {
		    BIONE.closeDialogAndReloadParent("addRuleType", "maingrid", "添加成功");
		}, function() {
		    BIONE.closeDialog("editRuleType", "添加失败");
		});
	}
</script>
</head>
<body>
	<div id="template.center">
		<form name="mainform" method="post" id="mainform"
			action="${ctx}/east/rules/checkRule/type/save"></form>
	</div>
</body>
</html>