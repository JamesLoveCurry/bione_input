<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/meta.jsp"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<meta name="decorator" content="/template/template26.jsp">
<%@ include file="/common/spreadjs_load.jsp"%>
<style type="text/css">
.l-btn {
	font-family: Arial, sans-serif;
	font-size: 12px;
	display: block;
	line-height: 29px;
	overflow: hidden;
	height: 29px;
	position: relative;
	padding-left: 3px;
	padding-right: 3px;
	cursor: pointer;
/*  	background: #D9D9D9 url(../ligerUI/Gray/images/ui/btn.png) repeat-x; */
	text-align: center;
	color: #000000;
	text-decoration: none;
	cursor: pointer;
	float: left;
	text-align: center;
	margin-left: 3px;
	margin-right: 3px;
}
</style>
<script type="text/javascript">
	$(function() {
		tmp.initDesign(window.parent.color);
		tmp.initButtons();
		tmp.initExport();
		tmp.initGridData();
	});
	var download=null;
	var View;
	var Spread;
	var objTmp;
	var Logic=null;
	var Sumpart=null;
	var Warn=null;
	var tmp = {
		jsonStr : null,
		//TAB
		tabHeight: 0,
		logicUrl: '',
		sumpartUrl: '',
		warnUrl: '',
		logicFlag: true,          
		sumpartFlag: true,
		warnFlag: true,
		urlTmp : ['${ctx}/rpt/frs/rptfill/logicCacheInfo'],
		//设计器
		Design : null,
		Spread : null,
		RptIdxInfo : null,
		//form
		mainform : null
	};
	tmp.initGridData = function() {
		var temp = tmp.urlTmp[0] + "&";
		temp += "rptTemplateId=${templateId}&";
		if("${rptId}"){
			temp += "rptId=${rptId}&";
		}
		if("${dataDate}"){
			temp += "dataDate=${dataDate}&";
		}
		if("${orgNo}"){
			temp += "orgNo=${orgNo}&";
		}
		temp += "d=" + new Date().getTime();
		//校验缓存
		$.ajax({
			cache : false,
			async : false,
			url : temp,
			dataType : 'json',
			type : "get",
			success : function(result){
				Logic=null;
				Sumpart=null;
				Warn=null;
				if(result && result.Logic){
					Logic = result.Logic;
				}
				if(result && result.Sumpart){
					Sumpart = result.Sumpart;
				}
				if(result && result.Warn){
					Warn = result.Warn;
				}
			},
			error : function(){
				BIONE.tip("数据异常，请联系系统管理员");
			}
		});
	};
	tmp.initExport = function()  {
		download = $('<iframe id="download"  style="display: none;"/>');
		$('body').append(download);
	};
	
	tmp.back = function() {
		$.ligerDialog.confirm("确定返回？", function(yes) {
			if(yes){
				BIONE.closeDialog("taskFillChildWin");
			}
		});
			
	};
	tmp.check = function() {
		$.ligerDialog.confirm("校验前需要保存数据，是否继续？", function(yes) {
			if(yes){
				tmp.save(false,tmp.checkInfo,"正在校验数据，请稍候...");
			}
		});
		
	};
	
	tmp.checkInfo = function(){
		if("${orgNo}" && "${rptId}" && "${dataDate}" && "${lineId}" && "${templateId}" && "${exeObjId}"){
			var orgNo = "${orgNo}";
			var rptId = "${rptId}";
			var templateId = "${templateId}";
			var dataDate = "${dataDate}";
			var lineId = "${lineId}";
			var exeObjId = "${exeObjId}";
			var taskId = "${taskId}";
			var taskType = "${type}";   
			$.ajax({
				cache : false,
				async : true,
				url : "${ctx}/rpt/frs/rptfill/getRsStsChild",
				dataType : 'json',
				data : {
					rptId : rptId, dataDate : dataDate, exeObjId : exeObjId, orgNo : orgNo, templateId : templateId, lineId : lineId
				},
				type : "post",
				success : function(result){
					if(result && result.taskInstanceId){
						
						//03-人行
						if("${type}" && "03" == "${type}"){
							var logicRs = result.logicRs;
							var warnRs = result.warnRs;
							if((logicRs == '01' || logicRs == '02') || (warnRs == '01' || warnRs == '02' )){
								BIONE.hideLoading();
								BIONE.tip("该记录正在校验，请等候");
								return;
							}
						}
						//02-1104
						if("${type}" && "02" == "${type}"){
							var logicRs = result.logicRs;
							var warnRs = result.warnRs;
							if((logicRs == '01' || logicRs == '02') || (warnRs == '01' || warnRs == '02' )){
								BIONE.hideLoading();
								BIONE.tip("该记录正在校验，请等候");
								return;
							}
						}
						var objArr = [];
						var obj = {"rptId" : rptId, "orgNo" : orgNo, "dataDate" : dataDate, "logicRs" : logicRs,  "warnRs" : warnRs, "lineId" : lineId, "templateId" : templateId};
						objArr.push(obj);
						var group = {};
						group.DataDate = "${dataDate}";
						group.OrgNo = ["${orgNo}"];
						
						if("${type}" && "03" == "${type}"){
							group.RealTimeLogicCheckRptTmpId = [templateId];
							group.RealTimeWarnCheckRptTmpId = [templateId];
						}
						if("${type}" && "02" == "${type}"){
							group.RealTimeLogicCheckRptTmpId = [templateId];
							group.RealTimeWarnCheckRptTmpId = [templateId];
						}
 						$.ajax({         
 							async : true,
 							url : "${ctx}/rpt/frs/rptfill/judgeAndSaveCheckStsSyncChild",
 							dataType : 'json',
 							type : 'POST',
 							data : {
 								objArr : JSON2.stringify(objArr),
 								objArrParms : JSON2.stringify(group),
 								dataDate : dataDate,
 								tmpId : templateId,
 								orgNo : orgNo,
 								taskId : taskId,
 								taskType : taskType
 							},
 							complete : function(){
 								BIONE.hideLoading();
 							},
 							success : function(result) {
	 							if(result.result){
	 								if("OK" == result.result){
	 									tmp.initGridData();
	 									var color = result.color;
	 									View.ajaxJson(color);
	 									if(null != parent.child.grid){
	 										parent.child.grid.loadData();
	 									}
	 									BIONE.tip("校验完成");
	 									if(color && "[]" != color){
		 									tmp.checkResult();
	 									}
	 								}else{
	 									BIONE.tip("校验引擎异常，请联系系统管理员");
	 								}
	 							}else{
	 								 BIONE.tip("数据异常，请联系系统管理员");
	 							}
 							},
 							error:function(){
 								//BIONE.tip("数据异常，请联系系统管理员");
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
		}else{
			BIONE.hideLoading();
			BIONE.tip("数据异常，请联系系统管理员");
		}
	}
	
	tmp.checkSingle = function(checkType) {
		$.ligerDialog.confirm("校验前需要保存数据，是否继续？", function(yes) {
			if(yes){
				tmp.save(false,tmp.checkSingleInfo,"正在进行校验，请稍候...",checkType);
			}
		});
	};
	
	tmp.checkSingleInfo = function(checkType) {
		if("${orgNo}" && "${rptId}" && "${dataDate}" && "${lineId}" && "${templateId}" && "${exeObjId}"){
			var orgNo = "${orgNo}";
			var rptId = "${rptId}";
			var templateId = "${templateId}";
			var dataDate = "${dataDate}";
			var lineId = "${lineId}";
			var exeObjId = "${exeObjId}";
			var taskId = "${taskId}";
			var taskType = "${type}";
			$.ajax({
				cache : false,
				async : true,
				url : "${ctx}/rpt/frs/rptfill/getRsStsChild",
				dataType : 'json',
				data : {
					rptId : rptId, dataDate : dataDate, exeObjId : exeObjId, orgNo : orgNo, templateId : templateId, lineId : lineId
				},
				type : "post",
				success : function(result){
					if(result && result.taskInstanceId){
						if("logic" == checkType){
							var logicRs = result.logicRs;
							if(logicRs == '01' || logicRs == '02'){
								BIONE.hideLoading();
								BIONE.tip("该记录正在逻辑校验，请等候");
								return;
							}
						}
						if("warn" == checkType){
							var warnRs = result.warnRs;
							if(warnRs == '01' || warnRs == '02'){
								BIONE.hideLoading();
								BIONE.tip("该记录正在进行预警校验，请等候");
								return;
							}
						}
						var objArr = [];
//							var obj = {"rptId" : rptId, "orgNo" : orgNo, "dataDate" : dataDate, "logicRs" : logicRs,  "warnRs" : warnRs, "lineId" : lineId, "templateId" : templateId};
						var obj;
						var group = {};
						group.DataDate = "${dataDate}";
						group.OrgNo = ["${orgNo}"];
						if("logic" == checkType){
							group.RealTimeLogicCheckRptTmpId = [templateId];
							obj = {"rptId" : rptId, "orgNo" : orgNo, "dataDate" : dataDate, "logicRs" : logicRs, "lineId" : lineId, "templateId" : templateId};
						}
						if("warn" == checkType){
							group.RealTimeWarnCheckRptTmpId = [templateId];
							obj = {"rptId" : rptId, "orgNo" : orgNo, "dataDate" : dataDate, "warnRs" : warnRs, "lineId" : lineId, "templateId" : templateId};
						}
						objArr.push(obj);
 						$.ajax({
 							cache : false,
 							async : true,
 							url : "${ctx}/rpt/frs/rptfill/judgeAndSaveCheckStsSyncChild",
 							dataType : 'json',
 							type : 'POST',
 							data : {
 								objArr : JSON2.stringify(objArr),
 								objArrParms : JSON2.stringify(group),
 								dataDate : dataDate,
 								tmpId : templateId,
 								orgNo : orgNo,
 								taskId : taskId,
 								taskType : taskType
 							},
 							complete : function(){
 								BIONE.hideLoading();
 							},
 							success : function(result) {
	 							if(result.result){
	 								if("OK" == result.result){
	 									tmp.initGridData();
	 									var color = result.color;
	 									View.ajaxJson(color);
	 									if(null != parent.child.grid){
	 										parent.child.grid.loadData();
	 									}
	 									BIONE.tip("校验完成");
	 									if(color && "[]" != color){
		 									tmp.checkResult();
	 									}
	 								}else{
	 									BIONE.tip("校验引擎异常，请联系系统管理员");
	 								}
	 							}else{
	 								 BIONE.tip("数据异常，请联系系统管理员");
	 							}
 							},
 							error : function(){
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
		}else{
			BIONE.hideLoading();
			BIONE.tip("数据异常，请联系系统管理员");
		}
	}
	
	tmp.caculate = function() {
		$.ligerDialog.confirm("计算前需要保存数据，并清除校验结果，是否继续？", function(yes) {
			if(yes){
				tmp.save(false,tmp.caculateInfo,"正在计算中，请稍候...");
			}
		});
	};
	
	tmp.caculateInfo = function(){
		if("${orgNo}" && "${rptId}" && "${dataDate}" && "${lineId}" && "${templateId}"){
			var orgNo = "${orgNo}";//上级任务实例orgNo
			var rptId = "${rptId}";
			var templateId = "${templateId}";
			var dataDate = "${dataDate}";
			var busiLineId = "${lineId}";
			//var obj = {"RptTmpId" : templateId, "OrgNo" : orgNo, "DataDate" : dataDate};
			var obj = {"RealRptTmpId" : templateId, "OrgNo" : orgNo, "DataDate" : dataDate};
			$.ajax({
				cache : false,
				async : true,
				url : "${ctx}/rpt/frs/rptfill/caculateRptChild",
				dataType : 'json',
				type : 'POST',
				data : {
					objArr : JSON2.stringify(obj)
				},
				complete : function(){
					BIONE.hideLoading();
				},
				success:function(result) {
					if(result.result){
						if("OK" == result.result){
							View.ajaxJson();
							BIONE.tip("计算成功");
						}else{
							BIONE.tip("校验引擎异常，请联系系统管理员");
						}
					}else{
						 BIONE.tip("数据异常，请联系系统管理员");
					}
				},
				error:function (){
					//BIONE.tip("数据异常，请联系系统管理员");
				}
			});
		}else{
			BIONE.hideLoading();
			BIONE.tip("数据异常，请联系系统管理员");
		}
	}
	tmp.importData = function() {
		if("${rptId}" && "${templateId}" && "${dataDate}" && "${orgNo}" && "${lineId}" && "${taskInsId}" && "${type}" && "${exeObjId}"){
			var rptId = "${rptId}";
			var templateId = "${templateId}";
			var dataDate = "${dataDate}";
			var orgNo = "${orgNo}";//上级任务实例机构标识
			var lineId = "${lineId}";
			var taskInsId = "${taskInsId}";
			var type = "${type}";
			var exeObjId = "${exeObjId}";//二次任务实例执行对象-用户                         
			BIONE.commonOpenDialog('导入数据文件','uploadWin',600,330,"${ctx}/rpt/frs/rptfill/showTab?path=rpt-todowork-uploadfile&dataDate=" + dataDate + "&orgNo=" + orgNo + "&rptId=" + rptId + "&templateId="  + templateId + "&taskInsId=" + taskInsId + "&lineId=" + lineId + "&type=" + type + "&exeObjId=" + exeObjId + "&flag=TWO&entry=EXCEL");
		}
	};
	tmp.reset = function() {
		View.reset();
	};
	tmp.promptAndSave = function(flag) {
		$.ligerDialog.alert("修改数据并保存将清除校验结果", "提示", function(yes) {
			if(yes){
				tmp.save(flag);
			}
		});
	}
	tmp.save = function(flag,func,message,param) { //flag true-正常保存，false-检验、计算、提交之前保存
		if("${taskInsId}"){
			var changeInfo = View.generateChangeInfo();
			$.ajax({
				cache : false,
				async : true,
				url : "${ctx}/rpt/frs/rptfill/saveData",
				dataType : 'json',
				type : 'POST',
				data : {
					cells : JSON2.stringify(changeInfo.cells),
					rptId : "${rptId}",
// 					orgNo : "${orgNo}",
					dataDate : "${dataDate}",
					busiLineId : "${lineId}",
					taskInsId : "${taskInsId}",
					searchArgs : changeInfo.searchArgs
				},
				beforeSend : function() {
					BIONE.showLoading(message);
				},
				success: function(result) {
					View.refreshCellVals(result.res);
					$.ajax({
						cache : false,
						async : true,
						url : "${ctx}/rpt/frs/rptfill/updateTask?taskInsId=${taskInsId}&d="+new Date().getTime(),
						dataType : 'json',
						type : "get",
						success : function(result){
							if("OK"== result.result){
								if(null != parent.child.grid){
									parent.child.grid.loadData();
								}
								if(flag){
									BIONE.hideLoading();
									BIONE.tip("保存成功");
								}
								else{
									if(func!=null){
										func(param);
									}
									else{
										BIONE.hideLoading();
									}
								}
							}
						},
						error:function(){
							BIONE.hideLoading();
							//BIONE.tip("数据加载异常，请联系系统管理员");
						}
					});
				},
				error: function(){
					BIONE.hideLoading();
					//BIONE.tip("数据异常，请联系系统管理员");
				}
			});
		}else{
			BIONE.tip("数据异常，请联系系统管理员");
		}
	};
	
	tmp.finish = function() {
		$.ligerDialog.confirm("结束填报前需要保存数据，并清除校验结果，是否继续？", function(yes) {
			if(yes){
				tmp.save(false,tmp.finishInfo,"正在结束填报，请稍候...");
			}
		});
	};
	
	tmp.finishInfo= function(){
		if("${taskInsId}" && "${orgNo}" && "${rptId}" && "${dataDate}" && "${lineId}" && "${templateId}" && "${exeObjId}"){
			var orgNo = "${orgNo}";
			var rptId = "${rptId}";
			var templateId = "${templateId}";
			var dataDate = "${dataDate}";
			var lineId = "${lineId}";
			var exeObjId = "${exeObjId}";
			$.ajax({
				cache : false,
				async : true,
				url : "${ctx}/rpt/frs/rptfill/getRsStsChild",
				dataType : 'json',
				data : {
					rptId : rptId, dataDate : dataDate, exeObjId : exeObjId, orgNo : orgNo, templateId : templateId, lineId : lineId
				},
				type : "post",
				success : function(result){
					if(result && result.taskInstanceId){
						var failFlag = false;
						//03-人行
						if("${type}" && "03" == "${type}"){
							var logicRs = result.logicRs;
							var warnRs = result.warnRs;
							if((logicRs == '01' || logicRs == '02') || (warnRs == '01' || warnRs == '02' )){
								BIONE.hideLoading();
								BIONE.tip("该记录正在校验，请等候");
								return;
							}
							if((logicRs == '04' || logicRs == '06') || (warnRs == '04' || warnRs == '06' )){
								failFlag = true;
							}
						}
						//02-1104
						if("${type}" && "02" == "${type}"){
							var logicRs = result.logicRs;
							var warnRs = result.warnRs;
							if((logicRs == '01' || logicRs == '02') || (warnRs == '01' || warnRs == '02' )){
								BIONE.hideLoading();
								BIONE.tip("该记录正在校验，请等候");
								return;
							}
							if((logicRs == '04' || logicRs == '06') || (warnRs == '04' || warnRs == '06' )){
								failFlag = true;
							}
						}
						if(failFlag){
							$.ligerDialog.confirm("该记录校验未通过，是否继续？", function(yes) {
								if(yes){
									$.ajax({
										cache : false,
										async : true,
										url : "${ctx}/rpt/frs/rptfill/finishTask?taskInsId=${taskInsId}&d="+new Date().getTime(),
										dataType : 'json',
										type : "get",
										complete: function(){
											BIONE.hideLoading();
										},
										success : function(result){
											if("OK"== result.result){
												if(null != parent.child.grid){
													parent.child.grid.loadData();
												}
												if(null != parent.child.tmp.resetRightInfo){
													parent.child.tmp.resetRightInfo();
												}
												BIONE.tip("填报完成");
												BIONE.closeDialog("taskFillChildWin");
											}
											if("ALREADY" == result.result){
												BIONE.tip("已填报完成，不可重复填报");
											}
											if("ERROR" == result.result){
												BIONE.tip("数据异常，请联系系统管理员");
											}
										},
										error:function(){
											//BIONE.tip("数据加载异常，请联系系统管理员");
										}
									});
								}
							});
						}else{
							$.ajax({
								cache : false,
								async : true,
								url : "${ctx}/rpt/frs/rptfill/finishTask?taskInsId=${taskInsId}&d="+new Date().getTime(),
								dataType : 'json',
								type : "get",
								complete: function(){
									BIONE.hideLoading();
								},
								success : function(result){
									if("OK"== result.result){
										if(null != parent.child.grid){
											parent.child.grid.loadData();
										}
										BIONE.tip("填报完成");
										BIONE.closeDialog("taskFillChildWin");
									}
									if("ALREADY" == result.result){
										BIONE.tip("已填报完成，不可重复填报");
									}
									if("ERROR" == result.result){
										BIONE.tip("数据异常，请联系系统管理员");
									}
								},
								error:function(){
									//BIONE.tip("数据加载异常，请联系系统管理员");
								}
							});
						}
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
		}else{
			BIONE.hideLoading();
			BIONE.tip("数据异常，请联系系统管理员");
		}
	}
	
	tmp.submit = function() {
		$.ligerDialog.confirm("提交前需要保存数据，并清除校验结果，是否继续？", function(yes) {
			if(yes){
				tmp.save(false,tmp.submitInfo,"正在提交，请稍候...");
			}
		});
	};
	
	tmp.submitInfo = function() {
		if("${taskInsId}" && "${orgNo}" && "${rptId}" && "${dataDate}" && "${lineId}" && "${templateId}" && "${exeObjId}"){
			var orgNo = "${orgNo}";
			var rptId = "${rptId}";
			var templateId = "${templateId}";
			var dataDate = "${dataDate}";
			var lineId = "${lineId}";
			var exeObjId = "${exeObjId}";
			$.ajax({
				cache : false,
				async : true,
				url : "${ctx}/rpt/frs/rptfill/getRsStsChild",
				dataType : 'json',
				data : {
					rptId : rptId, dataDate : dataDate, exeObjId : exeObjId, orgNo : orgNo, templateId : templateId, lineId : lineId
				},
				type : "post",
				success : function(result){
					if(result && result.taskInstanceId){
						var failFlag = false;
						//03-人行
						if("${type}" && "03" == "${type}"){
							var logicRs = result.logicRs;
							var warnRs = result.warnRs;
							if((logicRs == '01' || logicRs == '02') || (warnRs == '01' || warnRs == '02' )){
								BIONE.hideLoading();
								BIONE.tip("该记录正在校验，请等候");
								return;
							}
							if((logicRs == null || logicRs == '04' || logicRs == '06') || (warnRs == null || warnRs == '04' || warnRs == '06' )){
								failFlag = true;
							}
						}
						//02-1104
						if("${type}" && "02" == "${type}"){
							var logicRs = result.logicRs;
							var warnRs = result.warnRs;
							if((logicRs == '01' || logicRs == '02') || (warnRs == '01' || warnRs == '02' )){
								BIONE.hideLoading();
								BIONE.tip("该记录正在校验，请等候");
								return;
							}
							if((logicRs == null || logicRs == '04' || logicRs == '06') || (warnRs == null || warnRs == '04' || warnRs == '06' )){
								failFlag = true;
							}
						}
						if(failFlag){
							$.ligerDialog.confirm("该记录校验未通过或者未校验，是否继续？", function(yes) {
								if(yes){
									$.ajax({
										cache : false,
										async : true,
										url : "${ctx}/rpt/frs/rptfill/submitTask?taskInsId=${taskInsId}&d="+new Date().getTime(),
										dataType : 'json',
										type : "get",
										complete: function(){
											BIONE.hideLoading();
										},
										success : function(result){
											if("OK"== result.result){
												if(null != parent.curtab.grid){
													parent.child.grid.loadData();
												}
												if(null != parent.curtab.tmp.resetInfo){
													parent.curtab.tmp.resetInfo();
												}
												BIONE.tip("提交成功");
												BIONE.closeDialog("taskFillChildWin");
											}
											if("ALREADY" == result.result){
												BIONE.tip("已提交，不可重复提交");
											}
											if("ERROR" == result.result){
												BIONE.tip("数据异常，请联系系统管理员");
											}
										},
										error:function(){
											//BIONE.tip("数据加载异常，请联系系统管理员");
										}
									});
								}
							});
						}else{
							$.ajax({
								cache : false,
								async : true,
								url : "${ctx}/rpt/frs/rptfill/submitTask?taskInsId=${taskInsId}&d="+new Date().getTime(),
								dataType : 'json',
								type : "get",
								completet: function(){
									BIONE.hideLoading();
								},
								success : function(result){
									if("OK"== result.result){
										if(null != parent.child.grid){
											parent.child.grid.loadData();
										}
										BIONE.tip("提交成功");
										BIONE.closeDialog("taskFillChildWin");
									}
									if("ALREADY" == result.result){
										BIONE.tip("已提交，不可重复提交");
									}
									if("ERROR" == result.result){
										BIONE.tip("数据异常，请联系系统管理员");
									}
								},
								error:function(){
									//BIONE.tip("数据加载异常，请联系系统管理员");
								}
							});
						}
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
	
	// 初始化设计器
	tmp.initDesign = function(color) {
		require.config({
			baseUrl : "${ctx}/js/",
			paths:{
				"view" : "report/show/views/rptview"
			}
		});
		require(["view"] , function(view){
// 			templateinit();
			templateinit("${type}", false);
			initTab();
			var orgNo = "${orgNo}";//上级任务实例
			var rptId = "${rptId}";
			var dataDate = "${dataDate}";
			var lineId = "${lineId}";
			var fileName = '';
			var orgName = "${orgNm}";
			if(parent.child && parent.child.fileName){
				fileName = parent.child.fileName;
			}
			var argsArr = [];
			var args1 = {'DimNo':'ORG','Op':'=','Value':orgNo};
			//var args2 = {'dimNo':'busiLineId','op':'=','value':lineId};
			argsArr.push(args1);
			//argsArr.push(args2);
			var settings = {
					targetHeight : ($("#center").height() -$("#top").height() - 31) ,
					ctx : "${ctx}",
					readOnly : false,
					cellDetail : true,
					toolbar : false,
					canUserEditFormula : false,
					inValidMap : color,
					initFromAjax : true,
					searchArgs : JSON2.stringify(argsArr),
					ajaxData:{
						rptId : rptId,
						dataDate : dataDate,
						busiLineId : lineId,
						fileName : fileName,
						orgNm : orgName
					},
	 				onEnterCell : tmp.spreadEnterCell , 
					onCellDoubleClick : tmp.spreadDbclkCell
			};
			View = view;
			var spread = view.init($("#spread") , settings);
			Spread = spread;
		})
	};
	tmp.initExport=function()  {
		download = $('<iframe id="download"  style="display: none;"/>');
		$('body').append(download);

	};
	tmp.download=function(){
		if("${taskInsId}"){
			var argsArr = [];
			if("${orgNo}" && "${rptId}" && "${dataDate}" && "${lineId}" && "${orgNm}"){
				var rptId = "${rptId}";
				var dataDate = "${dataDate}";
				var orgNo = "${orgNo}";
				var busiLineId = "${lineId}";
				var orgName = "${orgNm}";
				if(rptId != null && orgNo != null && busiLineId != null && dataDate != null){
					var argsArr1 = [];
					var args1 = {'DimNo':'ORG','Op':'=','Value':orgNo};
					argsArr1.push(args1);
					var args = {'orgNm':orgName,'rptId':rptId,'dataDate':dataDate,'busiLineId':busiLineId,'searchArgs':JSON2.stringify(argsArr1)};
					argsArr.push(args);
				}else{
					BIONE.tip("数据异常，请联系系统管理员");
				}
			}else{
				BIONE.tip("数据异常，请联系系统管理员");
			}
			if(argsArr.length > 0){
				BIONE.ajax({
					async : false,
					url : "${ctx}/rpt/frs/rptfill/downloadList?json="+encodeURI(encodeURI(JSON2.stringify(argsArr)))+"&d="+ new Date().getTime(),
					dataType : 'json',
					type : 'get'
				}, function(result) {
					if(result.result){
						if("OK" == result.result){
							if(result.zipFilePath && result.folderinfoPath){
								var src = '';
								src = "${ctx}/rpt/frs/rptfill/downFile?zipFilePath="+encodeURI(encodeURI(result.zipFilePath))+"&folderinfoPath=" + encodeURI(encodeURI(result.folderinfoPath)) + "&d="+ new Date().getTime();
								download.attr('src', src);
							}else{
								BIONE.tip("数据异常，请联系系统管理员");
							}
						}else{
							BIONE.tip("数据异常，请联系系统管理员");
						}
					}else{
						 BIONE.tip("数据异常，请联系系统管理员");
					}
				});
// 				var src = '';
// 				src = "${ctx}/rpt/frs/rptfill/downloadList?json="+encodeURI(encodeURI(JSON2.stringify(argsArr)))+"&d="+ new Date();
// // 				window.open(src, '数据下载');
// 				download.attr('src', src);
			}else{
				BIONE.tip("数据异常，请联系系统管理员");
			}
		}else{
			BIONE.tip("数据异常，请联系系统管理员");
		}
// 		var json=Spread.toJSON();
// 		var src = '';
// 		src = "${ctx}/rpt/frs/rptfill/download?json="+encodeURI(encodeURI(JSON2.stringify(json))) + "&rptNm=" + encodeURI("${rptNm}") +"&d="+ new Date();
// 		download.attr('src', src);
	};
	//双击单元格事件
	tmp.spreadDbclkCell = function (sender , args, info){
		
	};
	tmp.spreadEnterCell = function(sender , args, info){
		if(info && info.isValidate == 'N'){//校验未通过
			if(info.indexNo && info.indexNo != ''){//指标类
				if(Logic != null){
					var grid = [];
					for(var i = 0; i < Logic.length; i++){
						if(Logic[i][info.indexNo] != null){
							grid.push(Logic[i][info.indexNo]);
						}
					}
					var data = {"Rows" : grid};
					logicGrid.loadData(data);
				}
				if(Sumpart != null){
					var grid = [];
					for(var i = 0; i < Sumpart.length; i++){
						if(Sumpart[i][info.indexNo] != null){
							grid.push(Sumpart[i][info.indexNo]);
						}
					}
					var data = {"Rows" : grid};
					sumpartGrid.loadData(data);
				}
				if(Warn != null){
					var grid = [];
					for(var i = 0; i < Warn.length; i++){
						if(Warn[i][info.indexNo]){
							grid.push(Warn[i][info.indexNo]);
						}
					}
					var data = {"Rows" : grid};
					warnGrid.loadData(data);
				}
			}
		}else{//校验通过
			var data = {"Rows" : []};
			logicGrid.loadData(data);
			sumpartGrid.loadData(data);
			warnGrid.loadData(data);
		}
	};
	tmp.checkResult = function() {
		if("${orgNo}" && "${rptId}" && "${dataDate}" && "${templateId}" && "${type}"){                     
			BIONE.commonOpenLargeDialog('校验结果查看','checkResultChildWin','${ctx}/rpt/frs/rptfill/showTab?path=rptfill-todowork-check-result-child&templateId=${templateId}&rptId=${rptId}&dataDate=${dataDate}&orgNo=${orgNo}&type=${type}&taskId=${taskId}');			
		}else{
			BIONE.tip("数据异常，请联系系统管理员");
		}
	};
	tmp.openCompare = function(){
		if(liger.get("compareWin")){
			return ;
		}
		var selIdxs = View.getSelectionIdxs();
		if(!selIdxs
				|| selIdxs.length <= 0){
			// 若没有选择指标，获取全部的指标比对信息
			selIdxs = View.getAllIdxs();
		}
		top.selIdxs = selIdxs;       
		$.ligerDialog.open({
			id : 'compareWin',
			title : '上期值比对',
			url: '${ctx}/rpt/frs/rptfill/showTab?path=rptfill-compare-index&dataDate=${dataDate}&rptId=${rptId}&orgNo=${orgNo}',
			width: 600, 
			height: 320, 
			modal: false, 
			isResize: true,
			allowClose : false,
			showMin : true
		});
	}
	
	tmp.openSumview = function(){
		if(liger.get("sumviewWin")){
			return ;
		}
		var selIdxs = View.getSelectionIdxs();
		if(!selIdxs
				|| selIdxs.length <= 0){
			// 若没有选择指标，获取全部的指标比对信息
			selIdxs = View.getAllIdxs();
		}
		top.selIdxs = selIdxs;
		$.ligerDialog.open({
			id : 'sumviewWin',
			title : '汇总查看',
			url: '${ctx}/rpt/frs/rptfill/showTab?path=rptfill-sumview-index&dataDate=${dataDate}&rptId=${rptId}&orgNo=${orgNo}&orgType=${type}',
			width: 600, 
			height: 320, 
			modal: false, 
			isResize: true,
			allowClose : false,
			showMin : true
		});
	}
	
	
	tmp.init = function() {
		$.ajax({
			async : true,
			url : "${ctx}/rpt/frs/rptfill/initData",
			dataType : 'json',
			type : 'POST',
			beforeSend : function() {
				BIONE.showLoading("数据恢复中，请稍等...");
			},
			data : {
				rptId : "${rptId}",
 				orgNo : "${orgNo}",
				dataDate : "${dataDate}",
				taskInsId : "${taskInsId}"
			}, 
			success: function(result){
				View.ajaxJson();
			},
			error: function(){
				BIONE.hideLoading();
			}
		});
	};
	
	tmp.buttonObject = {
			"INITBTN" : { text : '恢复初始值', width : '60px', appendTo : '#button', operNo:'initBtn', click : tmp.init},
			"RESETBTN" : { text : '数据重置', width : '60px', appendTo : '#button', operNo:'resetBtn', click : tmp.reset},
			"EXPORTBTN" : { text : '数据下载', width : '60px', appendTo : '#button', operNo:'exportBtn', click : tmp.download},
	  		"IMPORTBTN" : { text : '导入数据', width : '60px', appendTo : '#button', operNo:'importBtn', click : tmp.importData},
			"CALCULATEBTN" : { text : '报表计算', width : '60px', appendTo : '#button', operNo:'calculateBtn', click : tmp.caculate},
			"VALIDBTN" : { text : '校验', width : '60px', appendTo : '#button', operNo:'validBtn', click : tmp.check},
			"LOGICBTN" : { text : '逻辑校验', width : '60px', appendTo : '#button', operNo:'logicBtn', click : function(){tmp.checkSingle("logic");}},
// 			"SUMPARTBTN" : { text : '总分校验', width : '60px', appendTo : '#button', operNo:'sumpartBtn', click : function(){tmp.checkSingle("sumpart");}},
			"WARNBTN" : { text : '预警校验', width : '60px', appendTo : '#button', operNo:'warnBtn', click : function(){tmp.checkSingle("warn");}},
// 			"SUMBTN" : { text : '数据汇总', width : '60px', appendTo : '#button', operNo:'sumBtn', click : tmp.sumData},
			"CHECKRESULTBTN" : { text : '校验结果查看', width : '80px', appendTo : '#button', operNo:'checkResultBtn', click : tmp.checkResult},
			"COMPAREBTN" : { text : '比上期', width : '50px', appendTo : '#button', operNo:'compareBtn', click : tmp.openCompare},
			"SUMVIEWBTN" : { text : '汇总查看', width : '50px', appendTo : '#button', operNo:'sumviewBtn', click : tmp.openSumview},
			"SAVEBTN" : { text : '保存', width : '60px', appendTo : '#button', operNo:'saveBtn', click : function(){tmp.promptAndSave(true);}},
			"HANDLEBTN" : { text : '提交', width : '60px', appendTo : '#button', operNo:'handleBtn', click : tmp.submit},
			"FINISHBTN" : { text : '结束填报', width : '60px', appendTo : '#button', operNo:'finishBtn', click : tmp.finish},
//	  		"REMARKBTN" : { text : '备注', width : '60px', appendTo : '#button', operNo:'remarkBtn', click : tmp.save},
			"BACKBTN" : { text : '返回', width : '60px', appendTo : '#button', operNo:'backBtn', click : tmp.back}
	};
	tmp.initButtons = function() {
		$.ajax({
			cache : false,
			async : false,
			url : "${ctx}/rpt/frs/rptfill/createButton",
			dataType : 'json',
			data : {
				taskType : "${type}", flag : "2"
			},
			type : "post",
			success : function(result){
				if(result && result.length > 0){
					for(var i in result){
						if(tmp.buttonObject[result[i]]){
							BIONE.createButton(tmp.buttonObject[result[i]]);
						}
					}
				}else{
					BIONE.tip("数据异常，请联系系统管理员");
				}
			},
			error:function(){
				//BIONE.tip("数据异常，请联系系统管理员");
			}
		});
		
// 		BIONE.createButton({ text : '恢复初始值', width : '60px', appendTo : '#button', operNo:'resetBtn', click : tmp.reset});
// 		BIONE.createButton({ text : '数据下载', width : '60px', appendTo : '#button', operNo:'exportBtn', click : tmp.download});
// 		BIONE.createButton({ text : '导入数据', width : '60px', appendTo : '#button', operNo:'importBtn', click : tmp.importData});
// 		BIONE.createButton({ text : '校验', width : '60px', appendTo : '#button', operNo:'validBtn', click : tmp.check});
// 		BIONE.createButton({ text : '报表计算', width : '60px', appendTo : '#button', operNo:'calculateBtn', click : tmp.caculate});
// 		BIONE.createButton({ text : '校验结果查看', width : '80px', appendTo : '#button', operNo:'checkResultBtn', click : tmp.checkResult});
// 		BIONE.createButton({ text : '保存', width : '60px', appendTo : '#button', operNo:'saveBtn', click : function(){tmp.save(true);}});
// 		BIONE.createButton({ text : '提交', width : '60px', appendTo : '#button', operNo:'handleBtn', click : tmp.submit});
// // 		BIONE.createButton({ text : '备注', width : '60px', appendTo : '#button', operNo:'remarkBtn', click : tmp.save});
// 		BIONE.createButton({ text : '返回', width : '60px', appendTo : '#button', operNo:'backBtn', click : tmp.back});
	};
	
</script>
</head>
<body>
</body>

</html>