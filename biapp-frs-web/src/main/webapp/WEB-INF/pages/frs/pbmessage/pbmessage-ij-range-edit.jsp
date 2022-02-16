<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<meta name="decorator" content="/template/template5.jsp">
<head>
<script type="text/javascript">
	var groupicon = "${ctx}/images/classics/icons/communication.gif";
	var verId = window.parent.verId;
	var id = '${id}'
	//创建表单结构 
	var mainform;
	$(function() {
		mainform = $("#mainform");
		mainform.ligerForm({
			fields : [{
				name : "submitRangeNo",
				type : "hidden"
			 },{
				name : "verId",
				type : "hidden"
			 },{
				display : "报送范围编号",
				name : "submitRangeCode",
				newline : true,
				width: '200',
				labelWidth:90,
				type : "text", 
				validate : { 
					required : true,
					maxlength : 32,
					remote : {
						url : "${ctx}/frs/pbmessage/validateRangeCode",
						type : "POST",
						data : {
							"id" : id
						}
					},
					messages : {
						remote : "此编号已存在"
					}
				},
				attr : {
	        		readOnly: true
	        	}
			},{
				display : "报送范围名称", 
				name : "submitRangeNm", 
				newline : true, 
				width: '200',
				labelWidth:90,
				type : "text", 
				validate : { 
					required : true,
					maxlength : 200,
					remote : {
						url : "${ctx}/frs/pbmessage/validateRangeNm",
						type : "POST",
						data : {
							"id" : id
						}
					},
					messages : {
						remote : "此名称已存在"
					}
				}
			}
		]
		});
		$("#mainform input[name='verId']").val(verId);
		jQuery.metadata.setType("attr", "validate");
		BIONE.validate(mainform);
		var buttons = [];
		buttons.push({
			text : '取消',
			onclick : cancle
		});
		buttons.push({
			text : '保存',
			onclick : save
		});
		BIONE.addFormButtons(buttons);
		//加载数据
		BIONE.loadForm($("#mainform"), {url : "${ctx}/frs/pbmessage/getRange?id="+id});
	});
	//保存方法
	function save() {
		BIONE.submitForm($("#mainform"), function() {
			BIONE.closeDialogAndReloadParent("editRange", "maingrid", "保存成功");
		}, function() {
			BIONE.closeDialog("editRange", "保存失败");
		});
	}
	//取消方法
	function cancle() {
		BIONE.closeDialog("editRange");
	}
</script>
</head>
<body>
	<div id="template.center">
		<form id="mainform" action="${ctx}/frs/pbmessage/saveRange" method="post"></form>
	</div>
</body>
</html>