<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<meta name="decorator" content="/template/template5.jsp">
<script type="text/javascript">
	var mainform;
	var treeObjs;
	$(function() {
		groupicon = "${ctx}/images/classics/icons/communication.gif";
		mainform = $("#mainform")
				.ligerForm(
						{
							align : "centre",
							fields : [
									{
										name : 'grpId',
										type : 'hidden'
									},
									{
										name : 'orgType',
										type : 'hidden'
									},
									{
										display : '机构集名称',
										name : 'grpNm',
										newline : true,
										type : 'text',
										validate : {
											required : true,
											maxlength : 100,
											remote : {
												url : '${ctx}/rpt/frame/rptorggrp/checkName?collectionId=${colId}',
												type : 'post'
											},
											messages : {
												remote : '机构集名称重复'
											}
										}
									},{
										display : '机构类型',
										name : 'orgType',
										newline : true,
										type : 'select',
										comboboxName : 'typesId',
										options : {
											url : "${ctx}/report/frame/datashow/idx/busiTypeList.json?isPublic=Y"
										},
										validate : {
											required : true
										}
									},{
										display : "备注",
										name : "remark",
										newline : true,
										type : "textarea",
										width : 373,
										attr : {
											style : "resize: none;"
										}
									}]
						});
		$("#remark").css("resize","none");
		var btns = [];
		btns.push({
			text : '取消',
			onclick : f_close
		});
		btns.push({
			text : '保存',
			onclick : f_save
		});
		BIONE.addFormButtons(btns);
		jQuery.metadata.setType("attr", "validate");
		BIONE.validate("#mainform");
		if ("${colId}") {
			BIONE.loadForm(mainform, {
				url : "${ctx}/rpt/frame/rptorggrp/editgroup/${colId}"
			});
		}
	});

	function f_close() {
		BIONE.closeDialog("new_set");
	}
	function f_save() {
		BIONE.submitForm($("#mainform"), function() {
			BIONE.closeDialogAndReloadParent("new_set", "maingrid", "保存成功");
		}, function() {
			BIONE.closeDialog("new_set", "保存失败");
		});
	}
</script>
</head>
<body>
	<div id="template.center">
		<form action="${ctx}/rpt/frame/rptorggrp/savegroup" method="post"
			id="mainform"></form>

	</div>
</body>
</html>