<%@ page language="java" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<meta name="decorator" content="/template/template5.jsp">
<head>
<script type="text/javascript">
	var mainform;
	$(function() {
		var groupicon = "${ctx}/images/classics/icons/communication.gif";

		mainform = $("#mainform").ligerForm({
			align : "centre",
			fields : [ {
				name : "authTypeNo",
				type : "hidden"
			}, {
				display : "认证类型名称",
				name : "authTypeName",
				newline : true,
				type : "text",
				group : "认证类型",
				groupicon : groupicon,
				validate : {
					required : true,
					maxlength : 32,
					remote : "${ctx}/bione/admin/logicSys/checkLogicSysNo",
					messages : {
						remote : "该逻辑系统已存在"
					}
				}
			}, {
				display : "实现类",
				name : "beanName",
				newline : true,
				type : "text",
				validate : {
					required : true
				}
			},{
				display : "备注",
				name : "remark",
				newline : true,
				type : "textarea",
				width : 493,
				attr : {
					style : "resize: none;"
				}
			} ]
		});

		var buttons = [];
		buttons.push({
			text : '取消',
			onclick : f_close
		});
		buttons.push({
			text : '保存',
			onclick : f_save
		});
		BIONE.addFormButtons(buttons);

		jQuery.metadata.setType("attr", "validate");
		BIONE.validate("#mainform");

		// id 存在为修改, id 不存在为新增
		if ("${id}") {
			BIONE.loadForm(mainform, {
				url : "${ctx}/bione/admin/authConfig/${id}"
			});
		} else {

		}
	});

	function f_save() {
		BIONE.submitForm($("#mainform"),
				function() {
					BIONE.closeDialogAndReloadParent("authTypeWin", "maingrid",
							"保存成功");
				}, function() {
					BIONE.closeDialog("logicSysWin", "保存失败");
				});
	}

	function f_close() {
		BIONE.closeDialog("authTypeWin");
	}
</script>
</head>
<body>
	<div id="template.center">
		<form action="${ctx}/bione/admin/authConfig" method="post" id="mainform"></form>
	</div>
</body>
</html>