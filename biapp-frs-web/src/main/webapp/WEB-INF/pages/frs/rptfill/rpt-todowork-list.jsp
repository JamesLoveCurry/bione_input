<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<meta name="decorator" content="/template/template1P_BS.jsp">
<link rel="stylesheet" type="text/css" href="${ctx}/css/classics/jqueryui/smoothness/jquery-ui.min.css" />
<!-- 目前不需要在填报列表处进行批量excel公式计算，所以先注掉 -->
<%-- <script type="text/javascript" src="${ctx}/js/knockout/knockout-3.1.0.js"></script>
<script type="text/javascript" src="${ctx}/js/spreadjs/11.0.0/gc.spread.sheets.all.11.0.0.min.js"></script>
<script type="text/javascript" src="${ctx}/js/spreadjs/11.0.0/gc.spread.sheets.resources.zh.11.0.0.min.js"></script>
<script type="text/javascript" src="${ctx}/plugin/js/show/views/rptview_tool.js"></script> --%>
<script type="text/javascript">
	var ctx = "${ctx}";
	var base_SumType = "01";//机构汇总方式
</script>
<script>
	var notUptCells = [];
	(function($) {
		//审批状态渲染
		CollateStsRender = function(rowdata) {
			if (rowdata.collateSts == '0') {
				return "未审批";
			} else if (rowdata.collateSts == '1') {
				return "同意";
			} else if (rowdata.collateSts == '2') {
				return "不同意";
			} else {
				return rowdata.collateSts;
			}
		};
		// 任务状态渲染(加入"结束填报")
		HandStsRender = function(rowdata) {
			if (rowdata.sts == '0') {
				return "未提交";
			} else if (rowdata.sts == '1') {
				return "已提交";
			} else if (rowdata.sts == '2') {
				return "已复核";
			} else if (rowdata.sts == '3') {
				return "已审核";
			} else {
				return rowdata.sts;
			}
		};
		 //搜索框报表状态值
		 var finishStsData = [];
		 var rptSts;
		//获取报表状态字典值
		 $.ajax({
				cache : false,
				async : false,
				url : '${ctx}/rpt/frs/rptfill/getRptSts.json',
				dataType : 'json',
				type : 'get',
				success : function(result) {
					if(result){
						rptSts = result;
						//result    {0: "未提交", 1: "待复核", 2: "待审核", 3: "已提交", 9: "已冻结"}
						for(var key in result){
							finishStsData.push({"text": result[key],"id":key});
						}
					}
					
				}
		});
		// 任务状态渲染(加入"结束填报")
		FinishStsRender = function(rowdata) {
			return(rptSts[rowdata.sts]);
		};
		//add by wusb at 20161202 是否总表
		IsMainRptRender = function(rowdata) {
			if (rowdata.isMainRpt == 'Y') {
				return "是";
			} else {
				return "否";
			}
		};
		//驳回理由链接
		RejectReasonRender = function(rowdata) {
			var rr = rowdata.rejectReason;
			if (rr != "" && rr != null && rr != "undefined") {
				var r3 = rr;
				if (rr.length > 12)
					r3 = rr.substring(0, 12) + "...";
				return "<a href='javascript:void(0)' class= 'link' onclick = 'onShowRejectReason( \""
						+ rowdata.taskInstanceId
						+ "\" )'>"
						+ r3 + "</a>";
			} else {
				return null;
			}
		};

		isCalculationRender = function(rowdata) {
			if (rowdata.isCalculation == 'Y') {
				return "是";
			} else {
				return "否";
			}
		};
		
		onShowRejectReason = function(taskInstanceId) {
			BIONE.commonOpenLargeDialog(
							"驳回理由",
							"showRejectReason",
							"${ctx}/rpt/frs/rptfill/showTab?path=rpt-todowork-list-reject-reason&taskInsId="
									+ taskInstanceId);
		};

		// 任务状态渲染
		UpdateStsRender = function(rowdata) {
		};
		CommonRender = function(isCheck, rs, taskType, type) {
			var result = "未校验";
			if (rs == '01') {
				return "准备校验";
			}
			if (rs == '02') {
				return "校验中";
			}
			if (rs == '03') {
				return "校验成功";
			}
			if (rs == '04') {
				return "校验失败";
			}
			if (rs == '05') {
				return "通过";
			}
			if (rs == '06') {
				return "未通过";
			}
			if (rs == '07') {
				return "无需校验";
			}
			return result;
		};
		// 总分校验校验状态渲染
		SumpartRsRender = function(rowdata) {
			// 1. 获取转码的结果
			var dicName = CommonRender(rowdata.isCheck,
					rowdata.sumpartRs, rowdata.taskType,
					'sumpart');

			// 2. 拼装 a 标签返回
			if (dicName == "未校验") {
				return dicName;
			} else {
				var aStr = "<a href='javascript:void(0)' class='link' onclick='onShowRptRsView(\""
						+ rowdata.taskObjId
						+ "\",\""
						+ rowdata.dataDate
						+ "\",\""
						+ rowdata.taskType
						+ "\",\""
						+ rowdata.taskInstanceId
						+ "\",\""
						+ rowdata.exeObjId
						+ "\",\""
						+ rowdata.taskId
						+ "\",\"sumpart\")' title='"
						+ dicName
						+ "'>" + dicName + "</a>";
				return aStr;
			}
		};
		// 逻辑校验校验状态渲染
		LogicRsRender = function(rowdata) {
			//明细报表逻辑校验状态转换，因为综合类报表可能有俩种校验（指标和明细）
			if("01" == rowdata.templateType || "03" == rowdata.templateType){
				//有一个失败，那就都失败
				if(rowdata.logicRs == "04" || rowdata.deLogicSts == "04"){
					rowdata.logicRs = "04";
				}else if(rowdata.logicRs == "06" || rowdata.deLogicSts == "06"){//有一个未通过，那就都未通过
					rowdata.logicRs = "06";
				}else{
					rowdata.logicRs = rowdata.logicRs == null ? rowdata.deLogicSts : rowdata.logicRs;
				}
			}
			var dicName = CommonRender(rowdata.isCheck,
					rowdata.logicRs, rowdata.taskType, 'logic');
			// 2. 拼装 a 标签返回
			if (dicName == "未校验") {
				return dicName;
			} else {
				var aStr = "<a href='javascript:void(0)' class='link' onclick='onShowRptRsView(\""
						+ rowdata.taskObjId
						+ "\",\""
						+ rowdata.dataDate
						+ "\",\""
						+ rowdata.taskType
						+ "\",\""
						+ rowdata.taskInstanceId
						+ "\",\""
						+ rowdata.exeObjId
						+ "\",\""
						+ rowdata.taskId
						+ "\",\"logic\")' title='"
						+ dicName
						+ "'>" + dicName + "</a>";
				return aStr;
			}
		};
		// 警戒值校验校验状态渲染
		WarnRsRender = function(rowdata) {
			var dicName = CommonRender(rowdata.isCheck,
					rowdata.warnRs, rowdata.taskType, 'warn');
			// 2. 拼装 a 标签返回
			if (dicName == "未校验") {
				return dicName;
			} else {
				var aStr = "<a href='javascript:void(0)' class='link' onclick='onShowRptRsView(\""
						+ rowdata.taskObjId
						+ "\",\""
						+ rowdata.dataDate
						+ "\",\""
						+ rowdata.taskType
						+ "\",\""
						+ rowdata.taskInstanceId
						+ "\",\""
						+ rowdata.exeObjId
						+ "\",\""
						+ rowdata.taskId
						+ "\",\"warn\")' title='"
						+ dicName
						+ "'>" + dicName + "</a>";
				return aStr;
			}
		};
		//零值校验校验状态渲染   modify by wusb at 20161118 
		ZeroRsRender = function(rowdata) {
			var dicName = CommonRender(rowdata.isCheck,
					rowdata.zeroRs, rowdata.taskType, 'zero');
			// 2. 拼装 a 标签返回
			if (dicName == "未校验") {
				return dicName;
			} else {
				var aStr = "<a href='javascript:void(0)' class='link' onclick='onShowRptRsView(\""
						+ rowdata.taskObjId
						+ "\",\""
						+ rowdata.dataDate
						+ "\",\""
						+ rowdata.taskType
						+ "\",\""
						+ rowdata.taskInstanceId
						+ "\",\""
						+ rowdata.exeObjId
						+ "\",\""
						+ rowdata.taskId
						+ "\",\"zero\")' title='"
						+ dicName
						+ "'>" + dicName + "</a>";
				return aStr;
			}
		};
		// 打开校验结果
		onShowRptRsView = function(rptId, dataDate, taskType,
				taskInstanceId, exeObjId, taskId, checkedType) {
			if (checkedType == "" || checkedType == null) {
				checkedType = "NO";
			}
			var url = '${ctx}/rpt/frs/rptfill/showCheckResult?rptId='
					+ rptId
					+ '&dataDate='
					+ dataDate
					+ '&orgNo='
					+ exeObjId
					+ '&type='
					+ taskType
					+'&taskId='
					+ taskId
					+ '&checkType='
					+ checkedType
					+ '&taskFillOperType=34&operType=03'
					+ '&taskInsId=' + taskInstanceId;
			BIONE.commonOpenDialog('校验结果查看',
					'checkResultWin','960','460', url, function() {
					});
		};

		var rptOperType = "${rptOperType}";
		if (rptOperType == "") {
			rptOperType = "${operType}";
		}
		//报表名称链接
		taskObjNmRender = function(rowdata) {
			var aa = rowdata.taskObjNm;
			var bb = aa;/* 
					if(aa.length>20)
						bb=aa.substring(0,20)+"..."; */
			return "<a href='javascript:void(0)' class='link' onclick='onShowRpt(\""
					+ rowdata.sts
					+ "\",\""
					+ rowdata.taskObjId
					+ "\",\""
					+ rowdata.taskObjNm
					+ "\",\""
					+ rowdata.dataDate
					+ "\",\""
					+ rowdata.exeObjId
					+ "\",\""
					+ rowdata.exeObjNm
					+ "\",\""
					+ rowdata.lineId
					+ "\",\""
					+ rowdata.zeroRs
					+ "\",\""
					+ rowdata.warnRs
					+ "\",\""
					+ rowdata.sumpartRs
					+ "\",\""
					+ rowdata.logicRs
					+ "\",\""
					+ rowdata.taskInstanceId
					+ "\",\""
					+ rowdata.taskType
					+ "\", \""
					+ rowdata.taskId
					+ "\", \""
					+ rowdata.templateType
					+ "\", \""
					+ rowdata.templateId
					+ "\", \""
					+ rowdata.fixedLength
					+ "\", \""
					+ rowdata.isPaging
					+ "\")' title='"
					+ aa
					+ "'>" + bb + "</a>";
		};
		//点击报表名称查看报表
		onShowRpt = function(sts, taskObjId, taskObjNm,
				dataDate, exeObjId, exeObjNm, lineId, logicRs,
				sumpartRs, warnRs, zeroRs, taskInstanceId,
				taskType, taskId, templateType, templateId, fixedLength, isPaging) {
			$
					.ajax({
						cache : false,
						async : true,
						url : ctx + "/rpt/frs/rptfill/getTmpId",
						dataType : 'text',
						data : {
							rptId : taskObjId,
							dataDate : dataDate,
							lineId : lineId,
							operType : rptOperType
						},
						type : "post",
						success : function(result) {
							if (result) {
								BIONE
										.ajax(
												{
													async : false,
													url : ctx
															+ "/rpt/frs/rptfill/createColor",
													dataType : 'text',
													type : 'POST',
													data : {
														rptId : taskObjId,
														orgNo : exeObjId,
														dataDate : dataDate,
														tmpId : result,
														isBatchCheck : true
													}
												},
												function(result) {
													var title = "报表信息";
													var height = $(parent.parent.parent.window).height();
													var width = $(parent.parent.parent.window).width() + 10;
													window.top.color = result;
													if ((sts == "0" || sts == "10")
															&& rptOperType == "03") {//未提交报表
														//modify by wusb at 20161118  待定。。。
														if(templateType && (templateType == "01" || templateType == "03")){
															height = height - 30;
														}
														var url = ctx
																+ "/rpt/frs/rptfill/showTab?path=rptfill-todowork-oper&type="
																+ taskType;
														window.parent.parent.parent.BIONE
																.commonOpenDialog(
																		title,
																		"taskFillWin",
																		width,
																		height,
																		url
																				+ "&dataDate="
																				+ dataDate
																				+ "&orgNo="
																				+ exeObjId
																				+ "&rptId="
																				+ taskObjId
																				+ "&logicRs="
																				+ logicRs
																				+ "&sumpartRs="
																				+ sumpartRs
																				+ "&warnRs="
																				+ warnRs
																				+ "&taskInsId="
																				+ taskInstanceId
																				+ "&lineId="
																				+ lineId
																				+ "&rptNm="
																				+ encodeURI(encodeURI(taskObjNm))
																				+ "&orgNm="
																				+ encodeURI(encodeURI(exeObjNm))
																				+ "&taskId="
																				+ taskId
																				+ "&rptOperType="
																				+ rptOperType
																				+ "&taskType="
																				+ taskType
																				+ "&templateType="
																				+ templateType
																				+ "&templateId="
																				+ templateId
																				+ "&fixedLength="
																				+ fixedLength
																				+ "&isPaging="
																				+ isPaging,
																		null);
													} else if (rptOperType == "03") {//已提交报表
														//modify by wusb at 20161118  待定。。。
														var url = ctx
																+ "/rpt/frs/rptfill/showTab?path=rptfill-todowork-view&type="
																+ taskType;
														window.parent.parent.parent.BIONE
																.commonOpenDialog(
																		title,
																		"taskViewWin",
																		width,
																		height,
																		url
																				+ "&dataDate="
																				+ dataDate
																				+ "&orgNo="
																				+ exeObjId
																				+ "&rptId="
																				+ taskObjId
																				+ "&logicRs="
																				+ logicRs
																				+ "&sumpartRs="
																				+ sumpartRs
																				+ "&warnRs="
																				+ warnRs
																				+ "&taskInsId="
																				+ taskInstanceId
																				+ "&lineId="
																				+ lineId
																				+ "&rptNm="
																				+ encodeURI(encodeURI(taskObjNm))
																				+ "&orgNm="
																				+ encodeURI(encodeURI(exeObjNm))
																				+ "&taskId="
																				+ taskId
																				+ "&rptOperType="
																				+ rptOperType
																				+ "&taskType="
																				+ taskType
																				+ "&templateType="
																				+ templateType	
																				+ "&templateId="
																				+ templateId
																				+ "&fixedLength="
																				+ fixedLength
																				+ "&isPaging="
																				+ isPaging,
																		null);
													} else {//未提交报表
														//modify by wusb at 20161118  待定。。。
														if(templateType && templateType == "01"){
															height = height - 30;
														}
														var url = ctx
																+ "/rpt/frs/rptfill/showTab?path=rptfill-todowork-view&type="
																+ taskType;
														window.parent.BIONE
																.commonOpenDialog(
																		title,
																		"taskViewWin",
																		width,
																		height,
																		url
																				+ "&dataDate="
																				+ dataDate
																				+ "&orgNo="
																				+ exeObjId
																				+ "&rptId="
																				+ taskObjId
																				+ "&logicRs="
																				+ logicRs
																				+ "&sumpartRs="
																				+ sumpartRs
																				+ "&warnRs="
																				+ warnRs
																				+ "&taskInsId="
																				+ taskInstanceId
																				+ "&lineId="
																				+ lineId
																				+ "&rptNm="
																				+ encodeURI(encodeURI(taskObjNm))
																				+ "&orgNm="
																				+ encodeURI(encodeURI(exeObjNm))
																				+ "&taskId="
																				+ taskId
																				+ "&rptOperType="
																				+ rptOperType
																				+ "&taskType="
																				+ taskType
																				+ "&templateType="
																				+ templateType	
																				+ "&templateId="
																				+ templateId
																				+ "&fixedLength="
																				+ fixedLength
																				+ "&isPaging="
																				+ isPaging,
																		null);
													}
												});
							} else {
								BIONE.tip("数据异常，请联系系统管理员");
							}
						},
						error : function() {
							//BIONE.tip("数据异常，请联系系统管理员");
						}
					});

		};
		//初始化列对象
		GridObject = {
			"sts" : {
				display : '报表状态',
				name : 'sts',
				minWidth : 50,
				width : 60,
				render : HandStsRender
			},
			"finishSts" : {
				display : '报表状态',
				name : 'sts',
				minWidth : 50,
				width : 60,
				render : FinishStsRender
			},
			"dataDate" : {
				display : '数据日期',
				name : 'dataDate',
				minWidth : 50,
				width : 80
			},
			"exeObjId" : {
				display : '机构编码',
				name : 'exeObjId',
				minWidth : 50,
				width : 60
			},
			"exeObjNm" : {
				display : '机构名称',
				name : 'exeObjNm',
				minWidth : 100,
				width : 180
			},
			"taskNm" : {
				display : '任务名称',
				name : 'taskNm',
				minWidth : 130,
				width : 130
			},
			"taskObjNm" : {
				display : '报表名称',
				name : 'taskObjNm',
				minWidth : 400,
				width : 400,
				align : 'left',
				render : taskObjNmRender
			},
			"endTime" : {
				display : '截止时间',
				name : 'endTime',
				minWidth : 50,
				width : 80,
				type : 'date',
				format : 'yyyyMMdd'
			},
			"sumpartRs" : {
				display : '总分校验',
				name : 'sumpartRs',
				minWidth : 50,
				width : 60,
				render : SumpartRsRender
			},
			"logicRs" : {
				display : '逻辑校验',
				name : 'logicRs',
				minWidth : 50,
				width : 60,
				render : LogicRsRender
			},
			"warnRs" : {
				display : '预警校验',
				name : 'warnRs',
				minWidth : 70,
				width : 70,
				render : WarnRsRender
			},
			"isMainRpt" : {
				display : '是否总表',
				name : 'isMainRpt',
				minWidth : 50,
				width : 60,
				render : IsMainRptRender
			},
			//modify by wusb at 20161118 
			"zeroRs" : {
				display : '零值校验',
				name : 'zeroRs',
				minWidth : 50,
				width : 60,
				render : ZeroRsRender
			},
			"exeObjNmChild" : {
				display : '责任人',
				name : 'exeObjNm',
				minWidth : 50,
				width : 80
			},
			"applyUserNm" : {
				display : '申请人',
				name : 'applyUserNm',
				minWidth : 50,
				width : 60
			},
			"applyTime" : {
				display : '申请时间',
				name : 'applyTime',
				minWidth : 80,
				width : 180,
				type : 'date',
				format : 'yyyy-MM-dd hh:mm:ss'
			},
			"collateUserNm" : {
				display : '审批人',
				name : 'collateUserNm',
				minWidth : 50,
				width : 60
			},
			"collateTime" : {
				display : '审批时间',
				name : 'collateTime',
				minWidth : 80,
				width : 180,
				type : 'date',
				format : 'yyyy-MM-dd hh:mm:ss'
			},
			"collateSts" : {
				display : '审批状态',
				name : 'collateSts',
				minWidth : 50,
				width : 60,
				render : CollateStsRender
			},
			"sumSts" : {
				display : '汇总状态',
				name : 'sumSts',
				minWidth : 50,
				width : 60
			},
			"lineNm" : {
				display : '条线名称',
				name : 'lineNm',
				minWidth : 100,
				width : 100
			},
			"rowNum" : {
				display : '行数',
				name : 'rowNum',
				minWidth : 50,
				width : 60
			},
			"colNum" : {
				display : '列数',
				name : 'colNum',
				minWidth : 50,
				width : 60
			},
			"defuVal" : {
				display : '初始值',
				name : 'defuVal',
				minWidth : 50,
				width : 60
			},
			"befUpVal" : {
				display : '改前值',
				name : 'befUpVal',
				minWidth : 50,
				width : 60
			},
			"aftUpVal" : {
				display : '改后值',
				name : 'aftUpVal',
				minWidth : 50,
				width : 60
			},
			"fileVal" : {
				display : '提交值',
				name : 'fileVal',
				minWidth : 50,
				width : 60
			},
			"oprUser" : {
				display : '填报用户',
				name : 'oprUser',
				minWidth : 60,
				width : 80
			},
			"oprTime" : {
				display : '提交时间',
				name : 'oprTime',
				minWidth : 50,
				width : 100,
				type : 'date',
				format : 'yyyy-MM-dd hh:mm:ss'
			},
			"rejectReason" : {
				display : '驳回理由',
				name : 'rejectReason',
				minWidth : 70,
				width : 200,
				render : RejectReasonRender
			},
			"taskId" : {
				display : '任务Id',
				name : 'taskId',
				isAllowHide : false,
				hide : true,
				minWidth : 1,
				width : 1
			}
		};
		//初始化搜索表单对象
		//edit by lxp 20161128(调整搜索输入框) 
		var SearchFormObject = {
			"handSts" : {
				display : "报表状态",
				name : "handSts",
				newline : false,
				type : "select",
				width : '140',
				cssClass : "field",
				labelWidth : '90',
				comboboxName : "handSts_sel",
				attr : {
					op : "=",
					field : "i.sts"
				},
				options : {
					data : [ {
						text : '已提交',
						id : "1"
					}, {
						text : '已复核',
						id : "2"
					}, {
						text : '已审核',
						id : "3"
					} ],
					cancelable : true
				}
			},
			"finishSts" : {
				display : "报表状态",
				name : "finishSts",
				newline : false,
				type : "select",
				width : '140',
				cssClass : "field",
				labelWidth : '90',
				comboboxName : "finishSts_sel",
				attr : {
					op : "=",
					field : "i.sts"
				},
				options :{ data : [], cancelable  : true}
			},
			"logicRsSts" : {
				display : "逻辑校验状态",
				name : "logicRsSts",
				newline : false,
				type : "select",
				width : '140',
				cssClass : "field",
				labelWidth : '90',
				comboboxName : "logicRsSts_sel", //attr : { op : "=", field : "sl.checkSts"},
				options : {
					data : [ {
						text : '未校验',
						id : "00"
					}, {
                        text : '准备校验',
                        id : "01"
                    }, {
                        text : '校验中',
                        id : "02"
                    }, {
						text : '校验通过',
						id : "05"
					}, {
						text : '校验未通过',
						id : "06"
					}, {
						text : '校验失败',
						id : "04"
					}],
					cancelable : true
				}
			},
			"dataDate" : {
				display : "数据日期",
				name : "dataDate",
				newline : false,
				type : "date",
				width : '140',
				cssClass : "field",
				labelWidth : '90',
				attr : {
					op : "=",
					field : "i.data_date"
				},
				options : {
					format : "yyyyMMdd"
				}
			},
			"collateSts" : {
				display : "审批状态",
				name : "collateSts",
				newline : false,
				type : "select",
				width : '140',
				cssClass : "field",
				labelWidth : '90',
				comboboxName : "collateSts_sel",
				attr : {
					op : "=",
					field : "re.sts"
				},
				options : {
					data : [ {
						text : '未审批',
						id : "0"
					}, {
						text : '同意',
						id : "1"
					}, {
						text : '不同意',
						id : "2"
					} ],
					cancelable : true
				}
			},
			"orgNm" : {
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
					field : "exeObjId"
				}
			},
			"taskNm" : {
				display : "任务名称",
				name : "taskNm",
				newline : false,
				type : "select",
				width : '140',
				cssClass : "field",
				labelWidth : '90',
				comboboxName : "taskNm_sel",
				attr : {
					op : "=",
					field : "i.task_id"
				}
			},
			"rptNm" : {
				display : "报表名称",
				name : "rptNm",
				newline : false,
				type : "select",
				width : '140',
				cssClass : "field",
				labelWidth : '90',
				comboboxName : "rptNm_sel",
				attr : {
					op : "=",
					field : "i.taskObjId"
				}
			},
			"lineNm" : {
				display : "条线名称",
				name : "lineNm",
				newline : false,
				type : "select",
				width : '140',
				cssClass : "field",
				labelWidth : '90',
				comboboxName : "lineNm_sel",
				attr : {
					op : "=",
					field : "li.lineId"
				}
			},
			"oprNm" : {
				display : "操作用户",
				name : "oprNm",
				newline : false,
				type : "select",
				width : '140',
				cssClass : "field",
				labelWidth : '90',
				comboboxName : "oprNm_sel",
				attr : {
					op : "=",
					field : "li.lineId"
				}
			},
			"beginDate" : {
				display : "开始日期",
				name : "beginDate",
				newline : false,
				type : "date",
				width : '140',
				cssClass : "field",
				labelWidth : '90',
				attr : {
					op : "=",
					field : "i.begin_date"
				},
				options : {
					format : "yyyyMMdd"
				}
			},
			"endDate" : {
				display : "结束日期",
				name : "endDate",
				newline : false,
				type : "date",
				width : '140',
				cssClass : "field",
				labelWidth : '90',
				attr : {
					op : "=",
					field : "i.end_date"
				},
				options : {
					format : "yyyyMMdd"
				}
			}
		};
		 //搜索框finishSts赋值
		 SearchFormObject.finishSts.options.data=finishStsData;
		//生成子对象
		CreateChildObject = function(commonObject, sourceArr) {
			var resultArr = [];
			for ( var i in sourceArr) {
				resultArr.push(commonObject[sourceArr[i]]);
			}
			return resultArr;
		};
		//机构树
		CommonOrgTree = function(orgTreeUrl) {
			var setting = {
				async : {
					enable : true,
					url : orgTreeUrl,
					autoParam : [ "id" ],
					dataType : "json",
					type : "post",
					dataFilter : function(treeId, parentNode,
							childNodes) {
						if (childNodes) {
							for (var i = 0; i < childNodes.length; i++) {
								childNodes[i].id = childNodes[i].params.realId;
								childNodes[i].upId = childNodes[i].upId;
								childNodes[i].nodeType = childNodes[i].params.type;
								childNodes[i].isParent = childNodes[i].params.isParent == "true" ? true
										: false;
							}
						}
						return childNodes;
					}
				},
				//   check:{ enable : true, chkboxType :{"Y":"","N":""}, chkStyle :"checkbox"},
				data : {
					key : {
						name : "text"
					},
					simpleData : {
						enable : true,
						idKey : "id",
						pIdKey : "upId"
					}
				},
				view : {
					selectedMulti : false
				},
				callback : {
					onNodeCreated : function(event, treeId,
							treeNode) {
						if (treeNode.id == "ROOT") {
							// 若是根节点，展开下一级节点
							taskOrgTree.reAsyncChildNodes(
									treeNode, "refresh");
						}
					}
				}
			};
			window['taskOrgTree'] = $.fn.zTree.init($("#tree"),
					setting);
		};
		//树按钮
		CommonTreeButton = function() {
			var btns = [];
			btns.push({
				text : "取消",
				onclick : function() {
					BIONE.closeDialog("taskOrgWin");
				}
			}, {
				text : "选择",
				onclick : function() {
					var nodes = taskOrgTree.getSelectedNodes();
					if ("ROOT" == nodes[0].id) {
						BIONE.tip("根节点不可以选择！");
					} else {
						var c = window.parent.jQuery.ligerui
								.get("orgNm_sel");
						c._changeValue(nodes[0].id,
								nodes[0].text);
						BIONE.closeDialog("taskOrgWin");
					}
				}
			});
			BIONE.addFormButtons(btns);
		};
		//GRID
		//isOnDbClick:true-可以双击行 false-不可以双击行
		//add by wusb at 20161202 如果是报表导出需要显示是否是总表

		TaskFillGrid = function(gridUrl, columnSource,
				isOnDbClick, callBack, checkbox, delayLoad,
				sortName, operType) {
			if (operType != null && columnSource != null) {
				if (operType == "01") {
					GridObject["finishSts"].display = "复核状态";
				} else if (operType == "02") {
					GridObject["finishSts"].display = "审核状态";
				} else {
					GridObject["finishSts"].display = "报表状态";
				}
			} else {
				GridObject["finishSts"].display = "报表状态";
			}
			var columns = CreateChildObject(GridObject,
					columnSource);
			var eles = {
				width : '100%',
				height : '99%',
				columns : columns,
				checkbox : true,
				usePager : true,
				pageSize : 20,
				isScroll : true,
				rownumbers : true,
				alternatingRow : true,
				colDraggable : true,
				dataAction : 'server',
				method : 'get',
				url : gridUrl,
				sortName : sortName ? sortName
						: 'i.data_date, i.exe_obj_id, i.task_obj_id, i.task_id',
				sortOrder : 'desc',
				toolbar : {},
				onLoading : function() {
					BIONE.loading = true;
					BIONE.showLoading("正在加载数据中...");
				},
				onLoaded : function() {
					BIONE.loading = false;
					BIONE.hideLoading();
				},
				enabledSort : false,
				delayLoad : delayLoad ? delayLoad : false,
				selectRowButtonOnly : true
			};
			if (isOnDbClick) {
				eles["onDblClickRow"] = callBack;
			}
			if (checkbox) {
				eles["checkbox"] = false;
			}
			grid = $("#maingrid").ligerGrid(eles);
		};
		//SearchForm
		CommonSerchForm = function(taskComBoBoxUrl,
				rptComBoBoxUrl, orgTreeSkipUrl,
				lineComBoBoxUrl, fieldSource, flag, newLineNum,
				operType) {
			/* 注释modify by wusb at 20161201 operType 没有传值**/
			if (operType != null && fieldSource != null) {
				if (operType == "01") {
					SearchFormObject["finishSts"].display = "复核状态";
					SearchFormObject["finishSts"].options.data = [
							{
								text : '未复核',
								id : "1"
							}, {
								text : '已复核',
								id : "2"
							} ];
				} else if (operType == "02") {
					SearchFormObject["finishSts"].display = "审核状态";
					SearchFormObject["finishSts"].options.data = [
							{
								text : '未审核',
								id : "2"
							}, {
								text : '已审核',
								id : "3"
							} ];
				}
			}
			//判断是否为历史查询页面，决定搜索列表下任务状态栏显示内容 孔渊博 20170210
			if (pt == "021") {
				SearchFormObject["updateSts"].options.data = [ {
					text : '已完成',
					id : ""
				} ];
			}
			/* if(pt =='02'){
			  fieldSource = operType;
			} */

			if (taskComBoBoxUrl) {
				SearchFormObject["taskNm"].options = {
					valueFieldID : "taskId",
					url : taskComBoBoxUrl,
					ajaxType : "get",
					// 联动报表
					onSelected : function(value) {
						if ("" != value) {
							$.ajax({
								async : false,
								type : "post",
								url : rptComBoBoxUrl,
								dataType : "json",
								data : {
									"taskId" : value,
									"flag" : flag
								},
								success : function(rptData) {
									$.ligerui.get("rptNm_sel")
											.setValue("");
									$.ligerui.get("rptNm_sel")
											.setData(rptData);
								}
							});
						}
					},
					cancelable : true,
					dataFilter : true
				};
			}
			if (rptComBoBoxUrl) {
				SearchFormObject["rptNm"].options = {
					onBeforeOpen : function() {
						var taskId = $.ligerui
								.get("taskNm_sel").getValue();
						if ("" == taskId) {
							$.ligerui.get("rptNm_sel")
									.setValue("");
							$.ligerui.get("rptNm_sel").setData(
									"");
							BIONE.tip("请选择任务");
						}
					},
					cancelable : true,
					dataFilter : true
				};
			}
			if (lineComBoBoxUrl) {
				SearchFormObject["lineNm"].options = {
					valueFieldID : "lineId",
					url : lineComBoBoxUrl,
					ajaxType : "get",
					cancelable : true
				};
			}
			//添加机构名称的options属性
			if (orgTreeSkipUrl) {
				SearchFormObject["orgNm"].options = {
					onBeforeOpen : function() {
						var taskId = $.ligerui.get("taskNm_sel").getValue();
						var height = $(window).height() - 120;
						var width = $(window).width() - 480;
						window.BIONE
								.commonOpenDialog(
										"机构树",
										"taskOrgWin",
										width,
										height,
										orgTreeSkipUrl
												+ "?taskId="
												+ taskId
												+ "&orgType=${moduleType}",
										null);
						return false;
					},
					cancelable : true
				};
			}
			//修改换行

			if (newLineNum != null) {
				for (var j = 0; j < fieldSource.length; j = j
						+ newLineNum) {
					SearchFormObject[fieldSource[j]].newline = true;
				}
			}
			var fields = CreateChildObject(SearchFormObject,
					fieldSource);
			//  //拼接数组
			//  var fields = origFields.concat(addFields);
			$("#search").ligerForm({
				fields : fields
			});
		};
		// 查询表单的搜索按钮
		InitSearchButtons = function() {
			BIONE.addSearchButtons("#search", grid,
					"#searchbtn");
		};
	})(jQuery);
