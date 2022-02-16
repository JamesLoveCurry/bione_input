<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<meta name="decorator" content="/template/template1P.jsp">
<script type="text/javascript">var ctx="${ctx}"</script>
<script type="text/javascript" src="${ctx}/frs/js/rptfill/TaskFill.js"></script>
<script type="text/javascript">
	var doFlag = "";
	var gridUrl = "${ctx}/rpt/frs/rptfill/flTaskChildList?orgTypes=${orgTypes}&parentFlag=${parentFlag}";
	var taskComBoBoxUrl = "${ctx}/frs/rptfill/reject/taskNmComBoBox?orgTypes=${orgTypes}&flag=2&parentFlag=${parentFlag}";
	var rptComBoBoxUrl = "${ctx}/frs/rptfill/reject/rptNmComBoBox?parentFlag=${parentFlag}";
// 	var orgTreeSkipUrl = "${ctx}/rpt/frs/rptfill/reject/searchOrgTree";
	var lineComBoBoxUrl = "${ctx}/rpt/frs/rptfill/lineNmComBoBox";
	var columns = ["sts", "isUpt", "logicRs", "warnRs", "dataDate", "exeObjNmChild", "taskNm", "taskObjNm", "lineNm", "endTime"];
	var fields = ["taskNm", "rptNm", "handSts", "updateSts", "dataDate", "lineNm", "logicRsSts", "warnRsSts"];
	parent.parent.window.child = window;
	var download=null;
	var fileName = '';
	$(init);
	//初始化函数
	function init() {
		searchForm();
		initGrid();
		initButtons();
// 		InitSearchButtons();
		addMySearchButtons("#search", grid, "#searchbtn");
		initExport();
	}
	function initExport(){
		download = $('<iframe id="download"  style="display: none;"/>');
		$('body').append(download);
	}
	//搜索表单应用ligerui样式
	function searchForm() {
		CommonSerchForm(taskComBoBoxUrl, rptComBoBoxUrl, null, lineComBoBoxUrl, fields, "2", 2);
	}
	//初始化Grid
	function initGrid() {
		TaskFillGrid(gridUrl, columns, false);
	}
	//初始化Button
	function initButtons() {
		var btns = [{ text : '填报', click : oper_fill, icon : 'view', operNo : 'oper_fill'},
					{ text : '查看', click : oper_view, icon : 'view', operNo : 'oper_view'}];
		if("${parentFlag}" && "true" == "${parentFlag}"){
			btns.push({ text : '打回', click : oper_return, icon : 'down', operNo : 'oper_return'});
		}
		BIONE.loadToolbar(grid, btns, function() {});
	}
	function oper_return() {
		var manager = $("#maingrid").ligerGetGridManager();
		var rows = manager.getSelectedRows();
		if(rows.length > 0){
			var taskInsIds = [];
			for (var i = 0; i < rows.length; i++){
				if(rows[i].sts == '0'){
					BIONE.tip("含有未提交的记录");
					return;
				}else{
					taskInsIds.push(rows[i].taskInstanceId);
				}
			}
			if(taskInsIds.length > 0){
				$.ajax({
					cache : false,
					async : false,
					url : "${ctx}/rpt/frs/rptfill/flTaskChildSendList",
					type : "post",
					data : {
						taskInsIds : taskInsIds.join(",")
					},
					beforeSend : function() {
						BIONE.showLoading();
					},
					success : function(result){    
						BIONE.hideLoading();
						if(result){
							$.ajax({
								cache : false,
								async : false,
								url : "${ctx}/rpt/frs/rptfill/returnTaskBatch",
								dataType : 'json',
								type : "post",
								data : {
									taskInsIds : taskInsIds.join(",")
								},
								beforeSend : function() {
									BIONE.showLoading();
								},
								success : function(){
									BIONE.hideLoading();
									grid.loadData();
									BIONE.tip("打回成功");
								},
								error:function(){
									BIONE.hideLoading();
									//BIONE.tip("数据加载异常，请联系系统管理员");
								},
								complete : function() {
									BIONE.hideLoading();
								}
							});
						}else{
							BIONE.tip("含有没有权限的记录");
							return;
						}
					},
					error:function(){
						BIONE.hideLoading();
						//BIONE.tip("数据异常，请联系系统管理员");
					},
					complete : function() {
						BIONE.hideLoading();
					}
				});
			}else{
				BIONE.tip("数据异常，请联系系统管理员");
			}
		}else{
			BIONE.tip("请选择记录");
		}
	}
	function oper_check(){
		var manager = $("#maingrid").ligerGetGridManager();
		var rows = manager.getSelectedRows();
		if(rows.length > 0){
			var data = {};
			var objArr = [];
			for (var i = 0; i < rows.length; i++){
				//01:等待运行  02:运行中
				if ((rows[i].logicRs == '01' || rows[i].logicRs == '02') || (rows[i].warnRs == '01' || rows[i].warnRs == '02')){
					BIONE.tip("含有正在校验的记录，请等候");
					return;
				}else{
					var rptObj = {};
					rptObj.rptId = rows[i].taskObjId;
					rptObj.templateId = rows[i].templateId;
					rptObj.orgNo = rows[i].parentExeObjId;
					rptObj.dataDate = rows[i].dataDate;
					rptObj.lineId = rows[i].lineId;
					var obj = { "orgNo" : rows[i].parentExeObjId, "dataDate" : rows[i].dataDate, "logicRs" : rows[i].logicRs, "warnRs" : rows[i].warnRs, "lineId" : rows[i].lineId, "templateId" : rows[i].templateId};
					objArr.push(obj);
					// dataDate:orgNo,orgNo:tmpId,tmpId; ...
					if (!data[rows[i].dataDate]) { 
						data[rows[i].dataDate] = {}; 
						data[rows[i].dataDate].orgNo = {}; 
						data[rows[i].dataDate].templateId = {};
					}
					var orgNo = {}, rptId = {}, templateId = {};
					orgNo[rows[i].parentExeObjId] = ' ';
					templateId[rows[i].templateId] = ' ';
					$.extend(data[rows[i].dataDate].orgNo, orgNo);
					$.extend(data[rows[i].dataDate].templateId, templateId);
				}
			}
			var objArrParms = [], group = {};
			for (var i in data) {
				group = {};
				group.DataDate = i;
				group.OrgNo = [];
				group.LogicCheckRptTmpId = [];
				group.WarnCheckRptTmpId = [];
				for (var ii in data[i].orgNo) { group.OrgNo.push(ii);}
				for (var ii in data[i].templateId) { group.LogicCheckRptTmpId.push(ii); group.WarnCheckRptTmpId.push(ii);}
				objArrParms.push(JSON2.stringify(group));
			}
			$.ajax({
				cache : false, async : true,
				url : "${ctx}/rpt/frs/rptfill/judgeAndSaveCheckStsAyncChild",
				dataType : 'json',
				data : { objArr : JSON2.stringify(objArr), objArrParms : objArrParms.join(';')},//异步
				type : "post",
				success : function(result){ 
					if(result.result){
						if("OK" == result.result){
							grid.loadData();
//								BIONE.tip("开始校验");
						}else{
							BIONE.tip("校验引擎异常，请联系系统管理员");
						}
					}else{
						 BIONE.tip("数据异常，请联系系统管理员");
					}
				},
				error:function(){ //BIONE.tip("数据加载异常，请联系系统管理员");
				}
			});
		}else{
			BIONE.tip("请选择记录");
		}
	}
	function importData(){
		var rows = grid.getSelectedRows();
		if(rows.length == 1){
			if(rows[0].sts=="1"){
				BIONE.tip("该任务已提交，不可以导入");
			}else{
				var argsArr = [];
				var data = rows[0];
				var rptId = data.taskObjId;
				var templateId = data.templateId;
				var dataDate = data.dataDate;
				var orgNo = data.parentExeObjId;//上级任务实例机构标识
				var lineId = data.lineId;
				var taskInsId = data.taskInstanceId;
				var type = data.taskType;
				var exeObjId = data.exeObjId;//二次任务实例执行对象-用户
				BIONE.commonOpenDialog('导入数据文件','uploadWin',600,330,"${ctx}/rpt/frs/rptfill/showTab?path=rpt-todowork-uploadfile&dataDate=" + dataDate + "&orgNo=" + orgNo + "&rptId=" + rptId + "&templateId="  + templateId + "&taskInsId=" + taskInsId + "&lineId=" + lineId + "&type=" + type + "&exeObjId=" + exeObjId + "&flag=TWO&entry=GRID");
			}
		}else{
			BIONE.tip("请选择一条记录");
		}
	}
	//结束填报
	function oper_finish() {
		var manager = $("#maingrid").ligerGetGridManager();
		var rows = manager.getSelectedRows();
		var taskInsIds = [];
		if(rows.length > 0){
			var count = 0;
			for(var i in rows){
				if ((rows[i].logicRs == '01' || rows[i].logicRs == '02') || (rows[i].warnRs == '01' || rows[i].warnRs == '02')){
					BIONE.tip("含有正在校验的记录，无法结束填报");
					return;	
				}else{
					if(rows[i].sts == '0'){
						if((rows[i].logicRs == null || rows[i].logicRs == '04' || rows[i].logicRs == '06') || (rows[i].warnRs == null || rows[i].warnRs == '04' || rows[i].warnRs == '06')){
							count++;
							taskInsIds.push(rows[i].taskInstanceId);
						}else{
							taskInsIds.push(rows[i].taskInstanceId);
						}
					}else{
						BIONE.tip("含有已提交或者填报完成记录，无法结束填报");
						return;
					}
				}
			}
		}
		if(count > 0){
			$.ligerDialog.confirm("含有未通过或者未校验的记录，是否继续？", function(yes) {  
				if(yes){         
					if(taskInsIds.length > 0){
						$.ajax({
							cache : false,
							async : true,
							url : "${ctx}/rpt/frs/rptfill/finishTaskBatch",
							dataType : 'json',
							type : "post",
							data : {
								taskInsIds : taskInsIds.join(",")
							},
							success : function(){
								grid.loadData();
								BIONE.tip("填报完成");
							},
							error:function(){
								//BIONE.tip("数据加载异常，请联系系统管理员");
							}
						});
					}else{
						BIONE.tip("数据异常，请联系系统管理员");
					}
				}
			});
		}else{
			if(taskInsIds.length > 0){
				$.ajax({
					cache : false,
					async : true,
					url : "${ctx}/rpt/frs/rptfill/finishTaskBatch",
					dataType : 'json',
					type : "post",
					data : {
						taskInsIds : taskInsIds.join(",")
					},
					success : function(){
						grid.loadData();
						BIONE.tip("填报完成");
					},
					error:function(){
						//BIONE.tip("数据加载异常，请联系系统管理员");
					}
				});
			}else{
				BIONE.tip("数据异常，请联系系统管理员");
			}
		}
	}
	//提交
	function oper_submit() {
		var manager = $("#maingrid").ligerGetGridManager();
		var rows = manager.getSelectedRows();
		var taskInsIds = [];
		if(rows.length > 0){
			var count = 0;
			for(var i in rows){
				if ((rows[i].logicRs == '01' || rows[i].logicRs == '02') || (rows[i].warnRs == '01' || rows[i].warnRs == '02')){
					BIONE.tip("含有正在校验的记录，无法提交");
					return;	
				}else{
					if(rows[i].sts == '0'){
						if((rows[i].logicRs == null || rows[i].logicRs == '04' || rows[i].logicRs == '06') || (rows[i].warnRs == null || rows[i].warnRs == '04' || rows[i].warnRs == '06')){
							count++;
							taskInsIds.push(rows[i].taskInstanceId);
						}else{
							taskInsIds.push(rows[i].taskInstanceId);
						}
					}else{
						BIONE.tip("含有已提交记录，不可重复提交");
						return;
					}
				}
			}
		}
		if(count > 0){
			$.ligerDialog.confirm("含有未通过或者未校验的记录，是否继续？", function(yes) {
				if(yes){
					if(taskInsIds.length > 0){                         
						$.ajax({
							cache : false,
							async : true,
							url : "${ctx}/rpt/frs/rptfill/submitTaskBatch",
							dataType : 'json',
							type : "post",
							data : {
								taskInsIds : taskInsIds.join(",")
							},
							success : function(){
								grid.loadData();
								BIONE.tip("提交成功");
							},
							error:function(){
								//BIONE.tip("数据加载异常，请联系系统管理员");
							}
						});
					}else{
						BIONE.tip("数据异常，请联系系统管理员");
					}
				}
			});
		}else{
			if(taskInsIds.length > 0){
				$.ajax({
					cache : false,
					async : true,
					url : "${ctx}/rpt/frs/rptfill/submitTaskBatch",
					dataType : 'json',
					type : "post",
					data : {
						taskInsIds : taskInsIds.join(",")
					},
					success : function(){
						grid.loadData();
						BIONE.tip("提交成功");
					},
					error:function(){
						//BIONE.tip("数据加载异常，请联系系统管理员");
					}
				});
			}else{
				BIONE.tip("数据异常，请联系系统管理员");
			}
		}
	}
	//数据下载
	function oper_fdown() {
		var manager = $("#maingrid").ligerGetGridManager();
		var rows = manager.getSelectedRows();
		if(rows.length > 0){
			var argsArr = [];
			for(var i in rows){
				var rptId = rows[i].taskObjId;
				var dataDate = rows[i].dataDate;
				var orgNo = rows[i].parentExeObjId;
				var busiLineId = rows[i].lineId;
				if(rptId != null && orgNo != null && busiLineId != null && dataDate != null){
					var argsArr1 = [];
					var args1 = {'DimNo':'ORG','Op':'=','Value':orgNo};
					argsArr1.push(args1);
					var args = {'rptId':rptId,'dataDate':dataDate,'busiLineId':busiLineId,'searchArgs':JSON2.stringify(argsArr1)};
					argsArr.push(args);
				}else{
					BIONE.tip("数据异常，请联系系统管理员");
				}
			}
			if(argsArr.length > 0){   
				var src = '';
				src = "${ctx}/rpt/frs/rptfill/downloadList?json="+JSON2.stringify(argsArr)+"&d="+ new Date().getTime();
// 				window.open(src, '数据下载');
				download.attr('src', src);
			}else{
				BIONE.tip("数据异常，请联系系统管理员");
			}
		}
	}
	function oper_fill() {
		fileName = '';
		var manager = $("#maingrid").ligerGetGridManager();
		var rows = manager.getSelectedRows();
		if(rows.length == 1){
			var data = rows[0];
			var orgNm = data.parentExeObjNm;
			var rptId = data.taskObjId;
			var templateId = data.templateId;
			var orgNo = data.parentExeObjId;    
			var dataDate = data.dataDate;
			BIONE.ajax({
				async : false,
				url : "${ctx}/rpt/frs/rptfill/createColor",
				dataType : 'text',
				type : 'POST',
				data : {
					rptId : rptId,
					orgNo : orgNo,
					dataDate : dataDate,
					tmpId : templateId,
					isBatchCheck : true
				}
			}, function(result) {
				if("1" == data.sts){ 
					BIONE.tip("任务已归档，不能填报！");
				}else if( "2" == data.sts){
					BIONE.tip("任务已复核，不能填报！");
				}else if("3" == data.sts){
					BIONE.tip("任务已结束填报，不能填报！");
					}else { 
					var taskInsId = data.taskInstanceId;
					var rptNm = data.taskObjNm;
					var title = "当前报表:" + data.taskObjNm;
					var exeObjId = data.exeObjId;
					var lineId = data.lineId;
					var type = data.taskType;
					var height = $(parent.parent.window).height() - 10;                                         
					var width = $(parent.parent.window).width();
					window.top.color=result;
					window.parent.parent.BIONE.commonOpenDialog(title, "taskFillChildWin", width, height, "${ctx}/rpt/frs/rptfill/showTab?path=rptfill-todowork-oper-child&dataDate=" + dataDate + "&exeObjId=" + exeObjId + "&rptId=" + rptId + "&templateId=" + templateId + "&taskInsId=" + taskInsId + "&lineId=" + lineId + "&orgNo=" + orgNo + "&rptNm=" + encodeURI(encodeURI(rptNm)) + "&type=" + type + "&orgNm=" + encodeURI(encodeURI(orgNm)), null);
				}
			});
		}else{
			BIONE.tip("请选择一条任务记录");
		}

	}
	function oper_view(){
		fileName = '';
		var manager = $("#maingrid").ligerGetGridManager();
		var rows = manager.getSelectedRows();
		if(rows.length == 1){
			var data = rows[0];
			var orgNm = data.parentExeObjNm;
			var rptId = data.taskObjId;
			var templateId = data.templateId;
			var orgNo = data.parentExeObjId;
			var dataDate = data.dataDate;
			BIONE.ajax({
				async : false,
				url : "${ctx}/rpt/frs/rptfill/createColor",
				dataType : 'text',
				type : 'POST',
				data : {
					rptId : rptId,
					orgNo : orgNo,
					dataDate : dataDate,
					tmpId : templateId,
					isBatchCheck : true
				}
			}, function(result) {
					var taskInsId = data.taskInstanceId;
					var rptNm = data.taskObjNm;
					var title = "当前报表:" + data.taskObjNm;
					var exeObjId = data.exeObjId;
					var lineId = data.lineId;
					var height = $(parent.parent.window).height() - 10;
					var width = $(parent.parent.window).width();
					window.top.color=result;                                           
					window.parent.parent.BIONE.commonOpenDialog(title, "taskViewChildWin", width, height, "${ctx}/rpt/frs/rptfill/showTab?path=rptfill-todowork-view-child&dataDate=" + dataDate + "&exeObjId=" + exeObjId + "&rptId=" + rptId + "&taskInsId=" + taskInsId + "&lineId=" + lineId + "&orgNo=" + orgNo + "&rptNm=" + encodeURI(encodeURI(rptNm)) + "&orgNm=" + encodeURI(encodeURI(orgNm)), null);
			});
		}else{
			BIONE.tip("请选择一条任务记录");
		}
	}
	// 创建表单搜索按钮：搜索、高级搜索
	function addMySearchButtons(form, grid, btnContainer) {
		if (!form) return;
		form = $(form);
		if (btnContainer) {
			BIONE.createButton({
				appendTo : btnContainer, text : '搜索', icon : 'search3', width : '50px',
				click : function() {
					if("" != $.ligerui.get("logicRsSts_sel").getValue()){
						grid.setParm("logicRsSts",$.ligerui.get("logicRsSts_sel").getValue());
					}else{
						grid.removeParm("logicRsSts");
					}
					if("" != $.ligerui.get("warnRsSts_sel").getValue()){
						grid.setParm("warnRsSts",$.ligerui.get("warnRsSts_sel").getValue());
					}else{
						grid.removeParm("warnRsSts");
					}
					var rule = BIONE.bulidFilterGroup(form);
					/**
					 * edit in 2014-08-14 by caiqy
					 */
					if (rule.rules.length) {
						grid.setParm("condition",JSON2.stringify(rule));
						grid.setParm("newPage",1);
					} else {
						grid.setParm("condition","");
						grid.setParm('newPage', 1);
					}
					grid.loadData();
				}
			});
			BIONE.createButton({
						appendTo : btnContainer,
						text : '重置',
						icon : 'refresh2',
						width : '50px',
						click : function() {
							$(":input", form).not(":submit, :reset,:hidden,:image,:button, [disabled]").each(function() {$(this).val("");});
							$(":input[ltype=combobox]", form).each(
											function() {
												var ligerid = $(this).attr('data-ligerid'), ligerItem = $.ligerui.get(ligerid);// 需要配置comboboxName属性
												if (ligerid && ligerItem && ligerItem.clear) {// ligerUI
													// 1.2
													// 以上才支持clear方法
													ligerItem.clear();
												} else {
													$(this).val("");
												}
												grid.removeParm("logicRsSts");
												grid.removeParm("warnRsSts");
											});
							$(":input[ltype=select]", form).each(
											function() {
												var ligerid = $(this).attr('data-ligerid'), ligerItem = $.ligerui.get(ligerid);// 需要配置comboboxName属性
												if (ligerid && ligerItem && ligerItem.clear) {// ligerUI
													// 1.2
													// 以上才支持clear方法
													ligerItem.clear();
												} else {
													$(this).val("");
												}
												grid.removeParm("logicRsSts");
												grid.removeParm("warnRsSts");
											});
						}
			});
		}
	}
</script>

</head>
<body>
</body>
</html>