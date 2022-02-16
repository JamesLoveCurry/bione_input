<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<meta name="decorator" content="/template/template5.jsp">

<script type="text/javascript">
	var mainform = null;
	
	$(function() {
		initForm();
		initBtn();
	});
	
	function initForm(){
		mainform = $("#mainform").ligerForm({
			labelWidth : 100,
			inputWidth : 280,
			fields : [{
				name : 'bloodId',
				type : 'hidden'
			}, {
				display : "业务表",
				name : "tabName",
				newline : true,
				type : "text",
				cssClass : "field",
				group : "基本信息", 
				groupicon : "${ctx}/images/classics/icons/communication.gif",
			},{
				display:"来源生产系统",
				name : "fromSys",
				newline : true,
				type:"select",
				cssClass:"field",
				options:{
					data:[{
						text:"CRM系统",
						id : "CRM系统"
					},{
						text:"HR系统",
						id : "HR系统"
					},{
						text:"信贷系统",
						id : "信贷系统"
					},{
						text:"风险管理系统",
						id : "风险管理系统"
					},{
						text:"合规系统",
						id : "合规系统"
					},{
						text:"审计系统",
						id : "审计系统"
					}]
				}
			},{
				display:"生产系统表名",
				name : "fromTable",
				newline : true,
				type : "text",
				cssClass : "field"
			},{
				display:"生产系统相关表",
				name : "fromTable2",
				newline : true,
				type : "textarea",
				cssClass : "field"
			},{
				display:"ETL名称",
				name : "etlName",
				newline : true,
				type : "text",
				cssClass : "field"
			},{
				display:"ETL逻辑说明",
				name : "etlDescr", 
				newline : true,
				type : "textarea",
				cssClass : "field"
			},{
				display:"备注",
				name : "remark",
				newline : true,
				type : "textarea",
				cssClass : "field"
			}]
		});
	}

	//初始化按钮
	function initBtn(){
		var buttons = [];
		buttons.push({
		    text : '取消',
		    onclick : function() {
				BIONE.closeDialog("editTab", null);
		    }
		});
		buttons.push({
		    text : '保存',
		    onclick : save
		});
		BIONE.addFormButtons(buttons);
		
		jQuery.metadata.setType("attr", "validate");
		BIONE.validate("#mainform");
	}
	
	if("${id}") {
		BIONE.loadForm($("#mainform"), {url : "${ctx}/east/business/blood/${id}.json"});
		//$("#mainform input[name=id]").attr("readOnly", "true").removeAttr("validate");
	}
	
	
	//保存按钮-任务保存方法
	function save() {
		BIONE.submitForm($("#mainform"), function() {
		    BIONE.closeDialogAndReloadParent("editTab", "maingrid", "保存成功");
		}, function() {
		    BIONE.closeDialog("editTab", "修改失败");
		});
	}
</script>
</head>
<body>
	<div id="template.center">
		<form method="post" id="mainform"
			action="${ctx}/east/business/blood/save" method="post"></form>
	</div>
</body>
</html>