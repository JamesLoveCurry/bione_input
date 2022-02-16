<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<meta name="decorator" content="/template/template5_BS.jsp">
<head>
<script type="text/javascript">
	var groupicon = "${ctx}/images/classics/icons/communication.gif";
	//创建表单结构 
	var mainform;
	//
	$(function() {
		mainform = $("#mainform").ligerForm({
			fields : [ {
				name : "authTypeNo",
				type : "hidden"
			}, {
				display : "应用标识",
				name : "appNo",
				newline : true,
				type : "text",
				group : "UA认证源信息",
				groupicon : groupicon,
				validate : {
					required : true
				}
			}, {
				display : "认证源名称",
				name : "authSrcName",
				newline : true,
				type : "text",
				validate : {
					required : true
				}
			}, {
				display : "IP地址",
				name : "ipAddress",
				newline : true,
				type : "text",
				validate : {
					required : true
				}
			}, {
				display : "端口",
				name : "port",
				newline : true,
				type : "number",
				validate : {
					required : true
				}
			} ]
		});

		BIONE.loadForm($("#mainform"), {
			url : "${ctx}/bione/admin/userauth/authUA/getUaInfo"
		});
		buttons = [];
		buttons.push({
			text : '保存',
			onclick : f_save
		},{
			text : '测试连接',
			onclick : testConn
		});
		BIONE.addFormButtons(buttons);
		jQuery.metadata.setType("attr", "validate");
		BIONE.validate("#mainform");
	});

	//保存
	function f_save() {
		BIONE.submitForm($("#mainform"), function() {
			BIONE.tip("保存成功！");
		});
	}
	function testConn() {
		var ipAddress = $.ligerui.get("ipAddress").value;
		var port = $.ligerui.get("port").value;

		BIONE.ajax({
			url : "${ctx}/bione/admin/userauth/test",
			type : 'POST',
			data : {
				'ipAddress' : ipAddress,
				'port' : port
			}
		}, function(data) {
			if (data == true) {
				BIONE.tip('测试成功');
			} else {
				BIONE.tip('测试失败');
			}
		});
	}
</script>
</head>
<body>
	<div id="template.center">
		<form id="mainform" method="post" action="${ctx}/bione/admin/userauth/authUA/saveUaInfo"></form>
	</div>
</body>
</html>