<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/meta.jsp"%>


<html>
<head>
<meta name="decorator" content="/template/template26.jsp">
<%@ include file="/common/taglibs.jsp"%>
<%@ include file="/common/spreadjs_load.jsp"%>
<style type="text/css">
.l-btn {
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
		tmp.initExport();
		tmp.initGridData();
	});
	var fl = false;
	var isOpenSave = true;
	var isloadfinish = false;		//页面数据是够加载完成
	var rl = null;
	var c = null;
	var download=null;
	var tabFalg=false;
	var View;
	var Spread;
	var objTmp;
	var Logic=null;
	var Sumpart=null;
	var Warn=null;
	var dataDate ="${dataDate}";
	var orgNo = "${orgNo}";
	var orgNm = "${orgNm}";
	var taskInsId = "${taskInsId}";
	var taskId = "${taskId}";
	var selIdxs = []; // 选择的指标信息
	var checkedType="NO";//校验类型 
	
	var tmp = {
		jsonStr : null,
		tmpId: "",
		verId: "",
		logicUrl: '',
		sumpartUrl: '',
		warnUrl: '',
		logicFlag: true,
		sumpartFlag: true,
		warnFlag: true,
		urlTmp : ['${ctx}/rpt/frs/rptfill/checkCacheInfo'],
		RptIdxInfo : null,
		mainform : null
	};
	
	tmp.initSearchForm = function() {
		$("#formsearch").ligerForm({
			fields : [ {
				display : "数据日期",
				name : "date",
				newline : true,
				labelWidth : 100,
				width : 220,
				space : 30,
				type : "date",
				options :{
					format : "yyyyMMdd"
				},
				validate:{
					required : true
				}
			}, {
				display : "机构",
				name : "org",
				newline : false,
				labelWidth : 100,
				width : 220,
				space : 30,
				type : "select",
				options:{
					onBeforeOpen:function(){
						BIONE.commonOpenDialog(
								"机构树",
								"chooseOrg",
								400,
								$(window).height()-10,
								"${ctx}/rpt/frs/rptfill/showTab?path=rptfill-choose-org&orgType=${type}&rptId=${rptId}&dataDate="+window.dataDate,
								null
							);
							return false;
					}
				},
				validate:{
					required : true
				}
			} ]
		});
		$("#date").val(window.dataDate);
		$.ligerui.get("org")._changeValue(window.orgNo,"${orgNm}");
		//changeOrgValue(window.orgNo,"${orgNm}");
		jQuery.metadata.setType("attr", "validate");
		BIONE.validate('#formsearch');
		//alert("form....end..");
	};
	function changeOrgValue(orgNo,orgNm){
		$("#org").val(orgNo);
		$("#orgBox").val(orgNm);
	}
	tmp.initGridData = function() {

		$.ajax({
			cache : false,
			async : false,
			url : "${ctx}/rpt/frs/rptfill/showTemplateId",
			dataType : 'text',
			data : {
				rptId : "${rptId}",
				dataDate : dataDate
			},
			type : "post",
			success : function(result){
				if(result && result != "ERROR"){
					var temp = tmp.urlTmp[0] + "?";
					temp += "rptTemplateId=" + result + "&";
					if("${rptId}"){
						temp += "rptId=${rptId}&";
					}
					if(window.dataDate){
						temp += "dataDate="+window.dataDate+"&";
					}
					if(window.orgNo){
						temp += "orgNo="+window.orgNo+"&";
					}
					temp += "d=" + new Date().getTime();

					//校验缓存
					$.ajax({
						cache : false,
						async : false,
						url : temp,
						dataType : 'json',
						type : 'get',
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
							
							//设置加载完成标识
							isloadfinish = true;
						},
						error : function(){
							BIONE.tip("数据异常，请联系系统管理员");
						}					});
				}else{
					BIONE.tip("没有对应的报表模板");
				}
			},
			error:function(){
				//BIONE.tip("数据加载异常，请联系系统管理员");
			}
			

			
		});
	};
	
	tmp.addSearchButtons = function(form, btnContainer) {
		if (!form)
			return;
		form = $(form);
		if (btnContainer) {
			BIONE.createButton({
				appendTo : btnContainer,
				text : '',
				icon : 'search3',
				width : '40px',
				click : function() {
					BIONE.validate($("#formsearch"));
					if($("#formsearch").valid()){
						$.ajax({
							cache : false,
							async : true,
							url : "${ctx}/rpt/frs/rptfill/checkTaskIns",
							dataType : 'json',
							type : "post",
							data :{
								type : "${type}",
								orgNo : $("#org").val(),
								dataDate :  $("#date").val(),
								rptId : "${rptId}"
							},
							success : function(result){
								if(result.msg !="error"){
									var sc = JSON2.parse(View._settings.searchArgs);
									sc[0].value=$("#org").val();
									View._settings.searchArgs = JSON2.stringify(sc);
									View._settings.ajaxData.dataDate = $("#date").val();
									View._settings.ajaxData.orgNm = $.ligerui.get("org").getText();
									window.dataDate = $("#date").val();
									window.orgNo = $("#org").val();
									window.taskInsId = result.taskInsId;
									window.taskId = result.taskId;
									$.ajax({
										cache : false,
										async : false,
										url : "${ctx}/rpt/frs/rptfill/getTmpId",
										dataType : 'text',
										data : {
											rptId : "${rptId}", dataDate : window.dataDate, lineId : "${lineId}"
										},
										type : "post",
										success : function(result){
											$.ajax({
					 							async : false,
					 							url : "${ctx}/rpt/frs/rptfill/createColor",
					 							dataType : 'text',
					 							type : 'POST',
					 							data : {
					 								rptId : "${rptId}",
					 								orgNo : window.orgNo,
					 								dataDate : window.dataDate,
					 								tmpId : '${templateId}'
					 							},
					 							success : function(result) {
						 							View.ajaxJson(result);
						 						}
					 						});
										}
									});
									
									tmp.initGridData();
									
								}
								else{
									BIONE.tip("所选择的日期与机构无需填报")
								}
							}
						});
						
					}
				}
			});
			$("#searchbtn").width($("#center").width()-770);
		}
	};
	
	tmp.initExport = function()  {
		download = $('<iframe id="download"  style="display: none;"/>');
		$('body').append(download);
	};
	
	tmp.back = function() {
		if(tmp.isUpdateData()){
			$.ligerDialog.confirm("数据已经修改,确定返回?", function(yes) {
				if(yes){
					BIONE.closeDialog("taskFillWin");
				}
			});
		}else{
			BIONE.closeDialog("taskFillWin");
		}
		
			
	};
	//判断数据是否修改
	tmp.isUpdateData = function(){
		var changeInfo = View.generateChangeInfo();
		if(!changeInfo){
			return false;
		}
		var cells = changeInfo.cells
		if(cells&&cells.length>0){//
			for(var i in cells){
				if(cells[i].value!=cells[i].newValue&&!cells[i].formula) {
					return true;
				}
			}
		}
	};
	tmp.isColse = function(dl){
		$.ligerDialog.confirm("数据已经修改,确定返回?", function(yes) {
				if(yes){
					dl.beforeClose = function(){};
					BIONE.closeDialog("taskFillWin");
				}else{
					return false;
				}
			}); 
		return false;
			/* if(confirm("数据已经修改,确定返回?")){
				return true;
			}else{
				return false;
			} */
	};
	tmp.sumData = function() {
		 $.ligerDialog.confirm('确定要机构汇总？', '机构汇总', function(yes) {
				if (yes) {
					tmp.sumDataInfo();
				}
		 });
	};
	
	tmp.sumDataInfo =function(){
		if(window.orgNo && "${rptId}" && window.dataDate){
			var orgNo = window.orgNo;
			var rptId = "${rptId}";
			var dataDate = window.dataDate;
			var taskId = window.taskId;
			var taskInsId = window.taskInsId;
			$.ajax({
				cache : false,
				async : true,
				url : "${ctx}/rpt/frs/dataSum/judgeAndSaveSingleSumSts?doFlag=rptfill-todowork-oper",
				dataType : 'json',
				beforeSend : function() {
					BIONE.showLoading("数据汇总中，请稍等...");
				},
				data : {
					rptId : rptId, dataDate : dataDate, orgNo : orgNo,taskId :taskId,taskInsId : taskInsId
				},
				type : "post",
				success : function(result){
					if(result && "OK" == result.result){
						View.ajaxJson();
						BIONE.tip("汇总成功");
					}else if(result && result.result){
						BIONE.tip(result.result);
					}else{
						BIONE.tip("数据异常，请联系系统管理员");
					}
				},
				error:function(){
					//BIONE.tip("数据异常，请联系系统管理员");
				},
				complete: function(){
					BIONE.hideLoading();
				}
			});
		}else{
			BIONE.tip("数据异常，请联系系统管理员");
		}
	}
	
	tmp.checkResult = function() {                                             
		if(window.orgNo && "${rptId}" && window.dataDate && "${type}"){
			BIONE.commonOpenLargeDialog('校验结果查看','checkResultWin','${ctx}/rpt/frs/rptfill/showTab?path=rptfill-todowork-check-result&rptId=${rptId}&dataDate='+window.dataDate+'&orgNo='+window.orgNo+'&type=${type}&taskId='+window.taskId+'&checkType='+checkedType,function (){
			});			
		}else{
			BIONE.tip("数据异常，请联系系统管理员");
		}
	};
	
	tmp.openHisview = function(){
		BIONE.commonOpenLargeDialog('查看修改记录','hisViewWin','${ctx}/rpt/frs/rptfill/showTab?path=rpt-todowork-his-detail&taskInstanceId='+window.taskInsId+'&cellNo='+window.posi+"&templateId=${templateId}",function (){
		});	
	};
	
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
			url: '${ctx}/rpt/frs/rptfill/showTab?path=rptfill-sumview-index&dataDate='+window.dataDate+'&rptId=${rptId}&orgNo='+window.orgNo+'&orgType=${type}',
			width: 600, 
			height: 320, 
			modal: false, 
			isResize: true,
			allowClose : false,
			showMin : true
		});
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
			url: '${ctx}/rpt/frs/rptfill/showTab?path=rptfill-compare-index&dataDate='+window.dataDate+'&rptId=${rptId}&orgNo='+window.orgNo,
			width: 600, 
			height: 320, 
			modal: false, 
			isResize: true,
			allowClose : false,
			showMin : true
		});
	}
	
	tmp.caculate = function() {
		$.ligerDialog.confirm("计算前需要保存数据，并清除校验结果，是否继续？", function(yes) {
			if(yes){
				tmp.save(false,tmp.caculateInfo,"正在计算中，请稍候...");
			}
		});
	};
	
	tmp.caculateInfo= function(){
		if(window.orgNo && "${rptId}" && window.dataDate && "${lineId}"){
			var orgNo = window.orgNo;
			var rptId = "${rptId}";
			var dataDate = window.dataDate;
			var busiLineId = "${lineId}";
			$.ajax({
				cache : false,
				async : false,
				url : "${ctx}/rpt/frs/rptfill/getTmpId",
				dataType : 'text',
				data : {
					rptId : rptId, dataDate : dataDate, lineId : busiLineId
				},
				type : "post",
				success : function(result){
					if(result){
						var obj = {"RelRptTmpId" : result, "OrgNo" : orgNo, "DataDate" : dataDate};
						c = setInterval(ifDo,2000);
						$.ajax({
							async : true,
							url : "${ctx}/rpt/frs/rptfill/caculateRpt",
							dataType : 'json',
							type : 'POST',
							data : {
								objArr : JSON2.stringify(obj)
							},
							complete: function(){
								fl= true;
							},
							success: function(result) {
								rl = result.result;
							},
							error: function(){
								//BIONE.tip("数据异常，请联系系统管理员");
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
	tmp.importData = function() {
		if(window.taskInsId && window.orgNo && "${rptId}" && window.dataDate && "${lineId}" && "${type}"){
			var rptId = "${rptId}";
			var dataDate = window.dataDate;
			var orgNo = window.orgNo;
			var lineId = "${lineId}";
			var taskInsId = window.taskInsId;
			var type = "${type}";
			BIONE.commonOpenDialog('导入数据文件','uploadWin',600,330,"${ctx}/rpt/frs/rptfill/showTab?path=rpt-todowork-uploadfile&dataDate=" + dataDate + "&orgNo=" + orgNo + "&rptId=" + rptId + "&taskInsId=" + taskInsId + "&lineId=" + lineId + "&type=" + type + "&flag=ONE&entry=EXCEL");
		}else{
			BIONE.tip("数据异常，请联系系统管理员");
		}
	};
	tmp.reset = function() {
		View.reset();
	};
	
	tmp.init = function() {
		 $.ligerDialog.confirm('确定要恢复初始值？', '恢复初始值', function(yes) {
				if (yes) {

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
			 				orgNo : window.orgNo,
							dataDate : window.dataDate,
							taskInsId : window.taskInsId
						}, 
						success: function(result){
							if(result.error == "")
								View.ajaxJson();
							else{
								BIONE.hideLoading();
								BIONE.tip(result.error);
							}
						},
						error: function(){
							BIONE.hideLoading();
						}
					});
				}
		 });
	};
	
	tmp.promptAndSave = function(flag) {
		$.ligerDialog.alert("修改数据并保存将清除校验结果", "提示", function(yes) {
			if(yes){
				tmp.save(flag);
			}
		});
	}
	tmp.save = function(flag,func,message,param) {//flag true-正常保存，false-检验、汇总、计算、提交之前保存
		if(window.taskInsId){
			var changeInfo = View.generateChangeInfo();
			if(changeInfo.isValid == false){
				BIONE.tip("填报数据有误，无法保存");
				return;
			}
				$.ajax({
					async : false,
					url : "${ctx}/rpt/frs/rptfill/saveData",
					dataType : 'json',
					type : 'POST',
					beforeSend : function() {
						BIONE.showLoading(message);
					},
					data : {
						cells : JSON2.stringify(changeInfo.cells),
						rptId : "${rptId}",
//	 					orgNo : window.orgNo,
						dataDate : window.dataDate,
						busiLineId : "${lineId}",
						taskInsId : window.taskInsId,
						searchArgs : changeInfo.searchArgs
					}, 
					success: function(result) {
						View.refreshCellVals(result.res);
						View.refreshOldCellVals(result.oldRes);
						$.ajax({
							cache : false,
							async : false,
							url : "${ctx}/rpt/frs/rptfill/updateTask?taskInsId="+window.taskInsId,
							dataType : 'json',
							type : "post",
							success : function(result){
								if("OK"== result.result){
									/* if(null != parent.child.grid){
										parent.child.grid.loadData();
									} */
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
								if("ERROR" == result.result){
									BIONE.hideLoading();
									BIONE.tip("数据异常，请联系系统管理员");
								}
							},
							error:function(){
								BIONE.hideLoading();
								//BIONE.tip("数据加载异常，请联系系统管理员");
							}
						});
					},
					error:function(){
						BIONE.hideLoading();
						//BIONE.tip("数据加载异常，请联系系统管理员");
					}
				});
			}else{
				BIONE.hideLoading();
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
	
	tmp.finishInfo = function(){
		if(window.taskInsId && window.orgNo && "${rptId}" && window.dataDate && "${lineId}"){
			var orgNo = window.orgNo;
			var rptId = "${rptId}";
			var dataDate = window.dataDate;
			var lineId = "${lineId}";
			$.ajax({
				cache : false,
				async : true,
				url : "${ctx}/rpt/frs/rptfill/getRsSts",
				dataType : 'json',
				data : {
					rptId : rptId, dataDate : dataDate, orgNo : orgNo
				},
				type : "post",
				success : function(result){
					if(result && result.taskInstanceId){
						var failFlag = false;
						var taskInsIds = [];
						var params = [];
						//03-人行
						if("${type}" && "03" == "${type}"){
							var logicRs = result.logicRs;
							var sumpartRs = result.sumpartRs;
							var warnRs = result.warnRs;
							if((logicRs == '01' || logicRs == '02') || (sumpartRs == '01' || sumpartRs == "02") || (warnRs == '01' || warnRs == '02' )){
								BIONE.hideLoading();
								BIONE.tip("该记录正在校验，请等候");
								return;
							}
							if((logicRs == null || logicRs == '04' || logicRs == '06') || (sumpartRs == null || sumpartRs == '04' || sumpartRs == "06") || (warnRs == null || warnRs == '04' || warnRs == '06' )){
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
									tmp.finishFill(taskInsIds,params);
								}else{
									// 如果点击否，则去掉遮盖，否则继续进行后续操作。
									BIONE.hideLoading(taskInsIds,params);
								}
							});
						}else{
							tmp.finishFill();
						}
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
	tmp.finishFill = function(taskInsIds,params){
		if(typeof(taskInsIds) == 'undefined'){
			var taskInsIds = [];
		}
		if(typeof(params) == 'undefined'){
			var params = [];
		}
		taskInsIds.push(window.taskInsId);
		var param = {'taskIns' : window.taskInsId, 'orgNo' : window.orgNo, 'rptId' : "${rptId}", 'dataDate' : window.dataDate, 'type' : "${type}"};
		params.push(param);
		if(taskInsIds.length > 0 && params.length > 0){
			$.ajax({
				cache : false,
				async : true,
				url : "${ctx}/rpt/frs/rptfill/submitJudge",
				dataType : 'json',
				type : "post",
				data : {
					params : JSON2.stringify(params),
					sts : "0"
				},
				success : function(result){
					BIONE.hideLoading();
					if(result.result == "ERROR"){
						BIONE.tip("数据异常，请联系系统管理员");
						return;
					}
					if(result.result == "YES" && result.ins){             
						var height = $(window).height() - 30;
						var width = $(window).width() - 80;
						BIONE.hideLoading();
						BIONE.commonOpenDialog("未结束填报子机构任务实例", "taskInsChildWin", width, height, "${ctx}/report/frs/rptfill/rptFillTabController.mo?doFlag=rptfill-org-child-oper-ins&params="+JSON2.stringify(params), null);
					}else{
						$.ajax({
							cache : false,
							async : true,
							url : "${ctx}/report/frs/rptfill/rptFillTabController.mo?_type=data_event&_field=finishTaskBatch&_event=POST&_comp=main&Request-from=dhtmlx&taskInsIds=" + taskInsIds.join(",")+"&d="+new Date().getTime(),
							dataType : 'json',
							type : "get",
							complete:function(){
								BIONE.hideLoading();
							},
							success : function(){
								if(null != parent.child.grid){
									parent.child.grid.loadData();
								}
								if(null != parent.child.tmp.resetInfo){
									parent.child.tmp.resetInfo();
								}
								BIONE.tip("填报完成");
								BIONE.closeDialog("taskFillWin");
							},
							error:function(){
								//BIONE.tip("数据加载异常，请联系系统管理员");
							}
						});
					}	
				}
			});
		}else{
			BIONE.tip("数据异常，请联系系统管理员");
		}
	};
	
	tmp.submit = function() {
		$.ligerDialog.confirm("归档前需要保存数据，并清除校验结果，是否继续？", function(yes) {
			if(yes){
				tmp.save(false,tmp.submitInfo,"正在归档，请稍候...");
			}
		});
	};
	
	tmp.submitInfo =function(){
		if(window.taskInsId && window.orgNo && "${rptId}" && window.dataDate && "${lineId}"){
			var orgNo = window.orgNo;
			var rptId = "${rptId}";
			var dataDate = window.dataDate;
			var lineId = "${lineId}";
			$.ajax({
				cache : false,
				async : true,
				url : "${ctx}/report/frs/rptfill/rptFillTabController.mo?_type=data_event&_field=getRsSts&_event=POST&_comp=main&Request-from=dhtmlx",
				dataType : 'json',
				data : {
					rptId : rptId, dataDate : dataDate, orgNo : orgNo
				},
				type : "post",
				success : function(result){
					if(result && result.taskInstanceId){
						var failFlag = false;
						var taskInsIds = [];
						var params = [];
						//03-人行
						if("${type}" && "03" == "${type}"){
							var logicRs = result.logicRs;
							var sumpartRs = result.sumpartRs;
							var warnRs = result.warnRs;
							if((logicRs == '01' || logicRs == '02') || (sumpartRs == '01' || sumpartRs == "02") || (warnRs == '01' || warnRs == '02' )){
								BIONE.hideLoading();
								BIONE.tip("该记录正在校验，请等候");
								return;
							}
							if((logicRs == null || logicRs == '04' || logicRs == '06') || (sumpartRs == null || sumpartRs == '04' || sumpartRs == "06") || (warnRs == null || warnRs == '04' || warnRs == '06' )){
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
									taskInsIds.push(window.taskInsId);
									var param = {'taskIns' : window.taskInsId, 'orgNo' : window.orgNo, 'rptId' : "${rptId}", 'dataDate' : window.dataDate, 'type' : "${type}"};
									params.push(param);
									if(taskInsIds.length > 0 && params.length > 0){
										$.ajax({
											cache : false,
											async : true,
											url : "${ctx}/report/frs/rptfill/rptFillTabController.mo?_type=data_event&_field=submitJudge&_event=POST&_comp=main&Request-from=dhtmlx",
											dataType : 'json',
											type : "post",
											data : {
												params : JSON2.stringify(params),
												sts : "0"
											},
											success : function(result){
												BIONE.hideLoading();
												if(result.result == "ERROR"){
													BIONE.tip("数据异常，请联系系统管理员");
													return;
												}
												if(result.result == "YES" && result.ins){                                                            
													var height = $(window).height() - 30;
													var width = $(window).width() - 80;
													BIONE.commonOpenDialog("未归档子机构任务实例", "taskInsChildWin", width, height, "${ctx}/report/frs/rptfill/rptFillTabController.mo?doFlag=rptfill-org-child-oper-ins&ins=" + result.ins + "&taskInsIds=" + taskInsIds.join(","), null);
												}else{
													$.ajax({
														cache : false,
														async : true,
														url : "${ctx}/report/frs/rptfill/rptFillTabController.mo?_type=data_event&_field=submitTaskBatch&_event=POST&_comp=main&Request-from=dhtmlx&taskInsIds=" + taskInsIds.join(",")+"&d="+new Date().getTime(),
														dataType : 'json',
														type : "get",
														complete: function(){
															BIONE.hideLoading();
														},
														success : function(){
															/* if(null != parent.child.grid){
																parent.child.grid.loadData();
															} */
															//parent.window.frames.onetab.contentWindow.frames.tsktab.contentWindow.tmp.reAsyncChildNodes
															if(null !=parent.frames.findIframeByTitle("报表填报")[0].contentWindow.frames.tsktab){
																parent.frames.findIframeByTitle("报表填报")[0].contentWindow.frames.tsktab.contentWindow.tmp.reAsyncChildNodes("left");
																parent.frames.findIframeByTitle("报表填报")[0].contentWindow.frames.tsktab.contentWindow.tmp.reAsyncChildNodes("right");
															}
															if(null!=parent.frames.findIframeByTitle("报表填报")[0].contentWindow.frames.listtab.contentWindow){
																parent.frames.findIframeByTitle("报表填报")[0].contentWindow.frames.listtab.contentWindow.grid.loadData();
															}
															BIONE.tip("归档成功");
															BIONE.closeDialog("taskFillWin");
														},
														error:function(){
															//BIONE.tip("数据加载异常，请联系系统管理员");
														}
													});
												}
											},
											error:function(){
												BIONE.hideLoading();
												//BIONE.tip("数据加载异常，请联系系统管理员");
											}
										});
									}else{
										BIONE.hideLoading();
										BIONE.tip("数据异常，请联系系统管理员");
									}
								}
								else{
									BIONE.hideLoading();
								}
							});
						}else{
							taskInsIds.push(window.taskInsId);
							var param = {'taskIns' : window.taskInsId, 'orgNo' : window.orgNo, 'rptId' : "${rptId}", 'dataDate' : window.dataDate, 'type' : "${type}"};
							params.push(param);
							if(taskInsIds.length > 0 && params.length > 0){
								$.ajax({
									cache : false,
									async : true,
									url : "${ctx}/report/frs/rptfill/rptFillTabController.mo?_type=data_event&_field=submitJudge&_event=POST&_comp=main&Request-from=dhtmlx",
									dataType : 'json',
									type : "post",
									data : {
										params : JSON2.stringify(params),
										sts : "0"
									},
									success : function(result){
										if(result.result == "ERROR"){
											BIONE.hideLoading();
											BIONE.tip("数据异常，请联系系统管理员");
											return;
										}
										if(result.result == "YES" && result.ins){
											var height = $(window).height() - 30;
											var width = $(window).width() - 80;
											BIONE.hideLoading();
											BIONE.commonOpenDialog("未归档子机构任务实例", "taskInsChildWin", width, height, "${ctx}/report/frs/rptfill/rptFillTabController.mo?doFlag=rptfill-org-child-oper-ins&ins=" + result.ins + "&taskInsIds=" + taskInsIds.join(","), null);
										}else{
											$.ajax({
												cache : false,
												async : true,
												url : "${ctx}/report/frs/rptfill/rptFillTabController.mo?_type=data_event&_field=submitTaskBatch&_event=POST&_comp=main&Request-from=dhtmlx&taskInsIds=" + taskInsIds.join(",")+"&d="+new Date().getTime(),
												dataType : 'json',
												type : "get",
												complete: function(){
													BIONE.hideLoading();
												},
												success : function(){
													/* if(null != parent.child.grid){
														parent.child.grid.loadData();
													} */
													//parent.frames.findIframeByTitle("报表填报")[0].contentWindow.frames.tsktab
													if(null !=parent.frames.findIframeByTitle("报表填报")[0].contentWindow.frames.tsktab){
														parent.frames.findIframeByTitle("报表填报")[0].contentWindow.frames.tsktab.contentWindow.tmp.reAsyncChildNodes("left");
														parent.frames.findIframeByTitle("报表填报")[0].contentWindow.frames.tsktab.contentWindow.tmp.reAsyncChildNodes("right");
													}
													if(null!=parent.frames.findIframeByTitle("报表填报")[0].contentWindow.frames.listtab.contentWindow){
														parent.frames.findIframeByTitle("报表填报")[0].contentWindow.frames.listtab.contentWindow.grid.loadData();
													}
													BIONE.tip("归档成功");
													BIONE.closeDialog("taskFillWin");
												},
												error:function(){
													//BIONE.tip("数据加载异常，请联系系统管理员");
												}
											});
										}
									},
									error:function(){
										BIONE.hideLoading();
										//BIONE.tip("数据加载异常，请联系系统管理员");
									}
								});
							}else{
								BIONE.hideLoading();
								BIONE.tip("数据异常，请联系系统管理员");
							}
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
	tmp.check = function() {
		$.ligerDialog.confirm("校验前需要保存数据，是否继续？", function(yes) {
			if (yes) {
				tmp.save(false,tmp.checkInfo,"正在校验数据，请稍候...");
				
			}
		});
	};
	
	tmp.checkInfo=function(){
		if(window.orgNo && "${rptId}" && window.dataDate && "${lineId}"){
			var orgNo = window.orgNo;
			var rptId = "${rptId}";
			var dataDate = window.dataDate;
			var lineId = "${lineId}";
			var taskId = window.taskId;
			var taskType = "${type}";
			//判断报表是否有校验关系，若没有不进行校验
			/* $.ajax({
				cache : false,
				async : true,
				url : "${ctx}/rpt/frs/rptfill/getRsSts",
				dataType : 'json',
				data : {
					rptId : rptId, dataDate : dataDate, orgNo : orgNo
				},
				type : "post",
				success : function(result){
					
				},
				error:function(){
					BIONE.hideLoading();
					BIONE.tip("数据异常，请联系系统管理员");
				}
			}); */
			//判断 校验状态
			$.ajax({
				cache : false,
				async : true,
				url : "${ctx}/report/frs/rptfill/rptFillTabController.mo?_type=data_event&_field=getRsSts&_event=POST&_comp=main&Request-from=dhtmlx",
				dataType : 'json',
				data : {
					rptId : rptId, dataDate : dataDate, orgNo : orgNo
				},
				type : "post",
				success : function(result){
					if(result && result.taskInstanceId){
						//03-人行
						if("${type}" && "03" == "${type}"){
							var logicRs = result.logicRs;
							var sumpartRs = result.sumpartRs;
							var warnRs = result.warnRs;
							if((logicRs == '01' || logicRs == '02') || (sumpartRs == '01' || sumpartRs == "02") || (warnRs == '01' || warnRs == '02' )){
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
						$.ajax({
							cache : false,
							async : true,
							url : "${ctx}/report/frs/rptfill/rptFillTabController.mo?_type=data_event&_field=getTmpId&_event=POST&_comp=main&Request-from=dhtmlx",
							dataType : 'text',
							data : {
								rptId : rptId, dataDate : dataDate, lineId : lineId
							},
							type : "post",
							success : function(result){
								if(result){
									var objArr = [];
									var obj = {"rptId" : rptId, "orgNo" : orgNo, "dataDate" : dataDate, "logicRs" : logicRs, "sumpartRs" : sumpartRs, "warnRs" : warnRs, "lineId" : lineId, "templateId" : result};
									objArr.push(obj);
									var group = {};
									group.DataDate = window.dataDate;
									group.OrgNo = window.orgNo;
									if("${type}" && "03" == "${type}"){
										group.RealTimeLogicCheckRptTmpId = [result];
										group.RealTimeWarnCheckRptTmpId = [result];
										group.RealTimeSumCheckRptTmpId = [result];
									}
									if("${type}" && "02" == "${type}"){
										group.RealTimeLogicCheckRptTmpId = [result];
										group.RealTimeWarnCheckRptTmpId = [result];
									}
			 						$.ajax({
			 							cache : false,
			 							async : true,
			 							url : "${ctx}/report/frs/rptfill/rptFillTabController.mo?_type=data_event&_field=judgeAndSaveCheckStsSync&_event=POST&_comp=main&Request-from=dhtmlx",
			 							dataType : 'json',
			 							type : 'POST',
			 							data : {
			 								objArr : JSON2.stringify(objArr),
			 								objArrParms : JSON2.stringify(group),
			 								dataDate : dataDate,
			 								tmpId : result,
			 								orgNo : orgNo,
			 								taskId : taskId,
			 								taskType: taskType
			 							},
			 							complete: function(result){
			 								BIONE.hideLoading();
			 							},
			 							success: function(result) {
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
	//单个校验
	tmp.checkSingle = function(checkType) {
		$.ligerDialog.confirm("校验前需要保存数据，是否继续？", function(yes) {
			if (yes) {
				tmp.save(false,tmp.checkSingleInfo,"正在进行校验，请稍候...",checkType);
			}
		});
	};
	//校验方法
	tmp.checkSingleInfo = function(checkType){
		if(window.orgNo && "${rptId}" && window.dataDate && "${lineId}"){
			var orgNo = window.orgNo;
			var rptId = "${rptId}";
			var dataDate = window.dataDate;
			var lineId = "${lineId}";
			var taskId = window.taskId;
			var taskType = "${type}";
			var taskInsId = window.taskInsId;
			$.ajax({
				cache : false,
				async : false,
				url : "${ctx}/report/frs/rptfill/rptFillTabController.mo?_type=data_event&_field=getRsSts&_event=POST&_comp=main&Request-from=dhtmlx",
				dataType : 'json',
				data : {
					rptId : rptId, dataDate : dataDate, orgNo : orgNo,taskInsId :taskInsId
				},
				type : "post",
				success : function(result){
					var logicRs = null;
					var zeroRs = null;
					var sumpartRs = null;
					var warnRs = null;
					if(result && result.taskInstanceId){
						if("logic" == checkType){
							if(result.logicRs)
								logicRs = result.logicRs;
							if(logicRs == '01' || logicRs == '02'){
								BIONE.hideLoading();
								BIONE.tip("该记录正在逻辑校验，请等候");
								return;
							}
						}
						if("zero" == checkType){
							if(result.zeroRs)
								zeroRs = result.zeroRs;
							if(zeroRs == '01' || zeroRs == '02'){
								BIONE.hideLoading();
								BIONE.tip("该记录正在零值校验，请等候");
								return;
							}
						}
						if("sumpart" == checkType){
							if(result.sumpartRs)
								sumpartRs = result.sumpartRs;
							if(sumpartRs == '01' || sumpartRs == "02"){
								BIONE.hideLoading();
								BIONE.tip("该记录正在总分校验，请等候");
								return;
							}
							var sumCheckRs = result.sumCheckSts;
							if(!sumCheckRs){
								BIONE.hideLoading();
								BIONE.tip("该记录无下级机构分发，无需总分校验");
								return;
							}
						}
						if("warn" == checkType){
							if(result.warnRs)
								 warnRs = result.warnRs;
							if(warnRs == '01' || warnRs == '02'){
								BIONE.hideLoading();
								BIONE.tip("该记录正在进行预警校验，请等候");
								return;
							}
						}
						$.ajax({
							cache : false,
							async : false,
							url : "${ctx}/report/frs/rptfill/rptFillTabController.mo?_type=data_event&_field=getTmpId&_event=POST&_comp=main&Request-from=dhtmlx",
							dataType : 'text',
							data : {
								rptId : rptId, dataDate : dataDate, lineId : lineId
							},
							type : "post",
							success : function(result){
								if(result){
									var objArr = [];
									var obj;
//										var obj = {"rptId" : rptId, "orgNo" : orgNo, "dataDate" : dataDate, "logicRs" : logicRs, "sumpartRs" : sumpartRs, "warnRs" : warnRs, "lineId" : lineId, "templateId" : result};
									var group = {};
									group.DataDate = window.dataDate;
									group.OrgNo = window.orgNo;
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
			 							url : "${ctx}/report/frs/rptfill/rptFillTabController.mo?_type=data_event&_field=judgeAndSaveCheckStsSync&_event=POST&_comp=main&Request-from=dhtmlx",
			 							dataType : 'json',
			 							type : 'POST',
			 							data : {
			 								objArr : JSON2.stringify(objArr),
			 								objArrParms : JSON2.stringify(group),
			 								dataDate : dataDate,
			 								tmpId : result,
			 								orgNo : orgNo,
			 								taskId : taskId,
			 								taskType: taskType
			 							},
			 							complete: function(){
			 								BIONE.hideLoading();
			 							},
			 							success: function(result) {
				 							if(result.result){
				 								if("OK" == result.result){
				 									//BIONE.tip("已发送校验，请您稍等片刻 ");
				 									tmp.initGridData();
				 									var color = result.color;
				 									View.ajaxJson(color);
				 									/* if(null != parent.child.grid){
				 										parent.child.grid.loadData();
				 									} */
				 									BIONE.tip("校验完成");
				 									if(color && "[]" != color){
				 										checkedType = checkType;
					 									tmp.checkResult();
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
	// 初始化设计器
	tmp.initDesign = function(color) {
		require.config({
			baseUrl : "${ctx}/plugin/js/",
			paths:{
				"view" : "show/views/rptview"
			}
		});
		require(["view"] , function(view){
			templateinit("${type}", true);
			var orgNo = window.orgNo;
			var rptId = "${rptId}";
			var dataDate = window.dataDate;
			var lineId = "${lineId}";
			var orgName = "${orgNm}";
			var fileName = '${fileName}';
			fileName = decodeURI(fileName);
			var argsArr = [];
			var args1 = {'DimNo':'ORG','Op':'=','Value':orgNo};
			argsArr.push(args1);
			var settings = {
					targetHeight : ($("#center").height() -$("#top").height() -$("#remarkDiv").height() - 31),
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
					onCellDoubleClick : tmp.spreadDbclkCell ,
					onEditEnded : tmp.spreadEditEnded
			};
			View = view;
			var spread = view.init($("#spread") , settings);
			Spread = spread;
		});
	};
	tmp.spreadEnterCell = function(sender , args, info){
		window.posi = View.Utils.initAreaPosiLabel(args.row , args.col);
		/* if(info && info.isValidate == 'N'){//校验未通过
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
		} */
	};
	//双击单元格事件
	tmp.spreadDbclkCell = function (sender , args, info){
		
	};
	
	tmp.spreadEditEnded = function (sender , args){
		var compareWin = liger.get("compareWin");
		var a = new Date().getTime();
		if(compareWin 
				&& typeof compareWin != "undefined"
				&& compareWin.frame
				&& compareWin.frame.grid){
			var editText = args.editingText == null ? "" : args.editingText;
			var posi = View.Utils.initAreaPosiLabel(args.row , args.col);
			var gridData = compareWin.frame.grid.getData();
			for(var i = 0 , l = gridData.length ; i < l ; i++){
				if(gridData[i].cellNo == posi){
					var rowId = "r"+(1000+i+1);
					var rowData = compareWin.frame.grid.getRow(rowId);
					rowData.newVal = editText+"";
					compareWin.frame.grid.updateRow(rowData , rowData);
				}
			}
		}
	}
	
	tmp.download = function(){
		var argsArr = [];
		if(window.taskInsId && window.orgNo && "${rptId}" && window.dataDate && "${lineId}" && "${orgNm}"){
			var rptId = "${rptId}";
			var orgNo = window.orgNo;
			var busiLineId = "${lineId}";
			var dataDate = window.dataDate;
			var orgName = "${orgNm}";
			var argsArr1 = [];
			var args1 = {'DimNo':'ORG','Op':'=','Value':orgNo};
			argsArr1.push(args1);
			var args = {'orgNm':orgName,'rptId':rptId,'dataDate':dataDate,'busiLineId':busiLineId,'searchArgs':JSON2.stringify(argsArr1)};
			argsArr.push(args);
		}else{
			BIONE.tip("数据异常，请联系系统管理员");
		}
		if(argsArr.length > 0){
			BIONE.ajax({
					async : false,
					url : "${ctx}/report/frs/rptfill/rptFillTabController.mo?_type=data_event&_field=downloadList&_event=GET&_comp=main&Request-from=dhtmlx&json="+encodeURI(encodeURI(JSON2.stringify(argsArr)))+"&d="+ new Date().getTime(),
					dataType : 'json',
					type : 'get',
					loading : '正在生成下载文件，请稍等...'
				}, function(result) {
					if(result.result){
						if("OK" == result.result){
							if(result.zipFilePath && result.folderinfoPath){
								var src = '';
								src = "${ctx}/report/frs/rptfill/rptFillTabController.mo?_type=data_event&_field=downFile&_event=POST&_comp=main&Request-from=dhtmlx&zipFilePath="+encodeURI(encodeURI(result.zipFilePath))+"&folderinfoPath=" + encodeURI(encodeURI(result.folderinfoPath)) + "&d="+ new Date().getTime();
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
			
			
// 			var src = '';
// 			src = "${ctx}/rpt/frs/rptfill/downFile?json="+encodeURI(encodeURI(JSON2.stringify(argsArr)))+"&d="+ new Date();
// // 			window.open(src, '数据下载');
// 			download.attr('src', src);
		}else{
			BIONE.tip("数据异常，请联系系统管理员");
		}
// 		var json=Spread.toJSON();
// 		var src = '';
// 		src = "${ctx}/rpt/frs/rptfill/download?json="+encodeURI(encodeURI(JSON2.stringify(json)))+"&rptNm=" + encodeURI("${rptNm}") + "&d="+ new Date();
// 		download.attr('src', src);
	};
	
	tmp.descbtn = function() {
		BIONE.commonOpenFullDialog("报表说明","rptViewWin","${ctx}/report/frame/rptmgr/RptInfoController.mo?doFlag=report-frs-info&rptId=${rptId}&show=1");	
	};
	
	tmp.busiremark = function(){
		var selIdxs = View.getSelectionIdxs();
		if(!selIdxs
				|| selIdxs.length <= 0){
			BIONE.tip("非数据单元格无法填写业务备注");
			return;
		}
		var cellNo = selIdxs[0].cellNo;
		BIONE.commonOpenDialog("业务备注","remarkWin",800,400,"${ctx}/report/frs/rptfill/rptFillTabController.mo?doFlag=rptfill-remark-info&rptId=${rptId}&type=${type}&taskInsId="+window.taskInsId+"&orgNo="+window.orgNo+"&dataDate="+window.dataDate+"&cellNo="+cellNo);	

	}
	tmp.buttonObject = {
			"INITBTN" : { text : '恢复初始值', width : '60px', appendTo : '#button', operNo:'initBtn', click : tmp.init},
			"RESETBTN" : { text : '数据重置', width : '50px', appendTo : '#button', operNo:'resetBtn', click : tmp.reset},
			"EXPORTBTN" : { text : '数据下载', width : '50px', appendTo : '#button', operNo:'exportBtn', click : tmp.download},
	  		"IMPORTBTN" : { text : '数据导入', width : '50px', appendTo : '#button', operNo:'importBtn', click : tmp.importData},
			"CALCULATEBTN" : { text : '条线汇总', width : '50px', appendTo : '#button', operNo:'calculateBtn', click : tmp.caculate},
			"VALIDBTN" : { text : '校验', width : '50px', appendTo : '#button', operNo:'validBtn', click : tmp.check},
			"SUMBTN" : { text : '机构汇总', width : '50px', appendTo : '#button', operNo:'sumBtn', click : tmp.sumData},
			"COMPAREBTN" : { text : '比上期', width : '50px', appendTo : '#button', operNo:'compareBtn', click : tmp.openCompare},
			"HISVIEWBTN" : { text : '修改记录', width : '50px', appendTo : '#button', operNo:'hisviewBtn', click : tmp.openHisview},
			"SUMVIEWBTN" : { text : '汇总查看', width : '50px', appendTo : '#button', operNo:'sumviewBtn', click : tmp.openSumview},
			"DESCBTN" : { text : '填报说明', width : '50px', appendTo : '#button', operNo:'descBtn', click : function(){tmp.descbtn()}},
			"REMARKBTN" : { text : '备注说明', width : '50px', appendTo : '#button', operNo:'remarkBtn', click : function(){tmp.busiremark()}},
			
			"SAVEBTN" : { text : '保存', width : '30px', appendTo : '#button', operNo:'saveBtn', click : function(){tmp.promptAndSave(true);}},
			"BACKBTN" : { text : '返回', width : '30px', appendTo : '#button', operNo:'backBtn', click : tmp.back},
			"LOGICBTN" : { text : '逻辑校验', width : '50px', appendTo : '#button', operNo:'logicBtn', click : function(){tmp.checkSingle("logic");}},
			"WARNBTN" : { text : '预警校验', width : '50px', appendTo : '#button', operNo:'warnBtn', click : function(){tmp.checkSingle("warn");}},
			"SUMPARTBTN" : { text : '总分校验', width : '50px', appendTo : '#button', operNo:'sumpartBtn', click : function(){tmp.checkSingle("sumpart");}},
			"ZEROBTN" : { text : '零值校验', width : '50px', appendTo : '#button', operNo:'zeroBtn', click : function(){tmp.checkSingle("zero");}},
			"CHECKRESULTBTN" : {br : 'Y', text : '校验结果', width : '50px', appendTo : '#button', operNo:'checkResultBtn', click : tmp.checkResult},
			"HANDLEBTN" : { text : '归档', width : '30px', appendTo : '#button', operNo:'handleBtn', click : tmp.submit},
			"FINISHBTN" : { text : '结束填报', width : '50px', appendTo : '#button', operNo:'finishBtn', click : tmp.finish}
			
			
	};
	/* tmp.buttonObject = {
			"INITBTN" : { text : '恢复初始值', width : '60px', appendTo : '#button1', operNo:'initBtn', click : tmp.init},
			"RESETBTN" : { text : '数据重置', width : '60px', appendTo : '#button1', operNo:'resetBtn', click : tmp.reset},
			"EXPORTBTN" : { text : '数据下载', width : '50px', appendTo : '#button1', operNo:'exportBtn', click : tmp.download},
	  		"IMPORTBTN" : { text : '数据导入', width : '50px', appendTo : '#button1', operNo:'importBtn', click : tmp.importData},
			"CALCULATEBTN" : { text : '条线汇总', width : '50px', appendTo : '#button1', operNo:'calculateBtn', click : tmp.caculate},
			"VALIDBTN" : { text : '校验', width : '50px', appendTo : '#button1', operNo:'validBtn', click : tmp.check},
			"SUMBTN" : { text : '数据汇总', width : '50px', appendTo : '#button1', operNo:'sumBtn', click : tmp.sumData},
			"COMPAREBTN" : { text : '比上期', width : '50px', appendTo : '#button1', operNo:'compareBtn', click : tmp.openCompare},
			"HISVIEWBTN" : { text : '修改记录', width : '80px', appendTo : '#button1', operNo:'hisviewBtn', click : tmp.openHisview},
			"SUMVIEWBTN" : { text : '汇总查看', width : '50px', appendTo : '#button1', operNo:'sumviewBtn', click : tmp.openSumview},
			"DESCBTN" : { text : '填报说明', width : '60px', appendTo : '#button1', operNo:'descBtn', click : function(){tmp.descbtn()}},
			"REMARKBTN" : { text : '备注说明', width : '60px', appendTo : '#button1', operNo:'remarkBtn', click : function(){tmp.busiremark()}},
			
			"SAVEBTN" : { text : '保存', width : '30px', appendTo : '#button2', operNo:'saveBtn', click : function(){tmp.promptAndSave(true);}},
			"BACKBTN" : { text : '返回', width : '30px', appendTo : '#button2', operNo:'backBtn', click : tmp.back},
			"LOGICBTN" : { text : '逻辑校验', width : '50px', appendTo : '#button2', operNo:'logicBtn', click : function(){tmp.checkSingle("logic");}},
			"WARNBTN" : { text : '预警校验', width : '60px', appendTo : '#button2', operNo:'warnBtn', click : function(){tmp.checkSingle("warn");}},
			"SUMPARTBTN" : { text : '总分校验', width : '50px', appendTo : '#button2', operNo:'sumpartBtn', click : function(){tmp.checkSingle("sumpart");}},
			"ZEROBTN" : { text : '零值校验', width : '50px', appendTo : '#button2', operNo:'zeroBtn', click : function(){tmp.checkSingle("zero");}},
			"CHECKRESULTBTN" : {br : 'Y', text : '校验结果', width : '80px', appendTo : '#button2', operNo:'checkResultBtn', click : tmp.checkResult},
			"HANDLEBTN" : { text : '归档', width : '30px', appendTo : '#button2', operNo:'handleBtn', click : tmp.submit},
			"FINISHBTN" : { text : '结束填报', width : '50px', appendTo : '#button2', operNo:'finishBtn', click : tmp.finish}
			
			
	}; */
	tmp.initButtons = function() {
		$.ajax({
			cache : false,
			async : false,
			url : "${ctx}/report/frs/rptfill/rptFillTabController.mo?_type=data_event&_field=createButton&_event=POST&_comp=main&Request-from=dhtmlx",
			dataType : 'json',
			data : {
				taskType : "${type}", flag : "1"
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
	};
	//判断计算返回
	function ifDo(){
		if(fl){
			BIONE.hideLoading();
			if(rl!=null){
				if("OK" == rl){
					View.ajaxJson();
					BIONE.tip("计算成功");
				}else if("NO"==rl){
					BIONE.tip("报表无需计算");
				}
			}else{
				 BIONE.tip("数据异常，请联系系统管理员");
			}
			if(c!=null){
				clearInterval(c);
			}
			
			fl = false;
		}
	}
	function updateColor(){
		//查看校验结果时，刷新报表颜色 民泰修改20160112
		//String dataDate, String orgNo, String tmpId
		$.ajax({
		cache : false,
		async : false,
		url : "${ctx}/report/frs/rptfill/rptFillTabController.mo?_type=data_event&_field=showTemplateId&_event=POST&_comp=main&Request-from=dhtmlx",
		dataType : 'text',
		data : {
			rptId : "${rptId}",
			dataDate : window.dataDate
		},
		type : "post",
		success : function(result){
			$.ajax({
				cache : false,
				async : true,
				url : "${ctx}/report/frs/rptfill/rptFillTabController.mo?_type=data_event&_field=createColor&_event=POST&_comp=main&Request-from=dhtmlx",
				dataType : 'json',
				data : {
					dataDate : window.dataDate, orgNo : window.orgNo,tmpId :result
				},
				type : "post",
				success : function(color){
					View.ajaxJson(JSON.stringify(color));
				}
			});
		}
	}); 
	}
</script>
</head>
<body>
</body>

</html>