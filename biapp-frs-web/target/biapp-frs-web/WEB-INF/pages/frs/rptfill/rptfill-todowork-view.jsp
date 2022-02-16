<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/meta.jsp"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<meta name="decorator" content="/template/template14.jsp">
<%@ include file="/common/spreadjs_load.jsp"%>
<link rel="stylesheet" type="text/css" href="../../../js/myPagination/js/msgbox/msgbox.css" />
<link rel="stylesheet" type="text/css" href="../../../js/myPagination/js/myPagination/page.css" />
<script src="../../../js/myPagination/js/myPagination/jquery.myPagination6.0.js" type="text/javascript"></script>
<script language="javascript" type="text/javascript" src="../../../js/myPagination/js/msgbox/msgbox.js"></script>
<style type="text/css" >
#btn{
	float:left;
	overflow:hidden;
	margin:5px 15px 5px 15px;
}
#spread{
	float:left;
	overflow:hidden;
	margin:0px 0px 0px 0px;
}
</style>
<script type="text/javascript">
//指标下钻变量
var base_DataDate = "${dataDate}";
var base_OrgNo = "${orgNo}";
var base_OrgType = "${taskType}";
var base_OrgNm = "${orgNm}";
var base_TmpId = "";//报表模板编号
var base_selectIdxNo = "";
var base_selectUnit = "";
var base_selectDataPrecision = "";
var base_selectIdxNm = "";
var base_verId = "1";//报表版本

var orgNo = "${orgNo}";
var rptId = "${rptId}";
var dataDate = "${dataDate}";
var lineId = "${lineId}";
var taskType = "${taskType}";
var View, Spread;
var operType="${operType}"; 
var rptOperType = "${rptOperType}";
var backNode = null;
if(rptOperType!=null&&rptOperType!=""&&rptOperType!="undefined"){
	operType=rptOperType;
}
var btns =[];
var btnHeight=90;
var download=null;	
var taskInsIds = "${taskInsId}";
var rebutId="${rebutId}";
initExport = function()  {
	download = $('<iframe id="download"  style="display: none;"/>');
	$('body').append(download);
};

var pageSize=20;//明细类报表分页展示条数
var Pagination=null;
var templateType = '${templateType}';//模板类型
var fixedLength = '${fixedLength}';//是否定长
var isPaging = '${isPaging}';//是否分页

var grid;
var curColId = ""; //分析模式-当前选择的指标列
var colIds = []; //分析模式-报表指标列
var downdload;
$(function (){
	//明细类报表分页 20200212
	var height = $(window).height();
	$("#center").height(height - 30);
	if(templateType && (templateType == "01" || templateType == "03")){
		if(isPaging && isPaging == "Y"){
			$(document).on("focusout","#pagination input",jumpPage);
			$(document).on("change","#pageSize",function(val,text){
				changeSize($(this).val());
			});
		}
	}
	showTemplateId();
 	initButtons();
	initExport();
	initDesign('${color}' == '' ? window.top.color : '${color}');
	addLog();

	//初始化搜索框
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
		//切换模式样式转换
		$(this).removeClass("rpt-config");
		$(this).addClass("rpt-config2");
		$("#rptResult img").attr("src","../../../images/oper/idx2.png");
		$("#changeAnalysis").removeClass("rpt-config2");
		$("#changeAnalysis").addClass("rpt-config");
		$("#changeAnalysis img").attr("src","../../../images/oper/changeAnalysisBtn.png");
		//切换设计器展示信息
		$("#spread").show();
		$("#spread").empty();
		$("#pagination").show();
		$("#pagination").empty();
		initDesign();
	});

	//点击分析模式
	$("#changeAnalysis").bind('click', function(){
		//切换模式样式转换
		$(this).removeClass("rpt-config");
		$(this).addClass("rpt-config2");
		$("#changeAnalysis img").attr("src","../../../images/oper/changeAnalysisBtn2.png");
		$("#rptResult").removeClass("rpt-config2");
		$("#rptResult").addClass("rpt-config");
		$("#rptResult img").attr("src","../../../images/oper/idx.png");

		//切换设计器展示信息
		$("#spread").hide();
		$("#spread").empty();
		$("#pagination").hide();
		$("#pagination").empty();
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
	grid.set("url","${ctx}/rpt/frs/rptfill/changeAnalysis?rptId=" + rptId + "&orgNo=" + base_OrgNo
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
		height : '95%',
		columns : columns,
		checkbox : false,
		rownumbers : true,
		isScroll : true,
		alternatingRow : true,//附加奇偶行效果行
		usePager : false,
		dataAction : 'server',//从后台获取数据
		method : 'post',
		url : '${ctx}/rpt/frs/rptfill/changeAnalysis?rptId=' + rptId  + "&orgNo=" + base_OrgNo
				+ "&dataDate=" + base_DataDate + "&colId=" + colIds[0].id
	});
}

