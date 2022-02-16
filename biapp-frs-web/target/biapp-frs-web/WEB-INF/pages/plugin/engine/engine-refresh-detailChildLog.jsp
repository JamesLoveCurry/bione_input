<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<meta name="decorator" content="/template/template5.jsp">
<!-- 基础的JS和CSS文件 -->
<script type="text/javascript">
	var mainform = null;
	$(function() {
		initMainForm();
		initData();
	});
	

	function initMainForm() {
		//渲染表单
		mainform = $("#mainform");
		mainform.ligerForm({
			inputWidth : 160,
			labelWidth : 120,
			space : 30,
			fields : [ {
				display : '任务名称',
				id:"taskNm",
				name : 'taskNm',
				newline : false,
				width : 140,
				type : 'text',
				attr :{
					readonly : true
				}
			},{
				display : '刷新类型',
				id:"taskType",
				name : 'taskType',
				newline : false,
				width : 140,
				type : 'select',
				options : {
					url:"${ctx}/frs/frame/engineRefresh/taskTypeList.json",
					readonly : true
				}
			},{
				display : '任务状态',
				id:"sts",
				name : 'sts',
				newline : false,
				width : 140,
				type : 'select',
				options : {
					url : "${ctx}/report/frame/enginelog/rpt/exeStsList.json",
					readonly : true
				}
			},{
				display : '运行日志',
				id:"runLog",
				name : 'runLog',
				newline : true,
				width : 600,
				height:"260px",
				type : 'textarea'
			}]});
		$("#runLog").prop("readonly", "true");
	}
	
	function initData(){
		$.ajax({
			cache : false,
			async : true,
			url : "${ctx}/frs/frame/engineRefresh/getDetailChild",
			dataType : 'json',
			data:{
				taskNo : "${taskNo}"
			},
			type : "post",
			success:function (result){
				$("#taskNm").val(result.baseData.taskNm);
				$.ligerui.get("taskType").selectValue(result.baseData.taskType);
				$.ligerui.get("sts").selectValue(result.baseData.sts);
				/*$("#startTime").val(result.baseData.startTime);
				$("#endTime").val(result.baseData.endTime);*/
				$("#runLog").val(result.baseData.runLog);
			}
		});
	}
</script>
</head>
</head>
<body>
	<div id="template.center">
		<form id="mainform" action="" method="POST"></form>
	</div>
</body>
</html>