</script>
<script type="text/javascript">
	var submitJudgeParams = null;
	var submitJudgetaskInsIds = null;
	var doFlag = "";
	var rptOperType = "${rptOperType}";
	var selectedRptIdArr = [];
	var moduleType = "${moduleType}";
	if (rptOperType == "") {
		rptOperType = "${operType}";
	}
	//add by wusb at 20161201 是否过滤总表
	var pt = "${pt}";
	$(function() {
		tmp.searchForm();
		//add by wusb at 20161202 如果是报表导出需要显示是否总表  修改下面的方法默认值传null
		if (pt != "021") {//判断是否是历史查询页面，显示不同的任务实例信息  孔渊博  20170210
			TaskFillGrid(tmp.gridUrl, tmp.columns, false, null, false, true);
		} else {
			TaskFillGrid(tmp.gridHistoryUrl, tmp.columns, false, null, false,
					true);
		}
		tmp.initButtons();
		tmp.addMySearchButtons("#search", grid, "#searchbtn");
		
		$.ligerui.get("finishSts_sel").setValue("0");
		var form = $('#formsearch');
		var rule = BIONE.bulidFilterGroup(form);
		if (rule.rules.length) {
			grid.setParm("condition", JSON2
					.stringify(rule));
			grid.setParm("newPage", 1);
			grid.options.newPage=1
		} else {
			grid.setParm("condition", "");
			grid.setParm('newPage', 1);
			grid.options.newPage=1
		}
		grid.loadData();
		// 取得当前任务实例 的最大数据日期，第一次显示最大数据日期的任务实例
		//tmp.getMaxTskDatadate();
		tmp.initExport();
	});
	var download = null;
	parent.parent.window.child = window;
	var fileName = '';
	var columns = ["taskNm", "dataDate", "endTime", "taskObjNm", "exeObjNm", "finishSts", "logicRs", "warnRs", "zeroRs", "sumpartRs", "rejectReason", "taskId", "oprUser", "oprTime"];
	<%--if('03' == "${moduleType}"){--%>
	<%--	columns = ["taskNm", "dataDate", "endTime", "taskObjNm", "exeObjNm", "finishSts", "logicRs", "sumpartRs", "rejectReason", "taskId", "oprUser", "oprTime"];--%>
	<%--}--%>
	var tmp = {
		gridHistoryUrl : "${ctx}/rpt/frs/rptfill/flHistoryTaskList?orgTypes=${moduleType}&operType=${operType}&rptOperType=${rptOperType}",//历史查询获取已完成任务跳转链接  孔渊博  20170210
		gridUrl : "${ctx}/rpt/frs/rptfill/frsTaskList?orgTypes=${moduleType}&operType=${operType}&rptOperType=${rptOperType}",
		taskComBoBoxUrl : "${ctx}/frs/rptfill/reject/taskNmComBoBox?orgTypes=${moduleType}&pt=${pt}&flag=1",
		rptComBoBoxUrl : "${ctx}/frs/rptfill/reject/rptNmComBoBox",
		orgTreeSkipUrl : "${ctx}/frs/integratedquery/rptquery/searchOrgSetInfo",
	 	columns : columns,
		fields:["taskNm", "rptNm", "orgNm", "finishSts", "dataDate", "logicRsSts"],

		//如果是报表导出用这个
		searchForm : function() {
			var demoWidth = $("#search").width();
			var newLineNum = parseInt(demoWidth/260);
			CommonSerchForm(this.taskComBoBoxUrl, this.rptComBoBoxUrl, this.orgTreeSkipUrl, null, this.fields, "1", newLineNum);
		},
		getMaxTskDatadate : function() {
			$.ajax({
				cache : false,
				async : true,
				url : "${ctx}/rpt/frs/rptfill/getMaxTskDatadate?orgTypes=${orgTypes}",
				dataType : 'text',
				type : "post",
				success : function(result) {
					if (result) {
						$('#dataDate')[0].value = result;
						var form = $('#formsearch');
						var rule = BIONE.bulidFilterGroup(form);
						if (rule.rules.length) {
							grid.setParm("condition", JSON2
									.stringify(rule));
							grid.setParm("newPage", 1);
						} else {
							grid.setParm("condition", "");
							grid.setParm('newPage', 1);
						}
						grid.loadData();
					} else {
						BIONE.tip("没有任务实例");
					}
				},
				error : function() {
					BIONE.tip("任务实例最大日期获取异常，请联系系统管理员");
				}
			});
		},
		initExport : function() {
			download = $('<iframe id="download"  style="display: none;"/>');
			$('body').append(download);
		},
		oper_sumDataType : function(){
			BIONE.commonOpenDialog("汇总方式选择","sumDataType",450,300,"${ctx}/rpt/frs/rptfill/sumDataType?exhibition=list");	
		},
		oper_datasum : function() {
			//走单汇总 循环实现批量 
			var manager = $("#maingrid").ligerGetGridManager();
			var rows = manager.getSelectedRows();
			var detailCount = 0;
			if (rows.length > 0) {
				var data = [];
				var taskInsIds = [];
				for (var i = 0; i < rows.length; i++) {
					if("01" == rows[i].templateType){
						detailCount ++;
					}else{
						data.push(rows[i].taskObjId + "#" + rows[i].dataDate + "#"
								+ rows[i].exeObjId + "#" + rows[i].taskId + "#"
								+ rows[i].taskInstanceId);
						taskInsIds.push(rows[i].taskInstanceId);
					}
				}
				if(detailCount > 0 && detailCount < rows.length){
					BIONE.tip("明细报表不进行机构汇总，系统已为您跳过，机构汇总开始！");
				}else if(detailCount == rows.length){
					BIONE.tip("明细报表不进行机构汇总,请重新选择！");
					return;
				}
				$.ajax({
					cache : false,
					async : true,
					url : "${ctx}/rpt/frs/dataSum/batchSumData?doFlag=list-to-sumdata&d="
							+ new Date().getTime() + "&rptFillOperType=37",
					dataType : 'json',
					beforeSend : function() {
						BIONE.loading = true;
						BIONE.showLoading("数据汇总中，请稍等...");
					},
					data : {
						data : JSON2.stringify(data),
						taskInsIds : JSON2.stringify(taskInsIds),
						taskFillOperType : '38',
						operType : '03',
						sumType : base_SumType,
						orgType : moduleType
					},
					type : "post",
					success : function(result) {
						if(result.isChild){
							BIONE.tip(result.isChild);
							
						}
						if (result && result.result) {
							BIONE.showSuccess(result.result);
						} else {
							BIONE.tip("数据汇总异常，请联系系统管理员");
						}
					},
					error : function() {
						BIONE.tip("数据异常，请联系系统管理员");
					},
					complete : function() {
						BIONE.hideLoading();
					}
				});
			} else {
				BIONE.tip("请选择记录进行汇总");
			}
		},
		list_down : function() {
			var data = tmp.queryDownLoadList();
			var condition = data.condition;
			var src = "${ctx}/rpt/frs/rptfill/frsTaskList?orgTypes=${moduleType}&operType=${operType}&rptOperType=${rptOperType}&isListDownload=1&otherCondition="
					+ encodeURI(encodeURI(condition));
			if (pt == '02') {
				src = src + "&isRptExp=Y";
			}
			download.attr('src', src);
		},
		//modify by wusb at 20161118 批量校验换成 逻辑校验、警戒校验、总分校验、零值校验
		check_single : function(checkType) {
			var manager = $("#maingrid").ligerGetGridManager();
			var rows = manager.getSelectedRows();
			var taskInsIds = [];
			var taskIds = [];
			if (rows.length > 0) {
				var data = {};
				var objArr = [];
				//最多一次校验200条
				/* if(rows.length > 200){
					BIONE.tip("报表校验是一个特别消耗资源的操作，一次最多操作200条");
					return;
				} */
				for (var i = 0; i < rows.length; i++) {
					taskInsIds.push(rows[i].taskInstanceId);
					taskIds.push(rows[i].taskId);
					//01:等待运行  02:运行中
					if ((rows[i].logicRs == '01' || rows[i].logicRs == '02')
							|| (rows[i].sumpartRs == '01' || rows[i].sumpartRs == '02')
							|| (rows[i].warnRs == '01' || rows[i].warnRs == '02')
							|| (rows[i].zeroRs == '01' || rows[i].zeroRs == '02')) {
						BIONE.tip("含有正在校验的记录，请等候");
						continue;
					} else {
						var rptObj = {};
						rptObj.rptId = rows[i].taskObjId;
						rptObj.dataDate = rows[i].dataDate;
						rptObj.lineId = rows[i].lineId;
						var obj;
						//逻辑校验  
						if ("logic" == checkType) {
							obj = {
								"rptId" : rows[i].taskObjId,
								"orgNo" : rows[i].exeObjId,
								"dataDate" : rows[i].dataDate,
								"logicRs" : rows[i].logicRs ? rows[i].logicRs
										: null,
								"lineId" : rows[i].lineId
							};
						}
						//警戒校验
						if ("warn" == checkType) {
							obj = {
								"rptId" : rows[i].taskObjId,
								"orgNo" : rows[i].exeObjId,
								"dataDate" : rows[i].dataDate,
								"warnRs" : rows[i].warnRs ? rows[i].warnRs
										: null,
								"lineId" : rows[i].lineId
							};
						}
						//总分校验
						if ("sumpart" == checkType) {
							obj = {
								"rptId" : rows[i].taskObjId,
								"orgNo" : rows[i].exeObjId,
								"dataDate" : rows[i].dataDate,
								"sumpartRs" : rows[i].sumpartRs ? rows[i].sumpartRs : null,
								"lineId" : rows[i].lineId
							};
						}
						//零值校验
						if ("zero" == checkType) {
							obj = {
								"rptId" : rows[i].taskObjId,
								"orgNo" : rows[i].exeObjId,
								"dataDate" : rows[i].dataDate,
								"zeroRs" : rows[i].zeroRs ? rows[i].zeroRs : null,
								"lineId" : rows[i].lineId
							};
						}
						/********************** modif by wusb at 20161118   end ********************************/
						objArr.push(obj);
						// dataDate:orgNo,orgNo:rpgId,rpgId; ...
						if (!data[rows[i].dataDate]) {
							data[rows[i].dataDate] = {};
							data[rows[i].dataDate].orgNo = {};
							data[rows[i].dataDate].rptId = {};
						}
						var orgNo = {}, rptId = {};
						orgNo[rows[i].exeObjId] = ' ';
						rptId[rows[i].taskObjId] = ' ';
						$.extend(data[rows[i].dataDate].orgNo, orgNo);
						$.extend(data[rows[i].dataDate].rptId, rptId);
					}
				}
				var objArrParms = [], group = {};
				for ( var i in data) {
					group = {};
					group.DataDate = i;
					group.OrgNo = [];
					for ( var ii in data[i].orgNo) {
						group.OrgNo.push(ii);
					}
					if ("logic" == checkType) {
						group.LogicCheckRptTmpId = [];
					}
					//警戒校验
					if ("warn" == checkType) {
						group.WarnCheckRptTmpId = [];
					}
					//总分校验
					if ("sumpart" == checkType) {
						group.SumCheckRptTmpId = [];
						if("02" == ${moduleType}){
							group.IsCfgSum = "Y";
						}
					}
					//零值校验
					if ("zero" == checkType) {
						group.ZeroCheckRptTmpId = [];
					}
					for ( var ii in data[i].rptId) {
						if ("logic" == checkType) {
							group.LogicCheckRptTmpId.push(ii);
						}
						//警戒校验
						if ("warn" == checkType) {
							group.WarnCheckRptTmpId.push(ii);
						}
						//总分校验
						if ("sumpart" == checkType) {
							group.SumCheckRptTmpId.push(ii);
						}
						//零值校验
						if ("zero" == checkType) {
							group.ZeroCheckRptTmpId.push(ii); //modiby wusb at 20161118 
						}
					}
					objArrParms.push(JSON2.stringify(group));
				}
				$.ajax({
					cache : false,
					async : true,
					url : "${ctx}/rpt/frs/rptfill/judgeAndSaveCheckStsAync2",
					dataType : 'json',
					data : {
						objArr : JSON2.stringify(objArr),
						objArrParms : objArrParms.join(';'),
						taskInsIds : JSON2.stringify(taskInsIds), 
						taskFillOperType : '37',
						operType : '03',
						checkType : checkType,
						taskType : "${moduleType}",
						taskIds : JSON2.stringify(taskIds)
					},//异步
					type : "post",
					beforeSend : function() {
						BIONE.showLoading("正在校验，请稍候...");
					},
					success : function(result) {
						BIONE.hideLoading();
						if (result.result) {
						    if("WARN"== result.result){
						        grid.loadData();
						        BIONE.tip("存在已是当前任务所下发的最底层机构无需校验，非最底层机构正在校验，请刷新列表查看最新校验状态");
							}else if ("OK" == result.result) {
								grid.loadData();
								BIONE.tip("校验开始，刷新列表查看最新校验状态");
							} else if (result.isChild) {
								BIONE.showSuccess(result.isChild);
							} else {
								BIONE.tip("该记录正在校验，请刷新后重试");
							}
						} else {
							BIONE.tip("数据异常，请联系系统管理员");
						}
					},
					error : function() {
						BIONE.tip("数据加载异常，请联系系统管理员");
					}
				});
			} else {
				BIONE.tip("请选择记录");
			}
		},
		getLogicValidResult : function(templateId, dataDate, orgNo){
			var validResult = {};
			$.ajax({
				cache : false,
				async : false,
				url : "${ctx}/rpt/frs/rptfill/getLogicValidResult",
				dataType : 'json',
				data : {
					templateId : templateId, 
					dataDate : dataDate, 
					orgNo : orgNo
				},
				type : "post",
				beforeSend : function() {
					BIONE.showLoading("检查逻辑校验...");
				},
				complete: function(){
					BIONE.hideLoading();
				},
				success : function(result){
					validResult = result;
				}
			});
			return validResult;
		},

		//检查修改记录是否已填写
		getCellUpdateSts : function (dataDate, taskInstanceId, tmpId) {
			var updateSts = 'N';
			$.ajax({
				cache : false,
				async : false,
				url : "${ctx}/rpt/frs/rptfill/getCellUpdateSts",
				dataType : 'json',
				data : {
					dataDate : dataDate,
					taskInstanceId : taskInstanceId,
					tmpId : tmpId
				},
				type : "post",
				success : function(result){
					if(result.flag){
						updateSts = 'Y';
					}
				}
			});
			return updateSts;
		},

		oper_submit : function(flag) { //flag为强制提交标志
			var manager = $("#maingrid").ligerGetGridManager();
			var rows = manager.getSelectedRows();

			var taskInsIds = [];
			var params = [];
			var taskId = "";
			var taskNm = "";
			var rptId = "";
			var rptNm = "";
			var simpleOrg = null;
			var tmpIds = [];
			var dataDates = [];
			var orgNos = [];
			var isForceSubmit = 'N';
			if (rows.length > 0) {
				var count = 0;
				for ( var i in rows) {
					taskId += rows[i].taskId + ",";
					taskNm += rows[i].taskNm + ",";
					rptId += rows[i].taskObjId + ",";
					rptNm += rows[i].taskObjNm + ",";
					/* if (simpleOrg == null) {
						simpleOrg = rows[i].exeObjId;
					} else {
						if (simpleOrg != rows[i].exeObjId) {
							BIONE.tip("请选择同一机构进行提交");
							return;
						}
					} */
					if ((rows[i].logicRs == '01' || rows[i].logicRs == '02')
							|| (rows[i].sumpartRs == '01' || rows[i].sumpartRs == '02')
							|| (rows[i].warnRs == '01' || rows[i].warnRs == '02')
							|| (rows[i].zeroRs == '01' || rows[i].zeroRs == '02')) {//modify by wusb at 20161118 
						BIONE.tip("含有正在校验的记录，无法提交");
						return;
					} else {
						if (rows[i].sts == '0' || rows[i].sts == '10') {//未提交/已驳回
							if(flag == "force"){//强制提交
								isForceSubmit = 'Y';
								if ((rows[i].logicRs == null || rows[i].logicRs == '04' || rows[i].logicRs == '06')
									|| (rows[i].sumpartRs == null || rows[i].sumpartRs == '04' || rows[i].sumpartRs == '06')
									|| (rows[i].warnRs == null || rows[i].warnRs == '04' || rows[i].warnRs == '06')
									|| (rows[i].zeroRs == null || rows[i].zeroRs == '04' || rows[i].zeroRs == '06')){
										count++;
								}
								taskInsIds.push(rows[i].taskInstanceId);
								var param = {
									'taskIns' : rows[i].taskInstanceId,
									'orgNo' : rows[i].exeObjId,
									'rptId' : rows[i].taskObjId,
									'dataDate' : rows[i].dataDate,
									'type' : rows[i].taskType
								};
								params.push(param);
								tmpIds.push(rows[i].templateId);
								dataDates.push(rows[i].dataDate);
								orgNos.push(rows[i].exeObjId);
							}else{//普通提交
								if(rows[i].templateType != '01'){//明细报表不需要逻辑校验
									//检查修改记录是否已填写
									var updateSts = tmp.getCellUpdateSts(rows[i].dataDate, rows[i].taskInstanceId, rows[i].templateId);
									if('N' == updateSts){
										BIONE.showSuccess("报表【"+rows[i].taskObjNm+"】中修改的单元格中有些被锁定，请填写修改说明后再提交！");
										return;
									}
									if ((rows[i].logicRs == null || rows[i].logicRs == '04' || rows[i].logicRs == '06')){
										var result = tmp.getLogicValidResult(rows[i].templateId,rows[i].dataDate,rows[i].exeObjId);
										if(rows[i].logicRs != null){
											//判断逻辑校验是否有未通过项，监管制度校验公式未通过不能提交，自定义校验不通过需添加说明。	
											//var result = tmp.getLogicValidResult(rows[i].templateId,rows[i].dataDate,rows[i].exeObjId);
											var result01 = result.result01;//强校验
											var result02 = result.result02;//软校验
											if(result01 == 0 && result02 == 0){
												if(rows[i].taskType == '03' && (rows[i].sumpartRs == null || rows[i].sumpartRs == '04' || rows[i].sumpartRs == '06')){
													BIONE.showSuccess("总分校验未通过，请核对数据！");
													return;
												}else{
													if((rows[i].warnRs == null || rows[i].warnRs == '04' || rows[i].warnRs == '06')
															|| (rows[i].zeroRs == null || rows[i].zeroRs == '04' || rows[i].zeroRs == '06')){
															count++;
														}
														taskInsIds.push(rows[i].taskInstanceId);
														var param = {
															'taskIns' : rows[i].taskInstanceId,
															'orgNo' : rows[i].exeObjId,
															'rptId' : rows[i].taskObjId,
															'dataDate' : rows[i].dataDate,
															'type' : rows[i].taskType
														};
														params.push(param);
														tmpIds.push(rows[i].templateId);
														dataDates.push(rows[i].dataDate);
														orgNos.push(rows[i].exeObjId);
												}
											}else{
												BIONE.showSuccess("报表【"+rows[i].taskObjNm+"】的逻辑校验有未通过项，【监管制度】未通过"+result01+"条，【自定义】未通过"+result02+"条。");
												return;
											}
										}else{
											var result03 = result.result03;
											if(result03 > 0){
												BIONE.showSuccess("报表【"+rows[i].taskObjNm+"】的逻辑校验未进行校验，请您先进行校验！");
												return;
											}else{
												taskInsIds.push(rows[i].taskInstanceId);
												var param = {
													'taskIns' : rows[i].taskInstanceId,
													'orgNo' : rows[i].exeObjId,
													'rptId' : rows[i].taskObjId,
													'dataDate' : rows[i].dataDate,
													'type' : rows[i].taskType
												};
												params.push(param);
												tmpIds.push(rows[i].templateId);
												dataDates.push(rows[i].dataDate);
												orgNos.push(rows[i].exeObjId);
											}
										}
									}else{
										if(rows[i].taskType == '03' && (rows[i].sumpartRs == null || rows[i].sumpartRs == '04' || rows[i].sumpartRs == '06')){
											BIONE.showSuccess("总分校验未通过，请核对数据！");
											return;
										}else{
											if((rows[i].warnRs == null || rows[i].warnRs == '04' || rows[i].warnRs == '06')
													|| (rows[i].zeroRs == null || rows[i].zeroRs == '04' || rows[i].zeroRs == '06')){
												count++;
											}
											taskInsIds.push(rows[i].taskInstanceId);
											var param = {
												'taskIns' : rows[i].taskInstanceId,
												'orgNo' : rows[i].exeObjId,
												'rptId' : rows[i].taskObjId,
												'dataDate' : rows[i].dataDate,
												'type' : rows[i].taskType
											};
											params.push(param);
											tmpIds.push(rows[i].templateId);
											dataDates.push(rows[i].dataDate);
											orgNos.push(rows[i].exeObjId);
										}
									}
								}else{
									count++;
									taskInsIds.push(rows[i].taskInstanceId);
									var param = {
										'taskIns' : rows[i].taskInstanceId,
										'orgNo' : rows[i].exeObjId,
										'rptId' : rows[i].taskObjId,
										'dataDate' : rows[i].dataDate,
										'type' : rows[i].taskType
									};
									params.push(param);
									tmpIds.push(rows[i].templateId);
									dataDates.push(rows[i].dataDate);
									orgNos.push(rows[i].exeObjId);
								}
							}
						} else {
							BIONE.tip("含有已提交记录，不可重复提交");
							return;
						}
					}
				}
			}else{
				BIONE.tip("请选择要提交的记录！");
				return;
			}
			if (count > 0) {
				$.ligerDialog .confirm( "含有未通过或者未校验的记录，是否继续？",
					function(yes) {
						if (yes) {
							if (taskInsIds.length > 0
									&& params.length > 0) {
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
									success : function(result) {
										if (result.result == "ERROR") {
											BIONE.tip("数据异常，请联系系统管理员");
											return;
										}
										if (result.result == "YES" && result.ins) {
											var btnFlag = "01"; //提交按钮标识
											var height = $(window).height() - 120;
											var width = $(window).width() - 80;
											submitJudgeParams = JSON2.stringify(params);
											submitJudgetaskInsIds = taskInsIds.join(",");
											BIONE.commonOpenDialog(
												"未提交子机构任务实例",
												"taskInsChildWin",
												width,
												height,
												"${ctx}/rpt/frs/rptfill/showTab?path=rptfill-org-child-ins&sts=0"
												+ "&moduleType=${moduleType}&btnFlag="
												+ btnFlag +"&tmpId=" + tmpIds +"&dataDate=" + dataDates+"&orgNo=" + orgNos + "&isForceSubmit="+isForceSubmit, null);
										} else {
											data = "ignore";
											if (data
													&& data != null
													&& data != ""
													&& data != "ignore") {
												$.ajax({
													cache : false,
													async : true,
													url : "${ctx}/rpt/frs/rptfill/submitTaskBatch",		
													dataType : 'json',
													type : "post",
													data : {
														queryType : "fuhe",
														moduleType : "${moduleType}",
														rptOperType	: "03",
														taskFillOperType : "03",
														taskInsIds : taskInsIds.join(","),
														authUsers : data.join(","),
														org : simpleOrg,
														taskId : taskId,
														taskNm : taskNm,
														rptId : rptId,
														rptNm : rptNm,
														tmpId : tmpIds.join(","),
														dataDate : dataDates.join(","),
														orgNo : orgNos.join(",")
													},
													success : function() {
														grid.loadData();
														BIONE.tip("提交成功");
													},
													error : function() {
														BIONE.tip("提交失败");
													}
												});
											} else if (data && data != null && data == "ignore") {
												if('Y' == isForceSubmit){
													$.ligerDialog.prompt('强制提交原因', true, function(yes, value) {
														if (yes) {
															$.ajax({
																cache : false,
																async : true,
																url : "${ctx}/rpt/frs/rptfill/submitTaskBatch",
																dataType : 'json',
																type : "post",
																data : {
																	rptOperType	: "03",
																	taskFillOperType : "03",
																	taskInsIds : taskInsIds.join(","),
																	ignore : "Y",
																	tmpId : tmpIds.join(","),
																	dataDate : dataDates.join(","),
																	orgNo : orgNos.join(","),
																	isForceSubmit : 'Y',
																	forceSubmitReason : value
																},
																success : function() {
																	grid .loadData();
																	BIONE .tip("提交成功");
																},
																error : function() {
																	BIONE.tip("数据加载异常，请联系系统管理员");
																}
															});
														}
													});
												} else {
													$.ajax({
														cache : false,
														async : true,
														url : "${ctx}/rpt/frs/rptfill/submitTaskBatch",
														dataType : 'json',
														type : "post",
														data : {
															rptOperType	: "03",
															taskFillOperType : "03",
															taskInsIds : taskInsIds.join(","),
															ignore : "Y",
															tmpId : tmpIds.join(","),
															dataDate : dataDates.join(","),
															orgNo : orgNos.join(",")
														},
														success : function() {
															grid .loadData();
															BIONE .tip("提交成功");
														},
														error : function() {
															BIONE.tip("数据加载异常，请联系系统管理员");
														}
													});
												}
											}
										}
									},
									error : function() {
										//BIONE.tip("数据加载异常，请联系系统管理员");
									}
								});
							} else {
								BIONE.tip("数据异常，请联系系统管理员");
							}
						}
					});
			} else {
				if (taskInsIds.length > 0 && params.length > 0) {
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
						success : function(result) {
							if (result.result == "ERROR") {
								BIONE.tip("数据异常，请联系系统管理员");
								return;
							}
							if (result.result == "YES" && result.ins) {
								var btnFlag = "01"; //提交按钮标识
								var height = $(window).height() - 120;
								var width = $(window).width() - 80;
								submitJudgeParams = JSON2.stringify(params);
								submitJudgetaskInsIds = taskInsIds.join(",");
								BIONE.commonOpenDialog(
									"未提交子机构任务实例",
									"taskInsChildWin",
									width,
									height,
									"${ctx}/rpt/frs/rptfill/showTab?path=rptfill-org-child-ins&sts=0&orgNo="
											+ simpleOrg
											+ "&moduleType=${moduleType}&btnFlag="
											+ btnFlag + "&tmpId=" + tmpIds +"&dataDate=" + dataDates + "&orgNo=" + orgNos+ "&isForceSubmit="+isForceSubmit, null);
							} else {
								data = "ignore";
								if (data && data != null && data != ""
										&& data != "ignore") {
									$.ajax({
										cache : false,
										async : true,
										url : "${ctx}/rpt/frs/rptfill/submitTaskBatch",		
										dataType : 'json',
										type : "post",
										data : {
											queryType : "fuhe",
											moduleType : "${moduleType}",
											rptOperType	: "03",
											taskFillOperType : "03",
											taskInsIds : taskInsIds.join(","),
											authUsers : data.join(","),
											org : simpleOrg,
											taskId : taskId,
											taskNm : taskNm,
											rptId : rptId,
											rptNm : rptNm,
											tmpId : tmpIds.join(","),
											dataDate : dataDates.join(","),
											orgNo : orgNos.join(",")
										},
										success : function() {
											grid.loadData();
											BIONE.tip("提交成功");
										},
										error : function() {
											BIONE.tip("提交失败");
										}
									});
								} else if (data && data != null && data == "ignore") {
									if('Y' == isForceSubmit){
										$.ligerDialog.prompt('强制提交原因', true, function(yes, value) {
											if (yes) {
												$.ajax({
													cache : false,
													async : true,
													url : "${ctx}/rpt/frs/rptfill/submitTaskBatch",
													dataType : 'json',
													type : "post",
													data : {
														rptOperType	: "03",
														taskFillOperType : "03",
														taskInsIds : taskInsIds.join(","),
														ignore : "Y",
														tmpId : tmpIds.join(","),
														dataDate : dataDates.join(","),
														orgNo : orgNos.join(","),
														isForceSubmit : 'Y',
														forceSubmitReason : value
													},
													success : function() {
														grid.loadData();
														BIONE.tip("提交成功");
													},
													error : function() {
														BIONE.tip("提交失败");
													}
												});
											}
										});
									} else {
										$.ajax({
											cache : false,
											async : true,
											url : "${ctx}/rpt/frs/rptfill/submitTaskBatch",
											dataType : 'json',
											type : "post",
											data : {
												rptOperType	: "03",
												taskFillOperType : "03",
												taskInsIds : taskInsIds.join(","),
												ignore : "Y",
												tmpId : tmpIds.join(","),
												dataDate : dataDates.join(","),
												orgNo : orgNos.join(",")
											},
											success : function() {
												grid.loadData();
												BIONE.tip("提交成功");
											},
											error : function() {
												BIONE.tip("提交失败");
											}
										});
									}
								}
							}
						},
						error : function() {
							BIONE.tip("提交失败");
						}
					});
				} else {
					BIONE.tip("数据异常，请联系系统管理员");
				}
			}
		},

		banchExportData : function() {
			BIONE
					.commonOpenDialog('选择筛选条件', 'banchImpWin', 600, 430,
							"${ctx}/rpt/frs/rptfill/showTab?path=rpt-todowork-list-condition&operType=exp");
		},

		oper_fdown : function() {
			var manager = $("#maingrid").ligerGetGridManager();
			var rows = manager.getSelectedRows();
			//var firstTaskId = rows[0].taskId;
			if (rows.length > 0) {
				var argsArr = [];
				for ( var i in rows) {
					var orgName = rows[i].exeObjNm;
					var rptId = rows[i].taskObjId;
					var orgNo = rows[i].exeObjId;
					var busiLineId = rows[i].lineId;
					var dataDate = rows[i].dataDate;
					//       var rptNm = rows[i].taskObjNm;
					if (rptId != null && orgNo != null && dataDate != null) {
						var argsArr1 = [];
						var args1 = {
							'DimNo' : 'ORG',
							'Op' : '=',
							'Value' : orgNo
						};
						argsArr1.push(args1);
						var args = {
							'orgNm' : orgName,
							'rptId' : rptId,
							'dataDate' : dataDate,
							'busiLineId' : busiLineId,
							'searchArgs' : JSON2.stringify(argsArr1)
						};
						argsArr.push(args);
					} else {
						BIONE.tip("数据异常，请联系系统管理员");
					}
				}
				if (argsArr.length > 0) {
					if (argsArr.length > 0) {
						BIONE
								.ajax(
										{
											async : false,
											url : "${ctx}/rpt/frs/rptfill/downloadList?json="
													+ encodeURI(encodeURI(JSON2
															.stringify(argsArr)))
													+ "&d=" + new Date().getTime(),
											dataType : 'json',
											type : 'post',
											loading : '正在生成下载文件，请稍等...'
										},
										function(result) {
											if (result.result) {
												if ("OK" == result.result) {
													if (result.zipFilePath
															&& result.folderinfoPath) {
														var src = '';
														src = "${ctx}/rpt/frs/rptfill/downFile?zipFilePath="
																+ encodeURI(result.zipFilePath)
																+ "&folderinfoPath="
																+ encodeURI(result.folderinfoPath)
																+ "&d="
																+ new Date().getTime()
																+ "&taskFillOperType=36&operType="
																+ rptOperType
																+ "&taskInsId="
																+ rows[0].taskInstanceId;
														download.attr('src',
																src);
													} else {
														BIONE
																.tip("数据异常，请联系系统管理员");
													}
												} else {
													BIONE.tip(result.msg);
												}
											} else {
												BIONE.tip("数据异常，请联系系统管理员");
											}
										});
					} else {
						BIONE.tip("数据异常，请联系系统管理员");
					}
				} else {
					BIONE.tip("数据异常，请联系系统管理员");
				}
			} else {
				BIONE.tip("请选择需要下载的记录");
			}
		},
		oper_fill : function() {
			fileName = '';
			var manager = $("#maingrid").ligerGetGridManager();
			var rows = manager.getSelectedRows();
			if (rows.length == 1) {
				var data = rows[0];
				orgNm = data.exeObjNm;
				if ("1" == data.sts) {
					BIONE.tip("任务已提交，不能填报！");
				} else if ("2" == data.sts) {
					BIONE.tip("任务已结束填报，不能填报！");
				} else {
					var rptId = data.taskObjId;
					var dataDate = data.dataDate;
					var orgNo = data.exeObjId;
					var lineId = data.lineId;

					$
							.ajax({
								cache : false,
								async : true,
								url : "${ctx}/rpt/frs/rptfill/getTmpId",
								dataType : 'text',
								data : {
									rptId : rptId,
									dataDate : dataDate,
									lineId : lineId
								},
								type : "post",
								success : function(result) {
									if (result) {
										BIONE
												.ajax(
														{
															async : false,
															url : "${ctx}/rpt/frs/rptfill/createColor",
															dataType : 'text',
															type : 'POST',
															data : {
																rptId : rptId,
																orgNo : orgNo,
																dataDate : dataDate,
																tmpId : result,
																isBatchCheck : true
															}
														},
														function(result) {
															var rptNm = data.taskObjNm;
															var title = "报表信息";
															var logicRs = data.logicRs;
															var sumpartRs = data.sumpartRs;
															var warnRs = data.warnRs;
															var taskInsId = data.taskInstanceId;
															var type = data.taskType;
															var height = $(
																	parent.parent.window)
																	.height() - 10;
															var width = $(
																	parent.parent.window)
																	.width();
															var taskId = data.taskId;
															window.top.color = result;
															//modify by wusb at 20161118 待定...
															window.parent.parent.BIONE
																	.commonOpenDialog(
																			title,
																			"taskFillWin",
																			width,
																			height,
																			"${ctx}/rpt/frs/rptfill/showTab?path=rptfill-todowork-oper&dataDate="
																					+ dataDate
																					+ "&orgNo="
																					+ orgNo
																					+ "&rptId="
																					+ rptId
																					+ "&logicRs="
																					+ logicRs
																					+ "&sumpartRs="
																					+ sumpartRs
																					+ "&warnRs="
																					+ warnRs
																					+ "&taskInsId="
																					+ taskInsId
																					+ "&lineId="
																					+ lineId
																					+ "&rptNm="
																					+ encodeURI(encodeURI(rptNm))
																					+ "&type="
																					+ type
																					+ "&orgNm="
																					+ encodeURI(encodeURI(orgNm))
																					+ "&taskId="
																					+ taskId,
																			null);

														});
									} else {
										BIONE.tip("数据异常，请联系系统管理员");
									}
								},
								error : function() {
									//BIONE.tip("数据异常，请联系系统管理员");
								}
							});
				}
			} else {
				BIONE.tip("请选择一条任务记录");
			}
		},
		queryFav : function() {
			tmp.queryRptTaskList(1);
		},
		setFav : function() {
			var title = "设置收藏";
			var height = $("#center").height() - 50;
			var width = "300";
			BIONE
					.commonOpenDialog(
							title,
							"setWatchWin",
							width,
							height,
							"${ctx}/rpt/frs/rptfill/showTab?path=rptfill-todowork-watchrpt&moduleType=${moduleType}",
							null,
							function(data) {
								if (data == null)
									return;
								$
										.ajax({
											cache : false,
											async : true,
											url : "${ctx}/rpt/frs/rptfill/saveWatchRpt?moduleType=${moduleType}",
											dataType : 'text',
											data : {
												"data" : encodeURI(encodeURI(JSON2
														.stringify(data)))
											},
											type : "post",
											success : function(result) {
												BIONE.tip("保存成功!");
											},
											error : function() {
												//BIONE.tip("数据异常，请联系系统管理员");
											}
										});
							});

		},
		oper_sumview : function() {
			fileName = '';
			var manager = $("#maingrid").ligerGetGridManager();
			var rows = manager.getSelectedRows();

			if (rows.length == 1) {
				var data = rows[0];
				orgNm = data.exeObjNm;
				var rptId = data.taskObjId;
				var dataDate = data.dataDate;
				var orgNo = data.exeObjId;
				var lineId = data.lineId;
				$
						.ajax({
							cache : false,
							async : false,
							url : "${ctx}/rpt/frs/rptfill/getTmpId",
							dataType : 'text',
							data : {
								rptId : rptId,
								dataDate : dataDate,
								lineId : lineId
							},
							type : "post",
							success : function(result) {
								if (result) {
									BIONE
											.ajax(
													{
														async : false,
														url : "${ctx}/rpt/frs/rptfill/createColor",
														dataType : 'text',
														type : 'POST',
														data : {
															rptId : rptId,
															orgNo : orgNo,
															dataDate : dataDate,
															tmpId : result,
															isBatchCheck : true
														}
													},
													function(result) {
														var rptNm = data.taskObjNm;
														var title = "报表信息";
														var logicRs = data.logicRs;
														var sumpartRs = data.sumpartRs;
														var warnRs = data.warnRs;
														var taskInsId = data.taskInstanceId;
														var height = $(
																parent.parent.window)
																.height() - 10;
														var width = $(
																parent.parent.window)
																.width();
														//window.parent.parent.color=result;
														//modify by wusb at 20161118 待定....
														window.parent.parent.BIONE
																.commonOpenDialog(
																		title,
																		"taskViewWin",
																		width,
																		height,
																		"${ctx}/rpt/frs/rptfill/dataSumFillView?dataDate="
																				+ dataDate
																				+ "&orgNo="
																				+ orgNo
																				+ "&rptId="
																				+ rptId
																				+ "&logicRs="
																				+ logicRs
																				+ "&sumpartRs="
																				+ sumpartRs
																				+ "&warnRs="
																				+ warnRs
																				+ "&taskInsId="
																				+ taskInsId
																				+ "&lineId="
																				+ lineId
																				+ "&rptNm="
																				+ encodeURI(encodeURI(rptNm))
																				+ "&orgNm="
																				+ encodeURI(encodeURI(orgNm)),
																		null);
													});
								} else {
									BIONE.tip("数据异常，请联系系统管理员");
								}
							},
							error : function() {
								//BIONE.tip("数据异常，请联系系统管理员");
							}
						});
			} else {
				BIONE.tip("请选择一条任务记录");
			}
		},
		oper_sumexport : function() {
			fileName = '';
			var manager = $("#maingrid").ligerGetGridManager();
			var rows = manager.getSelectedRows();

			if (rows.length == 1) {
				var data = rows[0];
				orgNm = data.exeObjNm;
				var rptId = data.taskObjId;
				var dataDate = data.dataDate;
				var orgNo = data.exeObjId;
				var lineId = data.lineId;
				$
						.ajax({
							cache : false,
							async : false,
							url : "${ctx}/rpt/frs/rptfill/getTmpId",
							dataType : 'text',
							data : {
								rptId : rptId,
								dataDate : dataDate,
								lineId : lineId
							},
							type : "post",
							success : function(result) {
								if (result) {
									BIONE
											.ajax(
													{
														async : false,
														url : "${ctx}/rpt/frs/rptfill/createColor",
														dataType : 'text',
														type : 'POST',
														data : {
															rptId : rptId,
															orgNo : orgNo,
															dataDate : dataDate,
															tmpId : result,
															isBatchCheck : true
														}
													},
													function(result) {
														var rptNm = data.taskObjNm;
														var title = "报表信息";
														var logicRs = data.logicRs;
														var sumpartRs = data.sumpartRs;
														var warnRs = data.warnRs;
														var taskInsId = data.taskInstanceId;
														var height = $(
																parent.parent.window)
																.height() - 10;
														//window.parent.parent.color=result;
														//modify by wusb at 20161118 待定....
														window.parent.parent.BIONE
																.commonOpenDialog(
																		title,
																		"taskViewWin",
																		500,
																		height,
																		"${ctx}/rpt/frs/rptfill/showTab?path=rptfill-todowork-sumexport&dataDate="
																				+ dataDate
																				+ "&orgNo="
																				+ orgNo
																				+ "&rptId="
																				+ rptId
																				+ "&logicRs="
																				+ logicRs
																				+ "&sumpartRs="
																				+ sumpartRs
																				+ "&warnRs="
																				+ warnRs
																				+ "&taskInsId="
																				+ taskInsId
																				+ "&lineId="
																				+ lineId
																				+ "&rptNm="
																				+ encodeURI(encodeURI(rptNm))
																				+ "&orgNm="
																				+ encodeURI(encodeURI(orgNm)),
																		null);
													});
								} else {
									BIONE.tip("数据异常，请联系系统管理员");
								}
							},
							error : function() {
								//BIONE.tip("数据异常，请联系系统管理员");
							}
						});
			} else {
				BIONE.tip("请选择一条任务记录");
			}
		},
		oper_view : function() {
			fileName = '';
			var manager = $("#maingrid").ligerGetGridManager();
			var rows = manager.getSelectedRows();

			if (rows.length == 1) {
				var data = rows[0];
				orgNm = data.exeObjNm;
				var rptId = data.taskObjId;
				var dataDate = data.dataDate;
				var orgNo = data.exeObjId;
				var lineId = data.lineId;
				$
						.ajax({
							cache : false,
							async : false,
							url : "${ctx}/rpt/frs/rptfill/getTmpId",
							dataType : 'text',
							data : {
								rptId : rptId,
								dataDate : dataDate,
								lineId : lineId
							},
							type : "post",
							success : function(result) {
								if (result) {
									BIONE
											.ajax(
													{
														async : false,
														url : "${ctx}/rpt/frs/rptfill/createColor",
														dataType : 'text',
														type : 'POST',
														data : {
															rptId : rptId,
															orgNo : orgNo,
															dataDate : dataDate,
															tmpId : result,
															isBatchCheck : true
														}
													},
													function(result) {
														var rptNm = data.taskObjNm;
														var title = "报表信息";
														var logicRs = data.logicRs;
														var sumpartRs = data.sumpartRs;
														var warnRs = data.warnRs;
														var taskInsId = data.taskInstanceId;
														var height = $(
																parent.parent.window)
																.height() - 10;
														var width = $(
																parent.parent.window)
																.width();
														window.top.color = result;
														//modify by wusb at 20161118 待定。。。
														window.parent.parent.BIONE
																.commonOpenDialog(
																		title,
																		"taskViewWin",
																		width,
																		height,
																		"${ctx}/rpt/frs/rptfill/showTab?path=rptfill-todowork-view&dataDate="
																				+ dataDate
																				+ "&orgNo="
																				+ orgNo
																				+ "&rptId="
																				+ rptId
																				+ "&logicRs="
																				+ logicRs
																				+ "&sumpartRs="
																				+ sumpartRs
																				+ "&warnRs="
																				+ warnRs
																				+ "&taskInsId="
																				+ taskInsId
																				+ "&lineId="
																				+ lineId
																				+ "&rptNm="
																				+ encodeURI(encodeURI(rptNm))
																				+ "&orgNm="
																				+ encodeURI(encodeURI(orgNm)),
																		null);
													});
								} else {
									BIONE.tip("数据异常，请联系系统管理员");
								}
							},
							error : function() {
								//BIONE.tip("数据异常，请联系系统管理员");
							}
						});
			} else {
				BIONE.tip("请选择一条任务记录");
			}
		},
		childPublish : function() {//二次下发
			var rows = grid.getSelectedRows();
			if (rows.length == 1) {
				var taskId = rows[0].taskId;
				var insId = rows[0].taskInstanceId;
				var upExeObjId = rows[0].exeObjId;
				var rptId = rows[0].taskObjId;
				var dataDate = rows[0].dataDate;
				if (rows[0].sts == "1") {//一次实例已提交
					BIONE.tip("任务已提交，不允许二次下发!");
					return;
				}
				// 1、 判断任务实例是否有二次任务，若有是否已下发
				$
						.ajax({
							async : false,
							dataType : "json",
							type : "post",
							url : '${ctx}/report/frs/rpttsk/rptTskPublishChildController.mo?_type=data_event&_field=isChildPublish&_event=POST&_comp=main&Request-from=dhtmlx&doFlag=rpt-todowork-list',
							data : {
								taskId : taskId,
								childTaskInsId : insId,
								upExeObjId : upExeObjId
							},
							beforeSend : function() {
								BIONE.loading = true;
								BIONE.showLoading("数据处理中...");
							},
							complete : function() {
								BIONE.loading = false;
								BIONE.hideLoading();
							},
							success : function(result) {
								if (result && result.msg != "") {
									BIONE.tip(result.msg);
									return;
								}
								// 2、二次下发
								$
										.ajax({
											async : false,
											dataType : "json",
											type : "post",
											url : '${ctx}/report/frs/rpttsk/rptTskPublishChildController.mo?_type=data_event&_field=childTskPublish&_event=POST&_comp=main&Request-from=dhtmlx&doFlag=rpt-todowork-list',
											data : {
												taskId : taskId,
												childTaskInsId : insId,
												upExeObjId : upExeObjId,
												rptId : rptId,
												dataDate : dataDate
											},
											beforeSend : function() {
												BIONE.loading = true;
												BIONE.showLoading("正在下发中...");
											},
											complete : function() {
												BIONE.loading = false;
												BIONE.hideLoading();
											},
											success : function(result) {
												if (result && result.msg
														&& result.msg != "") {
													BIONE.tip(result.msg);
													return;
												}
												/* grid.set("parms",{
												orgType:parent.orgType,
												orgNo:parent.orgNo
												});
												grid.loadData(); */
												BIONE.tip('下发成功');
											},
											error : function(result, b) {
												BIONE.tip('错误 <BR>错误码：'
														+ result.status);
											}

										});
							},
							error : function(result, b) {
								BIONE.tip('错误 <BR>错误码：' + result.status);
							}

						});

			} else {
				BIONE.tip("请选择一条记录");
			}
		},

		banchImportData : function() {
			BIONE
					.commonOpenDialog(
							'选择筛选条件',
							'banchImpWin',
							600,
							430,
							"${ctx}/rpt/frs/rptfill/showTab?path=rpt-todowork-list-condition&operType=imp&importFileType=zip");
		},

		importData : function() {
			var rows = grid.getSelectedRows();
			if (rows.length == 0) {
				BIONE.tip("请选择一个任务进行导入！");
				return;
			} else if (rows.length == 1) {
				if (rows[0].sts == "1") {
					BIONE.tip("该任务已提交，不可以导入");
				} else {
					var data = rows[0];
					var rptId = data.taskObjId;
					var dataDate = data.dataDate;
					var orgNo = data.exeObjId;
					var lineId = data.lineId;
					var taskInsId = data.taskInstanceId;
					var type = data.taskType;
					var taskId = data.taskId;
					BIONE.commonOpenDialog('导入数据文件', 'uploadWin', 600, 330,
							"${ctx}/rpt/frs/rptfill/showTab?path=rpt-todowork-uploadfile&dataDate="
									+ dataDate + "&orgNo=" + orgNo + "&rptId="
									+ rptId + "&taskInsId=" + taskInsId
									+ "&lineId=" + lineId + "&type=" + type
									+ "&flag=ONE&entry=GRID" + "&taskId="
									+ taskId + "&taskFillOperType=35&operType="
									+ rptOperType);
				}
			} else {
				var data = rows[0];
				var rptId = data.taskObjId;
				var dataDate = data.dataDate;
				var orgNo = data.exeObjId;
				var lineId = data.lineId;
				var taskInsId = data.taskInstanceId;
				var type = data.taskType;
				var taskId = data.taskId;
				var firstTaskId = taskId;

				var submitName = "";
				for ( var i in rows) {
					if (rows[i].sts != "0") {
						submitName = rows[i].taskNm;
					}
					if (rows[i].taskId != firstTaskId) {
						BIONE.tip("只能选择一个任务进行导入！");
						return;
					}
				}
				if (submitName && submitName != null && submitName != "") {
					BIONE.tip("任务[" + submitName + "]不是未提交状态,无法修改或导入");
					return;
				}
				BIONE.commonOpenDialog('导入数据文件', 'uploadWin', 600, 330,
						"${ctx}/rpt/frs/rptfill/showTab?path=rpt-todowork-uploadfile&dataDate="
								+ dataDate + "&orgNo=" + orgNo + "&rptId="
								+ rptId + "&taskInsId=" + taskInsId
								+ "&lineId=" + lineId + "&type=" + type
								+ "&flag=ONE&entry=GRID" + "&taskId=" + taskId
								+ "&taskFillOperType=35&operType="
								+ rptOperType);
			}
		},
		importMoreData : function() {
			//var manager = $("#maingrid").ligerGetGridManager();
			var rows = grid.getSelectedRows();
			if (rows.length > 0) {
				var argsArr = [];
				for ( var i in rows) {
					if (rows[i].sts == "1") {
						BIONE.tip("有任务已提交，请检查!");
					} else {
						//var data = rows[i];
						var rptId = rows[i].taskObjId;
						var dataDate = rows[i].dataDate;
						var orgNo = rows[i].exeObjId;
						var lineId = rows[i].lineId;
						var taskInsId = rows[i].taskInstanceId;
						var type = rows[i].taskType;
						var taskId = rows[i].taskId;
						var argsArr1 = [];
						var args1 = {
							'DimNo' : 'ORG',
							'Op' : '=',
							'Value' : orgNo
						};
						argsArr1.push(args1);
						var args = {
							'rptId' : rptId,
							'dataDate' : dataDate,
							'lineId' : lineId,
							'taskInsId' : taskInsId,
							'type' : type,
							'taskId' : taskId,
							'orgNo' : orgNo,
							'searchArgs' : JSON2.stringify(argsArr1)
						};
						argsArr.push(args);
					}

				}
				BIONE
						.commonOpenDialog('导入数据文件', 'uploadWin', 600, 330,
								"${ctx}/rpt/frs/rptfill/showTab?path=rpt-todowork-uploadfile-child&argsArr="
										+ encodeURI(encodeURI(JSON2
												.stringify(argsArr))));

			} else {
				BIONE.tip("请选择导入记录");
			}
		},
		queryDownLoadList : function() {
			var form = $('#formsearch');
			var data = {};
			if ($.ligerui.get("logicRsSts_sel") && $.ligerui.get("logicRsSts_sel").getValue()) {
				data.logicRsSts = $.ligerui.get("logicRsSts_sel").getValue();
			}
			if ($.ligerui.get("sumpartRsSts_sel") && $.ligerui.get("sumpartRsSts_sel").getValue()) {
				data.sumpartRsSts = $.ligerui.get("sumpartRsSts_sel")
						.getValue();
			}
			if ($.ligerui.get("warnRsSts_sel") && $.ligerui.get("warnRsSts_sel").getValue()) {
				data.warnRsSts = $.ligerui.get("warnRsSts_sel").getValue();
			}
			if ($.ligerui.get("zeroRsSts_sel") && $.ligerui.get("zeroRsSts_sel").getValue()) {
				data.zeroRsSts = $.ligerui.get("zeroRsSts_sel").getValue();
			}
			var rule = BIONE.bulidFilterGroup(form);
			if (rule.rules.length) {
				data.condition = JSON2.stringify(rule);
			} else {
				data.condition = "";
			}
			return data;
		},
		queryRptTaskList : function(queryType) {
			var form = $('#formsearch');
			if (queryType && queryType == "1") {
				//收藏查询
				grid.setParm("queryFav", "1");
			} else {
				grid.removeParm("queryFav");
			}
			if ($.ligerui.get("logicRsSts_sel") && $.ligerui.get("logicRsSts_sel").getValue()) {
				grid.setParm("logicRsSts", $.ligerui.get("logicRsSts_sel")
						.getValue());
			} else {
				grid.removeParm("logicRsSts");
			}
			if ($.ligerui.get("sumpartRsSts_sel") && $.ligerui.get("sumpartRsSts_sel").getValue()) {
				grid.setParm("sumpartRsSts", $.ligerui.get("sumpartRsSts_sel")
						.getValue());
			} else {
				grid.removeParm("sumpartRsSts");
			}
			if ($.ligerui.get("warnRsSts_sel") && $.ligerui.get("warnRsSts_sel").getValue()) {
				grid.setParm("warnRsSts", $.ligerui.get("warnRsSts_sel")
						.getValue());
			} else {
				grid.removeParm("warnRsSts");
			}
			if ($.ligerui.get("zeroRsSts_sel") && $.ligerui.get("zeroRsSts_sel").getValue()) {
				grid.setParm("zeroRsSts", $.ligerui.get("zeroRsSts_sel")
						.getValue());
			} else {
				grid.removeParm("zeroRsSts");
			}
			var rule = BIONE.bulidFilterGroup(form);
			if (rule.rules.length) {
				grid.setParm("condition", JSON2.stringify(rule));
				grid.setParm("newPage", 1);
				grid.options.newPage=1
			} else {
				grid.setParm("condition", "");
				grid.setParm('newPage', 1);
				grid.options.newPage=1
			}
			grid.loadData();
		},
		initButtons : function() {
			// modify by wusb at 20161201 设置为共公变量
			//var pt = "${pt}";
			if (pt == "02") {
				var btns = [ {
					text : '批量导出',
					click : tmp.oper_fdown,
					icon : 'export',
					operNo : 'oper_fdown'
				}, {
					text : '查看收藏',
					click : tmp.queryFav,
					icon : 'export',
					operNo : 'queryFav'
				}, {
					text : '设置收藏',
					click : tmp.setFav,
					icon : 'export',
					operNo : 'setFav'
				}, {
					text : '列表导出',
					click : tmp.list_down,
					icon : 'export',
					operNo : 'list_down'
				} ];
			} else if (pt == "021") {
			}//历史查询页面按钮显示  孔渊博 20170210
			else {
				var btns = [ {
					text : '提交',
					click : tmp.oper_submit,
					icon : 'fa-inbox',
					operNo : 'oper_submit'
				}, {
					text : "报表计算",
					icon : "fa-calculator",
					operNo : "rpt_calculation",
					menu : {
						items : [ {
							text : '表间计算',
							click : tmp.oper_quote_rptCalculation,
							icon : 'modify'
						}, {
							text : '机构汇总',
							click : tmp.oper_sumDataType,
							icon : 'modify'
						} ]
					}
				}, {
					text : '批量导入',
					click : tmp.banchImportData,
					icon : 'fa-upload',
					operNo : 'importData'
				}, {
					text : '批量导出',
					click : tmp.banchExportData,
					icon : 'fa-download',
					operNo : 'oper_fdown'
				}, {
					text : '列表导出',
					click : tmp.list_down,
					icon : 'fa-download',
					operNo : 'list_down'
				}, {
					text : '逻辑校验',
					click : function() {
						tmp.check_single("logic");
					},
					icon : 'fa-balance-scale',
					operNo : 'oper_logic'
				}, {
					text : '预警校验',
					click : function() {
						tmp.check_single("warn");
					},
					icon : 'fa-balance-scale',
					operNo : 'oper_warn'
				}, {
					text : '零值校验',
					click : function() {
						tmp.check_single("zero");
					},
					icon : 'fa-balance-scale',
					operNo : 'oper_zero'
				}, {
					text : '总分校验',
					click : function () {
						tmp.check_single("sumpart");
					},
					icon : 'fa-balance-scale',
					operNO : 'oper_sumpart'
				}];
				
				//1104模块，去除总分校验
				<%--if('02' != "${moduleType}"){--%>
				// 	btns.push({
				// 		text : '总分校验',
				// 		click : function() {
				// 			tmp.check_single("sumpart");
				// 		},
				// 		icon : 'fa-balance-scale',
				// 		operNo : 'oper_sumpart'
				// 	});
				// }
				btns.push({
					text : '强制提交',
					click : tmp.oper_forceSubmit,
					icon : 'fa-inbox',
					operNo : 'oper_forceSubmit'
				});
			}
			BIONE.loadToolbar(grid, btns, function() {
			});
		},
		oper_forceSubmit : function(){
			tmp.oper_submit("force")
		},
		
		// 创建表单搜索按钮：搜索、高级搜索
		addMySearchButtons : function(form, grid, btnContainer) {
			if (!form)
				return;
			form = $(form);
			if (btnContainer) {
				BIONE.createButton({
					appendTo : btnContainer,
					text : '查询',
					icon : 'fa-search',
					click : function() {
						tmp.queryRptTaskList();
					}
				});
				BIONE
						.createButton({
							appendTo : btnContainer,
							text : '重置',
							icon : 'fa-repeat',
							click : function() {
								$(":input", form)
										.not(
												":submit, :reset,:hidden,:image,:button, [disabled]")
										.each(function() {
											$(this).val("");
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
													grid
															.removeParm("logicRsSts");
													grid
															.removeParm("sumpartRsSts");
													grid
															.removeParm("warnRsSts");
													//modify by wusb at 20161118  零值校验查询
													grid
															.removeParm("zeroRsSts");

													//modfify by wusb at 20161201   是否总表 
													grid
															.removeParm("isMainRptSts");

												});
								$(":input[ltype=select]", form)
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
													grid
															.removeParm("logicRsSts");
													grid
															.removeParm("sumpartRsSts");
													grid
															.removeParm("warnRsSts");
													//modify by wusb at 20161118  零值校验查询
													grid
															.removeParm("zeroRsSts");
													//modfify by wusb at 20161201   是否总表 
													grid
															.removeParm("isMainRptSts");
												});
							}
						});
			}
		},
		//报表excel公式计算
		oper_calculation_excel : function(){
			var manager = $("#maingrid").ligerGetGridManager();
			var rows = manager.getSelectedRows();
			if (rows.length > 0) {
				for (var i = 0; i < rows.length; i++) {
					initDesign(rows[i].taskObjId, rows[i].dataDate, rows[i].exeObjId, rows[i].taskInstanceId, rows[i].taskObjNm, rows[i].exeObjNm);
				}
			}
		},
		
		//报表计算(只计算表间取数)
		oper_quote_rptCalculation : function() {
			var manager = $("#maingrid").ligerGetGridManager();
			var rows = manager.getSelectedRows();
			var params = [];
			var detailCount = 0;
			if (rows.length > 0) {
				for (var i = 0; i < rows.length; i++) {
					if("01" == rows[i].templateType){
						detailCount ++;
					}else{
						var param = {
							rptId : rows[i].taskObjId, 
							dataDate : rows[i].dataDate, 
							orgNo : rows[i].exeObjId, 
							taskInsId : rows[i].taskInstanceId, 
							operType : rows[i].taskType, 
							rptTmpId : rows[i].templateId
						};
						params.push(param);
					}
				}
			}
			var msg = "";
			if(detailCount < rows.length){
				msg = "您勾选的报表中包含"+detailCount+"个明细报表,系统将跳过这些表进行计算，表间计算开始！";
				BIONE.showSuccess(msg);
				for(var i=0; i< params.length; i++){
					$.ajax({
						async : true,
						url : "${ctx}/rpt/frs/rptfill/rptCalculation",
						dataType : 'json',
						type : 'POST',
						data : {
							rptId : params[i].rptId,
			 				orgNo : params[i].orgNo,
							dataDate : params[i].dataDate,
							taskInsId : params[i].taskInsId,
							operType : params[i].operType,
							rptTmpId : params[i].rptTmpId
						}, 
						beforeSend : function() {
							BIONE.showLoading("报表计算中，请稍等...");
						},
						complete: function(){
							BIONE.hideLoading();
						},
						success: function(result){
							if(result.error == ""){
								BIONE.tip("报表计算成功");
							}else{
								BIONE.tip(result.error);
							}
						},
						error: function(){
							BIONE.tip("报表计算失败，请查看日志");
						}
					});
				}
			}else if(detailCount == rows.length){
				msg = "明细报表不用进行表间计算！";
				BIONE.showSuccess(msg);
				return;
			}
		}
	};
	// 初始化设计器
	function initDesign(rptId, dataDate, orgNo, taskInsId, rptNm, orgNm) {
		$("#center").append('<div id="spread'+ rptId + dataDate + orgNo +'" style="width:0;height:0;"></div>');
		var argsArr = [];
		var args = {
			'DimNo' : 'ORG',
			'Op' : '=',
			'Value' : orgNo
		};
		argsArr.push(args);
		var targetHeight = 0;
		var settings = {
			targetHeight : targetHeight,
			ctx : "${ctx}",
			readOnly : false,
			toolbar : false,
			canUserEditFormula : false,
			initFromAjax : true,
			isloadSave : true,
			searchArgs : JSON2.stringify(argsArr),
			ajaxData : {
				rptId : rptId,
				dataDate : dataDate,
				orgNo : orgNo,
				taskInsId : taskInsId
			},
			loadCompleted : gridLoad
		};
		$("#spread"+ rptId + dataDate + orgNo).view(settings);
	};
	
	function gridLoad(){
		grid.loadData();
	}
	function showNotUptCells(){
		BIONE.commonOpenDialog("导入结果", "notUptCellInfo",$(document).width() - 100,$(document).height() - 100, "${ctx}/rpt/frs/rptfill/showNotUptCells?type=batch");
	}
</script>
</head>
<body>
</body>
</html>