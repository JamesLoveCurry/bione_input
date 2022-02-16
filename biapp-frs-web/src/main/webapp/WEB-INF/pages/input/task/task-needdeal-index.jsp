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
	var taskIndexType = '${taskIndexType}';
	var canSubmit = false;
	var canRebut = false;
	var canBack = false;

	$(function() {
		initGrid();
		initSearchForm();
		if(taskIndexType=="02")
			initButtons();
	});

	function initButtons() {
		var btns = [ 
		             { text : '提交', click : onSubmit, icon : 'up', operNo : 'oper_submit'},
		             { text : '打回', click : onBack1, icon : 'down', operNo : 'oper_back1'},
		             { text : '退回', click : onBack2, icon : 'down', operNo : 'oper_back2'}
		             ];
		BIONE.loadToolbar(grid, btns, function() {});

		grid.toolbarManager.setDisabled("oper_submit");
		grid.toolbarManager.setDisabled("oper_back2");
		grid.toolbarManager.setDisabled("oper_back1");
	}
	function onSubmit() {

		if(!canSubmit) return ;
		var checkedRows = grid.getCheckedRows();
		if (checkedRows.length != 0) {
			var checkedVal = "";
			for (var i = 0; i <checkedRows.length; i++) {
				if (i != 0)
					checkedVal = checkedVal + ",";
				checkedVal = checkedVal + checkedRows[i].taskInstanceId;
			}
			$.ligerDialog.confirm('确实要提交吗?', function(yes) {
				if (yes) {
					$.ajax({
						type : "POST",
						url : '${ctx}/rpt/input/taskoper/submitMultiTask.json?taskInstanceIds=' + checkedVal,
						success : function(result) {
							if(result=="1") {
								grid.loadData();
								BIONE.tip("更新成功");
							}
							else
							  BIONE.tip("更新失败 ");
						}
					});
				}
			});
		} else {
			BIONE.tip("请选择需要提交的任务");
		}
	}

	function onBack2() {

		if(!canBack) return ;
		var checkedRows = grid.getCheckedRows();
		if (checkedRows.length != 0) {
			var checkedVal = "";
			for (var i = 0; i <checkedRows.length; i++) {
				if (i != 0)
					checkedVal = checkedVal+",";
				checkedVal = checkedVal +checkedRows[i].taskInstanceId;
			}
		
			$.ligerDialog.confirm('确实要退回吗?', function(yes) {
				if(yes) {
					$.ajax({
						type : "POST",
						url : '${ctx}/rpt/input/taskoper/submitBackTask.json?taskInstanceIds=' + checkedVal,
						success : function(result) {
							if(result=="1") {
								grid.loadData();
								BIONE.tip("更新成功");
							}
							else
							  BIONE.tip("更新失败 ");
						}
					});
				}
			});
		}else{
			BIONE.tip("请选择需要退回的任务");
		}
	}
	function onBack1() {

		if(!canRebut) return ;
		var checkedRows = grid.getCheckedRows();
		if (checkedRows.length != 0) {
			var checkedVal = "";
			for (var i = 0; i <checkedRows.length; i++) {
				if (i != 0)
					checkedVal = checkedVal+",";
				checkedVal = checkedVal +checkedRows[i].taskInstanceId;
			}
		
			$.ligerDialog.confirm('确实要打回吗?', function(yes) {
				if(yes) {
					$.ajax({
						type : "POST",
						url : '${ctx}/rpt/input/taskoper/rebutMultiTask.json?taskInstanceIds=' + checkedVal,
						success : function(result) {
							if(result=="1") {
								refreshGrid();
								BIONE.tip("更新成功");
							}
							else
							  BIONE.tip("更新失败 ");
						}
					});
				}
			});
		}else{
			BIONE.tip("请选择需要打回的任务");
		}
	}
	
	function initSearchForm() {
		//搜索表单应用ligerui样式
		$("#search").ligerForm({
			fields : [ {
				display : '任务名称',
				name : "taskName",
				newline : true,
				labelWidth : 100,
				width : 220,
				space : 30,
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
				labelWidth : 100,
				width : 220,
				space : 30,
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
				newline : true,
				labelWidth : 100,
				width : 220,
				space : 30,
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
			},{
				display : '数据日期',
				name : "dataDate",
				id : "dataDate",
				newline : false,
				labelWidth : 100,
				width : 220,
				space : 30,
				type : "date",
				cssClass : "field",
				options : {
					format : "yyyyMMdd"
				},
				attr : {
					field : 'dataDate',
					op : "="
				}
			} ]
		});
		BIONE.addSearchButtons("#search", grid, "#searchbtn");
	}
	function refreshGrid(){
		grid.loadData();
	}	
	function onShowOperLog(taskInstanceId,taskNm){
		var title = "任务["+taskNm+"]处理信息";

		dialog = window.BIONE.commonOpenLargeDialog(title,
				"operLogBox",
				"${ctx}/rpt/input/taskoper/initOperLog?taskInstanceId="+encodeURI(encodeURI(taskInstanceId)), null);
		
	}
	
	function onSelect(taskInstanceId,taskNodeInstanceId,taskNm){
		var selectedValue ,title;
		selectedValue="1";
		title="处理任务["+taskNm+"]";
		

		dialog = window.BIONE.commonOpenDialog(title,
				"operTaskBox",$(document).width(), $(document).height(),
				"${ctx}/rpt/input/taskoper/initOperTask?taskInstanceId="+encodeURI(encodeURI(taskInstanceId))+"&taskNodeInstanceId="+encodeURI(encodeURI(taskNodeInstanceId))+"&showType="+encodeURI(encodeURI(selectedValue)), null);
		
	}
	function initGrid() {
		var columnArr = [{
					display : '任务状态',
					name : 'sts',
					width : "8%",
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
					display : '数据日期',
					name : 'dataDate',
					width : "8%",
					align : 'creator'
				}, {
					display : '发起机构',
					name : 'deployOrg',
					width : "8%",
					align : 'center'
				}, 
				{
					display : '任务名称',
					name : 'taskNm',
					width : "20%",
					align : 'left',
					render : function(row) {
						return "<a href='javascript:void(0)'   onclick='onSelect(\""
								+ row.taskInstanceId +"\",\""+row.taskNodeInstanceId+"\",\""+row.taskNm+ "\")'>" + row.taskNm + "</a>";
					}
				},{
					display : '标题',
					name : 'taskTitle',
					width : "15%",
					align : 'left',
					render : function(row) {
						return row.taskTitle;
					}
				}, {
					display : '任务类型',
					name : 'taskTypeNm',
					width : "8%",
					align : 'center'
				}/*, {
					display : '补录类型',
					name : 'taskExeObjTypeNm',
					width : "10%",
					align : 'center'
				}, {
					display : '创建人',
					name : 'creator',
					width : "10%",
					align : 'center'
				}*/, {
					display : '操作',
					name: 'taskInstanceId',
					width : "15%",
					align : 'center',
					render : function(row) {
						return "<a href='javascript:void(0)'   onclick='onShowOperLog(\""
								+ row.taskInstanceId +"\",\""+row.taskNm+ "\")'>" + "任务处理信息" + "</a>";
					}
				} ];
		grid = $("#maingrid").ligerGrid({
			toolbar : {},
			checkbox : taskIndexType=="02",
			columns : columnArr,
			dataAction : 'server', //从后台获取数据
			usePager : true, //服务器分页
			alternatingRow : true, //附加奇偶行效果行
			colDraggable : true,
			url : "${ctx}/rpt/input/taskoper/getTaskList.json?taskIndexType="+taskIndexType+"&d="+new Date().getTime(),
			sortName : 'data_date',//第一次默认排序的字段
			sortOrder : 'desc', //排序的方式
			delayLoad : true,
			pageParmName : 'page',
			pagesizeParmName : 'pagesize',
			rownumbers : true,
			onBeforeCheckRow : function(){
				var checkedRows = grid.getCheckedRows();
				$.each(checkedRows, function(i, n) {
					grid.unselect(grid.getRow(n));
				});
			},
			onCheckRow : function(checked, rowdata, rowindex,rowDomElement){
				if(checked){
					checkedRow = rowdata;
				}else
					checkedRow = null;
				
				initDeployBtn();
			},
			width : '100%',
			height : '100%'
		});
		grid.setHeight($("#center").height()-50);
		grid.setParm("taskExeObjType", "1");
		grid.loadData();
	}
	var checkedRow = null ;
	function initDeployBtn(){
		if(checkedRow == null)
		{
			grid.toolbarManager.setDisabled("oper_submit");
			grid.toolbarManager.setDisabled("oper_back1");
			grid.toolbarManager.setDisabled("oper_back2");
			
			canSubmit = false;
			canRebut = false;
			canBack = false;
		}
		else{
			if(checkedRow.sts=="1")
			{
				grid.toolbarManager.setEnabled("oper_submit");
				grid.toolbarManager.setEnabled("oper_back1");
				grid.toolbarManager.setDisabled("oper_back2");

				canSubmit = true;
				canRebut = true;
				canBack = false;
			}
			else if(checkedRow.sts=="2"){
				grid.toolbarManager.setEnabled("oper_back2");
				grid.toolbarManager.setDisabled("oper_submit");
				grid.toolbarManager.setDisabled("oper_back1");
				
				canSubmit = false;
				canRebut = false;
				canBack = true;
			}
		}
	}
</script>

<title>指标信息</title>
</head>
<body>
	<div id="template.center">
			<div id="maingrid" class="maingrid"></div>
	</div>
</body>
</html>