<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<meta name="decorator" content="/template/template1_BS.jsp">
<script type="text/javascript">var ctx="${ctx}"</script>
<script type="text/javascript" src="${ctx}/frs/js/rptfill/TaskFill.js"></script>
<script type="text/javascript">

	var doFlag="";
    var grid;
    var taskType = '${taskType}';
	var gridUrl = "${ctx}/frs/rpttsk/monitor/getInsList?taskId=${taskId}&isCellBack=1&dataDate=${dataDate}&orgTypes=${orgTypes}";
	var taskComBoBoxUrl = "${ctx}/frs/rptfill/reject/taskNmComBoBox?orgTypes=${orgTypes}&flag=1&taskType=${taskType}";
	var rptComBoBoxUrl = "${ctx}/frs/rptfill/reject/rptNmComBoBox";
	var orgTreeSkipUrl = "${ctx}/frs/rptfill/reject/searchOrgTree";
	var columns = ["taskNm", "taskObjNm","exeObjNm", "exeObjId", "sts","dataDate"];
	var fields = [ "taskNm","rptNm", "orgNm", "handSts"];//"taskNm",
	var taskId = "${taskId}";
	$(init);
	//初始化函数 
	function init() {
		searchForm();
		initGrid();
		initButtons();
		myAddSearchButtons("#search", grid, "#searchbtn");
		
		//任务下拉框禁止操作
		$.ligerui.get("taskNm_sel").selectValue(taskId);
		$.ligerui.get("taskNm_sel").setDisabled();//.attr("readOnly","true");
		$("#taskNm_sel").css("color","#666");
		$.ligerui.get("taskNm_sel").updateStyle();
	}
	//搜索表单应用ligerui样式
	function searchForm() {
		CommonSerchForm(taskComBoBoxUrl, rptComBoBoxUrl, orgTreeSkipUrl, null, fields,"1",taskType);
	}
	//初始化Grid
	function initGrid() {
		TaskFillGrid(gridUrl, columns, false);
	}
	//初始化Button
	function initButtons() {
		var btns = [{ text : '任务回收', click : tskCallback, icon : 'back', operNo : 'tsk_back'},{ text : '回收全部任务', click : tskAllCallback, icon : 'back', operNo : 'tsk_back'}
					];
		BIONE.loadToolbar(grid, btns, function() {
		});
	}
	//任务回收
	function tskCallback() {
		$.ligerDialog.confirm('确实要回收该条任务实例吗?', function(yes) {
			if(yes) {
				var rows = grid.getSelectedRows();
				var flag=false;
				//若是过滤查询后，逻辑就不准
				if(rows.length>=grid.data.Total)
					flag=true; 
				if(rows.length>0){
					var insIds = "";
					for(var i =0;i<rows.length;i++){
						if(i==rows.length-1){
							insIds = insIds + rows[i].taskInstanceId
						}else{
							insIds = insIds + rows[i].taskInstanceId+","
						}
					}
					if(insIds!=""){
						$.ajax({
							cache : false,
							async : true,
							url : "${ctx}/frs/rpttsk/monitor/deleteTskInsByInsIds?d=" + new Date().getTime(),
							dataType : 'json',
							type : "get",
							data : {
								"insIds" : insIds,
								"flag": flag,
								"taskId": taskId,
								"dataDate": "${dataDate}"
							},
							beforeSend : function() {
								BIONE.showLoading("任务回收中...");
							},
							success : function(result) {
								BIONE.hideLoading();
								if(flag){
									parent.grid.loadData();
									BIONE.tip("任务回收成功");
									BIONE.closeDialog("callbackTskWin");
								}
								else{
									grid.loadData();
									BIONE.tip("任务回收成功");
								}
								
							},
							error : function(result, b) {
								//BIONE.tip('发现系统错误 <BR>错误码：' + result.status);
							}
						});
					}else{
						BIONE.tip("请选择记录");
					}
				}else{
					BIONE.tip("请选择记录");
				}
			}
		});
	}
	// 回收全部任务
	function tskAllCallback(){
		$.ligerDialog.confirm('确实要回收所有的任务实例吗?', function(yes) {
			if(yes) {
				$.ajax({
					cache : false,
					async : true,
					url : "${ctx}/frs/rpttsk/monitor/deleteAllTskInsByInsIds?d=" + new Date().getTime(),
					dataType : 'json',
					type : "get",
					data : {
						"taskId": taskId,
						"dataDate": "${dataDate}"
					},
					beforeSend : function() {
						BIONE.showLoading("任务回收中...");
					},
					success : function(result) {
						BIONE.hideLoading();
						parent.grid.loadData();
						BIONE.tip("任务回收成功");
						BIONE.closeDialog("callbackTskWin");
					},
					error : function(result, b) {
						//BIONE.tip('发现系统错误 <BR>错误码：' + result.status);
					}
				});
			}
		});
	}
	function myAddSearchButtons (form, grid, btnContainer) {
		if (!form)
			return;
		form = $(form);
		if (btnContainer) {
			BIONE.createButton({
				appendTo : btnContainer,
				text : '搜索',
				icon : 'search3',
				width : '50px',
				click : function() {
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
			BIONE
					.createButton({
						appendTo : btnContainer,
						text : '重置',
						icon : 'refresh2',
						width : '50px',
						click : function() {
							$(":input", form)
									.not(
											":submit, :reset,:hidden,:image,:button, [disabled]")
									.each(function() {
										if(this.id!="taskNm_sel"){
											$(this).val("");
										}
									});
							$(":input[ltype=combobox]", form)
									.each(
											function() {
												var ligerid = $(this).attr(
														'data-ligerid'), ligerItem = $.ligerui
														.get(ligerid);// 需要配置comboboxName属性
												if (ligerid && ligerItem
														&& ligerItem.clear) {// ligerUI
													// 1.2
													// 以上才支持clear方法
													ligerItem.clear();
												} else {
													$(this).val("");
												}
											});
							$(":input[ltype=select]", form)
									.each(
											function() {
												if(this.id!="taskId"){
													var ligerid = $(this).attr(
															'data-ligerid'), ligerItem = $.ligerui
															.get(ligerid);// 需要配置comboboxName属性
													if (ligerid && ligerItem
															&& ligerItem.clear) {// ligerUI
														// 1.2
														// 以上才支持clear方法
														ligerItem.clear();
													} else {
														$(this).val("");
													}
												}
												
											});
						}
					});
			BIONE.createButton({
				appendTo : btnContainer,
				text : '关闭',
				icon : 'close3',
				width : '50px',
				click : function() {
					BIONE.closeDialog("callbackTskWin");
				}
			});
		}
	}
</script>

</head>
<body>
</body>
</html>