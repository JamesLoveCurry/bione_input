<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<meta name="decorator" content="/template/template5.jsp" />
<script type="text/javascript">
	var mainform;
	var themeId= "${themeId}";
	$(function() {
		initForm();
		initData();
		initBtn();
	});
	
	function initData(){
		//修改时初始化表单
		if (themeId != "" && themeId != null) {
			BIONE.loadForm(mainform,{
				type : "POST",
				url : "${ctx}/frame/idx/bank/getThemeInfo?themeId="
					+ themeId+"&d="+new Date().getTime()});
		}
	}

	function initBtn(){
		//添加按钮
		var btns = [ {
			text : "取消",
			onclick : function() {
				BIONE.closeDialog("editThemeWin");
			}
		}, {
			text : "保存",
			onclick : f_save
		} ];
		BIONE.addFormButtons(btns);
	}
	
	//保存
	function f_save() {
		BIONE.submitForm($("#mainform"), function() {
			window.parent.refreshAllTree();
			BIONE.closeDialog("editThemeWin", "保存成功");
		}, function() {
			BIONE.closeDialog("editThemeWin", "保存失败");
		});
	}
	
	function initForm(){
		//渲染表单
		mainform = $("#mainform");
		mainform.ligerForm({
					inputWidth : 160,
					labelWidth : 80,
					space : 30,
					fields : [
							{
								name : "themeId",
								type : "hidden"
							},{
								group : "主题信息",
								groupicon : "${ctx}/images/classics/icons/communication.gif",
								display : "主题编号",
								name : "themeNo",
								labelWidth : 90,
								width : 250,
								newline : false,
								type : "text",
								validate : {
									required : true,
									maxlength : 100,
									remote :{
										url: "${ctx}/frame/idx/bank/validateThemeNo?themeId=${themeId}&d="
											+ new Date(),
										type:'post',
										data : {
											themeNo : $("#themeNo").val()
										}
									},
									messages : {
										required : "主题编号不能为空。",
										remote : "已存在同编号主题。"
									}
								}
							},{
								display : "主题名称",
								name : "themeNm",
								labelWidth : 90,
								width : 250,
								type : "text",
								validate : {
									required : true,
									maxlength : 100,
									remote :{
										url: "${ctx}/frame/idx/bank/validateThemeNm?themeId=${themeId}&d="
											+ new Date(),
										type:'post',
										data : {
											themeNm : $("#themeNm").val()
										}
									},
									messages : {
										required : "主题名称不能为空。",
										remote : "已存在同名主题"
									}
								}
							},{
								display : "描述",
								name : "remark",
								labelWidth : 90,
								width : 250,
								newline : true,
								type : "textarea",
								validate : {
									maxlength : 500
								}
							}]

				});
		jQuery.metadata.setType("attr", "validate");
		BIONE.validate("#mainform");
		//表单赋值
		$("#mainform input[name='themeId']").val("${themeId}");
	}
</script>
<title>Insert title here</title>
</head>
<body>
	<div id="template.center">
		<form id="mainform" action="${ctx}/frame/idx/bank/saveTheme"
			method="POST"></form>
	</div>
</body>
</html>