function findKey (obj, value, compare = (a, b) => a === b) {
	return Object.keys(obj).find(k => compare(obj[k], value))
}

function initAnalysisButtons() {
	var btns = [ {
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
	downdload.attr('src', "${ctx}/rpt/frs/rptfill/anlysisExp?rptId=" + rptId
			+ "&templateId=" + base_TmpId + "&orgNo=" + base_OrgNo + "&dataDate=" + base_DataDate + "&colId=" + curColId);
}

function initSearchInput(){
	var width = $("#spread").outerWidth() - $("#dataButton").outerWidth(true) - $("#validButton").outerWidth(true) - $("#explainButton").outerWidth(true) - $("#flowButton").outerWidth(true) - $("#analysisButton").outerWidth(true);
	$("#otherBtn").append($('<div><input type="text" id="searchAll" placeholder="全文搜索" class="search-all"/><img id="searchBtn" src="../../../images/oper/search.png" style="margin-left: -35px; width: 22px;"/></div'));
	$("#otherBtn").width(400);
	$("#searchAll").width(300);
	$("#otherBtn").append($('<div class="rpt-config" id="rptResult"><img src="../../../images/oper/idx.png" style="width: 20px;padding: 0 3px;"/><span>查看模式</span></div>' +
			'<div class="rpt-config"  id="changeAnalysis"><img src="../../../images/oper/changeAnalysisBtn.png" style="width: 20px;padding: 0 3px;"/><span>分析模式</span></div>'));
	$("#otherBtn").append($('<div class="rpt-config" style="clear:both;color:red;" id="operDesc"><img src="../../../images/oper/tip.png" style="width: 20px;padding: 0 3px;"/><span>使用提醒</span></div>'));
}

function searchText(){
	var activeSheet=null;
	activeSheet = View.spread.getActiveSheet();
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
	}
}

//根据报表编号找报表模板编号
function showTemplateId() {
	//先获取模板Id
	$.ajax({
		cache : false,
		async : false,
		url : "${ctx}/rpt/frs/rptfill/showTemplateId",
		dataType : 'json',
		data : {
			rptId : rptId,
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

//分页-输入页码跳转 20200212
function jumpPage() {
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

//初始化设计器
function initDesign(color){
	require.config({
		baseUrl : "${ctx}/plugin/js/",
		paths:{
			"view" : "show/views/rptview"
		}
	});
	require(["view"] , function(view){
		var orgName = '';
		var taskInsId = "${taskInsId}";
		if("${orgNm}" != null){
			orgName = "${orgNm}";
		}
		var argsArr = [];
		var args1 = {'DimNo':'ORG','Op':'=','Value':orgNo};
		argsArr.push(args1);
		var settings = {
				targetHeight : ($("#center").height() - btnHeight - 30),
				ctx : "${ctx}",
				readOnly : true,
				cellDetail : false,
				toolbar : false,
				canUserEditFormula : false,
				inValidMap : color,
				initFromAjax : true,
				searchArgs : JSON2.stringify(argsArr),
				ajaxData:{
					rptId : rptId,
					dataDate : dataDate,
					busiLineId : lineId,
					orgNm : orgName,
					taskInsId : taskInsId,
					operType : operType,
					templateType : templateType,
					isPaging : isPaging,
					archiveType : '01'//查询归档层数据
				},
				onEnterCell : spreadEnterCell
		};
		View = view;
		var spread = view.init($("#spread") , settings);
		Spread = spread;
	})
}

//选中事件
function spreadEnterCell(sender, args, info) {
	var selectCellNo = info.cellNo;
	var selectIdxNo = info.indexNo;
	if(selectIdxNo){
		base_selectDataPrecision = info.dataPrecision;//当前选择单元格指标精度
		base_selectUnit = info.unit;//当前选择单元格指标单位
	}
	base_selectIdxNm = info.cellNm;
	base_selectIdxNo = selectIdxNo;
};

//申请解锁
function apply_reject() {
		BIONE.commonOpenDialog(
					"申请解锁理由",
					"applyRejWin",
					"500",
					"300",
					"${ctx}/frs/rptfill/reject/applyReject?taskInstanceId="
							+ taskInsIds, null,function(data){
						if(data&&data=="success")
							BIONE.closeDialogAndReloadParent("taskViewWin", "maingrid", "解锁成功");
					});
	}
//解锁和强制解锁
function oper_force_locked(){
	BIONE.commonOpenDialog("强制解锁理由", "applyRejWin", "500", "300", "${ctx}/frs/rptfill/reject/forceDesc?taskInstanceId=" + taskInsIds,null, function(data){
		if(!data||data=="cancel"){
		}else
			BIONE.closeDialog("taskViewWin","解锁成功",true,"refresh");
	});
}
function oper_locked(){
	BIONE.commonOpenDialog(
					"审批解锁说明",
					"approveRejWin",
					"500",
					"350",
					"${ctx}/frs/rptfill/reject/approveDesc?rebutId="
							+ rebutId
							+ "&taskInstanceId="
							+ taskInsIds, null,function(data){
						if(!data||data=="cancel"){
							
						}else
							BIONE.closeDialog("taskViewWin","解锁成功",true,"refresh");
					});
}
function initButtons() {//back
	var btns = [];
	if(operType=="04"){
		$.ajax({
			cache : false,
			async : true,
			url : "${ctx}/frs/rptfill/reject/rejectJudge?moduleType=${taskType}&d="
					+ new Date().getTime(),
			dataType : 'json',
			type : "post",
			data : {
				"taskInstanceId" : taskInsIds
			},
			success : function(result) {
				if(result)
					btns.push({ text : '申请解锁', appendTo:'#flowButton', click : apply_reject, operNo : 'applyRejectBtn'});
			 	//数据下载 填报说明  修改记录  备注说明（只要查看界面） 返回 校验结果 
			 	btns.push({text : '数据下载', width : '60px', appendTo : '#dataButton', operNo:'exportBtn', click : downloadFile});
				btns.push({ text : '修改记录', width : '60px', appendTo : '#dataButton', operNo:'hisviewBtn', click : openHisview});
				btns.push({ text : '校验结果', width : '60px', appendTo :'#validButton' , operNo:'checkResultBtn', click : checkResult});
				btns.push({ text : '填报说明', width : '60px', appendTo : '#explainButton', operNo:'descBtn', click : descbtn});
				btns.push({ text : '备注说明', width : '60px', appendTo : '#explainButton', operNo:'remarkBtn', click : busiremark});
			 	btns.push({ text : '返回',width : '60px', appendTo:'#flowButton', click : back, operNo : 'backBtn'});
				//追溯与分析
				btns.push({ text : '结构分析', width : '50px', appendTo : '#analysisButton', operNo:'idxStructureBtn', click : function(){ rptIdxDataDrill("idxStructure") } });
				btns.push({ text : '趋势分析', width : '50px', appendTo : '#analysisButton', operNo:'idxLastBtn', click : function(){rptIdxDataDrill("idxLast")}});
				btns.push({ text : '机构下钻', width : '50px', appendTo : '#analysisButton', operNo:'orgDrillBtn', click : function(){rptIdxDataDrill("orgDrill")}});
				btns.push({ text : '明细分析', width : '50px', appendTo : '#analysisButton', operNo:'idxDetailBtn', click : function(){rptIdxDataDrill("idxDetail")}});
				btns.push({ text : '变动分析', width : '50px', appendTo : '#analysisButton', operNo:'warnAnalysisBtn', click : function(){warnAnalysisBtn()}});

				$("#btn").height(btnHeight);
				// $("#btn").css("margin","0px 15px 0px 15px");
				for(var i in btns){
					// BIONE.createButton(btns[i]);
					createButton(btns[i], i);
				}
				$("#dataButton").append($('<div class="div-desc">数据区</div>'));
				$("#validButton").append($('<div class="div-desc">校验区</div>'));
				$("#explainButton").append($('<div class="div-desc">操作区</div>'));
				$("#flowButton").append($('<div class="div-desc">流程区</div>'));
				$("#analysisButton").append($('<div class="div-desc">追溯与分析</div>'));
			}
		});
	}else{
		//数据下载 填报说明  修改记录  备注说明（只要查看界面） 返回 校验结果
		btns.push({text : '数据下载', width : '50px', appendTo : '#dataButton', operNo:'exportBtn', click : downloadFile});
		btns.push({ text : '修改记录', width : '50px', appendTo : '#dataButton', operNo:'hisviewBtn', click : openHisview});
		btns.push({ text : '校验结果', width : '50px', appendTo :'#validButton' , operNo:'checkResultBtn', click : checkResult});
		btns.push({ text : '填报说明', width : '50px', appendTo : '#explainButton', operNo:'descBtn', click : descbtn});
		btns.push({ text : '备注说明', width : '50px', appendTo : '#explainButton', operNo:'remarkBtn', click : busiremark});
		btns.push({ text : '查看批注', width : '50px', appendTo : '#explainButton', operNo:'queryRemarkBtn', click : queryRemark});
		//追溯与分析
		btns.push({ text : '结构分析', width : '50px', appendTo : '#analysisButton', operNo:'idxStructureBtn', click : function(){ rptIdxDataDrill("idxStructure") } });
		btns.push({ text : '趋势分析', width : '50px', appendTo : '#analysisButton', operNo:'idxLastBtn', click : function(){rptIdxDataDrill("idxLast")}});
		btns.push({ text : '机构下钻', width : '50px', appendTo : '#analysisButton', operNo:'orgDrillBtn', click : function(){rptIdxDataDrill("orgDrill")}});
		btns.push({ text : '明细分析', width : '50px', appendTo : '#analysisButton', operNo:'idxDetailBtn', click : function(){rptIdxDataDrill("idxDetail")}});
		btns.push({ text : '变动分析', width : '50px', appendTo : '#analysisButton', operNo:'warnAnalysisBtn', click : function(){warnAnalysisBtn()}});
		/* 	 	if(operType=="01"||operType=="03"){
                     btns.push({ text : '追回', width : '50px', appendTo:'#btn', click : turnBack, operNo : 'turnBack'});
                } */
		if(operType=="01"){
			btns.push({ text : '通过复核', width : '50px', appendTo:'#flowButton', click : oper_submit, operNo : 'operSubmitBtn'});
			btns.push({ text : '驳回', width : '50px', appendTo:'#flowButton', click : oper_return, operNo : 'operReturnBtn'});
		}else if(operType=="02"){
			btns.push({ text : '通过审核', appendTo:'#flowButton', click : oper_submit, operNo : 'operSubmitBtn'});
			btns.push({ text : '驳回', appendTo:'#flowButton', click : oper_return, operNo : 'operReturnBtn'});
		}else if(operType=="06"){
			btns.push({ text : '解锁', appendTo:'#flowButton', click : oper_force_locked, operNo : 'operLockedBtn'});
		}else if(operType=="05"){
			if(rebutId&&rebutId!=null&&rebutId!="")
				btns.push({ text : '解锁', appendTo:'#flowButton', click : oper_locked, operNo : 'operLockedBtn'});
		}
		btns.push({ text : '返回',width : '50px', appendTo:'#flowButton', click : back, operNo : 'backBtn'});
	 	$("#btn").height(btnHeight);
		// $("#btn").css("margin","0px 15px 0px 15px");
		//BIONE.loadToolbar(grid, btns, function() {});
		for(var i in btns){
			// BIONE.createButton(btns[i]);
			createButton(btns[i], i);
		}
		$("#dataButton").append($('<div class="div-desc">数据区</div>'));
		$("#validButton").append($('<div class="div-desc">校验区</div>'));
		$("#explainButton").append($('<div class="div-desc">操作区</div>'));
		$("#flowButton").append($('<div class="div-desc">流程区</div>'));
		$("#analysisButton").append($('<div class="div-desc">追溯与分析</div>'));
	}
}

function rptIdxDataDrill(flag){
	if(templateType != "01" && base_selectIdxNo){
		BIONE.commonOpenDialog("追溯与分析", "rptIdxDataDrill",$(document).width() - 200,$(document).height() - 200, "${ctx}/rpt/frs/rptfill/rptIdxDataDrill?flag="+flag);
	}else{
		BIONE.tip("请选择一个指标单元格！");
	}
}

function warnAnalysisBtn(){
	if(templateType != "01"){
		BIONE.commonOpenDialog("变动分析", "warnAnalysis",$(document).width() - 100,$(document).height() - 100,
				"${ctx}/rpt/frs/rptfill/warnAnalysisIndex?templateId="+ base_TmpId +"&dataDate=" + base_DataDate +
				"&rptId=${rptId}" + "&orgNo=" + base_OrgNo + "&orgType=" + base_OrgType + "&verId=" + base_verId);
	}else{
		BIONE.tip("请选择一张单元格或综合类报表");
	}
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

turnBack = function(){
	$.ajax({
		cache : false,
		async : true,
		url : "${ctx}/rpt/frs/rptfill/turnBack?operType="+operType+"&taskInsId=${taskInsId}",
		dataType : 'json',
		type : "get",
		success : function(result) {
			if(result){
				BIONE.tip("追回成功");
				if(operType=="01"){//报表复核
					if(null!=parent.frames.findIframeByTitle("报表复核")[0].contentWindow.TaskFillGrid){
						parent.frames.findIframeByTitle("报表复核")[0].contentWindow.grid.loadData();
					}
				}else{//报表填报
					if(null !=parent.frames.findIframeByTitle("报表填报")[0].contentWindow.frames.tsktab){
						if(parent.frames.findIframeByTitle("报表填报")[0].contentWindow.frames.tsktab){
							if(parent.frames.findIframeByTitle("报表填报")[0].contentWindow.frames.tsktab.contentWindow.tmp){
								parent.frames.findIframeByTitle("报表填报")[0].contentWindow.frames.tsktab.contentWindow.tmp.reAsyncChildNodes("left");
								parent.frames.findIframeByTitle("报表填报")[0].contentWindow.frames.tsktab.contentWindow.tmp.reAsyncChildNodes("right");
							}
						}else{
							parent.frames.findIframeByTitle("报表填报")[0].contentWindow.frames.tsktab.contentWindow.tmp.reAsyncChildNodes("left");
							parent.frames.findIframeByTitle("报表填报")[0].contentWindow.frames.tsktab.contentWindow.tmp.reAsyncChildNodes("right");
						}
					}
					//modify by lixp  at 20161226 归档成功刷新填报列表页面
					if(null!=parent.frames.findIframeByTitle("报表填报")[0].contentWindow.frames.listtab){
						if(parent.frames.findIframeByTitle("报表填报")[0].contentWindow.frames.listtab){
							if(parent.frames.findIframeByTitle("报表填报")[0].contentWindow.frames.listtab.contentWindow.grid)
								parent.frames.findIframeByTitle("报表填报")[0].contentWindow.frames.listtab.contentWindow.grid.loadData();
						}else{
							if(wparent.frames.findIframeByTitle("报表填报")[0].contentWindow.frames.listtab.contentWindow.grid)
								parent.frames.findIframeByTitle("报表填报")[0].contentWindow.frames.listtab.contentWindow.grid.loadData();
						}
					}
				}
				BIONE.closeDialog("taskViewWin");
			}else{
				BIONE.tip("追回失败，请刷新任务列表后重试");
			}
		}
	});
}

var checkedType="NO";//校验类型 
checkResult = function() {
	var taskId = '${taskId}';
	var erType = '${taskType}';
	var height = $(window).height() - 30;
	var width = $(window).width() - 80;
	BIONE.commonOpenDialog('校验结果查看', 'checkResultWin', width, height,'${ctx}/rpt/frs/rptfill/showTab?path=rptfill-todowork-check-result&templateId=${templateId}&rptId=${rptId}&dataDate=${dataDate}&orgNo=${orgNo}&type='+erType+'&taskId='+taskId+'&checkType='+checkedType+"&taskFillOperType=34&taskInsId="+ taskInsIds + "&operType=" + operType + "&templateType=" + templateType,function(){});	
};
busiremark = function(){
	var selIdxs = View.getSelectionIdxs();
	if(!selIdxs
			|| selIdxs.length <= 0){
		BIONE.tip("非数据单元格无法填写业务备注");
		return;
	}
	var cellNo = selIdxs[0].cellNo;
	var sendTaskInsId = window.taskInsId?window.taskInsId:'${taskInsId}';
	BIONE.commonOpenDialog("备注说明","remarkWin",800,400,"${ctx}/rpt/frs/rptfill/showTab?path=rpt-todowork-remark-frame&readOnly=true&rptId=${rptId}&type=${type}&taskInsId="+sendTaskInsId+"&orgNo="+window.orgNo+"&dataDate="+window.dataDate+"&cellNo="+cellNo+"&type="+taskType+"&operType="+operType);	

}
//查看所有批注
queryRemark = function(){
	BIONE.commonOpenDialog(
			'查看批注信息',
			'hisViewWin',
			$(window).width() - 200, $(window).height(),
			'${ctx}/rpt/frs/rptfill/showTab?path=rpt-todowork-remark-list&taskId=${taskId}'
			+ "&rptId=${rptId}" + "&orgType=" + base_OrgType + "&orgNo=" + orgNo
			+ "&dataDate=" + base_DataDate);
}

openHisview = function(){
	var cellNo = "";
	var selIdxs = View.getSelectionIdxs();
	if(selIdxs && selIdxs.length > 0){
		cellNo = selIdxs[0].cellNo;
	}
	BIONE.commonOpenDialog('查看修改记录','hisViewWin', $(window).width() - 100, $(window).height() - 100, '${ctx}/rpt/frs/rptfill/showTab?path=rpt-todowork-his-detail&taskInstanceId='+taskInsIds+"&operType="+operType+'&cellNo='+cellNo+"&templateId=${templateId}&taskFillOperType=27&orgNo="+orgNo+'&taskId=${taskId}'+'&orgType='+base_OrgType+'&dataDate='+base_DataDate,function (){
	});	
};
descbtn = function() {
	BIONE.commonOpenLargeDialog("填报说明","rptViewWin","${ctx}/rpt/frame/rptmgr/info/reportFrs?rptId=${rptId}&show=1&taskInsId=" + taskInsIds +"&operType="+operType+ "&taskFillOperType=26");	
};
 function downloadFile(){
	var argsArr = [];
	
	var taskInsId = "${taskInsId}";
	if("${orgNm}" != null){
		orgName = "${orgNm}";
	}
	var orgNo = "${orgNo}";
	var rptId = "${rptId}";
	var dataDate = "${dataDate}";
	var lineId = "${lineId}";
	if(taskInsId && orgNo && rptId && dataDate  && orgName){
		var busiLineId = "${lineId}";
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
				url : "${ctx}/rpt/frs/rptfill/downloadList?json="+encodeURI(encodeURI(JSON2.stringify(argsArr)))+"&d="+ new Date().getTime(),
				dataType : 'json',
				type : 'get',
				loading : '正在生成下载文件，请稍等...'
			}, function(result) {
				if(result.result){
					if("OK" == result.result){
						if(result.zipFilePath && result.folderinfoPath){
							var src = '';
							src = "${ctx}/rpt/frs/rptfill/downFile?zipFilePath="+encodeURI(encodeURI(result.zipFilePath))+"&folderinfoPath=" + encodeURI(encodeURI(result.folderinfoPath)) + "&d="+ new Date().getTime()+"&taskFillOperType=22&operType="+operType+"&taskInsId="+taskInsIds;
							download.attr('src', src);
						}else{
							BIONE.tip("数据异常，请联系系统管理员");
						}
					}else{
						BIONE.tip(result.msg);
					}
				}else{
					 BIONE.tip("数据异常，请联系系统管理员");
				}
			});
	}else{
		BIONE.tip("数据异常，请联系系统管理员");
	}
}
//提交按钮
function oper_submit(){
	var rows = [];
	if(operType=="01"){
		rows = parent.frames.findIframeByTitle("报表复核")[0].contentWindow.grid.rows;
	}else if(operType=="02"){
		rows = parent.frames.findIframeByTitle("报表审核")[0].contentWindow.grid.rows;
	}
	var finishSts;
	var taskInsIds;
	for(var i=0;i<rows.length;i++){
		if(rows[i].taskInstanceId == "${taskInsId}"){
			taskInsIds = [rows[i].taskInstanceId];
			finishSts =[rows[i].sts];
			if ((rows[i].logicRs == '01' || rows[i].logicRs == '02') || (rows[i].sumpartRs == '01' || rows[i].sumpartRs == '02') || (rows[i].warnRs == '01' || rows[i].warnRs == '02')){
				BIONE.tip("含有正在校验的记录，无法提交");
				return;	
			}else{
				if(operType=="01"){
					if(rows[i].sts == '1'){
					}else{
						BIONE.tip("含有已复核记录，不可重复复核");
						return;
					}
				}else if(operType=="02"){
					if(rows[i].sts == '2'){
					}else{
						BIONE.tip("含有已审核记录，不可重复审核");
						return;
					}
				}else{
					if(rows[i].sts == '0'){
					}else{
						BIONE.tip("含有已归档记录，不可重复提交");
						return;
					}
				}
			}
		}
	}
	if(operType=="01"){
		data="ignore";
				if(data&&data!=null&&data!=""&&data!="ignore"){
					$.ajax({
						cache : false,
						async : false,
						url : "${ctx}/frs/rptsubmit/submit/submitTaskBatchIndex?moduleType=${taskType}&operType="+operType+"&sts="+finishSts.join(",")+"&doFlag=rpt-submit-index&taskInsIds=" + taskInsIds+"&authUsers=" + data+"&d="+new Date().getTime(),
						dataType : 'json',
						type : "get",
						success : function(){
							window.parent.frames.findIframeByTitle("报表复核")[0].contentWindow.grid.loadData();
						if(operType=="01"){
							BIONE.tip("复核成功");
							BIONE.closeDialogAndReloadParent("taskViewWin", "maingrid", "复核成功");
						}else if(operType=="02"){
							BIONE.tip("审核成功");
							BIONE.closeDialogAndReloadParent("taskViewWin", "maingrid", "审核成功");
							}
						},
						error:function(){
							//grid.loadData();
							if(operType=="01"){
								BIONE.tip("复核失败");
							}else if(operType=="02"){
								BIONE.tip("审核失败");
							}
						}
					});
					
				}else if(data == 'ignore'){
					$.ajax({
						cache : false,
						async : false,
						url : "${ctx}/frs/rptsubmit/submit/submitTaskBatchIndex?moduleType=${taskType}&operType="+operType+"&sts="+finishSts.join(",")+"&doFlag=rpt-submit-index&taskInsIds=" + taskInsIds+"&authUsers=" + data+"&d="+new Date().getTime(),
						dataType : 'json',
						type : "get",
						success : function(){
							window.parent.frames.findIframeByTitle("报表复核")[0].contentWindow.grid.loadData();
						if(operType=="01"){
							BIONE.tip("复核成功");
							BIONE.closeDialogAndReloadParent("taskViewWin", "maingrid", "复核成功");
						}else if(operType=="02"){
							BIONE.tip("审核成功");
							BIONE.closeDialogAndReloadParent("taskViewWin", "maingrid", "审核成功");
							}
						},
						error:function(){
							//grid.loadData();
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
			url : "${ctx}/frs/rptsubmit/submit/submitTaskBatchIndex?moduleType=${taskType}&operType="+operType+"&sts="+finishSts+"&doFlag=rpt-submit-index&taskInsIds=" + taskInsIds,
			dataType : 'json',
			type : "get",
			success : function(){
				window.parent.frames.findIframeByTitle("报表审核")[0].contentWindow.grid.loadData();
			if(operType=="01"){
				BIONE.tip("复核成功");
				BIONE.closeDialogAndReloadParent("taskViewWin", "maingrid", "复核成功");
			}else if(operType=="02"){
				BIONE.tip("审核成功");
				BIONE.closeDialogAndReloadParent("taskViewWin", "maingrid", "审核成功");
				}
			},
			error:function(){
				//grid.loadData();
				if(operType=="01"){
					BIONE.tip("复核失败");
				}else if(operType=="02"){
					BIONE.tip("审核失败");
				}
			}
		});
	}
}
function oper_return() {
	var rows;
	if(operType=="01"){
		rows = window.parent.frames.findIframeByTitle("报表复核")[0].contentWindow.grid.rows;
	}else{
		rows = window.parent.frames.findIframeByTitle("报表审核")[0].contentWindow.grid.rows;
	}
	for(var i=0;i<rows.length;i++){
		if(rows[i].taskInstanceId == "${taskInsId}"){
			var ids=[];
			var taskInsIds=[];
			var strs =[];
			var id=rows[i].taskId+","+rows[i].taskType+","+rows[i].dataDate+","+rows[i].taskObjId+","+rows[i].exeObjId;
			ids.push(id);
			taskInsIds.push(rows[i].taskInstanceId);
			strs.push(rows[i].taskInstanceId+";"+rows[i].exeObjId+";"+rows[i].taskObjId+";"+rows[i].dataDate+";"+rows[i].taskObjNm+";"+rows[i].taskNm);
			var applyUser = rows[i].applyUserNm;
			backNode = false;	// 是否需要在驳回理由界面上显示打回节点选择
			if (rows[i].sts == '2' || rows[i].sts == '3') {
				backNode = true;
			}
			$.ajax({
				cache : false,
				async : false,
				url : "${ctx}/frs/rptsubmit/submit/getFrsOrgNo?operType="+operType+"&doFlag=rpt-submit-index",
				dataType : 'json',
				data:{
					json : ids.join(";"),
					orgType : rows[i].taskType
				},
				type : "post",
				success : function(info){
					/* if(!info.flag){
						BIONE.tip("用户所属机构已通过审核，无法驳回");
						return;
					} */
					$.ajax({
						cache : false,
						async : false,
						url : "${ctx}/frs/rptsubmit/submit/returnJudge",
						dataType : 'json',
						type : "post",
						data : {
							params : ids.join(";"),
							sts : "1,2,3"
						},
						success : function(result){
							if(result.result == "ERROR"){
								BIONE.tip("数据异常，请联系系统管理员");
								return;
							}
							//新增的驳回理由弹出界面
							window.resultList=result.resultList;
							window.taskInsIds=taskInsIds.join(",")
							//判断驳回任务的流程类型，新增的 <开始-审核-结束>流程只能驳回到开始  20190827
							var prodefSet =result.prodefSet;
							if(prodefSet.indexOf("frs_process2_2") > 0){
								backNode = false;
							}
							BIONE.commonOpenDialog("驳回理由", "approveRejWin", "500", "350", 
									"${ctx}/rpt/frs/rptfill/showTab?backNode="+backNode+"&applyUser="+applyUser+"&path=rpt-submit-approve-desc&moduleType=${taskType}&operType=${operType}&taskInsIds=" + taskInsIds+"&strs="+ encodeURI(encodeURI(strs.join(","))), null);
						},
						error:function(){	
						}
					});
				}
			});
		}
	}
}
function back() {
	$.ligerDialog.confirm("确定返回？", function(yes) {
		if(yes){
			BIONE.closeDialog("taskViewWin");
		}
	});
		
}
function closeDialog(){
	BIONE.closeDialogAndReloadParent("taskViewWin", "maingrid", "驳回成功");
}
function addLog(){
	var dataDate = "${dataDate}";
	var rptId = "${rptId}";
	var taskInsId = "${taskInsId}";
	$.ajax({
			async : false,
			url : "${ctx}/rpt/frs/rptfill/showTemplateId",
			dataType : 'text',
			type : 'POST',
			data : {
				dataDate : dataDate,
				rptId : rptId,
				id : taskInsId,
				operType : operType
			}
		});
}
</script>
</head>
<body>
	<div id="template.center">
		<div id="dataButton" style="float: left;border-right: 2px solid #c5c5c5;padding: 10px;margin: 5px 0px;height: 90px;"></div>
		<div id="validButton" style="float: left;border-right: 2px solid #c5c5c5;padding: 10px;margin: 5px 0px;height: 90px;"></div>
		<div id="explainButton" style="float: left;border-right: 2px solid #c5c5c5;padding: 10px;margin: 5px 0px;height: 90px;"></div>
		<div id="flowButton" style="float: left;border-right: 2px solid #c5c5c5;padding: 10px;margin: 5px 0px;height: 90px;"></div>
		<div id="analysisButton" style="float: left;border-right: 2px solid #c5c5c5;padding: 10px;margin: 5px 0px;height: 90px;"></div>
		<div id="otherBtn" style="float: left;padding: 10px;margin: 5px 0px;height: 90px;"></div>
		<div id="spread" style="width:99.5%;border:1px solid #D0D0D0;position:relative;"></div>
	</div>
	<div id="pagination"></div>
</body>

</html>