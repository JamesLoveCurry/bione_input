<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<meta name="decorator" content="/template/template1_BS.jsp">
<script type="text/javascript">var ctx="${ctx}"</script>
<script type="text/javascript" src="${ctx}/frs/js/rptsubmit/rpt-submit-index.js" ></script>
<script type="text/javascript">
    var doFlag ="";
    var operType ="${operType}";
    var moduleType ="${moduleType}";
	var p_backNode = null;
	var p_taskInsIds = null;
	var	p_moduleType = "${moduleType}";
	var	p_operType = "${operType}";
    var rows = null;
    var fromMainPage = "${fromMainPage}";
    var taskInstanceId = "${taskInstanceId}";
	$(function (){
		var rowMap = null;
		tmp.searchForm();
		TaskFillGrid(tmp.gridUrl, tmp.columns, false,null,null,false,null,operType);
		tmp.initButtons();
		tmp.addMySearchButtons("#search", grid, "#searchbtn");
		tmp.reloadData();
	});
	$(window).load(function(){//数据加载完成后模拟点击事件
		setTimeout(openDetail,1000);
	});
	function openDetail(){
		//要执行的方法体
		if(fromMainPage != null && fromMainPage == "1"){//首页待办进入，直接跳转到查看报表页面
			$("#" + taskInstanceId).click();
			grid.options.url = "${ctx}/frs/rptsubmit/submit/flTaskList.json?orgTypes=${orgTypes}&operType=${operType}&taskType=${moduleType}&doFlag="+doFlag;
			fromMainPage = "0";
		}
	}
	var tmp = {
			gridUrl : "${ctx}/frs/rptsubmit/submit/flTaskList.json?orgTypes=${orgTypes}&operType=${operType}&taskType=${moduleType}&doFlag="+doFlag+"&taskInstanceId=" +taskInstanceId,
			taskComBoBoxUrl : "${ctx}/frs/rptsubmit/submit/taskNmComBoBox.json?orgTypes=${moduleType}&flag=1&doFlag=rpt-submit-index",
			rptComBoBoxUrl : "${ctx}/frs/rptsubmit/submit/rptNmComBoBox?doFlag=rpt-submit-index",
			orgTreeSkipUrl : "${ctx}/frs/integratedquery/rptquery/searchOrgSetInfo",		
			//modify by wusb at 20161207 添加零值校验
			columns : ["taskNm", "dataDate", "endTime", "taskObjNm", "exeObjNm", "finishSts", "logicRs","applyUserNm", "isForceSubmit", "forceSubmitReason"],
			
			fields : ["taskNm", "rptNm", "orgNm", "dataDate", "finishSts", "logicRsSts"],
			searchForm : function () {
				var demoWidth = $("#search").width();
				var newLineNum = parseInt(demoWidth/260);
				CommonSerchForm(this.taskComBoBoxUrl, this.rptComBoBoxUrl, this.orgTreeSkipUrl, null, this.fields, "1", newLineNum, operType);
			},
			//驳回
			oper_return : function() {
				var manager = $("#maingrid").ligerGetGridManager();
				var rows = manager.getSelectedRows();
				var str = null;
				//因为当前经过审核环节的有多种流程，故只允许驳回一条数据 20190826
				if(rows.length >= 1){
					var backNode = false;	// 是否需要在驳回理由界面上显示打回节点选择
					//找出第一条记录对应的任务id和相关信息，用作判断条件，就是不同的任务可能对应的流程不一致，所以不同流程不能进行批量驳回
					var base_taskId = rows[0].taskId;
					var base_id = rows[0].taskId + "," + rows[0].taskType + "," + rows[0].dataDate + "," + rows[0].taskObjId + "," + rows[0].exeObjId;
					var taskInstanceIds = "";
					for(var rowNo in rows){
						var row = rows[rowNo];
						//判断是否勾选了不同的任务下的报表，因为不同的任务可能对应的流程不一致，批量驳回只能进行相同任务的驳回
						if(base_taskId != row.taskId){
							BIONE.tip("批量驳回只能操作同一个任务下的报表");
							return;
						}
						if(rows.sts == '0'){
							BIONE.tip("含有未归档的记录");
							return;
						}else if(operType =='01' && row.sts == '2'){ //20190724 待归档(已复核)的任务，不允许在复核页面驳回
							BIONE.tip("含有已复核的记录");
							return;
						}else if((operType =='01' || operType =='02' ) && row.sts == '3'){ //20190725 已归档的任务，不允许驳回
							BIONE.tip("含有已归档的记录");
							return;
						}else{
							if (row.sts == '2' || row.sts == '3') {
								backNode = true;
							}
						}
						taskInstanceIds += row.taskInstanceId + ",";
					}
					taskInstanceIds = taskInstanceIds.substring(0, taskInstanceIds.length - 1);
					if(base_id){
						$.ajax({
							cache : false,
							async : false,
							url : "${ctx}/frs/rptsubmit/submit/returnJudge",
							dataType : 'json',
							type : "post",
							data : {
								params : base_id,
								sts : "1,2,3"
							},
							success : function(result){
								if(result.result == "ERROR"){
									BIONE.tip("数据异常，请联系系统管理员");
									return;
								}
								//新增的驳回理由弹出界面
								window.taskInsIds= taskInstanceIds;
								p_taskInsIds= taskInstanceIds;
								p_backNode = backNode;
								//判断驳回任务的流程类型，新增的 <开始-审核-结束>流程只能驳回到开始  20190827
								var prodefSet =result.prodefSet;
								if(prodefSet.indexOf("frs_process2_2") > 0){
									p_backNode = false;
								}
								BIONE.commonOpenDialog("驳回理由", "approveRejWin", "500", "350", "${ctx}/frs/rptsubmit/submit/approveRejectForSubmit", null);
							},
							error:function(){
								BIONE.tip("数据加载异常，请联系系统管理员");
							}
						});
						
					}else{
						BIONE.tip("数据异常，请联系系统管理员");
					}
				}else{
					BIONE.tip("请选择一条记录！");
				}
			},
			//查看流程信息
			oper_check : function(){
				var urlp = "${ctx}/rpt/frs/rptfill/getProcessId";
				var manager = $("#maingrid").ligerGetGridManager();
				var height = $(parent.parent.parent.window).height() - 5;
				var width = $(parent.parent.parent.window).width() + 10;
				var rows = manager.getSelectedRows();
				
				if (rows.length != 1) {
					BIONE.tip("请选择一条数据");
					return;
				}
				var data = {
						orgNo : rows[0].exeObjId,
						taskInsId : rows[0].taskInstanceId
				} 
				
				$.ajax({
					cache : false,
					async : true,
					url : urlp,
					dataType : "json",
					type : "post",
					data : data,
					beforeSend : function(){
						BIONE.loading = true;
						BIONE.showLoading("正在加载数据中...");
					},
					success : function(result){
						if(result){
							processDefinitionId = result.processDefinitionId;
							processInstanceId = result.processInstanceId;
							window.parent.parent.parent.BIONE.commonOpenDialog("查看流程", "processInfo", width/1.5, height/1.5, "${ctx}/activiti/processInfo?processDefinitionId="+result.processDefinitionId +"&processInstanceId="+result.processInstanceId,null,tmp.closeTab);
						}
					}
				});
			},
			
			closeTab : function(){
			    BIONE.hideLoading();
			},
			//提交按钮
			oper_submit : function(){
				var manager = $("#maingrid").ligerGetGridManager();
				var rows = manager.getSelectedRows();
				var taskInsIds = [];
				var finishSts =[];
				var params = [];
				var checkFlg = 0;
				var checkParams = [];
					if(rows.length > 0){
					var count = 0;
					function checkout_(){
						if((rows[i].logicRs == null || rows[i].logicRs == '04' || rows[i].logicRs == '06') || (rows[i].sumpartRs == null || rows[i].sumpartRs == '04' || rows[i].sumpartRs == '06') || 
								(rows[i].warnRs == null || rows[i].warnRs == '04' || rows[i].warnRs == '06')||
								(rows[i].zeroRs == null || rows[i].zeroRs == '04' || rows[i].zeroRs == '06')){//modify by wusb at 20161207 添加零值校验
							count++;
							taskInsIds.push(rows[i].taskInstanceId);
							finishSts.push(rows[i].sts);
							var param = {'taskIns' : rows[i].taskInstanceId, 'orgNo' : rows[i].exeObjId, 'rptId' : rows[i].taskObjId, 'dataDate' : rows[i].dataDate, 'type' : rows[i].taskType,'sts':rows[i].sts};
							params.push(param);
						}else{
							taskInsIds.push(rows[i].taskInstanceId);
							finishSts.push(rows[i].sts);
							var param = {'taskIns' : rows[i].taskInstanceId, 'orgNo' : rows[i].exeObjId, 'rptId' : rows[i].taskObjId, 'dataDate' : rows[i].dataDate, 'type' : rows[i].taskType,'sts':rows[i].sts};
							params.push(param);
						}
					};
					var simpleOrg = null ;
					var taskId = "";
					for(var i in rows){
						taskId += rows[i].taskId;
						/* if(simpleOrg == null )
							simpleOrg = rows[i].exeObjId;
						else
						{
							if(simpleOrg!=rows[i].exeObjId){
								BIONE.tip("请选择同一机构进行审批操作");
								return;	
							}
						} */
						//modify by wusb at 20161207 添加零值校验
						if ((rows[i].logicRs == '01' || rows[i].logicRs == '02') || (rows[i].sumpartRs == '01' || rows[i].sumpartRs == '02') || (rows[i].warnRs == '01' || rows[i].warnRs == '02')|| (rows[i].zeroRs == '01' || rows[i].zeroRs == '02')){
							BIONE.tip("含有正在校验的记录，无法提交");
							return;	
						}else{
							if(operType=="01"){
								if(rows[i].sts == '1'){
									checkout_();
								}else{
									BIONE.tip("含有已复核记录，不可重复复核");
									return;
								}
							}else if(operType=="02"){
								if(rows[i].sts == '2'){
									checkout_();
								}else{
									BIONE.tip("含有已审核记录，不可重复审核");
									return;
								}
							}else{
								if(rows[i].sts == '0'){
									checkout_();
								}else{
									BIONE.tip("含有已归档记录，不可重复提交");
									return;
								}
							}
							
						}
						/* if(rows[i].taskType == "03"){//任务类型 利率报备（暂时不需要）
							checkFlg = 1;
							var param = {'orgNo' : rows[i].exeObjId, 'rptId' : rows[i].taskObjId, 
									 'rptNm' : rows[i].taskObjNm,'dataDate' : rows[i].dataDate,
									 'orgNm' : rows[i].exeObjNm,'taskInsId' : rows[i].taskInstanceId,'sts':rows[i].sts};
							checkParams.push(param);
						} */
					}
					//复核需要选择审核人  mod by chenl 2017年5月5日
					if(operType=="01"){
							data="ignore";
							if(data&&data!=null&&data!=""&&data!="ignore"){
								$.ajax({
									cache : false,
									async : false,
									url : "${ctx}/frs/rptsubmit/submit/submitTaskBatchIndex?queryType=shenhe&moduleType=${moduleType}&operType="+operType+"&sts="+finishSts+"&doFlag=rpt-submit-index&taskInsIds=" + taskInsIds.join(",")+"&authUsers=" + data+"&org="+simpleOrg+"&taskId="+taskId+"&d="+new Date(),
									dataType : 'json',
									type : "get",
									success : function(){
										grid.loadData();
									if(operType=="01"){
										BIONE.tip("复核成功");
									}else if(operType=="02"){
										BIONE.tip("审核成功");
									}
									},
									error:function(){
										grid.loadData();
										if(operType=="01"){
											BIONE.tip("复核失败");
										}else if(operType=="02"){
											BIONE.tip("审核失败");
										}
									}
								});
							}else if(data&&data!=null&&data=="ignore"){
								$.ajax({
									cache : false,
									async : false,
									url : "${ctx}/frs/rptsubmit/submit/submitTaskBatchIndex?moduleType=${moduleType}&ignore=Y&operType="+operType+"&sts="+finishSts+"&taskInsIds=" + taskInsIds.join(",")+"&authUsers=" + data+"&d="+new Date(),
									dataType : 'json',
									type : "get",
									success : function(){
										grid.loadData();
									if(operType=="01"){
										BIONE.tip("复核成功");
									}else if(operType=="02"){
										BIONE.tip("审核成功");
									}
									},
									error:function(){
										grid.loadData();
										if(operType=="01"){
											BIONE.tip("复核失败");
										}else if(operType=="02"){
											BIONE.tip("审核失败");
										}
									}
								});
							}
					}else{
						$.ajax({
							cache : false,
							async : false,
							url : "${ctx}/frs/rptsubmit/submit/submitTaskBatchIndex?moduleType=${moduleType}&operType="+operType+"&sts="+finishSts+"&doFlag=rpt-submit-index&taskInsIds=" + taskInsIds.join(",")+"&org="+simpleOrg+"&taskId="+taskId+"&d="+new Date(),
							dataType : 'json',
							type : "get",
							success : function(){
								manager.loadData();
							if(operType=="01"){
								BIONE.tip("复核成功");
							}else if(operType=="02"){
								BIONE.tip("审核成功");
							}
							},
							error:function(){
								manager.loadData();
								if(operType=="01"){
									BIONE.tip("复核失败");
								}else if(operType=="02"){
									BIONE.tip("审核失败");
								}
							}
						});
					}
				}else{
					BIONE.tip("请选择记录");
					return;
				}
			},
			initButtons : function () {
				if(operType=="01"){
					var btns = [ 
					             { text : '通过复核', click : tmp.oper_submit, icon : 'fa-check', operNo : 'oper_submit'},
					             { text : '驳回', click : tmp.oper_return, icon : 'fa-exclamation-triangle', operNo : 'oper_return'},
					             { text : '查看流程信息', click : tmp.oper_check, icon : 'fa-exchange', operNo : 'oper_return'}
					             ];
				}else if(operType=="02"){
					var btns = [ 
					             { text : '通过审核', click : tmp.oper_submit, icon : 'fa-check', operNo : 'oper_submit'},
					             { text : '驳回', click : tmp.oper_return, icon : 'fa-exclamation-triangle', operNo : 'oper_return'},
					             { text : '查看流程信息', click : tmp.oper_check, icon : 'fa-exchange', operNo : 'oper_return'}
					             ];
				}else{
					var btns = [ 
					             { text : '提交', click : tmp.oper_submit, icon : 'fa-check', operNo : 'oper_submit'},
					             { text : '上级打回', click : tmp.oper_return, icon : 'fa-exclamation-triangle', operNo : 'oper_return'}
					             ];
				}
    			BIONE.loadToolbar(grid, btns, function() {});
			},
			reloadData : function (){
				if($.ligerui.get("logicRsSts_sel") && $.ligerui.get("logicRsSts_sel").getValue()){
					grid.setParm("logicRsSts",$.ligerui.get("logicRsSts_sel").getValue());
				}else{
					grid.removeParm("logicRsSts");
				}
				if($.ligerui.get("sumpartRsSts_sel") && $.ligerui.get("sumpartRsSts_sel").getValue()){
					grid.setParm("sumpartRsSts",$.ligerui.get("sumpartRsSts_sel").getValue());
				}else{
					grid.removeParm("sumpartRsSts");
				}
				if($.ligerui.get("warnRsSts_sel") && $.ligerui.get("warnRsSts_sel").getValue()){
					grid.setParm("warnRsSts",$.ligerui.get("warnRsSts_sel").getValue());
				}else{
					grid.removeParm("swarnRsSts");
				}
				if($.ligerui.get("zeroRsSts_sel") && $.ligerui.get("zeroRsSts_sel").getValue()){
					grid.setParm("zeroRsSts",$.ligerui.get("zeroRsSts_sel").getValue());
				}else{
					grid.removeParm("zeroRsSts");
				}
				var form = $("#search");
				var rule = BIONE.bulidFilterGroup(form);
				if (rule.rules.length) {
					grid.setParm("condition",JSON2.stringify(rule));
					grid.setParm("newPage",1);
				} else {
					grid.setParm("condition","");
					grid.setParm('newPage', 1);
				}
				grid.loadData();
			},
			// 创建表单搜索按钮：搜索、高级搜索
			addMySearchButtons : function(form, grid, btnContainer) {
				if (!form) return;
				form = $(form);
				if (btnContainer) {
					BIONE.createButton({
						appendTo : btnContainer, text : '查询', icon : 'fa-search',
						click : tmp.reloadData
					});
					 BIONE.createButton({
								appendTo : btnContainer,
								text : '重置',
								icon : 'fa-repeat',
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
														grid.removeParm("sumpartRsSts");
														grid.removeParm("warnRsSts");
														//add by wusb at 20161207 添加零值校验
														grid.removeParm("zeroRsSts");
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
														grid.removeParm("sumpartRsSts");
														grid.removeParm("warnRsSts");
														//add by wusb at 20161207 添加零值校验
														grid.removeParm("zeroRsSts");
													});
								}
					}); 
				}
			}
	};
	

	
</script>

</head>
<body>
</body>
</html>