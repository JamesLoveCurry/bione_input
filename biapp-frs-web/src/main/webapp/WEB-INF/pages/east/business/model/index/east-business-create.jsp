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
				type : "text",
				cssClass : "field",
				group : "基本信息", 
				groupicon : "${ctx}/images/classics/icons/communication.gif",
				validate : {
					required : true
				}
			},{
				display:"表名（英文）",
				name : "tabNameEn",
				newline : true,
				type:"text",
				cssClass:"field",
				validate : {
					required : true
				}
			},{
				display:"是否公共表",
				name : "isCommon",
				comboboxName:"tabIsCommonBox",
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
			}]
		});
	}

	//初始化按钮
	function initBtn(){
		var buttons = [];
		buttons.push({
		    text : '取消',
		    onclick : function() {
				BIONE.closeDialog("addTab", null);
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
		if ($("#tabName").val() == "") {
			BIONE.tip("表名不能为空，请修改!");
			return;
		}
		if ($("#tabNameEn").val() == "") {
			BIONE.tip("表名（英文）不能为空，请修改!");
			return;
		}
		if ($("#isCommon").val() == "") {
			BIONE.tip("是否公共表不能为空，请修改!");
			return;
		}
		
		BIONE.submitForm($("#mainform"), function() {
		    BIONE.closeDialogAndReloadParent("addTab", "maingrid", "添加成功");
		}, function() {
		    BIONE.closeDialog("editTab", "添加失败");
		});
	}
</script>
</head>
<body>
	<div id="template.center">
		<form name="mainform" method="post" id="mainform"
			action="${ctx}/east/rules/business/tab/save"></form>
	</div>
</body>
</html>