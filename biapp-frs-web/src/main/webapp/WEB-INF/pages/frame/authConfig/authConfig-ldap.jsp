<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
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
			fields : [ 
			{ name: "authTypeNo", type: "hidden" },
			{
				display : "LDAP名称",
				name : "ldapName",
				newline : true,
				type : "text",
		    	group : "LDAP源信息",
				groupicon : groupicon,
				validate : {
					required : true
				}
			},
			{
				display : "IP地址",
				name : "ipAddress",
				newline : true,
				type : "text",
				validate : {
					required : true
				}
			}, 
			{
				display : "服务端口",
				name : "serverPort",
				newline : false,
				type : "number",
				validate : {
					required : true
				}
			},{
				display : "登陆",
				name : "loginBasedn",
				newline : true,
				type : "text",
				validate : {
					required : true
				}
			},{
				display : "密码",
				name : "pwd",
				newline : false,
				type : "password",
				validate : {
					required : true
				}
			},{
				display : "初始化连接数",
				name : "initConnNum",
				newline : true,
				type : "number",
				validate : {
					required : true
				}
			},{
				display : "最大连接数",
				name : "maxConnNum",
				newline : false,
				type : "text",
				validate : {
					required : true
				}
			}]
			});
		BIONE.loadForm($("#mainform"), {
			url : "${ctx}/bione/admin/userauth/authLDAP/getLdapInfo"
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
		<form id="mainform" action="${ctx}/bione/admin/userauth/authLDAP/saveLdap" method="post"></form>
	</div>
</body>
</html>