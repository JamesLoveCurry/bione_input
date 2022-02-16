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
	var sts = "${sts}";
	var exeObjId = "${exeObjId}";
 	$(function() {
		//01 行内补录02 银监会1104报表 03 人行报表
		grid = $("#maingrid").ligerGrid({
			columns : [ { 
				display : '报表名称', 
				name : 'taskObjNm', 
				width : '25%',
				align: 'center'
			},{ 
				display : '报表状态', 
				name : 'sts', 
				width : '10%', 
				align: 'center',
				render : function(value){
					if (value.sts == '0') { return "未归档";} else if (value.sts == '1') { return "已归档";} else if (value.sts == '2') { return "已复核";} else if (value.sts == '3') { return "已审核";} else { return value.sts;}
				}
			},{
				display : '任务状态', 
				name : 'isUpt', 
				width : '10%', 
				align: 'center',
				render : function(value){
					if (value.taskMgrSts == '0') { return "已完成";} else if (value.taskMgrSts == '1') { return "进行中";} else{ return "未知状态";} 
				}
			},{
				display : '总分校验', 
				name : 'sumpartRs', 
				width : '9%', 
				align: 'center',
				render : function(value){
					return CommonRender(value.sumpartRs);
				}
			},{
				display : '逻辑校验', 
				name : 'logicRs', 
				width : '9%', 
				align: 'center',
				render : function(value){
					return CommonRender(value.logicRs);
				}
			},{
				display : '预警校验',
				name : 'warnRs', 
				width : '9%', 
				align: 'center',
				render : function(value){
					return CommonRender(value.warnRs);
				}
			},{
				display : '零值校验',
				name : 'zeroRs', 
				width : '9%', 
				align: 'center',
				render : function(value){
					return CommonRender(value.zeroRs);
				}
			},{
				display : '截止时间', 
				name : 'endTime', 
				width : '15%', 
				align: 'center',
				type : 'date',
				format:'yyyyMMdd'
			}/*,{ 
				display : '报表状态', 
				name : 'sts', 
				width : '10%', 
				align: 'center',
				render : HandStsRender(value)
			} ,{
				display : '下发机构',
				name : 'exeObjId',
				width : '20%',
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
			} */],
			checkbox : true,
			rownumbers : true,
			isScroll : false,
			alternatingRow : true,//附加奇偶行效果行
			colDraggable : false,
			dataAction : 'server',//从后台获取数据
			method : 'post',
// 			url : '${ctx}/report/frs/rpttsk/rptTskPublishController.mo?_type=data_event&_field=getTaskDetailInfo&_event=POST&_comp=main&Request-from=dhtmlx&taskId='+taskId+'&dataDate='+dataDate+'&moduleType='+moduleType+'&exeObjId='+exeObjId+'&sts='+sts+'&pageInfo=2',
			url : '${ctx}/frs/rpttsk/publish/getTaskDetailInfo',
			parms : {taskId:taskId, dataDate:dataDate, moduleType:moduleType, exeObjId:exeObjId, sts:sts, pageInfo:'2'},
			sortName : 'taskObjNm',//第一次默认排序的字段
			sortOrder : 'asc', //排序的方式
			width : '100%',
			height : '100%'
		});
		
	}); 
 /*	$(function() {
		//01 行内补录02 银监会1104报表 03 人行报表
		grid = $("#maingrid").ligerGrid({
			columns : [ { 
				display : '报表名称', 
				name : 'taskObjNm', 
				width : '25%',
				align: 'center'
			},{ 
				display : '报表状态', 
				name : 'sts', 
				width : '10%', 
				align: 'center'
				render : HandStsRender
			},{
				display : '任务状态', 
				name : 'isUpt', 
				width : '10%', 
				align: 'center'
				render : UpdateStsRender
			},{
				display : '总分校验', 
				name : 'sumpartRs', 
				width : '10%', 
				align: 'center'
				render : function(value){
					CommonRender(value.sumpartRs);
				}
			},{
				display : '逻辑校验', 
				name : 'logicRs', 
				width : '10%', 
				align: 'center'
				render : CommonRender(value.logicRs)
			},{
				display : '预警校验',
				name : 'warnRs', 
				width : '10%', 
				align: 'center'
				render : CommonRender(value.warnRs)
			},{
				display : '零值校验',
				name : 'zeroRs', 
				width : '10%', 
				align: 'center'
				render : CommonRender(value.zeroRs)
			},{
				display : '截止时间', 
				name : 'endTime', 
				width : '15%', 
				align: 'center'
				type : 'date',
				format:'yyyyMMdd'
			}------ ,{
				display : '任务名称',
				name : 'taskNm',
				width : '24%',
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
				name : 'exeObjId',
				width : '20%',
				align : 'center'
			},{
				display : '数据日期',
				name : 'dataDate',
				width : '15%',
				align : 'center'
			} -----],
			checkbox : true,
			rownumbers : true,
			isScroll : false,
			alternatingRow : true,//附加奇偶行效果行
			colDraggable : false,
			dataAction : 'server',//从后台获取数据
			method : 'post',
			url : '${ctx}/report/frs/rpttsk/rptTskPublishController.mo?_type=data_event&_field=getTaskDetailInfo&_event=POST&_comp=main&Request-from=dhtmlx&taskId='+taskId+'&dataDate='+dataDate+'&moduleType='+moduleType+'&exeObjId='+exeObjId+'&sts='+sts+'&pageInfo=2',
			sortName : 'taskObjNm',//第一次默认排序的字段
			sortOrder : 'asc', //排序的方式
			width : '100%',
			height : '100%'
		});
		
	}); */
	

	 
	 // 校验状态通用渲染方法
	 function CommonRender(rs) {
	  if (rs == '01') {
	   return "准备校验";
	  }
	  if (rs == '02') {
	   return "校验中";
	  }
	  if (rs == '03') {
	   return "校验成功";
	  }
	  if (rs == '04') {
	   return "校验失败";
	  }
	  if (rs == '05') {
	   return "通过";
	  }
	  if (rs == '06') {
	   return "未通过";
	  }
	  return "未校验";
	 };
	
</script>
</head>
<body>
</body>
</html>