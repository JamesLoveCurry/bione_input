<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<meta name="decorator" content="/template/template14.jsp">
<head>
<script type="text/javascript">
	var templateId = '${templateId}';
	var templateNm = '${templateNm}';
	var	templateFreq = '${templateFreq}';
	var remark = '${remark}';
	
	var groupicon = "${ctx}/images/classics/icons/communication.gif";
	var field = [ {
		name : 'templateId',
		type : 'hidden'
	}, {
		display : "模板名称",
		name : "templateNm",
		newline : true,
		type : "text",
		width : 170,
		group : "模板信息",
		groupicon : groupicon
	}, {
    	display : '模板频度',
		name : 'templateFreq',
		newline : false,
		type : "select",
		options :{
			initValue : templateFreq ? templateFreq : "01",
			data :[{text :'日' ,id : '01'},
			       {text :'月' ,id : '02'},
			       {text :'季' ,id : '03'},
			       {text :'年' ,id : '04'}]
		}
    },{
    	display : '备注',
		name : 'remark',
		newline : true,
		type : "textarea",
		attr : {
			style : "resize: none;"
		},
		width : 470,
		validate : {
			maxlength : 500
		}
    }];
	
	$(function() {
		ligerFormNow();
		$("#mainform [name='templateId']").val(templateId);
		$("#mainform [name='templateNm']").val(templateNm);
		$("#mainform [name='remark']").val(remark);
	});
	
	//创建表单结构 
	function ligerFormNow() {
		mainform = $("#mainform").ligerForm({
			inputWidth : 170,
			labelWidth : 90,
			space : 40,
			fields : field
		});
	}
	
	function f_save() {
		BIONE.submitForm($("#mainform"), function() {
			BIONE.tip("保存成功");
			parent.initTree();
		}, function() {
			BIONE.tip("保存失败");
		});
	}
	
</script>
</head>
<body>
	<div id="template.center">
		<form name="mainform" method="post" id="mainform"
			action="${ctx}/cabin/analysis/config/saveTmpBasic"></form>
	</div>
</body>
</html>