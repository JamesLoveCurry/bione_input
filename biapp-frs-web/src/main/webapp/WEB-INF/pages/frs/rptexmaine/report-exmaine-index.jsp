<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<meta name="decorator" content="/template/template1_BS.jsp">
<script type="text/javascript">
	var grid;
	$(init);
	//初始化函数
	function init() {
		searchForm();
		initGrid();
		initButtons();
		BIONE.addSearchButtons("#search", grid, "#searchbtn");
		initExport();
	}
	//搜索表单应用ligerui样式
	function searchForm() {
		$("#search")
				.ligerForm(
						{
							fields : [
									{
										display : "任务类型",
										name : "rptType",
										newline : false,
										type : "select",
										cssClass : "field",
										labelWidth : '90',
										attr : {
											op : "=",
											field : "rptType"
										},
										options : {
											data : [ {
												text : '请选择',
												id : ''
											}, {
												text : '1104监管',
												id : '02'
											}, {
												text : '人行大集中',
												id : '03'
											}, {
												text : '利率报备',
												id : '05'
											} ],
											onSelected : function(value) {
												if ("" != value) {
													$
															.ajax({
																async : false,
																type : "post",
																url : "${ctx}/frs/rptexmaine/control/taskNmComBoBox.json?orgTypes="
																		+ value
																		+ "&flag=1&d="
																		+ new Date().getTime(),
																dataType : "json",
																data : {
																	"taskId" : value
																},
																success : function(
																		taskData) {
																	$.ligerui
																			.get(
																					"taskNm_sel")
																			.setValue(
																					"");
																	$.ligerui
																			.get(
																					"taskNm_sel")
																			.setData(
																					taskData);
																}
															});
												}
											}
										}
									},
									{
										display : "任务名称",
										name : "taskId",
										newline : false,
										type : "select",
										cssClass : "field",
										labelWidth : '90',
										comboboxName : "taskNm_sel",
										attr : {
											op : "=",
											field : "taskId"
										},
										options : {
											//valueFieldID : "taskId", 
											//url : "${ctx}/report/frs/rptfill/rptFillRejectController.mo?_type=data_event&_field=taskNmComBoBox&_event=POST&_comp=main&Request-from=dhtmlx&orgTypes=${rptType}&flag=1&d="+new Date(), 
											//ajaxType : "get",
											// 联动报表
											onBeforeOpen : function() {
												var rptType = $.ligerui.get(
														"rptType").getValue();
												if ("" == rptType) {
													$.ligerui.get("taskNm_sel")
															.setValue("");
													$.ligerui.get("taskNm_sel")
															.setData("");
													BIONE.tip("请选择任务类型");
												}
											},
											onSelected : function(value) {
												var rptType = $.ligerui.get(
														"rptType").getValue();
												if ("" != rptType) {
													$
															.ajax({
																async : false,
																type : "post",
																url : "${ctx}/frs/rptexmaine/control/rptNmComBoBox.json?d="
																		+ new Date().getTime(),
																dataType : "json",
																data : {
																	"taskId" : value,
																	"flag" : "1"
																},
																success : function(
																		rptData) {
																	$.ligerui
																			.get(
																					"rptNm_sel")
																			.setValue(
																					"");
																	$.ligerui
																			.get(
																					"rptNm_sel")
																			.setData(
																					rptData);
																}
															});
												}
											},
											cancelable : true,
											dataFilter : true
										}
									},
									{
										display : "报表名称",
										name : "rptId",
										newline : false,
										type : "select",
										cssClass : "field",
										labelWidth : '90',
										comboboxName : "rptNm_sel",
										attr : {
											op : "=",
											field : "rptId"
										},
										options : {
											onBeforeOpen : function() {
												var taskId = $.ligerui.get(
														"taskNm_sel")
														.getValue();
												if ("" == taskId) {
													$.ligerui.get("rptNm_sel")
															.setValue("");
													$.ligerui.get("rptNm_sel")
															.setData("");
													BIONE.tip("请选择任务");
												}
											},
											cancelable : true,
											dataFilter : true
										}
									},
									{
										display : "机构名称",
										name : "orgNo",
										newline : true,
										type : "select",
										cssClass : "field",
										labelWidth : '90',
										comboboxName : "orgNm_sel",
										attr : {
											op : "=",
											field : "orgNo"
										},
										options : {
											onBeforeOpen : function() {
												var taskId = $.ligerui.get(
														"taskNm_sel")
														.getValue();
												if (taskId) {
													var height = $(window)
															.height() - 120;
													var width = $(window)
															.width() - 480;
													window.BIONE
															.commonOpenDialog(
																	"机构树",
																	"taskOrgWin",
																	width,
																	height,
																	"${ctx}/frs/rptexmaine/control/searchOrgTree?d="
																			+ new Date().getTime()
																			+ "&taskId="
																			+ taskId,
																	null);
													return false;
												} else {
													BIONE.tip("请选择任务");
												}
											},
											cancelable : true
										}
									}, {
										display : "数据日期",
										name : "dataDate",
										newline : false,
										type : "date",
										cssClass : "field",
										labelWidth : '90',
										options : {
											format : 'yyyyMMdd',
										},
										attr : {
											op : "=",
											field : "dataDate"
										}
									}, {
										display : "类型",
										name : "showType",
										newline : false,
										type : "select",
										cssClass : "field",
										comboboxName : 'showTypeBox',
										labelWidth : '90',
										options : {
											valueFieldID : 'showType',
											data : [ {
												text : '请选择',
												id : ''
											}, {
												text : '过期未报',
												id : '01'
											}, {
												text : '强制提交',
												id : '02'
											}, {
												text : '迟报',
												id : '03'
											} ]
										},
										attr : {
											op : "like",
											field : "showType"
										}
									} ]
						});
	}
	//初始化Grid
	function initGrid() {
		grid = $("#maingrid")
				.ligerGrid(
						{
							InWindow : false,
							height : '100%',
							width : '100%',
							columns : [ {
								display : '报表名称',
								name : 'RPT_NM',
								width : '25%',
								align : 'left'
							}, {
								display : '任务类型',
								name : 'RPT_TYPE',
								width : '8%',
								align : 'left',
								render : function(rowdata, index, value) {
									if (value == "02")
										return "1104监管";
									if (value == "03")
										return "人行大集中";
									 if (value == "05")
										return "利率报备"; 
									else
										return "未知类型";
								}
							}, {
								display : '任务名称',
								name : 'TASK_NM',
								width : '15%',
								align : 'left'
							}, {
								display : '报送机构',
								name : 'RPT_ORG_NM',
								width : '10%',
								align : 'left'
							}, {
								display : '报送人',
								name : 'RPT_USER_NM',
								width : '8%',
								align : 'left'
							}, {
								display : '数据日期',
								name : 'DATA_DATE',
								width : '8%',
								align : 'left'
							}, {
								display : '类型',
								name : 'SHOW_TYPE',
								width : '6%',
								align : 'left',
								render : function(rowdata, index, value) {
									if (value == "01")
										return "过期未报";
									if (value == "02")
										return "强制提交";
									if (value == "03")
										return "迟报";
									else
										return "";
								}
							}, {
								display : '发生时间',
								name : 'OPER_DATE',
								width : '18%',
								align : 'left'
							} ],
							checkbox : false,
							usePager : true,
							isScroll : false,
							rownumbers : true,
							alternatingRow : true,//附加奇偶行效果行
							colDraggable : true,
							dataAction : 'server',//从后台获取数据
							method : 'post',
							//url : '${ctx}/report/peculiar/exmaine/showList.json',
							url : '${ctx}/frs/rptexmaine/control/queryList?d='+new Date().getTime(),
							sortName : 'operDate', //第一次默认排序的字段
							sortOrder : 'asc',
							toolbar : {}
						});
		grid.setHeight($("#center").height() - 140);
	}
	//初始化Button
	function initButtons() {
		var btns = [ {
			text : '导出',
			click : oper_export,
			icon : 'fa-download',
			operNo : 'oper_export'
		} ];
		BIONE.loadToolbar(grid, btns, function() {
		});
	}

 	function oper_export() {
 		//超过3000行不允许下载
 		if(grid.data.Total > 3000){
 			BIONE.tip("导出记录数不能超过3000行");
 		}else{
 			var rptNum = $("#search input[name='rptId']").val();
 			var orgNo = $("#search input[name='orgNo']").val();
 			var dataDate = $("#search input[name='dataDate']").val();
 			var taskType= $("#search input[name='rptType']").val();
 			var taskId= $("#search input[name='taskId']").val();
 			var showType= $("#search input[name='showType']").val();
 			//var src = "${ctx}/report/peculiar/exmaine/download?rptNum="+rptNum+"&orgNo="+orgNo+"&rptDate="+rptDate+"&d="+ new Date();
 			var src = "${ctx}/frs/rptexmaine/control/download.mo?rptNum="
 					+ rptNum
 					+ "&orgNo="
 					+ orgNo
 					+ "&dataDate="
 					+ dataDate
 					+ "&taskType="
 					+ taskType
 					+ "&taskId="
 					+ taskId
 					+ "&showType="
 					+ showType
 					+ "&d="
 					+ new Date().getTime();
 			downdload.attr('src', src);
 		}
		
	} 

	function initExport() {
		downdload = $('<iframe id="download"  style="display: none;"/>');
		$('body').append(downdload);

	}
</script>

</head>
<body>
</body>
</html>