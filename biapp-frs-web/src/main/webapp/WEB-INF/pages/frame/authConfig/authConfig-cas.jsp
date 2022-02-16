<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<meta name="decorator" content="/template/template5.jsp">
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
				name : "authSrcId",
				type : "hidden"
			}, {
				display : "认证源名称",
				name : "authSrcName",
				newline : true,
				group : "CAS认证源信息",
				groupicon : groupicon,
				type : "text",
				validate : {
					required : true,
					maxlength : 100
				}
			}, {
				display : "IP地址",
				name : "ipAddress",
				newline : true,
				type : "text",
				validate : {
					required : true,
					maxlength : 50
				}
			}, {
				display : "服务端口",
				name : "serverPort",
				newline : true,
				type : "text",
				validate : {
					required : true,
					maxlength: 5
				}
			}, {
				display : "服务名称",
				name : "serverName",
				newline : true,
				type : "text",
				validate : {
					required : true,
					maxlength : 100
				}
			}
			, {
				display : "服务协议",
				name : "serverProtocol",
				newline : true,
				type : "select",
				options :{
					data :[{text :'http' ,id : '02'},
					       {text :'https' ,id : '01'}
					       ],
					       initValue :'02'
				}
			}]
		});

		BIONE.loadForm($("#mainform"), {
			url : "${ctx}/bione/admin/userauth/authCAS/getCASInfo"
		});
		buttons = [];
		buttons.push({
			text : '保存',
			onclick : f_save
		},{
			text : '测试连接',
			onclick :testConn
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
		var serverPort = $.ligerui.get("serverPort").value;

		BIONE.ajax({
			url : "${ctx}/bione/admin/userauth/authCAS/test",
			type : 'POST',
			data : {
				'ipAddress' : ipAddress,
				'serverPort' : serverPort
			}
		}, function(data) {
			if (data ===  true) {
				BIONE.tip('连接成功');
			} else {
				BIONE.tip('连接失败');
			}
		});
	}
</script>
</head>
<body>
	<div id="template.center">
		<form id="mainform" method="post" action="${ctx}/bione/admin/userauth/authCAS/saveCASInfo"></form>
	</div>
</body>
</html>