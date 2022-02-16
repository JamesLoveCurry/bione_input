<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<meta name="decorator" content="/template/template5.jsp">
<script type="text/javascript">
	var mainform;
	var groupId = '${groupId}';
	var grpType = '${grpType}';
	window.parent.historyOrgs = "";
	$(function(){
		initForm();
		var btns = [{
			text : "取消",
			onclick : cancel
		},{
			text : "校验",
			onclick : check
		}];
		BIONE.addFormButtons(btns);
	})
	
	function initForm() {
		var validItem = [];
		$.ajax({
			cache : false,
			async : false,
			url : "${ctx}/report/frame/validgroup/getValidTypeTabs.json?d="
				+ new Date().getTime(),
			dataType : 'json',
			type : "post",
			success : function(result) {
				var data = null;
				if (result) {
					data = result.data;
				}
				if (data && data.length > 0) {
					for(var i in data) {
						var obj = {id : data[i].objDefNo, text : data[i].objDefName};
						validItem.push(obj);
					}
				}
			}
		})
		
		mainform = $("#mainform").ligerForm({
			fields : [{
				display : '校验类型',
				name : 'validTypes',
				type : "checkboxlist",
				labelWidth : "80",
				width : "400",
				options : {
					rowSize : 3 ,
					data : validItem
				}
			},{
				display : '校验日期',
				name : "validDate",
				labelWidth : "80",
				newline : false,
				type : "date",
				options:{
					format: "yyyyMMdd"
				},
				validate : {
					required : true,
					messages : {
						required : "校验日期不能为空"
					}
				}
			},{
				display : '校验机构',
				name : "validOrgs",
				labelWidth : "80",
				newline : false,
				type : "select",
				options:{
					onBeforeOpen:function(){
						window.parent.selectTab=window;
						window.parent.BIONE.commonOpenDialog("校验机构选择","selectOrg",400,450,
								"${ctx}/report/frame/validgroup/selectOrg",null);
						return false;
					}
				},
				validate : {
					required : true,
					messages : {
						required : "校验机构不能为空"
					}
				}
			}]
		});
		
		jQuery.metadata.setType("attr", "validate");
		BIONE.validate("#mainform");
		
		mainform.setData({validTypes : "01;02;03;04;05"});
	}
	
	function check(){
		var validTypes = mainform.getData().validTypes;
		var validDate = mainform.getData().validDate;
		var validOrgs = mainform.getData().validOrgs;
		if(!validTypes) {
			BIONE.tip("校验类型必选！");
			return;
		}
		if(!$("#mainform").valid()) {
			return;
		}
		$.ajax({
			cache : false,
			async : false,
			url : "${ctx}/report/frame/validgroup/checking",
			dataType : 'json',
			data:{
				validTypes : validTypes,
				validDate : validDate,
				validOrgs : validOrgs,
				groupId : groupId
			},
			type : "post",
			success : function(result) {
				if(result.msg){
					//quickRefresh();//指令发送完成后立即执行自动刷新
					BIONE.tip(reuslt.msg);
				}
				else{
					BIONE.tip("任务已重跑");
					BIONE.closeDialog("checkDialog");
				}
			}
		});
	}
	
	function cancel(){
		BIONE.closeDialog("checkDialog");
	}
	
</script>
</head>
<body>
<div id="template.center">
	<form id="mainform" action="" method="post"></form>
</div>
</body>
</html>