<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<meta name="decorator" content="/template/template1_BS.jsp">
<script type="text/javascript">
	var ctx = "${ctx}"
</script>
<script type="text/javascript" src="${ctx}/frs/js/rptfill/TaskFill.js"
	></script>
<script type="text/javascript">
	var moduleType;
	var gridUrl = "${ctx}/bione/frs/rptfreeze/flTaskList?orgTypes=${orgTypes}&moduleType=${moduleType}";
	var taskComBoBoxUrl = "${ctx}/frs/rptfill/reject/taskNmComBoBox?flag=1";//一次任务
	var rptComBoBoxUrl = "${ctx}/frs/rptfill/reject/rptNmComBoBox?flag=1";
	/*var columns = [ "sts", "dataDate", "exeObjId", "exeObjNm", "taskNm",
			"taskObjNm", "endTime", "isUpt", "sumpartRs", "logicRs", "warnRs" ];
	var fields = [ "handSts", "taskNm", "rptNm", "orgNm", "updateSts",
			"dataDate", "logicRsSts", "sumpartRsSts", "warnRsSts" ];*/
	$(init);
	//初始化函数
	function init() {
		searchForm();
		initGrid();
		initButtons();
		addMySearchButtons("#search", grid, "#searchbtn");
		
	}
	//搜索表单应用ligerui样式
	function searchForm() {
		//CommonSerchForm(taskComBoBoxUrl, rptComBoBoxUrl, null, null,
		//		this.fields, "1", 3);
		//调整搜索输入框宽度 edit by lxp 20161212
		$("#search").ligerForm({
			fields : [ {
					display : "任务类型", 
					name : "orgTypesNm",
					newline : false, 
					type : "select",
					width : '140',
					cssClass : "field", 
					labelWidth : '90',
					options : {
						valueFieldID : 'orgTypes',
						data : [{ id : '03', text : '人行大集中' }, 
						        { id : '02', text : '银监会1104报表'}
								//,{id:'01',text:'利率报备'}
						],
						onSelected : function(value) {
							if ("" != value) {
								$.ajax({
									async : false, 
									type : "post", 
									url : taskComBoBoxUrl, 
									dataType : "json",
									data : { "orgTypes" : value},
									success : function(rptData) 
									{ $.ligerui.get("taskNm_sel").setValue(""); $.ligerui.get("taskNm_sel").setData(rptData);}
								});
							}
						}, cancelable  : true,
						dataFilter : true
						
					},comboboxName : "moduleType_sel", 
					attr : { 
						op : "=", 
						field : "i.task_type"
						}
				},
				{display : "任务名称", 
					name : "taskNm", 
					newline : false, 
					type : "select",
					width : '140', 
					cssClass : "field", 
					labelWidth : '90',
					options : {
						valueFieldID : "taskId", 
						onBeforeOpen : function() {
							var moduleType = $.ligerui.get("moduleType_sel").getValue();
							if ("" == moduleType) {
								$.ligerui.get("taskNm_sel").setValue("");
								$.ligerui.get("taskNm_sel").setData("");
								BIONE.tip("请选择类型");
							}
						},
						// 联动报表
						onSelected : function(value) {
							if ("" != value) {
								$.ajax({
									async : false, 
									type : "post", 
									url : rptComBoBoxUrl, 
									dataType : "json",
									data : { "taskId" : value},
									success : function(rptData) 
									{ $.ligerui.get("rptNm_sel").setValue(""); $.ligerui.get("rptNm_sel").setData(rptData);}
								});
							}
						}, cancelable  : true,
						dataFilter : true
					},
					comboboxName : "taskNm_sel", 
					attr : { 
						op : "=", 
						field : "i.task_id"
						}
				},
				{display : "报表名称", 
					name : "rptNm", 
					newline : false, 
					type : "select",
					width : '140', 
					cssClass : "field", 
					labelWidth : '90',
					options : {
						onBeforeOpen : function() {
							var taskId = $.ligerui.get("taskNm_sel").getValue();
							if ("" == taskId) {
								$.ligerui.get("rptNm_sel").setValue("");
								$.ligerui.get("rptNm_sel").setData("");
								BIONE.tip("请选择任务");
							}
						} ,cancelable  : true,
						dataFilter : true
					},
				comboboxName : "rptNm_sel", 
				attr : { 
					op : "=", 
					field : "i.taskObjId"
					}
				},
				{display : '冻结状态',
					newline : false,
					cssClass : "field",
				name:'sts',
				type:'select',
				width : '140',
				labelWidth : '90',
				options : {
						data:[{
							text:'冻结',
							id:"9"
						},{
							text:'未冻结',
							id:'N'
						}] 
					},
				attr : {
					field : 'i.sts',
					op : "="
				}
			},{
				display : "数据日期", 
				name : "dataDate", 
				newline : false, 
				type : "date",
				width : '140', 
				cssClass : "field", 
				labelWidth : '90',
				attr : { 
					op : "=", 
					field : "i.data_date"},
				options : { format : "yyyyMMdd"}
			} 			
			]
		});
	}
	
	//初始化Grid
	function initGrid() {
		//TaskFillGrid(gridUrl, columns, false);
	 	grid = $("#maingrid").ligerGrid({
			height : '98%', width : '100%',
			columns : [ {display : '任务名称', name : 'taskNm', minWidth : 100, width : "19%",align: 'left'},
			            {display : '报表名称', name : 'taskObjNm', minWidth : 100, width : "23%",align: 'left'},
			            {display : '任务状态', name : 'sts', minWidth : 50, width : "19%", render : HandStsRender},
			            {display : '数据日期', name : 'dataDate', minWidth : 50, width : "19%"},
			            {
							display : '操作',
							width : "15%",
							render : function(rowdata) {
								// class='link' onclick='alertRptIndex(\""+ row.rptId+ "\",\""+row.rptNm+"\",\""+row.rptType+"\",\""+row.infoRights+"\")'
								//return "<a href='javascript:void(0)' class='link' onclick='details()'>查看</a>";
								return "<a href='javascript:void(0)' class='link' onclick='onShowRpt(\""+ rowdata.taskObjId+"\",\"" + rowdata.taskInstanceId+"\",\"" + rowdata.exeObjId+"\",\"" + rowdata.dataDate+"\",\"" + rowdata.exeObjNm+"\",\"" + rowdata.taskObjNm+"\",\"" + rowdata.taskId+"\")'>查看</a>";

							}
						} ],
			checkbox : true, 
			usePager : true, 
			pageSize : 20, 
			isScroll : true, 
			rownumbers : true,
			alternatingRow : true, 
			colDraggable : true, 
			dataAction : 'server',
			method : 'post', 
			url : gridUrl, 
			sortName : 'i.dataDate desc,i.taskNm', 
			sortOrder : 'desc', 
			toolbar : {}, 
			enabledSort : false });
			//if(isOnDbClick){ eles["onDblClickRow"] = false;}
			//if(checkbox){ eles["checkbox"] = false;}
	}
	function onShowRpt(taskObjId,taskInstanceId,exeObjId,dataDate,exeObjNm,taskObjNm,taskId){
		//doFlag ="freeze-details";
		//var strs=[];
		//strs.push(taskInstanceId+";"+exeObjId+";"+taskObjId+";"+dataDate+";"+taskObjNm+";"+taskNm);
		var url ="${ctx}/bione/frs/rptfreeze/details?orgNm="+encodeURI(exeObjNm)+"&taskObjId="+taskObjId+"&dataDate="
					+dataDate+"&orgNo="+exeObjId+"&taskInstanceId="+taskInstanceId+"&taskId="+taskId;
		var hight = window.parent.parent.$("body").height()*0.8;
		var width = window.parent.parent.$("body").width()*0.8;
		BIONE.commonOpenDialog("当前报表 : "+taskObjNm,"approveRejWin", width, hight, url);
	}
	//初始化Button
	function initButtons() {
		var btns=[ {
			text : '解冻',
			click : f_unfreeze,
			icon : 'fa-unlock'
			// color : '#FF0000'
		}, {
			text : '冻结',
			click : f_freeze,
			icon : 'fa-lock'
			// color : '#00BFFF'
		}];
		BIONE.loadToolbar(grid, btns, function() {
		});
	}
	// 报表状态渲染
	function HandStsRender(rowdata) {
		if (rowdata.sts == '9') { return "冻结";} else { return "未冻结";}
	}
	//冻结
	function f_freeze() {
		var selectedRow = grid.getSelecteds();
		//var manager = $("#maingrid").ligerGetGridManager();
		//var selectedRow = manager.getSelectedRows();
		if(selectedRow.length == 0 || selectedRow.length > 1){
			BIONE.tip('请选择一行记录');
			return;
		}
		if(selectedRow[0].sts == '9'){
			BIONE.tip('这条记录已经是冻结状态');
			return;
		}
		$.ligerDialog.confirm('确实要冻结这' + selectedRow.length + '条记录吗!',
				function(yes) {
					//var length = selectedRow.length;
					if (yes) {
						/*var ids = "";
						for ( var i = 0; i < length; i++) {
							if (ids != "") {
								ids += ",";
							}
							ids += selectedRow[i].rptId;
						}*/
						$.ajax({
							//type : "POST",
							url : '${ctx}/bione/frs/rptfreeze/freeze.json',
							dataType : "json",
							type : "post",
							data : {
								"taskId" : selectedRow[0].taskId,
								"taskObjId" : selectedRow[0].taskObjId,
								"dataDate": selectedRow[0].dataDate
							},
							success : function(result) {
									BIONE.tip('冻结成功');
									grid.loadData();
							},
							error:function(){
								BIONE.tip('冻结失败');
							}
						});
					
					}
				});
	}
	//解冻
	function f_unfreeze(){
		var selectedRow = grid.getSelecteds();
		if(selectedRow.length == 0 || selectedRow.length > 1){
			BIONE.tip('请选择一行记录');
			return;
		}
		if(selectedRow[0].sts != '9'){
				BIONE.tip('这条记录已经是未冻结状态');
				return;
		}
		$.ligerDialog.confirm('确实要解冻这' + selectedRow.length + '条记录吗!',
				function(yes) {
					//var length = selectedRow.length;
					if (yes) {
						/*var ids = "";
						for ( var i = 0; i < length; i++) {
							if (ids != "") {
								ids += ",";
							}
							ids += selectedRow[i].rptId;
						}*/
						$.ajax({
							//type : "POST",
							url : '${ctx}/bione/frs/rptfreeze/unfreeze',
							dataType : "json",
							type : "post",
							data : {
								"taskId" : selectedRow[0].taskId,
								"taskObjId" : selectedRow[0].taskObjId,
								"dataDate": selectedRow[0].dataDate
							},
							success : function(result) {
									BIONE.tip('解冻成功');
									grid.loadData();
							},
							error:function(){
								BIONE.tip('解冻失败');
							}
						});
					
					}
				});
	}

	// 创建表单搜索按钮：搜索、高级搜索
	function addMySearchButtons(form, grid, btnContainer) {
		if (!form)
			return;
		form = $(form);
		if (btnContainer) {
			BIONE.createButton({
				appendTo : btnContainer,
				text : '查询',
				icon : 'fa-search',
				// width : '50px',
				click : function() {
					/* if ("" != $.ligerui.get("logicRsSts_sel").getValue()) {
						grid.setParm("logicRsSts", $.ligerui.get(
								"logicRsSts_sel").getValue());
					}else{
						grid.removeParm("logicRsSts");
					}
					if ("" != $.ligerui.get("sumpartRsSts_sel").getValue()) {
						grid.setParm("sumpartRsSts", $.ligerui.get(
								"sumpartRsSts_sel").getValue());
					}else{
						grid.removeParm("sumpartRsSts");
					}
					if ("" != $.ligerui.get("warnRsSts_sel").getValue()) {
						grid.setParm("warnRsSts", $.ligerui
								.get("warnRsSts_sel").getValue());
					}else{
						grid.removeParm("warnRsSts");
					} */
					var rule = BIONE.bulidFilterGroup(form);
					/**
					 * edit in 2014-08-14 by caiqy
					 */
					if (rule.rules.length) {
						grid.setParm("condition", JSON2.stringify(rule));
						grid.setParm("newPage", 1);
					} else {
						grid.setParm("condition", "");
						grid.setParm('newPage', 1);
					}
					grid.loadData();
				}
			});
		}
	}
</script>

</head>
<body>
</body>
</html>