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
				name : 'colNo',
				type : 'hidden'
			}, {
				display : "表名",
				name : "tabName",
				newline : true,
				type : "text",
				cssClass : "field",
				group : "基本信息", 
				groupicon : "${ctx}/images/classics/icons/communication.gif"
			},{
				display:"字段名",
				name : "colName",
				newline : true,
				type:"text",
				cssClass:"field"
			},{
				display:"字段名（英文）",
				name : "colNameEn",
				newline : true,
				type:"text",
				cssClass:"field"
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
		
		$("#form1 input[name=tabName]").attr("readonly", "true").removeAttr("validate");
		$("#form1 input[name=colName]").attr("readonly", "true").removeAttr("validate");
	}

	BIONE.loadForm(mainform, {
	    url : "${ctx}/east/rules/business/col/${tabName}/${colName}"
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
				BIONE.closeDialog("editCol", null);
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
		    BIONE.closeDialogAndReloadParent("editCol", "maingrid", "修改成功");
		}, function() {
		    BIONE.closeDialog("editCol", "修改失败");
		});
	}
</script>
</head>
<body>
	<div id="template.center">
		<form name="form1" method="post" id="form1"
			action="${ctx}/east/rules/business/col"></form>
	</div>
</body>
</html>