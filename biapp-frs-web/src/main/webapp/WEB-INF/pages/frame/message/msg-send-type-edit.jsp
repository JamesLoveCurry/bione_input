<%@ page language="java" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<meta name="decorator" content="/template/template5.jsp">
<head>
<script type="text/javascript">
	var groupicon = "${ctx}/images/classics/icons/communication.gif";
	//创建表单结构 
	var mainform;
	$(function() {
		mainform = $("#mainform").ligerForm({
			inputWidth: 200, labelWidth: 90, space: 40,
			
			fields : [ 
			{ 
				display : "发送类型标识",
				newline : true,
				name: "sendTypeNo",
				group : "消息发送类型",
				groupicon : groupicon,
				width : 430,
				type: "text",
				validate : {
					required : true,
					remote : "${ctx}/bione/msgSendType/checkTypeNo",
					messages : {
						remote:"该发送类型名称已存在"
					},
					maxlength : 32
				}
			},
			{
				display : "发送类型名称",
				name : "sendTypeName",
				newline : true,
				type : "text",
				width : 430,
				validate : {
					required : true,
					remote : "${ctx}/bione/msgSendType/checkTypeName?id=${id}",
					messages : {
						remote:"该发送类型名称已存在"
					},
					maxlength : 100
				}
			}, {
				display : "实现类名称",
				name : "beanName",
				newline : true,
				type : "text",
				width : 430,
				validate : {
					required : true,
					remote : "${ctx}/bione/msgSendType/checkClassName",
					messages : {
						remote:"该类不存在或者没有继承IMessager接口"
					},
					maxlength : 100
				}
			}, {
				display : "备注",
				name : "remark",
				newline : true,
				type : "textarea",
				width:432,
				validate : {
					maxlength : 500
				}
			} ]
		});
		if ("${id}") {
			BIONE.loadForm($("#mainform"), {
				url : "${ctx}/bione/msgSendType/getInfo.json?id=${id}"
			});
			$("#mainform input[name=sendTypeNo]").attr("readOnly", "true")
					.css("color", "black").removeAttr("validate");
		}
		jQuery.metadata.setType("attr", "validate");
		BIONE.validate($("#mainform"));
		
		var buttons = [];
		buttons.push({
			text : '取消',
			onclick : cancleCallBack,
			operNo : 'var_add'
		});
		buttons.push({
			text : '保存',
			onclick : save_resDef,
			operNo : 'var_add'

		});
		BIONE.addFormButtons(buttons);

	});
	function save_resDef() {
		BIONE.submitForm($("#mainform"), function() {
			BIONE.closeDialogAndReloadParent("msgTypeManage", "maingrid", "保存成功");
		}, function() {
			BIONE.closeDialog("msgTypeManage", "保存失败");
		});
	}
	function cancleCallBack() {
		BIONE.closeDialog("msgTypeManage");
	}
</script>
</head>
<body>
	<div id="template.center">
		<form id="mainform" action="${ctx}/bione/msgSendType" method="post"></form>
	</div>
</body>
</html>