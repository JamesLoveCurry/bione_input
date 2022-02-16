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
				display : '单元格名称',
				id : 'taskNm',
				name : 'taskNm',
				newline : false,
				width : 220,
				type : 'text',
				attr :{
					readonly : true
				}
			},{
				display : '数据日期',
				id : 'dataDate',
				name : 'dataDate',
				newline : true,
				width : 220,
				type : 'date',
				options : {
					format : "yyyyMMdd",
					readonly : true
				}
			},{
				display : '任务状态',
				id : 'sts',
				name : 'sts',
				newline : true,
				width : 220,
				type : 'select',
				options : {
					url : "${ctx}/report/frame/enginelog/rpt/exeStsList.json",
					readonly : true
				}
			},{
				display : '完成状态',
				id : 'runlog',
				name : 'runlog',
				newline : true,
				width : 220,
				type : 'text',
				attr :{
					readonly : true
				}
			},{
				display : '完成描述',
				id : 'rundesc',
				name : 'rundesc',
				newline : true,
				width : 540,
				type : 'textarea'
			}]});
		$("#rundesc").attr("readonly","true");
		$("#rundesc").css("height","220px");
		
	}
	
	function initData(){
		$.ajax({
			cache : false,
			async : true,
			url : "${ctx}/report/frame/taskrundata/getTaskRunDataChildLog",
			dataType : 'json',
			data:{
				taskNo : "${taskNo}",
			},
			type : "post",
			success : function(result) {
				$("#taskNm").val(result.baseData.taskNm);
				$("#dataDate").val(result.baseData.id.dataDate);
				$("#startTime").val(result.baseData.startTime);
				$("#endTime").val(result.baseData.endTime);
				$.ligerui.get("sts").selectValue(result.baseData.sts);
				if(result.baseData.sts == "01" || result.baseData.sts == "02"){
					$("#runlog").parent().parent().parent().hide();
					$("#rundesc").parent().parent().hide();
				}
				var runlogs = result.baseData.runLog.split("\n");
				$("#runlog").val(runlogs[0]);
				$("#rundesc").val(runlogs[1]);
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