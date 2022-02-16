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
				name : "logicSysId",
				type : "hidden"
			}, {
				name : "isBuiltin",
				type : "hidden"
			}, {
				display : "逻辑系统缩写",
				name : "logicSysNo",
				newline : true,
				type : "text",
				group : "逻辑系统",
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
				display : "逻辑系统名称",
				name : "logicSysName",
				newline : false,
				type : "text",
				validate : {
					required : true
				}
			}, {
				display : "认证方式",
				name : "authTypeNo",
				newline : true,
				type : "select",
				options : {
					url : "${ctx}/bione/admin/authInfo/findForCombo.json",
					ajaxType : "get"
				},
				validate : {
					required : true
				}
			}, {
				display : "是否启用",
				name : "logicSysSts",
				newline : false,
				type : "select",
				comboboxName : "logicSysStsID",
				options : {
					initValue:'1',
					data : [ {
						text : '启用',
						id : '1'
					}, {
						text : '停用',
						id : '0'
					} ]
				},
				validate : {
					required : true
				}
			}, {
				display : "备注",
				name : "remark",
				newline : true,
				type : "textarea",
				width : 493,
				attr : {
					style : "resize: none;"
				}
			}, {
				name : "orderNo",
				type : "hidden"
			}, {
				display : "系统版本",
				name : "systemVersion",
				newline : true,
				type : "text",
				group : "版本信息",
				groupicon : groupicon,
				width : 493,
				validate : {
					required : true
				}
			}, {
				display : "中文版权",
				name : "cnCopyright",
				newline : true,
				type : "text",
				width : 493,
				validate : {
					required : true
				}
			}, {
				display : "英文版权",
				name : "enCopyright",
				newline : true,
				type : "text",
				width : 493,
				validate : {
					required : true
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

		/* 	//客户 在弹出grid中选择
			$.ligerui.get("IconBoxID").openSelect({
				url : '${ctx}/bione/logicSys/logic-sys!selectImages.xhtml',
				dialogname:'iconselector',
				title:'选择图标',
				comboxName:'IconBoxID',
				dialogType:'icon'
			});  */

		// id 存在为修改, id 不存在为新增
		if ("${id}") {
			$("#mainform [name='logicSysNo']").attr("readonly", "readonly").removeAttr("validate");
			BIONE.loadForm(mainform, {
				url : "${ctx}/bione/admin/logicSys/${id}"
			});
		} else {

			$("#logicSysName").change(function() {
				$("#systemVersion").val($(this).val());
			});
			$.ligerui.get("basicOrgStsID").selectValue("1");
			$.ligerui.get("basicDeptStsID").selectValue("1");
			$.ligerui.get("logicSysStsID").selectValue("1");
		}
	});

	function f_save() {
		$("#mainform input[name=logicSysIcon]").val(
				$("#mainform input[name=IconBoxID]").val());
		BIONE.submitForm($("#mainform"),
				function() {
					BIONE.closeDialogAndReloadParent("logicSysWin", "maingrid",
							"保存成功");
				}, function() {
					BIONE.closeDialog("logicSysWin", "保存失败");
				});
	}

	function f_close() {
		BIONE.closeDialog("logicSysWin");
	}
</script>
</head>
<body>
	<div id="template.center">
		<form action="${ctx}/bione/admin/logicSys" method="post" id="mainform"></form>
	</div>
</body>
</html>