<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<meta name="decorator" content="/template/template5.jsp" />
<script type="text/javascript">
	//参数模板缓存
	var paramtmpId = "${paramtmpId}";
	var catalogId = "${catalogId}";
	var templateType = "02";
	$(function() {
		initForm();
	});
	function initForm() {
		$("#mainform").ligerForm({
			fields : [ {
				name : 'catalogId',
				type : 'hidden'
			}, {
				name : 'paramtmpId',
				type : 'hidden'
			}, {
				name : "templateType",
				type : 'hidden'
			}, {
				display : '参数模板名称',
				name : 'paramtmpName',
				newline : true,
				type : 'text',
				group : "参数模板信息",
				validate : {
					required : true,
					maxlength : 32
				}
			}, {
				display : '参数模板URL',
				name : 'templateUrl',
				newline : true,
				type : 'text',
				validate : {
					required : true,
					maxlength : 2000
				}
			}, {
				display : '备&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;注',
				name : 'remark',
				newline : true,
				width : 493,
				validate : {
					maxlength : 500
				},
				type : 'textarea'
			} ]
		});
		if (paramtmpId != "") {
			BIONE.loadForm(mainform, {
				url : "${ctx}/report/frame/param/templates/getRealCode.json",
				data : {
					"paramtmpId" : paramtmpId
				}
			});
		}

		$("#mainform input[name=catalogId]").val(catalogId);
		$("#mainform input[name=paramtmpId]").val(paramtmpId);
		$("#mainform input[name=templateType]").val(templateType);
		jQuery.metadata.setType("attr", "validate");

		BIONE.validate($("#mainform"));
		var buttons = [];

		buttons.push({
			text : '取消',
			onclick : function() {
				BIONE.closeDialog("paramtmpBox");
			}
		});
		buttons.push({
			text : '保存',
			onclick : f_save
		});
		BIONE.addFormButtons(buttons);
	}

	function f_save() {
		BIONE.submitForm($("#mainform"), function() {
			// 关闭dialog窗口,并刷新父窗口中的grid
			var main = parent || window;
			var dialog = main.jQuery.ligerui.get("paramtmpBox");
			window.parent.maingrid.loadData();
			dialog.close();
			main.jQuery.ligerui.get("maingrid").loadData();
			if (message) {
				main.BIONE.tip("保存成功");
			}
			//	BIONE.closeDialog("paramtmpBox","maingrid","保存成功");
			//	BIONE.closeDialogAndReloadParent("paramtmpBox","maingrid","保存成功");
		}, function() {
			BIONE.closeDialog("paramtmpBox", "保存失败");
		});
	}
</script>
</head>
<body>
	<div id="template.center">
		<form name="mainform" method="post" id="mainform"
			action="${ctx}/report/frame/param/templates"></form>
	</div>
</body>
</html>