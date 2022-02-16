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
			labelWidth:'110',
			inputWidth:'250',
			fields : [{
				display : "流程中文名称",
				name : "name",
				newline : true,
				type : "text",
				validate : {
					required : true,
					//maxlength : 32
				}
			}/* , {
				display : "流程唯一标识(英文)",
				name : "key",
				newline : true,
				type : "text",
			} */,{
				display : "流程描述",
				name : "description",
				newline : true,
				type : "textarea",
			}]
		});
		jQuery.metadata.setType("attr", "validate");
		BIONE.validate(mainform); 
		var buttons = [];
		buttons.push({
			text : '取消',
			onclick : cancleCallBack
		});
		buttons.push({
			text : '创建',
			onclick : save_module
		});
		BIONE.addFormButtons(buttons);
		
	});
	function save_module() {
		$('#mainform').submit();
		setTimeout(function() {
			BIONE.closeDialogAndReloadParent("moduleUpdate", "maingrid", "保存成功");
        }, 1000);
	}

	function cancleCallBack() {
		BIONE.closeDialog("moduleUpdate");
	}
</script>
</head>
<body>
	<div id="template.center">
		<form id="mainform" action="${ctx}/frs/activiti/create" method="post" target="_blank"></form>
	</div>
</body>
</html>