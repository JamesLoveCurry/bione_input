<%@ page language="java" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<meta name="decorator" content="/template/template5.jsp">
<script type="text/javascript">
	$(function() {
		var groupicon = "${ctx}/images/classics/icons/communication.gif";
		var paramTypeNo = '${paramTypeNo}';
		var paramTypeName = '${paramTypeName}';
		var upNo = '${upNo}';
		var upParamName = '${upParamName}';
		$("#mainform")
				.ligerForm(
						{
							fields : [
									{
										name : 'paramTypeNo',
										type : 'hidden'
									},{
										name : 'upNo',
										type : 'hidden'
									},
									{
										group : "参数值信息",
										groupicon : groupicon,
										display : '参数类型',
										name : 'paramTypeName',
										newline : true,
										type : 'text'
									},{
										display : '上级参数',
										name : 'upParamName',
										newline : false,
										type : 'text'
									},
									{
										display : '参数名称',
										name : 'paramName',
										newline : true,
										type : 'text',
										validate : {
											required : true,
											maxlength : 100
										}
									},
									{
										display : '参数值',
										name : 'paramValue',
										type : 'text',
										newline : false,
										validate : {
											required : true,
											maxlength : 500,
											remote : "${ctx}/bione/variable/param/testParamValue?paramTypeId=${paramTypeId}",
											messages : {
												remote : "参数值已存在"
											}
										}
									},
									{
										display : '顺序编号',
										name : 'orderNo',
										newline : true,
										type : 'text',
										validate : {
											number : "请输入合法的数字",
											maxlength : 5
										}
									},
									{
										display : '备&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;注',
										name : 'remark',
										newline : true,
										width : '493',
										validate : {
											maxlength : 500
										},
										type : 'textarea',
										attr : {
											style : "resize : none"
										}
									} ]
						});
		jQuery.metadata.setType("attr", "validate");
		BIONE.validate($("#mainform"));
		//初始化表单数据
		if (paramTypeNo != null) {
			$("#mainform input[name=paramTypeNo]").val(paramTypeNo);
			$("#mainform input[name=paramTypeName]").val(paramTypeName);
			$("#mainform input[name=paramTypeName]").attr("disabled","disabled");
			$("#mainform input[name=upParamName]").val(upParamName).attr("disabled","disabled");
			$("#mainform input[name=upNo]").val(upNo);
		}
		
		$("#mainform input[name=orderNo]").val(getOrderNo);
		var buttons = [];
		buttons.push({
			text : '取消',
			onclick : function() {
				BIONE.closeDialog("paramAddWin");
			}
		});
		buttons.push({
			text : '保存',
			onclick : f_save
		});

		BIONE.addFormButtons(buttons);
	});
	//保存
	function f_save() {
		BIONE.submitForm($("#mainform"),
				function() {
					parent.document.getElementById("parTab").contentWindow.grid.loadData();
					BIONE.closeDialog("paramAddWin", "添加成功");
				}, function() {
					BIONE.closeDialog("paramAddWin", "添加失败");
				});
	}
	//获取默认顺序编号 
	function getOrderNo(){
		var g = parent.document.getElementById("parTab").contentWindow.grid;
		var size = g.data.Total;
		if(size == 0){
			return 1
		}else{
			return size+1
		}
	}
</script>
</head>
<body>
	<div id="template.center">
		<form name="mainform" method="post" id="mainform"
			action="${ctx}/bione/variable/param"></form>
	</div>
</body>
</html>