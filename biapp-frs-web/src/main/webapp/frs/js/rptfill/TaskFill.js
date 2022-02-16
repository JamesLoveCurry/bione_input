(function($) {
	//审批状态渲染
	CollateStsRender= function(rowdata) {
	if (rowdata.collateSts == '0') { return "未审批";} else if (rowdata.collateSts == '1') { return "同意";} else if (rowdata.collateSts == '2') { return "不同意";} else { return rowdata.collateSts;}
	};
	// 任务状态渲染(加入"结束填报")
	
	HandStsRender = function(rowdata) {
		if (rowdata.sts == '0') 
		{ return "未提交";} 
		else if (rowdata.sts == '1') 
		{ return "待复核";} 
		else if (rowdata.sts == '2') 
		{ return "待审核";} 
		else if (rowdata.sts == '3') 
		{ return "流程完成";}
		else if (rowdata.sts == "A1") {
			return "支行待核对";
		} else if(rowdata.sts == "A2"){
			return "支行待复核";
		} else if(rowdata.sts == "A3"){
			return "支行待审核";
		} else if(rowdata.sts == "B1"){
			return "分行待核对";
		} else if(rowdata.sts == "B2"){
			return "分行待复核";
		} else if(rowdata.sts == "B3"){
			return "分行待审核";
		} else if(rowdata.sts == "C1"){
			return "总行待核对";
		} else if(rowdata.sts == "C2"){
			return "总行待复核";
		} else if(rowdata.sts == "C3"){
			return "总行待审核";
		} else if(rowdata.sts == "D"){
			return "流程完成";
		}else { return rowdata.sts;}
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
		if (rs == '07') {
			return "无需校验";
		}
		return result;
	};
	// 总分校验校验状态渲染
	SumpartRsRender = function(rowdata) {
		return CommonRender(rowdata.isCheck, rowdata.sumpartRs, rowdata.taskType, 'sumpart');
	};
	// 逻辑校验校验状态渲染
	LogicRsRender = function(rowdata) {
		return CommonRender(rowdata.isCheck, rowdata.logicRs, rowdata.taskType, 'logic');
	};
	// 警戒值校验校验状态渲染
	WarnRsRender = function(rowdata) {
		return CommonRender(rowdata.isCheck, rowdata.warnRs, rowdata.taskType, 'warn');
	};
	// 零值校验校验状态渲染
	ZeroRsRender = function(rowdata) {
		return CommonRender(rowdata.isCheck, rowdata.warnRs, rowdata.taskType, 'zero');
	};
	//报表名称链接
	taskObjNmRender = function(rowdata) {
		var aa = rowdata.taskObjNm;
		var bb =aa;
		if(aa.length>20)
			bb=aa.substring(0,20)+"...";
		return "<a href='javascript:void(0)' class='link' onclick='onShowRpt(\""+ rowdata.sts+"\",\"" + rowdata.taskObjId+"\",\"" + rowdata.taskObjNm+"\",\"" + rowdata.dataDate+"\",\"" + rowdata.exeObjId+"\",\"" + rowdata.exeObjNm+"\",\"" + rowdata.lineId+"\",\"" + rowdata.zeroRs+"\",\"" + rowdata.warnRs+"\",\"" + rowdata.sumpartRs+"\",\"" + rowdata.logicRs+"\",\"" + rowdata.taskInstanceId+"\",\"" + rowdata.taskType+"\", \"" + rowdata.taskId+"\", \"" + rowdata.templateType+"\", \"" + rowdata.fixedLength+"\", \"" + rowdata.isPaging+"\")' title='"+aa+"' >"+ bb + "</a>"; 
	};
	onShowRpt = function(sts, taskObjId, taskObjNm, dataDate, exeObjId, exeObjNm, lineId, logicRs, sumpartRs, warnRs, zeroRs, taskInstanceId, taskType, taskId, templateType, fixedLength, isPaging){
		if(sts != "0"){
			$.ajax({
				cache : false,
				async : true,
				url : ctx + "/frs/rptsubmit/submit/getTmpId",
				dataType : 'text',
				data : {
					rptId : taskObjId, dataDate : dataDate, lineId : lineId, operType : operType		
				},
				type : "post",
				success : function(result){
					if(result){
 						BIONE.ajax({
 							async : false,
 							url : ctx + "/rpt/frs/rptfill/createColor",
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
 							var height = $(window.parent.parent.parent).height();
 							if(templateType && (templateType == "01" || templateType == "03")){
 								height = height - 30;
 							}
 							var width = $(window.parent.parent.parent).width() + 10;
 							window.top.color=result;
 							window.parent.BIONE.commonOpenDialog(title, "taskViewWin", width, height, ctx + "/rpt/frs/rptfill/dataFillView?dataDate=" + dataDate + "&orgNo=" + exeObjId + "&rptId=" + taskObjId + "&logicRs=" + logicRs + "&sumpartRs=" + sumpartRs + "&warnRs=" + warnRs + "&taskInsId=" + taskInstanceId + "&lineId=" + lineId + "&rptNm=" + encodeURI(encodeURI(taskObjNm)) +  "&orgNm=" + encodeURI(encodeURI(exeObjNm)) + "&taskId=" + taskId+"&operType="+operType+"&taskType="+taskType+"&templateType="+templateType+"&fixedLength="+fixedLength+"&isPaging="+isPaging+"&d="+new Date().getTime(), null);
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
			$.ajax({
				cache : false,
				async : true,
				url : ctx + "/frs/rptsubmit/submit/getTmpId",
				dataType : 'text',
				data : {
					rptId : taskObjId, dataDate : dataDate, lineId : lineId
				},
				type : "post",
				success : function(result){
					var tmpId = result;
					if(result){
 						BIONE.ajax({
 							async : false,
 							url : ctx + "/rpt/frs/rptfill/createColor",
 							dataType : 'text',
 							type : 'POST',
 							data : {
 								rptId : taskObjId,
 								orgNo : exeObjId,
 								dataDate : dataDate,
 								tmpId : tmpId,
								isBatchCheck : true
 							}
 						}, function(result) {
 							var title = "当前报表:" + taskObjNm;
 							var height = null;
 							if(templateType && templateType == "01"){
 								height = $(parent.parent.parent.window).height() - 30;
 							}else{
 								height = $(parent.parent.parent.window).height();
 							}
 							var width = $(parent.parent.parent.window).width();
 							window.top.color=result;
 							window.parent.parent.parent.BIONE.commonOpenDialog(title, "taskFillWin", width, height, ctx + "/rpt/frs/rptfill/dataFillView?templateId=" + tmpId + "&dataDate=" + dataDate + "&orgNo=" + exeObjId + "&rptId=" + taskObjId + "&logicRs=" + logicRs + "&sumpartRs=" + sumpartRs + "&warnRs=" + warnRs + "&zeroRs=" + zeroRs + "&taskInsId=" + taskInstanceId + "&lineId=" + lineId + "&rptNm=" + encodeURI(encodeURI(taskObjNm))  + "&taskType=" + taskType + "&orgNm=" + encodeURI(encodeURI(exeObjNm)) + "&taskId=" + taskId + "&templateType=" + templateType+ "&fixedLength=" + fixedLength+ "&isPaging=" + isPaging, null,function(){
 								if(this.frame.tmp.isUpdateData()){
 									var cf = this.frame.tmp.isColse(this);
 									return cf;
 								}
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
		}
	};
	//初始化列对象
	GridObject = {
			"sts" : {display : '报表状态', name : 'sts', minWidth : 50, width : 90, render : HandStsRender},
			"finishSts" : {display : '报表状态', name : 'sts', minWidth : 50, width : 60, render : FinishStsRender},
			"dataDate" : {display : '数据日期', name : 'dataDate', minWidth : 50, width : 80},
			"exeObjId" : {display : '机构编码', name : 'exeObjId', minWidth : 50, width : 60},
			"exeObjNm" : {display : '机构名称', name : 'exeObjNm', minWidth : 100, width : 180},
			"taskNm" : {display : '任务名称', name : 'taskNm', minWidth : 100, width : 120,align: 'left'},
			"taskObjNm" : {display : '报表名称', name : 'taskObjNm', minWidth : 100, width : 250,align: 'left',render:taskObjNmRender},
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
			"finishSts" : {display : "报表状态", name : "finishSts", newline : false, type : "select",width : '140', cssClass : "field", labelWidth : '90',
						comboboxName : "finishSts_sel", attr : { op : "=", field : "i.sts"},
						options : { data : [ { text : '未提交', id : "0"}, { text : '待复核', id : "1"}, { text : '待审核', id : "2"},{ text : '流程完成', id : "3"} ], cancelable  : true}
					},
			"updateSts" : {display : "报表状态", name : "updateSts", newline : false, type : "select", cssClass : "field", labelWidth : '90',
						comboboxName : "updateSts_sel", attr : { op : "=", field : ""},
						options : { data : [ { text : '未修改', id : "0"}, { text : '已修改',id : "1"} ], cancelable  : true}
					},
			"logicRsSts" : {display : "逻辑校验状态", name : "logicRsSts", newline : false, type : "select",width : '140', cssClass : "field", labelWidth : '90',
						comboboxName : "logicRsSts_sel", //attr : { op : "=", field : "sl.checkSts"},
						options : { data : [  { text : '未校验', id : "00"}, { text : '准备校验', id : "01"}, { text : '校验中', id : "02"}, { text : '校验成功', id : "03"}, { text : '校验失败', id : "04"}, { text : '通过', id : "05"}, { text : '未通过', id : "06"} ], cancelable  : true}
					},
			"sumpartRsSts" : {display : "总分校验状态", name : "sumpartRsSts", newline : false, type : "select",width : '140', cssClass : "field", labelWidth : '90',
						comboboxName : "sumpartRsSts_sel", //attr : { op : "=", field : "ss.checkSts"},
						options : { data : [  { text : '未校验', id : "00"}, { text : '准备校验', id : "01"}, { text : '校验中', id : "02"}, { text : '校验成功', id : "03"}, { text : '校验失败', id : "04"}, { text : '通过', id : "05"}, { text : '未通过', id : "06"} ], cancelable  : true}
					},
			"warnRsSts" : {display : "预警校验状态", name : "warnRsSts", newline : false, type : "select",width : '140', cssClass : "field", labelWidth : '90',
						comboboxName : "warnRsSts_sel", //attr : { op : "=", field : "sw.checkSts"},
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
						comboboxName : "orgNm_sel", attr : { op : "=", field : "exeObjId"} 
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
//			check:{ enable : true, chkboxType :{"Y":"","N":""}, chkStyle :"checkbox"},
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
	
	//带搜索的机构树
	CommonAndSearchOrgTree = function(urlAsync, urlSync){
		window['urlAsync'] = urlAsync;
		window['urlSync'] = urlSync;
		window['settingAsync'] = {  //异步
			async : {
				enable : true,
				url : urlAsync,
				autoParam : ["id"],
				dataType : "json",
				type : "post",
				dataFilter : function(treeId, parentNode, childNodes) {
					if (childNodes) {
						for ( var i = 0; i < childNodes.length; i++) {
							childNodes[i].id = childNodes[i].params.realId;
							childNodes[i].upId = childNodes[i].upId;
							childNodes[i].nodeType = childNodes[i].params.type;
							childNodes[i].isParent = childNodes[i].params.isParent == "true" ? true: false;
						}
						return childNodes;
					}
				}
			},
			data : {
				key : {
					name : "text"
				}
			},
			view : {
				selectedMulti : false
			},
			callback : {
				onNodeCreated : function(event, treeId, treeNode) {
					//若是根节点，展开下一级节点
					if((treeNode.upId == "0000000")){
						taskOrgTree.reAsyncChildNodes(treeNode, "refresh");
					}
				}
			}
		};
		
		window['settingSync'] = {  //同步
			data : {
				key : {
					name : "text"
				},
				simpleData : {
					enable : true,
					idKey : "id",
					pIdKey : "upId",
					rootPId : null
				}
			},
			view : {
				selectedMulti : false
			}
		};
		
		$("#treeSearchIcon").bind("click", function(){
			setTree($("#treeSearchInput").val() != "")
		});
		$("#treeSearchInput").bind("keydown", function(e){
			if(e.keyCode == 13){
				setTree($("#treeSearchInput").val() != "")
			}
		});
		
		setTree(false);  //初始为false
	}
	function setTree(searchFlag){
		if(searchFlag){
			window['taskOrgTree'] = $.fn.zTree.init($("#tree"), settingSync);
			loadTree(urlSync, taskOrgTree, {searchName : $("#treeSearchInput").val(), orgNm : $("#treeSearchInput").val()});
		}else{
			window['taskOrgTree'] = $.fn.zTree.init($("#tree"), settingAsync);
		}
	}
	
	//加载树
	function loadTree(url, treeObj, data){
		 $.ajax({
			cache : false,
			async : true,
			url : url,
			type : "POST",
			dataType : "json",
			data : data,
			beforeSend : function(){
			},
			complete : function(){
				BIONE.loading = false;
				BIONE.hideLoading();
			},
			success : function(result){
				var nodes = treeObj.getNodes();
				for(var i=0; i<nodes.length; i++){
					treeObj.removeNode(nodes[i], false);   //移除先前节点
				}
				if(result.length > 0){
					treeObj.addNodes(null, result, false);
					treeObj.expandAll(true);
				}
			},
			error : function(result, b){
			}
		 });
	}
	
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
		var eles = {height : '100%', width : '100%',
					columns : columns,
					checkbox : true, usePager : true, pageSize : 20, isScroll : false, rownumbers : true,
					alternatingRow : true, colDraggable : true, dataAction : 'server',
					method : 'post', url : gridUrl, sortName : sortName?sortName:'i.data_date desc, i.exe_obj_id, i.task_obj_id, i.task_id', 
					sortOrder : 'desc', toolbar : {}, enabledSort : false, delayLoad : delayLoad?  delayLoad: false };
		if(isOnDbClick){ eles["onDblClickRow"] = callBack;}
		if(checkbox){ eles["checkbox"] = false;}
		grid = $("#maingrid").ligerGrid(eles);
	};
	//SearchForm
	CommonSerchForm = function(taskComBoBoxUrl, rptComBoBoxUrl, orgTreeSkipUrl, lineComBoBoxUrl, fieldSource, flag, taskType,newLineNum) {
		if(taskType!=null&&fieldSource!=null){
			if(taskType=="01"){
			SearchFormObject["finishSts"].display = "复核状态";
			SearchFormObject["finishSts"].options.data= [ { text : '未复核', id : "1"}, { text : '待审核', id : "2"} ];
			}else if(taskType=="02"){
				SearchFormObject["finishSts"].display = "审核状态";
				SearchFormObject["finishSts"].options.data= [ { text : '未审核', id : "2"}, { text : '流程完成', id : "3"} ];
			}else{
				SearchFormObject["handSts"].display = "报表状态";
				SearchFormObject["handSts"].options.data= [ { text : '支行待核对', id : "A1"}, { text : '支行待复核', id : "A2"},{ text : '支行待审核', id : "A3"}, { text : '分行待核对', id : "A1"}, { text : '分行待复核', id : "A2"},{ text : '分行待审核', id : "A3"},{ text : '总行待核对', id : "A1"}, { text : '总行待复核', id : "A2"},{ text : '总行待审核', id : "A3"}, { text : '流程完成', id : "D"}];
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
						if(taskId){
							var height = $(window).height() - 120;
 							var width = $(window).width() - 480;
 							if(orgTreeSkipUrl.indexOf("?") > 0 ){
 								window.BIONE.commonOpenDialog("机构树", "taskOrgWin", width, height, orgTreeSkipUrl + "&taskId=" + taskId, null);
 							}else{
 								window.BIONE.commonOpenDialog("机构树", "taskOrgWin", width, height, orgTreeSkipUrl + "?taskId=" + taskId, null);
 							}
							return false;
						}else{ BIONE.tip("请选择任务");}
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
