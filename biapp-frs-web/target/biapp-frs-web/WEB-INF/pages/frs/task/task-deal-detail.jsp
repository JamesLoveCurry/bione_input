<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<meta name="decorator" content="/template/template1_a.jsp">
<head>
<script type="text/javascript">
	var grid;
	var taskId = "${taskId}";
	var dataDate = "${dataDate}";
	var moduleType = "${moduleType}";
	$(function() {
		//01 行内补录02 银监会1104报表 03 人行报表
		grid = $("#maingrid").ligerGrid({
			columns : [ {
				display : '任务名称',
				name : 'taskNm',
				width : '25%',
				align : 'center'
			},{
				display : '任务类型',
				name : 'taskType',
				width : '15%',
				align : 'center',
				render :function(value){
					var taskType = value.taskType;
					if(taskType=="02"){
						return "1104监管";
					}else if(taskType=="01"){
						return "利率报备";
					}else if(taskType=="03"){
						return "人行大集中";
					}else{
						return "未知类型"
					}
				}
			},{
				display : '下发机构',
				name : 'orgNm',
				width : '25%',
				align : 'center'
			},{
				display : '下发报表数',
				name : 'counts',
				width : '15%',
				align : 'center'
			},{
				display : '已完成报表',
				name : 'countFinish',
				width : '10%',
				align : 'center',
				render : function(value){
					return "<a href='javascript:void(0)' class= 'link' onclick = 'getInfo(\""+value.dataDate+"\",\""+value.exeObjId+"\",\""+value.taskId+"\",\""+"end"+"\")'>"+ value.countEnd + "</a>"; 
				}
			},{
				display : '处理中报表',
				name : 'countStart',
				width : '10%',
				align : 'center',
				render : function(value){
					return "<a href='javascript:void(0)' class= 'link' onclick = 'getInfo(\""+value.dataDate+"\",\""+value.exeObjId+"\",\""+value.taskId+"\",\""+"start"+"\")'>"+ value.countStart + "</a>"; 
				}
			}],
			checkbox : true,
			rownumbers : true,
			isScroll : false,
			alternatingRow : true,//附加奇偶行效果行
			colDraggable : false,
			dataAction : 'server',//从后台获取数据
			method : 'post',
// 			url : '${ctx}/report/frs/rpttsk/rptTskPublishController.mo?_type=data_event&_field=getTaskDetailInfo&_event=POST&_comp=main&Request-from=dhtmlx&taskId='+taskId+'&dataDate='+dataDate+'&moduleType='+moduleType+'&pageInfo=1',
			url : '${ctx}/frs/rpttsk/publish/getTaskDetailInfo',
			parms : {taskId:taskId, dataDate:dataDate, moduleType:moduleType, pageInfo:'1'},
			sortName : 'exeObjId',//第一次默认排序的字段
			sortOrder : 'asc', //排序的方式
			width : '100%',
			height : '100%'
		});
		
	});

	//报表详细信息
	function getInfo(dataDate,exeObjId,taskId,sts){
// 		var src = "${ctx}/report/frs/rpttsk/rptTskPublishController.mo?doFlag=task-detail-info&taskId="+taskId+"&dataDate="+dataDate+"&moduleType="+moduleType+"&exeObjId="+exeObjId+"&sts="+sts;
		var src = "${ctx}/frs/rpttsk/publish/taskDetailInfo?taskId="+taskId+"&dataDate="+dataDate+"&moduleType="+moduleType+"&exeObjId="+exeObjId+"&sts="+sts;
		BIONE.commonOpenDialog("报表详细信息", "getInfo", 960,$("#center").height(), src);
	}
	
</script>
</head>
<body>
</body>
</html>