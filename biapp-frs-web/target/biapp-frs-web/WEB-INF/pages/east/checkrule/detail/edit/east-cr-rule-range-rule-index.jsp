<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<meta name="decorator" content="/template/template5.jsp">
<head>
<script type="text/javascript">
	var dialog,grid,data,ruleId,typeCd;
	var mainform;
	
	$(function() {
		init();
		initForm();
		initBtn();
	});
	
	function init() {
		ruleId = ${ruleId};
		typeCd = "${typeCd}";
	}
	
	function initForm(){
		mainform = $("#form1").ligerForm({
			labelWidth : 100,
			inputWidth : 280,
			fields : [{
				name : "dispCd",
				type : "hidden"
			}, {
				display : "规则标识",
				name : "ruleId",
				newline : true,
				type : "text",
				cssClass : "field",
				group : "基本信息", 
				groupicon : "${ctx}/images/classics/icons/communication.gif"
			},{
				display : "检验类型",
				name : "typeCd",
				newline : true,
				type : "text",
				cssClass : "field"
			},{
				display : "检验名称",
				name : "ruleName",
				newline : true,
				type : "text",
				cssClass : "field",
				validate : {
					required : true
				}
			},{
				display : "表名",
				name : "tabName",
				newline : true,
				type : "text",
				cssClass : "field"
			},{
				display : "字段名",
				name : "colName",
				newline : true,
				type : "text",
				cssClass : "field"
			},{
				display : "是否启用",
				name : "ruleSts",
				newline : true,
				type : "select",
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
				display:"前置条件",
				name : "cond",
				newline : true,
				type:"textarea",
				cssClass:"field"
			},{
				display : "限制类型",
				name : "typeCdRange",
				newline : true,
				type : "select",
				cssClass : "field",
				group : "其他信息", 
				groupicon : "${ctx}/images/classics/icons/communication.gif",
				options:{
					data:[{
						text:"区间",
						id : "区间"
					},{
						text:"枚举",
						id : "枚举"
					},{
						text:"字典表",
						id : "字典表"
					}]
				}
			},{
				display : "是否多值",
				name : "multiVal",
				newline : true,
				type : "select",
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
				display : "多值时分隔符",
				name : "sep",
				newline : true,
				type : "text",
				cssClass : "field"
			},{
				display : "最大值",
				name : "maxVal",
				newline : true,
				type : "number",
				cssClass : "field"
			},{
				display : "最大值区间",
				name : "maxRange",
				newline : true,
				type : "text",
				cssClass : "field"
			},{
				display : "最小值",
				name : "minVal",
				newline : true,
				type : "number",
				cssClass : "field"
			},{
				display : "最小值区间",
				name : "minRange",
				newline : true,
				type : "text",
				cssClass : "field"
			},{
				display : "枚举值",
				name : "rangeCd",
				newline : true,
				type : "text",
				cssClass : "field"
			},{
				display : "字典视图",
				name : "dicView",
				newline : true,
				type : "textarea",
				cssClass : "field"
			}]
		});
		$("#form1 input[name=ruleId]").attr("readonly", "true").removeAttr("validate");
		$("#form1 input[name=typeCd]").attr("readonly", "true").removeAttr("validate");
		$("#form1 input[name=tabName]").attr("readonly", "true").removeAttr("validate");
		$("#form1 input[name=colName]").attr("readonly", "true").removeAttr("validate");
	}
	
	BIONE.loadForm(mainform, {
	    url : "${ctx}/east/rules/checkRule/range/${ruleId}"
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
				BIONE.closeDialog("editRule", null);
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
		BIONE.submitForm($("#form1"), function() {
		    BIONE.closeDialogAndReloadParent("editRule", "maingrid", "保存成功");
		}, function() {
		    BIONE.closeDialog("editRule", "保存失败");
		});
	}
</script>
</head>
<body>
	<div id="template.center">
		<form name="form1" method="post" id="form1"
			action="${ctx}/east/rules/checkRule/range/edit"></form>
	</div>
</body>
</html>