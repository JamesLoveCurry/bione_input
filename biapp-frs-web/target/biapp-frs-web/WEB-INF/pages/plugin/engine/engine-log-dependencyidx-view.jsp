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
		initTab();
		initMainForm();
		initData();
	});
	
	function initTab(){
		$("#tab").ligerTab({
			//height : '100%',
			//width : '100%',
			changeHeightOnResize : true,
			contextmenu : false,
			onAfterSelectTabItem : function(tabId) {
					if(tabId == "child"){
						initGrid();
					}			
			}
		});
		tabObj = $("#tab").ligerGetTabManager();
	}
	function initMainForm() {
		//渲染表单
		mainform = $("#mainform");
		mainform.ligerForm({
			inputWidth : 160,
			labelWidth : 120,
			space : 30,
			fields : [ {
				display : '指标名称',
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
				newline : false,
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
				width : 600,
				type : 'textarea'
			}]});
		$("#rundesc").attr("readonly","true");
		$("#rundesc").css("height","260px");
	}
	
	function initData(){
		$.ajax({
			cache : false,
			async : true,
			url : "${ctx}/report/frame/enginelog/rpt/getDetail",
			dataType : 'json',
			data:{
				taskNo : "${taskNo}",
				dataDate: '${dataDate}',
				instanceId: '${instanceId}'
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
	
	function initGrid() {
		grid = $("#maingrid").ligerGrid({
			width: '100%',
			height: '90%',
			columns : [{
				display : '指标名称',
				name : 'taskNm',
				width : "30%",
				align : 'center',
				render : function(row,index,val){
					return "<a style='color:blue' onclick='f_open_view(\"" + row.id.taskNo + "\",\"" 
							+ row.id.dataDate + "\",\"" + row.id.instanceId + "\")'>"+val+"</a>"
				}
			},{
				display : '指标编号',
				name : 'id.instanceId',
				width : "30%",
				align : 'center'
			},{
				display : '数据日期',
				name : 'id.dataDate',
				width : "10%",
				align : 'center'
			},{
				display : '指标类型',
				name : 'taskType',
				width : "10%",
				align : 'center',
				render:function(row){
					switch(row.taskType){
						case  "RptIndexNo":return "报表指标";
						case  "IndexNo":return "指标";
						default  :return "未知";
			        }
				}
			},{
				display : '任务状态',
				name : 'sts',
				width : "10%",
				align : 'center',
				render:function(row){
					switch(row.sts){
						case  "01":return "等待执行";
						case  "02":return "正在执行";
						case  "03":return "执行成功";
						case  "04":return "执行失败";
						case  "07":return "手动停止";
						case  "08":return "超时停止";
						default  :return "未知";
			        }
				}
			},{
				display : '执行节点',
				name : 'execNode',
				width : "10%",
				align : 'center'
			},  {
				display : '开始时间',
				name : 'startTime',
				width : "13%",
				align : 'center',
				type : "date",
				format : "yyyy-MM-dd hh:mm:ss"
			}, {
				display : '结束时间',
				name : 'endTime',
				width : "14%",
				align : 'center',
				type : "date",
				format : "yyyy-MM-dd hh:mm:ss"
			}],
			dataAction : 'server', 
			usePager : true, 
			alternatingRow : true,
			colDraggable : true,
			url : "${ctx}/report/frame/enginelog/idx/getEngineChildIdxStsList.json?taskNo=${taskNo}&instanceId=${instanceId}",
			sortName : 'startTime',
			sortOrder : 'desc', 
			pageParmName : 'page',
			pagesizeParmName : 'pagesize',
			checkbox : false,
			rownumbers : true
		});
	};
	
	function  f_open_view(taskNo, dataDate, instanceId){
		dialog = BIONE.commonOpenDialog("子任务状态",
				"rptEngineDetail",600,500,
				"${ctx}/report/frame/enginelog/rpt/detailChildLog?taskNo=" 
						+ taskNo + "&dataDate=" + dataDate + "&instanceId=" + instanceId, null);
	}
</script>
</head>
</head>
<body>
	<div id="template.center">
		<div id="tab" style="width: 100%; overflow: hidden;">
				<div tabid="basic" title="基本信息" lselected="true">
					<form id="mainform" action="" method="POST"></form>
				</div>
				<div tabid="child" title="子任务信息" lselected="false">
					<div class="content">
						<div id="maingrid" class="maingrid"></div>
					</div>
				</div>
		</div>
	</div>
</body>
</html>