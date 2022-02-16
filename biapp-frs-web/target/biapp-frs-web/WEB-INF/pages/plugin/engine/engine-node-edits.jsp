<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<meta name="decorator" content="/template/template5.jsp">
<script type="text/javascript">
	//全局变量
	var groupicon = "${ctx}/images/classics/icons/communication.gif";
	var mainform = null;
	function initMainForm() {
		//渲染表单
		mainform = $("#mainform");
		mainform.ligerForm({
			fields : [ {
				name : 'nodeIds',
				newline : false,
				type : 'hidden'
			}, {
				display : '节点地址',
				name : 'ipAddress',
				newline : true,
				type : 'text',
				validate : {
					required : true
				}
			}, {
				display : '节点端口',
				name : 'port',
				newline : false,
				type : 'digits',
				validate : {
					required : true
				}
			}, {
				display : '最大并发数',
				name : 'maxThread',
				newline : true,
				type : 'digits',
				validate : {
					required : true
				}
			} ,{
				display : '备注',
				name : 'remark',
				newline : true,
				width: '410',
				type : 'textarea'
			}]

		});
		jQuery.metadata.setType("attr", "validate");
		BIONE.validate(mainform);
	}
	function initData() {
		if ("${nodeIds}" != "") {
			$.ajax({
					cache : false,
					async : true,
					url : "${ctx}/report/frame/engine/log/node/show?nodeIds=${nodeIds}",
					dataType : 'json',
					type : "GET",
					success : function(result) {
						$.ligerui.get("ipAddress").updateStyle();
						$("#ipAddress").val(result.ipAddress);
						$("#port").val(result.port);
						$("#nodeIds").val("${nodeIds}");
						$("#maxThread").val(result.maxThread);
						$("#remark").val(result.remark);
					}
					});
		}
	}
	function f_save() {
		BIONE.submitForm($("#mainform"), function() {
			window.parent.grid.loadData();
			BIONE.closeDialog("engineNodeDialog", "保存成功");
		}, function() {
			BIONE.tip("engineNodeDialog", "保存失败");
		});
	}
	$(function() {
		initMainForm();
		initData();
		//添加表单按钮
		var btns = [ {
			text : "取消",
			onclick : function() {
				BIONE.closeDialog("engineNodeDialog");
			}
		}, {
			text : "保存",
			onclick : function() {
				f_save();
			}
		} ];
		BIONE.addFormButtons(btns);
	});
</script>
<title></title>
</head>
<body>
	<div id="template.center">
		<form id="mainform" action="${ctx}/report/frame/engine/log/node/save"
			method="GET"></form>
	</div>
</body>
</html>