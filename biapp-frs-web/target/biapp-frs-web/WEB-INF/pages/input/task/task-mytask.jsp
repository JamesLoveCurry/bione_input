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

	});
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
			} , {
				display : '数据日期',
				name : 'dataDate',
				newline : false,
				type : 'date',
				format : 'yyyy-MM-dd',
				attr : {
					field : 'dataDate',
					op : "="
				}
			} , {
				display : '下发人名称',
				name : 'deployUserNm',
				newline : true,
				type : 'text',
				attr : {
					field : 'deployUserNm',
					op : "like"
				}
			} , {
				display : '执行机构',
				name : 'orgNoID',
				newline : false,
	 			type : 'select',
				attr : {
					field : 'orgNoID',
					op : "="
				},
	 			options : {
	 				onBeforeOpen: function() {

						BIONE.commonOpenIconDialog('选择机构', 'orgComBoBox',
								'${ctx}/bione/admin/orgtree/asyncOrgTree', 'orgNoID');
	 				},
					hideOnLoseFocus : true,
					slide : false,
					selectBoxHeight : 1,
					selectBoxWidth : 1,
					resize : false,
					switchPageSizeApplyComboBox : false
	 			}
			}]
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
				"${ctx}/rpt/input/taskoper/initOperTask?taskInstanceId="+encodeURI(encodeURI(taskInstanceId))+"&taskNodeInstanceId="+taskNodeInstanceId+"&showType="+encodeURI(encodeURI(selectedValue)), null);
		
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
								+ row.taskInstanceId +"\",\""+row.taskNodeInstanceId+ "\",\""+row.taskNm+ "\")'>" + row.taskNm + "</a>";
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
			checkbox : false,
			columns : columnArr,
			dataAction : 'server', //从后台获取数据
			usePager : true, //服务器分页
			alternatingRow : true, //附加奇偶行效果行
			colDraggable : true,
			url : "${ctx}/rpt/input/taskoper/getTaskList.json?d="+new Date().getTime(),
			sortName : 'start_time',//第一次默认排序的字段
			sortOrder : 'desc', //排序的方式
			delayLoad : true,
			pageParmName : 'page',
			pagesizeParmName : 'pagesize',
			rownumbers : true,
			width : '100%'
		});
		grid.setHeight($("#center").height()-200);
		grid.setParm("taskExeObjType", "5");
		grid.loadData();
	}
</script>

<title>指标信息</title>
</head>
<body>
</body>
</html>