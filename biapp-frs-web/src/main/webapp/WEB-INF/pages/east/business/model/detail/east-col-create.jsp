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
				display : "表名",
				name : "tabName",
				newline : true,
				cssClass : "field",
				type : 'text',
				group : "基本信息", 
				groupicon : "${ctx}/images/classics/icons/communication.gif",
				validate : {
					required : true
				}
			},{
				display:"字段名",
				name : "colName",
				newline : true,
				type:"text",
				cssClass:"field",
				validate : {
					required : true
				}
			},{
				display:"字段名（英文）",
				name : "colNameEn",
				newline : true,
				type:"text",
				cssClass:"field",
				validate : {
					required : true
				}
			},{
				display:"是否是主键",
				name : "isPk",
				comboboxName:"tabIsPkBox",
				newline : true,
				type:"select",
				cssClass:"field",
				options:{
					data:[{
						text:"是",
						id : "Y"
					},{
						text:"否",
						id : "N"
					}]
				},
				validate : {
					required : true
				}
			},{
				display:"过滤序号",
				name : "filterNo",
				newline : true,
				type:"number",
				cssClass:"field"
			},{
				display:"流程序号",
				name : "flowNo",
				newline : true,
				type:"number",
				cssClass:"field"
			}]
		});

		$("#mainform input[name=tabName]").val("${tabName}");
		$("#mainform input[name=tabName]").attr("readonly", "true").removeAttr("validate");
	}

	jQuery.metadata.setType("attr", "validate");
	BIONE.validate($("#mainform"));

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
				BIONE.closeDialog("addCol", null);
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
		if ($("#colName").val() == "") {
			BIONE.tip("字段名不能为空，请修改!");
			return;
		}
		if ($("#colNameEn").val() == "") {
			BIONE.tip("字段名（英文）不能为空，请修改!");
			return;
		}
		if ($("#isPk").val() == "") {
			BIONE.tip("是否是主键不能为空，请修改!");
			return;
		}
		BIONE.submitForm($("#mainform"), function() {
		    BIONE.closeDialogAndReloadParent("addCol", "maingrid", "添加成功");
		}, function() {
		    BIONE.closeDialog("editCol", "添加失败");
		});
	}
</script>
</head>
<body>
	<div id="template.center">
		<form name="mainform" method="post" id="mainform"
			action="${ctx}/east/rules/business/col/save"></form>
	</div>
</body>
</html>