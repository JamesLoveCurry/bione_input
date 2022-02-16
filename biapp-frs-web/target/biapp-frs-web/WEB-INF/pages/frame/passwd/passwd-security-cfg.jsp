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
	var deftmainform = '{"validTime":"3","maxErrorTimes":"10","minLength":"6","maxLength":"20","lockType":"01","lockTime":"1","isFirst":"0","enableSts":"1","diffRecentHis":"1","incUppercase":"1","incLowercase":"1","incSpecial":"1","incNum":"1"}';
	var cpxOpts = [ {
		text : '是',
		id : '1'
	}, {
		text : '否',
		id : '0'
	} ];
	var glabelWidth = 125;
	var gfieldWidth = 180;
	var gspaceWidth = 40;
	//
	$(function() {
		mainform = $("#mainform");
		mainform.ligerForm({
			labelWidth : glabelWidth,
			fields : [
					{
						name : "lastUpdateUser",
						type : "hidden"
					},
					{
						display : "是否启用安全策略",
						name : "enableSts",
						newline : true,
						type : "select",
						comboboxName : "enableStsID",
						options : {
							data : [ {
								text : '启用',
								id : '1'
							}, {
								text : '停用',
								id : '0'
							} ],
							onSelected : function(value, text) {

								switch (value) {
								case "0":
									$("#diffRecentHis").parents("li:last").css(
											"display", "none");
									$("#incUppercase").parents("li:last").css(
											"display", "none");
									$("#incLowercase").parents("li:last").css(
											"display", "none");
									$("#incSpecial").parents("li:last").css(
											"display", "none");
									$("#incNum").parents("li:last").css(
											"display", "none");
									break;
								case "1":
									$("#diffRecentHis").parents("li:last").css(
											"display", "block");
									$("#incUppercase").parents("li:last").css(
											"display", "block");
									$("#incLowercase").parents("li:last").css(
											"display", "block");
									$("#incSpecial").parents("li:last").css(
											"display", "block");
									$("#incNum").parents("li:last").css(
											"display", "block");
									break;

								default:
									break;

								}
							}
						},
						group : "密码安全策略",
						groupicon : groupicon,
						validate : {
							required : true
						}
					},
					{
						display : "密码有效时间（月）",
						name : "validTime",
						newline : true,
						type : "select",
						comboboxName : "validTimeID",
						options : {
							data : [ {
								text : '1个月',
								id : "1"
							}, {
								text : '2个月',
								id : "2"
							}, {
								text : '3个月',
								id : "3"
							}, {
								text : '6个月',
								id : "6"
							}, {
								text : '9个月',
								id : "9"
							}, {
								text : '12个月',
								id : "12"
							} ]
						},
						group : "密码属性设置",
						groupicon : groupicon,
						validate : {
							required : true
						}
					},
					{
						display : "最大错误次数",
						name : "maxErrorTimes",
						newline : false,
						type : "number",
						validate : {
							required : true,
							maxlength : 22
						}
					},
					{
						display : "密码最小长度",
						name : "minLength",
						newline : true,
						type : "number",
						validate : {
							required : true,
							min : 1
						}
					},
					{
						display : "密码最大长度",
						name : "maxLength",
						newline : false,
						type : "number",
						validate : {
							required : true,
							max : 100
						}
					},
					{
						display : "锁定方式",
						name : "lockType",
						newline : true,
						type : "select",
						comboboxName : "lockTypeID",
						options : {
							data : [ {
								text : '限时锁定',
								id : "01"
							}, {
								text : '永久锁定',
								id : "02"
							} ],
							onSelected : function(value, text) {
								switch (value) {
								case "02":
									$("#lockTime").val("1").parents("li:last")
											.css("display", "none");
									break;
								case "01":
									$("#lockTime").parents("li:last").css(
											"display", "block");
									break;

								default:
									break;

								}
							}
						},
						validate : {
							required : true
						}
					}, {
						display : "锁定时间(小时)",
						name : "lockTime",
						newline : false,
						type : "number"
					},{
						display : "首次登陆修改密码",
						name : "isFirst",
						newline : true,
						type : "select",
						comboboxName : "isFirstID",
						options : {
							cancelable : false,
							data : cpxOpts
						},
						validate : {
							required : true
						}
					}, {
						display : "密码包含大写字母",
						name : "incUppercase",
						newline : true,
						type : "select",
						comboboxName : "incUppercaseID",
						group : "安全策略设置",
						groupicon : groupicon,
						options : {
							cancelable : false,
							data : cpxOpts
						},

						validate : {
							required : true
						}
					}, {
						display : "密码包含小写字母",
						name : "incLowercase",
						newline : false,
						type : "select",
						comboboxName : "incLowercaseID",
						options : {
							cancelable : false,
							data : cpxOpts
						},
						validate : {
							required : true
						}
					}, {
						display : "密码包含特殊符号",
						name : "incSpecial",
						newline : true,
						type : "select",
						comboboxName : "incSpecialID",
						options : {
							cancelable : false,
							data : cpxOpts
						},
						validate : {
							required : true
						}
					}, {
						display : "密码包含数字",
						name : "incNum",
						newline : false,
						type : "select",
						comboboxName : "incNumID",
						options : {
							cancelable : false,
							data : cpxOpts
						},
						validate : {
							required : true
						}
					}, {
						display : "异于近几次历史",
						name : "diffRecentHis",
						newline : true,
						type : "number",
						validate : {

							maxlength : 22
						}
					} ]
		});
		if ("${id}") {
			BIONE.loadForm($("#mainform"), {
				url : "${ctx}/bione/admin/pwsec/${id}/load"
			});
		} else {
			//showCpxItems(deftPwdComplex); 
		}

		jQuery.metadata.setType("attr", "validate");
		BIONE.validate(mainform);
		var buttons = [];
		buttons.push({
			text : '恢复默认值 ',
			onclick : cancleCallBack
		});
		//buttons.push({
		//	text : '重置',
		//	onclick : reload
		//});
		buttons.push({
			text : '保存',
			onclick : savePwdSecurity
		});
		BIONE.addFormButtons(buttons);
	});

	/*
	保存表单，并判断最大密码长度是否大于最小密码长度
	 */
	function savePwdSecurity() {
		var minl = parseInt($("#minLength").val());
		var maxl = parseInt($("#maxLength").val());
		if (maxl - minl >= 0) {

			BIONE.submitForm($("#mainform"), function(data) {
				BIONE.tip("保存成功");
			});
		} else {
			alert("密码最大长度不能小于密码最小长度");
		}
	}

	/*
	将页面设置还原为默认设置 ，将  deftmainform 对象加载到表单中
	
	 */
	function cancleCallBack() {
		setCpx(deftmainform);
	}

	function toObject(json) {
		if (!json) {
			json = deftmainform;
		}
		return $.parseJSON(json);
	}
	function setCpx(json) {
		var pwdComplexObj = toObject(json);

		setCpxValue(pwdComplexObj);
	}
	function setCpxValue(pwdComplexObj) {
		$.ligerui.get("validTimeID").selectValue(pwdComplexObj.validTime);
		$("#maxErrorTimes").val(pwdComplexObj.maxErrorTimes);
		$("#minLength").val(pwdComplexObj.minLength);
		$("#maxLength").val(pwdComplexObj.maxLength);
		$.ligerui.get("lockTypeID").selectValue(pwdComplexObj.lockType);
		$("#lockTime").val(pwdComplexObj.lockTime);
		$.ligerui.get("enableStsID").selectValue(pwdComplexObj.enableSts);
		$("#diffRecentHis").val(pwdComplexObj.diffRecentHis);
		$.ligerui.get("incUppercaseID").selectValue(pwdComplexObj.incUppercase);
		$.ligerui.get("incLowercaseID").selectValue(pwdComplexObj.incLowercase);
		$.ligerui.get("incSpecialID").selectValue(pwdComplexObj.incSpecial);
		$.ligerui.get("incNumID").selectValue(pwdComplexObj.incNum);
		$.ligerui.get("isFirstID").selectValue(pwdComplexObj.isFirst);

	}
</script>
</head>
<body>
	<div id="template.center">
		<form id="mainform" action="${ctx}/bione/admin/pwsec/save"
			method="post"></form>
	</div>
</body>
</html>