<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<meta name="decorator" content="/template/template1.jsp">
<!-- 基础的JS和CSS文件 -->
<script type="text/javascript">
	var grid;
	var tskType = "${tskType}";
	$(function() {	
		initSearchForm();
		initGrid();
		initToolBar();
		BIONE.addSearchButtons("#search", grid, "#searchbtn");
	});
	
	function initSearchForm() {
		$("#search").ligerForm({
			fields : [{
				display : '数据日期',
				name : "dataDate",
				newline : false,
				type : "date",
				cssClass : "field",
				options:{
					format: "yyyyMMdd"
				},
				attr : {
					op : "=",
					field : 'tsk.id.dataDate'
				}
			},{
				display : '指标编号',
				name : "indexNo",
				newline : false,
				cssClass : "field",
				attr : {
					op : "like",
					field : 'idx.id.indexNo'
				}
			},{
				display : '指标名称',
				name : "indexNm",
				newline : false,
				cssClass : "field",
				attr : {
					op : "like",
					field : 'idx.indexNm'
				}
			}]
		});
	}; 
	
	function initGrid() {	
		grid = $("#maingrid").ligerGrid({
			columns : [{
				display : '数据日期',
				name : 'id.dataDate',
				width : "15%",
				type : 'date',
				format : 'yyyy-MM-dd hh:mm:ss',
				align : 'center'
			},{
				display : '指标名称',
				name : 'taskNm',
				width : "15%",
				align : 'center'
			},{
				display : '开始时间',
				name : 'startTime',
				width : "15%",
				align : 'center',
				type : "date",
				format : "yyyy-MM-dd hh:mm:ss"
			},{
				display : '结束时间',
				name : 'endTime',
				width : "15%",
				type : "date",
				format : "yyyy-MM-dd hh:mm:ss",
				align : 'center'
			},{
				display : '跑数状态',
				name : 'sts',
				width : "15%",
				align : 'center',
				render : function(row){
					return renderHandler(row);
				}
			},{
				display : '重跑次数',
				name : 'retryTimes',
				width : "15%",
				align : 'center'
			}],
			toolbar : {},
			checkbox: true,
			dataAction : 'server', 
			usePager : true, 
			alternatingRow : true,
			colDraggable : true,
			url : "${ctx}/rpt/frame/rptenginetsk/indexList.json?tskType="+tskType,
 			sortName : 'create_Time',//第一次默认排序的字段
 			sortOrder : 'desc', //排序的方式
			pageParmName : 'page',
			pagesizeParmName : 'pagesize',
			rownumbers : true
		});
	};
	
	function initToolBar() {
		var toolBars = [ {
			text : '增加',
			click : f_open_add,
			icon : 'add'
		},{
			text : '重跑',
			click : retryRun,
			icon : 'start'
		}];
		BIONE.loadToolbar(grid, toolBars, function() {});
	};
	
	function f_open_add() {
		var title;
		if(tskType == "01"){
			title = "组合/派生指标";
		}else{
			title = "泛化指标";
		}
		dialog = BIONE.commonOpenDialog("新建"+title+"跑数任务",
				"newIndexTaskBatchBox"+tskType,800,500,
				"${ctx}/rpt/frame/rptenginetsk/newIndexTask?taskType=${tskType}", null);
	}
	
	function retryRun() {
		var retryRunObj = [];
		var rows = grid.getSelectedRows();
		if(rows.length == 0){
			BIONE.tip("至少选择一条记录！");
			return;
		}
		for(var i in rows){
			retryRunObj.push(rows[i].id.dataDate + "@" + rows[i].id.taskId);
		}
		window.parent.updateAutoTaskStatus(retryRunObj.join(","));
		grid.reload();
	};
	
	function renderHandler(row){
		if(row.sts == "0"){
			return "完成";
		}else if(row.sts == "1"){
				return "失败";
		}else if(row.sts == "2"){
				return "就绪";
		}else if(row.sts == "3"){
				return "进行中";
		}
	}
	
</script>
</head>
<body>
</body>
</html>