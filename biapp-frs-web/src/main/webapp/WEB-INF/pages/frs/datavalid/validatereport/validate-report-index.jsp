<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<meta name="decorator" content="/template/template1_BS.jsp">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<script>
	var mainform;
	var grid;
	$(function() {
		initForm();
		initGrid();
		initButtons();
		BIONE.addSearchButtons("#search", grid, "#searchbtn");
	});
	function initForm() {
		mainform = $("#search").ligerForm({
			fields : [ {
				display : "任务类型", 
				name : "paramName", 
				newline : false, 
				type : "select",
				width : '140', 
				cssClass : "field", 
				labelWidth : '90',
				comboboxName : "paramName_sel", 
				attr : { 
					op : "=", 
					field : "t.taskType"
				},
				options : { 
					onBeforeOpen : function() {
						$.ajax({
							cache : false,
                            async : false,
							url : "${ctx}/frs/validatereport/getIdxTaskType", 
							type : "get", 
							dataType : "json",
							success : function(result) {
								if(result.taskTypeList){
                                    $.ligerui.get('paramName_sel').setData (result.taskTypeList);
                                }
							}
						});
					}
				}
			}, {
				display : "任务名称",
				name : "taskNm",
				newline : false,
				type : "text",
				width : '140',
				cssClass : "field",
				labelWidth : '90',
				attr : {
					op : "like",
					field : "t.task_nm"
				}
			}, {
				display : "机构名称",
				name : "orgNm",
				newline : false,
				type : "select",
				width : '140',
				cssClass : "field",
				labelWidth : '90',
				comboboxName : "orgNm_sel",
				attr : {
					op : "in",
					field : "t2.org_no"
				},
				options : { 
					onBeforeOpen : function() {
						var taskType = $("#paramName").val();
						if(taskType){
							var height = $(window).height() - 120;
							var width = $(window).width() - 480;
							window.BIONE .commonOpenDialog(
											"机构树", "taskOrgWin", width, height,
											"${ctx}/frs/integratedquery/rptquery/searchOrgSetInfo?orgType="+taskType,
											null);
						}else{
							BIONE.tip("请选择任务类型！");
						}
						return false;
					},
					cancelable : true
				}
			}, {
				display : "数据日期",
				name : "dataDate",
				newline : false,
				type : "date",
				width : '140',
				cssClass : "field",
				labelWidth : '90',
				attr : {
					op : "=",
					field : "t.data_date"
				},
				options : {
					format : "yyyyMMdd"
				}
			}]
		});
	}

	function initGrid() {
		grid = $("#maingrid").ligerGrid({
			columns : [{
				display : '任务名称',
				name : 'TASK_NM',
				width : "30%",
				align : "left",
				render : taskObjNmRender
			}, {
				display : '任务类型',
				name : 'PARAM_NAME',
				width : "20%"
			}, {
				display : '数据日期',
				name : 'DATA_DATE',
				width : "20%"
			}, {
				display : '机构',
				name : 'ORG_NM',
				width : "20%"
			}],
			height : '99%',
			width : '100%',
			checkbox : false,
			rownumbers : true,
            isScroll : true,
            alternatingRow : true,//附加奇偶行效果行
            colDraggable : false,
			dataAction : 'server',//从后台获取数据
            method : 'post',
			url : "${ctx}/frs/validatereport/getTaskList",
			toolbar : {}
		});
	}
	
	//初始化按钮
    function initButtons() {
        var btns = [ {
            text : '生成校验简报',
            click : createValidReport,
            icon : 'fa-plus'
        }];
        BIONE.loadToolbar(grid, btns, function() {});
    }
	
	//生成校验简报
	function createValidReport(){
		var row = grid.getSelectedRow();
		if(row != null){
			BIONE.commonOpenDialog("校验简报", "reportWin", $(window).width() - 100, $(window).height() - 100, 
					"${ctx}/frs/validatereport/createValidReport?taskId="+row.TASK_ID+"&taskNm="+row.TASK_NM+"&dataDate="+row.DATA_DATE+"&orgNo="+row.EXE_OBJ_ID+"&orgNm="+row.ORG_NM);
		}else{
			BIONE.tip("请选择一条数据！");
		}
	}
	
	//报表名称链接
	function taskObjNmRender(rowdata) {
		var aa = rowdata.taskObjNm;
		return "<a href='javascript:void(0)' class='link' onclick='onShowRptValid(\""
				+ rowdata.TASK_ID
				+ "\",\""
				+ rowdata.DATA_DATE
				+ "\",\""
				+ rowdata.EXE_OBJ_ID
				+ "\")' title='"
				+ rowdata.TASK_NM
				+ "'>" + rowdata.TASK_NM + "</a>";
	};
	
	//生成校验简报
	function onShowRptValid(taskId, dataDate, orgNo){
		var height = $("#center").height() - 10;
		BIONE.commonOpenDialog("报表校验结果", "rptValidWin", 960, height, 
				"${ctx}/frs/validatereport/showRptValid?taskId="+taskId+"&dataDate="+dataDate+"&orgNo="+orgNo);
	}
</script>
<title>校验简报</title>
</head>
<body>
</body>
</html>