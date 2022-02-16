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
		/* 	labelWidth : 80,
			inputWidth : 160,
			space : 30, */
			fields : [ 
			{
				display : "对象标识",
				name : "objDefNo",
				newline : true,
				type : "text",
				group : "授权对象信息",
				groupicon : groupicon,
				validate : {
					required : true,
					maxlength : 32,
					remote : "${ctx}/bione/admin/authObjDef/objDefNoValid",
					messages : {
						remote : "对象标识重复"
					}
				}
			}, {
				display : "对象名称",
				name : "objDefName",
				newline : false,
				type : "text",
				validate : {
					required : true,
					maxlength : 100
				}
			}, {
				display : "实现类名称",
				name : "beanName",
				newline : true,
				type : "text",
				validate : {
					required : true,
					maxlength : 100
				}
			},{
				display : "备注",
				name : "remark",
				newline : true,
				type : "textarea",
				width : 493,
				attr : {
					style : "resize: none;"
				},
				validate : {
					maxlength : 500
				}
			} ]
		});
		if("${id}") {
			BIONE.loadForm($("#mainform"), {url : "${ctx}/bione/admin/authObjDef/${id}.json"});
			$("#mainform input[name=objDefNo]").attr("readonly", "true").css("color", "black").removeAttr("validate");
		}
		jQuery.metadata.setType("attr", "validate");
		BIONE.validate(mainform);
		var buttons = [];
		buttons.push({
			text : '取消',
			onclick : cancleCallBack
		});
		buttons.push({
			text : '保存',
			onclick : save_objDef
		});
		BIONE.addFormButtons(buttons);

	});
	function save_objDef() {
		BIONE.submitForm($("#mainform"), function() {
			BIONE.closeDialogAndReloadParent("objDefManage", "maingrid", "保存成功");
		}, function() {
			BIONE.closeDialog("objDefManage", "保存失败");
		});
	}
	function cancleCallBack() {
		BIONE.closeDialog("objDefManage");
	}
</script>
</head>
<body>
	<div id="template.center">
		<form id="mainform" action="${ctx}/bione/admin/authObjDef/" method="post"></form>
	</div>
</body>
</html>