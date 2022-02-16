<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/meta.jsp"%>
<html>
<head>

<meta name="decorator" content="/template/template26_BS.jsp">
<%@ include file="/common/taglibs.jsp"%>
<%@ include file="/common/spreadjs_load.jsp"%>
<script type="text/javascript">
	var base_BtnFlag = "01";
	var base_QueryInit = false;//查询标识 QueryInit:false 正常值  QueryInit:true 初始值 O
	var base_Download = null;
	var base_View;//设计器视图
	var base_Spread;//报表设计器
	var base_Logic = null;//逻辑校验
	var base_Sumpart = null;//总分校验
	var base_Warn = null;//警戒校验
	var base_DataDate = "20190916";//数据日期
	var base_OrgNo = "9999";//机构编号
	var base_OrgNm = "宁夏银行总行";//机构名称
	var base_TaskInsId = "f45ec4723e3f482992b17d36134f4441";
	var base_TaskId = "22727f9ae5e84c6296225fb6a46097aa";
	var base_SelIdxs = []; // 选择的指标信息
	var base_CheckedType = "NO";//校验类型 
	var base_OperType = "03";
	var base_InitRptId = "aeac78a5a7034d25801c10b5a719b88a";//报表编号
	var base_InitrptName = "%E9%87%91%E6%A0%87%E5%87%86%E7%A1%AE%E6%80%A7%E6%A0%A1%E9%AA%8C";//报表名称
	var base_OrgType = "02";//机构类型
	var base_TmpId = "";//报表模板编号
	var base_selectIdxNo = "";//当前选择单元格指标编号
	var base_selectCellNo = "";//当前选择单元编号
	var base_selectDataPrecision = "";//当前选择单元格指标精度
	var base_selectUnit = "";//当前选择单元格指标单位
	var base_selectIdxNm = "";//当前选择单元格指标名称
	var tabObj = null;
	var base_layout_upDown = true;//初始进来是上下布局
	var tmp = {
			urlTmp : ['${ctx}/rpt/frs/rptfill/checkCacheInfo']
		};
	$(function() {
		showTemplateId();
		initDesign();
		initSearchForm();
		initSearchButtons("#search", "#searchbtn");
		initButtons();
		initExport();
		initCheckResult();
		initTool();
	});

	//初始化一些页面事件
	function initTool() {
		$(window).bind('beforeunload', function(e) {
			if (e.originalEvent.clientY < 0) { //来自窗口点击（非当前页面点击）
				return "结束填报前需要保存数据，并清除校验结果，是否继续？";
			} else {
				e.stopPropagation();
			}
		});
		window.onbeforeunload = onbeforeunload_handler;
		$("#formsearch").css("margin-top", "1px").css("margin-bottom", "1px");
		$("#searchbtn").css("margin-top", "1px").css("margin-bottom", "1px").css("padding-top", "0px").css("padding-bottom", "0px");
	};

	//页面离开事件
	function onbeforeunload_handler() {
		if (tmp.isUpdateData()) {
			return "结束填报前需要保存数据，并清除校验结果，是否继续？";
		}
	}

	// 初始化设计器
	function initDesign() {
		require.config({
			baseUrl : "${ctx}/plugin/js/",
			paths : {
				"view" : "show/views/rptview"
			}
		});
		require([ "view" ], function(view) {
			var base_OrgType='02';
			templateinit(base_OrgType, true);
			var fileName = '';
			if (parent.child && parent.child.fileName) {
				fileName = parent.child.fileName;
			}
			var argsArr = [];
			var args = {
				'DimNo' : 'ORG',
				'Op' : '=',
				'Value' : base_OrgNo
			};
			argsArr.push(args);
			var targetHeight = ($(window).height() - $("#mainsearch").height() - $("#top").height() - $("#checkResult").height() - 10);
			if("none" == $("#checkResult").css("display")){
				targetHeight = ($(window).height() - $("#mainsearch").height() - $("#top").height() - 10);
			}
			var settings = {
				targetHeight : targetHeight,
				ctx : "${ctx}",
				readOnly : false,
				cellDetail : true,
				toolbar : false,
				canUserEditFormula : false,
				inValidMap : window.parent.color,
				initFromAjax : true,
				searchArgs : JSON2.stringify(argsArr),
				ajaxData : {
					rptId : base_InitRptId,
					dataDate : base_DataDate,
					fileName : fileName,
					orgNm : base_OrgNm
				},
				onEnterCell : spreadEnterCell,
				onCellDoubleClick : spreadDbclkCell,
				onEditEnded : spreadEditEnded
			};
			View = view;
			var spread = view.init($("#spread"), settings);
			base_Spread = spread;
		});
	};

	//选中事件
	function spreadEnterCell(sender, args, info) {
		window.posi = View.Utils.initAreaPosiLabel(args.row, args.col);
		var selectCellNo = info.cellNo;
		var selectIdxNo = info.indexNo;
		var checkResult = $("#checkResult");
		if(selectIdxNo){
			base_selectDataPrecision = info.dataPrecision;//当前选择单元格指标精度
			base_selectUnit = info.unit;//当前选择单元格指标单位
			if (!$("#togglebtnIcon").hasClass("togglebtn-down")) {//判断是不是展开状态
				$("#togglebtnIcon").addClass("togglebtn-down");
				checkResult.slideToggle('fast', function() {
					View.spreadDom.height($(window).height()- $("#mainsearch").height()- $("#checkResult").height()  - $("#top").height() - 35);
					if(View){
						View.resize(View.spread);
					} 
				});//展开
		    }
		}else{//如果点击的不是指标，就直接收缩展示区
			if ($("#togglebtnIcon").hasClass("togglebtn-down")) {//判断是不是展开状态
				$("#togglebtnIcon").removeClass("togglebtn-down");
				checkResult.slideToggle('fast', function() {
					View.spreadDom.height($(window).height()- $("#mainsearch").height() - $("#top").height() - 35);
					if(View){
						View.resize(View.spread);
					} 
				});//收缩
		    }
			return;
		}
		base_selectIdxNm = info.cellNm;
		base_selectIdxNo = selectIdxNo;
		base_selectCellNo = selectCellNo;
		searchCheakNoPass(selectIdxNo);
		var selectTabId = tabObj.getSelectedTabItemID();
		tabObj.reload(selectTabId);
	};

	//双击单元格事件
	function spreadDbclkCell(sender, args, info) {};

	//修改结束事件
	function spreadEditEnded(sender, args) {
		var compareWin = liger.get("compareWin");
		var a = new Date().getTime();
		if (compareWin && typeof compareWin != "undefined" && compareWin.frame
				&& compareWin.frame.grid) {
			var editText = args.editingText == null ? "" : args.editingText;
			var posi = View.Utils.initAreaPosiLabel(args.row, args.col);
			var gridData = compareWin.frame.grid.getData();
			for (var i = 0, l = gridData.length; i < l; i++) {
				if (gridData[i].cellNo == posi) {
					var rowId = "r" + (1000 + i + 1);
					var rowData = compareWin.frame.grid.getRow(rowId);
					rowData.newVal = editText + "";
					compareWin.frame.grid.updateRow(rowData, rowData);
				}
			}
		}
	}

	//初始化查询表单
	function initSearchForm() {
		$("#formsearch")
				.ligerForm(
						{
							fields : [
									{
										display : "数据日期",
										name : "date",
										newline : true,
										labelWidth : 75,
										width : 120,
										space : 15,
										type : "date",
										options : {
											format : "yyyyMMdd"
										},
										validate : {
											required : false
										}
									},
									/* {
										display : '报表名称',
										name : 'rptNm',
										newline : false,
										labelWidth : 75,
										width : 260,
										space : 15,
										type : 'select',
										comboboxName : 'rptCombox',
										options : {
											url : '${ctx}/frs/rptfill/reject/rptNmComBoBox?taskId='
													+ base_TaskId,
											valueFieldID : 'rptId',
											isShowCheckBox : false,
											isMultiSelect : false
										},
										validate : {
											required : false
										}
									}, */
									{
										display : "机构",
										name : "org",
										newline : false,
										labelWidth : 48,
										width : 220,
										space : 15,
										type : "select",
										options : {
											onBeforeOpen : function() {
												BIONE.commonOpenDialog(
																"机构树",
																"chooseOrg",
																400,
																$(window)
																		.height() - 200,
																"${ctx}/rpt/frs/rptfill/chooseOrg?&orgType="
																		+ base_OrgType
																		+ "&rptId="
																		+ base_InitRptId
																		+ "&dataDate="
																		+ base_DataDate,
																null);
												return false;
											}
										},
										validate : {
											required : false
										}
									} ]
						});

		$("#date").val(base_DataDate);
	 
		jQuery.metadata.setType("attr", "validate");
		BIONE.validate('#formsearch');
	};

	//改变机构值，应该是子页面会用到
	function changeOrgValue(orgNo, orgNm) {
		$("#org").val(orgNo);
		$("#orgBox").val(orgNm);
	}

	//初始化查询按钮
	function initSearchButtons(form, btnContainer) {
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
					if ($("#formsearch").valid()) {
						var chooseOrgNo = $("#org").val();
						var chooseOrgNm = $("#orgBox").val();
						if (chooseOrgNo) {
							base_OrgNo = chooseOrgNo;
							base_OrgNm = chooseOrgNm;
						}
						var chooseRptId = liger.get('rptCombox').getValue();
						if (chooseRptId) {
							base_InitRptId = chooseRptId;
						}
						var chooseDataDate = $("#date").val();
						if (chooseDataDate) {
							base_DataDate = chooseDataDate;
						}
						$("#spread").html("");
						initDesign();
						showTemplateId();
					}
				}
			});
