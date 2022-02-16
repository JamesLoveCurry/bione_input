<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<meta name="decorator" content="/template/template5.jsp">
<head>
<script type="text/javascript">
	//创建表单结构 
	var mainform;
	$(function() {
		mainform = $("#mainform");
		mainform.ligerForm({
			fields : [ 
			{
				display : "校验标识",
				name : "objDefNo",
				newline : true,
				type : "text",
				validate : {
					required : true,
					maxlength : 32,
					remote : "${ctx}/bione/admin/validTypeDef/objDefNoValid",
					messages : {
						remote : "校验标识重复"
					}
				}
			}, {
				display : "校验名称",
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
			}, {
				display : "备注",
				name : "remark",
				newline : true,
				type : "textarea",
				width : 410,
				attr : {
					style : "resize: none;"
				},
				validate : {
					maxlength : 500
				}
			} ]
		});
		
		if("${id}") {
			BIONE.loadForm($("#mainform"), {url : "${ctx}/bione/admin/validTypeDef/${id}"});
			$("#mainform input[name=objDefNo]").attr("readonly","readonly").css("color", "black").removeAttr("validate");
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
			BIONE.closeDialogAndReloadParent("defManage","maingrid" , "保存成功");
		}, function() {
			BIONE.closeDialog("defManage", "保存失败");
		});
	}
	function cancleCallBack() {
		parent.BIONE.closeDialog("resDefManage");
	}
</script>
</head>
<body>
	<div id="template.center">
		<form id="mainform" action="${ctx}/bione/admin/validTypeDef" method="post"></form>
	</div>
</body>
</html>