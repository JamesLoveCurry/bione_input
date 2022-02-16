<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<meta name="decorator" content="/template/template1_BS.jsp">
<script>
(function($) {
	//审批状态渲染
	CollateStsRender= function(rowdata) {
	if (rowdata.collateSts == '0') { return "未审批";} else if (rowdata.collateSts == '1') { return "同意";} else if (rowdata.collateSts == '2') { return "不同意";} else { return rowdata.collateSts;}
	};
	// 任务状态渲染(加入"结束填报")
	HandStsRender = function(rowdata) {
		if (rowdata.sts == '0') { return "未提交";} else if (rowdata.sts == '1') { return "待复核";} else if (rowdata.sts == '2') { return "待审核";} else if (rowdata.sts == '3') { return "流程完成";} else { return rowdata.sts;}
	};
	// 任务状态渲染(加入"结束填报")
	FinishStsRender = function(rowdata) {
			if (rowdata.sts == '0') { return "未提交";} else if (rowdata.sts == '1') { return "待复核";} else if (rowdata.sts == '2') { return "待审核";} else if (rowdata.sts == '3') { return "流程完成";}  else if (rowdata.sts == '4') { return "已完成";}else { return rowdata.sts;}

	};
	// 报表状态渲染
	UpdateStsRender = function(rowdata) {
		if (rowdata.isUpt == '0') { return "未修改";} else if (rowdata.isUpt == '1') { return "已修改";} else { return rowdata.sts;}
	};
	// 校验状态通用渲染方法
	CommonRender = function(isCheck, rs, taskType, type) {
		var result = "未校验";
		if(type == 'sumpart'){
			if(taskType == '02'){//1104
				result = "-";
			}
		}
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
		return result;
	};
	// 总分校验校验状态渲染
	SumpartRsRender = function(rowdata) {
		return CommonRender(rowdata.isCheck, rowdata.sumpartRs, rowdata.taskType, 'sumpart');
	};
	// 逻辑校验校验状态渲染
	LogicRsRender = function(rowdata) {
		//明细报表逻辑校验状态转换，因为综合类报表可能有俩种校验（指标和明细）
		if("01" == rowdata.templateType || "03" == rowdata.templateType){
			//有一个失败，那就都失败
			if(rowdata.logicRs == "04" || rowdata.deLogicSts == "04"){
				rowdata.logicRs = "04"
			}else if(rowdata.logicRs == "06" || rowdata.deLogicSts == "06"){//有一个未通过，那就都未通过
				rowdata.logicRs = "06"
			}
		}
		return CommonRender(rowdata.isCheck, rowdata.logicRs, rowdata.taskType, 'logic');
	};
	// 警戒值校验校验状态渲染
	WarnRsRender = function(rowdata) {
		return CommonRender(rowdata.isCheck, rowdata.warnRs, rowdata.taskType, 'warn');
	};
	
	//modify by wusb at 20161207 添加零值校验
	ZeroRsRender = function(rowdata) {
		return CommonRender(rowdata.isCheck, rowdata.zeroRs, rowdata.taskType, 'zero');
	};
	
	var operType = "${operType}";
	//报表名称链接
	taskObjNmRender = function(rowdata) {
		var aa = rowdata.taskObjNm;
		var bb =aa;
		return "<a href='javascript:void(0)' class='link' onclick='onShowRpt(\""+ rowdata.sts+"\",\"" + rowdata.taskObjId+"\",\"" + rowdata.taskObjNm+"\",\"" + rowdata.dataDate+"\",\"" + rowdata.exeObjId+"\",\"" + rowdata.exeObjNm+"\",\"" + rowdata.lineId+"\",\"" + rowdata.zeroRs+"\",\"" + rowdata.warnRs+"\",\"" + rowdata.sumpartRs+"\",\"" + rowdata.logicRs+"\",\"" + rowdata.taskInstanceId+"\",\"" + rowdata.taskType+"\", \"" + rowdata.taskId+"\", \"" + rowdata.templateType+ "\", \"" + rowdata.templateId+"\", \"" + rowdata.fixedLength+"\", \"" + rowdata.isPaging+"\")'>"+ bb + "</a>"; 
	};
	//modify by wusb at 20161207 修改下面的参数保持和传进去的参数顺序一致
	onShowRpt = function(sts, taskObjId, taskObjNm, dataDate, exeObjId, exeObjNm, lineId, zeroRs, warnRs, sumpartRs, logicRs, taskInstanceId, taskType, taskId, templateType, templateId, fixedLength, isPaging){
			$.ajax({
				cache : false,
				async : true,
				url : "${ctx}/rpt/frs/rptfill/getTmpId",
				dataType : 'text',
				data : {
					rptId : taskObjId, dataDate : dataDate, lineId : lineId, operType : operType
				},
				type : "post",
				success : function(result){
					if(result){
 						BIONE.ajax({
 							async : false,
 							url : "${ctx}/rpt/frs/rptfill/createColor",
 							dataType : 'text',
 							type : 'POST',
 							data : {
 								rptId : taskObjId,
 								orgNo : exeObjId,
 								dataDate : dataDate,
 								tmpId : result,
								isBatchCheck : true
 							}
 						}, function(result) { 
 							var title = "当前报表:" + taskObjNm;
 							var height = $(parent.parent.parent.window).height();
 							var width = $(parent.parent.parent.window).width() + 10;
 							window.top.color=result;
 							window.parent.BIONE.commonOpenDialog(title, "taskViewWin", width, height, "${ctx}/frs/rptsubmit/submit/dataFillView?dataDate=" + dataDate + "&orgNo=" + exeObjId + "&rptId=" + taskObjId + "&logicRs=" + logicRs + "&sumpartRs=" + sumpartRs + "&warnRs=" + warnRs + "&zeroRs=" + zeroRs + "&taskInsId=" + taskInstanceId + "&lineId=" + lineId + "&rptNm=" + encodeURI(encodeURI(taskObjNm)) +  "&orgNm=" + encodeURI(encodeURI(exeObjNm)) + "&taskId=" + taskId + "&operType=" + operType+"&taskType="+taskType+"&templateType="+templateType+"&templateId="+templateId+"&fixedLength="+fixedLength+"&isPaging="+isPaging+"&d="+new Date(), null,function(data){
 								if(data&&data=="refresh")
 									grid.loadData();
 							});
 						});
					}else{
						BIONE.tip("数据异常，请联系系统管理员");
					}
				},
				error:function(){
					BIONE.tip("数据异常，请联系系统管理员");
				}
			});
		
	};
	//初始化列对象
	GridObject = {
			"sts" : {display : '报表状态', name : 'sts', minWidth : 50, width : 60, render : HandStsRender},
			"finishSts" : {display : '报表状态', name : 'sts', minWidth : 50, width : 60, render : FinishStsRender},
			"dataDate" : {display : '数据日期', name : 'dataDate', minWidth : 50, width : 80},
			"exeObjId" : {display : '机构编码', name : 'exeObjId', minWidth : 50, width : 60},
			"exeObjNm" : {display : '机构名称', name : 'exeObjNm', minWidth : 80, width : 120},
			"taskNm" : {display : '任务名称', name : 'taskNm', minWidth : 130, width : 130},
			"taskObjNm" : {display : '报表名称', name : 'taskObjNm', minWidth : 400, width : 400,align: 'left', render : taskObjNmRender},
			"endTime" : {display : '截止时间', name : 'endTime', minWidth : 150, width : 60, type : 'date',format:'yyyyMMdd'},
			"isUpt" : {display : '报表状态', name : 'isUpt', minWidth : 50, width : 60, render : UpdateStsRender},
			"sumpartRs" : {display : '总分校验', name : 'sumpartRs', minWidth : 50, width : 60, render : SumpartRsRender},
			"logicRs" : {display : '逻辑校验', name : 'logicRs', minWidth : 50, width : 60, render : LogicRsRender},
			"warnRs" : {display : '预警校验', name : 'warnRs', minWidth : 50, width : 60, render : WarnRsRender},
			"zeroRs" : {display : '零值校验', name : 'zeroRs', minWidth : 50, width : 60, render : ZeroRsRender},
			"exeObjNmChild" : {display : '责任人', name : 'exeObjNm', minWidth : 50, width : 80},
			"applyUserNm" : {display : '申请人', name : 'applyUserNm', minWidth : 50, width : 60},
			"applyTime" : {display : '申请时间', name : 'applyTime', minWidth : 80, width : 180, type : 'date',format:'yyyy-MM-dd hh:mm:ss'},
			"collateUserNm" : {display : '审批人', name : 'collateUserNm', minWidth : 50, width : 60},
			"collateTime" : {display : '审批时间', name : 'collateTime', minWidth : 80, width : 180, type : 'date',format:'yyyy-MM-dd hh:mm:ss'},
			"collateSts" : {display : '审批状态', name : 'collateSts', minWidth : 50, width : 60, render : CollateStsRender},
			"sumSts" : {display : '汇总状态', name : 'sumSts', minWidth : 50, width : 60},
			"lineNm" : {display : '条线名称', name : 'lineNm', minWidth : 100, width : 100},
			"rowNum" : {display : '行数', name : 'rowNum', minWidth : 50, width : 60},
			"colNum" : {display : '列数', name : 'colNum', minWidth : 50, width : 60},
			"defuVal" : {display : '初始值', name : 'defuVal', minWidth : 50, width : 60},
			"befUpVal" : {display : '改前值', name : 'befUpVal', minWidth : 50, width : 60},
			"aftUpVal" : {display : '改后值', name : 'aftUpVal', minWidth : 50, width : 60},
			"fileVal" : {display : '归档值', name : 'fileVal', minWidth : 50, width : 60},
			"oprUser" : {display : '操作用户', name : 'oprUser', minWidth : 60, width : 80},
			"oprTime" : {display : '操作时间', name : 'oprTime', minWidth : 50, width : 100, type : 'date',format:'yyyy-MM-dd hh:mm:ss'},
			"taskId" : {display : '任务Id', name : 'taskId', isAllowHide : false, hide : true, minWidth : 1, width : 1}
	};
	//初始化搜索表单对象
	//调整搜索输入框宽度 edit by lxp 20161129
	var SearchFormObject = {
			"handSts" : {display : "报表状态", name : "handSts", newline : false, type : "select",width : '140', cssClass : "field", labelWidth : '90',
						comboboxName : "handSts_sel", attr : { op : "=", field : "i.sts"},
						options : { data : [ { text : '待复核', id : "1"}, { text : '待审核', id : "2"}, { text : '流程完成', id : "3"} ], cancelable  : true}
					},
			"finishSts" : {display : "报表状态", name : "finishSts", newline : false, type : "select", cssClass : "field", labelWidth : '90',
						comboboxName : "finishSts_sel", attr : { op : "=", field : "i.sts"},
						options : { data : [ { text : '未提交', id : "0"}, { text : '待复核', id : "1"}, { text : '待审核', id : "2"},{ text : '流程完成', id : "3"} ], cancelable  : true}
					},
			"updateSts" : {display : "报表状态", name : "updateSts", newline : false, type : "select", cssClass : "field", labelWidth : '90',
						comboboxName : "updateSts_sel", attr : { op : "=", field : "i.isUpt"},
						options : { data : [ { text : '未修改', id : "0"}, { text : '已修改',id : "1"} ], cancelable  : true}
					},
			"logicRsSts" : {display : "逻辑校验状态", name : "logicRsSts", newline : false, type : "select",width : '140', cssClass : "field", labelWidth : '90',
						comboboxName : "logicRsSts_sel", //attr : { op : "=", field : "sl.checkSts"},
						options : { data : [  { text : '未校验', id : "00"}, { text : '校验通过', id : "05"}, { text : '校验未通过', id : "06"}, { text : '校验失败', id : "04"} ], cancelable  : true}
					},
			"sumpartRsSts" : {display : "总分校验状态", name : "sumpartRsSts", newline : false, type : "select",width : '140', cssClass : "field", labelWidth : '90',
						comboboxName : "sumpartRsSts_sel", //attr : { op : "=", field : "ss.checkSts"},
						options : { data : [  { text : '未校验', id : "00"}, { text : '准备校验', id : "01"}, { text : '校验中', id : "02"}, { text : '校验成功', id : "03"}, { text : '校验失败', id : "04"}, { text : '通过', id : "05"}, { text : '未通过', id : "06"} ], cancelable  : true}
					},
			"warnRsSts" : {display : "预警校验状态", name : "warnRsSts", newline : false, type : "select",width : '140', cssClass : "field", labelWidth : '90',
						comboboxName : "warnRsSts_sel", //attr : { op : "=", field : "sw.checkSts"},
						options : { data : [{ text : '未校验', id : "00"}, { text : '准备校验', id : "01"}, { text : '校验中', id : "02"}, { text : '校验成功', id : "03"}, { text : '校验失败', id : "04"}, { text : '通过', id : "05"}, { text : '未通过', id : "06"} ], cancelable  : true}
					},
			"zeroRsSts" : {display : "零值校验状态", name : "zeroRsSts", newline : false, type : "select",width : '140', cssClass : "field", labelWidth : '90',
						comboboxName : "zeroRsSts_sel", //attr : { op : "=", field : "sz.checkSts"},
						options : { data : [{ text : '未校验', id : "00"}, { text : '准备校验', id : "01"}, { text : '校验中', id : "02"}, { text : '校验成功', id : "03"}, { text : '校验失败', id : "04"}, { text : '通过', id : "05"}, { text : '未通过', id : "06"} ], cancelable  : true}
					},
			"dataDate" : {display : "数据日期", name : "dataDate", newline : false, type : "date",width : '140', cssClass : "field", labelWidth : '90',
						attr : { op : "=", field : "i.data_date"},
						options : { format : "yyyyMMdd"}
					},
			"collateSts" : {display : "审批状态", name : "collateSts", newline : false, type : "select", cssClass : "field", labelWidth : '90',
						comboboxName : "collateSts_sel", attr : { op : "=", field : "re.sts"},
						options : { data : [ { text : '未审批', id : "0"}, { text : '同意', id : "1"}, { text : '不同意', id : "2"} ], cancelable  : true}
					},
			"orgNm" : {display : "机构名称", name : "orgNm", newline : false, type : "select",width : '140', cssClass : "field", labelWidth : '90',
						comboboxName : "orgNm_sel", attr : { op : "in", field : "exeObjId"} 
					},
			"taskNm" : {display : "任务名称", name : "taskNm", newline : false, type : "select",width : '140', cssClass : "field", labelWidth : '90',
						comboboxName : "taskNm_sel", attr : { op : "=", field : "i.task_id"}
					},
			"rptNm" : {display : "报表名称", name : "rptNm", newline : false, type : "select",width : '140', cssClass : "field", labelWidth : '90',
						comboboxName : "rptNm_sel", attr : { op : "=", field : "i.taskObjId"}
					},
			"lineNm" : {display : "条线名称", name : "lineNm", newline : false, type : "select", cssClass : "field", labelWidth : '90',
						comboboxName : "lineNm_sel", attr : { op : "=", field : "li.lineId"}
					},
			"oprNm" : {display : "操作用户", name : "oprNm", newline : false, type : "select", cssClass : "field", labelWidth : '90',
					comboboxName : "oprNm_sel", attr : { op : "=", field : "li.lineId"}
					},
			"beginDate" : {display : "开始日期", name : "beginDate", newline : false, type : "date", cssClass : "field", labelWidth : '90',
						attr : { op : "=", field : "i.begin_date"},
						options : { format : "yyyyMMdd"}
					},
			"endDate" : {display : "结束日期", name : "endDate", newline : false, type : "date", cssClass : "field", labelWidth : '90',
						attr : { op : "=", field : "i.end_date"},
						options : { format : "yyyyMMdd"}
					}
	};
	//生成子对象
	CreateChildObject = function(commonObject, sourceArr) {
		var resultArr = [];
		for(var i in sourceArr){ resultArr.push(commonObject[sourceArr[i]]);}
		return resultArr;
	};
	//机构树
	CommonOrgTree = function(orgTreeUrl) {
		var setting = {
			async : {
				enable : true, url : orgTreeUrl, autoParam : [ "id" ], dataType : "json", type : "post",
				dataFilter : function(treeId, parentNode, childNodes) {
					if (childNodes) {
						for ( var i = 0; i < childNodes.length; i++) {
							childNodes[i].id = childNodes[i].params.realId;
							childNodes[i].upId = childNodes[i].upId;
							childNodes[i].nodeType = childNodes[i].params.type;
							childNodes[i].isParent = childNodes[i].params.isParent == "true" ? true : false;
						}
					}
					return childNodes;
				}
			},
			data : { key : { name : "text"}, simpleData : {enable : true, idKey : "id", pIdKey : "upId"}}, view : { selectedMulti : false},
			callback : { onNodeCreated : function(event, treeId, treeNode) {
											if (treeNode.id == "ROOT") {
												// 若是根节点，展开下一级节点
												taskOrgTree.reAsyncChildNodes(treeNode, "refresh");
											}
										}
			}
		};
		window['taskOrgTree'] = $.fn.zTree.init($("#tree"), setting);
	};
	//树按钮
	CommonTreeButton = function() {
		var btns =[];
		btns.push(
				{ text : "取消", onclick : function(){BIONE.closeDialog("taskOrgWin");}},	
				{ text : "选择", onclick : function() {
						var nodes = taskOrgTree.getSelectedNodes();
						if("ROOT" == nodes[0].id){
							BIONE.tip("根节点不可以选择！");
						}else{
							var c = window.parent.jQuery.ligerui.get("orgNm_sel");
							c._changeValue(nodes[0].id, nodes[0].text);
							BIONE.closeDialog("taskOrgWin");
						}
					}
			 }
		);
		BIONE.addFormButtons(btns);
	};
	//GRID
	//isOnDbClick:true-可以双击行 false-不可以双击行
	TaskFillGrid = function(gridUrl, columnSource, isOnDbClick, callBack, checkbox , delayLoad ,sortName,operType) {
		if(operType!=null&&columnSource!=null){
			if(operType=="01"){
				GridObject["finishSts"].display = "报表状态";
			}else if(operType=="02"){
				GridObject["finishSts"].display = "报表状态";
			}else{
				GridObject["finishSts"].display = "报表状态";
			}
		}else{
			GridObject["finishSts"].display = "报表状态";
		}
		var columns = CreateChildObject(GridObject, columnSource);
		var eles = {height : '99%', width : '100%',
					columns : columns,
					checkbox : true, usePager : true, pageSize : 20, isScroll : true, rownumbers : true,
					alternatingRow : true, colDraggable : true, dataAction : 'server',
					method : 'post', url : gridUrl, sortName : sortName?sortName:'i.data_date, i.exe_obj_id, i.task_obj_id, i.task_id', 
					sortOrder : 'desc', toolbar : {}, enabledSort : false, delayLoad : delayLoad?  delayLoad: false, selectRowButtonOnly:true };
		if(isOnDbClick){ eles["onDblClickRow"] = callBack;}
		if(checkbox){ eles["checkbox"] = false;}
		grid = $("#maingrid").ligerGrid(eles);
	};
	//SearchForm
	CommonSerchForm = function(taskComBoBoxUrl, rptComBoBoxUrl, orgTreeSkipUrl, lineComBoBoxUrl, fieldSource, flag, newLineNum,operType) {
		if(operType!=null&&fieldSource!=null){
			if(operType=="01"){
			SearchFormObject["finishSts"].display = "复核状态";
			SearchFormObject["finishSts"].options.data= [ { text : '未复核', id : "1"}, { text : '待审核', id : "2"} ];
			}else if(operType=="02"){
				SearchFormObject["finishSts"].display = "审核状态";
				SearchFormObject["finishSts"].options.data= [ { text : '未审核', id : "2"}, { text : '流程完成', id : "3"} ];
			}
		}
		if(taskComBoBoxUrl){
			SearchFormObject["taskNm"].options = {
					valueFieldID : "taskId", url : taskComBoBoxUrl, ajaxType : "get",
					// 联动报表
					onSelected : function(value) {
						if ("" != value) {
							$.ajax({
								async : false, type : "post", url : rptComBoBoxUrl, dataType : "json",
								data : { "taskId" : value, "flag" : flag},
								success : function(rptData) { $.ligerui.get("rptNm_sel").setValue(""); $.ligerui.get("rptNm_sel").setData(rptData);}
							});
						}
					}, cancelable  : true,dataFilter : true
				};
		}
		if(rptComBoBoxUrl){
			SearchFormObject["rptNm"].options = {
					onBeforeOpen : function() {
						var taskId = $.ligerui.get("taskNm_sel").getValue();
						if ("" == taskId) {
							$.ligerui.get("rptNm_sel").setValue("");
							$.ligerui.get("rptNm_sel").setData("");
							BIONE.tip("请选择任务");
						}
					}, cancelable  : true,dataFilter : true
				};
		}
		if(lineComBoBoxUrl){
			SearchFormObject["lineNm"].options = {
					valueFieldID : "lineId", url : lineComBoBoxUrl, ajaxType : "get", cancelable  : true
				};
		}
		//添加机构名称的options属性
		if(orgTreeSkipUrl){
			SearchFormObject["orgNm"].options = {
					onBeforeOpen : function() {
						var taskId = $.ligerui.get("taskNm_sel").getValue();
						var height = $(window).height() - 120;
						var width = $(window).width() - 480;
						window.BIONE.commonOpenDialog("机构树", "taskOrgWin", width, height, orgTreeSkipUrl + "&taskId=" + taskId, null);
						return false;
					}, cancelable  : true
				};
		}
		//修改换行
		
		if(newLineNum != null){
			for(var j = 0; j < fieldSource.length; j = j + newLineNum){
				SearchFormObject[fieldSource[j]].newline = true;
			}
		}
		var fields = CreateChildObject(SearchFormObject, fieldSource);
//		//拼接数组
		$("#search").ligerForm({ fields : fields});
	};
	// 查询表单的搜索按钮
	InitSearchButtons = function() {
		BIONE.addSearchButtons("#search", grid, "#searchbtn");
	};

})(jQuery);
</script>
<script type="text/javascript">
	var moduleType="${moduleType}";
	var gridUrl = "${ctx}/frs/rptfill/reject/flTaskList?orgTypes=${orgTypes}&moduleType=${moduleType}&operType=${operType}";
	var taskComBoBoxUrl = "${ctx}/frs/rptfill/reject/taskNmComBoBox?orgTypes=${moduleType}&flag=1";//一次任务
	var rptComBoBoxUrl = "${ctx}/frs/rptfill/reject/rptNmComBoBox";
	var orgTreeSkipUrl = "${ctx}/frs/integratedquery/rptquery/searchOrgSetInfo?orgType=" + moduleType;
	//modify by wusb at 20161207 添加零值校验  zeroRs
	var columns = [ "taskNm", "dataDate", "endTime", "taskObjNm", "exeObjNm", "sts", "logicRs"];
	//modify by wusb at 20161207 添加零值校验 zeroRsSts
	var fields = [ "handSts", "taskNm", "rptNm", "orgNm", "dataDate", "logicRsSts"];
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
		var demoWidth = $("#search").width();
		var newLineNum = parseInt(demoWidth/260);
		CommonSerchForm(taskComBoBoxUrl, rptComBoBoxUrl, orgTreeSkipUrl, null, fields, "1", newLineNum);
	}
	//初始化Grid
	function initGrid() {
		TaskFillGrid(gridUrl, columns, false);
	}
	//初始化Button
	function initButtons() {
		var btns = [{
			text : '解锁',
			click : apply_reject,
			icon : 'fa-unlock',
			operNo : 'apply_reject'
		} ];
		BIONE.loadToolbar(grid, btns, function() {
		});
	}
	//解锁
	function apply_reject() {
		var rows = grid.getSelectedRows();
		if(rows.length > 0){
			var taskInstanceId = [];
			for (var i = 0; i < rows.length; i++){
				taskInstanceId.push(rows[i].taskInstanceId);
			}
			BIONE.commonOpenDialog("强制解锁理由", "applyRejWin", "550", "300", "${ctx}/frs/rptfill/reject/forceDesc?taskInstanceId=" + taskInstanceId.join(","), null);
		}else{
			BIONE.tip("请选择记录");
		}
	}
	//确定
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
					} else {
						grid.setParm("condition", "");
						grid.setParm('newPage', 1);
					}
					grid.loadData();
				}
			});

			BIONE.createButton({
				appendTo : btnContainer,
				text : '重置',
				icon : 'fa-repeat',
				// width : '50px',
				click : function() {
					$(":input", form).not(":submit,:reset,:hidden,:image,:button,[disabled]")
						.each(function() {
							$(this).val("");
						});
					$(":input[ltype=combobox]", form).each(function() {
						var ligerid = $(this).attr('data-ligerid'), ligerItem = $.ligerui.get(ligerid);// 需要配置comboboxName属性
						if (ligerid && ligerItem && ligerItem.clear) {
							// ligerUI 1.2 以上才支持clear方法
							ligerItem.clear();
						} else {
							$(this).val("");
						}
					});
					$(":input[ltype=select]", form).each(function() {
						var ligerid = $(this).attr('data-ligerid'), ligerItem = $.ligerui.get(ligerid);// 需要配置comboboxName属性
						if (ligerid && ligerItem && ligerItem.clear) {
							ligerItem.clear();
						} else {
							$(this).val("");
						}
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