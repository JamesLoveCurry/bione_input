<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<meta name="decorator" content="/template/template22A_BS.jsp">
<head>
<script type="text/javascript" 
	src="${ctx}/js/contextMenu/jquery.contextMenu.min.js"></script>
<link rel="stylesheet" type="text/css"
	href="${ctx}/css/classics/contextMenu/jquery.contextMenu.css" />
<link rel="stylesheet" type="text/css"
	href="${ctx }/js/datashow/img/iconfont.css" />
<link rel="stylesheet" type="text/css"
	href="${ctx}/js/bootstrap3/css/bootstrap.css" />
<link rel="stylesheet" type="text/css"
	href="${ctx}/css/classics/daterangepicker/daterangepicker.css" />
<link rel="stylesheet" href="${ctx}/js/exlabel/exlabel.css">
<script type="text/javascript" src="${ctx}/js/exlabel/exlabel.js"></script>
<script type="text/javascript" src="${ctx}/js/numeral/numeral.js"></script>
<script type="text/javascript" src="${ctx}/js/underscore/underscore-min.js"></script>
<script type="text/javascript"
	src="${ctx}/js/bignumber/bignumber.min.js"></script>
<script type="text/javascript" src="${ctx}/js/uuid/uuid.js"></script>
<script type="text/javascript"
	src="${ctx}/js/daterangepicker/moment.min.js"></script>
<script type="text/javascript"
	src="${ctx}/js/daterangepicker/daterangepicker.js"></script>
