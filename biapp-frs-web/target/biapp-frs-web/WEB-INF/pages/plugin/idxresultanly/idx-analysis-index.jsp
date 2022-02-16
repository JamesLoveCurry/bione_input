<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<%@ include file="/common/meta.jsp"%>
<meta name="decorator" content="/template/template22.jsp">

<meta charset="utf-8">
<title>指标分析</title>

<meta name="description" content="morris charts">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<link rel="stylesheet" href="${ctx}/js/dupontChart/dupontChart.css">
<link rel="stylesheet" href="${ctx}/css/classics/font-awesome/css/font-awesome.css">
<script type="text/javascript" src="${ctx}/js/echarts/js/echarts.min.js"></script>
<script type="text/javascript" src="${ctx}/js/dupontChart/dupontChart.all.js"></script>
<style>
.title {
	font-size: 14px;
	color: rgb(34, 34, 34);
	height: 30px;
	line-height: 30px;
	font-weight: normal;
	margin: 0px;
	padding: 0px 10px;
}

.title2 {
	text-align: left;
	color: #222;
	font-weight: normal !important;
	line-height: 18px;
	margin: 0;
	padding: 0 10px;
	font-size: 14px !important;
	font-family: inherit;
}

.numValue {
	text-align: center;
	font-size: 250%;
	margin: 0;
	padding: 0 10px;
	color: #7f99c7;
	font-weight: 600;
	line-height: 1.1;
}

.numValue2 {
	color: #777;
	font-weight: bold;
	box-sizing: border-box;
	margin-left: 40px;
	line-height: 18px;
	font-size: 14px !important;
}

