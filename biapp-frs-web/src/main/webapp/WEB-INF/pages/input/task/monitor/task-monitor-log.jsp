<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<meta name="decorator" content="/template/template14.jsp">
<head>
<style>
.indexStsA,.indexNmA{
     width:55%;
     cursor:pointer;
}
.stop{
    color:red;
}
</style>
<!-- 基础的JS和CSS文件 -->
<script type="text/javascript">


	var taskInstanceId = '${taskInstanceId}';
	var grid;
	var operGrid;
	var height = 160;
	var nodeMap = {};
	$(function() {
		initData();
		initOper();
		initGrid();
	});

	function initData(){
		 $.ajax({
		        cache : false,
		        async : false,
		        url : "${ctx}/input/task/flow/getNodeType.json?d="
		                + new Date().getTime(),
		        dataType : 'json',
		        success : function(result){
		        	nodeMap = result.translate;
		        },
		        error : function(result, b){
		            BIONE.tip('节点类型加载失败');
		        }
		    });
	}
	function initOper() {
			var columnArr = [
					{
						display : '节点序号',
						name : 'orderNo',
						width : "7%",
						align : 'center',
						isSort : false
					}, {
						display : '节点名称',
						name : 'taskNodeName',
						width : "15%",
						align : 'center',
						isSort : false
					}, {
						display : '节点类型',
						name : 'nodeType',
						width : "15%",
						align : 'center',
						render : function(rowdata,rownum,value){
			                return value == '' ? '' : nodeMap[value]
			            },
						isSort : false
					},{
						display : '状态',
						name : 'sts',
						width : "15%",
						align : 'center',
						render : function(rowdata,rownum,value){
			               if(value == "0"){
			            	   return "未开始";
			               }
			               if(value == "1"){
			            	   return "处理中";
			               }
			               if(value == "2"){
			            	   return "已完成";
			               }
			               if(value == "3"){
			            	   return "被打回";
			               }
			            },
						isSort : false
					}, {
						display : '开始时间',
						name : 'startTime',
						width : "22%",
						align : 'center',
						type : "date",
						format : "yyyy-MM-dd hh:mm:ss",
						isSort : false
					}, {
						display : '结束时间',
						name :'endTime',
						width : "22%",
						align : 'center',
						type : "date",
						format : "yyyy-MM-dd hh:mm:ss",
						isSort : false
					} ];

			operGrid = $("#mainoper").ligerGrid({
				checkbox : false,
				title:"任务流程节点信息",
				columns : columnArr,
				dataAction : 'server', //从后台获取数据
				usePager : false, //服务器分页
				alternatingRow : true, //附加奇偶行效果行
				colDraggable : true,
				url : "${ctx}/input/task/monitor/getTaskOperList.json?taskInstanceId="+taskInstanceId,
				sortName : 'taskNm',//第一次默认排序的字段
				sortOrder : 'asc', //排序的方式
	            delayLoad:true,
				pageParmName : 'page',
				pagesizeParmName : 'pagesize',
				rownumbers : true,
				width : '100%',
				height : height
			});
			operGrid.loadData();	
	}
	function detail(logId){
		dialog = window.BIONE.commonOpenLargeDialog("查看明细",
				"indexLogBox",
				"${ctx}/input/task/oper/initIndexLog?logId="+ logId , null);
	}
	function initGrid() {
		var gridHeight = $("#center").height()  - height-20;
			var columnArr = [
					{
						display : '节点序号',
						name : 'orderNo',
						width : "7%",
						align : 'center',
						isSort : false
					}, {
						display : '节点名称',
						name : 'taskNodeName',
						width : "18%",
						align : 'center',
						isSort : false
					}, {
						display : '节点类型',
						name : 'nodeType',
						width : "10%",
						align : 'center',
						render : function(rowdata,rownum,value){
			                return value == '' ? '' : nodeMap[value]
			            },
						isSort : false
					}, {
						display : '操作类型',
						name : 'operType',
						width : "15%",
						align : 'center',
						render : function(row){
							if(row.operType==null)
								return "";
							if(row.operType =="01") 
								return "保存";
							if(row.operType =="02") 
								return "提交";
							if(row.operType =="03") 
								return "通过";
							if(row.operType =="04") 
								return "驳回";
						},
						isSort : false
					}, {
						display : '操作时间',
						name : 'operTime',
						width : "25%",
						align : 'center',
						type: "date",
						format : "yyyy-MM-dd hh:mm:ss",
						isSort : false
					}, {
						display : '操作人',
						name :'userName',
						width : "20%",
						align : 'center',
						isSort : false
					}];

			grid = $("#maingrid").ligerGrid({
				checkbox : false,
				columns : columnArr,
				title : "任务处理日志",
				dataAction : 'server', //从后台获取数据
				usePager : false, //服务器分页
				alternatingRow : true, //附加奇偶行效果行
				colDraggable : true,
				url : "${ctx}/input/task/monitor/getTaskLogList.json?taskInstanceId="+taskInstanceId,
				sortName : 'operTime',//第一次默认排序的字段
				sortOrder : 'asc', //排序的方式
	            delayLoad:true,
				pageParmName : 'page',
				pagesizeParmName : 'pagesize',
				rownumbers : true,
				width : '100%',
				height : gridHeight
			});
		   grid .loadData();	
	}
	
</script>
</head>
<body>
<div id="template.center">
	<div id="mainoper">
	</div>
	<div id="maingrid" >
	</div>
</div>
</body>
</html>