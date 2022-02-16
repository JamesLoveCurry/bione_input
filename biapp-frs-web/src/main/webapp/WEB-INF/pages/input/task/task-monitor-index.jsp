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
	var operType = 'del';
	var pageType = 'taskMonitor';
	$(function() {
		initGrid();
		initSearchForm();
		BIONE.loadToolbar(grid,[{
			text : '删除',
			icon : 'icon-delete',
			operNo :'delInputUser',
			click : onDeleteTask
		},{
			text: '驳回',
			click: onRebut,
			icon: 'icon-modify',
			operNo: "rebutTask"
		}]);
	});
	function initSearchForm() {
		//搜索表单应用ligerui样式
		$("#search").ligerForm({
			fields : [ 
			/*
			{
				display : '归属部门',
				name : "catalogNm",
				newline : true,
				type : "text",
				cssClass : "field",
				attr : {
					field : 'catalogNm',
					op : "like"
				}
			}, */
			{
				display : '任务名称',
				name : "taskName",
				newline : false,
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
				type : "hidden",
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
				display : '创建人',
				name : 'deployUserNm',
				newline : true,
				type : 'text',
				attr : {
					field : 'deployUserNm',
					op : "like"
				}
			} , {
				display : '下发机构',
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
					switchPageSizeApplyComboBox : false,
					cancelable:true
	 			}
			} , {
				display : '查询类型',
				name : 'queryType',
				newline : false,
				type : "select",
				comboboxName : "queryType_box",
				cssClass : "field",
				attr : {
					field : 'queryType',
					op : "="
				},
				options : {
					data : [ {
						text : '所有的',
						id : '0'
					},{
						text : '我下发的',
						id : '1'
					}],
					cancelable:true
				}
			} ]
		});
		BIONE.addSearchButtons("#search", grid, "#searchbtn");
		/* 去除删除按钮
		BIONE.loadToolbar(grid,[{
			text : '补录任务填报删除',
			icon : 'delete',
			operNo :'delInputUser',
			click : function(){
				var rows = grid.getSelectedRows();
				if(rows.length==1){
					var width = $(parent.document).width()*0.75;
					var height = $(parent.document).height()*0.90;
					var options = {
							url : "${ctx}/rpt/input/task/selectDeployDept?operType=del&taskId="+rows[0].taskId+"&insId="+rows[0].taskInstanceId,
							dialogname : "selectDeployBox",
							title : "补录任务填报删除"
						};
						BIONE.commonOpenDialog(options.title, options.dialogname, width, height,
								options.url);
				}else{
					BIONE.tip("请选择一条数据进行修改 ");
				}
			}
		}]);
		*/
		
	}
	//弹出窗口中选择触发器
	function showDeployDialog(taskId) {
		var width = $(parent.document).width()*0.75;
		var height = $(parent.document).height()*0.90;
		var options = {
			url : "${ctx}/rpt/input/task/selectDeployDept?taskId="+taskId,
			dialogname : "selectDeployBox",
			title : "删除填报人"
		};
		BIONE.commonOpenDialog(options.title, options.dialogname, width, height,
				options.url);
	}

	//驳回
	function onRebut(){
		rows = grid.getSelectedRows();
		if (rows.length == 1) {
			rows[0];
			$.ligerDialog.confirm("是否要驳回任务？", function (yes) {
				if (yes) {
					rebutIt(rows[0].taskInstanceId, rows[0].templateId)
				}
			});
		} else {
			BIONE.tip("请选择一条任务进行驳回");
		}
	}

	function rebutIt(taskInstanceId, templateId) {
		$.ajax({
			cache : false,
			async : true,
			url : "${ctx}/rpt/input/taskoper/resetTask",
			dataType : 'json',
			type : "post",
			beforeSend : function() {
				BIONE.loading = true;
				BIONE.showLoading("正在驳回中...");
			},
			complete : function() {
				BIONE.loading = false;
				BIONE.hideLoading();
			},
			data : {
				"taskInstanceId" : taskInstanceId,
				"templateId" : templateId
			},
			success : function(result) {
				if (result.status == 'success') {
					refreshGrid();
					BIONE.tip('驳回成功');
				} else {
					BIONE.tip(result.msg);
				}
			},
			error : function(result, b) {
				BIONE.tip('驳回失败');
			}
		});
	}
	
	function onShowOperLog(taskInstanceId,taskNm){
		var title = "任务["+taskNm+"]处理信息";

		dialog = window.BIONE.commonOpenDialog(title,
				"operLogBox",$(document).width()-50, $(document).height()-30,
				"${ctx}/rpt/input/taskoper/initOperLog?taskInstanceId="+encodeURI(encodeURI(taskInstanceId)), null);
		
	}
	
	function onDeleteTask(){
		var rows = grid.getSelectedRows();
		if(rows.length > 0){
			var taskInstanceIds = [];
	        for(var i =0;i<rows.length;i++){
	            taskInstanceIds.push(rows[i].taskInstanceId);
	        }
			$.ligerDialog.confirm("确定要删除任务？任务删除后将清空补录表中该任务填报的数据！", function(yes) {
				if (yes) {
					$.ajax({
						cache : false,
						async : true,
						url : "${ctx}/rpt/input/taskoper/deleteTask",
						dataType : 'json',
						type : "POST",
						data : {
							taskInstanceIds : taskInstanceIds.join(",")
						},
						success : function(result) {
							if(result.status =="success"){
								grid.loadData();
								BIONE.tip('删除成功');
							}else{
								BIONE.tip(result.msg);
							}
						},
						error : function(result, b) {
							BIONE.tip('发现系统错误 <BR>错误码：' + result.status);
						}
					});
				}
			});
		}else{
			 BIONE.tip('请至少选择一条记录!');
		}
	}
	
	function onSelect(taskInstanceId,taskNodeInstanceId,taskNm){
		var selectedValue ="3";
		var title ="查看任务["+taskNm+"]";
		dialog = window.BIONE.commonOpenDialog(title,
				"operTaskBox",$(document).width(), $(document).height(),
				"${ctx}/rpt/input/taskoper/initOperTask?taskInstanceId="+encodeURI(encodeURI(taskInstanceId))+"&taskNodeInstanceId="+encodeURI(encodeURI(taskNodeInstanceId))+"&showType="+encodeURI(encodeURI(selectedValue)), null);
		
	}
	
	function refreshGrid(){
		grid.loadData();
	}
	
	function initGrid() {
		var taskExeObjType = "3";
		var columnArr = [{
					hide : true,
					name : 'taskId'
				},
				/*{
					display : '归属部门',
					name : 'catalogNm',
					width : "6%",
					align : 'left'
				},*/
				{
					display : '任务名称',
					name : 'taskNm',
					width : "12%",
					align : 'left',
					render : function(row) {
						return "<a href='javascript:void(0)'   onclick='onSelect(\""
								+ row.taskInstanceId +"\",\""+row.taskNodeInstanceId+ "\",\""+row.taskNm+ "\")'>" + row.taskNm + "</a>";
					}
				}, {
					display : '标题',
					name : 'taskTitle',
					width : "15%",
					align : 'left'
				},{
					display : '任务类型',
					name : 'taskTypeNm',
					width : "5%",
					align : 'center',
					hide : "true"
				}, {
					display : '补录类型',
					name : 'taskExeObjTypeNm',
					width : "8%",
					align : 'center'
				}, {
					display : '创建人',
					name : 'creator',
					width : "8%",
					align : 'creator'
				}, {
					display : '创建任务机构',
					name : 'orgName',
					width : "10%",
					align : 'center'
				}, {
					display : '下发机构',
					name : 'orgNm',
					width : "12%",
					align : 'center'
				},{
					display : '数据日期',
					name : 'dataDate',
					width : "6%",
					align : 'creator'
				}, {
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
					display : '下发时间',
					name : 'startTime',
					width : "10%",
					align : 'creator',
					render : function(rowData) {
						return rowData.startTime.substring(0,19);
					}
				}, {
					display : '操作',
					name: 'taskInstanceId',
					width : "8%",
					align : 'centor',
					render : function(row) {
						return "<a href='javascript:void(0)'   onclick='onShowOperLog(\""
								+ row.taskInstanceId +"\",\""+row.taskNm+ "\")'>" + "任务处理信息" + "</a>";
					}
// 					render : function(row) {
// 						var showInfo ="";
// 						if(row.canDelete=="1")
							
// 							//showInfo +="<a href='javascript:void(0)'   onclick='onDeleteTask(\""
// 							//	+ row.taskInstanceId +"\")'>" + "删除任务" + "</a>&nbsp;&nbsp;&nbsp;";
// 							showInfo +="<a href='javascript:void(0)'   onclick='onShowOperLog(\""
// 								+ row.taskInstanceId +"\",\""+row.taskNm+ "\")'>" + "任务处理信息" + "</a>";
// 						return showInfo;
// 					}
				} ];
		grid = $("#maingrid").ligerGrid({
			toolbar : {},
			checkbox : true,
			columns : columnArr,
			dataAction : 'server', //从后台获取数据
			usePager : true, //服务器分页
			alternatingRow : true, //附加奇偶行效果行
			colDraggable : true,
			url : "${ctx}/rpt/input/taskoper/getTaskList.json?d="+new Date(),
			sortName : 'start_time',//第一次默认排序的字段
			sortOrder : 'desc', //排序的方式
			delayLoad : true,
			pageParmName : 'page',
			pagesizeParmName : 'pagesize',
			rownumbers : true,
			width : '100%',
			height : '99%'
		});
		grid.setParm("taskExeObjType", "3");
		grid.loadData();
	}
</script>

<title>指标信息</title>
</head>
<body>
</body>
</html>