.idxTitle{
	width: 200px;
	height: 100%; 
	float: left; 
	border-right: 2px solid white;
	margin-bottom: 4px;
}
</style>
<script type="text/javascript">
	var leftTreeObj = "";//指标树对象
	var currencyList = [];//币种集合
	var date = "";//日期
	var currency = "";//币种
	var org = "";//机构
	var idxNo = "";//选择指标标号
	var idxName = "";//选择指标名称
	var dataUnit = "";//数据单位
	var idxTitleNum = "";
	var indexTab = "";
	var mainTab = "";
	var idxBelongType = "";
	var idxGrpNo;//指标组编号
	
	$(function() {
		initTree();
		initTool();
		initTab();
		initform();
	});

	//初始化查询框
	function initform() {
		var field = [
		 			{
		 				display : '日期',
		 				name : "date",
		 				newline : false,
		 				type : "date",
		 				options : {
		 					format : "yyyyMMdd"
		 				},
		 				cssClass : "field"
		 			},
		 			{
		 				display : '币种',
		 				name : 'currency',
		 				type : "select",
		 				newline : false,
		 				options : {
		 					data : null
		 				}
		 			},
		 			{
		 				display : "机构",
		 				name : "org",
		 				newline : false,
		 				type : "popup",
		 				options : {
		 					cancelable : false,
		 					onButtonClick : function() {
		 						BIONE.commonOpenDialog('机构目录', "orgCatalog", 400, 300,
		 								"${ctx}/idx/analysis/show/initOrgTree?idxBelongType="+idxBelongType);
		 					}
		 				}
		 			}/* ,
					{
		 				display : "对比组",
		 				name : "grp",
		 				newline : false,
		 				type : "popup",
		 				options : {
		 					cancelable : false,
		 					onButtonClick : function() {
		 						BIONE.commonOpenDialog('对比组目录', "grpCatalog", 400, 300,
		 								"${ctx}/idx/analysis/show/initGrpTree?idxNo="+idxNo);
		 					}
		 				}
		 			} */]
		var mainform = $("#mainform").ligerForm({
			inputWidth : 140,
			labelWidth : 50,
			space : 20,
			fields : field
		});
		$("#currency").parent().parent().parent().hide();
	}

	//初始化按钮
	function initbutton() {
		var $btn = '<div id="queryButton" class="l-dialog-btn" style="float: right;top: 8px;right: 100px;"><div class="l-dialog-btn-inner">查询</div></div>';
		$("#buttonHome").html($btn);
		$("#queryButton").click(function() {
			queryData();
		});
	}

	//调整高度及CSS等
	function initTool() {
		$("#treeContainer").height(
				$("#left").height() - $("#lefttable").height()
						- $("#treeToolbar").height()
						- $("#treeSearchbar").height());
		$("#treeSearchIcon").live('click', function() {
			initTree($('#treeSearchInput').val());
		});
		$('#treeSearchInput').bind('keydown', function(event) {
			if (event.keyCode == 13) {
				initTree($('#treeSearchInput').val());
			}
		});
	}
	
	//左侧回收回调函数
	function resizeLay(){
		var iWidth = $("#indexNm").width();
		if(idxTitleNum){
			var itWidth = Math.floor(iWidth / idxTitleNum);
			$(".idxTitle").css("width",itWidth - 2 + "px");
			var itWidth = $("#indexTab").width();
			if(itWidth){
				var tabid = indexTab.getSelectedTabItemID();
				indexTab.selectTabItem(tabid);
			}
		}
	}
	
	// 初始化tab
	function initTab() {
		mainTab = $("#tab").ligerTab({
			contextmenu : false
		});
		mainTab.addTabItem({
			tabid : "topTab",
			text : "指标分析",
			showClose : false,
			content : '<div id="mainform" style="width: 80%; height: 7%; float: left;"></div><div id="buttonHome"></div><div id="gridDiv" style="height:'+ ($(document).height() - 26) + 'px;width:100%;float: left;"></div>'
		});
	}

	//初始化指标树
	function initTree(searchNm) {
		var setting = {
			async : {
				enable : true,
				url : "${ctx}/idx/analysis/show/getAsyncTreeIdxShow.json",
				autoParam : [ "nodeType", "id", "indexVerId" ],
				otherParam : {
					'searchNm' : searchNm,
					'isShowIdx' : '1',
					'isShowDim' : 1,
					'isShowMeasure' : 1,
					'isPublish' : '1',
					"isAuth" : "1",
					"showEmptyFolder" : 0
				},
				dataType : "json",
				dataFilter : function(treeId, parentNode, childNodes) {
					if (childNodes) {
						for (var i = 0; i < childNodes.length; i++) {
							childNodes[i].nodeType = childNodes[i].params.nodeType;
							childNodes[i].indexVerId = childNodes[i].params.indexVerId;
						}
					}
					return childNodes;
				}
			},
			check : {
				enable : false
			},
			data : {
				key : {
					name : "text",
					title : "title"
				}
			},
			view : {
				selectedMulti : false
			},
			callback : {
				onClick : function(event, treeId, treeNode, clickFlag) {
					if ((treeNode.params.nodeType == "idxInfo" && treeNode.params.indexType != "05") || treeNode.params.nodeType == "measureInfo") {
						idxBelongType = treeNode.data.busiType;
						$("#tab").show();
						$("#cover").hide();
						if(treeNode.params.nodeType == "measureInfo"){
							idxNo = treeNode.params.indexNo;
							idxNo = idxNo + "." + treeNode.id;
						}else{
							idxNo = treeNode.id;
						}
						idxName = treeNode.text;
						getIdxDim(idxNo);
						initdate(idxNo);
						initbutton();
					}else{
						$("#tab").hide();
						$("#cover").show();
					}
				}
			}
		};
		leftTreeObj = $.fn.zTree.init($("#tree"), setting, []);
	}

	//初始化币种数据
	function initcurrency() {
		$.ajax({
			type : "GET",
			async : false,
			dataType : "json",
			url : "${ctx}/idx/analysis/show/initcurrency",
			success : function(result) {
				if (result.length > 0) {
					for (var i = 0; i < result.length; i++) {
						var currencyObj = {};
						currencyObj.id = result[i].id.dimItemNo;
						currencyObj.text = result[i].dimItemNm;
						currencyList.push(currencyObj);
					}
					$.ligerui.get("currency").setValue("");
					$.ligerui.get("org").setValue("","");
					//$.ligerui.get("grp").setValue("","");
					$.ligerui.get("currency").setData(currencyList);
				}
			},
			error : function(e) {
				BIONE.tip('发现系统错误 <BR>错误码：' + e.status);
			}
		});
	}
	
	//获取指标翻牌日期
	function initdate(idxNo){
		$.ajax({
			type : "post",
			async : false,
			dataType : "json",
			url : "${ctx}/idx/analysis/show/initDrawDate",
			data : {
				idxNo : idxNo
			},
			success : function(result) {
				if(result.drawDate){
					result.drawDate = result.drawDate.split("-").join("");
					$("#mainform [name='date']").val(result.drawDate);
				}
			},
			error : function(e) {
				BIONE.tip('发现系统错误 <BR>错误码：' + e.status);
			}
		});
	}

	//查询当前指标所含维度
	function getIdxDim(idxNo){
		$.ajax({
			type : "post",
			async : false,
			dataType : "json",
			url : "${ctx}/idx/analysis/show/getIdxDim",
			data : {
				idxNo : idxNo
			},
			success : function(result) {
				if (result.length > 0) {
					$("#currency").parent().parent().parent().hide();
					for (var i = 0; i < result.length; i++) {
						if(result[i].id.dimNo == "CURRENCY"){
							$("#currency").parent().parent().parent().show();
							initcurrency();
						}
					}
				}
			},
			error : function(e) {
				BIONE.tip('发现系统错误 <BR>错误码：' + e.status);
			}
		});
	}
	
	//查询事件
	function queryData() {
		$('#gridDiv').html('<div id="indexNm" style="width: 100%;height: 15%;" ></div><div id="indexTab" style="width: 100%;height: 85%;"></div>');
		date = $.ligerui.get("date").getValue();
		currency = $.ligerui.get("currency").getValue();
		org = $.ligerui.get("org").getValue();
		var isCurrency = false;
		if($("#currency").parent().parent().parent().css("display") == "none"){
			isCurrency = true;
		} 
		if (date && org && (currency || isCurrency )) {
			if (idxNo) {
				initchartList(idxNo, date, org, currency);
				$.ajax({
					type : "post",
					url : "${ctx}/idx/analysis/show/indexTime",
					async : true,
					dataType : 'json',
					data : {
						idxNo : idxNo,
						date : date,
						org : org,
						currency : currency
					},
					beforeSend : function() {
						BIONE.loading = true;
						BIONE.showLoading("正在加载数据中...");
					},
					success : function(resultMap) {
						var resultTime = resultMap;
						initTime(resultTime);
					},
					error : function(e) {
						BIONE.tip('发现系统错误 <BR>错误码：' + e.status);
					}
				});
			} else {
				BIONE.tip("请选择指标");
			}
		} else {
			BIONE.tip("请选择维度");
		}
	}
	
	//查询所有图表
	function initchartList(idxNo, date, org, currency){
		$.ajax({
			type : "post",
			url : "${ctx}/idx/analysis/show/initchartList",
			async : true,
			dataType : 'json',
			data : {
				idxNo : idxNo
			},
			success : function(resultMap) {
				var resultchartlist = resultMap.chartslist; //该模版下的图表
				initCharts(idxNo,resultchartlist);
			},
			error : function(e) {
				BIONE.tip('发现系统错误 <BR>错误码：' + e.status);
			}
		});
	}
	

	//生成指标概要信息
	function initTime(resultTime) {
		var initFormula = resultTime.formula;
		var tmpInfo = resultTime.tmpInfo;
		var idxVolue = resultTime.idxVolue;
		dataUnit = tmpInfo.dataUnit;
		if (initFormula.length > 0) {
			var fetch = Math.floor(($("#indexNm").width() - 3*(initFormula.length + 1))/(initFormula.length + 1));//向下取整
			var rowlist = '<div class = "idxTitle" style="background-color: rgba(222, 222, 222, 0.48);">'
							+ '<h6 class = "title">'+ idxName + '</h6>'
							+ '<h5 class = "numValue">' + '<b>' + idxVolue + '<small style="margin-left: 10px;">'+ dataUnit + '</small></b></h5>' 
						+ '</div>';
			$('#indexNm').prepend(rowlist);
			for (var i = 0; i < initFormula.length; i++) {
				rowlist = '<div class = "idxTitle" style="background-color: rgba(222, 222, 222, 0.48);">'
							+ '<h6 class = "title">'+ initFormula[i].formulaNm + '</h6>'
							+ '<h5 class = "numValue">' + '<b>' + initFormula[i].formulaContent + '<small style="margin-left: 10px;">' + dataUnit + '</small></b></h5>'
						+ '</div>';
				$('#indexNm').append(rowlist);
			}
			$(".idxTitle").css("width",fetch + "px");
		}else{
			BIONE.hideLoading();
			BIONE.tip('指标分析模板未配置，请先配置模板');
		}
	}

	//生成具体图表tab页
	function initCharts(idxNo, resultchartlist) {
		indexTab = $("#indexTab").ligerTab({
			contextmenu : false,
			onAfterSelectTabItem : function(tabId) {
				var chartAttr = tabId.split("-");
				if (chartAttr.length == 3) {
					var chartId = chartAttr[0];
					var chartType = chartAttr[1];
					var showType = chartAttr[2];
					initChart(chartId, chartType, showType);
				}
			}
		});
		if(resultchartlist && resultchartlist.length > 0){
			for (var i = 0; i < resultchartlist.length; i++) {
				var chart = resultchartlist[i];
				indexTab.addTabItem({
					tabid : chart.chartId + "-" + chart.chartType + "-" + chart.showType,
					text : chart.chartNm,
					showClose : false,
					content : '<div id="chartBox'+ chart.chartId + '" class = "chartBox" data-chartId = "" style ="width: 100%;height: 100%;float: left;"></div>'
								+ '<div class ="chartText" style ="height: 100%;margin-top: 20px;color: #6d4704;">'
									+ '<span id="chartText'+ chart.chartId + '"style="width: 100%; font-size : 1.2em;"></span>'
								+ '</div>'
				});
			}
			indexTab.selectTabItem(resultchartlist[0].chartId + "-"+ resultchartlist[0].chartType + "-"+ resultchartlist[0].showType);
		}else{
			BIONE.hideLoading();
			BIONE.tip('指标分析模板未配置，请先配置模板');
		}
	}

	//生成具体图形
	function initChart(chartId, chartType, showType) {
		if(chartType == "01" || chartType == "02" || chartType == "03"){
			_initChartText(chartId,chartType);
		}
		if (chartType == "04" || chartType == "05") {
			_initdupont(chartId,chartType);
		}else{
			var grpNo = "";
			//$.ligerui.get("grp").getValue();
			idxGrpNo = idxNo + ';'
			if(grpNo && showType != "05"){
				idxGrpNo += grpNo;
			}
			_initChart(idxGrpNo, chartId, chartType, showType);
		}
	}

	//生成图表内部函数(除杜邦)
	function _initChart(idxGrpNo, chartId, chartType, showType) {
		$.ajax({
			type : "post",
			url : "${ctx}/idx/analysis/show/getCharts",
			async : true,
			dataType : 'json',
			data : {
				idxNo : idxGrpNo,
				chartType : chartType,
				chartId : chartId,
				showType : showType,
				idxNm : idxName,
				dataUnit : dataUnit,
				date : date,
				org : org,
				currency : currency,
				orgType : idxBelongType
			},
			beforeSend : function() {
				BIONE.loading = true;
				BIONE.showLoading("正在加载数据中...");
			},
			complete : function() {
				BIONE.loading = false;
				BIONE.hideLoading();
			},
			success : function(resultMap) {
				$("#chartBox" + chartId).height($("#indexTab").height() - 52);
				if((resultMap.echart) && ($("#chartBox" + chartId).width() != 100)){
					var myChart = echarts.init(document.getElementById('chartBox' + chartId));
					myChart.setOption(resultMap.echart.option);
				}
			},
			error : function(e) {
				BIONE.tip('发现系统错误 <BR>错误码：' + e.status);
			}
		});
	}

	//生成杜邦图内部函数
	function _initdupont(chartId, chartType) {
		if (chartType == "04") {
			_initdupDim(chartId);
		}else{
			_initStrdupont(chartId);
		}
	}

	//生成维度杜邦图维度集合
	function _initdupDim(chartId) {
		var returnDimkey = "";
		var Dimselect = '<div><select id="idxDim"></select></div><div id = "dubanghome" style="width:100%;height:100%;"></div>';
		$('#chartBox' + chartId).html(Dimselect);
		$("#idxDim").css("width",70).css("height",23).css("margin-top",10).css("margin-left",10);
		$("#idxDim").change(function() {
			returnDimkey = $("#idxDim").find("option:checked").attr("id");
			if(returnDimkey){
				_initReldupont(returnDimkey, chartId);
			}
		});
		$.ajax({
			type : "post",
			url : "${ctx}/idx/analysis/show/getDim",
			async : true,
			dataType : 'json',
			data : {
				idxNo : idxNo
			},
			beforeSend : function() {
				BIONE.loading = true;
				BIONE.showLoading("正在加载数据中...");
			},
			complete : function() {
				BIONE.loading = false;
				BIONE.hideLoading();
			},
			success : function(dimList) {
				if (dimList.length > 0) {
					for ( var i in dimList) {
						if (i == 0) {
							$("#idxDim").append(
									"<option id="+dimList[i].dimTypeEnNm+ " selected >"
											+ dimList[i].dimTypeNm
											+ "</option>");
						} else {
							$("#idxDim").append(
									"<option id="+dimList[i].dimTypeEnNm+" >"
											+ dimList[i].dimTypeNm
											+ "</option>");
						}
					}
				}
				returnDimkey = $("#idxDim").find("option:checked").attr("id");
				if(returnDimkey){
					_initReldupont(returnDimkey, chartId);
				}
			},
			error : function(e) {
				BIONE.tip('发现系统错误 <BR>错误码：' + e.status);
			}
		});
	}
	
	//生成结构解析杜邦图
	function _initReldupont(returnDimkey,chartId){
		$.ajax({
			type : "post",
			url:"${ctx}/idx/analysis/show/initReldupont",
			async: true,
			dataType: 'json',
			data: {
				idxNo : idxNo,
				idxNm : idxName,
				date : date,
	            org : org,
	            currency : currency,
	            chartId : chartId,
				dataUnit : dataUnit,
				returnDimkey : returnDimkey
			},
			beforeSend : function() {
				BIONE.loading = true;
				BIONE.showLoading("正在加载数据中...");
			},
			complete : function() {
				BIONE.loading = false;
				BIONE.hideLoading();
			},
			success : function(resultMap) {
				if(resultMap.dupontnodes){
					var $dimDupont = $('<div id = "dimDupont" style="width:100%;height:100%;overflow:auto;position:relative;"></div>');
					var rootNode = resultMap.dupontnodes;
					$("#chartBox" + chartId).height($("#indexTab").height() - 52);
					$("#dubanghome").html($dimDupont);
					var dimDupont = $dimDupont.dupontChart({
						lineColor : "#56A2F5",
						slice : 20,
						sharpWidth : 120 //形状宽度
					});
					dimDupont.setData(rootNode);
					dimDupont.loadData();
				}
			},
			error: function (e) {
				BIONE.tip('发现系统错误 <BR>错误码：' + e.status);
			}
		});
	}
	
	//生成关系解析杜邦图
	function _initStrdupont(chartId){
		$.ajax({
			type : "post",
			url:"${ctx}/idx/analysis/show/initStrdupont",
			async: true,
			dataType: 'json',
			data: {
				idxNo : idxNo,
				chartId : chartId,
				idxNm : idxName,
	            date : date,
	            org : org,
	            currency : currency,
	            orgType : idxBelongType
			},
			beforeSend : function() {
				BIONE.loading = true;
				BIONE.showLoading("正在加载数据中...");
			},
			complete : function() {
				BIONE.loading = false;
				BIONE.hideLoading();
			},
			success : function(resultlist) {
				if(resultlist.dupontnodes){
					var $strDupont =$('<div id = "strDupont" style="height:100%;width:100%;overflow:auto;position:relative;"></div>');
					var rootNode = resultlist.dupontnodes;
					$("#chartBox" + chartId).height($("#indexTab").height() - 52);
					$('#chartBox' + chartId).html($strDupont);
					var strDupont = $strDupont.dupontChart({
						lineColor : "#56A2F5",
						slice : 20,
						sharpWidth : 120 //形状宽度
					});
					strDupont.setData(rootNode);
					strDupont.loadData();
				}
			},
			error: function (e) {
				BIONE.tip('发现系统错误 <BR>错误码：' + e.status);
			}
		});
	}
	
	//生成图表文本
	function _initChartText(chartId,chartType){
		$.ajax({
			type : "post",
			url:"${ctx}/idx/analysis/show/getText",
			async: false,
			dataType: 'json',
			data: {
				idxNo : idxNo,
				chartId : chartId,
				chartType : chartType,
				dataUnit : dataUnit,
				idxNm : idxName,
				date : date,
	            org : org,
	            currency : currency,
	            orgType : idxBelongType
			},
			success : function(resultMap) {
				if(resultMap.chartText){
					$('#chartBox' + chartId).width($('#indexTab').width() * 0.8);
					$('#chartText' + chartId).width($('#indexTab').width() * 0.2);
					$('#chartText' + chartId).html("");
					$('#chartText' + chartId).prepend(resultMap.chartText);
				}
			},
			error: function (e) {
				BIONE.tip('发现系统错误 <BR>错误码：' + e.status);
			}
		});
	}
	
	//提前请求趋势分析图
	function aheadTre(idxNo,chartId){
		$.ajax({
			type : "post",
			url : "${ctx}/idx/analysis/show/aheadTre",
			async : true,
			dataType : 'json',
			data : {
				idxNo : idxNo,
				chartId : chartId,
				date : date,
				org : org,
				currency : currency
			},
			success : function(resultMap) {
			},
			error : function(e) {
			}
		});
	}
</script>
</head>
<body>
	<div id="template.left.up.icon">
		<img src="${ctx }/images/classics/icons/application_side_tree.png" />
	</div>
	<div id="template.left.up">
		<span style="font-size: 12">指标树</span>
	</div>

	<div id="template.right">
		<div id="tab" style="display: none; width: 100%; height: 100%;"></div>
		<div id="cover" class="l-tab-loading" style="display:block; background:url(/rpt-web/images/classics/index/bg_right_bottom.jpg) #ffffff;"></div>
	</div>
</body>
</html>