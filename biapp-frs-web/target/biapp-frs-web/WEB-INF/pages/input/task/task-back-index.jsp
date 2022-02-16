<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<%@ include file="/common/meta.jsp"%>
<meta name="decorator" content="/template/template1_BS.jsp">
<script type="text/javascript">
	var ROOT_NO = '0';
	//授权资源根节点图标
	var auth_obj_root_icon = "${ctx}/images/classics/icons/house.png";
	//记录当前点击的授权对象id
	var selectedObjId = "";
	//初始化选中的对象
	var selectedObjs = '${selectedObjs}';
	var parentRowIndex = '${parentRowIndex}';
	var grid;

	$(function() {
		initGrid();
		initSearchForm();
		initButtons();
	});
	

	function initButtons() {
		var btns = [ 
		             { text : '退回', click : onBack, icon : 'up', operNo : 'oper_submit'}
		             ];
		BIONE.loadToolbar(grid, btns, function() {});
	}

	function onBack(){
		var checkedRows = grid.getCheckedRows();
		
		if(checkedRows.length!=0){
			var checkedVal = "";
			for(var i = 0 ;i <checkedRows.length;i++){
				if(i!=0)
					checkedVal = checkedVal+",";
				checkedVal = checkedVal +checkedRows[i].taskInstanceId;
			}
		}else{
			BIONE.tip("请选择需要退回的任务");
		}

		$.ligerDialog.confirm('确实要提交吗?', function(yes) {
			if(yes) {
				$.ajax({
					type : "POST",
					url : '${ctx}/rpt/input/taskoper/submitBackTask.json?taskInstanceIds=' + checkedVal,
					success : function(result) {
						if(result=="1")
						{
							grid.loadData();
							BIONE.tip("更新成功");
						}
						else
						  BIONE.tip("更新失败 ");
					}
				});
			}
		});
	}
	function initSearchForm() {
		//搜索表单应用ligerui样式
		$("#search").ligerForm({
			fields : [ {
				display : '任务名称',
				name : "taskName",
				newline : true,
				type : "text",
				cssClass : "field",
				attr : {
					field : 'taskNm',
					op : "like"
				}
			}, {
				display : '任务类型',
				name : 'taskType',
				newline : false,
				type : "select",
				comboboxName : "task_type_box",
				cssClass : "field",
				attr : {
					field : 'taskType',
					op : "="
				},
				options : {
					url : "${ctx}/rpt/input/task/getTaskType.json"
				}
			} , {
				display : '任务状态',
				name : 'sts',
				newline : false,
				type : "select",
				comboboxName : "sts_box",
				cssClass : "field",
				attr : {
					field : 'sts',
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
			} ]
		});
		BIONE.addSearchButtons("#search", grid, "#searchbtn");
	}
	
	function onShowOperLog(taskInstanceId,taskNm){
		var title = "任务["+taskNm+"]处理信息";

		dialog = window.BIONE.commonOpenLargeDialog(title,
				"operLogBox",
				"${ctx}/rpt/input/taskoper/initOperLog?taskInstanceId="+encodeURI(encodeURI(taskInstanceId)), null);
		
	}
	
	function onSelect(taskInstanceId,taskNodeInstanceId,taskNm){
		var selectedValue ="3";
		var title ="查看任务["+taskNm+"]";
		dialog = window.BIONE.commonOpenDialog(title,
				"operTaskBox",$(document).width(), $(document).height(),
				"${ctx}/rpt/input/taskoper/initOperTask?taskInstanceId="+encodeURI(encodeURI(taskInstanceId))+"&taskNodeInstanceId="+encodeURI(encodeURI(taskNodeInstanceId))+"&showType="+encodeURI(encodeURI(selectedValue)), null);
		
	}
	function initGrid() {
		var taskExeObjType = "3";
		var columnArr = [
				{
					display : '任务名称',
					name : 'taskNm',
					width : "20%",
					align : 'left',
					render : function(row) {
						return "<a href='javascript:void(0)'   onclick='onSelect(\""
								+ row.taskInstanceId+"\",\""+row.taskNodeInstanceId+ "\",\""+row.taskNm+ "\")'>" + row.taskNm + "</a>";
					}
				}, {
					display : '任务类型',
					name : 'taskTypeNm',
					width : "10%",
					align : 'center'
				}, {
					display : '下发机构',
					name : 'orgName',
					width : "10%",
					align : 'center'
				}, {
					display : '补录类型',
					name : 'taskExeObjTypeNm',
					width : "10%",
					align : 'center'
				}, {
					display : '创建人',
					name : 'creator',
					width : "10%",
					align : 'creator'
				}, {
					display : '数据日期',
					name : 'dataDate',
					width : "10%",
					align : 'creator'
				}, {
					display : '任务状态',
					name : 'sts',
					width : "10%",
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
					display : '操作',
					name: 'taskInstanceId',
					width : "15%",
					align : 'centor',
					render : function(row) {
						return "<a href='javascript:void(0)'   onclick='onShowOperLog(\""
								+ row.taskInstanceId +"\",\""+row.taskNm+ "\")'>" + "任务处理信息" + "</a>";
					}
				} ];
		grid = $("#maingrid").ligerGrid({
			toolbar : {},
			checkbox : true,
			columns : columnArr,
			dataAction : 'server', //从后台获取数据
			usePager : true, //服务器分页
			alternatingRow : true, //附加奇偶行效果行
			colDraggable : true,
			url : "${ctx}/rpt/input/taskoper/getTaskList.json?d="+new Date().getTime(),
			sortName : 'taskNm',//第一次默认排序的字段
			sortOrder : 'asc', //排序的方式
			delayLoad : true,
			pageParmName : 'page',
			pagesizeParmName : 'pagesize',
			rownumbers : true,
			width : '100%'
		});
		grid.setHeight($("#center").height()-200);
		grid.setParm("taskExeObjType", "4");
		grid.loadData();
	}
</script>

<title>指标信息</title>
</head>
<body>
</body>
</html>