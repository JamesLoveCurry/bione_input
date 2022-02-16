<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<%@ include file="/common/meta.jsp"%>
<meta name="decorator" content="/template/template1_BS.jsp">
<script type="text/javascript">
	//记录当前点击的授权对象id
	var selectedObjId = "";
	//初始化选中的对象
	var grid;
	$(function() {
		initGrid();
		initSearchForm();
	});
	
	function initSearchForm() {
		//搜索表单应用ligerui样式
		$("#search").ligerForm({
			fields : [{
				display : '任务名称',
				name : "taskName",
				newline : false,
				type : "text",
				cssClass : "field",
				attr : {
					field : 'tinfo.taskName',
					op : "like"
				}
			}, {
				display : '任务类型',
				name : 'taskType',
				newline : false,
				type : "select",
				cssClass : "field",
				attr : {
					field : 'tinfo.taskType',
					op : "="
				},
				options : {
					url : "${ctx}/input/task/getTaskType.json"
				}
			} , {
				display : '任务操作状态',
				name : 'sts',
				newline : false,
				type : "select",
				cssClass : "field",
				attr : {
					field : 'tskins.sts',
					op : "="
				},
				options : {
					data : [ {
						text : '--请选择--',
						id : ''
					}, {
						text : "流转中",
						id : "1"
					}, {
						text : "已完成",
						id : "2"
					}]
				}
			} , {
				display : '数据日期',
				name : 'dataDate',
				newline : true,
				type : 'date',
				options: {
					format : 'yyyyMMdd'
				},
				attr : {
					field : 'tskins.dataDate',
					op : "="
				}
			} , {
				display : '创建人',
				name : 'userName',
				newline : false,
				type : 'text',
				attr : {
					field : 'usr.userName',
					op : "like"
				}
			} , {
				display : '下发机构',
				name : 'orgNoID',
				newline : false,
	 			type : 'select',
				attr : {
					field : 'tskins.orgNo',
					op : "="
				},
	 			options : {
	 				onBeforeOpen: function() {
						BIONE.commonOpenIconDialog('选择机构', 'orgComBoBox',
								'${ctx}/bione/admin/orgtree/asyncOrgTree', 'orgNoID');
						return false;
	 				}
	 			}
			}]
		});
		BIONE.addSearchButtons("#search", grid, "#searchbtn");
		BIONE.loadToolbar(grid,[{
			text : '任务删除',
			icon : 'delete',
			operNo :'delInputUser',
			click : function(){
				var rows = grid.getSelectedRows();
				if(rows.length==1){
					onDeleteTask(rows[0].taskInstanceId);
				}else{
					BIONE.tip("请选择一条数据进行修改 ");
				}
			}
		}]);
		
	}
	
	
	function onDeleteTask(taskInstanceId){
		$.ligerDialog.confirm("确定要删除任务？", function(
				yes) {
			if (yes) {
				$.ajax({
					cache : false,
					async : true,
					url : "${ctx}/input/task/monitor/deleteTask/"+taskInstanceId+"?d="
							+ new Date().getTime(),
					dataType : 'json',
					type : "DELETE",
					data : {
						taskInstanceId : taskInstanceId
					},
					success : function(result) {
						if(result.error){
							BIONE.tip("删除失败");
						}else{
							grid.loadData();
							BIONE.tip('删除成功');
						}
					},
					error : function(result, b) {
						BIONE.tip('发现系统错误 <BR>错误码：' + result.status);
					}
				});
			}
		});
	}
	
	
	function refreshGrid(){
		grid.loadData();
	}
	
	function initGrid() {
		var columnArr = [{
					display : '任务名称',
					name : 'taskName',
					sortname : 'task_Name',
					width : "15%",
					align : 'left',
					render : function(rowData,rowNum,rowValue) {
						if(rowData.taskSts =="0"){
							return "<font color='red'>"+rowValue+"</font>";
						}
						if(rowData.taskSts =="1"){
							return rowValue;
						}
					}
				}, {
					display : '任务类型',
					name : 'taskTypeName',
					sortname : 'task_Type',
					width : "10%",
					align : 'center'
				}, {
					display : '补录类型',
					name : 'exeObjType',
					width : "10%",
					align : 'center',
					render:function(row,id,val){
						if(val == "02"){
							return "明细补录";
						}
					}
				}, {
					display : '创建人',
					name : 'creator',
					width : "10%",
					align : 'creator'
				}, {
					display : '下发机构',
					name : 'orgName',
					width : "10%",
					align : 'center'
				},{
					display : '数据日期',
					name : 'dataDate',
					width : "10%",
					align : 'center'
				},{
					display : '任务状态',
					name : 'taskSts',
					width : "6%",
					align : 'center'
				},{
					display : '任务操作状态',
					name : 'sts',
					width : "6%",
					align : 'center',
					render : function(rowData,rowNum,rowValue) {
						if(rowValue =="1"){
							return "流转中"
						}
						if(rowValue =="2"){
							return "已完成";
						}
					}
				}, {
					display : '下发时间',
					name : 'startTime',
					width : "15%",
					align : 'center',
					type: "date",
					format : "yyyy-MM-dd hh:mm:ss"
					
				}, {
					display : '操作',
					name: 'taskInstanceId',
					width : "5%",
					align : 'centor',
					render : function(row) {
						return "<a href='javascript:void(0)'   onclick='onShowOperLog(\""
								+ row.taskInstanceId +"\",\""+row.taskName+ "\")'>" + "任务处理信息" + "</a>";
					}
				} ];
		grid = $("#maingrid").ligerGrid({
			toolbar : {},
			checkbox : false,
			columns : columnArr,
			dataAction : 'server', //从后台获取数据
			usePager : true, //服务器分页
			alternatingRow : true, //附加奇偶行效果行
			colDraggable : true,
			url : "${ctx}/input/task/monitor/getTaskList.json?d="+new Date(),
			sortName : 'start_time',//第一次默认排序的字段
			sortOrder : 'desc', //排序的方式
			delayLoad : true,
			pageParmName : 'page',
			pagesizeParmName : 'pagesize',
			rownumbers : true,
			width : '100%'
		});
		grid.setHeight($("#center").height()-200);
		grid.loadData();
	}
	
	function onShowOperLog(taskInstanceId,taskNm){
		var title = "任务["+taskNm+"]处理信息";
		dialog = window.BIONE.commonOpenLargeDialog(title,
				"operLogBox",
				"${ctx}/input/task/monitor/operLog?taskInstanceId="+taskInstanceId, null);
		
	}
</script>

<title>指标信息</title>
</head>
<body>
</body>
</html>