<style>
body {
  font-size: 12px;
}
</style>
<script>
	var busiType = "02";//默认显示1104机构
	var result;
	var treeTab = null;
	var storeTab = null;
	var downdload = null;
	var dimNos = [];//多个指标的维度交集
	var columns = [];//表格列
	var measureFilterName = [];//度量单位中文名称
	var type = "";
	var leftTreeObj = null;
	var storeTreeObj = null;
	var grid = null;
	var notAllowedIcon = "/images/classics/icons/cancel.gif";
	var allowedIcon = "/images/classics/icons/accept.png";
	var labelFlag = false;
	var orgs = JSON2.parse('${orgs}');
	var judge = false;
	var draggingNode = null;
	var chooseIdx = [];
	var dimFilters = [];
	var panel = null;
	var srcCode ='';
	var initFlag = true;
	var queryResult = [];
	var queryRules = '';//查询规则
	var queryDate = [];//查询日期，当不按照日力度查询的时候生效
	var initUserOrgNm = "";//用户默认所属机构
	var unit ={
		"01" : 1,
		"02" : 100,
		"03" : 1000,
		"04" : 10000,
		"05" : 100000000
	};
	$(function(){
		initRptBusiType();
		initRptVar();
		initExport();
		initTreeTab();
		initTreeSearch();
		initPanel();
		initGrid();
		getSystemVer();
		$("#storeConfig").hide();
		$("#showConfig").show();
		initTool();
	});
	
	function initTool(){
		$("#dimResize").click(function(){
			//页面切换
			var dimFilterDisplay = $("#dimFilter").css('display');
			var panelDisplay = $("#panel").css('display');
			$("#panel").css('display', dimFilterDisplay);
			$("#dimFilter").css('display', panelDisplay);
		});
		initDate();
		initQueryRules();
		initQueryOrg();
		initQueryArchiveType();
		initQueryCurrency();
		$('#dimCurrency').hide();
		$('#maingridgrid').height($('#maingridgrid').height() + 20);
	}
	
	Date.prototype.Format = function(fmt) 
	{ //author: meizz 
	  var o = { 
	    "M+" : this.getMonth()+1,                 //月份 
	    "d+" : this.getDate(),                    //日 
	    "h+" : this.getHours(),                   //小时 
	    "m+" : this.getMinutes(),                 //分 
	    "s+" : this.getSeconds(),                 //秒 
	    "q+" : Math.floor((this.getMonth()+3)/3), //季度 
	    "S"  : this.getMilliseconds()             //毫秒 
	  }; 
	  if(/(y+)/.test(fmt)) 
	    fmt=fmt.replace(RegExp.$1, (this.getFullYear()+"").substr(4 - RegExp.$1.length)); 
	  for(var k in o) 
	    if(new RegExp("("+ k +")").test(fmt)) 
	  fmt = fmt.replace(RegExp.$1, (RegExp.$1.length==1) ? (o[k]) : (("00"+ o[k]).substr((""+ o[k]).length))); 
	  return fmt; 
	}
	
	//初始化日期控件
	function initDate(){
		var selectedNodes = null;
		if(dimFilters["DATE"] != null){
			startDate = dimFilters["DATE"].selectedNodes[0].id;
			endDate = dimFilters["DATE"].selectedNodes[1].id;
		}else{
			endDate = startDate = new Date().Format("yyyy-MM-dd");
		} 
		$('#daterange').daterangepicker({
			"locale" :{
				format : "YYYY-MM-DD",
				separator: ' 至 ',
				monthNames: ["一月","二月","三月","四月","五月","六月","七月","八月","九月","十月","十一月","十二月"],
				daysOfWeek: ["日","一","二","三","四","五","六"]
			},
		    "showDropdowns": true,
		    "autoApply": true,
		    "linkedCalendars":false,
		    "startDate": startDate,
		    "endDate":  endDate
		}, function(start, end, label) {
			var daterangepicker = $("#daterange").data('daterangepicker');
			var date = $("#daterange").val().split("至");
			selectedNodes = [{id : start.format("YYYY-MM-DD"), text : start.format("YYYY-MM-DD")},
			                     {id :end.format("YYYY-MM-DD"), text : end.format("YYYY-MM-DD")}];
			dimFilters["DATE"] = {"selectedNodes" : selectedNodes};
		});
		selectedNodes = [{id : startDate, text : startDate},
	                     {id :endDate, text : endDate}];
		dimFilters["DATE"] = {"selectedNodes" : selectedNodes};
	}
	
	//初始化日期力度
	function initQueryRules(){
		$("#queryRules").ligerComboBox(
				{
					data : [{
						id:'everyDay',
						text:'每日'
					},{
						id:'monthBegin',
						text:'月初'
					},{
						id:'monthEnd',
						text:'月末'
					},{
						id:'quarterBegin',
						text:'季初'
					},{
						id:'quarterEnd',
						text:'季末'
					},{
						id:'yearBegin',
						text:'年初'
					},{
						id:'yearEnd',
						text:'年末'
					}],
					initValue : queryRules? queryRules : 'everyDay',
					onSelected : function(id, text) {
						queryRules = id;
						strQueryDate(id);
					}
				});
	}
	
	//初始化机构选择框
	function initQueryOrg(){
		$("#queryOrg").ligerComboBox({
			onBeforeOpen:function(){
				BIONE.commonOpenDialog("维度过滤", "chooseOrg", 400, 450, 
						"${ctx}/report/frame/datashow/idx/dimFilter?dimNo=ORG", null, function(){
				});
				return false;
			}
		});
		$.ligerui.get('queryOrg').setText(initUserOrgNm);
	}
	
	//初始化币种选择框
	function initQueryCurrency(){
		$("#queryCurrency").ligerComboBox({
			onBeforeOpen:function(){
				BIONE.commonOpenDialog("维度过滤", "chooseOrg", 400, 450, 
						"${ctx}/report/frame/datashow/idx/dimFilter?dimNo=CURRENCY", null, function(){
				});
				return false;
			}
		});
	}
	
	//初始化日期力度
	function initQueryArchiveType(){
		$("#archiveType").ligerComboBox({
			data : [
					{ text : '初始数据(元)', id : '05' },
					{ text : '初始数据', id : '03' }, 
					{ text : '填报数据', id : '04' }, 
					{ text : '归档数据', id : '01' }, 
					{ text : '回灌数据', id : '02' } 
			],
			initValue : '04'
		});
	}
	
	//初始化日期维度选择，默认选择当前日期
	function initDateFilter(){
		var nowDate = new Date();
		var year = nowDate.getFullYear();
	    var month = nowDate.getMonth() + 1;
	    var day = nowDate.getDate();   
	    var clock = year + "-";	        
        if(month < 10)
            clock += "0";       
        clock += month + "-";       
        if(day < 10)
            clock += "0";          
        clock += day;
		var selectedNodes = [{id : clock, text : clock}, {id :clock, text : clock}];
		dimFilters["DATE"] = {"selectedNodes" : selectedNodes};
	}
	
	//初始化机构维度选择，默认选择当前用户所属机构
	function initOrgFilter(){
		var checkIds =[];
		var selectedNodes =[];
		if(orgs && orgs.length>0){
			for(var i=0 ;i<orgs.length;i++){
				if(busiType == orgs[i].id.orgType){
					checkIds.push(orgs[i].id.orgNo);
					initUserOrgNm = orgs[i].orgNm;
					selectedNodes.push({
						id : orgs[i].id.orgNo,
						text : orgs[i].orgNm
					});
				}
			}
		}
		dimFilters["ORG"]={
			checkIds : checkIds,
			selectedNodes : selectedNodes
		}
	}
	
	function initTreeTab(){
		treeTab = $('#navtab').ligerTab({
			contextmenu: false,
			height: $('#left').height() - 2,
			onAfterSelectTabItem : function(tabId) {
				if (tabId == 'store') {
					initStoreTab();
					initStoreTree();
				}
				if (tabId == 'rptIdx') {
					$("#showConfig").show();
					$("#storeConfig").hide();
				}
			}
		}); 
		treeTab.selectTabItem("rptIdx");
	}
	
	function initStoreTab(){
		$("#storeConfig").show();
		$("#showConfig").hide();
		if(storeTab == null){
			storeTab = $('#store_tab').ligerTab({
				contextmenu: true,
				height: $('#center').height() - 2,
				onAfterAddTabItem: function() {
					$('#cover').hide();
				},
				onAfterRemoveTabItem: function() {
					if (tmpTab.getTabItemCount() == 0) {
						$('#cover').show();
					}
				}
			});
		}
	}
	
	//初始化标签函数
	function initPanel(){
		panel = $('#panel').exlabel({
			type: 'btn',
			text: 'text',
			value: 'id',
			isEdit : false,
			isInput : false,
			callback: {
				onClick : function(data,flag){
					if(data.id == "DATE" || data.id == "ORG" ){
						return false;
					}
					labelClick(data,flag);
				}
		    }
		});
		panel.add({
			id : "DATE",
			text : "日期"
		});
		panel.add({
			id : "ORG",
			text : "机构编号"
		});
		panel.selectNodes("DATE,ORG",true,false);
	}
	
	function labelClick(e,flag){
		if(flag == true){
			var i=0;
			for(i=0;i<columns.length;i++){
				if(columns[i].businessType == "INDEX" || columns[i].businessType == "MEASURE"){
					break;
				}
			}
			columns.splice(i,0,{
				isSort: true,
				display: e.text,
				name: e.id,
				text : e.text,
				businessType : 'DIM',
				sortname : "$"+e.id,
				width: ((e.text.length + 5)* 12 > 120)? (e.text.length + 5) * 12  : 120
			});
			if("CURRENCY" == e.id){
				$('#dimCurrency').show();
			}
		}else{
			var i=0;
			for(i=0;i<columns.length;i++){
				if(columns[i].name == e.id){
					break;
				}
			}
			columns.splice(i,1);
			//删除所选维度过滤
			delete dimFilters[e.id];	
			if("CURRENCY" == e.id){
				$('#dimCurrency').hide();
			}
		}
		setGridColumns();
	}
	
	function initGrid() {
		columns = [];
		columns.push({
			isSort: true,
			display: "日期",
			name: 'DATE',
			text : '日期',
			type : 'date',
			sortname : "$DATE",
			businessType : 'DIM',
			width: 120
		},{
			isSort: true,
			display: "机构",
			name: 'ORG',
			text : '机构',
			type : 'org',
			sortname : "$ORG",
			businessType : 'DIM',
			width: 120
		});
		grid = $('#maingrid').ligerGrid(
				{
					toolbar : {},
					width : '100%',
					columns : columns,
					usePager : true,
					checkbox : false,
					data :{
						Rows :[]
					},
					dataAction : 'local',
					allowHideColumn : false,
					delayLoad : false,
					onBeforeShowData : function() {
		
					},
					onAfterShowData : function() {
					},
					onAfterChangeColumnWidth : function() {
						$(".delBtn").each(
								function() {
									$(this)
											.css(
													"left",
													$(this).parent().parent()
															.width() - 10);
								})
					},
					mouseoverRowCssClass : null
				});
		var buttons = [{
			text : "查询",
			icon : "search",
			operNo : "idx-show-search",
			click : function() {
				indexQuery();
			}
		},{
			text : "收藏",
			icon : "store",
			operNo : "idx-show-store",
			click : function() {
				fstore();
			}
		},{
			text : "导出",
			icon : "export",
			operNo : "idx-show-export",
			click : function() {
				fexport();
			}
		},{
			text : "折线图",
			icon : "chartForm",
			operNo : "idx-show-linechart",
			click : function() {
				if(result && result.length > 0){
					BIONE.commonOpenDialog("图表", "chartDialog", 1100, 500,
							"${ctx}/report/frame/datashow/idx/chart?chartType=01" );
				}
			}
		},{
			text : "柱状图",
			icon : "list4",
			operNo : "idx-show-barchart",
			click : function() {
				if(result && result.length > 0){
					BIONE.commonOpenDialog("图表", "chartDialog", 1100, 500,
							"${ctx}/report/frame/datashow/idx/chart?chartType=02" );
				}
			}
		}];
		grid.setHeight($(document).height() - 160);
		BIONE.loadToolbar(grid, buttons);
		addDivToColumn();
	}
	
	function fstore(){
		if(chooseIdx.length <= 0){
			BIONE.tip("未配置指标，无法收藏！");
			return; 
		}
		var req = window.favour = {
			'dimNo': [], 
			'colums': chooseIdx,
			'searchArg' : [],
			'busiType' : busiType
		};
			
		$.each(grid.getColumns(), function (i, n) {
			 if ("DIM" === n.businessType) {
				if ("DATE" == n.name) {
				} else {
					req.searchArg.push({'dimNo': n.name,
						'op' : '01',
						'value' : dimFilters[n.name]?dimFilters[n.name].checkIds:[]
					});
				}
			} 
		});
		BIONE.commonOpenSmallDialog('收藏', 'store'
					, '${ctx}/report/frame/datashow/idx/store');
	}
	
	function fexport(){
		if(window.queryResult.length <= 0){
			BIONE.tip("无可导出数据！");
			return;
		}
		var cols = {};
		var formates = {};
		var unit = {};
		for(var i in  columns){
			cols[columns[i].name] = columns[i].display; 
			if(columns[i].businessType == "DIM"){
				formates[columns[i].name] = "@";
			}
		}
		for(var j in chooseIdx){
			var format = "";
			var val = "";
			if(chooseIdx[j].dataType == "01"){
				format += "#,##";
			}
			if(chooseIdx[j].dataPrecision > 0){
				format += "0.";
				for(var h=0;h<chooseIdx[j].dataPrecision;h++){
					format += "0";
				}
			}
			if(chooseIdx[j].dataType == "02"){
				format += "%";
			}
			if(chooseIdx[j].dataType == "03"){
				format = "0";
			}
			formates[chooseIdx[j].detailId] = format;
			unit[chooseIdx[j].detailId] = chooseIdx[j].dataUnit;
		}
		$.ajax({
			url: '${ctx}/report/frame/datashow/idx/resultExport',
			type: 'post',
			data: {
				colums: JSON2.stringify(cols),
				result : JSON2.stringify(window.queryResult),
				formate: JSON2.stringify(formates),
				unit : JSON2.stringify(unit)
			},
			dataType: 'json',
			beforeSend : function() {
				BIONE.loading = true;
				BIONE.showLoading("正在生成数据文件中...");
			},
			complete : function() {
				BIONE.loading = false;
				BIONE.hideLoading();
			},
			success: function(json) {
				if(json.fileName){
					var src = '';
					src = "${ctx}/report/frame/datashow/idx/download?fileName="+json.fileName;
					downdload.attr('src', src);
				}
				else{
					BIONE.tip("数据文件生成失败");
				}
			},
			error : function(result, b) {
			}
		});
	}
	
	function clone(obj){
		var o;
		switch(typeof obj){
		case 'undefined': break;
		case 'string'   : o = obj + '';break;
		case 'number'   : o = obj - 0;break;
		case 'boolean'  : o = obj;break;
		case 'object'   :
			if(obj === null){
				o = null;
			}else{
				if(obj instanceof Array){
					o = [];
					for(var i = 0, len = obj.length; i < len; i++){
						o.push(clone(obj[i]));
					}
				}else{
					o = {};
					for(var k in obj){
						o[k] = clone(obj[k]);
					}
				}
			}
			break;
		default:		
			o = obj;break;
		}
		return o;	
	}
	
	function addGridData(queryResult){ // 将搜索结果转换格式添加到grid中
		var archiveType = $.ligerui.get('archiveType').getValue();
		window.queryResult = queryResult;
		result = clone(queryResult);
		if(queryResult && queryResult.length > 0){
			for(var i=0;i<queryResult.length;i++){
				for(var j=0;j<chooseIdx.length;j++){
					var format = "";
					var val = "";
					if(chooseIdx[j].dataType == "01"){
						format += "0,";
					}
					if(archiveType != "05"){// 初始数据（元），不做精度处理
						if(chooseIdx[j].dataPrecision > 0){
							format += "0.";
							for(var h=0;h<chooseIdx[j].dataPrecision;h++){
								format += "0";
							}
						}
					}else{
						format += "0,0.00000";
					}
					if(chooseIdx[j].dataType == "02"){
						try{
							val = new BigNumber(queryResult[i][chooseIdx[j].detailId]).times(new BigNumber(100)).toNumber();
						}
						catch(e){
							val = 0;
						}
						
					}
					else{
						try{
							if(archiveType == "05"){// 初始数据（元），不做单位处理
								val = new BigNumber(queryResult[i][chooseIdx[j].detailId]).toNumber();
							}else{
								val = new BigNumber(queryResult[i][chooseIdx[j].detailId]).dividedBy(new BigNumber(unit[chooseIdx[j].dataUnit])).toNumber();
							}
						}
						catch(e){
							val = 0; 
						}
					}
					var number = numeral(val);
					val = number.format(format);
					if(chooseIdx[j].dataType == "02"){
						val += "%";
					}
					result[i][chooseIdx[j].detailId] = val;
				}
			}
		}
		grid.sortedData = null;//added by fangjuan 20160601'
		grid.set('data',{Rows : result, Total : queryResult.length});	
	}
		
	function indexQuery(){// 指标搜索
		strQueryDate(queryRules);
		var condition = {};
		var cols = {};
		var indexHis = [];
		condition.QueryType = "index";
		if(dimFilters["DATE"] && chooseIdx.length>0 ){
			var searchArg = [];
			var dimNo = [];
			for(var i=0;i<columns.length;i++){
				if(columns[i].businessType == "DIM"){
					var tmp = columns[i].name;
					dimNo.push(tmp);
					if(dimFilters[tmp]){
						if(tmp != "DATE"){
							searchArg.push({DimNo : tmp, Op : '=', Value : dimFilters[tmp].checkIds});
						}else{
							if(('everyDay' != queryRules) && (queryDate.length > 0)){//判断是传日期区间还是日期时点值
								searchArg.push({DimNo : tmp, Op : '=', Value : queryDate});
							}else{
								searchArg.push({DimNo : tmp, Op : '>=', Value : [dimFilters["DATE"].selectedNodes[0].id.split("-").join("")]});
								searchArg.push({DimNo : tmp, Op : '<=', Value : [dimFilters["DATE"].selectedNodes[1].id.split("-").join("")]});
							}
						}
					}else{
						BIONE.tip(columns[i].display + "维度未选择，请选择！");
						return;
					}
				}
			}
			condition.DimNo = dimNo;
			
			if(chooseIdx.length > 0){
				var column = [];
				for(var tmp =0 ;tmp< chooseIdx.length; tmp++){
					var obj = {
							ColumNo : chooseIdx[tmp].detailId, 
							IndexNo : chooseIdx[tmp].indexNo,
							SearchArg : searchArg
					};
					var indexNo = "";
					if(chooseIdx[tmp].measureNo){
						obj.MeasureNo = chooseIdx[tmp].measureNo; 
					}
					column.push(obj);
					indexHis.push({indexNo: chooseIdx[tmp].indexNo, indexVerId: chooseIdx[tmp].indexVerId});
				}
				condition.Colums = column;
			}
			for(var tmp in columns){
				cols[columns[tmp].name] = columns[tmp].text;
			}
			
			condition.ArchiveType = $.ligerui.get('archiveType').getValue();
			searchJson = JSON2.stringify(condition);
			syncSysCode();
			$.ajax({
				url: '${ctx}/report/frame/datashow/idx/search/frsResult',
				type: 'post',
				data: {
					parameter: JSON2.stringify(condition),
					columns : JSON2.stringify(cols),
					indexNos: JSON2.stringify(indexHis),
					srcSysCode : window.srcCode,
					idxCols : JSON2.stringify(chooseIdx)
				},
				dataType: 'json',
				beforeSend : function() {
					BIONE.loading = true;
					BIONE.showLoading("正在查询数据中...");
				},
				complete : function() {
					BIONE.loading = false;
					BIONE.hideLoading();
				},
				success: function(json) {
					if (json && json.Code && json.Code == "0000") {
						result = json.Msg;
						addGridData(result);
					}else{
						BIONE.tip(json.Msg);
					}
				},
				error : function(result, b) {
					grid.set('data',{Rows : [], Total : 0});	
					BIONE.tip("查询异常");
				}
			});
		}else{
			BIONE.tip("查询日期未选择或未配置查询指标！");
		}
	}
	
	function initExport(){
		downdload = $('<iframe id="download"  style="display: none;"/>');
		$('body').append(downdload);
	}
	
	function initTreeSearch(){
		$("#treeSearchIcon").live('click',function(){// 树搜索
			var selectTabId = treeTab.getSelectedTabItemID();
			if('rptIdx' != selectTabId){
				treeTab.selectTabItem('rptIdx');
			}
			var verId = $.ligerui.get("rptVerId").getValue();	
			initRptIdxTree($('#treeSearchInput').val(), verId);
		});
		$('#treeSearchInput').bind('keydown', function(event) {
			var selectTabId = treeTab.getSelectedTabItemID();
			if (event.keyCode == 13) {
				if('rptIdx' != selectTabId){
					treeTab.selectTabItem('rptIdx');
				}
				var verId = $.ligerui.get("rptVerId").getValue();	
				initRptIdxTree($('#treeSearchInput').val(), verId);
	 		}
		});
	}
	
	function initStoreTree(param) {
		$("#storetreeContainer").height($("#navtab").height()-90);
		storeTreeObj = $.fn.zTree.init($('#store-tree'), {
			data: {
	 			key: {
	 				name: 'text'
	 			},
	 			simpleData: {
	 				enable: true,
	 				idKey: 'id',
	 				pIdKey: 'upId'
	 			}
	 		},
 			callback: {
 				onClick: function(event, treeId, treeNode) {
 					if ('catalog' == treeNode.params.type) {
 						return;
 					}
 					storeTab.addTabItem({
						tabid: treeNode.id,
						text: treeNode.text,
						url: '${ctx}/report/frame/datashow/idx/show?instanceId=' + treeNode.id,
						showClose: true
					});
 				}
 			}
		});
		BIONE.loadTree('${ctx}/report/frame/datashow/idx/store/tree',storeTreeObj,{param: param});
	}
	
	//添加拖拽控制
	function setDragDrop(dom, receiveDom,labelInfo) {
		if (typeof dom == "undefined" || dom == null) {
			return;
		}
		$(dom).ligerDrag({
			proxy : function(target, g, e) {
				var defaultName = "指标";
				var proxyLabel = "${ctx}" + notAllowedIcon;
				var targetTitle = labelInfo;
				var proxyHtml = $('<div style="width：150;position: absolute;padding-left: 10px;color: #183152;font-weight: bold;height: 20px;line-height: 19px;overflow: hidden;background: #E5EFFE ;border: 1px solid #DDDDDD;border-top:none; z-index:9999;"><span class="dragimage_span" style="position:absolute;width:16px;height:16px;left:5px;top:2px;background:url('
								+ proxyLabel
								+ ')" ></span><span style="padding-left: 14px;">'
								+ targetTitle + '</span></div>');
				var div = $(proxyHtml);
				div.appendTo('body');
				return div;
			},
			revert : false,
			receive : receiveDom,
			onStartDrag : function(current, e) {
				// 获取拖拽树节点信息
				var treeAId = current.target.attr("id");
				var treeId = treeAId;
				if (treeAId) {
					var strsTmp = treeAId.split("_");
					var treeId = treeAId;
					if (strsTmp.length > 1) {
							var newStrsTmp = [];
							for (var i = 0; i < strsTmp.length - 1; i++) {
								newStrsTmp.push(strsTmp[i]);
							}
							treeId = newStrsTmp.join("_");
					}
					draggingNode = leftTreeObj.getNodeByTId(treeId);
				}
			},
			onDragEnter : function(receive, source, e) {
				var allowLabel = "${ctx}" + allowedIcon;
				source.children(".dragimage_span").css("background","url('" + allowLabel + "')");
			},
			onDragOver : function(receive, source, e) {
				var allowLabel = "${ctx}" + allowedIcon;
				source.children(".dragimage_span").css("background","url('" + allowLabel + "')");
			},
			onDragLeave : function(rceive, source, e) {
				var notAllowLabel = "${ctx}" + notAllowedIcon;
				source.children(".dragimage_span").css("background","url('" + notAllowLabel + "')");
			},
			onDrop : function(obj, target, e) {
				var treeNode = draggingNode;
				if(treeNode.params.nodeType == "measureInfo"){
					var measureNode = treeNode;
					treeNode = treeNode.getParentNode();
					measureNode.params.dimNos = treeNode.params.dimNos;
					addIdx(measureNode, true);
				}else{
					addIdx(treeNode, true);
				} 
			}
		});
	}

	function renderLabelPad(){//重绘维度标签区
		panel.removeAll();
		panel.add({
			id :　"DATE",
			text : "日期"
		});
		panel.add({
			id :　"ORG",
			text : "机构编号"
		});
		var params = "";
		for(var i=0;i<dimNos.length;i++){
			params += dimNos[i] + ",";
		}
		$.ajax({
			url: '${ctx}/report/frame/datashow/idx/getDimNm',
			type: 'post',
			data: {
				dimNos: params
			},
			dataType: 'json',
			beforeSend : function() {
				BIONE.loading = true;
				BIONE.showLoading("正在加载数据中...");
			},
			complete : function() {
				BIONE.loading = false;
				BIONE.hideLoading();
			},
			success: function(json) {
				for(var i=0;i<dimNos.length;i++){
					if(dimNos[i] != "INDEXNO" && dimNos[i] != "DATE"){
						panel.add({
							id :　dimNos[i],
							text : json[dimNos[i]]
						});
					}
				}
				var ids = [];
				for(var x=0;x<columns.length;x++){
					for(var y=0;y<dimNos.length;y++){
						if(columns[x].name == dimNos[y]){
							ids.push(columns[x].name);
							break;
						}
					}
				}
				panel.selectNodes("DATE,ORG",true,false);
				panel.selectNodes(ids.join(","),true);
			},
			error : function(result, b) {
				BIONE.tip('发现系统错误 <BR>错误码：' + result.status);
			}
		});
	}
	
	function syncSysCode(){
		srcCode = "01";
		for(var i = 0 ;i < chooseIdx.length;i++ ){
			if(chooseIdx[i].data.idxBelongType == "01"){
				srcCode = "01";
				break;
			}
			else{
				if(srcCode == "01"){
					srcCode = chooseIdx[i].data.idxBelongType;
				}
				else if(srcCode != chooseIdx[i].data.idxBelongType){
					srcCode = "01";
					break;
				}
			}
		}
	}
	
	function addIdx(treeNode, isRenderLabel) {
		var colId = uuid.v1().split("-").join("");
		var text = treeNode.data.indexNm;
		var indexNo = treeNode.data.id.indexNo;
		var measureNo = "";
		if(treeNode.nodeType == "measureInfo"){
			text += "."+treeNode.text;
			measureNo = treeNode.id;
		}
		if(!treeNode.data.dataUnit)
			treeNode.data.dataUnit = "01";
		if(!treeNode.data.dataType)
			treeNode.data.dataType = "01";
		var idxCol = {
			detailId : colId,
			indexAlias : text,
			data : treeNode.data,
			type : treeNode.nodeType,
			indexNo : indexNo,
			measureNo : measureNo,
			indexVerId : treeNode.data.id.indexVerId,
			dimNos : treeNode.params.dimNos,
			dataUnit : treeNode.data.dataUnit,
			dataType : treeNode.data.dataType,
			dataPrecision : 2,
			ruleId : '1',
			timeMeasureId : '1',
			modeId : '1',
			isPassyear :""
		}
		chooseIdx.push(idxCol);
		//构造维度
		if(isRenderLabel){
			if(dimNos.length == 0){
				dimNos = idxCol.dimNos.split(",");
			}
			else{
				dimNos = _.intersection(dimNos, idxCol.dimNos.split(","));
			}
			renderLabelPad();
		}
		//移除掉非公共的维度列
		syncDimCol();
		
		var businessType = "";
		if(measureNo){//有度量
			businessType = "MEASURE";
		}else{
			businessType = "INDEX";
		}
		columns.push({
			isSort: true,
			display: idxCol.indexAlias,
			name: idxCol.detailId,
			indexNo : idxCol.indexNo,
			indexVerId : idxCol.indexVerId,
			businessType : 'MEASURE',
			width: ((idxCol.indexAlias.length * 12 + 80) > 120)? (idxCol.indexAlias.length * 12+80) : 120
		});
		syncSysCode();
		setGridColumns();
	}
	
	function setGridColumns(){
		window.queryResult = [];
		grid.set("data", {Rows:[]});
		grid.set("columns", columns);
		grid.reRender();
		addDivToColumn();
	}
	
	function addDivToColumn() {
		for (var i = 0; i < columns.length; i++) {
			var columnName = columns[i].name;
			var businessType = columns[i].businessType;
			var indexNo = "";
			var indexVerId = "";
			if(columns[i].indexNo){
				indexNo = columns[i].indexNo;
				indexVerId = columns[i].indexVerId;
			}
			addColBtn(columnName, businessType, indexNo, indexVerId);
		}
	}

	function addColBtn(columnName, businessType, indexNo, indexVerId) {
		var img = $("td[columnname=" + columnName
				+ "] .l-grid-hd-cell-inner > #delBtn");
		if (img.length == 0) {
			var $selectedLi = $("td[columnname=" + columnName
					+ "] .l-grid-hd-cell-inner ");
			var top = 0 - $(".l-panel-topbar").height()
			- $selectedLi.position().top + 13;
			if(businessType != "DIM"){
				var delImg = $(
						"<img src='${ctx}/images/classics/icons/icons_label_cross.png' colId = '"+columnName+"' style='width:7px;height:7px' class='l-grid-dim-filter' />")
						.attr('title', '删除');
				delImg.click(function() {
					deleteCol(this, $(this).attr("colId"));
				});
				var deldiv = $("<div id='delBtn' class='delBtn' style='display:block;position: relative;cursor: pointer;z-index:5;'/>");
				deldiv.css("left", ($selectedLi.width() - 10)).css(
						"top",
						0 - $(".l-panel-topbar").height()
								- $selectedLi.position().top + 10).css("width",
						"7px");
				deldiv.append(delImg);
				$selectedLi.append(deldiv);
				top = 0 - $(".l-panel-topbar").height()
				- $selectedLi.position().top + 5;
			}
			if(businessType == "DIM"){
/* 				var cofImg = $(
						"<img src='${ctx}/images/classics/icons/icons_filter.png' colId = '"+columnName+"' style='width:16px;height:16px' class='l-grid-dim-filter' />")
						.attr('title', '筛选');
				cofImg.click(function() {
					f_open_filter(columnName);
				}); */
				var cofdiv = $("<div id='cofdiv' style='display:block;position: relative;cursor: pointer;z-index:5;'/>");
				cofdiv.css("left", "10px").css(
						"top",
						top).css("width",
						"7px");
				cofdiv.append(cofImg);
				$selectedLi.append(cofdiv);
			}
			else{
				var cofImg = $(
						"<img src='${ctx}/images/classics/icons/cog.png' colId = '"+columnName+"' style='width:16px;height:16px' class='l-grid-dim-filter' />")
						.attr('title', '配置');
				cofImg.click(function() {
					BIONE.commonOpenDialog("指标配置", "attrEdit", 700, 500, 
							"${ctx}/report/frame/datashow/idx/config?id="+ columnName + "&indexNo=" + indexNo + "&indexVerId=" + indexVerId);
				});
				var cofdiv = $("<div id='cofdiv' style='display:block;position: relative;cursor: pointer;z-index:5;'/>");
				cofdiv.css("left", "10px").css(
						"top",
						0 - $(".l-panel-topbar").height()
								- $selectedLi.position().top + 5).css("width",
						"7px");
				cofdiv.append(cofImg);
				$selectedLi.append(cofdiv);
			}
		}
		
		function f_open_filter(dimNo){
			if(dimNo != "DATE"){
				BIONE.commonOpenDialog("维度过滤", "chooseOrg", 400, 450, 
						"${ctx}/report/frame/datashow/idx/dimFilter?dimNo=" + dimNo ,null, function(){
				});
			}else{
				BIONE.commonOpenDialog("维度过滤", "chooseOrg", 650, 400, 
						"${ctx}/report/frame/datashow/idx/dimFilter?dimNo=" + dimNo ,null, function(){
				});
			}
		}
		
		function deleteCol(g, colId) {
			for(var i in chooseIdx){
				if(chooseIdx[i].detailId == colId){
					break;
				}
			}
			chooseIdx.splice(i,1);
			for (i = 0; i < columns.length; i++) {
				if (columns[i].name == colId) {
					break;
				}
			}
			columns.splice(i, 1);
			syncLabel();
			syncDimCol();
			setGridColumns();
		}
	}
	
	function deleteColByCid(colId) {
		for(var i in chooseIdx){
			if(chooseIdx[i].detailId == colId){
				break;
			}
		}
		chooseIdx.splice(i,1);
		for (i = 0; i < columns.length; i++) {
			if (columns[i].name == colId) {
				break;
			}
		}
		columns.splice(i, 1);
		syncLabel();
		syncDimCol();
		setGridColumns();
	}
	
	function getIdx(id){
		for(var i in chooseIdx){
			if(chooseIdx[i].detailId == id){
				return chooseIdx[i];
			}
		}
	}
	
	function setIdx(id,idxCol){
		for(var i in chooseIdx){
			if(chooseIdx[i].detailId == id){
				chooseIdx[i] = idxCol;
			}
		}
	}
	
	function syncLabel(){
		dimNos = [];
		for (var i = 0; i < chooseIdx.length; i ++){
			if(dimNos.length == 0){
				dimNos = chooseIdx[i].dimNos.split(",");
			}else{
				dimNos = _.intersection(dimNos, chooseIdx[i].dimNos.split(","));
			}
		}
		renderLabelPad();
	}
	
	function syncDimCol(){
		//移除掉非公共的维度列
		for(var x =0;x<columns.length;x++){
			if(columns[x].businessType == 'DIM' && columns[x].name != "DATE" && columns[x].name != "ORG"){
				var y = 0;
				for(y=0;y<dimNos.length;y++){
					if(columns[x].name == dimNos[y]){
						break;
					}
				}
				if(y >= dimNos.length){
					delete dimFilters[columns[x].name];
					columns.splice(x,1);
					x--;
				}
			}
		}
	}
	
	//初始化业务信息
	function initRptBusiType(){
		$("#rptIdxTreeContainer").before('<div class="l-form" style="margin: 2px;"><ul><li style="width: 40%;">监管业务类型：</li><li style="width: 59%;"><input id="rptBusiType" /></li></ul></div>');
		$("#rptBusiType").ligerComboBox(
				{
					url : "${ctx}/report/frame/datashow/idx/getIdxTaskType.json",
					onSelected : function(id, text) {
						if (id && id != busiType) {
							busiType = id;
							getSystemVer();
						}
					}
				});
		$("#rptBusiType").parent().width("99%");
		$.ligerui.get("rptBusiType").selectValue(busiType);	
	}
	
	//初始化监管制度版本
	function initRptVar(){
		$("#rptIdxTreeContainer").before('<div class="l-form" style="margin: 2px;"><ul><li style="width: 40%;">制度版本选择：</li><li style="width: 59%;"><input id="rptVerId" /></li></ul></div>');
		$("#rptVerId").ligerComboBox(
				{
					onSelected : function(id, text) {
						if(id){
							$('#treeSearchInput').val("");
							initRptIdxTree(null, id);
							//统一报送模块下，不同版本切换不需要进行数据清空
							//removeAll();
							//initOrgFilter();
							//initDateFilter();
						}
					}
				});
		$("#rptVerId").parent().width("99%");
	}
	
	//清空全部信息
	function removeAll(){
		dimNos = [];//多个指标的维度交集
		columns = [];//表格列
		measureFilterName = [];//度量单位中文名称
		chooseIdx = [];
		dimFilters = [];
		panel.removeAll();
		initPanel();
		initGrid();
	}
	
	//初始化报表指标树
	function initRptIdxTree(searchRptNm, rptVerId){
        var $height = $(window.parent.document).height;
        $("#rptIdxTreeContainer").height($height-10);
		setting ={
				async:{
					enable:true,
					url:"${ctx}/report/frame/datashow/rptIdx/getRptIdxTree.json",
					autoParam:["nodeType", "id", "indexVerId"],
					otherParam:{"busiType":busiType, "searchRptNm": searchRptNm, "rptVerId":rptVerId},
					dataType:"json",
					dataFilter:function(treeId,parentNode,childNodes){
						if (childNodes){
							for (var i = 0; i < childNodes.length; i++){
								childNodes[i].nodeType = childNodes[i].params.nodeType;
								childNodes[i].isParent = childNodes[i].params.isParent == "true" ? true : false;
							}
						}
						return childNodes;
					}
				},
				data:{
					key:{
						name:"text",
						title:"title"
					},
					simpleData : {
						enable : true,
						idKey : "id",
						pIdKey : "upId",
						rootPId : null
					}
				},
				view:{
					addDiyDom: addDiyDom,
					selectedMulti:false
				},
				callback:{
					onNodeCreated : function(event, treeId, treeNode) {
						if(treeNode.nodeType == "rptIdx"){
							labelInfo = treeNode.text;
							setDragDrop("#" + treeNode.tId + "_span", "#maingrid",labelInfo);
						}
					}
				}
		};
		leftTreeObj = $.fn.zTree.init($("#rptIdxTree"), setting,[]);
	}
	
	//添加跳转报表界面图标
	function addDiyDom(treeId, treeNode) {
		if("rptMgr" == treeNode.params.nodeType){
			var aObj = $("#" + treeNode.tId + "_span");
			if ($("#diyBtn_"+treeNode.id).length>0) return;
			var editStr = "<i class='l-grid-icon icon-database' id='diyBtn_" +treeNode.id+ "' title='打开表样'  onfocus='this.blur();'></i>";
			aObj.after(editStr);
			var btn = $("#diyBtn_"+treeNode.id);
			leftTreeObj.updateNode(treeNode);
			if (btn) btn.bind("click", function(e){
				rptMgrCfg(treeNode);
				e.stopPropagation(); //可以使事件不再被派发
			}); 
		}
	}
	
	//根据业务类型获取对应的版本
	function getSystemVer() {
		if (busiType) {
			$.ajax({
				cache : false,
				async : false,
				url : "${ctx}/report/frame/design/cfg/getSystemList",
				dataType : 'json',
				data : {
					busiType : busiType
				},
				type : "post",
				success : function(result) {
					if (result != null && result.length > 0) {
						$.ligerui.get("rptVerId").setData(result);
						$.ligerui.get("rptVerId").selectValue(result[result.length-1].id);	
					}
				},
				error : function() {
					parent.BIONE.tip("数据加载异常，请联系系统管理员");
				}
			});
		}
	}
	
	//跳转报表配置界面
	function rptMgrCfg(treeNode){
		if(treeNode){
			var rptId = treeNode.data.rptId;
			var templateId = treeNode.data.cfgId;
			var verId = $.ligerui.get("rptVerId").getValue();	
			BIONE.commonOpenDialog("报表表样", "rptMgrCfg", 1200, 500, 
					"${ctx}/report/frame/datashow/rptIdx/rptMgrCfg?rptId="+ rptId + "&templateId=" + templateId + "&verId=" + verId);
		}
	}
	
		//构造查询日期数组
	function strQueryDate(id){
		var startDate = dimFilters["DATE"].selectedNodes[0].id;
		var endDate = dimFilters["DATE"].selectedNodes[1].id;
		if(startDate && endDate){
			switch(id){
			case 'everyDay': 
				break;
			case 'monthBegin': 
				queryDate = strMonthBegin(startDate, endDate);
				break;
			case 'monthEnd':
				queryDate = strMonthEnd(startDate, endDate);
				break;
			case 'quarterBegin': 
				queryDate = strQuarterBegin(startDate, endDate);
				break;
			case 'quarterEnd': 
				queryDate = strQuarterEnd(startDate, endDate);
				break;
			case 'yearBegin':
				queryDate = strYearBegin(startDate, endDate);
				break;
			case 'yearEnd':
				queryDate = strYearEnd(startDate, endDate);
				break;
			}
		}
	}
	
	//数组去重
	function newArr(array){ 
	    var arrs = []; 
	    for(var i = 0; i < array.length; i++){ 
	        if (arrs.indexOf(array[i]) == -1){ 
	            arrs.push(array[i])
	        }; 
	    } 
	    return arrs; 
	}
	
	function getDate(datestr){
		  var temp = datestr.split("-");
		  var date = new Date(temp[0],temp[1] - 1,temp[2]);
		  return date;
	}
	
	//构造月初
	function strMonthBegin(startDate, endDate){
		var dateArray = [];
		var startTime = getDate(startDate);
		var endTime = getDate(endDate);
		while((endTime.getTime()-startTime.getTime())>=0){
			var year = startTime.getFullYear();
			var month = startTime.getMonth() + 1;
			if(month < 10)
				month = "0" + month; 
			var day = "01";
			dateArray.push(year + "" + month + "" + day);
			startTime.setMonth(startTime.getMonth()+1);
		}
		return dateArray;
	}
	
	//构造月末
	function strMonthEnd(startDate, endDate){
		var dateArray = [];
		var startTime = getDate(startDate);
		var endTime = getDate(endDate);
		while((endTime.getTime()-startTime.getTime())>=0){
			var year = startTime.getFullYear();
			var month = startTime.getMonth() + 1;
			var day = new Date(year, month, 0).getDate();
			if(month < 10)
				month = "0" + month; 
			dateArray.push(year + "" + month + "" + day);
			startTime.setMonth(startTime.getMonth()+1);
		}
		return dateArray;
	}
	
	//构造季初
	function strQuarterBegin(startDate, endDate){
		var dateArray = [];
		var startTime = getDate(startDate);
		var endTime = getDate(endDate);
		while((endTime.getTime()-startTime.getTime())>=0){
			var year = startTime.getFullYear();
			var month = startTime.getUTCMonth() + 1;
			var day = "01";
			if(month >= 1 && month <= 3){
				month = "01"
			}else if(month >= 4 && month <= 6){
				month = "04"
			}else if(month >= 7 && month <= 9){
				month = "07"
			}else if(month >= 10 && month <= 12){
				month = "10"
			}
			dateArray.push(year + "" + month + "" + day);
			startTime.setMonth(startTime.getMonth()+1);
		}
		dateArray = newArr(dateArray);
		return dateArray;
	}
	
	//构造季末
	function strQuarterEnd(startDate, endDate){
		var dateArray = [];
		var startTime = getDate(startDate);
		var endTime = getDate(endDate);
		while((endTime.getTime()-startTime.getTime())>=0){
			var year = startTime.getFullYear();
			var month = startTime.getUTCMonth() + 1;
			if(month >= 1 && month <= 3){
				month = "03"
			}else if(month >= 4 && month <= 6){
				month = "06"
			}else if(month >= 7 && month <= 9){
				month = "09"
			}else if(month >= 10 && month <= 12){
				month = "12"
			}
			var day = new Date(year, month, 0).getDate();
			dateArray.push(year + "" + month + "" + day);
			startTime.setMonth(startTime.getMonth()+1);
		}
		dateArray = newArr(dateArray);
		return dateArray;
	}
	
	//构造年初
	function strYearBegin(startDate, endDate){
		var dateArray = [];
		var startTime = getDate(startDate);
		var endTime = getDate(endDate);
		while((endTime.getTime()-startTime.getTime())>=0){
			var year = startTime.getFullYear();
			var month = "01";
			var day = "01";
			dateArray.push(year + "" + month + "" + day);
			startTime.setYear(startTime.getFullYear()+1);
		}
		dateArray = newArr(dateArray);
		return dateArray;
	}
	
	//构造年末
	function strYearEnd(startDate, endDate){
		var dateArray = [];
		var startTime = getDate(startDate);
		var endTime = getDate(endDate);
		while((endTime.getTime()-startTime.getTime())>=0){
			var year = startTime.getFullYear();
			var month = "12";
			var day = "31";
			dateArray.push(year + "" + month + "" + day);
			startTime.setYear(startTime.getFullYear()+1);
		}
		queryDate = newArr(dateArray);
		return dateArray;
	}
