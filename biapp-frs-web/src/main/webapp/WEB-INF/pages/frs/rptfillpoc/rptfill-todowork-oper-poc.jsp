<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/meta.jsp"%>
<html>
<head>

<meta name="decorator" content="/template/template26_BS.jsp">
<%@ include file="/common/taglibs.jsp"%>
<%@ include file="/common/spreadjs_load.jsp"%>
<link rel="stylesheet" type="text/css" href="../../../js/myPagination/js/msgbox/msgbox.css" />
<link rel="stylesheet" type="text/css" href="../../../js/myPagination/js/myPagination/page.css" />
<script src="../../../js/myPagination/js/myPagination/jquery.myPagination6.0.js" type="text/javascript"></script>
<script language="javascript" type="text/javascript" src="../../../js/myPagination/js/msgbox/msgbox.js"></script>
<script type="text/javascript">
	var base_BtnFlag = "01";
	var base_QueryInit = false;//查询标识 QueryInit:false 正常值  QueryInit:true 初始值 O
	var base_Download = null;
	var base_View;//设计器视图
	var base_Spread;//报表设计器
	var base_DataDate = "${dataDate}";//数据日期
	var base_OrgNo = "${orgNo}";//机构编号
	var base_OrgNm = "${orgNm}";//机构名称
	var base_TaskInsId = "${taskInsId}";
	var base_TaskId = "${taskId}";
	var base_SelIdxs = []; // 选择的指标信息
	var base_CheckedType = "NO";//校验类型 
	var base_OperType = "03";
	var base_InitRptId = "${rptId}";//报表编号
	var base_InitrptName = "${rptNm}";//报表名称
	var base_OrgType = "${type}";//机构类型
	var base_TmpId = "";//报表模板编号
	var base_selectIdxNo = "";//当前选择单元格指标编号
	var base_selectCellNo = "";//当前选择单元编号
	var base_selectDataPrecision = "";//当前选择单元格指标精度
	var base_selectUnit = "";//当前选择单元格指标单位
	var base_selectIdxNm = "";//当前选择单元格指标名称
	var tabObj = null;
	var base_layout_upDown = true;//初始进来是上下布局
	var base_Remark = "";//单元格备注
	var base_isLogic = false;//是否完成逻辑校验
	var base_isSumpart = false;//是否完成总分校验
	var base_isWarn = false;//是否完成预警校验
	var base_isZero = false;//是否完成零值校验
	var base_CheckResult = "";//校验结果
	var base_verId = "1";//报表版本
	var tmp = {
			urlTmp : ['${ctx}/rpt/frs/rptfill/checkCacheInfo']
		};
	
	var pageSize=20;//明细类报表分页展示条数
	var Pagination=null;
	var templateType = '${templateType}';//模板类型
	var oldVal = 0;
	var pageNumber = 1;
	var cellColor = [];//高亮单元格数组
	var base_SumType = '01';//机构汇总方式
	$(function() {
		//明细类报表分页 20200212
		if(templateType && templateType == "01"){
			var height = $(window).height();
			$('#center').height(height - 30);
			$(document).on("focusout","#pagination input",jumpPage);
			$(document).on("change","#pageSize",function(val,text){
				changeSize($(this).val());
			});
		}
		
		showTemplateId();
		initDesign();
		initSearchForm();
		initSearchButtons("#search", "#searchbtn");
		initButtons();
		initExport();
		initCheckResult();
		initTool();
		$("#top").hide();
	});
	
	//分页-输入页码跳转 20200212
	function jumpPage() {
		if(tmp.isUpdateData()){
			tmp.save(true);
		}
		var size = pageSize;
		var page = $("#pagination").find("input").val();
		p = parseInt(page)
		if (isNaN(p) || p != page) {
			p = oldVal;
			$("#pagination").find("input").val(oldVal);
		}
		if (total < p) {
			p = total;
			Pagination.jumpPage(p);
		}
		if (p != oldVal) {
			var sumPage = parseInt(window.total / size)
			if (window.total % size != 0) {
				sumPage++;
			}
			var isInit = false;
			if (page == sumPage) {
				isInit = true;
				window.islast = true;
			} else {
				if (window.islast) {
					isInit = true;
					window.islast = false;
				}
			}
			oldVal = p;
			Pagination.jumpPage(p);
			pageNumber = p;
			View.ajaxJson(null, null, isInit, "/report/frame/tmp/view/getViewInfo?d="+new Date().getTime(), (p - 1) * size + 1, size, false);
		}
	}
		
	//分页-初始化分页栏 20200212
	function initPagination(total){
		window.total = total;
		this.total=total;
		var size=pageSize;
		if(total != null && total != 0){
			if(size == "全部"){
				total=1;
			}else{
				total=parseInt((total-1)/size)+1;
			}
		}else{
			total=1;
		}
		Pagination=$("#pagination").myPagination({
			pageCount: total,
			pageNumber: 10,
			cssStyle: 'liger',
	        panel: {
	            tipInfo_on: true,
	            tipInfo: '<span class="tip">{input}/{sumPage}页,每页显示<select id="pageSize"><option value="20">20</option><option value="50">50</option><option value="100">100</option><option value="全部">全部</option></select>条</span>',
	            tipInfo_css: {
	              width: '25px',
	              height: "20px",
	              border: "1px solid #777",
	              padding: "0 0 0 5px",
	              margin: "0 5px 0 5px",
	              color: "#333"
	            }
	        },
	        ajax: {
	            on: false,
	            onClick: function(page) {
	            	pageNumber = page;
	            	if(tmp.isUpdateData()){
	        			tmp.save(true);
	        		}
	            	var sumPage =parseInt(window.total/size)
	            	if(window.total%size !=0){
	            		sumPage ++;
	            	}
	            	var isInit = false;
	            	if(page == sumPage){
	            		isInit = true;
	            		window.islast = true;
	            	}
	            	else{
	            		if(window.islast){
	            			isInit = true;
		            		window.islast = false;
	            		}
	            	}
	            	oldVal = page;
	        		View.ajaxJson(null,null,isInit,"/report/frame/tmp/view/getViewInfo?d="+new Date().getTime(),(page-1)*size+1,size,false);
	        		setTimeout(function(){
	        			if($("#pageSize").val()!= size){
	        				$("#pageSize").val(size);
	        				setTimeout(function(){
	    	        			if($("#pageSize").val()!= size){
	    	        				$("#pageSize").val(size);
	    	        			}
	    	        		})
	        			}
	        		});
	            }
	        }
	    });
		$("#pagination").css("overflow","hidden").css("margin-top","0px").css("padding-top","0px");
		$("#pageSize").val(size);
	}
	
	//分页-切换显示条数 20200212
	function changeSize(size){
		pageSize=size;
		View.ajaxJson(null,null,true,"/report/frame/tmp/view/getViewInfo?d="+new Date().getTime(),1,size);
	}

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
					orgNm : base_OrgNm,
					templateType : templateType
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
		return;
		window.posi = View.Utils.initAreaPosiLabel(args.row, args.col);
		var selectCellNo = info.cellNo;
		var selectIdxNo = info.indexNo;
		var checkResult = $("#checkResult");
		//还原校验公式涉及单元格的变色
		for (var cellNo in cellColor){
			var backColor = cellColor[cellNo];
			if(backColor){
				var rowCol = View.Utils.posiToRowCol(cellNo);
				var myCell = View.spread.getActiveSheet().getCell(Number(rowCol.row), Number(rowCol.col)).backColor(backColor);
			}
		}
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
											required : true
										}
									},
									{
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
											required : true
										}
									},
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
												BIONE
														.commonOpenDialog(
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
											required : true
										}
									} ]
						});

		$("#date").val(base_DataDate);
		$.ligerui.get("org")._changeValue(base_OrgNo, base_OrgNm);
		$.ligerui.get("rptCombox")._changeValue(base_InitRptId, base_InitrptName);
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
						initBaseVariable();
						initDesign();
						showTemplateId();
					}
				}
			});
			$("#searchbtn").width($("#center").width() - 990);
		}
	};

	//根据当前查询条件获取对应变量值
	function initBaseVariable(){
		$.ajax({
			cache : false,
			async : false,
			url : "${ctx}/rpt/frs/rptfill/initBaseVariable",
			dataType : 'json',
			data : {
				taskType : base_OrgType,
				orgNo : base_OrgNo,
				dataDate : base_DataDate,
				rptId : base_InitRptId,
				taskId : base_TaskId
			},
			type : "post",
			success : function(result) {
				if (result && result.ins) {
					base_TaskInsId = result.ins.taskInstanceId;
				}else{
					BIONE.showError("当前日期机构下，当前报表没有下发任务！！！", closeDialog);
				}
			},
			error : function() {
				BIONE.tip("数据查询异常，请联系系统管理员");
			}
		});
	}
	
	//关闭填报界面
	function closeDialog(){
		window.parent.BIONE.closeDialog("taskFillWin");
	}
	
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
							//BIONE.createButton(tmp.buttonArray[result[i]]);
							createButton(tmp.buttonArray[result[i]]);
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
		if(queryInit){
			base_QueryInit = queryInit;	
		}
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
	
	//报表数据下载（下载当前值）
	tmp.exportRpt = function(){
		if(tmp.isUpdateData()){//如果数据有修改，先保存再下载
			base_QueryInit = false;
			tmp.save(false,tmp.download);
		}else{
			tmp.download(false);
		}
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
		BIONE.commonOpenDialog("汇总方式选择","sumDataType",450,300,"${ctx}/rpt/frs/rptfill/sumDataType");	
	};
	
	//机构汇总
	tmp.sumDataInfo =function(){
		//先检查是否包含未归档子机构
		var isContainOrg  = submitJudge("03", judgeAndSaveSingleSumSts);
	}
	
	//检查是否包含未归档子机构
	function submitJudge(btnFlag ,fun){
		var isContainOrg = true;
		BIONE.showLoading("查询是否有未归档子机构，请稍等...");
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
					BIONE.commonOpenDialog("未归档子机构任务实例", "taskInsChildWin", width, height, "${ctx}/rpt/frs/rptfill/showTab?path=rptfill-org-child-ins&sts=0&params="
							+ JSON2.stringify(params) + "&taskInsIds=" + taskInsIds.join(",") + "&orgNo=" + base_OrgNo + "&rptId="
							+ base_InitRptId + "&dataDate=" + base_DataDate + "&taskId=" + base_TaskId + "&btnFlag=" + btnFlag + "&templateType=" + templateType + "&sumDataType=" + base_SumType, null);
					return false;
				}else if("NO" == result.result){
					if (typeof (fun) == "function") {
						fun();
					}
				}
			},
			error:function(){
				BIONE.hideLoading();
				BIONE.tip("获取未归档子任务异常，请联系系统管理员");
			}
		});
		return isContainOrg;
	}
	
	//发起机构汇总
	function judgeAndSaveSingleSumSts(){
		$.ajax({
			cache : false,
			async : true,
			url : "${ctx}/rpt/frs/dataSum/judgeAndSaveSingleSumSts",
			dataType : 'json',
			data : {
				rptId : base_InitRptId, 
				dataDate : base_DataDate, 
				orgNo : base_OrgNo, 
				taskId :base_TaskId, 
				taskInsId : base_TaskInsId,
				operType : base_OperType,
				taskFillOperType : '25',
				sumType : base_SumType
			},
			type : "post",
			beforeSend : function() {
				BIONE.showLoading("数据汇总中，请稍等...");
			},
			complete: function(){
				BIONE.hideLoading();
			},
			success : function(result){
				if(result && "OK" == result.result){
					View.ajaxJson();
					BIONE.tip("汇总成功");
				}else if(result && result.result){
					BIONE.tip(result.result);
				}
			},
			error:function(){
				BIONE.tip("机构汇总异常，请联系系统管理员");
			}
		});
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
				if(newValue != oldValue){
					if(parseFloat(newValue) != parseFloat(oldValue)) {
						return true;
					}
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
			BIONE.tip("非数据单元格无法填写业务批注");
			return;
		}
		var cellNo = selIdxs[0].cellNo;
		BIONE.commonOpenDialog("业务批注","remarkWin",450,300,"${ctx}/rpt/frs/rptfill/showTab?path=rpt-todowork-remark-info&rptId="
				+ base_InitRptId + "&type=" + base_OrgType + "&taskInsId=" + base_TaskInsId + "&orgNo="+ base_OrgNo +"&dataDate="+ base_DataDate +"&cellNo="+cellNo);	
	}
	
	//保存功能入口
	tmp.promptAndSave = function(flag) {
		if(tmp.isUpdateData() || base_QueryInit){
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
					searchArgs : changeInfo.searchArgs,
					pageSize : $("#pageSize").val(),
					pageNumber : $(".current").text() 
				}, 
				beforeSend : function() {
					BIONE.showLoading(message);
				},
				complete: function(){
					if(flag){//正常保存后取消遮盖，其他的业务逻辑处理就先不取消遮盖
						BIONE.hideLoading();
					}
				},
				success: function(result) {
					View.refreshCellVals(result.res);
					View.refreshOldCellVals(result.oldRes);
					if(templateType && templateType == "01"){
						updateTask(flag, "/report/frame/tmp/view/getViewInfo?d="+new Date().getTime(), ($(".current").text() - 1) * pageSize + 1, pageSize);
					}else{
						updateTask(flag);
					}
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
	function updateTask(flag, url, start, step){
		$.ajax({
			cache : false,
			async : true,
			url : "${ctx}/rpt/frs/rptfill/updateTask?taskInsId=" + base_TaskInsId,
			dataType : 'json',
			type : "get",
			success : function(result){
				if("OK"== result.result){
					if(flag){
						updateColor(url, start, step);
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
	function updateColor(url, start, step){
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
				View.ajaxJson(JSON.stringify(color), null, true, url, start, step, false);
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
	
	//归档功能入口函数
	tmp.submit = function() {
		if(tmp.isUpdateData()){//修改
			$.ligerDialog.confirm("归档前需要保存数据，是否继续？", function(yes) {
				if(yes){
					tmp.save(false,tmp.submitInfo,"正在归档，请稍候...");
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
					tmp.save(false,tmp.finishInfo,"正在归档，请稍候...");
				}
			});
		}else{
			tmp.finishInfo();
		}
	};
	
	//归档
	tmp.submitInfo =function(){
		//先检查当前任务检验状态
		var rsStsObj = getRsSts();
		if(rsStsObj.failFlag){
			$.ligerDialog.confirm("该记录校验未通过或者未校验，是否继续？", function(yes) {
				if(yes){
					//再检查是否有未归档子机构
					var isContainOrg  = submitJudge('01', implementSubmit);
				}
			});
		}
	}
	
	//执行归档
	function implementSubmit(){
		var taskInsIds = [];
		taskInsIds.push(base_TaskInsId);
		//进行归档
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
				rptOperType : base_OperType
			},
			beforeSend : function() {
				BIONE.showLoading("正在归档，请稍候...");
			},
			complete: function(){
				BIONE.hideLoading();
			},
			success : function(){
				if(null !=parent.frames.findIframeByTitle("报表填报")[0].contentWindow.frames.tsktab){
					if(parent.frames.findIframeByTitle("报表填报")[0].contentWindow.frames.tsktab){
						var tsktabWindow = null;
						if (window.document.documentMode){//documentMode 是一个IE的私有属性，在IE8+中被支持。
							tsktabWindow = parent.frames.findIframeByTitle("报表填报")[0].contentWindow.frames.tsktab.tmp;
					    }else{
					    	tsktabWindow = parent.frames.findIframeByTitle("报表填报")[0].contentWindow.frames.tsktab.contentWindow.tmp;
					    }
						if(tsktabWindow){
							tsktabWindow.reAsyncChildNodes("left");
							tsktabWindow.reAsyncChildNodes("right");
						}
					}
				}
				//归档成功刷新填报列表页面
				if(null!=parent.frames.findIframeByTitle("报表填报")[0].contentWindow.frames.listtab){
					if(parent.frames.findIframeByTitle("报表填报")[0].contentWindow.frames.listtab){
						var listtabWindow = null;
					    if (window.document.documentMode) { //判断是否IE浏览器
					    	listtabWindow = parent.frames.findIframeByTitle("报表填报")[0].contentWindow.frames.listtab.grid;
					    }else{
					    	listtabWindow = parent.frames.findIframeByTitle("报表填报")[0].contentWindow.frames.listtab.contentWindow.grid;
					    }
						if(listtabWindow){
							listtabWindow.loadData();
						}
					}
				}
				parent.BIONE.tip("归档成功");
				BIONE.closeDialog("taskFillWin");
			},
			error:function(){
				BIONE.tip("归档失败，请联系系统管理员");
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
						returnObj.failFlag = false;//有正在校验记录，不能归档
					}
					if("sumpart" == checkType){//如果是总分检验，还得校验下级是否有机构
						var sumCheckRs = result.sumCheckSts;
						if(!sumCheckRs){
							BIONE.tip("该记录无下级机构分发，无需总分校验");
							returnObj.failFlag = false;
						}
					}
					if(!checkType){//判断是不是归档或者结束填报操作
						if((logicRs == null || logicRs == '04' || logicRs == '06')){//逻辑校验未通过
							//判断逻辑校验是否有未通过项，监管制度校验公式未通过不能提交，自定义校验不通过需添加说明。	
							var logicFlag = tmp.getLogicValidResult();
							if(logicFlag){//未通过的校验处理了
								if(base_OrgType == '03' && (sumpartRs == null || sumpartRs == '04' || sumpartRs == "06")){//总分校验未通过
									BIONE.showSuccess("总分校验未通过，请核对数据！");
									returnObj.failFlag = false;
								}else{
									if((warnRs == null || warnRs == '04' || warnRs == '06' )){
										$.ligerDialog.confirm("该记录校验未通过或者未校验，是否继续？", function(yes) {
											if(yes){
												returnObj.failFlag = true;//可以正常归档
											}else{
												returnObj.failFlag = false;//不能归档
											}
										});
									}
								}	
							}else{
								BIONE.showSuccess("逻辑校验有未通过项，请核对数据或添加说明！");
								returnObj.failFlag = false;
							}
						}else{//逻辑校验通过
							if(base_OrgType == '03' && (sumpartRs == null || sumpartRs == '04' || sumpartRs == "06")){//总分校验未通过
								BIONE.showSuccess("总分校验未通过，请核对数据！");
								returnObj.failFlag = false;
							}else{
								if((warnRs == null || warnRs == '04' || warnRs == '06' )){
									$.ligerDialog.confirm("该记录校验未通过或者未校验，是否继续？", function(yes) {
										if(yes){
											returnObj.failFlag = true;//可以正常归档
										}else{
											returnObj.failFlag = false;//不能归档
										}
									});
								}
							} 
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
	
	tmp.getLogicValidResult = function(){
		var logicFlag = false;
		$.ajax({
			cache : false,
			async : false,
			url : "${ctx}/rpt/frs/rptfill/getLogicValidResult",
			dataType : 'json',
			data : {
				templateId : base_TmpId, 
				dataDate : base_DataDate, 
				orgNo : base_OrgNo
			},
			type : "post",
			beforeSend : function() {
				BIONE.showLoading("检查逻辑校验...");
			},
			complete: function(){
				BIONE.hideLoading();
			},
			success : function(result){
				if(result.logicFlag){
					logicFlag = true;
				}
			}
		});
		return logicFlag;
	}
	
	//结束填报
	tmp.finishInfo = function(){
		//先检查当前任务检验状态
		var rsStsObj = getRsSts();
		if(rsStsObj.failFlag){
			$.ligerDialog.confirm("该记录校验未通过或者未校验，是否继续？", function(yes) {
				if(yes){
					//再检查是否有未归档子机构
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
					tmp.save(false, checkSingleFunc, "正在进行校验，请稍候...", checkType);
				}
			});
		}else{
			tmp.checkSingleInfo(checkType, false);
		}
	};

	//为适应save只传递单个函数进行处理
	function checkSingleFunc(checkType){
		tmp.checkSingleInfo(checkType, false);
	}

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
			/*complete : function() {
				BIONE.loading = false;
				BIONE.hideLoading();
			},*/
			success: function(result) {
				if(result.result){
					// 根据校验状态返回提示
					if ("logic" == result.checkType) {
						base_isLogic = true;
					} else if ("warn" == result.checkType) {
						base_isWarn = true;
					} else if ("sumpart" == result.checkType) {
						base_isSumpart = true;
					} else if ("zero" == result.checkType) {
						base_isZero = true;
					}
					var color = result.color;
					if(result.taskCheckSts == "05"){
						base_CheckResult += checkName + " ： 校验通过。<br>";
					}else if(result.taskCheckSts == "06"){
						base_CheckResult += checkName + " ：有未通过项,请检查。<br>";
					}else if(result.taskCheckSts == "04"){
						base_CheckResult += checkName + " ：校验失败,请检查日志。<br>";
					}
					if("OK" != result.result){
						BIONE.showError(result.result);
						$(".l-dialog-body").width(600);
					}
				}else{
					 BIONE.tip("校验异常，请联系系统管理员");
				}
				if (isBatchCheck) {
					if (base_isLogic && (base_isSumpart || ("02" == base_OrgType)) && base_isWarn && base_isZero) {//全部校验完了再取消遮盖
						BIONE.hideLoading();
						BIONE.showSuccess(base_CheckResult);
						base_CheckResult = "";
						base_isLogic = base_isSumpart = base_isWarn = base_isZero = false;
						View.ajaxJson(color);
					}
				} else {
					BIONE.hideLoading();
					BIONE.showSuccess(base_CheckResult);
					base_CheckResult = "";
					base_isLogic = base_isSumpart = base_isWarn = base_isZero = false;
					View.ajaxJson(color);
				}
			},
			error: function(){
				BIONE.tip("校验异常，请联系系统管理员");
			}
		});
	}
	
	//校验结果查看
	tmp.checkResult = function(checkType) {  
		var height = $(window).height() - 30;
		var width = $(window).width() - 80;
		BIONE.commonOpenDialog('校验结果查看', 'checkResultWin', width, height, '${ctx}/rpt/frs/rptfill/showTab?path=rptfill-todowork-check-result&rptId='
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
		//1104不做总分校验
		if("02" != base_OrgType){
			tmp.checkSingleInfo("sumpart", true);//总分校验
		}
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
	
	//新增备注（针对单元格的说明）
	tmp.explain = function(){
		var selIdxs = View.getSelectionIdxs();
		if(!selIdxs || selIdxs.length <= 0){
			BIONE.tip("非数据单元格无法填写单元格备注");
			return;
		}
		var cellNo = selIdxs[0].cellNo;
		base_Remark = selIdxs[0].remark;
		BIONE.commonOpenDialog("单元格备注","explain",450,300,"${ctx}/rpt/frs/rptfill/addExplain?tmpId="
				+ base_TmpId + "&dataDate=" + base_DataDate + "&cellNo=" + cellNo);	
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
				BIONE.showLoading("正在查询哪些校验没有通过，请稍等...");
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
							if(("logicTab" != key) && ("logicByExternalTab" != key)){
								$("li[tabid="+ key +"]").css("display", "none");
							}
						}
					}
					tabObj.selectTabItem("logicTab");//默认选择逻辑校验
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
						$("#"+tabId).attr('src', '${ctx}/rpt/frs/rptfill/logicResult?' + checkResultParam + '&checkType=01');
					}else if("logicByExternalTab" == tabId){
						$("#"+tabId).attr('src', '${ctx}/rpt/frs/rptfill/logicResult?' + checkResultParam + '&checkType=02');
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
			text : "表内校验结果",
			showClose : false,
			content : '<iframe id="logicTab" style="height:'+ iframeHeight +'px" src="" data-idxNo= ""></iframe>'
		});
		tabObj.addTabItem({
			tabid : "logicByExternalTab",
			text : "表间校验结果",
			showClose : false,
			content : '<iframe id="logicByExternalTab" style="height:'+ iframeHeight +'px" src="" data-idxNo= ""></iframe>'
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
/* 		tabObj.addTabItem({
			tabid : "remarkTab",
			text : "批注",
			showClose : false,
			content : '<iframe id="remarkTab" style="height:'+ iframeHeight +'px" src="" data-idxNo= ""></iframe>'
		}); */
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

	// 重写创建按钮方法,新增创建可点击下拉的按钮
	createButton = function(options) {
		var p = $.extend({
			appendTo : $('body')
		}, options || {});

		if (p.operNo && top.window["protectedResOperNo"]) {

			if ($.inArray(p.operNo, top.window["protectedResOperNo"]) != -1) {
				// 说明改按钮收权限保护，需要判断当前登录用户是否具有权限
				if (!top.window["authorizedResOperNo"]
						|| $.inArray(p.operNo,
								top.window["authorizedResOperNo"]) == -1) {
					return;
				}
			}
		}
		// l-toolbar-item l-panel-btn l-toolbar-item-hasicon
        var btn;
		if(p.menu){
			btnHtml = '<span class="l-btn" data-toggle="dropdown">' + p.text + '</span>' +
					  '<ul class="dropdown-menu">';
			var menuChilds = p.menu.items;
			for ( var i = 0; i < menuChilds.length ; i++){
				btnHtml += '<li style="text-align:center"><a href="javascript:void(0);" onclick="' + menuChilds[i].click + '">' + menuChilds[i].text + '</a></li>';
			}
			btnHtml += '</ul>';
			btn = $(btnHtml);
			btn.appendTo(p.appendTo);
		}else{
			var hasIcon = false;
			if (p.icon) {
				hasIcon = true;
			}
			var btnHtml = '<div ';
			if (p.id) {
				btnHtml = btnHtml + 'id="'+ p.id +'" class="l-btn'
			}else{
				btnHtml = btnHtml + 'class="l-btn'
			}
			if (hasIcon) {
				btnHtml = btnHtml + ' l-btn-hasicon';
			}
			btnHtml += '"><span></span></div>';
			btn = $(btnHtml);
			var $i = null;
			if (p.icon) {
				$i = $('<i class="icon-' + p.icon + '" style ="color : #fff;line-height:2em"> </i> ')
				btn.prepend($i);
			}

			if (p.width) {
				btn.width(p.width);
			}
			if (p.click) {
				btn.click(p.click);
			}
			if (p.text) {
				if($i)
					$i.html("&nbsp"+p.text);
				else
					$("span", btn).html(p.text);
			}
			if (typeof (p.appendTo) == "string") {
				var space = 30;
				var iconwidth = 0;
				var prewidth = 0;
				var num = $(p.appendTo + ' .l-btn').length;
				p.appendTo = $(p.appendTo);
				if (num != 0)
					prewidth = p.appendTo.width();
				if (hasIcon)
					iconwidth = 10;
				if(false != p.isCountWidth){
					p.appendTo.width(prewidth + parseInt(p.width) + space + iconwidth);
				}
			}
			btn.appendTo(p.appendTo);
		}
	};
	
	//恢复初始值
	tmp.init = function() {
		 $.ligerDialog.confirm('恢复初始值会清除报表现有数据, 确定要执行此操作？', '恢复初始值', function(yes) {
				if (yes) {
					$.ajax({
						async : true,
						url : "${ctx}/rpt/frs/rptfill/initData",
						dataType : 'json',
						type : 'POST',
						data : {
							rptId : base_InitRptId,
			 				orgNo : base_OrgNo,
							dataDate : base_DataDate,
							taskInsId : base_TaskInsId,
							operType : base_OperType,
							verId : base_verId,
							tmpId : base_TmpId
						}, 
						beforeSend : function() {
							BIONE.showLoading("数据恢复中，请稍等...");
						},
						complete: function(){
							BIONE.hideLoading();
						},
						success: function(result){
							if(result.error){
								BIONE.tip(result.error);
							}else{
								BIONE.tip("初始化恢复完成！");
								View.ajaxJson();
							}
						},
						error: function(){
							BIONE.tip("恢复初始值失败，请联系系统管理员");
						}
					});
				}
		 });
	};
	
	//功能按钮集合
	tmp.buttonArray = {
			"INITBTN" : { text : '表间计算', width : '50px', appendTo : '#dataButton', operNo:'initBtn', click : tmp.rptCalculation, isCountWidth : false},
//			"VIEWINITBTN" : { text : '查看初始值', width : '60px', appendTo : '#dataButton', operNo:'viewInitBtn', click : tmp.viewInit, isCountWidth : false},
//			"EXPORTINITBTN" : { text : '导出初始值', width : '60px', appendTo : '#dataButton', operNo:'exportInitBtn', click : function(){tmp.download(true)}, isCountWidth : false},
			"INITBTN" : { text : '初始值', width : '50px', appendTo : '#dataButton', operNo:'viewInitBtn', isCountWidth : false,
				menu : { items : [ { text : '查看初始值', click : 'tmp.viewInit()' }, { text : '恢复初始值', click : 'tmp.init()' },
								   { text : '导出初始值', click : 'tmp.download(\'true\')' } ] }},
			"RESETBTN" : { text : '数据重置', width : '50px', appendTo : '#dataButton', operNo:'resetBtn', click : tmp.reset, isCountWidth : false},
			"EXPORTBTN" : { text : '数据下载', width : '50px', appendTo : '#dataButton', operNo:'exportBtn', click : tmp.exportRpt, isCountWidth : false},
	  		"IMPORTBTN" : { text : '数据导入', width : '50px', appendTo : '#dataButton', operNo:'importBtn', click : tmp.importData, isCountWidth : false},
			"CALCULATEBTN" : { text : '条线汇总', width : '50px', appendTo : '#dataButton', operNo:'calculateBtn', click : null, isCountWidth : false},
			"SUMBTN" : { text : '机构汇总', width : '50px', appendTo : '#dataButton', operNo:'sumBtn', click : tmp.sumData, isCountWidth : false},
			"COMPAREBTN" : { text : '比上期', width : '50px', appendTo : '#dataButton', operNo:'compareBtn', click : null, isCountWidth : false},
			
//			"HISVIEWBTN" : { text : '修改记录', width : '50px', appendTo : '#explainButton', operNo:'hisviewBtn', click : tmp.openHisview, isCountWidth : false},
			"DESCBTN" : { text : '填报说明', width : '50px', appendTo : '#explainButton', operNo:'descBtn', click : function(){tmp.descbtn()}, isCountWidth : false},
			"REMARKBTN" : { text : '批注', width : '25px', appendTo : '#explainButton', operNo:'remarkBtn', click : function(){tmp.busiremark()}, isCountWidth : false},
			"EXPLAINBTN" : { text : '备注', width : '25px', appendTo : '#explainButton', operNo:'explainBtn', click : function(){tmp.explain()}, isCountWidth : false},

			"VALIDBTN" : { text : '数据校验', width : '50px', appendTo : '#validButton', operNo:'validBtn', isCountWidth : false ,
				menu : { items : [ { text : '全部校验', click : 'tmp.check()' }, { text : '逻辑校验', click : 'tmp.checkSingle(\'logic\')' }, { text : '预警校验', click : 'tmp.checkSingle(\'warn\')' },
								   { text : '总分校验', click : 'tmp.checkSingle(\'sumpart\')' }, { text : '零值校验', click : 'tmp.checkSingle(\'zero\')' } ] }},
			"1104VALIDBTN" : { text : '数据校验', width : '50px', appendTo : '#validButton', operNo:'validBtn', isCountWidth : false ,
				menu : { items : [ { text : '全部校验', click : 'tmp.check()' }, { text : '逻辑校验', click : 'tmp.checkSingle(\'logic\')' }, { text : '预警校验', click : 'tmp.checkSingle(\'warn\')' },
								   { text : '零值校验', click : 'tmp.checkSingle(\'zero\')' } ] }},
/* 			"LOGICBTN" : { text : '逻辑', width : '30px', appendTo : '#validButton', operNo:'logicBtn', click : function(){tmp.checkSingle("logic")}, isCountWidth : false},
			"WARNBTN" : { text : '预警', width : '30px', appendTo : '#validButton', operNo:'warnBtn', click : function(){tmp.checkSingle("warn")}, isCountWidth : false},
			"SUMPARTBTN" : { text : '总分', width : '30px', appendTo : '#validButton', operNo:'sumpartBtn', click : function(){tmp.checkSingle("sumpart")}, isCountWidth : false},
			"ZEROBTN" : { text : '零值', width : '30px', appendTo : '#validButton', operNo:'zeroBtn', click : function(){tmp.checkSingle("zero")}, isCountWidth : false}, */
			
			"SAVEBTN" : { text : '保存', width : '25px', appendTo : '#flowButton', operNo:'saveBtn', click : function(){tmp.promptAndSave(true)}, isCountWidth : false},
			"BACKBTN" : { text : '返回', width : '25px', appendTo : '#flowButton', operNo:'backBtn', click : tmp.back, isCountWidth : false},
			"HANDLEBTN" : { text : '归档', width : '25px', appendTo : '#flowButton', operNo:'handleBtn', click : tmp.submit, isCountWidth : false},
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