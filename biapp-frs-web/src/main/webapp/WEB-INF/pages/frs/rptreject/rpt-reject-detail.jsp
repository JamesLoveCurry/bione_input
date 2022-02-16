<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<meta name="decorator" content="/template/template5.jsp">
<head>
<script type="text/javascript">
	var groupicon = "${ctx}/images/classics/icons/communication.gif";
	//创建表单结构 
	var mainform;
	$(function() {
		mainform = $("#mainform");
		mainform.ligerForm({
			fields : [ 
			{ display : "数据日期", name : "dataDate", newline : true, type : "text", group : "详细信息", groupicon : groupicon, attr : { readonly : true}},
			{ display : "任务名称", name : "taskNm", newline : false, type : "text", attr : { readonly : true}}, 
			{ display : "机构编码", name : "exeObjId", newline : true, type : "text", attr : { readonly : true}}, 
			{ display : "机构名称", name : "exeObjNm", newline : false, type : "text", attr : { readonly : true}}, 
			{ display : "报表名称", name : "taskObjNm", newline : true, type : "text", attr : { readonly : true}},
			{ display : "审批人", name : "collateUserNm", newline : false, type : "text", attr : { readonly : true}}, 
			{ display : "审批时间", name : "collateTime", newline : true, type : "date", attr : { readonly : true}}, 
			{ display : "审批状态", name : "collateSts", newline : false, type : "select", comboboxName : "collateSts_sel", options : { data : [{ text : '未审批', id : "0"},{ text : '同意', id : "1"},{ text : '不同意', id : "2"}]}}, 
			{ display : "申请人", name : "applyUserNm", newline : true, type : "text", attr : { readonly : true}}, 
			{ display : "申请时间", name : "applyTime", newline : false, type : "date", attr : { readonly : true}},
			{ display : "错误来源", name : "errorRes", newline : true, type : "select", comboboxName : "errorRes_sel", options : { data : [{ text : '监管发现', id : "01"},{ text : '行内发现', id : "02"}]}},
			{ display : "审批描述", name : "rebutDesc", newline : true, type : "textarea", width : 490}, 
			{ display : "申请描述", name : "applyDesc", newline : true, type : "textarea", width : 490}]
		});
		if("${rebutId}") { 
			BIONE.loadForm($("#mainform"), {url : "${ctx}/frs/rptfill/reject/rejectListById?rebutId=${rebutId}"});
			$.ligerui.get("collateSts_sel").setDisabled();
			$("#collateSts_sel").css('color', '#000');
			$("textarea").attr('readonly', 'readonly');
			$.ligerui.get("collateTime").setDisabled();
			$("#collateTime").css('color', '#000');
			$.ligerui.get("applyTime").setDisabled();
			$("#applyTime").css('color', '#000');
			$.ligerui.get("errorRes_sel").setDisabled();
			$("#errorRes_sel").css('color', '#000');
		}
	
		var buttons = [];
		buttons.push({ text : '取消', onclick : cancle});
		BIONE.addFormButtons(buttons);

	});
	function cancle() {
		BIONE.closeDialog("detailRejWin");
	}

</script>
</head>
<body>
	<div id="template.center">
		<form id="mainform" action="" method="post"></form>
	</div>
</body>
</html>