</script>
</head>

<body>
	<div id="template.left.up.icon">
		<img src="${ctx}/images/classics/icons/application_side_tree.png" />
	</div>
	<div id="template.left.up">
		<span style="font-size: 12">指标树</span>
	</div>
	<div id="template.tabitem">
		<div tabid="rptIdx" title="报表指标" style="height: 100%; overflow: auto;">
			<div id="rptIdxTreeContainer"
				style="width: 100%; height: 80%; overflow: auto; clear: both; background-color: #FFFFFF;">
				<ul id="rptIdxTree"
					style="font-size: 12; background-color: #FFFFFF; width: 92%"
					class="ztree"></ul>
			</div>
		</div>
		<div tabid="store" title="收藏" style="height: 100%; overflow: auto;">
			<div id="storetreeContainer"
					style="width: 100%; overflow: auto; clear: both; background-color: #FFFFFF;">
				<ul id="store-tree"
					style="font-size: 12; background-color: #FFFFFF; width: 92%"
					class="ztree"></ul>
			</div>
		</div>
	</div>
		<div id="template.right">
			<div id="showConfig" style="height: 100%;width:100%;">
				<div id="content" style="height: 100%;width:100%;">
					<div class="l-form-tabs">
						<ul original-title="" class="ui-tabs-nav ui-helper-reset ui-helper-clearfix ui-widget-header ui-corner-all">
							<li class="ui-state-default ui-corner-top ui-tabs-selected ui-state-hover" data-index="0">
								<a href="javascript:void(0)">
									<a d='title3' style = "color: #4bbdfb;padding-left: 20px;padding-right: 0px;">维度选择</a>
								</a>
								<a id = 'dimResize' class='l-grid-icon icon-resize' style="margin-top: 5px; padding-left: 20px; padding-right: 0px;"></a>
							</li>
						</ul>
					</div>
					<div id="dimFilter" style="width:99%; height:100px; margin:2px; border: 1px solid transparent; border-radius: 4px; border-color: #ddd; padding-top: 10px;">
						<div style="margin-left: 20px; margin-top: 10px;">
							<div style="width: 75px; float: left;">
								<span >查询日期：</span>
							</div>
							<div class="l-text l-text-date" style="width: 21%; float: left; margin-left: 7px;">
								<input type="text" id="daterange" name="daterange" ltype="date"
								key="DATE" class="l-text-field" style="width: 100%;"></input>
								<div class="l-text-l"></div>
								<div class="l-text-r"></div>
								<div class="l-trigger" style="padding-bottom: 5px;">
									<div class="l-trigger-icon"></div>
								</div>
								<div class="l-trigger l-trigger-cancel" style="display: none;">
									<div class="l-trigger-icon"></div>
								</div>
							</div>
							<div style="float: left; margin-left: 40px;">
								<span style="font-size: 12">日期频度：</span>
							</div>
							<div class="l-form"  style="float: left;margin-top: 0px;">
								<input id="queryRules"/>
							</div>
							<div style="float: left; margin-left: 40px;">
								<span style="font-size: 12">查询机构：</span>
							</div>
							<div class="l-form"  style="float: left; margin-top: 0px;">
								<input id="queryOrg" />
							</div>
							<div>
								<div style="float: left; margin-left: 40px;">
									<span style="font-size: 12">数据类型：</span>
								</div>
								<div class="l-form"  style="float: left; margin-top: 0px;">
									<input id="archiveType" />
								</div>
							</div>
							<div style="clear: both;">
								<div id="dimCurrency">
									<div style="width: 75px; float: left; margin-left: 16px;">
										<span style="font-size: 12">查询币种：</span>
									</div>
									<div class="l-form"  style="float: left; width: 20%; margin-top: 0px;">
										<input id="queryCurrency" style="width: 100%;"/>
									</div>
								</div>
							</div>
						</div>	
					</div>
					<div id="panel" style="width:99%; height:100px; margin:2px; display: none;"></div>
					<div id="table" class="frame" style="width: 100%;">
						<div class="win" style="margin-top: 2px;">
							<div id="maingrid" class="maingrid" oncopy="return false" oncut="return false"></div>
						</div>			
					</div>
				</div>
			</div>
			<div id="storeConfig" style = "height:100%;widht:100%">
				<div id='cover' class='l-tab-loading'
				style='display:block; background:url(${ctx}/images/classics/index/bg_center.jpg) no-repeat center center #ffffff;'></div>
				<div id="store_tab" style="width: 100%; overflow: hidden;"></div>
			</div> 
		</div>
</body>
</html>