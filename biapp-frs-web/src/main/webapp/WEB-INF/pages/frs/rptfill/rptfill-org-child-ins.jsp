<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<meta name="decorator" content="/template/template8.jsp">
<%@ include file="/common/spreadjs_load.jsp"%>
<script type="text/javascript">var ctx="${ctx}"</script>
<script type="text/javascript" src="${ctx}/frs/js/rptfill/TaskFill.js" ></script>
<script type="text/javascript">
 	var btnFlag = "${btnFlag}";
	var orgNo = "${orgNo}";
	var rptId = "${rptId}";
	var dataDate = "${dataDate}";
	var lineId = "${lineId}";
	var isLine = "${isLine}";
	var taskId = "${taskId}";
	var checkType = "${checkType}";
	var logicRs = "${logicRs}";
	var sumpartRs = "${sumpartRs}";
	var warnRs = "${warnRs}";
	var zeroRs = "${zeroRs}";
	var taskType = "${taskType}";
	var operType = '03';
	var operatType = 'hedui';//邮件操作类型
 	var sts = "${sts}";
 	var params = '${params}'; 
 	var tmpId = '${tmpId}';
	var isForceSubmit = '${isForceSubmit}';
 	if(window.parent.submitJudgeParams){
 		params = window.parent.submitJudgeParams;
 	}
	var grid;
	var ins="${ins}";	
	var taskInsIds="${taskInsIds}";
	var taskInsId = "${taskInsIds}"; 
 	if(window.parent.submitJudgetaskInsIds){
 		taskInsIds = window.parent.submitJudgetaskInsIds;
 	}
	var columns = ["finishSts", "dataDate", "exeObjId", "exeObjNm", "taskNm", "taskObjNm", "endTime", "sumpartRs", "logicRs", "warnRs", "zeroRs"];
	var templateType = "${templateType}";
	var sumDataType = "${sumDataType}";
	$(function() {
		//初始化grid		
		TaskFillGrid("${ctx}/rpt/frs/rptfill/taskChildIns?ins=" + ins, columns, false,null,true,true);
		grid.setParm("sts",sts);
		grid.setParm("params",params); 
		grid.setParm("isLine",isLine);
		/* grid.setParm("upOrgNo",orgNo) */
		grid.loadData();
		//初始化按钮
		initButtons();
	});
	
	//初始化按钮
	function initButtons() {
		var btns;
		if(btnFlag == "01"){
			btns = [ { text : '继续提交', click : submit, icon : 'up', operNo : 'oper_submit'}];
		}else if(btnFlag == "02"){  //条线汇总
			btns = [ { text : '继续汇总', click : caculateInfo, icon : 'up', operNo : 'oper_submit'},
			         {text : '取消汇总' , click : submit_close, icon :'delete', operNo : 'oper_submit'}];
		}else if(btnFlag == "03"){  //机构汇总
			btns = [ { text : '继续汇总', click : sumDataInfo, icon : 'up', operNo : 'oper_submit'},
			         {text : '取消汇总' , click : submit_close, icon :'delete', operNo : 'oper_submit'}];
		}else if(btnFlag == "04"){  //校验
			btns = [ { text : '继续校验', click : checkSingleInfo, icon : 'up', operNo : 'oper_submit'},
			         {text : '取消校验' , click : submit_close, icon :'delete', operNo : 'oper_submit'}];
		}
		
		BIONE.loadToolbar(grid, btns, function() {});
	}
	//校验
	function checkSingleInfo(){
		if(orgNo && rptId && dataDate && lineId && taskType && checkType && taskInsId && taskId)
		$.ajax({
			cache : false,
			async : true,
			url : "${ctx}/rpt/frs/rptfill/getTmpId",
			dataType : 'text',
			beforeSend : function() {
				BIONE.showLoading("数据校验中，请稍等...");
			},
			data : {
				rptId : rptId, dataDate : dataDate, lineId : lineId
			},
			type : "post",
			success : function(result){
				if(result){
					var objArr = [];
					var obj;
					var group = {};
					group.DataDate = dataDate;
					group.OrgNo = orgNo;
					if("logic" == checkType){
						//group.RealTimeLogicCheckRptTmpId = [result];
						//民泰修改同步为异步
						group.LogicCheckRptTmpId = [result];
						obj = {"rptId" : rptId, "orgNo" : orgNo, "dataDate" : dataDate, "logicRs" : logicRs, "lineId" : lineId, "templateId" : result};
					}
					if("sumpart" == checkType){
						//group.RealTimeSumCheckRptTmpId = [result];
						//民泰修改同步为异步
						group.SumCheckRptTmpId = [result];
						obj = {"rptId" : rptId, "orgNo" : orgNo, "dataDate" : dataDate, "sumpartRs" : sumpartRs, "lineId" : lineId, "templateId" : result};
					}
					if("warn" == checkType){
						//group.RealTimeWarnCheckRptTmpId = [result];
						//民泰修改同步为异步
						group.WarnCheckRptTmpId = [result]; 
						obj = {"rptId" : rptId, "orgNo" : orgNo, "dataDate" : dataDate, "warnRs" : warnRs, "lineId" : lineId, "templateId" : result};
					}
					if("zero" == checkType){
						group.RealTimeZeroCheckRptTmpId = [result];
						//民泰修改同步为异步
						//group.ZeroCheckRptTmpId = [result]; 
						obj = {"rptId" : rptId, "orgNo" : orgNo, "dataDate" : dataDate, "zeroRs" : zeroRs, "lineId" : lineId, "templateId" : result};
					}
					objArr.push(obj);
						$.ajax({
							cache : false,
							async : false,
							url : "${ctx}/rpt/frs/rptfill/judgeAndSaveCheckStsSync",
							dataType : 'json',
							type : 'POST',
							data : {
								objArr : JSON2.stringify(objArr),
								objArrParms : JSON2.stringify(group),
								dataDate : dataDate,
								tmpId : result,
								orgNo : orgNo,
								taskId : taskId,
								taskType: taskType,
								taskInsId :taskInsId,
								checkType: checkType,
								operType: operType, 
								taskFillOperType : '37'
							},
							complete: function(){
								BIONE.closeDialog("taskInsChildWin");
								BIONE.hideLoading();
							},
							success: function(result) {
 							if(result.result){
 								if("OK" == result.result){
 									//BIONE.tip("已发送校验，请您稍等片刻 ");
 									window.parent.tmp.initGridData();
 									var color = result.color;
 									window.parent.View.ajaxJson(color);
 									/* if(null != parent.child.grid){
 										parent.child.grid.loadData();
 									} */
 									//BIONE.tip("校验完成");
 									// 根据校验状态返回提示
 									if(result.taskCheckSts	 == "01"){
 										BIONE.tip("校验等待运行中");
 									}else if(result.taskCheckSts == "02"){
 										BIONE.tip("校验运行中");
 									}else if(result.taskCheckSts == "03"){
 										BIONE.tip("校验成功");
 									}else if(result.taskCheckSts == "04"){
 										BIONE.tip("校验失败");
 									}else if(result.taskCheckSts == "05"){
 										BIONE.tip("校验通过");
 									}else if(result.taskCheckSts == "06"){
 										BIONE.tip("校验有未通过项,请检查");
 									}else{
 										BIONE.tip("校验完成");
 									}
 									
 									
 									if(color && "[]" != color){
 										window.parent.checkedType = checkType;
 										window.parent.tmp.checkResult();
 										
 									}
 								}else{
 									BIONE.tip("校验引擎异常，请联系系统管理员");
 								}
 							}else{
 								 BIONE.tip("数据异常，请联系系统管理员");
 							}
							},
							error: function(){
								BIONE.tip("数据异常，请联系系统管理员");
							}
						});
				}else{
					BIONE.hideLoading();
					BIONE.tip("数据异常，请联系系统管理员");
				}
			},
			error:function(){
				BIONE.hideLoading();
				//BIONE.tip("数据异常，请联系系统管理员");
			}
		});
	}
	
	//条线汇总
	function caculateInfo(){
		if( orgNo && rptId && dataDate && lineId ){
			$.ajax({
				cache : false,
				async : false,
				url : "${ctx}/rpt/frs/rptfill/getTmpId",
				dataType : 'text',
				data : {
					rptId : rptId, dataDate : dataDate, lineId : lineId
				},
				type : "post",
				success : function(result){
					if(result){
						var obj = {"RelRptTmpId" : result, "OrgNo" : orgNo, "DataDate" : dataDate};
						//c = setInterval(ifDo,2000);
						$.ajax({
							async : true,
							url : "${ctx}/rpt/frs/rptfill/caculateRpt?taskInsId=" + taskInsId + "&taskFillOperType=24&operType=03",
							dataType : 'json',
							type : 'POST',
							data : {
								objArr : JSON2.stringify(obj)
							},
							beforeSend : function() {
								BIONE.showLoading("数据汇总中，请稍等...");
							},
							complete: function(){
								BIONE.hideLoading();
								BIONE.closeDialog("taskInsChildWin");
							},
							success: function(result) {
								rl = result.result;
								if(rl){
									if("OK" == rl){
										window.parent.View.ajaxJson();
										BIONE.tip("计算成功");
									}else if("NO" == rl){
										BIONE.tip("报表无需计算");
									}else{
										BIONE.tip("数据异常，请联系系统管理员");
									}
								}
							}
						}); 
					}else{
						BIONE.tip("数据异常，请联系系统管理员");
					}
				},
				error:function(){
					//BIONE.tip("数据异常，请联系系统管理员");
				}
			});
			
		}else{
			BIONE.tip("数据异常，请联系系统管理员");
		}
	}
	
	//机构汇总
	function sumDataInfo(){
		if(orgNo && rptId && dataDate && taskId && taskInsId){
			$.ajax({
				cache : false,
				async : true,
				url : "${ctx}/rpt/frs/dataSum/judgeAndSaveSingleSumSts?" + "taskFillOperType=25&operType=03",
				dataType : 'json',
				beforeSend : function() {
					BIONE.showLoading("数据汇总中，请稍等...");
				},
				data : {
					rptId : rptId, 
					dataDate : dataDate, 
					orgNo : orgNo, 
					taskId :taskId, 
					taskInsId : taskInsId,
					sumType : sumDataType,
					orgType : taskType
				},
				type : "post",
				success : function(result){
					if(result && "OK" == result.result){
						window.parent.View.ajaxJson();    //用于更新报表数据
						parent.BIONE.tip("汇总成功");
					}else if(result && result.result){
						parent.BIONE.tip(result.result);
					}else{
						parent.BIONE.tip("数据异常，请联系系统管理员");
					}
				},
				error:function(){
					//BIONE.tip("数据异常，请联系系统管理员");
				},
				complete: function(){
					BIONE.closeDialog("taskInsChildWin");
					BIONE.hideLoading();
				}
			});
		}else{
			BIONE.tip("数据异常，请联系系统管理员");
		}
	}
	
	function submit_close(){
		BIONE.closeDialog("taskInsChildWin");
	}
	
	//强制提交
	function submit(){
		$.ligerDialog.confirm('该任务下有关联的子任务未提交,是否继续?', '提交', function(yes) {
			if (yes) {
				if('Y'==isForceSubmit){
					$.ligerDialog.prompt('强制提交原因', true, function(yes, value) {
						if (yes) {
							BIONE.showLoading("正在提交中，请稍等...");
							data="ignore";
							if(data=="ignore"){
								forceUrl = "${ctx}/rpt/frs/rptfill/forceSubmit?rptOperType=03&taskFillOperType=03&ignore=Y&d="+new Date().getTime();
							}else{
								forceUrl = "${ctx}/rpt/frs/rptfill/forceSubmit?rptOperType=03&taskFillOperType=03&authUsers=" + data.join(",")+"&d="+new Date().getTime();
							}
							$.ajax({
								async : false,
								type : "post",
								data :{
									sts : sts,
									params : params,
									isLine : isLine
								},
								dataType : "json",
								url : forceUrl,
								success : function() {
									var url;
									if(data!="ignore"){
										url = "${ctx}/rpt/frs/rptfill/submitTaskBatch?rptOperType=03&taskFillOperType=03&taskInsIds="+taskInsIds+"&operatType="+operatType+"&authUsers=" + data.join(",")+"&tmpId="+tmpId+"&dataDate="+dataDate+"&orgNo="+orgNo+"&isForceSubmit=Y"+"&forceSubmitReason="+value;
									}else{
										url = "${ctx}/rpt/frs/rptfill/submitTaskBatch?rptOperType=03&taskFillOperType=03&ignore=Y&taskInsIds="+taskInsIds+"&tmpId="+tmpId+"&dataDate="+dataDate+"&orgNo="+orgNo+"&isForceSubmit=Y"+"&forceSubmitReason="+value;
									}
									$.ajax({
										cache : false,
										async : true,
										url : url,
										dataType : 'json',
										type : "get",
										success : function(){
											BIONE.hideLoading();
											var closeMain = true;
											if(parent.grid)
											{
												closeMain = false;
												parent.grid.loadData();
											}else{
												//刷新 填报树和填报列表
												if(window.parent.parent && window.parent.parent.findIframeByTitle){
													if(null !=parent.parent.findIframeByTitle("报表填报")[0].contentWindow.frames.tsktab){
														if(parent.parent.findIframeByTitle("报表填报")[0].contentWindow.frames.tsktab){
															var tsktabWindow = null;
															if (window.document.documentMode){//documentMode 是一个IE的私有属性，在IE8+中被支持。
																tsktabWindow = parent.parent.frames.findIframeByTitle("报表填报")[0].contentWindow.frames.tsktab.tmp;
															}else{
																tsktabWindow = parent.parent.frames.findIframeByTitle("报表填报")[0].contentWindow.frames.tsktab.contentWindow.tmp;
															}
															if(tsktabWindow){
																tsktabWindow.reAsyncChildNodes("left");
																tsktabWindow.reAsyncChildNodes("right");
															}
														}
													}
													if(null!=parent.parent.findIframeByTitle("报表填报")[0].contentWindow.frames.listtab){
														if(parent.parent.findIframeByTitle("报表填报")[0].contentWindow.frames.listtab){
															var listtabWindow = null;
															if (window.document.documentMode) { //判断是否IE浏览器
																listtabWindow = parent.parent.frames.findIframeByTitle("报表填报")[0].contentWindow.frames.listtab.grid;
															}else{
																listtabWindow = parent.parent.frames.findIframeByTitle("报表填报")[0].contentWindow.frames.listtab.contentWindow.grid;
															}
															if(listtabWindow){
																listtabWindow.loadData();
															}
														}
													}
												}
											}
											parent.parent.BIONE.tip("提交成功");

											//关闭当前页面和填报页面
											if(closeMain)
												parent.BIONE.closeDialog("taskFillWin");
											else
												BIONE.closeDialog("taskInsChildWin");
										},
										error:function(){
											BIONE.tip("数据异常，请联系系统管理员");
										}
									});
								},error : function(result, b) {
									BIONE.tip("数据异常，请联系系统管理员");
								}
							});
						}
					})
				} else {
					BIONE.showLoading("正在提交中，请稍等...");
					data="ignore";
					if(data=="ignore"){
						forceUrl = "${ctx}/rpt/frs/rptfill/forceSubmit?rptOperType=03&taskFillOperType=03&ignore=Y&d="+new Date().getTime();
					}else{
						forceUrl = "${ctx}/rpt/frs/rptfill/forceSubmit?rptOperType=03&taskFillOperType=03&authUsers=" + data.join(",")+"&d="+new Date().getTime();
					}
					$.ajax({
						async : false,
						type : "post",
						data :{
							sts : sts,
							params : params,
							isLine : isLine
						},
						dataType : "json",
						url : forceUrl,
						success : function() {
							var url;
							if(data!="ignore"){
								url = "${ctx}/rpt/frs/rptfill/submitTaskBatch?rptOperType=03&taskFillOperType=03&taskInsIds="+taskInsIds+"&operatType="+operatType+"&authUsers=" + data.join(",")+"&tmpId="+tmpId+"&dataDate="+dataDate+"&orgNo="+orgNo;
							}else{
								url = "${ctx}/rpt/frs/rptfill/submitTaskBatch?rptOperType=03&taskFillOperType=03&ignore=Y&taskInsIds="+taskInsIds+"&tmpId="+tmpId+"&dataDate="+dataDate+"&orgNo="+orgNo;
							}
							$.ajax({
								cache : false,
								async : true,
								url : url,
								dataType : 'json',
								type : "get",
								success : function(){
									BIONE.hideLoading();
									var closeMain = true;
									if(parent.grid)
									{
										closeMain = false;
										parent.grid.loadData();
									}else{
										//刷新 填报树和填报列表
										if(window.parent.parent && window.parent.parent.findIframeByTitle){
											if(null !=parent.parent.findIframeByTitle("报表填报")[0].contentWindow.frames.tsktab){
												if(parent.parent.findIframeByTitle("报表填报")[0].contentWindow.frames.tsktab){
													var tsktabWindow = null;
													if (window.document.documentMode){//documentMode 是一个IE的私有属性，在IE8+中被支持。
														tsktabWindow = parent.parent.frames.findIframeByTitle("报表填报")[0].contentWindow.frames.tsktab.tmp;
													}else{
														tsktabWindow = parent.parent.frames.findIframeByTitle("报表填报")[0].contentWindow.frames.tsktab.contentWindow.tmp;
													}
													if(tsktabWindow){
														tsktabWindow.reAsyncChildNodes("left");
														tsktabWindow.reAsyncChildNodes("right");
													}
												}
											}
											if(null!=parent.parent.findIframeByTitle("报表填报")[0].contentWindow.frames.listtab){
												if(parent.parent.findIframeByTitle("报表填报")[0].contentWindow.frames.listtab){
													var listtabWindow = null;
													if (window.document.documentMode) { //判断是否IE浏览器
														listtabWindow = parent.parent.frames.findIframeByTitle("报表填报")[0].contentWindow.frames.listtab.grid;
													}else{
														listtabWindow = parent.parent.frames.findIframeByTitle("报表填报")[0].contentWindow.frames.listtab.contentWindow.grid;
													}
													if(listtabWindow){
														listtabWindow.loadData();
													}
												}
											}
										}
									}
									parent.parent.BIONE.tip("提交成功");

									//关闭当前页面和填报页面
									if(closeMain)
										parent.BIONE.closeDialog("taskFillWin");
									else
										BIONE.closeDialog("taskInsChildWin");
								},
								error:function(){
									BIONE.tip("数据异常，请联系系统管理员");
								}
							});
						},error : function(result, b) {
							BIONE.tip("数据异常，请联系系统管理员");
						}
					});
				}

			}
		});
	}
	
	
</script>
</head>
<body>
</body>
</html>