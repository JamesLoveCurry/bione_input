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
<%@ include file="/WEB-INF/pages/plugin/design/edit/template-content-view.jsp"%>
<style type="text/css">

.l-dialog-content {
    max-height: 500px;
    overflow-y: auto;
}
</style>
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
	var base_selectDsId = "";//当前选择的明细单元格对应数据模型ID
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
	var fixedLength = '${fixedLength}';//是否定长
	var isPaging = '${isPaging}';//是否分页
	var oldVal = 0;
	var pageNumber = 1;
	var cellColor = [];//高亮单元格数组
	var base_SumType = '01';//机构汇总方式
	var layout;
	var spreadDesign = null;
	var spread = null;
	// 已选中的报表指标单元格
	var selectionIdx = {};
	var buttonArray = {};
	var configIndexNo = "";
	var srcIndexNo = "";
	var moduleParam;
	var clickCalculation = false; //false-未重新计算表间计算  true-已重新计算表间计算
	var idxCalcCells = []; //表间计算单元格
	var grid;
	var curColId = ""; //分析模式-当前选择的指标列
	var colIds = []; //分析模式-报表指标列
	var downdload;
	$(function() {
		//明细类报表分页 20200212
		if(templateType && (templateType == "01" || templateType == "03")){
			if(isPaging && isPaging == "Y"){
				var height = $(window).height();
				$('#center').height(height - 30);
				$(document).on("focusout","#pagination input",jumpPage);
				$(document).on("change","#pageSize",function(val,text){
					changeSize($(this).val());
				});
			}
		}
		buttonArray = tmp.rptResultButton;
		showTemplateId();
		getIdxCalcCells();
		initMaintab();
		initButtons();
		initDesign();
		initExport();
		initTool();
		initSearchInput();
		$("#operDesc").bind('click',function(){
			BIONE.commonOpenDialog('使用提醒', 'remind', 1200, 800, '${ctx}/rpt/frs/rptfill/remind');
		});
		$("#searchBtn").bind('click',function(){
			searchText();
		});
		$('#searchAll').keydown(function(e){
		    if(e.keyCode==13){
		    	searchText();
		    }
		});
		//点击填报模式
		$("#rptResult").bind('click',function(){
			//切换报表处理的按钮显示
			buttonArray = tmp.rptResultButton;
			$("#dataButton").empty();
			$("#dataButton").show();
			$("#validButton").empty();
			$("#explainButton").empty();
			$("#flowButton").empty();
			$("#analysisButton").empty();
			initButtons();
			//切换模式样式转换
			$(this).removeClass("rpt-config");
			$(this).addClass("rpt-config2");
			$("#rptResult img").attr("src","../../../images/oper/idx2.png");
			$("#rptConfig").removeClass("rpt-config2");
			$("#rptConfig").addClass("rpt-config");
			$("#rptConfig img").attr("src","../../../images/oper/config.png");
			$("#changeAnalysis").removeClass("rpt-config2");
			$("#changeAnalysis").addClass("rpt-config");
			$("#changeAnalysis img").attr("src","../../../images/oper/changeAnalysisBtn.png");
			//切换设计器展示信息
			$("#spread").show();
			$("#spread").empty();
			$("#pagination").show();
			$("#pagination").empty();
			$("#spreadConfig").empty();
			$("#rightCellForm").empty();
			$("#designLayout").hide();
			initDesign();
			spreadDesign = null;
		});
		//点击溯源模式
		$("#rptConfig").bind('click',function(){
			if(isPaging && isPaging == "Y"){
				$('#center').height(height + 30);
			}
			//切换报表处理的按钮显示
			buttonArray = tmp.rptConfigButton;
			$("#dataButton").hide();
			$("#validButton").empty();
			$("#explainButton").empty();
			$("#flowButton").empty();
			$("#analysisButton").empty();
			initButtons();

			//切换模式样式转换
			$(this).removeClass("rpt-config");
			$(this).addClass("rpt-config2");
			$("#rptConfig img").attr("src","../../../images/oper/config2.png");
			$("#rptResult").removeClass("rpt-config2");
			$("#rptResult").addClass("rpt-config");
			$("#rptResult img").attr("src","../../../images/oper/idx.png");
			$("#changeAnalysis").removeClass("rpt-config2");
			$("#changeAnalysis").addClass("rpt-config");
			$("#changeAnalysis img").attr("src","../../../images/oper/changeAnalysisBtn.png");

			//切换设计器展示信息
			$("#spread").hide();
			$("#spread").empty();
			$("#pagination").hide();
			$("#pagination").empty();
			$("#spreadConfig").empty();
			$("#rightCellForm").empty();
			$("#designLayout").show();
			initLayout();
			initConfig();
		});
		$("#showOrHide").bind('click', function(){
			if($("#showOrHide").find("i").attr("class") == "icon icon-up"){//隐藏
				$("#showOrHide").find("i").attr("class", "icon icon-arrow-down");
				$("#maintab").find(".l-tab-content").attr("style","display : none");
				$("#maintab").height(26);
				if(spreadDesign != null){
					$("#designLayout").height($("#center").height()- $("#maintab").height());
					$("#designLayout").children().height($("#center").height()- $("#maintab").height());
					$("#centerDiv").height($("#center").height()- $("#maintab").height());
					$("#rightDiv").height($("#center").height()- $("#maintab").height()-30);
					Design.spreadDom.height($("#center").height()- $("#maintab").height() - 30);
					Design.resize(Design.spread);
				}else{
					View.spreadDom.height($("#center").height()- $("#maintab").height() - 30);
					View.resize(View.spread);
				}
			}else {
				$("#showOrHide").find("i").attr("class", "icon icon-up");
				$("#maintab").find(".l-tab-content").attr("style","display : block");
				$("#maintab").height(140);
				if(spreadDesign != null){
					$("#designLayout").height($("#center").height()- $("#maintab").height());
					$("#designLayout").children().height($("#center").height()- $("#maintab").height());
					$("#centerDiv").height($("#center").height()- $("#maintab").height());
					$("#rightDiv").height($("#center").height()- $("#maintab").height()  - 30);
					Design.spreadDom.height($("#center").height()- $("#maintab").height() - 30);
					Design.resize(Design.spread);
				}else{
					View.spreadDom.height($("#center").height()- $("#maintab").height() - 30);
					View.resize(View.spread);
				}
			}
		});
		//点击分析模式
		$("#changeAnalysis").bind('click', function(){
			//切换报表处理的按钮显示
			buttonArray = tmp.rptResultButton;
			$("#dataButton").empty();
			$("#dataButton").show();
			$("#validButton").empty();
			$("#explainButton").empty();
			$("#flowButton").empty();
			$("#analysisButton").empty();
			initButtons();

			//切换模式样式转换
			$(this).removeClass("rpt-config");
			$(this).addClass("rpt-config2");
			$("#changeAnalysis img").attr("src","../../../images/oper/changeAnalysisBtn2.png");
			$("#rptConfig").removeClass("rpt-config2");
			$("#rptConfig").addClass("rpt-config");
			$("#rptConfig img").attr("src","../../../images/oper/config.png");
			$("#rptResult").removeClass("rpt-config2");
			$("#rptResult").addClass("rpt-config");
			$("#rptResult img").attr("src","../../../images/oper/idx.png");

			//切换设计器展示信息
			$("#spread").hide();
			$("#spread").empty();
			$("#pagination").hide();
			$("#pagination").empty();
			$("#spreadConfig").empty();
			$("#rightCellForm").empty();
			$("#designLayout").hide();
			$("#spread").show();
			initAnalysis();
		});
	});

	function initLigerForm(){
		$("#search").ligerForm({
			fields : [{
				display : "选择指标列",
				name : "colId",
				comboboxName : 'indexNoBox',
				newline : true,
				type : "select",
				width : '140',
				options : {
					initValue :  colIds[0].id,
					url : '${ctx}/rpt/frs/rptfill/getIndexCol?templateId='+base_TmpId+"&verId="+base_verId,
					onSelected : function(id, text){
						if(id != "" && grid){
							curColId = id;
							reloadGrid(id);
						}
					}
				}
			}
			]
		});
	}

	function reloadGrid(curColId){
		grid.setParm('newPage', 1);
		grid.set("url","${ctx}/rpt/frs/rptfill/changeAnalysis?rptId=" + base_InitRptId + "&orgNo=" + base_OrgNo
				+ "&dataDate=" + base_DataDate + "&colId=" + curColId);
	}

	function initIndexCol(){
		$.ajax({
			cache : false,
			async : false,
			url : '${ctx}/rpt/frs/rptfill/getIndexCol',
			dataType : 'json',
			type : "get",
			data : {
				templateId : base_TmpId,
				verId : base_verId
			},
			success : function(result) {
				if(result){
					colIds = result;
				}
			}
		});
	}

	function initGrid(){
		var columns = [];
		$.ajax({
			cache : false,
			async : false,
			url : '${ctx}/rpt/frs/rptfill/getColumns',
			dataType : 'json',
			type : "post",
			data : {
				templateId : base_TmpId,
				dataDate : base_DataDate
			},
			success : function(data) {
				if(data){
					for ( var i = 0; i < data.length; i++) {
						columns.push({
							display : data[i].display,
							name : data[i].name,
							width : null == data[i].width ? '10%' : data[i].width,
							render : function (row, index, value){
								if(null == value || "" == value){
									return value;
								}
								var key = findKey(row, value);
								if("diff" == key.substr(0,4)){
									var colorkey = "color"+key;
									if("1" == row[colorkey]){
										return "<div style='width:100%;height:100%;background:red;'>"+value+"</div>";
									} else if ("0" == row[colorkey]) {
										return "<div style='width:100%;height:100%;background:green;'>"+value+"</div>";
									} else {
										return value;
									}
								}
								return value;
							}
						});
					}
				}
			}
		});

		grid = $("#maingrid").ligerGrid({
			toolbar : {},
			width : '100%',
			height : '99%',
			columns : columns,
			checkbox : false,
			rownumbers : true,
			isScroll : true,
			alternatingRow : true,//附加奇偶行效果行
			usePager : false,
			dataAction : 'server',//从后台获取数据
			method : 'post',
			url : '${ctx}/rpt/frs/rptfill/changeAnalysis?rptId=' + base_InitRptId + "&orgNo=" + base_OrgNo
					+ "&dataDate=" + base_DataDate + "&colId=" + colIds[0].id
		});
	}

	function findKey (obj, value, compare = (a, b) => a === b) {
		return Object.keys(obj).find(k => compare(obj[k], value))
	}

	function initAnalysisButtons() {
		var btns = [{
			text : '导出',
			click : warnDownload,
			icon : 'fa-download'
		}];
		BIONE.loadToolbar(grid, btns, function() {});
	}

	//初始化分析模式
	function initAnalysis(){
		downdload = $('<iframe id="download"  style="display: none;"/>');
		$('body').append(downdload);
		$("#spread").append($('<div id="searchbox" class="searchbox"><form id="formsearch"><div id="search"></div></form><div id="searchbtn" class="btn-group"></div></div> <div id="maingrid" class="maingrid"></div>'))
		initIndexCol();
		initLigerForm();
		//设置初始值
		$.ligerui.get("indexNoBox").selectValue(colIds[0].id);
		initGrid();
		initAnalysisButtons();

	}

	//分析模式-导出
	function warnDownload(){
		downdload.attr('src', "${ctx}/rpt/frs/rptfill/anlysisExp?rptId=" + base_InitRptId
				+ "&templateId=" + base_TmpId + "&orgNo=" + base_OrgNo + "&dataDate=" + base_DataDate + "&colId=" + curColId);
	}

	function searchText(){
		var activeSheet=null;
		if(spreadDesign != null){
			activeSheet = spreadDesign.getActiveSheet();
		}else{
			activeSheet = View.spread.getActiveSheet();
		}
		var searchCondition = new GC.Spread.Sheets.Search.SearchCondition();
		searchCondition.searchString = $("#searchAll").val();
		searchCondition.startSheetIndex = View.spread.getActiveSheetIndex();
		searchCondition.endSheetIndex = View.spread.getActiveSheetIndex();
		searchCondition.searchOrder = GC.Spread.Sheets.Search.SearchOrder.nOrder;
		searchCondition.searchTarget = GC.Spread.Sheets.Search.SearchFoundFlags.cellText;
		searchCondition.searchFlags = GC.Spread.Sheets.Search.SearchFlags.ignoreCase | GC.Spread.Sheets.Search.SearchFlags.useWildCards;
		var searchresult= activeSheet.search(searchCondition);
		if(searchresult.foundRowIndex != -1 && searchresult.foundColumnIndex != -1){
			activeSheet.setActiveCell(searchresult.foundRowIndex, searchresult.foundColumnIndex);
			activeSheet.showCell(searchresult.foundRowIndex, searchresult.foundColumnIndex, GC.Spread.Sheets.VerticalPosition.center, GC.Spread.Sheets.HorizontalPosition.center);
		}
	}
	//加载报表配置
	function initConfig() {
		require.config({
			baseUrl : "${ctx}/plugin/js/",
			paths:{
				"design" : "cfg/views/rptdesign",
				"utils" : "cfg/utils/designutil"
			}
		});
		require(["design" , "utils"] , function(ds , utils){
			Utils = utils;
			var ajaxData = {
					templateId : base_TmpId,
					verId : base_verId,
					getChildTmps : false
			};
			var settings = {
					targetHeight : ($(window).height() - $("#maintab").height() - 10),
					templateType : templateType,
					ctx : "${ctx}",
					readOnly : false ,
					showBusiType : true , // 可以显示业务标识（人行标识）
					initFromAjax : true,//是否获取报表配置数据
					ajaxData : ajaxData,
					readOnly : true,
					cellDetail : true,
					toolbar : false,
					canUserEditFormula : false,
					onSelectionChanged : spreadSelectionChanged,
					isRptIdxCfg : false,//是否报表指标配置功能
					isBusiLine : false
			};
			Design = ds;
			spreadDesign = ds.init($("#spreadConfig") , settings);
			RptIdxInfo = ds.RptIdxInfo;
			// 单元格信息
			initDetailForm();
		});
	};
	
	// 初始化布局
	function initLayout(){
		// 初始化ligerlayout
		var centerWidth = $("#center").width();
		layout = $("#designLayout").ligerLayout({ 
			leftWidth : (centerWidth - 220) * 0.21,
			centerWidth : (centerWidth - 220) * 0.79,
			rightWidth : 220,
			allowRightResize : true 
		});		
		$("#rightDiv").css("overflow", "auto");
		$("#rightDiv").css("height", $(".l-layout-center").height() - 40);
	}
	
	function initDetailForm(){
		// 初始化单元格明细模板内容
		$.ajax({
			cache : false,
			async : false,
			url : "${ctx}/report/frame/design/cfg/getFormDetailDatas",
			dataType : 'json',
			type : "post",
			success : function(result){
				if(result != null
						&& typeof result != "undefined"){
					var objTmp = {template : result};
					$("#rightCellForm").html($("#template-content").tpl(objTmp));
					// 初始化属性部分的数据绑定
					initCellForm();
				}
			}
		});
	}
	
	// 初始化右侧单元格信息form
	function initCellForm(){
		// 初始化ko数据绑定
		if(ko  != null
				&& typeof ko == "object"
				&& RptIdxInfo != null
				&& typeof RptIdxInfo != "undefined"){
			var tmpCellNm = "";
			var tmpCellNo = "";
			var sheet = spreadDesign.getActiveSheet();
			var seq = sheet.getTag(sheet.getActiveRowIndex(), sheet.getActiveColumnIndex(), GC.Spread.Sheets.SheetArea.viewport);
			var rptIdxTmp = Design.rptIdxs[seq];
			selectionIdx=RptIdxInfo.newInstanceKO();
			if(rptIdxTmp){
				$("#rightCellForm [name='cellNo']").val(Utils.initAreaPosiLabel(sheet.getActiveRowIndex() , sheet.getActiveColumnIndex()));
				$("#rightCellForm [name='cellNm']").val(rptIdxTmp.cellNm);  //某一单元格打开后, 更新右侧表单单元格名称
				RptIdxInfo.initIdxKO(selectionIdx,rptIdxTmp);
			}
			ko.cleanNode($("#rightCellForm")[0]);
			ko.applyBindings(selectionIdx , $("#rightCellForm")[0]);
			
			//过滤指标名称
			var idxTmpAll = Design.rptIdxs;
			for(var i in idxTmpAll){
				tmpCellNm = idxTmpAll[i].cellNm;
				tmpCellNo = idxTmpAll[i].cellNo;
				if(tmpCellNm == "" || tmpCellNm == null){
					idxTmpAll[i].cellNm = idxTmpAll[i].cellNo;
				}
			}
		}
	}
	
	// spread选择区域发生变化事件扩展
	function spreadSelectionChanged(sender , args){
		var currSheet = Design.spread.getActiveSheet();
		var selections = currSheet.getSelections();
		if(selections.length == 1){
			var currSelection = selections[0];
			if(currSelection.colCount == 1
					&& currSelection.rowCount == 1){
				// 只有一个单元格，不处理（一个单元格的处理交由enterCell事件）
				spreadEnterCell2(currSelection.row , currSelection.col);
				return ;
			} else {
				var spans = Design.getSelectionSpans(currSheet);
				if(spans
						&& typeof spans.length != "undefined"
						&& spans.length == 1
						&& spans[0].rowCount == currSelection.rowCount
						&& spans[0].colCount == currSelection.colCount){
					// 选中了多行多列，但是一个合并的单元格
					spreadEnterCell2(currSelection.row , currSelection.col);
					return ;
				}
			}
		}
		var font=Utils.getFont( currSheet.getCell(currSheet.getActiveRowIndex(), 
				currSheet.getActiveColumnIndex()).font());
		var fontsize=font.fontsize;
		for(var c in selections){
			var currSelection = selections[c];
			for(var i=0;i<currSelection.rowCount;i++){
				for(var j=0;j<currSelection.colCount;j++){
					if(fontsize!=Utils.getFont(currSheet.getCell(currSelection.row+i,currSelection.col+j).font()).fontsize){
						fontsize="  pt";
						break;
					}
				}
			}
		}
		selectionToolBar.font(font.font);
		selectionToolBar.fontSize(fontsize.substring(0,fontsize.length-2));
		selectionToolBar.bold(font.bold);
		selectionToolBar.italic(font.italic);
		selectionToolBar.textDecoration( currSheet.getCell(currSheet.getActiveRowIndex(), 
				currSheet.getActiveColumnIndex()).textDecoration()?currSheet.getCell(currSheet.getActiveRowIndex(), 
						currSheet.getActiveColumnIndex()).textDecoration():"");
		var batchObj = Design.generateBatchSelObj();
		if(batchObj != null){
			for(var i in selectionIdx){
				var reg = /^_.*Batch$/;
				if(reg.test(i)){
					selectionIdx[i](false);
				}
			}
			RptIdxInfo.initIdxKO(selectionIdx , batchObj);
		}
	}
	
	//spread移入单元格事件扩展
	function spreadEnterCell2(row , col){
		var tmpCellNm = "";
		var tmpCellNo = "";
		var cellNo="";
		var cellTmp = spreadDesign.getActiveSheet().getCell(row , col);
		var isFormal = (cellTmp.formula() != null && cellTmp.formula() != "") ? true : false;
		configIndexNo = "";
		srcIndexNo = "";
		if(RptIdxInfo.initIdxKO
				&& typeof RptIdxInfo.initIdxKO == "function"){
			var seq = spreadDesign.getActiveSheet().getTag(row, col, GC.Spread.Sheets.SheetArea.viewport);
			if(seq != null
					&& Design.rptIdxs[seq] != null
					&& typeof Design.rptIdxs[seq] != "undefined"){
				// 若是报表指标
				var rptIdxTmp = Design.rptIdxs[seq];
				configIndexNo = rptIdxTmp.realIndexNo;
				srcIndexNo = rptIdxTmp.indexNo;
				RptIdxInfo.initIdxKO(selectionIdx , rptIdxTmp);
				selectionIdx.seq(seq);
				if(selectionIdx.cellType() == "04"){
					// excel公式单元格
					selectionIdx.excelFormula(cellTmp.formula());
				}
				
				//指标单元格过多时，采用单元格内容异步加载模式
				var cellLabel = spreadDesign.getActiveSheet().getValue(row, col);
				if(!cellLabel){
					cellLabel = rptIdxTmp.cellLabel("normal");
					spreadDesign.getActiveSheet().setValue(row, col, cellLabel);
				}
				
				if(Utils){
					tmpCellNm = rptIdxTmp.cellNm;
					tmpCellNo = rptIdxTmp.cellNo;
					cellNo=Utils.initAreaPosiLabel(row , col);  // 维护单元格编号
					if(tmpCellNm == "" || tmpCellNm == tmpCellNo || tmpCellNo ==""){  //同步单元格名称
						selectionIdx.cellNm(cellNo);
						selectionIdx.cellNo(cellNo);
					}
				}
			}else if(isFormal === true){
				// 将该单元格定义成为公式报表指标
				var uuid = Design.Uuid.v1();
				currSheet.setTag(row, col, uuid);
				var rptIdxInfoTmp = Design.RptIdxInfo.newInstance("04");
				rptIdxInfoTmp.excelFormula = cellTmp.formula();
				var realIndexNo = Uuid.v1().replace(/-/g, '');//去掉报表编号_单元格编号，统一改为：uuid的写法
				Design.rptIdxs[uuid] = rptIdxInfoTmp;
				RptIdxInfo.initIdxKO(selectionIdx , rptIdxInfoTmp);
				selectionIdx.seq(seq);
				selectionIdx.excelFormula(cellTmp.formula());
				// 维护单元格编号
				if(Utils){					
					cellNo=Utils.initAreaPosiLabel(row , col);
					//指标配置表存在，则使用配置表中的“报表指标编号”
					if(Design.rptIdxCfg[cellNo] && Design.rptIdxCfg[cellNo] != "" && Design.rptIdxCfg[cellNo] != null){
						realIndexNo = Design.rptIdxCfg[cellNo];
					}
					selectionIdx.realIndexNo = realIndexNo;
					tmpCellNm = rptIdxTmp.cellNm;
					tmpCellNo = rptIdxTmp.cellNo;
					if(tmpCellNm == "" || tmpCellNm == tmpCellNo || tmpCellNo ==""){
						selectionIdx.cellNm(cellNo);
						selectionIdx.cellNo(cellNo);
					}
				}
			}else if(seq != null
					&& Design.commonCells[seq] != null
					&& typeof Design.commonCells[seq] != "undefined"){
				// 若是报表指标
				var commonCellTmp = Design.commonCells[seq];
				RptIdxInfo.initIdxKO(selectionIdx , commonCellTmp);
				selectionIdx.seq(seq);
			}else{
				var uuid = Design.Uuid.v1();
				spreadDesign.getActiveSheet().setTag(row, col, uuid);
				var commonCellTmp = Design.RptIdxInfo.newInstance("01");
				commonCellTmp.content = cellTmp.value();
				Design.commonCells[uuid] = commonCellTmp;
				RptIdxInfo.initIdxKO(selectionIdx , commonCellTmp);
				selectionIdx.seq(uuid);
			}
		}
		$("#rightCellForm [name='cellNo']").val(cellNo);
		if(Design.rptIdxCfg[cellNo] && Design.rptIdxCfg[cellNo] != "" && Design.rptIdxCfg[cellNo] != null){
			$("#realIndexNo").attr("readonly", true);
		}else{
			$("#realIndexNo").attr("readonly", false);
		}
	}
	
	function initSearchInput(){
		var width = $("#maintab").outerWidth() - $("#dataButton").outerWidth(true) - $("#validButton").outerWidth(true) - $("#explainButton").outerWidth(true) - $("#flowButton").outerWidth(true) - $("#analysisButton").outerWidth(true);
		$("#otherBtn").append($('<div><input type="text" id="searchAll" placeholder="全文搜索" class="search-all"/><img id="searchBtn" src="../../../images/oper/search.png" style="margin-left: -35px; width: 22px;"/></div'));
		$("#otherBtn").width(width - 30);
		$("#searchAll").width(width - 72);
		$("#otherBtn").append($('<div class="rpt-config" id="rptResult"><img src="../../../images/oper/idx.png" style="width: 20px;padding: 0 3px;"/><span>填报模式</span></div>' +
				'<div class="rpt-config"  id="rptConfig"><img src="../../../images/oper/config.png" style="width: 20px;padding: 0 3px;"/><span>溯源模式</span></div>' +
				'<div class="rpt-config"  id="changeAnalysis"><img src="../../../images/oper/changeAnalysisBtn.png" style="width: 20px;padding: 0 3px;"/><span>分析模式</span></div>'));
		$("#otherBtn").append($('<div class="rpt-config" style="clear:both;color:red;" id="operDesc"><img src="../../../images/oper/tip.png" style="width: 20px;padding: 0 3px;"/><span>使用提醒</span></div>'));
	}
	
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
			var targetHeight = ($(window).height() - $("#maintab").height() - 10);
			var settings = {
				targetHeight : targetHeight,
				ctx : "${ctx}",
				readOnly : false,
				cellDetail : true,
				toolbar : false,
				canUserEditFormula : false,
				inValidMap : window.top.color,
				initFromAjax : true,
				searchArgs : JSON2.stringify(argsArr),
				ajaxData : {
					rptId : base_InitRptId,
					dataDate : base_DataDate,
					fileName : fileName,
					orgNm : base_OrgNm,
					templateType : templateType,
					isPaging : isPaging
				},
				onEnterCell : spreadEnterCell,
				onCellDoubleClick : spreadDbclkCell,
				onEditEnded : spreadEditEnded
			};
			View = view;
			spread = view.init($("#spread"), settings);
			base_Spread = spread;
		});
	};

	//选中事件
	function spreadEnterCell(sender, args, info) {
		window.posi = View.Utils.initAreaPosiLabel(args.row, args.col);
		var selectCellNo = info.cellNo;
		var selectIdxNo = info.indexNo;
		//还原校验公式涉及单元格的变色
		for (var cellNo in cellColor){
			var backColor = cellColor[cellNo];
			if(backColor){
				var rowCol = View.Utils.posiToRowCol(cellNo);
				var myCell = View.spread.getActiveSheet().getCell(Number(rowCol.row), Number(rowCol.col)).backColor(backColor);
			}else{//默认背景颜色就是白色
				var rowCol = View.Utils.posiToRowCol(cellNo);
				var myCell = View.spread.getActiveSheet().getCell(Number(rowCol.row), Number(rowCol.col)).backColor("#ffffff");
			}
		}
		if(selectIdxNo){
			base_selectDataPrecision = info.dataPrecision;//当前选择单元格指标精度
			base_selectUnit = info.unit;//当前选择单元格指标单位
		}
		base_selectIdxNm = info.cellNm;
		base_selectIdxNo = selectIdxNo;
		base_selectCellNo = selectCellNo;
		base_selectDsId = info.dsId;
		searchCheakNoPass(selectIdxNo);
		var selectTabId = tabObj.getSelectedTabItemID();
		if(selectIdxNo){
			tabObj.reload(selectTabId);
		}
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

	//改变机构值，应该是子页面会用到
	function changeOrgValue(orgNo, orgNm) {
		$("#org").val(orgNo);
		$("#orgBox").val(orgNm);
	}

	//刷新数据
	tmp.refreshData = function () {
		View.ajaxJson(null, null, null, "/report/frame/tmp/view/getViewInfo?d="+new Date().getTime());
	};

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
					if("02" != templateType){
						//只有单元格报表有导出初始值和查看初始值，明细只能恢复初始值
						result[0] = "INITBTNBYDETAIL";
						result[2] = "DETAILIMPORTBTN";
					}
					//明细不定长报表有新增行功能
					if(("N" == fixedLength) && ("02" != templateType)){
						result.push("ADDROWSBTN");
						result.push("DELROWSBTN");
					}
					if("01" == templateType){//明细报表
						result[3] = "DETAILVALIDBTN";
						result[20] = "";
					}
					var time = base_DataDate.substring(4);
					if(base_OrgType == "03" && time == '0101'){
						result.push("YEAREND");
					}
					for ( var i in result) {
						if (buttonArray[result[i]]) {
							createButton(buttonArray[result[i]], i);
						}
					}
				}
			},
			error : function() {
				BIONE.tip("获取功能按钮异常，请联系系统管理员");
			}
		});
		$("#dataButton").append($('<div class="div-desc">数据区</div>'));
		$("#validButton").append($('<div class="div-desc">校验区</div>'));
		$("#explainButton").append($('<div class="div-desc">操作区</div>'));
		$("#flowButton").append($('<div class="div-desc">流程区</div>'));
		$("#analysisButton").append($('<div class="div-desc">追溯与分析</div>'));
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
			dataType : 'json',
			data : {
				rptId : base_InitRptId,
				dataDate : base_DataDate
			},
			type : "post",
			success : function(result) {
				if (result) {
					base_TmpId = result.tmpId;
					base_verId = result.verId;
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
					if(tmp.isUpdateData()){//修改
						tmp.save(true);
					}
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
								clickCalculation = true;
							}
						},
						error: function(){
							BIONE.tip("报表计算失败，请联系系统管理员");
						}
					});
				}
		 });
	};

	// 查看初始值
	// tmp.viewInit = function() {
	// 	base_QueryInit = true;
	//     var url = "/report/frame/tmp/view/getViewInfo?QueryInit="+base_QueryInit+"&d="+new Date().getTime();
	//     View.ajaxJson(null,null,true,url);
	// };

	//查看初始值
	tmp.viewInit = function(queryInit) {
		if(typeof(queryInit) != 'undefined'){
			base_QueryInit = queryInit;
		}else{
			base_QueryInit = false;
		}
		var url = "/report/frame/tmp/view/getViewInfo?QueryInit="+base_QueryInit+"&d="+new Date().getTime();
		View.ajaxJson(null,null,true,url);
	};
	
	//数据导出 
	//queryInit:true 导出初始值
	//queryInit:false 导出实际值
	tmp.download = function(queryInit){
		if(typeof(queryInit) != 'undefined'){
			base_QueryInit = queryInit;	
		}else{
			base_QueryInit = false;
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
	tmp.newImportData = function () {
		if("01" == templateType || "03" == templateType){
			tmp.coverImportData();
		}
		if("02" == templateType){
			tmp.importData()
		}
	}

	//数据导入
	tmp.importData = function() {
		BIONE.commonOpenDialog('导入数据文件', 'uploadWin', 600, 330,
				"${ctx}/rpt/frs/rptfill/showTab?path=rpt-todowork-uploadfile&dataDate="
						+ base_DataDate + "&orgNo=" + base_OrgNo + "&rptId=" + base_InitRptId
						+ "&taskInsId=" + base_TaskInsId + "&type=" + base_OrgType
						+ "&flag=ONE&entry=EXCEL&importFileType=xls,xlsx&uuid="
						+ new Date().getTime());
	};
	//明细报表-覆盖导入
	tmp.coverImportData = function(){
		//加载报表配置的模型信息
		initRptSourceDs();
		BIONE.commonOpenDialog('导入数据文件', 'uploadWin', 600, 330,
				'${ctx}/rpt/frs/rptfill/detailUpload?rptId=' + base_InitRptId + '&tmpId=' + base_TmpId
				+ '&dataDate=' + base_DataDate + '&orgNo=' + base_OrgNo
				+ '&taskInsId=' + base_TaskInsId + '&moduleParam=' + encodeURI(encodeURI(JSON.stringify(moduleParam))));
	}

	function initRptSourceDs(){
		$.ajax({
			cache : false,
			async : false,
			url : "${ctx}/rpt/frs/rptfill/initRptSourceDs",
			dataType : 'json',
			data : {
				tmpId : base_TmpId,
				dataDate : base_DataDate
			},
			type : "post",
			success : function(result){
				if(result != null){
					moduleParam = result;
				}else{
					BIONE.tip("当前报表没有配置模型字段，请修改报表配置！")
				}

			},
			error:function(){
				BIONE.tip("数据加载异常，请联系系统管理员");
			}
		});
	}

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
		var param = {'taskIns' : base_TaskInsId, 'orgNo' : base_OrgNo, 'rptId' : base_InitRptId, 'dataDate' : base_DataDate, 'type' : base_OrgType,'taskId' :base_TaskId,'operType' :btnFlag};
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
				if (result.result == "warn") {
                    BIONE.tip("已是当前任务所下发的最底层机构，无需汇总");
                    return;
                }else if("ERROR" == result.result){
					return false;
				}else if(("YES" == result.result) && result.ins){
					var height = $(window).height() - 30;
					var width = $(window).width() - 80;
					var taskInsIds = [];
					taskInsIds.push(base_TaskInsId);
					BIONE.commonOpenDialog("未归档子机构任务实例", "taskInsChildWin", width, height, "${ctx}/rpt/frs/rptfill/showTab?path=rptfill-org-child-ins&sts=0&params="
							+ JSON2.stringify(params) + "&taskInsIds=" + taskInsIds.join(",") + "&orgNo=" + base_OrgNo + "&rptId="
							+ base_InitRptId + "&dataDate=" + base_DataDate + "&taskId=" + base_TaskId + "&btnFlag=" + btnFlag + "&templateType=" + templateType + "&sumDataType=" + base_SumType + "&taskType=" + base_OrgType
							+ "&tmpId=" + base_TmpId, null);
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
				sumType : base_SumType,
				orgType : base_OrgType
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

	//发起机构汇总
	function getIdxCalcCells(){
		$.ajax({
			cache : false,
			async : true,
			url : "${ctx}/report/frame/tmp/view/getIdxCalcCells",
			dataType : 'json',
			data : {
				templateId : base_TmpId,
				verId : base_verId
			},
			type : "post",
			success : function(result){
				if(result && "success" == result.status){
					idxCalcCells = result.idxCalcCells;
				}
			},
			error:function(){
				BIONE.tip("获取表间取数单元格异常，请联系系统管理员");
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
				//明细类字段不做单位变换
				if("02" != cells[i].type){
					newValue = View._changValue(cells[i].unit, newValue, cells[i].unit);
				}
				if(newValue != oldValue){
					if((!isNaN(newValue)) && (!isNaN(oldValue)) && cells[i].displayFormat != "04"){//判断是否是数字
						if(parseFloat(newValue) != parseFloat(oldValue)) {
							return true;
						}
					} else {
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
		var cellNo = "";
		//当前选中的是指标单元格,只有指标单元格才有修改记录
		if(base_selectIdxNo){
			cellNo = base_selectCellNo;
		}
		BIONE.commonOpenDialog(
						'查看修改记录',
						'hisViewWin',
						 $(window).width() - 200, $(window).height(),
						'${ctx}/rpt/frs/rptfill/showTab?path=rpt-todowork-his-detail&taskInstanceId='
								+ base_TaskInsId
								+ '&cellNo='
								+ cellNo
								+ "&templateId=" + base_TmpId+ "&orgType=" + base_OrgType + "&orgNo=" + base_OrgNo
				                + "&taskId=" + base_TaskId + "&dataDate=" + base_DataDate);
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
					if(idxCalcCells && idxCalcCells.length > 0){ //判断是否有表间计算单元格
						if(!clickCalculation){
							$.ligerDialog.confirm("是否先进行表间计算", "提示", function(yes) {
								if(!yes){
									tmp.save(flag);
								}
							});
						} else {
							clickCalculation = false;
							tmp.save(flag);
						}
					} else {
						tmp.save(flag);
					}
				}
			});
		}else{
			BIONE.tip("数据未修改，无需保存");
		}	
	}
	
	//填报数据保存
	//flag true-正常保存，false-检验、汇总、计算、提交之前保存 fuFlag 功能标识  param 参数 （根据功能标识不同而不同）
	tmp.save = function(flag, func, message, param) {
		if(!message){
			message = "正在保存中...";
		}
		var isLeafNode = tmp.isLeafNode();
		var changeInfo = View.generateChangeInfo(isLeafNode);
		if(changeInfo.isValid == false){
			BIONE.tip("填报数据有误，无法保存");
			return;
		}
		var formulaCellInfo = View.getFormulaCellInfo();
		BIONE.showLoading(message);
		$.ajax({
			async : false,
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
				detailCells : JSON2.stringify(changeInfo.detailCells),
				pageSize : $("#pageSize").val(),
				pageNumber : $(".current").text() 
			}, 
			success: function(result) {
				if(flag){//正常保存后取消遮盖，其他的业务逻辑处理就先不取消遮盖
					BIONE.hideLoading();
				}
				if(result && result.status){
					if("error" == result.status){
						var errorMsg = result.msg ? result.msg : "保存失败,请联系管理员!";
						var config = {};
						if(errorMsg.length > 80){
							config = {
								width: $(window).width() * 0.8
							};
						}
						BIONE.showError(errorMsg, null, config);
					}
				}else{
					View.refreshCellVals(result.res);
					View.refreshOldCellVals(result.oldRes);
					if(templateType && (templateType == "01" || templateType == "03")){
						if(isPaging && isPaging == "Y"){
							updateTask(flag, "/report/frame/tmp/view/getViewInfo?d="+new Date().getTime(), ($(".current").text() - 1) * pageSize + 1, pageSize);
						}
					}else{
						updateTask(flag);
					}
					if (typeof (func) == "function") {
						func(param);
					}
					if(result.mgs){
						BIONE.showError("报表第" + result.mgs + "行,主键异常无法进行数据修改。");
					}else{
						BIONE.tip("数据保存成功");
					}
				}
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
						//updateColor(url, start, step);
					}
				}
				if("ERROR" == result.result){
					BIONE.showError("没有需要更新的任务");
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
		// 检查修改记录是否已填写
		var updateSts = getCellUpdateSts();
		if(rsStsObj.failFlag && updateSts){
			var isContainOrg  = submitJudge('01', implementSubmit);
		}
	}
	
	// 检查修改记录是否已填写
	function getCellUpdateSts(){
		var updateSts = false;
		$.ajax({
			cache : false,
			async : false,
			url : "${ctx}/rpt/frs/rptfill/getCellUpdateSts",
			dataType : 'json',
			data : {
				dataDate : base_DataDate, 
				taskInstanceId : base_TaskInsId,
				tmpId : base_TmpId
			},
			type : "post",
			success : function(result){
				if(!result.flag){
					BIONE.showError("您修改的单元格中有些被锁定，请填写修改说明后再提交！");
				}else{
					updateSts = true;
				}
			}
		});
		return updateSts;
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
				rptOperType : base_OperType,
				dataDate : base_DataDate, 
				orgNo : base_OrgNo,
				tmpId : base_TmpId
				
			},
			beforeSend : function() {
				BIONE.showLoading("正在提交，请稍候...");
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
				parent.BIONE.tip("提交成功",8000);
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
				taskInsId : base_TaskInsId,
				orgType : base_OrgType
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
					if(!checkType){//判断是不是归档或者结束填报操作
						if(templateType != '01'){//明细报表不需要逻辑校验
							if((logicRs == null || logicRs == '04' || logicRs == '06')){//逻辑校验未通过
								var result = tmp.getLogicValidResult();
								if(logicRs != null){
									//判断逻辑校验是否有未通过项，监管制度校验公式未通过不能提交，自定义校验不通过需添加说明。
									//var result = tmp.getLogicValidResult();
									var result01 = result.result01;//强校验
									var result02 = result.result02;//软校验
									if(result01 == 0 && result02 == 0){//未通过的校验处理了
										if(base_OrgType == '03' && (sumpartRs == null || sumpartRs == '04' || sumpartRs == "06")){//总分校验未通过
											BIONE.showError("总分校验未通过，请核对数据！");
											returnObj.failFlag = false;
										}else{
											if((warnRs == null || warnRs == '04' || warnRs == '06' )){
												$.ligerDialog.confirm("预警校验未通过或者未校验，是否继续？", function(yes) {
													if(yes){
														returnObj.failFlag = true;//可以正常归档
													}else{
														returnObj.failFlag = false;//不能归档
													}
												});
											}
										}
									}else{
										BIONE.showError("逻辑校验有未通过项，【监管制度】未通过"+result01+"条，【自定义】未通过"+result02+"条。");
										returnObj.failFlag = false;
									}
								}else{
									var result03 = result.result03;//软校验
									if(result03 > 0){
										BIONE.showError("逻辑校验未校验，请您先进行校验！");
										returnObj.failFlag = false;
									}else{
										returnObj.failFlag = true;
									}
								}
							}else{//逻辑校验通过
								if(base_OrgType == '03' && (sumpartRs == null || sumpartRs == '04' || sumpartRs == "06")){//总分校验未通过
									BIONE.showError("总分校验未通过，请核对数据！");
									returnObj.failFlag = false;
								}else{
									if((warnRs == null || warnRs == '04' || warnRs == '06' )){
										$.ligerDialog.confirm("预警校验未通过或者未校验，是否继续？", function(yes) {
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
				}
			},
			error:function(){
				BIONE.tip("检查失败，请联系系统管理员");
			}
		});
		return returnObj;
	}
	
	tmp.getLogicValidResult = function(){
		var validResult = {};
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
				validResult = result;
			}
		});
		return validResult;
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
		var obj = {"rptId" : base_InitRptId, "orgNo" : base_OrgNo, "dataDate" : base_DataDate, "templateId" : base_TmpId};
		var group = {"DataDate" : base_DataDate, "OrgNo" : base_OrgNo};
		var checkName = "";
		if("logic" == checkType){
			group.LogicCheckRptTmpId = [base_TmpId];
			obj.logicRs = rsStsObj.logicRs;
			checkName = '逻辑校验';
		}
		if("sumpart" == checkType){
			group.SumCheckRptTmpId = [base_TmpId];
			if("02" == base_OrgType){
				group.IsCfgSum = "Y";
			}
			obj.sumpartRs = rsStsObj.sumpartRs;
			checkName = '总分校验';
		}
		if("warn" == checkType){
			group.WarnCheckRptTmpId = [base_TmpId]; 
			obj.warnRs = rsStsObj.warnRs;
			checkName = '预警校验';
		}
		if("zero" == checkType){
			group.ZeroCheckRptTmpId = [base_TmpId];
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
				isBatchCheck : isBatchCheck,
				verId : base_verId
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
					if(result.taskCheckSts == "05" || result.isPass == true){
						base_CheckResult += checkName + " ： 校验通过。<br>";
					}else if(result.taskCheckSts == "06" || result.isPass == false){
						base_CheckResult += checkName + " ：有未通过项,请检查。<br>";
					}else if(result.taskCheckSts == "04"){
						base_CheckResult += checkName + " ：校验失败,请检查日志。<br>";
					}else if(result.taskCheckSts == "00"){
						base_CheckResult += checkName + " ：当前机构是末级填报机构，无需进行总分校验。<br>";
					}else if(result.taskCheckSts == "10"){
						base_CheckResult += checkName + " ：未配置校验公式，无需进行校验。<br>";
					}
					if("OK" != result.result){
						var errorMsg = result.result ? result.result : "校验异常,请联系管理员!";
						var config = {};
						if(errorMsg.length > 80){
							config = {
								width: $(window).width() * 0.8
							};
						}
						BIONE.showError(errorMsg, null, config);
					}
				}else if("01" == templateType){
					base_CheckResult = result.msg;
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
				+'&checkType='+ checkType + "&taskInsId=" + base_TaskInsId + "&templateType=" + templateType);
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
				cellNo : base_selectCellNo
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
							if(("logicTab" != key) && ("logicByExternalTab" != key) && ("warnTab" != key)){
								$("li[tabid="+ key +"]").css("display", "none");
							}
						}
					}
					tabObj.selectTabItem("operTab");//默认选择报表处理
				}
			},
			error: function(){
				BIONE.tip("查询异常，请联系系统管理员");
			}
		});
	}
	
	//初始化顶部tab页
	function initMaintab(){
		var flag = false;//addtab后为true
		tabObj = $("#maintab").ligerTab({
			changeHeightOnResize : true,
			dragToMove: false,
			contextmenu : false ,
			onAfterSelectTabItem : function(tabId) {
				//指标单元格可以查看以下内容
				if(base_selectIdxNo){
					var checkResultParam = 'dataDate='+ base_DataDate +'&orgNo='+ base_OrgNo +'&rptIndexNo='+ base_selectIdxNo +'&rptTmpId='+ base_TmpId +'&cellNo='+ base_selectCellNo +'&taskInstanceId='+ base_TaskInsId +'&rptId='+ base_InitRptId +'&type='+ base_OrgType;
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
					}else if("lastDataTab" == tabId){
						checkResultParam = checkResultParam + "&dataUnit=" + base_selectUnit + "&dataPrecision=" + base_selectDataPrecision;
						$("#"+tabId).attr('src', '${ctx}/rpt/frs/rptfill/lastDataTab?' + checkResultParam);
					}
				}else if(flag && (tabId != 'operTab') ){
					//明细单元格可以查看以下内容
					if(base_selectDsId){
						var checkResultParam = 'dataDate='+ base_DataDate +'&orgNo='+ base_OrgNo +'&rptIndexNo='+ base_selectIdxNo +'&rptTmpId='+ base_TmpId +'&cellNo='+ base_selectCellNo +'&taskInstanceId='+ base_TaskInsId +'&rptId='+ base_InitRptId +'&type='+ base_OrgType;
						$("#"+tabId).attr('data-idxNo', base_selectIdxNo);
						if("logicTab" == tabId){
							$("#"+tabId).attr('src', '${ctx}/rpt/frs/rptfill/logicResult?' + checkResultParam + '&checkType=01');
						}else if("explainTab" == tabId){
							$("#"+tabId).attr('src', '${ctx}/rpt/frs/rptfill/cellExplain?' + checkResultParam);
						}else{
							tabObj.selectTabItem("operTab");//默认选择报表处理页签
							BIONE.tip("请先选择一个指标单元格！");
							return;
						}
					}else if(("logicTab" == tabId) || ("explainTab" == tabId)){
						tabObj.selectTabItem("operTab");//默认选择报表处理页签 
						BIONE.tip("请先选择一个指标单元格或明细单元格！");
						return;
					}else{
						tabObj.selectTabItem("operTab");//默认选择报表处理页签 
						BIONE.tip("请先选择一个指标单元格！");
						return;
					}
				}
				return false;
			} 
		});
		var iframeHeight = $("#maintab").height() - 26;
		tabObj.addTabItem({
			tabid : "operTab",
			text : "报表处理",
			showClose : false,
			content : '<div id="dataButton" class="oper-div"></div><div id="validButton" class="oper-div"></div>'
					+'<div id="explainButton" class="oper-div"></div><div id="flowButton" class="oper-div"></div>'
					+'<div id="analysisButton" class="oper-div"></div><div id="otherBtn" class="oper-div" style="border: none;min-width: 125px;"></div>'
		});
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
/*  	tabObj.addTabItem({
			tabid : "lastDataTab",
			text : "指标数据",
			showClose : false,
			content : '<iframe id="lastDataTab" style="height:'+ iframeHeight +'px" src="" data-idxNo= ""></iframe>'
		}); */
		tabObj.selectTabItem("operTab");//默认选择报表处理页签 
		flag = true;
	}

	// 重写创建按钮方法,新增创建可点击下拉的按钮
	createButton = function(options, num) {
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
        var btn;
		if(p.menu){
			var btnId = "btn"+num;
			var itemsId = "items"+num;
			btnHtml = '<div style="float:left; width:48px; margin-left: 10px;position: relative; cursor: pointer;" id="'+btnId+'"><img src="../../../images/oper/'+p.operNo+'.png" alt=""  data-toggle="dropdown"/><div class="oper-font" data-toggle="dropdown">'+ p.text +'</div>' +
					  '<ul class="dropdown-menu" id="'+itemsId+'" style="margin:0;padding:0;max-height:78px;overflow-y: auto;">';
			var menuChilds = p.menu.items;
			for ( var i = 0; i < menuChilds.length ; i++){
				btnHtml += '<li style="text-align:center"><a href="javascript:void(0);" onclick="' + menuChilds[i].click + '">' + menuChilds[i].text + '</a></li>';
			}
			btnHtml += '</ul></div>';
			btn = $(btnHtml);
			btn.appendTo(p.appendTo);
		}else{
			var btnHtml = '<div style="float:left; width:48px; margin-left: 10px; position: relative; cursor: pointer;" ';
			if (p.id) {
				btnHtml = btnHtml + 'id="'+ p.id +'"'
			}
			btnHtml += '"><img src="../../../images/oper/' +p.operNo +'.png" alt="" /><div class="oper-font">'+ p.text +'</div></div>';
			btn = $(btnHtml);
			if (p.click) {
				btn.click(p.click);
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
						success: function(result){
							if(result.error){
								var errorMsg = result.error ? result.error : "恢复初始值失败,请联系管理员!";
								var config = {};
								if(errorMsg.length > 80){
									config = {
										width: $(window).width() * 0.8
									};
								}
								BIONE.showError(errorMsg, null, config);
								BIONE.hideLoading();
							}else{
								if(templateType && templateType == "02"){//指标类
									View._settings.loadCompleted = tmp.saveInit;
								} 
								tmp.viewInit(true);
							}
						},
						error: function(){
							BIONE.tip("恢复初始值失败，请联系系统管理员");
						}
					});
				}
		 });
	};
	
	//保存初始值
	tmp.saveInit = function(){
		var isLeafNode = tmp.isLeafNode();
		var changeInfo = View.generateChangeInfo(isLeafNode, true);
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
				complete: function(){
					BIONE.hideLoading();
				},
				success: function(result) {
					View._settings.loadCompleted = null;
					BIONE.tip("恢复初始值成功！");
				},
				error:function(){
					BIONE.tip("恢复初始值失败，请联系系统管理员");
				}
			});
	}
	
	//新增行，不定长明细表会使用到
	tmp.addRows = function(){
		BIONE.commonOpenDialog("新增数据","addRows",600,700,"${ctx}/rpt/frs/rptfill/addRows");	
	}
		
	//删除行，不定长明细表会使用到
	tmp.delRows = function () {
		var rowData = [];
		var cellInfos = View.cellInfos;
		var currSheet = window.View.spread.getActiveSheet();
		var selections = currSheet.getSelections();
		if(selections.length > 0){
			$.ligerDialog.confirm("您确定要删除该数据吗?", function(yes) {
				initRptSourceDs();
				for (var i = 0; i<selections.length; i++){
					var currSelection = selections[i];
					var rowNo = currSelection.row;
					var rowCount = currSelection.rowCount;
					for(var j = 1; j <= rowCount; j++){
						for(var key in cellInfos){
							if(key.indexOf(rowNo + j) != -1){
								var cellInfo = cellInfos[key];
								rowData.push(cellInfo.key);
								break;
							}
						}
					}
				}
				if (yes) {
					$.ajax({
						async : false,
						url : "${ctx}/rpt/frs/rptfill/delModuleData",
						dataType : 'json',
						type : 'post',
						data : {
							rowData : JSON2.stringify(rowData),
							moduleParam : JSON2.stringify(moduleParam)
						},
						success: function(result) {
							if(result.status == "true"){
								tmp.viewInit(false);
							} else {
								BIONE.tip(result.msg);
							}
						},
						error:function(){
							BIONE.tip("数据删除异常，请联系系统管理员");
						}
					});
				}
			});
		} else {
			BIONE.tip('请选择要删除的数据!');
		}
	}

	//大集中年结方法
	tmp.yearEnd = function(){
		$.ligerDialog.confirm('确定要进行年结吗？', '年结', function(yes) {
			if (yes) {
				$.ajax({
					async : true,
					url : "${ctx}/rpt/frs/rptfill/rptYearEnd",
					dataType : 'json',
					type : 'POST',
					data : {
						rptId : base_InitRptId,
						orgNo : base_OrgNo,
						dataDate : base_DataDate
					},
					beforeSend : function() {
						BIONE.showLoading("请稍等...");
					},
					success: function(result){
						BIONE.hideLoading();
						if(result.status == 'true'){
							tmp.viewInit(false);
						}else{
							BIONE.tip(result.msg);
						}
					},
					error: function(){
						BIONE.tip("年结失败，请联系系统管理员");
					}
				});
			}
		});
	}

	//查询报表所有批注
	tmp.queryRemark = function () {
		BIONE.commonOpenDialog(
				'查看批注信息',
				'hisViewWin',
				$(window).width() - 200, $(window).height(),
				'${ctx}/rpt/frs/rptfill/showTab?path=rpt-todowork-remark-list&taskId=' + base_TaskId
				+ "&rptId=" + base_InitRptId+ "&orgType=" + base_OrgType + "&orgNo=" + base_OrgNo
				+ "&dataDate=" + base_DataDate);
	}

	//变动分析
	tmp.warnAnalysisBtn = function () {
		if(templateType != "01"){
			BIONE.commonOpenDialog("变动分析", "warnAnalysis",$(document).width() - 100,$(document).height() - 100,
					"${ctx}/rpt/frs/rptfill/warnAnalysisIndex?templateId="+ base_TmpId +"&dataDate=" + base_DataDate +
					"&rptId=" + base_InitRptId + "&orgNo=" + base_OrgNo + "&orgType=" + base_OrgType + "&verId=" + base_verId);
		}else{
			BIONE.tip("请选择一张单元格或综合类报表");
		}
	}

	//填报模式按钮集合
	tmp.rptResultButton = {
			//数据区
			"INITBTN" : { text : '初始值', width : '50px', appendTo : '#dataButton', operNo:'initBtn', isCountWidth : false,
				menu : { items : [ { text : '查看初始值', click : 'tmp.viewInit(true)' }, { text : '恢复初始值', click : 'tmp.init()' },
								   { text : '导出初始值', click : 'tmp.download(\'true\')' } ] }},
			"INITBTNBYDETAIL" : { text : '初始值', width : '50px', appendTo : '#dataButton', operNo:'initBtn', isCountWidth : false,
				menu : { items : [{ text : '恢复初始值', click : 'tmp.init()' }] }},
			"EXPORTBTN" : { text : '数据下载', width : '50px', appendTo : '#dataButton', operNo:'exportBtn', click : tmp.exportRpt, isCountWidth : false},
	  		"IMPORTBTN" : { text : '数据导入', width : '50px', appendTo : '#dataButton', operNo:'importBtn', click : tmp.importData, isCountWidth : false},
	  		"DETAILIMPORTBTN" : { text : '数据导入', width : '50px', appendTo : '#dataButton', operNo:'importBtn', click : tmp.newImportData, isCountWidth : false},
			"SUMBTN" : { text : '机构汇总', width : '50px', appendTo : '#dataButton', operNo:'sumBtn', click : tmp.sumData, isCountWidth : false},
			"EXTERNALCALBTN" : { text : '表间计算', width : '50px', appendTo : '#dataButton', operNo:'externalCalBtn', click : tmp.rptCalculation, isCountWidth : false},
			"YEAREND" : { text : '年结', width : '50px', appendTo : '#dataButton', operNo:'config', click : tmp.yearEnd, isCountWidth : false},
			"HISVIEWBTN" : { text : '修改记录', width : '50px', appendTo : '#dataButton', operNo:'hisviewBtn', click : tmp.openHisview, isCountWidth : false},

			//新增行（不定长报表才会用到）
			"ADDROWSBTN" : { text : '新增行', width : '50px', appendTo : '#dataButton', operNo:'addRowsBtn', click : tmp.addRows, isCountWidth : false},
			"DELROWSBTN" : { text : '删除行', width : '50px', appendTo : '#dataButton', operNo:'delRowsBtn', click : tmp.delRows, isCountWidth : false},
			
			//校验区
			"VALIDBTN" : { text : '数据校验', width : '50px', appendTo : '#validButton', operNo:'validBtn', isCountWidth : false ,
				menu : { items : [ { text : '全部校验', click : 'tmp.check()' }, { text : '逻辑校验', click : 'tmp.checkSingle(\'logic\')' }, { text : '预警校验', click : 'tmp.checkSingle(\'warn\')' },
								   { text : '总分校验', click : 'tmp.checkSingle(\'sumpart\')' }, { text : '零值校验', click : 'tmp.checkSingle(\'zero\')' } ] }},
			"1104VALIDBTN" : { text : '数据校验', width : '50px', appendTo : '#validButton', operNo:'validBtn', isCountWidth : false ,
				menu : { items : [ { text : '全部校验', click : 'tmp.check()' }, { text : '逻辑校验', click : 'tmp.checkSingle(\'logic\')' }, { text : '预警校验', click : 'tmp.checkSingle(\'warn\')' },
						           { text : '总分校验', click : 'tmp.checkSingle(\'sumpart\')' },{ text : '零值校验', click : 'tmp.checkSingle(\'zero\')' } ] }},
			"DETAILVALIDBTN" : { text : '数据校验', width : '50px', appendTo : '#validButton', operNo:'validBtn', isCountWidth : false ,
				menu : { items : [{ text : '逻辑校验', click : 'tmp.checkSingle(\'logic\')' }] }},
				"CHECKRESULTBTN" : {text : '结果', width : '25px', appendTo : '#validButton', operNo:'checkResultBtn', click : function(){tmp.checkResult(null)}, isCountWidth : false},
			//操作区
			"DESCBTN" : { text : '填报说明', width : '50px', appendTo : '#explainButton', operNo:'descBtn', click : function(){tmp.descbtn()}, isCountWidth : false},
			"REMARKBTN" : { text : '批注', width : '25px', appendTo : '#explainButton', operNo:'remarkBtn', click : function(){tmp.busiremark()}, isCountWidth : false},	
			"REFRESHBTN" : { text : '刷新', width : '25px', appendTo : '#explainButton', operNo:'refreshBtn', click : function(){tmp.refreshData()}, isCountWidth : false},
			"QUERYREMARKBTN" : { text : '查看批注', width : '25px', appendTo : '#explainButton', operNo:'queryRemarkBtn', click : function(){tmp.queryRemark()}, isCountWidth : false},

			//流程区
			"SAVEBTN" : { text : '保存', width : '25px', appendTo : '#flowButton', operNo:'saveBtn', click : function(){tmp.promptAndSave(true)}, isCountWidth : false},
			"BACKBTN" : { text : '返回', width : '25px', appendTo : '#flowButton', operNo:'backBtn', click : tmp.back, isCountWidth : false},
			"HANDLEBTN" : { text : '提交', width : '25px', appendTo : '#flowButton', operNo:'handleBtn', click : tmp.submit, isCountWidth : false},
			
			//追溯与分析
			"IDXSTRUCTUREBTN" : { text : '结构分析', width : '25px', appendTo : '#analysisButton', operNo:'idxStructureBtn', click : function(){tmp.rptIdxDataDrill("idxStructure")}, isCountWidth : false},
			"IDXLASTBTN" : { text : '趋势分析', width : '25px', appendTo : '#analysisButton', operNo:'idxLastBtn', click : function(){tmp.rptIdxDataDrill("idxLast")}, isCountWidth : false},
			"ORGDRILLBTN" : { text : '机构下钻', width : '25px', appendTo : '#analysisButton', operNo:'orgDrillBtn', click : function(){tmp.rptIdxDataDrill("orgDrill")}, isCountWidth : false},
			"IDXDETAILBTN" : { text : '明细分析', width : '25px', appendTo : '#analysisButton', operNo:'idxDetailBtn', click : function(){tmp.rptIdxDataDrill("idxDetail")}, isCountWidth : false},
			"WARNANALYSISBTN" : { text : '变动分析', width : '25px', appendTo : '#analysisButton', operNo:'warnAnalysisBtn', click : function(){tmp.warnAnalysisBtn()}, isCountWidth : false},

			//暂时未用到
			"FINISHBTN" : { text : '结束填报', width : '50px', appendTo : '#flowButton', operNo:'finishBtn', click : tmp.finish, isCountWidth : false},
			"EXPLAINBTN" : { text : '备注', width : '25px', appendTo : '#explainButton', operNo:'explainBtn', click : function(){tmp.explain()}, isCountWidth : false},
			"COMPAREBTN" : { text : '比上期', width : '50px', appendTo : '#dataButton', operNo:'compareBtn', click : null, isCountWidth : false},
			"CALCULATEBTN" : { text : '条线汇总', width : '50px', appendTo : '#dataButton', operNo:'calculateBtn', click : null, isCountWidth : false},
			"RESETBTN" : { text : '数据重置', width : '50px', appendTo : '#dataButton', operNo:'resetBtn', click : tmp.reset, isCountWidth : false},
			"LOGICRESULTBTN" : { text : '逻辑校验', width : '50px', appendTo : '#resultButton', id:'logicResultBtn', operNo:'logicResultBtn', click : function(){tmp.checkResult("logic")}, isCountWidth : false},
			"WARNRESULTBTN" : { text : '预警校验', width : '50px', appendTo : '#resultButton', id:'warnResultBtn', operNo:'warnResultBtn', click : function(){tmp.checkResult("warn")}, isCountWidth : false},
			"SUMPARTRESULTBTN" : { text : '总分校验', width : '50px', appendTo : '#resultButton', id:'sumpartResultBtn', operNo:'sumpartResultBtn', click : function(){tmp.checkResult("sumpart")}, isCountWidth : false},
			"ZERORESULTBTN" : { text : '零值校验', width : '50px', appendTo : '#resultButton', id:'zeroResultBtn', operNo:'zeroResultBtn', click : function(){tmp.checkResult("zero")}, isCountWidth : false}
	};
	//溯源模式按钮集合
	tmp.rptConfigButton = {
			//校验区
			"LOGICRESULTBTN" : { text : '逻辑公式', width : '50px', appendTo : '#validButton', operNo:'logicResultBtn', isCountWidth : false, click : function(){tmp.logicValidConfig()}},
			"WARNRESULTBTN" : { text : '预警公式', width : '50px', appendTo : '#validButton', operNo:'warnResultBtn', isCountWidth : false, click : function(){tmp.warnValidConfig()}},
			"SUMPARTRESULTBTN" : { text : '明细公式', width : '50px', appendTo : '#validButton', operNo:'sumpartResultBtn', isCountWidth : false, click : function(){tmp.detailValidConfig()}},
			//操作区
			"DESCBTN" : { text : '填报说明', width : '50px', appendTo : '#explainButton', operNo:'descBtn', click : function(){tmp.descbtn()}, isCountWidth : false},
			"REMARKBTN" : { text : '批注', width : '25px', appendTo : '#explainButton', operNo:'remarkBtn', click : function(){tmp.busiremark()}, isCountWidth : false},
			"REFRESHBTN" : { text : '刷新', width : '25px', appendTo : '#explainButton', operNo:'refreshBtn', click : function(){tmp.refreshData()}, isCountWidth : false},

			//流程区
			"SAVEBTN" : { text : '保存', width : '25px', appendTo : '#flowButton', operNo:'saveBtn', click : function(){tmp.promptAndSave(true)}, isCountWidth : false},
			"BACKBTN" : { text : '返回', width : '25px', appendTo : '#flowButton', operNo:'backBtn', click : tmp.back, isCountWidth : false},
			"HANDLEBTN" : { text : '提交', width : '25px', appendTo : '#flowButton', operNo:'handleBtn', click : tmp.submit, isCountWidth : false},

			//追溯与分析
			"IDXSTRUCTUREBTN" : { text : '血缘分析', width : '25px', appendTo : '#analysisButton', operNo:'idxStructureBtn', click : function(){tmp.idxAnalysis()}, isCountWidth : false},
			"ORGDRILLBTN" : { text : '过滤信息', width : '25px', appendTo : '#analysisButton', operNo:'orgDrillBtn', click : function(){tmp.idxFiliter()}, isCountWidth : false}
	};
	tmp.rptIdxDataDrill = function(flag){
		if(templateType != "01" && base_selectIdxNo){
			BIONE.commonOpenDialog("追溯与分析", "rptIdxDataDrill",$(document).width() - 200,$(document).height() - 200, "${ctx}/rpt/frs/rptfill/rptIdxDataDrill?flag="+flag);
		}else{
			BIONE.tip("请选择一个指标单元格！");
		}
	};
	tmp.logicValidConfig = function(){
		if(templateType != "01"){
			if(configIndexNo && configIndexNo != ""){
				BIONE.commonOpenDialog("逻辑校验公式配置", "logicValidConfig",$(document).width() - 100,$(document).height() - 100, "${ctx}/rpt/frs/rptfill/logicValidConfig?templateId="+ base_TmpId +"&dataDate=" + base_DataDate + "&indexNo=" + configIndexNo);
			}else{
				BIONE.commonOpenDialog("逻辑校验公式配置", "logicValidConfig",$(document).width() - 100,$(document).height() - 100, "${ctx}/rpt/frs/rptfill/logicValidConfig?templateId="+ base_TmpId +"&dataDate=" + base_DataDate);
			}
		}else{
			BIONE.tip("明细报表没有逻辑校验公式！");
		}
	};
	tmp.warnValidConfig = function(){
		if(configIndexNo && configIndexNo != ""){
			BIONE.commonOpenDialog("预警校验公式配置", "warnValidConfig",$(document).width() - 100,$(document).height() - 100, "${ctx}/rpt/frs/rptfill/warnValidConfig?templateId="+ base_TmpId +"&dataDate=" + base_DataDate + "&indexNo=" + configIndexNo);
		}else{
			BIONE.commonOpenDialog("预警校验公式配置", "warnValidConfig",$(document).width() - 100,$(document).height() - 100, "${ctx}/rpt/frs/rptfill/warnValidConfig?templateId="+ base_TmpId +"&dataDate=" + base_DataDate);
		}
	};
	tmp.detailValidConfig = function(){
		if(templateType != "02"){
			BIONE.commonOpenDialog("明细校验公式配置", "detailValidConfig",$(document).width() - 100,$(document).height() - 100, "${ctx}/rpt/frs/rptfill/detailValidConfig?templateId="+ base_TmpId +"&dataDate=" + base_DataDate);
		}else{
			BIONE.tip("指标报表没有明细校验公式！");
		}
	};
	tmp.idxAnalysis = function(){
		if(configIndexNo && configIndexNo != ""){
			BIONE.commonOpenDialog("血缘分析", "idxAnalysis",$(document).width() - 500,$(document).height() - 200, "${ctx}/rpt/frs/rptfill/idxAnalysis?id="+ configIndexNo +"&dataDate=" + base_DataDate);
		}else{
			BIONE.tip("只有指标单元格才能进行血缘分析！");
		}
	}
	tmp.idxFiliter = function(){
		if(srcIndexNo && srcIndexNo != ""){
			BIONE.commonOpenDialog("指标过滤信息", "idxFiliter",$(document).width() - 500,$(document).height() - 200, "${ctx}/rpt/frs/rptfill/idxFiliter");
		}else{
			BIONE.tip("只有指标单元格才有过滤信息！");
		}
	}
	
	tmp.showNotUptCells = function(){
		BIONE.commonOpenDialog("导入结果", "notUptCellInfo",$(document).width() - 100,$(document).height() - 100, "${ctx}/rpt/frs/rptfill/showNotUptCells");
	}
</script>
</head>
<body>
</body>

</html>