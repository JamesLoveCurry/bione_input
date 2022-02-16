<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<meta name="decorator" content="/template/template32.jsp">
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
	var operGrid;
	var height = 160;

	var nodetypeTranslate = {
	        'combox' : [],
	        'translate' : {}
	    };

	    $.ajax({
	        cache : false,
	        async : false,
	        url : "${ctx}/rpt/input/task/getTaskNodeType.json?paramTypeNo=TASK_NODE_TYPE&d="
	                + new Date().getTime(),
	        dataType : 'json',
	        success : function(result){
	            nodetypeTranslate.combox = result.combox;
	            nodetypeTranslate.translate = result.translate;
	        },
	        error : function(result, b){
	            BIONE.tip('节点类型加载失败');
	        }
	    });
	
	
	$(function() {	
		initOper();
		initGrid();
	});

	function initOper() {
			var columnArr = [
					{
						display : '节点序号',
						name : 'taskOrderno',
						width : "7%",
						align : 'cenrter'
					}, {
						display : '节点名称',
						name : 'taskNodeNm',
						width : "15%",
						align : 'center'
					}, {
						display : '节点类型',
						name : 'nodeType',
						width : "15%",
						align : 'center',
						render : function(rowdata,rownum,value){
			                return value == '' ? '' : nodetypeTranslate.translate[value]
			            }
					}, {
						display : '节点对象类型',
						name : 'taskObjType',
						width : "15%",
						align : 'center',
						render: function (rowdata) {
							return BIONE.paramTransformer(rowdata.taskObjType, '${ctx}/rpt/input/task/getAuthObjName');
						}
				    }, {
						display : '节点对象',
						name : 'taskObjNm',
						width : "15%",
						align : 'center'
				    },{
						display : '开始时间',
						name : 'startTime',
						width : "15%",
						align : 'centor'
					}, {
						display : '结束时间',
						name :'endTime',
						width : "15%",
						align : 'centor'
					} ];

			operGrid = $("#mainoper").ligerGrid({
				toolbar : {},
				checkbox : false,
				title:"任务流程节点信息",
				columns : columnArr,
				dataAction : 'server', //从后台获取数据
				usePager : false, //服务器分页
				alternatingRow : true, //附加奇偶行效果行
				colDraggable : true,
				url : "${ctx}/rpt/input/taskoper/getTaskOperInfo.json?taskInstanceId="+taskInstanceId,
				sortName : 'taskNm',//第一次默认排序的字段
				sortOrder : 'asc', //排序的方式
	            delayLoad:true,
				pageParmName : 'page',
				pagesizeParmName : 'pagesize',
				rownumbers : true,
				width : '100%',
				height : height
			});
			operGrid .loadData();	
	}
	function detail(logId){
		dialog = window.BIONE.commonOpenLargeDialog("查看明细",
				"indexLogBox",
				"${ctx}/rpt/input/taskoper/initIndexLog?logId="+ logId , null);
	}
	function initGrid() {
			var columnArr = [
					{
						display : '节点序号',
						name : 'TASK_ORDERNO',
						width : "7%",
						align : 'center'
					}, {
						display : '节点名称',
						name : 'TASK_NODE_NM',
						width : "18%",
						align : 'center'
					}, {
						display : '节点类型',
						name : 'NODE_TYPE',
						width : "10%",
						align : 'center',
						render : function(rowdata,rownum,value){
			                return value == '' ? '' : nodetypeTranslate.translate[value]
			            }
					}, {
						display : '操作类型',
						name : 'OPER_TYPE',
						width : "10%",
						align : 'center',
						render : function(row){
							if(row.OPER_TYPE==null)
								return "";
							if(row.OPER_TYPE =="1") 
								return "保存";
							if(row.OPER_TYPE =="2") 
								return "提交";
							if(row.OPER_TYPE =="3") 
								return "驳回";
							if(row.OPER_TYPE =="4") 
								return "回退";
							if(row.OPER_TYPE =="5") 
								return "回写";
						}
					}, {
						display : '操作时间',
						name : 'OPER_TIME',
						width : "21%",
						align : 'centor',
						type : 'date',
						format : 'yyyy-MM-dd hh:mm:ss'
					}, {
						display : '操作人',
						name :'USER_NAME',
						width : "15%",
						align : 'centor'
					},{
						display : '明细',
						width : "15%",
						align : 'center',
						render : function(row){
							if(row.OPER_TYPE =="1") //只有操作类型为“保存”才能够查看明细
								return "<a href='javascript:void(0)' class='link' onclick='detail(\""+row.LOG_ID + "\")'>"+'查看明细'+"</a>";
						}
					}];

			grid = $("#maingrid").ligerGrid({
				toolbar : {},
				checkbox : false,
				columns : columnArr,
				title : "任务处理日志",
				dataAction : 'server', //从后台获取数据
				usePager : false, //服务器分页
				alternatingRow : true, //附加奇偶行效果行
				colDraggable : true,
				url : "${ctx}/rpt/input/taskoper/getTaskLog.json?taskInstanceId="+taskInstanceId,
				sortName : 'taskNm',//第一次默认排序的字段
				sortOrder : 'asc', //排序的方式
	            delayLoad:true,
				pageParmName : 'page',
				pagesizeParmName : 'pagesize',
				rownumbers : true,
				width : '100%',
				height : '99%'
			});
		   grid.loadData();	
	}
	
</script>
</head>
<body>
</body>
</html>