/* 			BIONE.createButton({
				appendTo : btnContainer,
				text : '',
				icon : 'refresh2',
				width : '40px',
				click : function() {
					if(base_layout_upDown){
						$("#checkResult").hide();
						base_layout_upDown = false;
					}else{
						$("#checkResultLeft").hide();
						base_layout_upDown = true;
					}
					$("#togglebtnIcon").removeClass("togglebtn-down");
					View.spreadDom.height($(window).height()- $("#mainsearch").height() - $("#top").height() -25);
					View.spreadDom.width($(window).width()*0.994);
					if(View){
						View.resize(View.spread);
					} 
				}
			}); */
			$("#searchbtn").width($("#center").width() - 990);
		}
	};

	//初始化功能按钮
	function initButtons() {
		$.ajax({
			cache : false,
			async : false,
			url : "${ctx}/rpt/frs/rptfill/createButton",
			dataType : 'json',
			data : {
				taskType : base_OrgType,
				flag : "1"
			},
			type : "post",
			success : function(result) {
				if (result && result.length > 0) {
					for ( var i in result) {
						if (tmp.buttonArray[result[i]]) {
							BIONE.createButton(tmp.buttonArray[result[i]]);
						}
					}
				}
			},
			error : function() {
				BIONE.tip("获取功能按钮异常，请联系系统管理员");
			}
		});
	};

	//初始化下载页面
	function initExport() {
		base_Download = $('<iframe id="download"  style="display: none;"/>');
		$('body').append(base_Download);
	};

	//根据报表编号找报表模板编号
	function showTemplateId() {
		//先获取模板Id
		$.ajax({
			cache : false,
			async : false,
			url : "${ctx}/rpt/frs/rptfill/showTemplateId",
			dataType : 'text',
			data : {
				rptId : base_InitRptId
			},
			type : "post",
			success : function(result) {
				if (result) {
					base_TmpId = result;
				} else {
					BIONE.tip("没有对应的报表模板");
				}
			},
			error : function() {
				BIONE.tip("模板数据加载异常，请联系系统管理员");
			}
		});
	}

	//报表计算(只计算表间取数)
	tmp.rptCalculation = function() {
		 $.ligerDialog.confirm('报表计算会进行表间取数的计算, 确定要执行此操作？', '表间计算', function(yes) {
				if (yes) {
					$.ajax({
						async : true,
						url : "${ctx}/rpt/frs/rptfill/rptCalculation",
						dataType : 'json',
						type : 'POST',
						data : {
							rptId : base_InitRptId,
			 				orgNo : base_OrgNo,
							dataDate : base_DataDate,
							taskInsId : base_TaskInsId,
							operType : base_OperType,
							rptTmpId : base_TmpId
						}, 
						beforeSend : function() {
							BIONE.showLoading("报表计算中，请稍等...");
						},
						complete: function(){
							BIONE.hideLoading();
						},
						success: function(result){
							if(result.error){
								BIONE.tip(result.error);
							}else{
								View.ajaxJson();
								BIONE.tip("表间计算完成");
							}
						},
						error: function(){
							BIONE.tip("报表计算失败，请联系系统管理员");
						}
					});
				}
		 });
	};
	
	//查看初始值
	tmp.viewInit = function() {
		base_QueryInit = true;
	    var url = "/report/frame/tmp/view/getViewInfo?QueryInit="+base_QueryInit+"&d="+new Date().getTime();
	    View.ajaxJson(null,null,null,url);
	};
	
	//数据导出 
	//queryInit:true 导出初始值
	//queryInit:false 导出实际值
	tmp.download = function(queryInit){
		base_QueryInit = queryInit;
		var argsArr = [];
		var searchArgs = [];
		var searchArg = {'DimNo':'ORG','Op':'=','Value': base_OrgNo};
		searchArgs.push(searchArg);
		var args = {'orgNm': base_OrgNm,'rptId': base_InitRptId,'dataDate': base_DataDate,'searchArgs':JSON2.stringify(searchArgs)};
		argsArr.push(args);
		BIONE.ajax({
			async : false,
			url : "${ctx}/rpt/frs/rptfill/downloadList?QueryInit="+base_QueryInit+"&json="+encodeURI(encodeURI(JSON2.stringify(argsArr)))+"&d="+ new Date().getTime(),
			dataType : 'json',
			type : 'get',
			loading : '正在生成下载文件，请稍等...'
		}, function(result) {
			if(result.result){
				if("OK" == result.result){
					if(result.zipFilePath && result.folderinfoPath){
						var src = '';
						src = "${ctx}/rpt/frs/rptfill/downFile?zipFilePath="+encodeURI(encodeURI(result.zipFilePath))+"&folderinfoPath=" + encodeURI(encodeURI(result.folderinfoPath)) + "&d="+ new Date().getTime();
						base_Download.attr('src', src);
					}
				}else{
					BIONE.tip(result.msg);
				}
			}else{
				 BIONE.tip("文件导出失败，请联系系统管理员");
			}
		});
	};
	
	//数据重置
	tmp.reset = function() {
		View.reset();
	};

	//数据导入
	tmp.importData = function() {
		BIONE.commonOpenDialog('导入数据文件', 'uploadWin', 600, 330,
				"${ctx}/rpt/frs/rptfill/showTab?path=rpt-todowork-uploadfile&dataDate="
						+ base_DataDate + "&orgNo=" + base_OrgNo + "&rptId=" + base_InitRptId
						+ "&taskInsId=" + base_TaskInsId + "&type=" + base_OrgType
						+ "&flag=ONE&entry=EXCEL&importFileType=xls,xlsx&uuid="
						+ new Date().getTime());
	};
	
	//机构汇总功能入口函数
	tmp.sumData = function() {
		 $.ligerDialog.confirm('确定要进行机构汇总？', '机构汇总', function(yes) {
			if (yes) {
				tmp.sumDataInfo();
			}
		 });
	};
	
	 
	
	//检查是否包含未提交子机构
	function submitJudge(btnFlag ,fun){
		var isContainOrg = true;
		BIONE.showLoading("查询是否有未提交子机构，请稍等...");
		var params = [];
		var param = {'taskIns' : base_TaskInsId, 'orgNo' : base_OrgNo, 'rptId' : base_InitRptId, 'dataDate' : base_DataDate, 'type' : base_OrgType};
		params.push(param);
		$.ajax({
			cache : false,
			async : false,
			url : "${ctx}/rpt/frs/rptfill/submitJudge",
			dataType : 'json',
			type : "post",
			data : {
				params : JSON2.stringify(params),
				sts : "0"
			},
			success : function(result){
				BIONE.hideLoading();
				if("ERROR" == result.result){
					return false;
				}else if(("YES" == result.result) && result.ins){
					var height = $(window).height() - 30;
					var width = $(window).width() - 80;
					var taskInsIds = [];
					taskInsIds.push(base_TaskInsId);
					BIONE.commonOpenDialog("未提交子机构任务实例", "taskInsChildWin", width, height, "${ctx}/rpt/frs/rptfill/showTab?path=rptfill-org-child-ins&sts=0&params="
							+ JSON2.stringify(params) + "&taskInsIds=" + taskInsIds.join(",") + "&orgNo=" + base_OrgNo + "&rptId="
							+ base_InitRptId + "&dataDate=" + base_DataDate + "&taskId=" + base_TaskId + "&btnFlag=" + btnFlag+ "&tmpId=" + base_TmpId, null);
					return false;
				}else if("NO" == result.result){
					if (typeof (fun) == "function") {
						fun();
					}
				}
			},
			error:function(){
				BIONE.hideLoading();
				BIONE.tip("获取未提交子任务异常，请联系系统管理员");
			}
		});
		return isContainOrg;
	}
	
	 
	
	//判断数据是否修改
	tmp.isUpdateData = function(){
		var isLeafNode = tmp.isLeafNode();
		var changeInfo = View.generateChangeInfo(isLeafNode);
		if(!changeInfo){
			return false;
		}
		var cells = changeInfo.cells
		if(cells&&cells.length>0){
			for(var i in cells){
				var newValue = cells[i].newValue;
				var oldValue = cells[i].oldValue;
				newValue = View._changValue(cells[i].unit, newValue);
				if(newValue != oldValue) {
					return true;
				}
			}
		}
	};
	
	//判断当前查询的机构是否是叶子节点数据
	tmp.isLeafNode = function(){
		var isLeafNode = false;
		$.ajax({
			cache : false,
			async : false,
			url : "${ctx}/rpt/frs/rptfill/isLeafNode",
			dataType : 'json',
			data : {
				orgNo : base_OrgNo,
				orgType : base_OrgType
			},
			type : "post",
			success : function(result){
				if(result){
					isLeafNode = result;
				}
			}
		}); 
		return isLeafNode;
	}

	//查询修改记录
	tmp.openHisview = function() {
		BIONE.commonOpenLargeDialog(
						'查看修改记录',
						'hisViewWin',
						'${ctx}/rpt/frs/rptfill/showTab?path=rpt-todowork-his-detail&taskInstanceId='
								+ base_TaskInsId
								+ '&cellNo='
								+ window.posi
								+ "&templateId=" + base_TmpId);
	};
	
	//填报说明
	tmp.descbtn = function() {
		BIONE.commonOpenLargeDialog("填报说明","rptViewWin","${ctx}/rpt/frame/rptmgr/info/reportFrs?rptId=" + base_InitRptId + "&show=1&taskInsId=" + base_TaskInsId);	
	};
	
	//备注说明
	tmp.busiremark = function(){
		var selIdxs = View.getSelectionIdxs();
		if(!selIdxs || selIdxs.length <= 0){
			BIONE.tip("非数据单元格无法填写业务备注");
			return;
		}
		var cellNo = selIdxs[0].cellNo;
		BIONE.commonOpenDialog("业务备注","remarkWin",450,300,"${ctx}/rpt/frs/rptfill/showTab?path=rpt-todowork-remark-info&rptId="
				+ base_InitRptId + "&type=" + base_OrgType + "&taskInsId=" + base_TaskInsId + "&orgNo="+ base_OrgNo +"&dataDate="+ base_DataDate +"&cellNo="+cellNo);	
	}
	
	//保存功能入口
	tmp.promptAndSave = function(flag) {
		if(tmp.isUpdateData()){
			$.ligerDialog.confirm("数据已修改是否保存", "提示", function(yes) {
				if(yes){
					tmp.save(flag);
				}
			});
		}else{
			BIONE.tip("数据未修改，无需保存");
		}	
	}
	
	//填报数据保存
	tmp.save = function(flag,func,message,param) {//flag true-正常保存，false-检验、汇总、计算、提交之前保存 fuFlag 功能标识  param 参数 （根据功能标识不同而不同）
		var isLeafNode = tmp.isLeafNode();
		var changeInfo = View.generateChangeInfo(isLeafNode);
		if(changeInfo.isValid == false){
			BIONE.tip("填报数据有误，无法保存");
			return;
		}
		var formulaCellInfo = View.getFormulaCellInfo();
			$.ajax({
				async : true,
				url : "${ctx}/rpt/frs/rptfill/saveData",
				dataType : 'json',
				type : 'post',
				data : {
					cells : JSON2.stringify(changeInfo.cells),
					rptId : base_InitRptId,
 					orgNo : base_OrgNo,
					dataDate : base_DataDate,
					taskInsId : base_TaskInsId,
					formulaCellInfo : JSON2.stringify(formulaCellInfo),
					searchArgs : changeInfo.searchArgs
				}, 
				beforeSend : function() {
					BIONE.showLoading(message);
				},
				complete: function(){
					BIONE.hideLoading();
				},
				success: function(result) {
					View.refreshCellVals(result.res);
					View.refreshOldCellVals(result.oldRes);
					updateTask(flag);
					if (typeof (func) == "function") {
						func(param);
					}
					BIONE.tip("数据保存成功");
				},
				error:function(){
					BIONE.tip("数据保存异常，请联系系统管理员");
				}
			});
	};
	
	//更新任务状态未已修改
	function updateTask(flag){
		$.ajax({
			cache : false,
			async : true,
			url : "${ctx}/rpt/frs/rptfill/updateTask?taskInsId=" + base_TaskInsId,
			dataType : 'json',
			type : "get",
			success : function(result){
				if("OK"== result.result){
					if(flag){
						updateColor();
					}
				}
				if("ERROR" == result.result){
					BIONE.tip("没有需要更新的任务");
				}
			},
			error:function(){
				BIONE.tip("更新任务状态异常，请联系系统管理员");
			}
		});
	}
	
	//更新报表单元格颜色
	function updateColor(){
		$.ajax({
			cache : false,
			async : true,
			url : "${ctx}/rpt/frs/rptfill/createColor",
			dataType : 'json',
			data : {
				dataDate : base_DataDate, 
				orgNo : base_OrgNo,
				tmpId : base_TmpId
			},
			type : "post",
			success : function(color){
				View.ajaxJson(JSON.stringify(color));
			}
		});
	}
	
	//返回
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
	
	//提交功能入口函数
	tmp.submit = function() {
		if(tmp.isUpdateData()){//修改
			$.ligerDialog.confirm("提交前需要保存数据，是否继续？", function(yes) {
				if(yes){
					tmp.save(false,tmp.submitInfo,"正在提交，请稍候...");
				}
			});
		}else{
			tmp.submitInfo();
		}
	};
	
	//结束填报
	tmp.finish = function() {
		if(tmp.isUpdateData()){//修改
			$.ligerDialog.confirm("结束填报前需要保存数据，并清除校验结果，是否继续？", function(yes) {
				if(yes){
					tmp.save(false,tmp.finishInfo,"正在提交，请稍候...");
				}
			});
		}else{
			tmp.finishInfo();
		}
	};
	
	//提交
	tmp.submitInfo =function(){
		//先检查当前任务检验状态
		var rsStsObj = getRsSts();
		if(rsStsObj.failFlag){
			$.ligerDialog.confirm("该记录校验未通过或者未校验，是否继续？", function(yes) {
				if(yes){
					//再检查是否有未提交子机构
					var isContainOrg  = submitJudge('01', implementSubmit);
				}
			});
		}
	}
	
	//执行提交
	function implementSubmit(){
		var taskInsIds = [];
		taskInsIds.push(base_TaskInsId);
		//进行提交
		$.ajax({
			cache : false,
			async : true,
			url : "${ctx}/rpt/frs/rptfill/submitTaskBatch",
			dataType : 'json',
			type : "get",
			data : {
				ignore : 'Y',
				taskFillOperType : base_OperType,
				taskInsIds : taskInsIds.join(","),
				taskId : base_TaskId,
				rptOperType : base_OperType,
				org : base_OrgNo
			},
			beforeSend : function() {
				BIONE.showLoading("正在提交，请稍候...");
			},
			complete: function(){
				BIONE.hideLoading();
			},
			success : function(){
				if(null !=parent.frames.findIframeByTitle("报表填报")[0].contentWindow.frames.onetab.contentWindow.frames.tsktab){
					if(parent.frames.findIframeByTitle("报表填报")[0].contentWindow.frames.onetab.contentWindow.frames.tsktab){
						if(parent.frames.findIframeByTitle("报表填报")[0].contentWindow.frames.onetab.contentWindow.frames.tsktab.contentWindow.tmp){
							parent.frames.findIframeByTitle("报表填报")[0].contentWindow.frames.onetab.contentWindow.frames.tsktab.contentWindow.tmp.reAsyncChildNodes("left");
							parent.frames.findIframeByTitle("报表填报")[0].contentWindow.frames.onetab.contentWindow.frames.tsktab.contentWindow.tmp.reAsyncChildNodes("right");
						}
					}else{
						parent.frames.findIframeByTitle("报表填报")[0].contentWindow.frames.onetab.contentWindow.frames.tsktab.contentWindow.tmp.reAsyncChildNodes("left");
						parent.frames.findIframeByTitle("报表填报")[0].contentWindow.frames.onetab.contentWindow.frames.tsktab.contentWindow.tmp.reAsyncChildNodes("right");
					}
				}
				//提交成功刷新填报列表页面
				if(null!=parent.frames.findIframeByTitle("报表填报")[0].contentWindow.frames.onetab.contentWindow.frames.listtab){
					if(parent.frames.findIframeByTitle("报表填报")[0].contentWindow.frames.onetab.contentWindow.frames.listtab){
						if(parent.frames.findIframeByTitle("报表填报")[0].contentWindow.frames.onetab.contentWindow.frames.listtab.contentWindow.grid)
							parent.frames.findIframeByTitle("报表填报")[0].contentWindow.frames.onetab.contentWindow.frames.listtab.contentWindow.grid.loadData();
					}else{
						if(wparent.frames.findIframeByTitle("报表填报")[0].contentWindow.frames.onetab.contentWindow.frames.listtab.contentWindow.grid)
							parent.frames.findIframeByTitle("报表填报")[0].contentWindow.frames.onetab.contentWindow.frames.listtab.contentWindow.grid.loadData();
					}
				}
				parent.BIONE.tip("提交成功");
				BIONE.closeDialog("taskFillWin");
			},
			error:function(){
				BIONE.tip("提交失败，请联系系统管理员");
			}
		});
	}
	
	//检验当前任务状态
	function getRsSts(checkType){
		var returnObj = {};
		returnObj.failFlag = true;
		$.ajax({
			cache : false,
			async : false,
			url : "${ctx}/rpt/frs/rptfill/getRsSts",
			dataType : 'json',
			data : {
				rptId : base_InitRptId, 
				dataDate : base_DataDate, 
				orgNo : base_OrgNo ,
				taskInsId : base_TaskInsId
			},
			type : "post",
			beforeSend : function() {
				BIONE.showLoading("检查是否正在校验，请稍等...");
			},
			complete: function(){
				BIONE.hideLoading();
			},
			success : function(result){
				if(result && result.taskInstanceId){
					var logicRs = returnObj.logicRs = result.logicRs;
					var sumpartRs = returnObj.sumpartRs = result.sumpartRs;
					var warnRs = returnObj.warnRs = result.warnRs;
					var zeroRs = returnObj.zeroRs = result.zeroRs;
					if((logicRs == '01' || logicRs == '02') || (sumpartRs == '01' || sumpartRs == "02") || (warnRs == '01' || warnRs == '02') || (zeroRs == '01' || zeroRs == '02')){
						BIONE.tip("该记录正在校验，请等候");
						returnObj.failFlag = false;//有正在校验记录，不能提交
					}
					if("sumpart" == checkType){//如果是总分检验，还得校验下级是否有机构
						var sumCheckRs = result.sumCheckSts;
						if(!sumCheckRs){
							BIONE.tip("该记录无下级机构分发，无需总分校验");
							returnObj.failFlag = false;
						}
					}
					if(!checkType){//判断是不是提交或者结束填报操作
						if((logicRs == null || logicRs == '04' || logicRs == '06') || (sumpartRs == null || sumpartRs == '04' || sumpartRs == "06") || (warnRs == null || warnRs == '04' || warnRs == '06' )){
							$.ligerDialog.confirm("该记录校验未通过或者未校验，是否继续？", function(yes) {
								if(yes){
									returnObj.failFlag = true;//可以正常提交
								}else{
									returnObj.failFlag = false;//不能提交
								}
							});
						}
					}
				}
			},
			error:function(){
				BIONE.tip("检查失败，请联系系统管理员");
			}
		});
		return returnObj;
	}
	
	//结束填报
	tmp.finishInfo = function(){
		//先检查当前任务检验状态
		var rsStsObj = getRsSts();
		if(rsStsObj.failFlag){
			$.ligerDialog.confirm("该记录校验未通过或者未校验，是否继续？", function(yes) {
				if(yes){
					//再检查是否有未提交子机构
					var isContainOrg  = submitJudge('01');
					if(isContainOrg){
						var taskInsIds = [];
						taskInsIds.push(base_TaskInsId);
						//结束填报
						$.ajax({
							cache : false,
							async : true,
							url : "${ctx}/rpt/frs/rptfill/finishTaskBatch?taskInsIds=" + taskInsIds.join(",")+"&d="+new Date().getTime(),
							dataType : 'json',
							type : "get",
							beforeSend : function() {
								BIONE.showLoading("正在结束填报，请稍等...");
							},
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
								BIONE.tip("结束填报异常，请联系系统管理员");
							}
						});
					}
				}
			});
		}
	}
	
	//单个校验功能入口
	tmp.checkSingle = function(checkType) {
		if(tmp.isUpdateData()){//修改
			$.ligerDialog.confirm("执行校验会自动保存数据，是否继续？", function(yes) {
				if (yes) {
					tmp.save(false, tmp.checkSingleInfo(checkType, false), "正在进行校验，请稍候...", checkType);
				}
			});
		}else{
			tmp.checkSingleInfo(checkType, false);
		}
	};
	
	//进行单个校验
	tmp.checkSingleInfo = function(checkType, isBatchCheck){
		var rsStsObj = getRsSts(checkType);
		var taskInsIds = [];
		taskInsIds.push(base_TaskInsId);
		//进行校验
		var objArr = [];
		var obj = {"rptId" : rptId, "orgNo" : base_OrgNo, "dataDate" : base_DataDate, "templateId" : base_TmpId};
		var group = {"DataDate" : base_DataDate, "OrgNo" : base_OrgNo};
		var checkName = "";
		if("logic" == checkType){
			group.LogicCheckRptTmpId = [base_TmpId];
			obj.logicRs = rsStsObj.logicRs;
			checkName = '逻辑校验';
		}
		if("sumpart" == checkType){
			group.SumCheckRptTmpId = [base_TmpId];
			obj.sumpartRs = rsStsObj.sumpartRs;
			checkName = '总分校验';
		}
		if("warn" == checkType){
			group.WarnCheckRptTmpId = [base_TmpId]; 
			obj.warnRs = rsStsObj.warnRs;
			checkName = '预警校验';
		}
		if("zero" == checkType){
			group.RealTimeZeroCheckRptTmpId = [base_TmpId];
			obj.zeroRs = rsStsObj.zeroRs;
			checkName = '零值校验';
		}
		objArr.push(obj);
		$.ajax({
			cache : false,
			async : true,
			url : "${ctx}/rpt/frs/rptfill/judgeAndSaveCheckStsSync",
			dataType : 'json',
			type : 'POST',
			data : {
				objArr : JSON2.stringify(objArr),
				objArrParms : JSON2.stringify(group),
				dataDate : base_DataDate,
				tmpId : base_TmpId,
				orgNo : base_OrgNo,
				taskId : base_TaskId,
				taskType : base_OrgType,
				taskInsId : base_TaskInsId,
				checkType : checkType,
				isBatchCheck : isBatchCheck
			},
			beforeSend : function() {
				BIONE.showLoading("正在进行校验，请稍等...");
			},
			complete: function(){
				BIONE.hideLoading();
			},
			success: function(result) {
				if(result.result){
					if("OK" == result.result){
						var color = result.color;
						View.ajaxJson(color);
						// 根据校验状态返回提示
						if(result.taskCheckSts == "01"){
							BIONE.tip(checkName + "校验等待运行中");
						}else if(result.taskCheckSts == "02"){
							BIONE.tip(checkName + "校验运行中");
						}else if(result.taskCheckSts == "03"){
							BIONE.tip(checkName + "校验成功");
						}else if(result.taskCheckSts == "04"){
							BIONE.tip(checkName + "校验失败");
						}else if(result.taskCheckSts == "05"){
							BIONE.tip(checkName + "校验通过");
						}else if(result.taskCheckSts == "06"){
							BIONE.tip(checkName + "校验有未通过项,请检查");
						}else{
							BIONE.tip(checkName + "校验完成");
						}
					}else{
						BIONE.tip(result.result);
					}
				}else{
					 BIONE.tip("校验异常，请联系系统管理员");
				}
			},
			error: function(){
				BIONE.tip("校验异常，请联系系统管理员");
			}
		});
	}
	
	//校验结果查看
	tmp.checkResult = function(checkType) {  
		BIONE.commonOpenLargeDialog('校验结果查看','checkResultWin','${ctx}/rpt/frs/rptfill/showTab?path=rptfill-todowork-check-result&rptId='
				+ base_InitRptId +'&templateId='+ base_TmpId +'&dataDate='+ base_DataDate
				+'&orgNo='+ base_OrgNo +'&type='+ base_OrgType +'&taskId='+ base_TaskId 
				+'&checkType='+ checkType + "&taskInsId=" + base_TaskInsId);
	};
	
	//批量校验功能入口
	tmp.check = function(){
		if(tmp.isUpdateData()){//修改
			$.ligerDialog.confirm("执行校验会自动保存数据，是否继续？", function(yes) {
				if (yes) {
					tmp.save(false, batchCheck, "正在进行校验，请稍候...");
				}
			});
		}else{
			batchCheck();
		}
	}
	
	//进行批量校验
	function batchCheck(){
		tmp.checkSingleInfo("zero", true);//零值校验
		tmp.checkSingleInfo("logic", true);//逻辑校验
		tmp.checkSingleInfo("warn", true);//警戒值校验
		tmp.checkSingleInfo("sumpart", true);//总分校验
	}
	
	//关闭页面
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
	};
	
	//查找指标校验不通过的记录
	function searchCheakNoPass(rptIdxNo){
		$.ajax({
			cache : false,
			async : true,
			url : "${ctx}/rpt/frs/rptfill/searchCheakNoPass",
			dataType : 'json',
			type : 'POST',
			data : {
				rptIdxNo : rptIdxNo,
				dataDate : base_DataDate,
				tmpId : base_TmpId,
				orgNo : base_OrgNo,
			},
			beforeSend : function() {
				BIONE.showLoading("正在查询那些校验没有通过，请稍等...");
			},
			complete: function(){
				BIONE.hideLoading();
			},
			success: function(result) {
				if(result){
					for (var key in result){
						if("noPass" == result[key]){
							$("li[tabid="+ key +"]").css("border", "2px solid #ff0000").css("display", "block");
						}else{
							$("li[tabid="+ key +"]").css("border", "1px solid #3c8dbc");
							if("logicTab" != key){
								$("li[tabid="+ key +"]").css("display", "none");
							}
						}
					}
					tabObj.selectTabItem("explainTab");//默认选择口径说明
				}
			},
			error: function(){
				BIONE.tip("查询异常，请联系系统管理员");
			}
		});
	}
	
	//初始化校验结果tab页
	function initCheckResult(){
		tabObj = $("#checkResult").ligerTab({
			changeHeightOnResize : true,
			dragToMove: false,
			contextmenu : false,
			onAfterSelectTabItem : function(tabId) {
				if(base_selectIdxNo){
					var checkResultParam = 'dataDate='+ base_DataDate +'&orgNo='+ base_OrgNo +'&rptIndexNo='+ base_selectIdxNo +'&rptTmpId='+ base_TmpId +'&cellNo='+ base_selectCellNo +'&taskInstanceId='+ base_TaskInsId +'&rptId='+ base_InitRptId +'&type='+ base_OrgType;
					var tabIdxNo = $("#"+tabId).attr('data-idxNo');
					if(base_selectIdxNo == tabIdxNo){
						return;
					}
					$("#"+tabId).attr('data-idxNo', base_selectIdxNo);
					if("logicTab" == tabId){
						$("#"+tabId).attr('src', '${ctx}/rpt/frs/rptfill/logicResult?' + checkResultParam);
					}else if("sumpartTab" == tabId){
						$("#"+tabId).attr('src', '${ctx}/rpt/frs/rptfill/sumpartResult?' + checkResultParam);
					}else if("warnTab" == tabId){
						$("#"+tabId).attr('src', '${ctx}/rpt/frs/rptfill/warnResult?' + checkResultParam);
					}else if("zeroTab" == tabId){
						$("#"+tabId).attr('src', '${ctx}/rpt/frs/rptfill/zeroResult?' + checkResultParam);
					}else if("detailTab" == tabId){
						$("#"+tabId).attr('src', '${ctx}/rpt/frs/rptfill/cellDetail?' + checkResultParam);
					}else if("remarkTab" == tabId){
						$("#"+tabId).attr('src', '${ctx}/rpt/frs/rptfill/cellRemark?' + checkResultParam);
					}else if("explainTab" == tabId){
						$("#"+tabId).attr('src', '${ctx}/rpt/frs/rptfill/cellExplain?' + checkResultParam);
					}else if("chartTab" == tabId){
						$("#"+tabId).attr('src', '${ctx}/rpt/frs/rptfill/chartAna?' + checkResultParam);
					}
				}else{
					return false;
				}
			}
		});
		var iframeHeight = $("#checkResult").height() - 26;
		tabObj.addTabItem({
			tabid : "logicTab",
			text : "逻辑校验结果",
			showClose : false,
			content : '<iframe id="logicTab" style="height:'+ iframeHeight +'px" src="" data-idxNo= ""></iframe>'
		});
		tabObj.addTabItem({
			tabid : "sumpartTab",
			text : "总分校验结果",
			showClose : false,
			content : '<iframe id="sumpartTab" style="height:'+ iframeHeight +'px" src="" data-idxNo= ""></iframe>'
		});
		tabObj.addTabItem({
			tabid : "warnTab",
			text : "预警校验结果",
			showClose : false,
			content : '<iframe id="warnTab" style="height:'+ iframeHeight +'px" src="" data-idxNo= ""></iframe>'
		});
		tabObj.addTabItem({
			tabid : "zeroTab",
			text : "零值校验结果",
			showClose : false,
			content : '<iframe id="zeroTab" style="height:'+ iframeHeight +'px" src="" data-idxNo= ""></iframe>'
		});
		tabObj.addTabItem({
			tabid : "detailTab",
			text : "修改记录",
			showClose : false,
			content : '<iframe id="detailTab" style="height:'+ iframeHeight +'px" src="" data-idxNo= ""></iframe>'
		});
		tabObj.addTabItem({
			tabid : "remarkTab",
			text : "备注",
			showClose : false,
			content : '<iframe id="remarkTab" style="height:'+ iframeHeight +'px" src="" data-idxNo= ""></iframe>'
		});
/* 		tabObj.addTabItem({
			tabid : "chartTab",
			text : "数据分析",
			showClose : false,
			content : '<iframe id="chartTab" style="height:'+ iframeHeight +'px" src="" data-idxNo= ""></iframe>'
		}); */
 		tabObj.addTabItem({
			tabid : "explainTab",
			text : "口径说明",
			showClose : false,
			content : '<iframe id="explainTab" style="height:'+ iframeHeight +'px" src="" data-idxNo= ""></iframe>'
		});
	} 
	
	//功能按钮集合
	tmp.buttonArray = {
		    "INITBTN" : { text : '表间计算', width : '50px', appendTo : '#dataButton', operNo:'initBtn', click : tmp.rptCalculation, isCountWidth : false},
			
			"VIEWINITBTN" : { text : '初始值', width : '40px', appendTo : '#dataButton', operNo:'viewInitBtn', click : tmp.viewInit, isCountWidth : false},
 			       "RESETBTN" : { text : '数据重置', width : '50px', appendTo : '#dataButton', operNo:'resetBtn', click : tmp.reset, isCountWidth : false},
		    "EXPORTBTN" : { text : '数据下载', width : '50px', appendTo : '#dataButton', operNo:'exportBtn', click : function(){tmp.download(false)}, isCountWidth : false},
	  		"IMPORTBTN" : { text : '数据导入', width : '50px', appendTo : '#dataButton', operNo:'importBtn', click : tmp.importData, isCountWidth : false},
			       "CALCULATEBTN" : { text : '条线汇总', width : '50px', appendTo : '#dataButton', operNo:'calculateBtn', click : null, isCountWidth : false},
			"SUMBTN" : { text : '机构汇总', width : '50px', appendTo : '#dataButton', operNo:'sumBtn', click : tmp.sumData, isCountWidth : false},
			       "COMPAREBTN" : { text : '比上期', width : '50px', appendTo : '#dataButton', operNo:'compareBtn', click : null, isCountWidth : false},
			
 			"DESCBTN" : { text : '填报说明', width : '50px', appendTo : '#explainButton', operNo:'descBtn', click : function(){tmp.descbtn()}, isCountWidth : false},
			"REMARKBTN" : { text : '新增备注', width : '50px', appendTo : '#explainButton', operNo:'remarkBtn', click : function(){tmp.busiremark()}, isCountWidth : false},
			
			
			"VALIDBTN" : { text : '一键校验', width : '50px', appendTo : '#validButton', operNo:'validBtn', click : tmp.check, isCountWidth : false},
 			
			"SAVEBTN" : { text : '保存', width : '25px', appendTo : '#flowButton', operNo:'saveBtn', click : function(){tmp.promptAndSave(true)}, isCountWidth : false},
			"BACKBTN" : { text : '返回', width : '25px', appendTo : '#flowButton', operNo:'backBtn', click : tmp.back, isCountWidth : false},
			"HANDLEBTN" : { text : '提交', width : '25px', appendTo : '#flowButton', operNo:'handleBtn', click : tmp.submit, isCountWidth : false},
					"FINISHBTN" : { text : '结束填报', width : '50px', appendTo : '#flowButton', operNo:'finishBtn', click : tmp.finish, isCountWidth : false},
			
			"CHECKRESULTBTN" : {text : '结果', width : '25px', appendTo : '#validButton', operNo:'checkResultBtn', click : function(){tmp.checkResult(null)}, isCountWidth : false},
					"LOGICRESULTBTN" : { text : '逻辑校验', width : '50px', appendTo : '#resultButton', id:'logicResultBtn', operNo:'logicResultBtn', click : function(){tmp.checkResult("logic")}, isCountWidth : false},
					"WARNRESULTBTN" : { text : '预警校验', width : '50px', appendTo : '#resultButton', id:'warnResultBtn', operNo:'warnResultBtn', click : function(){tmp.checkResult("warn")}, isCountWidth : false},
					"SUMPARTRESULTBTN" : { text : '总分校验', width : '50px', appendTo : '#resultButton', id:'sumpartResultBtn', operNo:'sumpartResultBtn', click : function(){tmp.checkResult("sumpart")}, isCountWidth : false},
					"ZERORESULTBTN" : { text : '零值校验', width : '50px', appendTo : '#resultButton', id:'zeroResultBtn', operNo:'zeroResultBtn', click : function(){tmp.checkResult("zero")}, isCountWidth : false}
					
	};
</script>
</head>
<body>
</body>

</html>