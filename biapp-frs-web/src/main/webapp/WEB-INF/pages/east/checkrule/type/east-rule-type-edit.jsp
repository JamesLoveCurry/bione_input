<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<meta name="decorator" content="/template/template5.jsp">

<script type="text/javascript">
	var mainform;
	
	$(function() {
		initForm();
		initBtn();
	});
	
	function initForm(){
		mainform = $("#form1").ligerForm({
			labelWidth : 100,
			inputWidth : 280,
			fields : [{
				name : 'typeId',
				type : 'hidden'
			}, {
			    display : "规则类型",
				name : "typeName",
				newline : true,
				type : "text",
				cssClass : "field",
				group : "基本信息", 
				groupicon : "${ctx}/images/classics/icons/communication.gif"
			},{
				display:"检核逻辑说明",
				name : "typeDesc",
				newline : true,
				type:"textarea",
				cssClass:"field"
			}]
		});
		
		$("#form1 input[name=typeName]").attr("readonly", "true").removeAttr("validate");
	}

	BIONE.loadForm(mainform, {
	    url : "${ctx}/east/rules/checkRule/type/${typeName}"
	});

	jQuery.metadata.setType("attr", "validate");
	BIONE.validate($("#form1"));

	var managers = $.ligerui.find($.ligerui.controls.Input);
	for ( var i = 0, l = managers.length; i < l; i++) {
	    //改变了表单的值，需要调用这个方法来更新ligerui样式
	    managers[i].updateStyle();
	}
	
	//初始化按钮
	function initBtn(){
		var buttons = [];
		buttons.push({
		    text : '取消',
		    onclick : function() {
				BIONE.closeDialog("editRuleType", null);
		    }
		});
		buttons.push({
		    text : '修改',
		    onclick : save
		});
		BIONE.addFormButtons(buttons);
	}
	
	//保存按钮-任务保存方法
	function save() {
		BIONE.submitForm($("#form1"), function() {
		    BIONE.closeDialogAndReloadParent("editRuleType", "maingrid", "修改成功");
		}, function() {
		    BIONE.closeDialog("editRuleType", "修改失败");
		});
	}
</script>
</head>
<body>
	<div id="template.center">
		<form name="form1" method="post" id="form1"
			action="${ctx}/east/rules/checkRule/type"></form>
	</div>
</body>
</html>